package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun RadioGroup(
    enabled: Boolean,
    title: String?,
    options: List<String>?,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.End) {
        Column {
            title?.let { Text(title) }
            Spacer(Modifier.width(4.dp))
            options?.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(CircleShape).clickable {
                        onOptionSelected(index)
                    }
                ) {
                    RadioButton(
                        enabled = enabled,
                        selected = (index == selectedOption),
                        onClick = { onOptionSelected(index) },
                    )
                    Text(item)
                    Spacer(Modifier.width(16.dp))
                }
            }
        }
    }

}