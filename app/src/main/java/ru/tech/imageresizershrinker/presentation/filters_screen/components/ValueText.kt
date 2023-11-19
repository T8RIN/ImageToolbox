package ru.tech.imageresizershrinker.presentation.filters_screen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ValueText(
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.padding(
        top = 8.dp,
        end = 8.dp
    ),
    value: Number,
    enabled: Boolean = true,
    valueSuffix: String = "",
    onClick: () -> Unit
) {
    Text(
        text = "$value$valueSuffix",
        color = LocalContentColor.current.copy(0.5f),
        modifier = modifier
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        lineHeight = 18.sp
    )
}