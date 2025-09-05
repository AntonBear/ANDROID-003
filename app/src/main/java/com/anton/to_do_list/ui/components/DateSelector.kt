package com.anton.to_do_list.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anton.to_do_list.R
import java.text.SimpleDateFormat

@Composable
fun DateSelector(dueDate: Long, openDialog: () -> Unit, context: Context) {
    Button(
        onClick = openDialog,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            context.getString(
                R.string.pick_due_date_format,
                SimpleDateFormat("dd/MM/yyyy").format(dueDate)
            )
        )
    }
}
