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

package com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AspectRatio
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.image.AspectRatioSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientBackgroundSelector
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.screenLogic.ScreenshotFramingComponent
import kotlin.math.roundToInt

@Composable
internal fun ScreenshotFramingControls(component: ScreenshotFramingComponent) {
    val params = component.params

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            modifier = Modifier.container(
                shape = ShapeDefaults.large,
                resultPadding = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TitleItem(
                text = stringResource(R.string.screenshot_framing_background),
                icon = Icons.Rounded.Palette,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProvideContainerDefaults(color = MaterialTheme.colorScheme.surface) {
                PreferenceRowSwitch(
                    title = stringResource(R.string.gradient),
                    subtitle = stringResource(R.string.screenshot_framing_gradient_sub),
                    checked = params.useGradient,
                    startIcon = null,
                    shape = ShapeDefaults.top,
                    onClick = component::updateGradient
                )
                AnimatedVisibility(visible = params.useGradient) {
                    GradientBackgroundSelector(
                        preset = params.preset,
                        colors = params.backgroundColors,
                        onPresetChange = component::updatePreset,
                        onPaletteChange = component::updateGradientPalette,
                        onStartColorChange = component::updateStartColor,
                        onEndColorChange = component::updateEndColor,
                        lastShape = ShapeDefaults.bottom
                    )
                }
                AnimatedVisibility(visible = !params.useGradient) {
                    ColorRowSelector(
                        value = params.backgroundColors.first(),
                        onValueChange = component::updateStartColor,
                        title = stringResource(R.string.screenshot_framing_background),
                        allowAlpha = false,
                        modifier = Modifier.container(shape = ShapeDefaults.bottom)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.container(
                shape = ShapeDefaults.large,
                resultPadding = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TitleItem(
                text = stringResource(R.string.screenshot_framing_layout),
                icon = Icons.Outlined.AspectRatio,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProvideContainerDefaults(color = MaterialTheme.colorScheme.surface) {
                AspectRatioSelector(
                    selectedAspectRatio = params.aspectRatio,
                    onAspectRatioChange = { aspectRatio, _ ->
                        component.updateAspectRatio(aspectRatio)
                    },
                    unselectedCardColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentPadding = PaddingValues(8.dp),
                    excludedAspectRatios = remember { setOf(DomainAspectRatio.Original) },
                    shape = ShapeDefaults.top
                )
                EnhancedSliderItem(
                    value = params.padding,
                    title = stringResource(R.string.padding),
                    valueRange = 0f..40f,
                    valueSuffix = "%",
                    internalStateTransformation = Float::roundToInt,
                    onValueChange = { component.updatePadding(it.roundToInt()) },
                    shape = ShapeDefaults.center
                )
                EnhancedSliderItem(
                    value = params.cornerRadius,
                    title = stringResource(R.string.screenshot_framing_corners),
                    valueRange = 0f..20f,
                    valueSuffix = "%",
                    internalStateTransformation = Float::roundToInt,
                    onValueChange = { component.updateCornerRadius(it.roundToInt()) },
                    shape = ShapeDefaults.center
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.screenshot_framing_shadow),
                    subtitle = stringResource(R.string.screenshot_framing_shadow_sub),
                    checked = params.showShadow,
                    startIcon = null,
                    shape = if (params.showShadow) ShapeDefaults.center else ShapeDefaults.bottom,
                    onClick = component::updateShadow
                )
                AnimatedVisibility(visible = params.showShadow) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        EnhancedSliderItem(
                            value = params.shadowBlur,
                            title = stringResource(R.string.blur_radius),
                            valueRange = 1f..12f,
                            valueSuffix = "%",
                            internalStateTransformation = Float::roundToInt,
                            onValueChange = { component.updateShadowBlur(it.roundToInt()) },
                            shape = ShapeDefaults.center
                        )
                        EnhancedSliderItem(
                            value = params.shadowOpacity,
                            title = stringResource(R.string.screenshot_framing_shadow_opacity),
                            valueRange = 0f..100f,
                            valueSuffix = "%",
                            internalStateTransformation = Float::roundToInt,
                            onValueChange = { component.updateShadowOpacity(it.roundToInt()) },
                            shape = ShapeDefaults.bottom
                        )
                    }
                }
            }
        }

        ImageFormatSelector(
            value = params.outputFormat,
            onValueChange = component::updateOutputFormat,
            quality = Quality.Base(100),
            forceEnabled = true
        )

        Spacer(Modifier.size(4.dp))
    }
}