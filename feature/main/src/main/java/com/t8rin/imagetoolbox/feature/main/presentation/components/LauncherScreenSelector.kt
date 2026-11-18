/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.main.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.settings.presentation.model.IconShape
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCircleShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import sh.calvin.reorderable.rememberScroller

@Composable
internal fun LauncherScreenSelector(
    screenList: List<Screen>,
    canReorderScreens: Boolean,
    onNavigateToScreenWithPopUpTo: (Screen) -> Unit,
    contentPadding: PaddingValues,
    onToggleFavorite: (Screen) -> Unit,
    onReorderScreens: (List<Screen>) -> Unit,
    onReorderDragStateChange: (Boolean) -> Unit,
    lastUsedTools: List<UiLastUsedTool>
) {
    val settingsState = LocalSettingsState.current
    val showFavoriteControls =
        !settingsState.groupOptionsByTypes || settingsState.showFavoriteToolsInGroupedMode

    val data = rememberReorderableScreenList(screenList)
    var dragStartOrder by remember { mutableStateOf(data.value) }
    val gridState = rememberLazyGridState()
    val reorderScroll = rememberToolReorderScroll(gridState)
    val haptics = LocalHapticFeedback.current
    val state = rememberReorderableLazyGridState(
        lazyGridState = gridState,
        scroller = rememberScroller(
            scrollableState = reorderScroll,
            pixelAmountProvider = { gridState.layoutInfo.viewportSize.height * 0.05f }
        ),
        onMove = { from, to ->
            val fromIndex = data.value.indexOfFirst { it.first == from.key }
            val toIndex = data.value.indexOfFirst { it.first == to.key }
            if (fromIndex >= 0 && toIndex >= 0 && fromIndex != toIndex) {
                data.value = data.value.toMutableList().apply {
                    add(toIndex, removeAt(fromIndex))
                }
                haptics.press()
            }
        }
    )
    reorderScroll.LockWhileDragging(state.isAnyItemDragging)

    LazyVerticalGrid(
        state = gridState,
        userScrollEnabled = !state.isAnyItemDragging,
        columns = GridCells.Adaptive(80.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        flingBehavior = enhancedFlingBehavior()
    ) {
        if (lastUsedTools.isNotEmpty()) {
            item(
                key = "lastUsedTools",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                LastUsedToolsCard(
                    tools = lastUsedTools,
                    onNavigate = onNavigateToScreenWithPopUpTo,
                    modifier = Modifier.animateItem()
                )
            }
        }
        items(
            items = data.value,
            key = { it.first }
        ) { (key, screen) ->
            ReorderableItem(
                state = state,
                key = key,
                enabled = canReorderScreens
            ) { isDragging ->
                val containerColor by animateColorAsState(
                    if (settingsState.isNightMode) {
                        MaterialTheme.colorScheme.secondaryContainer.blend(
                            color = Color.Black,
                            fraction = 0.3f
                        )
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .draggableHandle(
                            enabled = canReorderScreens,
                            dragGestureDetector = reorderScroll.detector(key),
                            onDragStarted = {
                                dragStartOrder = data.value
                                onReorderDragStateChange(true)
                                haptics.longPress()
                            },
                            onDragStopped = {
                                val released = reorderScroll.finish(key)
                                onReorderDragStateChange(false)
                                if (!released) {
                                    data.value = dragStartOrder
                                } else if (data.value != dragStartOrder) {
                                    onReorderScreens(data.value.map { it.second })
                                }
                            }
                        )
                        .scale(
                            animateFloatAsState(
                                if (isDragging) 1.05f else 1f
                            ).value
                        )
                ) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Below
                        ),
                        tooltip = {
                            RichTooltip(
                                title = {
                                    Text(
                                        text = stringResource(screen.title),
                                        textAlign = TextAlign.Start
                                    )
                                },
                                text = {
                                    Text(
                                        text = stringResource(screen.subtitle),
                                        textAlign = TextAlign.Start
                                    )
                                },
                                colors = TooltipDefaults.richTooltipColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                        0.5f
                                    ),
                                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                            )
                        },
                        state = rememberTooltipState(),
                        enableUserInput = !isDragging
                    ) {
                        BadgedBox(
                            badge = {
                                BoxAnimatedVisibility(
                                    visible = showFavoriteControls,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .offset(x = (-8).dp, y = 8.dp),
                                ) {
                                    val interactionSource = remember { MutableInteractionSource() }
                                    val shape = shapeByInteraction(
                                        shape = AutoCircleShape(),
                                        pressedShape = ShapeDefaults.smallMini,
                                        interactionSource = interactionSource
                                    )
                                    EnhancedIconButton(
                                        onClick = {
                                            onToggleFavorite(screen)
                                        },
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                shape = shape,
                                                color = MaterialTheme.colorScheme.surface
                                            )
                                            .padding(2.dp)
                                            .padding(bottom = 0.5.dp),
                                        containerColor = containerColor.copy(0.5f),
                                        contentColor = LocalContentColor.current,
                                        interactionSource = interactionSource
                                    ) {
                                        val inFavorite by remember(
                                            settingsState.favoriteScreenList,
                                            screen
                                        ) {
                                            derivedStateOf {
                                                settingsState.favoriteScreenList.find { it == screen.id } != null
                                            }
                                        }
                                        AnimatedContent(
                                            targetState = inFavorite,
                                            transitionSpec = {
                                                (fadeIn() + scaleIn(initialScale = 0.85f))
                                                    .togetherWith(
                                                        fadeOut() + scaleOut(
                                                            targetScale = 0.85f
                                                        )
                                                    )
                                            },
                                            modifier = Modifier.fillMaxSize(0.6f)
                                        ) { isInFavorite ->
                                            val icon by remember(isInFavorite) {
                                                derivedStateOf {
                                                    if (isInFavorite) Icons.Rounded.BookmarkRemove
                                                    else Icons.Outlined.Bookmark
                                                }
                                            }
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            val iconShape by remember(settingsState.iconShape) {
                                derivedStateOf {
                                    settingsState.iconShape?.takeOrElseFrom(IconShape.entries)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .container(
                                        resultPadding = 0.dp,
                                        color = containerColor,
                                        borderColor = MaterialTheme.colorScheme.outlineVariant(),
                                        shape = iconShape?.shape ?: ShapeDefaults.circle
                                    )
                                    .hapticsClickable {
                                        onNavigateToScreenWithPopUpTo(screen)
                                    }
                                    .padding(iconShape?.padding ?: 0.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedContent(
                                    targetState = screen.icon!!,
                                    modifier = Modifier.fillMaxSize(0.6f),
                                    transitionSpec = {
                                        (slideInVertically() + fadeIn() + scaleIn())
                                            .togetherWith(slideOutVertically { it / 2 } + fadeOut() + scaleOut())
                                            .using(SizeTransform(false))
                                    }
                                ) { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = animateColorAsState(
                                            if (settingsState.isNightMode) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.primary.blend(Color.Black)
                                            }
                                        ).value,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    AnimatedContent(
                        targetState = screen.title,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(it),
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}