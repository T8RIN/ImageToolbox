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

package com.t8rin.imagetoolbox.feature.ai_tools.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BlurCircular
import com.t8rin.imagetoolbox.core.resources.icons.CenterFocusStrong
import com.t8rin.imagetoolbox.core.resources.icons.Exercise
import com.t8rin.imagetoolbox.core.resources.icons.LightMode
import com.t8rin.imagetoolbox.core.resources.icons.Opacity
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.DepthEffect
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.DepthParams
import kotlin.math.roundToInt

@Composable
internal fun DepthParamsSelector(
    value: DepthParams,
    onValueChange: (DepthParams) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.container(ShapeDefaults.extraLarge),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            EnhancedButtonGroup(
                entries = DepthEffect.entries,
                value = value.effect,
                title = stringResource(R.string.depth_result),
                onValueChange = { onValueChange(value.copy(effect = it)) },
                itemContent = { Text(it.title()) },
                modifier = Modifier.padding(horizontal = 3.dp)
            )
            AnimatedContent(
                targetState = value.effect.description(),
                modifier = Modifier.fillMaxWidth()
            ) { description ->
                description?.let {
                    InfoContainer(
                        text = description,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.85f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }

        AnimatedContent(
            targetState = value.effect,
            modifier = Modifier.fillMaxWidth()
        ) { effect ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                when (effect) {
                    DepthEffect.Map -> Unit
                    DepthEffect.LensBlur -> LensBlurControls(value, onValueChange)
                    DepthEffect.Fog -> StrengthControl(
                        value = value,
                        onValueChange = onValueChange,
                        title = stringResource(R.string.strength)
                    )
                    DepthEffect.Relight -> RelightControls(value, onValueChange)
                    DepthEffect.NormalMap -> StrengthControl(
                        value = value,
                        onValueChange = onValueChange,
                        title = stringResource(R.string.relief_strength)
                    )

                    DepthEffect.Stereo -> StereoControls(value, onValueChange)
                }
            }
        }
    }
}

@Composable
private fun LensBlurControls(
    value: DepthParams,
    onValueChange: (DepthParams) -> Unit
) {
    EnhancedSliderItem(
        value = value.strength,
        title = stringResource(R.string.blur_strength),
        icon = Icons.Outlined.BlurCircular,
        valueRange = 0f..100f,
        steps = 99,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onValueChange(value.copy(strength = it)) },
        shape = ShapeDefaults.byIndex(0, 3)
    )
    EnhancedSliderItem(
        value = value.focus,
        title = stringResource(R.string.depth_focus),
        icon = Icons.Rounded.CenterFocusStrong,
        valueRange = 0f..100f,
        steps = 99,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onValueChange(value.copy(focus = it)) },
        shape = ShapeDefaults.byIndex(1, 3)
    )
    EnhancedSliderItem(
        value = value.focusRange,
        title = stringResource(R.string.depth_focus_range),
        icon = Icons.Rounded.Opacity,
        valueRange = 1f..50f,
        steps = 48,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onValueChange(value.copy(focusRange = it)) },
        shape = ShapeDefaults.byIndex(2, 3)
    )
}

@Composable
private fun StrengthControl(
    value: DepthParams,
    onValueChange: (DepthParams) -> Unit,
    title: String
) {
    EnhancedSliderItem(
        value = value.strength,
        title = title,
        icon = Icons.Outlined.Exercise,
        valueRange = 0f..100f,
        steps = 99,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onValueChange(value.copy(strength = it)) },
        shape = ShapeDefaults.large
    )
}

@Composable
private fun StereoControls(
    value: DepthParams,
    onValueChange: (DepthParams) -> Unit
) {
    EnhancedSliderItem(
        value = value.strength,
        title = stringResource(R.string.depth_3d_strength),
        icon = Icons.Outlined.Exercise,
        valueRange = 0f..100f,
        steps = 99,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onValueChange(value.copy(strength = it)) },
        shape = ShapeDefaults.byIndex(0, 2)
    )
    EnhancedSliderItem(
        value = value.focus,
        title = stringResource(R.string.depth_zero_plane),
        icon = Icons.Rounded.CenterFocusStrong,
        valueRange = 0f..100f,
        steps = 99,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onValueChange(value.copy(focus = it)) },
        shape = ShapeDefaults.byIndex(1, 2)
    )
}

@Composable
private fun RelightControls(
    value: DepthParams,
    onValueChange: (DepthParams) -> Unit
) {
    EnhancedSliderItem(
        value = value.strength,
        title = stringResource(R.string.strength),
        icon = Icons.Outlined.Exercise,
        valueRange = 0f..100f,
        steps = 99,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onValueChange(value.copy(strength = it)) },
        shape = ShapeDefaults.byIndex(0, 2)
    )
    EnhancedSliderItem(
        value = value.lightAngle,
        title = stringResource(R.string.light_angle),
        icon = Icons.Outlined.LightMode,
        valueRange = 0f..360f,
        steps = 359,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onValueChange(value.copy(lightAngle = it)) },
        shape = ShapeDefaults.byIndex(1, 2)
    )
}

@Composable
private fun DepthEffect.title(): String = stringResource(
    when (this) {
        DepthEffect.Map -> R.string.depth_map
        DepthEffect.LensBlur -> R.string.depth_blur
        DepthEffect.Fog -> R.string.depth_fog
        DepthEffect.Relight -> R.string.depth_relight
        DepthEffect.NormalMap -> R.string.depth_normal_map
        DepthEffect.Stereo -> R.string.depth_stereo
    }
)

@Composable
private fun DepthEffect.description(): String? = stringResource(
    when (this) {
        DepthEffect.Map -> return null
        DepthEffect.LensBlur -> R.string.depth_blur_sub
        DepthEffect.Fog -> R.string.depth_fog_sub
        DepthEffect.Relight -> R.string.depth_relight_sub
        DepthEffect.NormalMap -> R.string.depth_normal_map_sub
        DepthEffect.Stereo -> R.string.depth_stereo_sub
    }
)
