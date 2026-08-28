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

package com.t8rin.imagetoolbox.feature.code_preview.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormatGroup
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Code
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.resources.icons.Highlight
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.resources.icons.Regex
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodeBackgroundPreset
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodeLanguage
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodePreviewTheme
import com.t8rin.imagetoolbox.feature.code_preview.presentation.screenLogic.CodePreviewComponent
import kotlin.math.roundToInt

@Composable
internal fun CodePreviewControls(component: CodePreviewComponent) {
    val params = component.params
    val isNightMode = LocalSettingsState.current.isNightMode
    val highlightedCode = rememberCodeHighlight(
        code = params.code,
        language = params.language,
        theme = if (isNightMode) {
            CodePreviewTheme.TomorrowNight
        } else CodePreviewTheme.Tomorrow
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            modifier = Modifier.container(
                shape = ShapeDefaults.large,
                resultPadding = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TitleItem(
                text = stringResource(R.string.code_preview_code),
                icon = Icons.Rounded.Code,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            RoundedTextField(
                value = params.code,
                onValueChange = component::updateCode,
                hint = stringResource(R.string.code_preview_code_hint),
                singleLine = false,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Default
                ),
                visualTransformation = remember(highlightedCode) {
                    CodeHighlightVisualTransformation(highlightedCode)
                },
                shape = ShapeDefaults.large,
                modifier = Modifier.fillMaxWidth()
            )
            DataSelector(
                value = params.language,
                onValueChange = component::updateLanguage,
                entries = CodeLanguage.entries,
                title = stringResource(R.string.language),
                titleIcon = Icons.Outlined.Regex,
                itemContentText = { it.title },
                spanCount = 3,
                key = CodeLanguage::highlightKey,
                badgeContent = { Text(CodeLanguage.entries.size.toString()) },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = ShapeDefaults.large
            )
        }

        Column(
            modifier = Modifier.container(
                shape = ShapeDefaults.large,
                resultPadding = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TitleItem(
                text = stringResource(R.string.code_preview_style),
                icon = Icons.Rounded.Palette,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProvideContainerDefaults(color = MaterialTheme.colorScheme.surface) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DataSelector(
                        value = params.theme,
                        onValueChange = component::updateTheme,
                        entries = CodePreviewTheme.entries,
                        title = stringResource(R.string.code_preview_syntax_theme),
                        titleIcon = Icons.Outlined.Highlight,
                        badgeContent = { Text(CodePreviewTheme.entries.size.toString()) },
                        itemContentText = { it.title },
                        spanCount = 3,
                        shape = ShapeDefaults.top
                    )
                    PreferenceRowSwitch(
                        title = stringResource(R.string.code_preview_background),
                        subtitle = stringResource(R.string.code_preview_background_sub),
                        checked = params.showCanvasBackground,
                        startIcon = null,
                        shape = ShapeDefaults.center,
                        onClick = component::toggleCanvasBackground
                    )
                    AnimatedVisibility(visible = params.showCanvasBackground) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val entries = remember {
                                CodeBackgroundPreset.entries.filterNot {
                                    it == CodeBackgroundPreset.Custom
                                }
                            }

                            DataSelector(
                                value = params.backgroundPreset,
                                onValueChange = component::updateBackgroundPreset,
                                entries = entries,
                                title = stringResource(R.string.gradient),
                                titleIcon = Icons.Outlined.Gradient,
                                itemContentText = { stringResource(it.title) },
                                spanCount = 3,
                                badgeContent = { Text(entries.size.toString()) },
                                key = CodeBackgroundPreset::name,
                                shape = ShapeDefaults.center
                            )
                            ColorRowSelector(
                                value = params.backgroundStartColor,
                                onValueChange = component::updateBackgroundStartColor,
                                title = stringResource(R.string.code_preview_gradient_start),
                                allowAlpha = false,
                                modifier = Modifier.container(shape = ShapeDefaults.center)
                            )
                            ColorRowSelector(
                                value = params.backgroundEndColor,
                                onValueChange = component::updateBackgroundEndColor,
                                title = stringResource(R.string.code_preview_gradient_end),
                                allowAlpha = false,
                                modifier = Modifier.container(shape = ShapeDefaults.center)
                            )
                        }
                    }
                    EnhancedSliderItem(
                        value = params.fontSize,
                        title = stringResource(R.string.font_size),
                        valueRange = 10f..30f,
                        steps = 19,
                        valueSuffix = " sp",
                        internalStateTransformation = Float::roundToInt,
                        onValueChange = { component.updateFontSize(it.roundToInt()) },
                        shape = ShapeDefaults.center
                    )
                    EnhancedSliderItem(
                        value = params.outerPadding,
                        title = stringResource(R.string.code_preview_outer_padding),
                        valueRange = 0f..80f,
                        valueSuffix = " dp",
                        internalStateTransformation = Float::roundToInt,
                        onValueChange = { component.updateOuterPadding(it.roundToInt()) },
                        shape = ShapeDefaults.center
                    )
                    EnhancedSliderItem(
                        value = params.canvasCornerRadius,
                        title = stringResource(R.string.code_preview_canvas_corners),
                        valueRange = 0f..64f,
                        valueSuffix = " dp",
                        internalStateTransformation = Float::roundToInt,
                        onValueChange = {
                            component.updateCanvasCornerRadius(it.roundToInt())
                        },
                        shape = ShapeDefaults.center
                    )
                    EnhancedSliderItem(
                        value = params.rotation,
                        title = stringResource(R.string.rotation),
                        valueRange = -15f..15f,
                        valueSuffix = "°",
                        internalStateTransformation = Float::roundToInt,
                        onValueChange = component::updateRotation,
                        shape = ShapeDefaults.center
                    )
                    EnhancedSliderItem(
                        value = params.innerPadding,
                        title = stringResource(R.string.code_preview_inner_padding),
                        valueRange = 8f..48f,
                        valueSuffix = " dp",
                        internalStateTransformation = Float::roundToInt,
                        onValueChange = { component.updateInnerPadding(it.roundToInt()) },
                        shape = ShapeDefaults.center
                    )
                    EnhancedSliderItem(
                        value = params.cornerRadius,
                        title = stringResource(R.string.code_preview_code_corners),
                        valueRange = 0f..36f,
                        valueSuffix = " dp",
                        internalStateTransformation = Float::roundToInt,
                        onValueChange = { component.updateCornerRadius(it.roundToInt()) },
                        shape = ShapeDefaults.center
                    )
                    PreferenceRowSwitch(
                        title = stringResource(R.string.code_preview_card_shadow),
                        subtitle = stringResource(R.string.code_preview_card_shadow_sub),
                        checked = params.showCardShadow,
                        startIcon = null,
                        shape = ShapeDefaults.center,
                        onClick = component::toggleCardShadow
                    )
                    AnimatedVisibility(visible = params.showCardShadow) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ColorRowSelector(
                                value = params.cardShadowColor,
                                onValueChange = component::updateCardShadowColor,
                                title = stringResource(R.string.shadow_color),
                                allowAlpha = true,
                                modifier = Modifier.container(shape = ShapeDefaults.center)
                            )
                            EnhancedSliderItem(
                                value = params.cardShadowBlurRadius,
                                title = stringResource(R.string.blur_radius),
                                valueRange = 0f..40f,
                                valueSuffix = " dp",
                                internalStateTransformation = Float::roundToInt,
                                onValueChange = {
                                    component.updateCardShadowBlurRadius(it.roundToInt())
                                },
                                shape = ShapeDefaults.center
                            )
                            EnhancedSliderItem(
                                value = params.cardShadowOffsetX,
                                title = stringResource(R.string.offset_x),
                                valueRange = -30f..30f,
                                valueSuffix = " dp",
                                internalStateTransformation = Float::roundToInt,
                                onValueChange = {
                                    component.updateCardShadowOffsetX(it.roundToInt())
                                },
                                shape = ShapeDefaults.center
                            )
                            EnhancedSliderItem(
                                value = params.cardShadowOffsetY,
                                title = stringResource(R.string.offset_y),
                                valueRange = -30f..30f,
                                valueSuffix = " dp",
                                internalStateTransformation = Float::roundToInt,
                                onValueChange = {
                                    component.updateCardShadowOffsetY(it.roundToInt())
                                },
                                shape = ShapeDefaults.center
                            )
                        }
                    }
                    RoundedTextField(
                        value = params.title,
                        onValueChange = component::updateTitle,
                        label = stringResource(R.string.code_preview_filename),
                        enabled = params.showTitle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ShapeDefaults.center,
                                resultPadding = 8.dp
                            )
                    )
                    PreferenceRowSwitch(
                        title = stringResource(R.string.code_preview_window_controls),
                        subtitle = stringResource(R.string.code_preview_window_controls_sub),
                        checked = params.showWindowControls,
                        startIcon = null,
                        shape = ShapeDefaults.center,
                        onClick = component::toggleWindowControls
                    )
                    PreferenceRowSwitch(
                        title = stringResource(R.string.code_preview_show_filename),
                        subtitle = stringResource(R.string.code_preview_show_filename_sub),
                        checked = params.showTitle,
                        startIcon = null,
                        shape = ShapeDefaults.center,
                        onClick = component::toggleTitle
                    )
                    PreferenceRowSwitch(
                        title = stringResource(R.string.code_preview_line_numbers),
                        subtitle = stringResource(R.string.code_preview_line_numbers_sub),
                        checked = params.showLineNumbers,
                        startIcon = null,
                        shape = ShapeDefaults.center,
                        onClick = component::toggleLineNumbers
                    )
                    PreferenceRowSwitch(
                        title = stringResource(R.string.code_preview_wrap_lines),
                        subtitle = stringResource(R.string.code_preview_wrap_lines_sub),
                        checked = params.wrapLongLines,
                        startIcon = null,
                        shape = ShapeDefaults.bottom,
                        onClick = component::toggleWrapLongLines
                    )
                }
            }
        }

        ImageFormatSelector(
            value = params.outputFormat,
            onValueChange = component::updateOutputFormat,
            entries = if (params.showCanvasBackground) {
                ImageFormatGroup.entries
            } else ImageFormatGroup.alphaContainedEntries,
            quality = Quality.Base(100),
            forceEnabled = true
        )

        Spacer(Modifier.size(4.dp))
    }
}