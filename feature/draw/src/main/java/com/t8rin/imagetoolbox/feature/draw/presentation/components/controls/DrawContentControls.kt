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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.SaveExifWidget
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.AlphaSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.HelperGridParamsSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.MagnifierEnabledSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.feature.draw.domain.DrawBehavior
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.BrushSoftnessSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawColorSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawLineStyleSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawModeSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawPathModeSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.LineWidthSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.OpenColorPickerCard
import com.t8rin.imagetoolbox.feature.draw.presentation.screenLogic.DrawComponent
import com.t8rin.imagetoolbox.feature.pick_color.presentation.components.PickColorFromImageSheet

@Composable
internal fun DrawContentControls(
    component: DrawComponent,
    secondaryControls: @Composable () -> Unit,
    drawColor: Color,
    onDrawColorChange: (Color) -> Unit,
    strokeWidth: Pt,
    onStrokeWidthChange: (Pt) -> Unit,
    brushSoftness: Pt,
    onBrushSoftnessChange: (Pt) -> Unit,
    alpha: Float,
    onAlphaChange: (Float) -> Unit
) {
    var showPickColorSheet by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()

    val drawMode = component.drawMode
    val drawPathMode = component.drawPathMode
    val drawLineStyle = component.drawLineStyle

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isPortrait) {
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .container(shape = ShapeDefaults.circle)
            ) {
                secondaryControls()
            }
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.SpotHeal && drawMode !is DrawMode.Warp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxWidth()
        ) {
            OpenColorPickerCard(
                modifier = Modifier.fillMaxWidth(),
                onOpen = {
                    component.openColorPicker()
                    showPickColorSheet = true
                }
            )
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.Image && drawMode !is DrawMode.SpotHeal && drawMode !is DrawMode.Warp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxWidth()
        ) {
            DrawColorSelector(
                modifier = Modifier.fillMaxWidth(),
                value = drawColor,
                onValueChange = onDrawColorChange
            )
        }
        AnimatedVisibility(
            visible = drawPathMode.canChangeStrokeWidth,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxWidth()
        ) {
            LineWidthSelector(
                modifier = Modifier.fillMaxWidth(),
                title = if (drawMode is DrawMode.Text) {
                    stringResource(R.string.font_size)
                } else stringResource(R.string.line_width),
                valueRange = if (drawMode is DrawMode.Image) {
                    10f..120f
                } else 1f..100f,
                value = strokeWidth.value,
                onValueChange = { onStrokeWidthChange(it.pt) }
            )
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.Highlighter && drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.SpotHeal && drawMode !is DrawMode.Warp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxWidth()
        ) {
            BrushSoftnessSelector(
                modifier = Modifier.fillMaxWidth(),
                value = brushSoftness.value,
                onValueChange = { onBrushSoftnessChange(it.pt) }
            )
        }
        if (component.drawBehavior is DrawBehavior.Background) {
            ColorRowSelector(
                value = component.backgroundColor,
                onValueChange = component::updateBackgroundColor,
                icon = Icons.Outlined.BackgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .container(
                        shape = ShapeDefaults.extraLarge
                    )
            )
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.Neon && drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.SpotHeal && drawMode !is DrawMode.Warp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxWidth()
        ) {
            AlphaSelector(
                value = alpha,
                onValueChange = onAlphaChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        DrawModeSelector(
            modifier = Modifier.fillMaxWidth(),
            value = drawMode,
            strokeWidth = strokeWidth,
            onValueChange = component::updateDrawMode,
            values = remember(drawLineStyle) {
                derivedStateOf {
                    if (drawLineStyle == DrawLineStyle.None) {
                        DrawMode.entries
                    } else {
                        listOf(
                            DrawMode.Pen,
                            DrawMode.Highlighter,
                            DrawMode.Neon
                        )
                    }
                }
            }.value,
            addFiltersSheetComponent = component.addFiltersSheetComponent,
            filterTemplateCreationSheetComponent = component.filterTemplateCreationSheetComponent
        )
        AnimatedVisibility(
            visible = drawMode !is DrawMode.Warp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxWidth()
        ) {
            DrawPathModeSelector(
                modifier = Modifier.fillMaxWidth(),
                value = drawPathMode,
                onValueChange = component::updateDrawPathMode,
                values = remember(drawMode, drawLineStyle) {
                    derivedStateOf {
                        val outlinedModes = listOf(
                            DrawPathMode.OutlinedRect(),
                            DrawPathMode.OutlinedOval,
                            DrawPathMode.OutlinedTriangle,
                            DrawPathMode.OutlinedPolygon(),
                            DrawPathMode.OutlinedStar()
                        )
                        if (drawMode !is DrawMode.Text && drawMode !is DrawMode.Image) {
                            when (drawLineStyle) {
                                DrawLineStyle.None -> DrawPathMode.entries

                                !is DrawLineStyle.Stamped<*> -> listOf(
                                    DrawPathMode.Free,
                                    DrawPathMode.Line,
                                    DrawPathMode.LinePointingArrow(),
                                    DrawPathMode.PointingArrow(),
                                    DrawPathMode.DoublePointingArrow(),
                                    DrawPathMode.DoubleLinePointingArrow(),
                                ) + outlinedModes

                                else -> listOf(
                                    DrawPathMode.Free,
                                    DrawPathMode.Line
                                ) + outlinedModes
                            }
                        } else {
                            listOf(
                                DrawPathMode.Free,
                                DrawPathMode.Line
                            ) + outlinedModes
                        }
                    }
                }.value
            )
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.Warp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxWidth()
        ) {
            DrawLineStyleSelector(
                modifier = Modifier.fillMaxWidth(),
                value = drawLineStyle,
                onValueChange = component::updateDrawLineStyle
            )
        }
        HelperGridParamsSelector(
            value = component.helperGridParams,
            onValueChange = component::updateHelperGridParams,
            modifier = Modifier.fillMaxWidth()
        )
        MagnifierEnabledSelector(
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.extraLarge,
        )
        SaveExifWidget(
            modifier = Modifier.fillMaxWidth(),
            checked = component.saveExif,
            imageFormat = component.imageFormat,
            onCheckedChange = component::setSaveExif
        )
        ImageFormatSelector(
            modifier = Modifier.navigationBarsPadding(),
            forceEnabled = component.drawBehavior is DrawBehavior.Background,
            value = component.imageFormat,
            onValueChange = component::setImageFormat
        )
    }

    var colorPickerColor by rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(Color.Black) }
    PickColorFromImageSheet(
        visible = showPickColorSheet,
        onDismiss = {
            showPickColorSheet = false
        },
        bitmap = component.colorPickerBitmap,
        onColorChange = { colorPickerColor = it },
        color = colorPickerColor
    )
}