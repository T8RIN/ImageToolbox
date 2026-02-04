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

package com.t8rin.imagetoolbox.feature.compare.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Highlight
import androidx.compose.material.icons.rounded.Pix
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.compare.presentation.components.model.CompareData
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun CompareScreenContent(
    bitmapData: CompareData?,
    compareType: CompareType,
    onCompareTypeSelected: (CompareType) -> Unit,
    isPortrait: Boolean,
    compareProgress: Float,
    onCompareProgressChange: (Float) -> Unit,
    pixelByPixelCompareState: PixelByPixelCompareState,
    onPixelByPixelCompareStateChange: (PixelByPixelCompareState) -> Unit,
    imagePicker: ImagePicker,
    isLabelsEnabled: Boolean,
    createPixelByPixelTransformation: () -> Transformation
) {
    AnimatedContent(bitmapData == null) { noData ->
        bitmapData.takeIf { !noData }?.let { bitmapPair ->
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }

            val zoomEnabled = compareType != CompareType.SideBySide
            val zoomState = rememberZoomState(30f, key = compareType)
            val zoomModifier = Modifier
                .clipToBounds()
                .zoomable(
                    zoomState = zoomState,
                    onDoubleTap = {
                        if (zoomEnabled) {
                            zoomState.defaultZoomOnDoubleTap(it)
                        }
                    },
                    enableOneFingerZoom = zoomEnabled,
                    zoomEnabled = zoomEnabled
                )

            val tuneButton: @Composable BoxScope.() -> Unit = {
                BoxAnimatedVisibility(
                    visible = compareType == CompareType.PixelByPixel,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    var openTuneMenu by rememberSaveable {
                        mutableStateOf(false)
                    }
                    EnhancedIconButton(
                        onClick = {
                            openTuneMenu = true
                        },
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary.copy(0.85f),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = null
                        )
                    }

                    EnhancedModalBottomSheet(
                        visible = openTuneMenu,
                        onDismiss = { openTuneMenu = it },
                        title = {
                            TitleItem(
                                icon = Icons.Rounded.Tune,
                                text = stringResource(compareType.title)
                            )
                        },
                        confirmButton = {
                            EnhancedButton(
                                onClick = { openTuneMenu = false }
                            ) {
                                Text(stringResource(R.string.close))
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .enhancedVerticalScroll(rememberScrollState())
                                .padding(8.dp)
                        ) {
                            ColorRowSelector(
                                value = pixelByPixelCompareState.highlightColor,
                                onValueChange = {
                                    onPixelByPixelCompareStateChange(
                                        pixelByPixelCompareState.copy(
                                            highlightColor = it
                                        )
                                    )
                                },
                                allowAlpha = false,
                                modifier = Modifier.container(
                                    shape = ShapeDefaults.top
                                ),
                                title = stringResource(R.string.highlight_color),
                                icon = Icons.Rounded.Highlight
                            )
                            Spacer(Modifier.height(4.dp))
                            DataSelector(
                                value = pixelByPixelCompareState.comparisonType,
                                onValueChange = {
                                    onPixelByPixelCompareStateChange(
                                        pixelByPixelCompareState.copy(
                                            comparisonType = it
                                        )
                                    )
                                },
                                entries = ComparisonType.entries,
                                title = stringResource(R.string.pixel_comparison_type),
                                titleIcon = Icons.Rounded.Pix,
                                spanCount = 1,
                                shape = ShapeDefaults.bottom,
                                itemContentText = {
                                    it.name
                                },
                                containerColor = Color.Unspecified
                            )
                        }
                    }
                }
            }

            if (isPortrait) {
                Column {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(zoomModifier),
                            contentAlignment = Alignment.Center,
                        ) {
                            CompareScreenContentImpl(
                                compareType = compareType,
                                bitmapPair = bitmapPair,
                                compareProgress = compareProgress,
                                onCompareProgressChange = onCompareProgressChange,
                                isPortrait = true,
                                isLabelsEnabled = isLabelsEnabled,
                                pixelByPixelCompareState = pixelByPixelCompareState,
                                createPixelByPixelTransformation = createPixelByPixelTransformation
                            )
                        }

                        tuneButton()
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
                                    color = MaterialTheme.colorScheme.surfaceContainer,
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
                                onClick = imagePicker::pickImage,
                                onLongClick = {
                                    showOneTimeImagePickingDialog = true
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddPhotoAlt,
                                    contentDescription = stringResource(R.string.pick_image_alt)
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
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
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
                    val direction = LocalLayoutDirection.current
                    Box(
                        modifier = Modifier.weight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(zoomModifier)
                                .padding(
                                    start = WindowInsets
                                        .displayCutout
                                        .asPaddingValues()
                                        .calculateStartPadding(direction)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CompareScreenContentImpl(
                                compareType = compareType,
                                bitmapPair = bitmapPair,
                                compareProgress = compareProgress,
                                onCompareProgressChange = onCompareProgressChange,
                                isPortrait = false,
                                isLabelsEnabled = isLabelsEnabled,
                                pixelByPixelCompareState = pixelByPixelCompareState,
                                createPixelByPixelTransformation = createPixelByPixelTransformation
                            )
                        }

                        tuneButton()
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
                                    isPortrait = false,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                    Column(
                        Modifier
                            .container(
                                shape = RectangleShape,
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
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CompareSelectionButtons(
                                        value = compareType,
                                        onValueChange = onCompareTypeSelected,
                                        isPortrait = false,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
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
                                    .width(LocalScreenSize.current.height / 2f)

                                EnhancedSlider(
                                    modifier = modifier,
                                    value = compareProgress,
                                    onValueChange = onCompareProgressChange,
                                    valueRange = 0f..100f
                                )
                            }
                        }

                        EnhancedFloatingActionButton(
                            onClick = imagePicker::pickImage,
                            onLongClick = {
                                showOneTimeImagePickingDialog = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlt,
                                contentDescription = stringResource(R.string.pick_image_alt)
                            )
                        }
                    }
                }
            }

            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        } ?: Column(
            modifier = Modifier.enhancedVerticalScroll(rememberScrollState())
        ) {
            ImageNotPickedWidget(
                onPickImage = imagePicker::pickImage,
                modifier = Modifier
                    .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                    .navigationBarsPadding(),
                text = stringResource(R.string.pick_two_images)
            )
        }
    }
}