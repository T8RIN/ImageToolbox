package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.outlineVariant

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
            .border(
                width = LocalBorderWidth.current,
                color = colorScheme.outlineVariant(),
                shape = RoundedCornerShape(12.dp),
            )
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fabBorder(shape = shapes.small)
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

        CompositionLocalProvider(LocalContentColor provides colorScheme.onPrimaryContainer) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(alignment)
                        .size(18.dp)
                        .fabBorder(shape = RoundedCornerShape(6.dp))
                        .background(
                            color = colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp),
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null, Modifier.size(8.dp))
                }
            }
        }
    }
}