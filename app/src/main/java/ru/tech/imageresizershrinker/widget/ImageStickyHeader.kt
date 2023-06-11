package ru.tech.imageresizershrinker.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.availableHeight

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.imageStickyHeader(
    visible: Boolean,
    expanded: Boolean = false,
    imageState: Int,
    onStateChange: (Int) -> Unit,
    backgroundColor: Color = Color.Unspecified,
    imageBlock: @Composable () -> Unit,
) {
    if (visible) {
        stickyHeader {
            val color = if (backgroundColor.isSpecified) {
                backgroundColor
            } else MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)

            val settingsState = LocalSettingsState.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        availableHeight(expanded = expanded, imageState = imageState)
                    )
                    .background(color)
                    .clip(MaterialTheme.shapes.medium)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.weight(1f, false)) {
                    imageBlock()
                }
                Spacer(Modifier.height(36.dp))
            }
            Box {
                GradientEdge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    startColor = color,
                    endColor = Color.Transparent
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-48).dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .height(40.dp)
                        .fillMaxWidth(0.8f)
                        .border(
                            width = settingsState.borderWidth,
                            color = MaterialTheme.colorScheme
                                .outlineVariant()
                                .copy(alpha = 0.85f),
                            shape = CircleShape
                        )
                ) {
                    val modifier = Modifier.padding(horizontal = 16.dp)

                    Slider(
                        modifier = modifier,
                        value = animateFloatAsState(targetValue = imageState.toFloat()).value,
                        onValueChange = {
                            onStateChange(it.toInt())
                        },
                        colors = SliderDefaults.colors(
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                            thumbColor = MaterialTheme.colorScheme.tertiary,
                            activeTrackColor = MaterialTheme.colorScheme.tertiary
                        ),
                        steps = 3,
                        valueRange = 0f..4f
                    )
                }
            }
        }
    }
}