/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.noise_generation.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RampLeft
import androidx.compose.material.icons.outlined.SettingsEthernet
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.noise_generation.domain.model.CellularDistanceFunction
import com.t8rin.imagetoolbox.noise_generation.domain.model.CellularReturnType
import com.t8rin.imagetoolbox.noise_generation.domain.model.DomainWarpType
import com.t8rin.imagetoolbox.noise_generation.domain.model.FractalType
import com.t8rin.imagetoolbox.noise_generation.domain.model.NoiseParams
import com.t8rin.imagetoolbox.noise_generation.domain.model.NoiseType
import kotlin.math.roundToInt

@Composable
fun NoiseParamsSelection(
    value: NoiseParams,
    onValueChange: (NoiseParams) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EnhancedSliderItem(
            value = value.seed,
            icon = Icons.Outlined.SettingsEthernet,
            title = stringResource(R.string.seed),
            valueRange = -10000f..10000f,
            internalStateTransformation = {
                it.roundToInt()
            },
            onValueChange = {
                onValueChange(value.copy(seed = it.toInt()))
            },
            shape = ShapeDefaults.extraLarge
        )
        EnhancedSliderItem(
            value = value.frequency,
            icon = Icons.Outlined.Waves,
            title = stringResource(R.string.frequency),
            valueRange = -0.5f..0.5f,
            internalStateTransformation = {
                it.roundTo(3)
            },
            onValueChange = {
                onValueChange(value.copy(frequency = it))
            },
            shape = ShapeDefaults.extraLarge
        )
        DataSelector(
            value = value.noiseType,
            onValueChange = {
                onValueChange(value.copy(noiseType = it))
            },
            entries = NoiseType.entries,
            title = stringResource(R.string.noise_type),
            titleIcon = null,
            itemContentText = {
                it.name
            },
            spanCount = 2,
            color = Color.Unspecified
        )
        DataSelector(
            value = value.fractalType,
            onValueChange = {
                onValueChange(value.copy(fractalType = it))
            },
            entries = FractalType.entries,
            title = stringResource(R.string.fractal_type),
            titleIcon = null,
            itemContentText = {
                it.name
            },
            spanCount = 2,
            color = Color.Unspecified
        )
        AnimatedVisibility(value.fractalType != FractalType.None) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EnhancedSliderItem(
                    value = value.fractalOctaves,
                    title = stringResource(R.string.octaves),
                    valueRange = 1f..5f,
                    steps = 3,
                    internalStateTransformation = {
                        it.roundToInt()
                    },
                    onValueChange = {
                        onValueChange(value.copy(fractalOctaves = it.toInt()))
                    },
                    shape = ShapeDefaults.extraLarge
                )
                EnhancedSliderItem(
                    value = value.fractalLacunarity,
                    title = stringResource(R.string.lacunarity),
                    valueRange = -50f..50f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        onValueChange(value.copy(fractalLacunarity = it))
                    },
                    shape = ShapeDefaults.extraLarge
                )
                EnhancedSliderItem(
                    value = value.fractalGain,
                    title = stringResource(R.string.gain),
                    valueRange = -10f..10f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        onValueChange(value.copy(fractalGain = it))
                    },
                    shape = ShapeDefaults.extraLarge
                )
                EnhancedSliderItem(
                    value = value.fractalWeightedStrength,
                    title = stringResource(R.string.weighted_strength),
                    valueRange = -3f..3f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        onValueChange(value.copy(fractalWeightedStrength = it))
                    },
                    shape = ShapeDefaults.extraLarge
                )
                AnimatedVisibility(value.fractalType == FractalType.PingPong) {
                    EnhancedSliderItem(
                        value = value.fractalPingPongStrength,
                        title = stringResource(R.string.ping_pong_strength),
                        valueRange = 0f..20f,
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onValueChange(value.copy(fractalPingPongStrength = it))
                        },
                        shape = ShapeDefaults.extraLarge
                    )
                }
                AnimatedVisibility(value.noiseType == NoiseType.Cellular) {
                    Column {
                        DataSelector(
                            value = value.cellularDistanceFunction,
                            onValueChange = {
                                onValueChange(value.copy(cellularDistanceFunction = it))
                            },
                            entries = CellularDistanceFunction.entries,
                            title = stringResource(R.string.distance_function),
                            titleIcon = null,
                            itemContentText = {
                                it.name
                            },
                            spanCount = 2,
                            color = Color.Unspecified
                        )
                        Spacer(Modifier.height(8.dp))
                        DataSelector(
                            value = value.cellularReturnType,
                            onValueChange = {
                                onValueChange(value.copy(cellularReturnType = it))
                            },
                            entries = CellularReturnType.entries,
                            title = stringResource(R.string.return_type),
                            titleIcon = null,
                            itemContentText = {
                                it.name
                            },
                            spanCount = 2,
                            color = Color.Unspecified
                        )
                        Spacer(Modifier.height(8.dp))
                        EnhancedSliderItem(
                            value = value.cellularJitter,
                            title = stringResource(R.string.jitter),
                            valueRange = -10f..10f,
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                onValueChange(value.copy(cellularJitter = it))
                            },
                            shape = ShapeDefaults.extraLarge
                        )
                    }
                }
            }
        }
        DataSelector(
            value = value.domainWarpType,
            onValueChange = {
                onValueChange(value.copy(domainWarpType = it))
            },
            entries = DomainWarpType.entries,
            title = stringResource(R.string.domain_warp),
            titleIcon = null,
            itemContentText = {
                it.name
            },
            spanCount = 2,
            color = Color.Unspecified
        )
        EnhancedSliderItem(
            value = value.domainWarpAmp,
            icon = Icons.Outlined.RampLeft,
            title = stringResource(R.string.amplitude),
            valueRange = -2000f..2000f,
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = {
                onValueChange(value.copy(domainWarpAmp = it))
            },
            shape = ShapeDefaults.extraLarge
        )
    }
}