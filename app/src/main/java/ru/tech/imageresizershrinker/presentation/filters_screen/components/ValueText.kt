package ru.tech.imageresizershrinker.presentation.filters_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ValueText(
    value: Float,
    onClick: () -> Unit
) {
    Text(
        text = "$value",
        color = MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.5f
        ),
        modifier = Modifier
            .padding(top = 8.dp, end = 8.dp)
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        lineHeight = 18.sp
    )
}