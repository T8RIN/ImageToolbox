package ru.tech.imageresizershrinker.presentation.root.utils.modifier

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder

@Composable
fun Modifier.shimmer(
    visible: Boolean,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) = then(
    Modifier.placeholder(
        visible = visible,
        color = color,
        highlight = PlaceholderHighlight.shimmer()
    )
)
