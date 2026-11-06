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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.collage_maker.domain.CollageStyle
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormatGroup
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Archive
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.resources.icons.BorderStyle
import com.t8rin.imagetoolbox.core.resources.icons.FormatLineSpacing
import com.t8rin.imagetoolbox.core.resources.icons.Padding
import com.t8rin.imagetoolbox.core.resources.icons.PinEnd
import com.t8rin.imagetoolbox.core.resources.icons.RoundedCorner
import com.t8rin.imagetoolbox.core.resources.icons.SwipeVertical
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.BackgroundColorSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.StylePresetSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.StylePresetSelectorState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.image.AspectRatioSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun CollageMakerControls(
    component: CollageMakerComponent,
    isPortrait: Boolean,
    outputSize: Pair<Int, Int>?,
    styleSelectorState: StylePresetSelectorState,
    onSaveProject: () -> Unit,
    onApplyStyle: (CollageStyle) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isPortrait) {
            InfoContainer(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                text = stringResource(R.string.collages_info)
            )
        }

        PreferenceItem(
            onClick = onSaveProject,
            enabled = component.canSaveProject,
            startIcon = Icons.Outlined.Archive,
            title = stringResource(R.string.save_markup_project),
            subtitle = stringResource(R.string.save_collage_project_sub),
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.large
        )

        CollageLayoutSelector(component)
        StylePresetSelector(
            state = styleSelectorState,
            showSelection = false,
            currentStyle = component.currentStyle,
            styles = component.library.styles.map { it.name to it.style },
            listStateKey = "COLLAGE_STYLES",
            summary = { style ->
                stringResource(
                    R.string.collage_style_summary,
                    style.imageCount,
                    style.spacing.toInt(),
                    style.outerPadding.toInt(),
                    style.cornerRadius.toInt()
                )
            },
            onApply = onApplyStyle,
            onSave = component::saveStyle,
            onDelete = component::deleteStyle
        )
        EnhancedSliderItem(
            value = component.params.outerPadding,
            internalStateTransformation = { it.roundToTwoDigits() },
            title = stringResource(R.string.collage_outer_padding),
            icon = Icons.Outlined.Padding,
            valueRange = 0f..50f,
            onValueChange = component::setOuterPadding,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.extraLarge
        )
        EnhancedSliderItem(
            value = component.params.borderWidth,
            internalStateTransformation = { it.roundToTwoDigits() },
            title = stringResource(R.string.collage_border),
            icon = Icons.Rounded.BorderStyle,
            valueRange = 0f..30f,
            onValueChange = component::setBorderWidth,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.extraLarge
        )
        AnimatedVisibility(component.params.borderWidth > 0f) {
            CollageBorderSelector(
                color = Color(component.params.borderColor),
                gradient = component.params.borderGradient,
                onColorChange = component::setBorderColor,
                onGradientChange = component::setBorderGradient
            )
        }
        CollageShadowSelector(
            radius = component.params.shadowRadius,
            color = Color(component.params.shadowColor),
            onRadiusChange = component::setShadowRadius,
            onColorChange = component::setShadowColor
        )

        BackgroundColorSelector(
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.extraLarge,
            icon = Icons.Outlined.BackgroundColor,
            value = component.backgroundColor,
            gradient = component.backgroundGradient,
            onGradientChange = component::updateBackgroundGradient,
            onValueChange = component::setBackgroundColor
        )
        AspectRatioSelector(
            selectedAspectRatio = component.aspectRatio,
            onAspectRatioChange = { aspect, _ ->
                component.setAspectRatio(aspect)
            },
            unselectedCardColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            excludedAspectRatios = remember {
                setOf(DomainAspectRatio.Original)
            }
        )
        EnhancedSliderItem(
            modifier = Modifier.fillMaxWidth(),
            value = component.params.spacing,
            title = stringResource(R.string.spacing),
            valueRange = 0f..50f,
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = component::setSpacing,
            sliderModifier = Modifier
                .padding(
                    top = 14.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                ),
            icon = Icons.Rounded.FormatLineSpacing,
            shape = ShapeDefaults.extraLarge
        )
        EnhancedSliderItem(
            modifier = Modifier.fillMaxWidth(),
            value = component.params.cornerRadius,
            title = stringResource(R.string.corners),
            valueRange = 0f..50f,
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = component::setCornerRadius,
            sliderModifier = Modifier
                .padding(
                    top = 14.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                ),
            icon = Icons.Rounded.RoundedCorner,
            shape = ShapeDefaults.extraLarge
        )
        CollageOutputScaleSelector(
            value = component.params.outputScaleRatio,
            onValueChange = component::setOutputScaleRatio,
            outputSize = outputSize
        )
        PreferenceRowSwitch(
            title = stringResource(id = R.string.disable_rotation),
            subtitle = stringResource(id = R.string.disable_rotation_sub),
            checked = component.params.disableRotation,
            startIcon = Icons.Outlined.SwipeVertical,
            onClick = component::setDisableRotation
        )
        PreferenceRowSwitch(
            title = stringResource(id = R.string.enable_snapping_to_borders),
            subtitle = stringResource(id = R.string.enable_snapping_to_borders_sub),
            checked = component.params.enableSnapToBorders,
            startIcon = Icons.Rounded.PinEnd,
            onClick = component::setEnableSnapToBorders
        )
        QualitySelector(
            imageFormat = component.imageFormat,
            quality = component.quality,
            onQualityChange = component::setQuality
        )
        ImageFormatSelector(
            value = component.imageFormat,
            quality = component.quality,
            onValueChange = component::setImageFormat,
            entries = if (component.backgroundGradient?.palette?.colors?.any { (it.colorInt ushr 24) != 255 }
                    ?: (component.backgroundColor.alpha != 1f)) {
                ImageFormatGroup.alphaContainedEntries
            } else ImageFormatGroup.entries,
            forceEnabled = true
        )
    }
}