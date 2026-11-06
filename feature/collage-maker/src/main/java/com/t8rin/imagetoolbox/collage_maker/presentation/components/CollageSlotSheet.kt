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

package com.t8rin.imagetoolbox.collage_maker.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BorderStyle
import com.t8rin.imagetoolbox.core.resources.icons.RoundedCorner
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.ScrollAnchoredColumn
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun CollageSlotSheet(
    component: CollageMakerComponent,
    index: Int,
    visible: Boolean,
    onDismiss: (Boolean) -> Unit
) {
    val style = component.slotStyle(index)
    EnhancedModalBottomSheet(
        visible = visible && index in component.uris.orEmpty().indices,
        onDismiss = onDismiss,
        title = {
            TitleItem(
                icon = Icons.Rounded.Tune,
                text = stringResource(R.string.collage_slot)
            )
        },
        confirmButton = {
            EnhancedButton(onClick = { onDismiss(false) }) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        ScrollAnchoredColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PreferenceRowSwitch(
                applyHorizontalPadding = false,
                shape = ShapeDefaults.default,
                title = stringResource(R.string.collage_fit),
                subtitle = stringResource(R.string.collage_fit_sub),
                checked = style.fit,
                onClick = { component.setSlotStyle(index, style.copy(fit = it)) },
                resultModifier = Modifier.padding(16.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PreferenceRowSwitch(
                    applyHorizontalPadding = false,
                    shape = if (style.cornerRadius == null) ShapeDefaults.default else ShapeDefaults.top,
                    title = stringResource(R.string.collage_global_corners),
                    checked = style.cornerRadius == null,
                    onClick = {
                        component.setSlotStyle(
                            index,
                            style.copy(cornerRadius = if (it) null else component.params.cornerRadius)
                        )
                    }
                )
                AnimatedVisibility(style.cornerRadius != null) {
                    EnhancedSliderItem(
                        value = style.cornerRadius ?: component.params.cornerRadius,
                        title = stringResource(R.string.corners),
                        icon = Icons.Rounded.RoundedCorner,
                        valueRange = 0f..50f,
                        internalStateTransformation = { it.roundToTwoDigits() },
                        onValueChange = {
                            component.setSlotStyle(
                                index,
                                style.copy(cornerRadius = it)
                            )
                        },
                        shape = ShapeDefaults.bottom
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EnhancedSliderItem(
                    value = style.borderWidth,
                    title = stringResource(R.string.collage_border),
                    icon = Icons.Rounded.BorderStyle,
                    valueRange = 0f..30f,
                    internalStateTransformation = { it.roundToTwoDigits() },
                    onValueChange = { component.setSlotStyle(index, style.copy(borderWidth = it)) },
                    shape = if (style.borderWidth > 0f) ShapeDefaults.top else ShapeDefaults.default
                )
                AnimatedVisibility(style.borderWidth > 0f) {
                    CollageBorderSelector(
                        color = Color(style.borderColor),
                        gradient = component.slotBorderGradient(index),
                        shape = ShapeDefaults.bottom,
                        selectionKey = index,
                        onColorChange = {
                            component.setSlotStyle(index, style.copy(borderColor = it.toArgb()))
                            component.setSlotBorderGradient(index, null)
                        },
                        onGradientChange = { component.setSlotBorderGradient(index, it) }
                    )
                }
            }
            CollageShadowSelector(
                radius = style.shadowRadius,
                color = Color(style.shadowColor),
                shape = ShapeDefaults.default,
                onRadiusChange = {
                    component.setSlotStyle(
                        index,
                        style.copy(shadowRadius = it)
                    )
                },
                onColorChange = {
                    component.setSlotStyle(
                        index,
                        style.copy(shadowColor = it.toArgb())
                    )
                }
            )
        }
    }
}