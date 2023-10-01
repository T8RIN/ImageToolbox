package ru.tech.imageresizershrinker.presentation.root.widget.modifier

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import ru.tech.imageresizershrinker.presentation.root.theme.harmonizeWithPrimary

@Composable
fun Modifier.shimmer(
    visible: Boolean,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)
) = then(
    Modifier.placeholder(
        visible = visible,
        color = color,
        highlight = PlaceholderHighlight.shimmer(
            highlightColor = color.harmonizeWithPrimary(0.5f)
        )
    )
)
