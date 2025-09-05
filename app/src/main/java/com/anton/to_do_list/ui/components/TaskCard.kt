package com.anton.to_do_list.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anton.to_do_list.R
import com.anton.to_do_list.data.local.entity.TaskEntity
import java.text.SimpleDateFormat

@Composable
fun TaskCard(
    task: TaskEntity,
    onDelete: (TaskEntity) -> Unit,
    onEdit: (TaskEntity) -> Unit,
    context: Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit(task) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isDone = task.isCompleted

            if (isDone) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Выполнено",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                Text(task.description, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = context.getString(
                        R.string.due_date_format,
                        SimpleDateFormat("dd/MM/yyyy").format(task.dueDate)
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = { onDelete(task) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = context.getString(R.string.delete_task)
                )
            }
        }
    }
}
