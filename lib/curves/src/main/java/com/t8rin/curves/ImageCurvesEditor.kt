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

package com.t8rin.curves

import android.app.Activity
import android.graphics.Bitmap
import android.view.TextureView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.t8rin.curves.utils.safeAspectRatio
import com.t8rin.curves.view.PhotoFilterCurvesControl
import jp.co.cyberagent.android.gpuimage.GLTextureView
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter


@Composable
fun ImageCurvesEditor(
    bitmap: Bitmap?,
    state: ImageCurvesEditorState = remember {
        ImageCurvesEditorState.Default
    },
    onStateChange: (ImageCurvesEditorState) -> Unit,
    imageObtainingTrigger: Boolean,
    onImageObtained: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    curvesSelectionText: @Composable (curveType: Int) -> Unit = {},
    colors: ImageCurvesEditorColors = ImageCurvesEditorDefaults.Colors,
    drawNotActiveCurves: Boolean = true,
    placeControlsAtTheEnd: Boolean = false,
    showOriginal: Boolean = false,
    shape: Shape = RoundedCornerShape(2.dp),
    disallowInterceptTouchEvents: Boolean = true,
) {
    val context = LocalContext.current as Activity

    AnimatedContent(
        modifier = containerModifier,
        targetState = bitmap,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { image ->
        if (image != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
            ) {
                var imageHeight by remember(image) {
                    mutableFloatStateOf(image.height.toFloat())
                }
                var imageWidth by remember(image) {
                    mutableFloatStateOf(image.width.toFloat())
                }
                var imageOffset by remember(image) {
                    mutableStateOf(Offset.Zero)
                }
                var textureView by remember(image) {
                    mutableStateOf<TextureView?>(null)
                }

                val gpuImage by remember(context, image) {
                    mutableStateOf(
                        GPUImage(context).apply {
                            setImage(image)
                            setFilter(state.buildFilter())
                        }
                    )
                }

                LaunchedEffect(showOriginal, state) {
                    gpuImage.setFilter(
                        if (showOriginal) {
                            GPUImageContrastFilter(1f)
                        } else {
                            state.buildFilter()
                        }
                    )
                }

                LaunchedEffect(imageObtainingTrigger, gpuImage) {
                    if (imageObtainingTrigger) {
                        onImageObtained(gpuImage.bitmapWithFilterApplied)
                    }
                }

                var controlsPadding by remember {
                    mutableStateOf(0.dp)
                }

                val space = with(LocalDensity.current) {
                    1.dp.toPx()
                }
                AndroidView(
                    modifier = Modifier
                        .padding(contentPadding)
                        .then(
                            if (placeControlsAtTheEnd) Modifier.padding(end = controlsPadding)
                            else Modifier.padding(bottom = controlsPadding)
                        )
                        .aspectRatio(image.safeAspectRatio)
                        .onGloballyPositioned {
                            imageHeight = it.size.height.toFloat()
                            imageWidth = it.size.width.toFloat() - space
                            imageOffset = Offset(
                                x = it.positionInParent().x,
                                y = it.positionInParent().y
                            )
                        }
                        .clip(shape)
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                topLeft = Offset(
                                    x = size.width - space,
                                    y = 0f
                                ),
                                color = Color.Transparent,
                                blendMode = BlendMode.Clear
                            )
                        },
                    factory = {
                        GLTextureView(it).apply {
                            textureView = this
                            gpuImage.setGLTextureView(this)
                        }
                    }
                )
                var curvesView by remember {
                    mutableStateOf<PhotoFilterCurvesControl?>(null)
                }
                val disallowIntercept = remember { RequestDisallowInterceptTouchEvent() }

                AndroidView(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInteropFilter(
                            requestDisallowInterceptTouchEvent = disallowIntercept,
                            onTouchEvent = {
                                curvesView?.onTouchEvent(it)
                                disallowIntercept.invoke(disallowInterceptTouchEvents)
                                true
                            }
                        ),
                    factory = {
                        PhotoFilterCurvesControl(
                            context = it,
                            value = state.curvesToolValue
                        ).apply {
                            curvesView = this
                            setColors(
                                lumaCurveColor = colors.lumaCurveColor.toArgb(),
                                redCurveColor = colors.redCurveColor.toArgb(),
                                greenCurveColor = colors.greenCurveColor.toArgb(),
                                blueCurveColor = colors.blueCurveColor.toArgb(),
                                defaultCurveColor = colors.defaultCurveColor.toArgb(),
                                guidelinesColor = colors.guidelinesColor.toArgb()
                            )
                            setDrawNotActiveCurves(drawNotActiveCurves)
                            setActualArea(imageOffset.x, imageOffset.y, imageWidth, imageHeight)
                            setDelegate {
                                onStateChange(state.copy())
                                gpuImage.setFilter(state.buildFilter())
                            }
                        }
                    },
                    update = {
                        it.updateValue(state.curvesToolValue)
                        it.setActualArea(imageOffset.x, imageOffset.y, imageWidth, imageHeight)
                        it.setDelegate {
                            onStateChange(state.copy())
                            gpuImage.setFilter(state.buildFilter())
                        }
                        it.setColors(
                            lumaCurveColor = colors.lumaCurveColor.toArgb(),
                            redCurveColor = colors.redCurveColor.toArgb(),
                            greenCurveColor = colors.greenCurveColor.toArgb(),
                            blueCurveColor = colors.blueCurveColor.toArgb(),
                            defaultCurveColor = colors.defaultCurveColor.toArgb(),
                            guidelinesColor = colors.guidelinesColor.toArgb()
                        )
                        it.setDrawNotActiveCurves(drawNotActiveCurves)
                    }
                )

                val direction = LocalLayoutDirection.current
                val density = LocalDensity.current
                val controlsModifier = Modifier
                    .align(
                        if (placeControlsAtTheEnd) Alignment.CenterEnd
                        else Alignment.BottomCenter
                    )
                    .then(
                        if (placeControlsAtTheEnd) {
                            Modifier.padding(
                                top = contentPadding.calculateTopPadding(),
                                bottom = contentPadding.calculateBottomPadding(),
                                start = 0.dp,
                                end = contentPadding.calculateEndPadding(direction)
                            )
                        } else {
                            Modifier.padding(
                                top = 0.dp,
                                bottom = contentPadding.calculateBottomPadding(),
                                start = contentPadding.calculateStartPadding(direction),
                                end = contentPadding.calculateEndPadding(direction)
                            )
                        }
                    )
                    .onGloballyPositioned {
                        controlsPadding = with(density) {
                            if (placeControlsAtTheEnd) {
                                it.size.width
                            } else {
                                it.size.height
                            }.toDp()
                        }
                    }

                if (placeControlsAtTheEnd) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterVertically
                        ),
                        modifier = controlsModifier
                    ) {
                        val invalidations = remember(state) {
                            mutableIntStateOf(0)
                        }

                        CurvesSelectionRadioButton(
                            state = state,
                            color = colors.lumaCurveColor,
                            type = PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeLuminance,
                            curvesSelectionText = curvesSelectionText,
                            invalidations = invalidations
                        )
                        CurvesSelectionRadioButton(
                            state = state,
                            color = colors.redCurveColor,
                            type = PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeRed,
                            curvesSelectionText = curvesSelectionText,
                            invalidations = invalidations
                        )
                        CurvesSelectionRadioButton(
                            state = state,
                            color = colors.greenCurveColor,
                            type = PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeGreen,
                            curvesSelectionText = curvesSelectionText,
                            invalidations = invalidations
                        )
                        CurvesSelectionRadioButton(
                            state = state,
                            color = colors.blueCurveColor,
                            type = PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeBlue,
                            curvesSelectionText = curvesSelectionText,
                            invalidations = invalidations
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        modifier = controlsModifier
                    ) {
                        val invalidations = remember(state) {
                            mutableIntStateOf(0)
                        }

                        CurvesSelectionRadioButton(
                            state = state,
                            color = colors.lumaCurveColor,
                            type = PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeLuminance,
                            curvesSelectionText = curvesSelectionText,
                            invalidations = invalidations
                        )
                        CurvesSelectionRadioButton(
                            state = state,
                            color = colors.redCurveColor,
                            type = PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeRed,
                            curvesSelectionText = curvesSelectionText,
                            invalidations = invalidations
                        )
                        CurvesSelectionRadioButton(
                            state = state,
                            color = colors.greenCurveColor,
                            type = PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeGreen,
                            curvesSelectionText = curvesSelectionText,
                            invalidations = invalidations
                        )
                        CurvesSelectionRadioButton(
                            state = state,
                            color = colors.blueCurveColor,
                            type = PhotoFilterCurvesControl.CurvesToolValue.CurvesTypeBlue,
                            curvesSelectionText = curvesSelectionText,
                            invalidations = invalidations
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.CurvesSelectionRadioButton(
    state: ImageCurvesEditorState,
    color: Color,
    type: Int,
    invalidations: MutableState<Int>,
    curvesSelectionText: @Composable (type: Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f, false)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                state.curvesToolValue.activeType = type
                invalidations.value++
            }
    ) {
        val isSelected by remember(invalidations.value) {
            mutableStateOf(state.curvesToolValue.activeType == type)
        }
        RadioButton(
            selected = isSelected,
            onClick = {
                state.curvesToolValue.activeType = type
                invalidations.value++
            },
            colors = RadioButtonDefaults.colors(
                selectedColor = color,
                unselectedColor = color
            ),
            interactionSource = interactionSource
        )
        CompositionLocalProvider(LocalContentColor provides color) {
            curvesSelectionText(type)
        }
    }
}

@Composable
private fun ColumnScope.CurvesSelectionRadioButton(
    state: ImageCurvesEditorState,
    color: Color,
    type: Int,
    invalidations: MutableState<Int>,
    curvesSelectionText: @Composable (type: Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f, false)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                state.curvesToolValue.activeType = type
                invalidations.value++
            }
    ) {
        val isSelected by remember(invalidations.value) {
            mutableStateOf(state.curvesToolValue.activeType == type)
        }
        RadioButton(
            selected = isSelected,
            onClick = {
                state.curvesToolValue.activeType = type
                invalidations.value++
            },
            colors = RadioButtonDefaults.colors(
                selectedColor = color,
                unselectedColor = color
            ),
            interactionSource = interactionSource
        )
        CompositionLocalProvider(LocalContentColor provides color) {
            curvesSelectionText(type)
        }
    }
}