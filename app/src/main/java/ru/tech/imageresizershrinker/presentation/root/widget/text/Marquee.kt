package ru.tech.imageresizershrinker.presentation.root.widget.text

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.marqueeHorizontalFadingEdges

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Marquee(
    modifier: Modifier = Modifier,
    edgeColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit
) {
    var showMarquee by remember { mutableStateOf(false) }
    Layout(
        modifier = modifier
            .clipToBounds()
            .then(
                if (showMarquee) Modifier.marqueeHorizontalFadingEdges(
                    fillType = FadingEdgesFillType.FadeColor(
                        color = edgeColor
                    ),
                    length = 10.dp,
                    isMarqueeAutoLayout = false
                ) { Modifier.basicMarquee(Int.MAX_VALUE, velocity = 30.dp) }
                else Modifier
            ),
        content = content
    ) { measurable, constraints ->
        val childConstraints = constraints.copy(maxWidth = Constraints.Infinity)
        val placeable = measurable[0].measure(childConstraints)
        val containerWidth = constraints.constrainWidth(placeable.width)
        val contentWidth = placeable.width
        if (!showMarquee) {
            showMarquee = contentWidth > containerWidth
        }
        layout(containerWidth, placeable.height) {
            placeable.placeWithLayer(x = 0, y = 0)
        }
    }
}