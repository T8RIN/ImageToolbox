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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LineWeight
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrushColor
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Signature
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreviewLandscape
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.saver.PtSaver
import kotlin.math.abs

@Composable
fun SignatureDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onDone: (bitmap: Bitmap) -> Unit
) {
    val path = retain(visible) { Path() }

    var redraw by remember { mutableIntStateOf(0) }

    var lastPoint by remember { mutableStateOf<Offset?>(null) }
    var lastMid by remember { mutableStateOf<Offset?>(null) }

    val borderColor = MaterialTheme.colorScheme.outlineVariant()

    var canvasSize by remember {
        mutableStateOf(IntegerSize.Zero)
    }
    var strokeWidth by rememberSaveable(stateSaver = PtSaver) {
        mutableStateOf(15.pt)
    }

    var drawColor by rememberSaveable(stateSaver = ColorSaver) {
        mutableStateOf(Color.Black)
    }

    var showTuneDialog by remember {
        mutableStateOf(false)
    }

    val isPortrait by isPortraitOrientationAsState()
    val screenHeight = LocalWindowInfo.current.containerDpSize.height
    val showIconAndTitle = isPortrait || screenHeight > 500.dp

    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        icon = if (showIconAndTitle) {
            {
                Icon(
                    imageVector = Icons.Outlined.Signature,
                    contentDescription = null
                )
            }
        } else null,
        title = if (showIconAndTitle) {
            {
                Text(stringResource(R.string.draw_signature))
            }
        } else null,
        confirmButton = {
            EnhancedButton(
                onClick = {
                    val size = IntegerSize(1024, 1024)

                    val bitmap = ImageBitmap(size.width, size.height)

                    val canvas = Canvas(bitmap)

                    canvas.drawPath(
                        path = path.scaleToFitCanvas(
                            oldSize = canvasSize,
                            currentSize = size
                        ),
                        paint = Paint().apply {
                            color = drawColor
                            this.strokeWidth = strokeWidth.toPx(size)
                            style = PaintingStyle.Stroke
                            isAntiAlias = true
                            strokeCap = StrokeCap.Round
                            strokeJoin = StrokeJoin.Round
                        }
                    )

                    onDone(bitmap.asAndroidBitmap())

                    path.reset()
                    redraw++

                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        },
        text = {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val min = minOf(maxWidth, maxHeight)
                val shape = ShapeDefaults.mini

                Box(
                    modifier = Modifier
                        .size(min)
                        .align(Alignment.Center)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged {
                                canvasSize = IntegerSize(it.width, it.height)
                            }
                            .border(
                                width = 1.dp,
                                color = borderColor,
                                shape = shape
                            )
                            .clip(shape)
                            .transparencyChecker()
                            .background(Color.White.copy(0.6f))
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown()

                                    path.moveTo(down.position.x, down.position.y)

                                    lastPoint = down.position
                                    lastMid = down.position

                                    redraw++

                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.first()

                                        if (!change.pressed) break
                                        val point = change.position
                                        val prev = lastPoint ?: point

                                        if (
                                            abs(point.x - prev.x) < 1f &&
                                            abs(point.y - prev.y) < 1f
                                        ) continue

                                        val mid = Offset(
                                            (prev.x + point.x) / 2f,
                                            (prev.y + point.y) / 2f
                                        )
                                        val lastMidPoint = lastMid ?: prev
                                        path.cubicTo(
                                            lastMidPoint.x,
                                            lastMidPoint.y,
                                            prev.x,
                                            prev.y,
                                            mid.x,
                                            mid.y
                                        )
                                        lastPoint = point
                                        lastMid = mid
                                        redraw++

                                        change.consume()
                                    }
                                }
                            }
                    ) {
                        redraw
                        drawPath(
                            path = path,
                            color = drawColor,
                            style = Stroke(
                                width = strokeWidth.toPx(canvasSize),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    EnhancedIconButton(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(
                                x = (-2).dp,
                                y = 2.dp
                            ),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.8f),
                        enableAutoShadowAndBorder = false,
                        onClick = {
                            showTuneDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = null
                        )
                    }

                    EnhancedIconButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(
                                x = 2.dp,
                                y = 2.dp
                            ),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.8f),
                        enableAutoShadowAndBorder = false,
                        onClick = {
                            path.reset()
                            lastPoint = null
                            lastMid = null
                            redraw++
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        modifier = if (canvasSize != IntegerSize.Zero) {
            Modifier.width(
                with(LocalDensity.current) { canvasSize.width.toDp() + 48.dp }.coerceAtLeast(300.dp)
            )
        } else Modifier
    )

    EnhancedAlertDialog(
        visible = showTuneDialog,
        onDismissRequest = { showTuneDialog = false },
        icon = {
            Icon(Icons.Rounded.Tune, null)
        },
        title = {
            Text(stringResource(R.string.pen_params))
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showTuneDialog = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        },
        text = {
            Column {
                ColorRowSelector(
                    value = drawColor,
                    onValueChange = { drawColor = it },
                    modifier = Modifier
                        .container(
                            shape = ShapeDefaults.top
                        ),
                    title = stringResource(R.string.paint_color),
                    allowAlpha = false,
                    icon = Icons.Outlined.BrushColor
                )
                Spacer(Modifier.height(4.dp))
                EnhancedSliderItem(
                    modifier = Modifier.fillMaxWidth(),
                    value = strokeWidth.value,
                    icon = Icons.Rounded.LineWeight,
                    title = stringResource(R.string.line_width),
                    valueSuffix = " Pt",
                    sliderModifier = Modifier
                        .padding(top = 14.dp, start = 12.dp, end = 12.dp, bottom = 10.dp),
                    valueRange = 1f..50f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        strokeWidth = it.roundToTwoDigits().pt
                    },
                    shape = ShapeDefaults.bottom
                )
            }
        }
    )
}

@EnPreview
@EnPreviewLandscape
@Composable
private fun Preview() = ImageToolboxThemeForPreview(false) {
    Surface {
        Spacer(Modifier.fillMaxSize())
        var image by remember {
            mutableStateOf<Bitmap?>(null)
        }
        var visible by remember {
            mutableStateOf(true)
        }
        if (visible) {
            SignatureDialog(
                visible = true,
                onDismiss = { visible = false },
                onDone = { bmp -> image = bmp }
            )
        }

        image?.let {
            Image(it.asImageBitmap(), null, modifier = Modifier.background(Color.White))
        }
    }
}