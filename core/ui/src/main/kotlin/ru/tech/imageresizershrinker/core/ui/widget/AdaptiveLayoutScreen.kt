/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.utils.isExpanded
import ru.tech.imageresizershrinker.core.ui.widget.utils.middleImageState
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight

@Composable
fun AdaptiveLayoutScreen(
    title: @Composable () -> Unit,
    onGoBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    topAppBarPersistentActions: @Composable RowScope.() -> Unit = {},
    imagePreview: @Composable () -> Unit,
    controls: (@Composable ColumnScope.(LazyListState) -> Unit)?,
    buttons: @Composable (actions: @Composable RowScope.() -> Unit) -> Unit,
    noDataControls: @Composable () -> Unit = {},
    canShowScreenData: Boolean,
    forceImagePreviewToMax: Boolean = false,
    isPortrait: Boolean,
    contentPadding: Dp = 20.dp,
    showImagePreviewAsStickyHeader: Boolean = true,
    autoClearFocus: Boolean = true,
    placeImagePreview: Boolean = true
) {
    val settingsState = LocalSettingsState.current

    var imageState by remember { mutableStateOf(middleImageState()) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, canScroll = { !imageState.isExpanded() && !forceImagePreviewToMax }
    )

    LaunchedEffect(imageState, forceImagePreviewToMax) {
        if (imageState.isExpanded() || forceImagePreviewToMax) {
            while (topAppBarState.heightOffset > topAppBarState.heightOffsetLimit) {
                topAppBarState.heightOffset -= 5f
                delay(1)
            }
        }
    }

    val focus = LocalFocusManager.current
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = if (autoClearFocus) {
            Modifier.pointerInput(Unit) {
                detectTapGestures {
                    focus.clearFocus()
                }
            }
        } else Modifier
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(Modifier.fillMaxSize()) {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.drawHorizontalStroke(),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            title()
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onGoBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (!isPortrait && canShowScreenData) actions()
                        topAppBarPersistentActions()
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    val direction = LocalLayoutDirection.current
                    if (!isPortrait && canShowScreenData) {
                        if (placeImagePreview) {
                            Box(
                                modifier = Modifier
                                    .then(
                                        if (controls != null) {
                                            Modifier.container(
                                                shape = RectangleShape,
                                                color = MaterialTheme.colorScheme.surfaceContainer
                                            )
                                        } else Modifier
                                    )
                                    .fillMaxHeight()
                                    .padding(
                                        start = WindowInsets
                                            .displayCutout
                                            .asPaddingValues()
                                            .calculateStartPadding(direction)
                                    )
                                    .weight(1.2f)
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                imagePreview()
                            }
                        }
                    }
                    val internalHeight = rememberAvailableHeight(
                        imageState = imageState,
                        expanded = forceImagePreviewToMax
                    )
                    val cutout = if (!placeImagePreview) {
                        WindowInsets
                            .displayCutout
                            .asPaddingValues()
                            .calculateStartPadding(direction)
                    } else 0.dp

                    val state = rememberLazyListState()
                    LazyColumn(
                        state = state,
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (!isPortrait && canShowScreenData) contentPadding else 100.dp),
                            top = if (!canShowScreenData || !isPortrait) contentPadding else 0.dp,
                            start = contentPadding + cutout,
                            end = contentPadding
                        ),
                        modifier = Modifier
                            .weight(
                                if (controls == null) 0.01f
                                else 1f
                            )
                            .fillMaxHeight()
                            .clipToBounds()
                    ) {
                        if (showImagePreviewAsStickyHeader && placeImagePreview) {
                            imageStickyHeader(
                                visible = isPortrait && canShowScreenData,
                                internalHeight = internalHeight,
                                imageState = imageState,
                                onStateChange = { imageState = it },
                                imageBlock = imagePreview
                            )
                        }
                        item {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (canShowScreenData) {
                                    if (!showImagePreviewAsStickyHeader && isPortrait && placeImagePreview) imagePreview()
                                    if (controls != null) controls(state)
                                } else noDataControls()
                            }
                        }
                    }
                    if (!isPortrait && canShowScreenData) {
                        buttons(actions)
                    }
                }
            }

            if (isPortrait || !canShowScreenData) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons(actions)
                }
            }

            BackHandler { onGoBack() }
        }
    }
}

@Composable
fun BoxWrapper(content: @Composable () -> Unit) {
    Box {
        content()
    }
}