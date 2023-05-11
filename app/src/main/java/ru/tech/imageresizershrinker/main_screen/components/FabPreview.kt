package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.utils.modifier.fabBorder

@Composable
fun FabPreview(
    modifier: Modifier = Modifier,
    alignment: Alignment,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1.7f)
            .padding(4.dp)
            .fabBorder(shape = RoundedCornerShape(12.dp), elevation = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fabBorder(shape = shapes.small, elevation = 4.dp)
                .background(
                    color = colorScheme.surfaceVariant,
                    shape = shapes.small,
                )
                .fillMaxWidth(1f),
        ) {
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .height(36.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .weight(1f)
                        .background(
                            color = colorScheme.tertiary,
                            shape = RoundedCornerShape(5.5.dp)
                        ),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .weight(1f)
                        .background(
                            color = colorScheme.secondary,
                            shape = RoundedCornerShape(5.5.dp)
                        )
                )
            }
        }

        val weight by animateFloatAsState(
            targetValue = when (alignment) {
                Alignment.BottomStart -> 0f
                Alignment.BottomCenter -> 0.5f
                else -> 1f
            }
        )

        CompositionLocalProvider(LocalContentColor provides colorScheme.onPrimaryContainer) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.weight(0.01f + weight))
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(18.dp)
                        .fabBorder(shape = RoundedCornerShape(6.dp), elevation = 4.dp)
                        .background(
                            color = colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp),
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null, Modifier.size(8.dp))
                }
                Spacer(modifier = Modifier.weight(1.01f - weight))
            }
        }
    }
}