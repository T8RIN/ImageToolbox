package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@ExperimentalMaterial3Api
@Composable
fun RadioGroup(
    title: String?,
    options: List<String>?,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.End) {
        Column {
            title?.let { Text(title) }
            options?.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == selectedOption),
                        onClick = { onOptionSelected(index) },
                    )
                    Text(item)
                }
            }
        }
    }

}