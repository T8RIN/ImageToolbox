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
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Icon
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
                Modifier
                    .fillMaxWidth()
                    .height(
                        availableHeight(
                            expanded = expanded || imageState == 2,
                            collapsed = imageState == 0
                        )
                    )
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
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
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = (-24).dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer.copy(
                                alpha = 0.85f
                            ), CircleShape
                        )
                        .border(
                            settingsState.borderWidth,
                            MaterialTheme.colorScheme
                                .outlineVariant()
                                .copy(alpha = 0.85f),
                            CircleShape
                        )
                ) {
                    AnimatedVisibility(imageState != 0) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (imageState > 0) {
                                        onStateChange(imageState - 1)
                                    } else onStateChange(0)
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.RemoveCircle,
                                contentDescription = null
                            )
                        }
                    }
                    AnimatedVisibility(imageState != 2) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (imageState < 2) {
                                        onStateChange(imageState + 1)
                                    } else onStateChange(2)
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddCircle,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}