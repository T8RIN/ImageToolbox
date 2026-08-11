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

package com.t8rin.imagetoolbox.feature.multi_frame_fusion.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AutoMode
import com.t8rin.imagetoolbox.core.resources.icons.CenterFocusStrong
import com.t8rin.imagetoolbox.core.resources.icons.Contrast
import com.t8rin.imagetoolbox.core.resources.icons.CropSmall
import com.t8rin.imagetoolbox.core.resources.icons.Exercise
import com.t8rin.imagetoolbox.core.resources.icons.FilterHdr
import com.t8rin.imagetoolbox.core.resources.icons.LightMode
import com.t8rin.imagetoolbox.core.resources.icons.MotionPhotosAuto
import com.t8rin.imagetoolbox.core.resources.icons.Opacity
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionMode
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionParams
import kotlin.math.roundToInt

@Composable
internal fun FusionParamsSelector(
    value: FusionParams,
    onValueChange: (FusionParams) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Column(
            modifier = modifier.container(ShapeDefaults.extraLarge),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            EnhancedButtonGroup(
                entries = FusionMode.entries,
                value = value.mode,
                title = stringResource(R.string.fusion_mode),
                onValueChange = { onValueChange(value.copy(mode = it)) },
                itemContent = { mode -> Text(mode.title()) },
                modifier = Modifier.padding(horizontal = 3.dp)
            )
            AnimatedContent(
                targetState = value.mode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) { mode ->
                InfoContainer(
                    text = mode.description(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        AnimatedContent(
            targetState = value.mode,
            modifier = Modifier.fillMaxWidth()
        ) { mode ->
            when (mode) {
                FusionMode.Exposure,
                FusionMode.Focus,
                FusionMode.LightTrails,
                FusionMode.MotionTrails -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        when (mode) {
                            FusionMode.Exposure -> ExposureControls(value, onValueChange)
                            FusionMode.Focus -> FocusControls(value, onValueChange)
                            FusionMode.LightTrails -> LightTrailControls(value, onValueChange)
                            FusionMode.MotionTrails -> MotionTrailControls(value, onValueChange)
                        }
                    }
                }

                FusionMode.Median,
                FusionMode.LongExposure -> Unit
            }
        }

        Spacer(Modifier.height(8.dp))

        Column {
            PreferenceRowSwitch(
                title = stringResource(R.string.automatic_alignment),
                subtitle = stringResource(R.string.automatic_alignment_sub),
                startIcon = Icons.Rounded.AutoMode,
                checked = value.alignImages,
                onClick = { onValueChange(value.copy(alignImages = it)) },
                shape = ShapeDefaults.byIndex(0, if (value.alignImages) 2 else 1)
            )
            AnimatedVisibility(
                visible = value.alignImages,
                modifier = Modifier.fillMaxWidth()
            ) {
                PreferenceRowSwitch(
                    title = stringResource(R.string.crop_to_overlap),
                    subtitle = stringResource(R.string.crop_to_overlap_sub),
                    startIcon = Icons.Rounded.CropSmall,
                    checked = value.cropToOverlap,
                    onClick = { onValueChange(value.copy(cropToOverlap = it)) },
                    shape = ShapeDefaults.byIndex(1, 2),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ExposureControls(
    value: FusionParams,
    onValueChange: (FusionParams) -> Unit
) {
    EnhancedSliderItem(
        value = value.contrastWeight,
        title = stringResource(R.string.contrast_weight),
        icon = Icons.Rounded.Contrast,
        valueRange = FusionParams.MIN_WEIGHT..FusionParams.MAX_WEIGHT,
        internalStateTransformation = { (it * 10).roundToInt() / 10f },
        onValueChange = {},
        onValueChangeFinished = { onValueChange(value.copy(contrastWeight = it)) },
        shape = ShapeDefaults.byIndex(0, 3)
    )
    EnhancedSliderItem(
        value = value.saturationWeight,
        title = stringResource(R.string.saturation_weight),
        icon = Icons.Rounded.Opacity,
        valueRange = FusionParams.MIN_WEIGHT..FusionParams.MAX_WEIGHT,
        internalStateTransformation = { (it * 10).roundToInt() / 10f },
        onValueChange = {},
        onValueChangeFinished = { onValueChange(value.copy(saturationWeight = it)) },
        shape = ShapeDefaults.byIndex(1, 3)
    )
    EnhancedSliderItem(
        value = value.exposureWeight,
        title = stringResource(R.string.exposure_weight),
        icon = Icons.Rounded.FilterHdr,
        valueRange = FusionParams.MIN_WEIGHT..FusionParams.MAX_WEIGHT,
        internalStateTransformation = { (it * 10).roundToInt() / 10f },
        onValueChange = {},
        onValueChangeFinished = { onValueChange(value.copy(exposureWeight = it)) },
        shape = ShapeDefaults.byIndex(2, 3)
    )
}

@Composable
private fun FocusControls(
    value: FusionParams,
    onValueChange: (FusionParams) -> Unit
) {
    EnhancedSliderItem(
        value = value.focusRadius,
        title = stringResource(R.string.focus_radius),
        icon = Icons.Rounded.CenterFocusStrong,
        valueRange = FusionParams.MIN_FOCUS_RADIUS.toFloat()..FusionParams.MAX_FOCUS_RADIUS.toFloat(),
        steps = 9,
        internalStateTransformation = { it.roundToInt().let { radius -> radius / 2 * 2 + 1 } },
        onValueChange = {},
        onValueChangeFinished = {
            onValueChange(value.copy(focusRadius = it.roundToInt()))
        },
        shape = ShapeDefaults.byIndex(0, 2)
    )
    EnhancedSliderItem(
        value = value.focusStrength,
        title = stringResource(R.string.focus_strength),
        icon = Icons.Rounded.Contrast,
        valueRange = FusionParams.MIN_FOCUS_STRENGTH..FusionParams.MAX_FOCUS_STRENGTH,
        steps = 9,
        internalStateTransformation = { (it * 2).roundToInt() / 2f },
        onValueChange = {},
        onValueChangeFinished = { onValueChange(value.copy(focusStrength = it)) },
        shape = ShapeDefaults.byIndex(1, 2)
    )
}

@Composable
private fun LightTrailControls(
    value: FusionParams,
    onValueChange: (FusionParams) -> Unit
) {
    EnhancedSliderItem(
        value = value.lightTrailThreshold,
        title = stringResource(R.string.light_threshold),
        icon = Icons.Outlined.LightMode,
        valueRange = FusionParams.MIN_LIGHT_TRAIL_THRESHOLD..FusionParams.MAX_LIGHT_TRAIL_THRESHOLD,
        steps = 9,
        internalStateTransformation = { (it * 20).roundToInt() / 20f },
        onValueChange = {},
        onValueChangeFinished = { onValueChange(value.copy(lightTrailThreshold = it)) },
        shape = ShapeDefaults.byIndex(0, 2)
    )
    EnhancedSliderItem(
        value = value.trailStrength,
        title = stringResource(R.string.strength),
        icon = Icons.Outlined.Exercise,
        valueRange = FusionParams.MIN_TRAIL_STRENGTH..FusionParams.MAX_TRAIL_STRENGTH,
        steps = 9,
        internalStateTransformation = { (it * 10).roundToInt() / 10f },
        onValueChange = {},
        onValueChangeFinished = { onValueChange(value.copy(trailStrength = it)) },
        shape = ShapeDefaults.byIndex(1, 2)
    )
}

@Composable
private fun MotionTrailControls(
    value: FusionParams,
    onValueChange: (FusionParams) -> Unit
) {
    EnhancedSliderItem(
        value = value.trailPersistence,
        title = stringResource(R.string.trail_persistence),
        icon = Icons.Rounded.MotionPhotosAuto,
        valueRange = FusionParams.MIN_TRAIL_PERSISTENCE..FusionParams.MAX_TRAIL_PERSISTENCE,
        steps = 8,
        internalStateTransformation = { (it * 10).roundToInt() / 10f },
        onValueChange = {},
        onValueChangeFinished = { onValueChange(value.copy(trailPersistence = it)) },
        shape = ShapeDefaults.byIndex(0, 2)
    )
    EnhancedSliderItem(
        value = value.trailStrength,
        title = stringResource(R.string.strength),
        icon = Icons.Outlined.Exercise,
        valueRange = FusionParams.MIN_TRAIL_STRENGTH..FusionParams.MAX_TRAIL_STRENGTH,
        steps = 9,
        internalStateTransformation = { (it * 10).roundToInt() / 10f },
        onValueChange = {},
        onValueChangeFinished = { onValueChange(value.copy(trailStrength = it)) },
        shape = ShapeDefaults.byIndex(1, 2)
    )
}

@Composable
private fun FusionMode.title(): String = stringResource(
    when (this) {
        FusionMode.Exposure -> R.string.exposure_fusion
        FusionMode.Focus -> R.string.focus_stacking
        FusionMode.Median -> R.string.median_stack
        FusionMode.LongExposure -> R.string.long_exposure
        FusionMode.LightTrails -> R.string.light_trails
        FusionMode.MotionTrails -> R.string.motion_trails
    }
)

@Composable
private fun FusionMode.description(): String = stringResource(
    when (this) {
        FusionMode.Exposure -> R.string.exposure_fusion_sub
        FusionMode.Focus -> R.string.focus_stacking_sub
        FusionMode.Median -> R.string.median_stack_sub
        FusionMode.LongExposure -> R.string.long_exposure_sub
        FusionMode.LightTrails -> R.string.light_trails_sub
        FusionMode.MotionTrails -> R.string.motion_trails_sub
    }
)
