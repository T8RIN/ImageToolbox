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

package ru.tech.imageresizershrinker.feature.compare.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke

@Composable
fun CompareScreenContent(
    bitmapData: Pair<Bitmap?, Bitmap?>?,
    compareType: CompareType,
    onCompareTypeSelected: (CompareType) -> Unit,
    isPortrait: Boolean,
    compareProgress: Float,
    onCompareProgressChange: (Float) -> Unit,
    onPickImage: () -> Unit
) {
    AnimatedContent(bitmapData == null) { nil ->
        bitmapData.takeIf { !nil }?.let { bitmapPair ->
            val zoomEnabled = compareType != CompareType.SideBySide
            val zoomState = rememberZoomState(30f, key = compareType)
            val zoomModifier = Modifier.zoomable(
                zoomState = zoomState,
                onDoubleTap = {
                    if (zoomEnabled) {
                        zoomState.defaultZoomOnDoubleTap(it)
                    }
                },
                enableOneFingerZoom = zoomEnabled,
                enabled = { _, _ ->
                    zoomEnabled
                }
            )

            if (isPortrait) {
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .then(zoomModifier)
                    ) {
                        CompareScreenContentImpl(
                            compareType = compareType,
                            bitmapPair = bitmapPair,
                            compareProgress = compareProgress,
                            onCompareProgressChange = onCompareProgressChange,
                            isPortrait = true
                        )
                    }
                    val showButtonsAtTheTop by remember(compareType) {
                        derivedStateOf {
                            compareType != CompareType.Tap && compareType != CompareType.SideBySide
                        }
                    }
                    AnimatedVisibility(
                        visible = showButtonsAtTheTop
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawHorizontalStroke(
                                    top = true
                                )
                                .container(
                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                                    shape = RectangleShape,
                                    borderColor = Color.Transparent
                                )
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompareSelectionButtons(
                                value = compareType,
                                onValueChange = onCompareTypeSelected,
                                isPortrait = true
                            )
                        }
                    }
                    BottomAppBar(
                        modifier = Modifier
                            .drawHorizontalStroke(
                                top = true,
                                enabled = !showButtonsAtTheTop
                            ),
                        floatingActionButton = {
                            EnhancedFloatingActionButton(
                                onClick = onPickImage
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddPhotoAlternate,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            AnimatedContent(
                                targetState = !showButtonsAtTheTop
                            ) { showButtons ->
                                if (showButtons) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        CompareSelectionButtons(
                                            value = compareType,
                                            onValueChange = onCompareTypeSelected,
                                            isPortrait = true
                                        )
                                    }
                                } else {
                                    EnhancedSlider(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .weight(100f, true)
                                            .offset(y = (-2).dp),
                                        value = compareProgress,
                                        onValueChange = onCompareProgressChange,
                                        valueRange = 0f..100f
                                    )
                                }
                            }
                        }
                    )
                }
            } else {
                Row {
                    Box(
                        modifier = Modifier
                            .weight(0.8f)
                            .then(zoomModifier)
                            .padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CompareScreenContentImpl(
                                compareType = compareType,
                                bitmapPair = bitmapPair,
                                compareProgress = compareProgress,
                                onCompareProgressChange = onCompareProgressChange,
                                isPortrait = false
                            )
                        }
                    }
                    val showButtonsAtTheStart by remember(compareType) {
                        derivedStateOf {
                            compareType != CompareType.Tap && compareType != CompareType.SideBySide
                        }
                    }
                    AnimatedVisibility(
                        visible = showButtonsAtTheStart
                    ) {
                        Row {
                            LocalSettingsState.current.borderWidth.takeIf {
                                it > 0.dp
                            }?.let {
                                VerticalDivider(thickness = it)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .container(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = RectangleShape,
                                        borderColor = Color.Transparent
                                    )
                                    .padding(start = 4.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CompareSelectionButtons(
                                    value = compareType,
                                    onValueChange = onCompareTypeSelected,
                                    isPortrait = false
                                )
                            }
                        }
                    }
                    val direction = LocalLayoutDirection.current
                    Column(
                        Modifier
                            .container(
                                shape = RectangleShape,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                borderColor = if (showButtonsAtTheStart) Color.Transparent
                                else null
                            )
                            .padding(horizontal = 20.dp)
                            .fillMaxHeight()
                            .navigationBarsPadding()
                            .padding(
                                end = WindowInsets.displayCutout
                                    .asPaddingValues()
                                    .calculateEndPadding(direction)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedContent(
                            targetState = !showButtonsAtTheStart
                        ) { showButtons ->
                            if (showButtons) {
                                CompareSelectionButtons(
                                    value = compareType,
                                    onValueChange = onCompareTypeSelected,
                                    isPortrait = false,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                val modifier = Modifier
                                    .padding(16.dp)
                                    .graphicsLayer {
                                        rotationZ = 270f
                                        transformOrigin = TransformOrigin(0f, 0f)
                                    }
                                    .layout { measurable, constraints ->
                                        val placeable = measurable.measure(
                                            Constraints(
                                                minWidth = constraints.minHeight,
                                                maxWidth = constraints.maxHeight,
                                                minHeight = constraints.minWidth,
                                                maxHeight = constraints.maxHeight,
                                            )
                                        )
                                        layout(placeable.height, placeable.width) {
                                            placeable.place(-placeable.width, 0)
                                        }
                                    }
                                    .width((LocalConfiguration.current.screenHeightDp / 2f).dp)

                                EnhancedSlider(
                                    modifier = modifier,
                                    value = compareProgress,
                                    onValueChange = onCompareProgressChange,
                                    valueRange = 0f..100f
                                )
                            }
                        }

                        EnhancedFloatingActionButton(
                            onClick = onPickImage
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        } ?: Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            ImageNotPickedWidget(
                onPickImage = onPickImage,
                modifier = Modifier
                    .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                    .navigationBarsPadding(),
                text = stringResource(R.string.pick_two_images)
            )
        }
    }
}