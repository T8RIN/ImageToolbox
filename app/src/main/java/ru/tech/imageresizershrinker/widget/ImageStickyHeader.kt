package ru.tech.imageresizershrinker.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomInMap
import androidx.compose.material.icons.rounded.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    imageBlock: @Composable () -> Unit,
) {
    if (visible) {
        stickyHeader {
            val settingsState = LocalSettingsState.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        availableHeight(
                            expanded = expanded || imageState == 2,
                            collapsed = imageState == 0
                        )
                    )
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                    .clip(MaterialTheme.shapes.medium)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                imageBlock()
                Spacer(Modifier.height(32.dp))
            }
            Box {
                GradientEdge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    startColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    endColor = Color.Transparent
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = (-24).dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                alpha = 0.85f
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = settingsState.borderWidth,
                            color = MaterialTheme.colorScheme
                                .outlineVariant()
                                .copy(alpha = 0.85f),
                            shape = CircleShape
                        )
                ) {
                    AnimatedVisibility(visible = imageState != 0) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (imageState > 0) {
                                        onStateChange(imageState - 1)
                                    } else onStateChange(0)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = LocalContentColor.current.copy(alpha = 0.8f),
                                        shape = CircleShape
                                    )
                            )
                            Icon(
                                imageVector = Icons.Rounded.ZoomInMap,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.85f
                                )
                            )
                        }
                    }
                    AnimatedVisibility(visible = imageState != 2) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (imageState < 2) {
                                        onStateChange(imageState + 1)
                                    } else onStateChange(2)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = LocalContentColor.current.copy(alpha = 0.8f),
                                        shape = CircleShape
                                    )
                            )
                            Icon(
                                imageVector = Icons.Rounded.ZoomOutMap,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.85f
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}