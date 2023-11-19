package ru.tech.imageresizershrinker.presentation.main_screen.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.shapes.CloverShape
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun FabPreview(
    modifier: Modifier = Modifier,
    alignment: Alignment,
) {
    val shadowEnabled = LocalSettingsState.current.allowShowingShadowsInsteadOfBorders
    val elevation by animateDpAsState(if (shadowEnabled) 4.dp else 0.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1.7f)
            .padding(4.dp)
            .autoElevatedBorder(shape = RoundedCornerShape(12.dp), autoElevation = elevation)
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .autoElevatedBorder(shape = shapes.small, autoElevation = elevation)
                .clip(shapes.small)
                .background(colorScheme.surfaceColorAtElevation(1.dp))
                .fillMaxWidth(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(4.dp))
            EnhancedIconButton(
                onClick = {},
                modifier = Modifier.size(25.dp),
                shape = CloverShape,
                containerColor = colorScheme.surfaceColorAtElevation(6.dp),
                contentColor = colorScheme.onSurfaceVariant
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = stringResource(R.string.pick_image),
                modifier = Modifier.padding(4.dp),
                fontSize = 3.sp,
                lineHeight = 4.sp,
                textAlign = TextAlign.Center,
                color = colorScheme.onSurfaceVariant
            )
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
                        .size(22.dp)
                        .autoElevatedBorder(shape = RoundedCornerShape(7.dp), autoElevation = 4.dp)
                        .background(
                            color = colorScheme.primaryContainer,
                            shape = RoundedCornerShape(7.dp),
                        )
                        .clip(RoundedCornerShape(7.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null, Modifier.size(10.dp))
                }
                Spacer(modifier = Modifier.weight(1.01f - weight))
            }
        }
    }
}