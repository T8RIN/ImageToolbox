package ru.tech.imageresizershrinker.presentation.root.widget.image

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.draw_screen.components.materialShadow
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.presentation.root.widget.other.GradientEdge
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.presentation.root.widget.utils.rememberFullHeight

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun LazyListScope.imageStickyHeader(
    visible: Boolean,
    expanded: Boolean = false,
    imageState: ImageHeaderState,
    onStateChange: (ImageHeaderState) -> Unit,
    backgroundColor: Color = Color.Unspecified,
    imageBlock: @Composable () -> Unit,
) {
    if (visible) {
        stickyHeader {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val density = LocalDensity.current
            Column(
                modifier = Modifier.layout { measurable, constraints ->
                    val result =
                        measurable.measure(
                            constraints.copy(
                                maxWidth = with(density) {
                                    screenWidth.roundToPx()
                                }
                            )
                        )
                    layout(result.measuredWidth, result.measuredHeight) {
                        result.place(0, 0)
                    }
                }
            ) {
                val color = if (backgroundColor.isSpecified) {
                    backgroundColor
                } else MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)

                val settingsState = LocalSettingsState.current
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            rememberAvailableHeight(expanded = expanded, imageState = imageState)
                        )
                        .background(color)
                        .clip(MaterialTheme.shapes.medium)
                        .padding(20.dp)
                        .animateItemPlacement(),
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
                            .fillMaxWidth(0.7f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EnhancedSlider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            value = animateFloatAsState(targetValue = imageState.position.toFloat()).value,
                            onValueChange = {
                                onStateChange(imageState.copy(position = it.toInt()))
                            },
                            colors = SliderDefaults.colors(
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant(
                                    onTopOf = MaterialTheme.colorScheme.tertiaryContainer
                                ).copy(0.5f),
                                activeTrackColor = MaterialTheme.colorScheme.tertiary.copy(0.5f)
                            ),
                            thumbColor = MaterialTheme.colorScheme.onTertiary,
                            steps = 3,
                            valueRange = 0f..4f
                        )
                        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                            OutlinedIconToggleButton(
                                checked = imageState.isBlocked,
                                onCheckedChange = {
                                    onStateChange(imageState.copy(isBlocked = it))
                                },
                                modifier = Modifier.materialShadow(
                                    shape = CircleShape,
                                    elevation = if (settingsState.borderWidth > 0.dp) 0.dp else 0.5.dp,
                                    isClipped = true
                                ),
                                border = BorderStroke(
                                    width = settingsState.borderWidth,
                                    color = MaterialTheme.colorScheme
                                        .outlineVariant()
                                        .copy(alpha = 0.3f)
                                ),
                                colors = IconButtonDefaults.filledTonalIconToggleButtonColors(
                                    checkedContainerColor = MaterialTheme.colorScheme.tertiary.copy(
                                        0.8f
                                    ),
                                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        0.5f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                AnimatedContent(targetState = imageState.isBlocked) { blocked ->
                                    if (blocked) {
                                        Icon(Icons.Rounded.Lock, null)
                                    } else {
                                        Icon(Icons.Rounded.LockOpen, null)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!imageState.isBlocked) {
            stickyHeader {
                Spacer(
                    Modifier.height(
                        rememberAvailableHeight(
                            expanded = expanded,
                            imageState = imageState
                        ) - rememberFullHeight()
                    )
                )
            }
        }
    }
}