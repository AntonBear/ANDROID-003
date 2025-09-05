package com.anton.to_do_list.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anton.to_do_list.R

@Composable
fun TaskInputFields(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    context: Context
) {
    Text(
        text = context.getString(R.string.task_title_label),
        style = MaterialTheme.typography.labelMedium
    )
    TextField(
        value = title,
        onValueChange = onTitleChange,
        placeholder = { Text(context.getString(R.string.task_title_hint)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = context.getString(R.string.task_description_label),
        style = MaterialTheme.typography.labelMedium
    )
    TextField(
        value = description,
        onValueChange = onDescriptionChange,
        placeholder = { Text(context.getString(R.string.task_description_hint)) },
        modifier = Modifier.fillMaxWidth()
    )
}
