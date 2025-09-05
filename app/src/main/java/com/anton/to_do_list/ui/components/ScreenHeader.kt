package com.anton.to_do_list.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ScreenHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
}
