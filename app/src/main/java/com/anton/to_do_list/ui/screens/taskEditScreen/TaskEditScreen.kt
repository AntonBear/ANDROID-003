package com.anton.to_do_list.ui.screens.taskEditScreen

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anton.to_do_list.R
import com.anton.to_do_list.ui.components.ActionButtons
import com.anton.to_do_list.ui.components.DateSelector
import com.anton.to_do_list.ui.components.ScreenHeader
import com.anton.to_do_list.ui.components.TaskInputFields

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    navController: NavController,
    taskId: Int,
    viewModel: TaskEditViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(taskId) {
        viewModel.handleIntent(TaskEditIntent.LoadTask(taskId))
    }

    val state by viewModel.state.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val encoded = Base64.encodeToString(bytes, Base64.DEFAULT)
                viewModel.handleIntent(TaskEditIntent.SetPhotoBase64(encoded))
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ScreenHeader(title = context.getString(R.string.task_details_title))
            Spacer(Modifier.height(16.dp))

            TaskInputFields(
                title = state.title,
                onTitleChange = { viewModel.handleIntent(TaskEditIntent.SetTitle(it)) },
                description = state.description,
                onDescriptionChange = { viewModel.handleIntent(TaskEditIntent.SetDescription(it)) },
                context = context
            )

            Spacer(Modifier.height(12.dp))
            DateSelector(
                dueDate = state.date,
                openDialog = { viewModel.handleIntent(TaskEditIntent.OpenDatePicker) },
                context = context
            )

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Выполнено", modifier = Modifier.padding(end = 8.dp))
                Switch(
                    checked = state.isCompleted,
                    onCheckedChange = { checked ->
                        viewModel.handleIntent(TaskEditIntent.SetCompleted(checked))
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { photoPickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить фото")
            }

            state.photoBase64?.let { base64 ->
                val decoded = Base64.decode(base64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                bitmap?.let {
                    Image(
                        it.asImageBitmap(),
                        contentDescription = "Фото задачи",
                        modifier = Modifier
                            .height(120.dp)
                            .padding(top = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            ActionButtons(
                onCancel = { navController.popBackStack() },
                onSave = {
                    viewModel.handleIntent(TaskEditIntent.SaveTask)
                    navController.popBackStack()
                },
                context = context
            )
        }
    }

    if (state.showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = state.date)

        DatePickerDialog(
            onDismissRequest = { viewModel.handleIntent(TaskEditIntent.DatePickerDismissed) },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        viewModel.handleIntent(TaskEditIntent.DatePicked(millis))
                    }
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.handleIntent(TaskEditIntent.DatePickerDismissed) }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
