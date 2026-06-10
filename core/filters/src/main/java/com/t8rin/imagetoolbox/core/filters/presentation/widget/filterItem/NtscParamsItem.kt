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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.NtscParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.trickle.NtscSettings
import kotlin.math.roundToInt

@Composable
internal fun NtscParamsItem(
    value: NtscParams,
    filter: UiFilter<NtscParams>,
    onFilterChange: (value: NtscParams) -> Unit,
    previewOnly: Boolean
) {
    var params by remember(value) { mutableStateOf(value) }

    LaunchedEffect(params) {
        onFilterChange(params)
    }

    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NtscParamSlider(
            value = params.amount,
            info = filter.paramsInfo[0],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(amount = it)
            }
        )
        NtscParamSlider(
            value = params.tapeWear,
            info = filter.paramsInfo[1],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(tapeWear = it)
            }
        )
        NtscParamSlider(
            value = params.chromaBleed,
            info = filter.paramsInfo[2],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(chromaBleed = it)
            }
        )
        NtscParamSlider(
            value = params.tracking,
            info = filter.paramsInfo[3],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(tracking = it)
            }
        )
        NtscParamSlider(
            value = params.noise,
            info = filter.paramsInfo[4],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(noise = it)
            }
        )
        NtscParamSlider(
            value = params.snow,
            info = filter.paramsInfo[5],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(snow = it)
            }
        )
        NtscParamSlider(
            value = params.lumaSmear,
            info = filter.paramsInfo[6],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(lumaSmear = it)
            }
        )
        NtscParamSlider(
            value = params.compositeSharpening,
            info = filter.paramsInfo[7],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(compositeSharpening = it)
            }
        )
        NtscParamSlider(
            value = params.ringing,
            info = filter.paramsInfo[8],
            previewOnly = previewOnly,
            onValueChange = {
                params = params.copy(ringing = it)
            }
        )

        ExpandableItem(
            modifier = Modifier.padding(top = 4.dp),
            visibleContent = {
                TitleItem(
                    text = stringResource(R.string.ntsc_advanced_settings),
                    subtitle = stringResource(R.string.ntsc_advanced_settings_sub),
                    icon = Icons.Rounded.Tune,
                    iconEndPadding = 16.dp,
                    modifier = Modifier.padding(8.dp)
                )
            },
            expandableContent = {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NtscIntSlider(
                        value = params.seed,
                        info = filter.paramsInfo[9],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(seed = it)
                        }
                    )
                    NtscEnumSelector(
                        title = R.string.ntsc_use_field,
                        entries = NtscSettings.UseField.entries,
                        selectedIndex = params.useField,
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(useField = it)
                        }
                    )
                    NtscEnumSelector(
                        title = R.string.ntsc_filter_type,
                        entries = NtscSettings.FilterType.entries,
                        selectedIndex = params.filterType,
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(filterType = it)
                        }
                    )
                    NtscEnumSelector(
                        title = R.string.ntsc_input_luma_filter,
                        entries = NtscSettings.LumaLowpass.entries,
                        selectedIndex = params.inputLumaFilter,
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(inputLumaFilter = it)
                        }
                    )
                    NtscEnumSelector(
                        title = R.string.ntsc_chroma_lowpass_in,
                        entries = NtscSettings.ChromaLowpass.entries,
                        selectedIndex = params.chromaLowpassIn,
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(chromaLowpassIn = it)
                        }
                    )
                    NtscEnumSelector(
                        title = R.string.ntsc_chroma_demodulation,
                        entries = NtscSettings.ChromaDemodulationFilter.entries,
                        selectedIndex = params.chromaDemodulation,
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(chromaDemodulation = it)
                        }
                    )
                    NtscEnumSelector(
                        title = R.string.ntsc_phase_shift,
                        entries = NtscSettings.PhaseShift.entries,
                        selectedIndex = params.videoScanlinePhaseShift,
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(videoScanlinePhaseShift = it)
                        }
                    )
                    NtscEnumSelector(
                        title = R.string.ntsc_chroma_lowpass_out,
                        entries = NtscSettings.ChromaLowpass.entries,
                        selectedIndex = params.chromaLowpassOut,
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(chromaLowpassOut = it)
                        }
                    )
                    NtscIntSlider(
                        value = params.videoScanlinePhaseShiftOffset,
                        info = filter.paramsInfo[10],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(videoScanlinePhaseShiftOffset = it)
                        }
                    )
                    NtscToggleGroup(
                        title = R.string.ntsc_head_switching,
                        checked = params.headSwitchingEnabled,
                        previewOnly = previewOnly,
                        onCheckedChange = {
                            params = params.copy(headSwitchingEnabled = it)
                        },
                        content = {
                            NtscIntSlider(
                                value = params.headSwitchingHeight,
                                info = filter.paramsInfo[11],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.top,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(headSwitchingHeight = it)
                                }
                            )
                            NtscIntSlider(
                                value = params.headSwitchingOffset,
                                info = filter.paramsInfo[12],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(headSwitchingOffset = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.headSwitchingHorizontalShift,
                                info = filter.paramsInfo[13],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(headSwitchingHorizontalShift = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.headSwitchingMidLinePosition,
                                info = filter.paramsInfo[14],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(headSwitchingMidLinePosition = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.headSwitchingMidLineJitter,
                                info = filter.paramsInfo[15],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.bottom,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(headSwitchingMidLineJitter = it)
                                }
                            )
                        }
                    )
                    NtscToggleGroup(
                        title = R.string.ntsc_tracking_noise,
                        checked = params.trackingNoiseEnabled,
                        previewOnly = previewOnly,
                        onCheckedChange = {
                            params = params.copy(trackingNoiseEnabled = it)
                        },
                        content = {
                            NtscIntSlider(
                                value = params.trackingNoiseHeight,
                                info = filter.paramsInfo[16],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.top,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(trackingNoiseHeight = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.trackingNoiseWaveIntensity,
                                info = filter.paramsInfo[17],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(trackingNoiseWaveIntensity = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.trackingNoiseSnowIntensity,
                                info = filter.paramsInfo[18],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(trackingNoiseSnowIntensity = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.trackingNoiseSnowAnisotropy,
                                info = filter.paramsInfo[19],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(trackingNoiseSnowAnisotropy = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.trackingNoiseNoiseIntensity,
                                info = filter.paramsInfo[20],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.bottom,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(trackingNoiseNoiseIntensity = it)
                                }
                            )
                        }
                    )
                    NtscToggleGroup(
                        title = R.string.ntsc_composite_noise,
                        checked = params.compositeNoiseEnabled,
                        previewOnly = previewOnly,
                        onCheckedChange = {
                            params = params.copy(compositeNoiseEnabled = it)
                        },
                        content = {
                            NtscParamSlider(
                                value = params.compositeNoiseFrequency,
                                info = filter.paramsInfo[21],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.top,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(compositeNoiseFrequency = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.compositeNoiseIntensity,
                                info = filter.paramsInfo[22],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(compositeNoiseIntensity = it)
                                }
                            )
                            NtscIntSlider(
                                value = params.compositeNoiseDetail,
                                info = filter.paramsInfo[23],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.bottom,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(compositeNoiseDetail = it)
                                }
                            )
                        }
                    )
                    NtscToggleGroup(
                        title = R.string.ringing,
                        checked = params.ringingEnabled,
                        previewOnly = previewOnly,
                        onCheckedChange = {
                            params = params.copy(ringingEnabled = it)
                        },
                        content = {
                            NtscParamSlider(
                                value = params.ringingFrequency,
                                info = filter.paramsInfo[24],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.top,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(ringingFrequency = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.ringingPower,
                                info = filter.paramsInfo[25],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.bottom,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(ringingPower = it)
                                }
                            )
                        }
                    )
                    NtscToggleGroup(
                        title = R.string.ntsc_luma_noise,
                        checked = params.lumaNoiseEnabled,
                        previewOnly = previewOnly,
                        onCheckedChange = {
                            params = params.copy(lumaNoiseEnabled = it)
                        },
                        content = {
                            NtscParamSlider(
                                value = params.lumaNoiseFrequency,
                                info = filter.paramsInfo[26],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.top,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(lumaNoiseFrequency = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.lumaNoiseIntensity,
                                info = filter.paramsInfo[27],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(lumaNoiseIntensity = it)
                                }
                            )
                            NtscIntSlider(
                                value = params.lumaNoiseDetail,
                                info = filter.paramsInfo[28],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.bottom,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(lumaNoiseDetail = it)
                                }
                            )
                        }
                    )
                    NtscToggleGroup(
                        title = R.string.ntsc_chroma_noise,
                        checked = params.chromaNoiseEnabled,
                        previewOnly = previewOnly,
                        onCheckedChange = {
                            params = params.copy(chromaNoiseEnabled = it)
                        },
                        content = {
                            NtscParamSlider(
                                value = params.chromaNoiseFrequency,
                                info = filter.paramsInfo[29],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.top,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(chromaNoiseFrequency = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.chromaNoiseIntensity,
                                info = filter.paramsInfo[30],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(chromaNoiseIntensity = it)
                                }
                            )
                            NtscIntSlider(
                                value = params.chromaNoiseDetail,
                                info = filter.paramsInfo[31],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.bottom,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(chromaNoiseDetail = it)
                                }
                            )
                        }
                    )
                    NtscParamSlider(
                        value = params.snowIntensity,
                        info = filter.paramsInfo[32],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(snowIntensity = it)
                        }
                    )
                    NtscParamSlider(
                        value = params.snowAnisotropy,
                        info = filter.paramsInfo[33],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(snowAnisotropy = it)
                        }
                    )
                    NtscParamSlider(
                        value = params.chromaPhaseNoiseIntensity,
                        info = filter.paramsInfo[34],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(chromaPhaseNoiseIntensity = it)
                        }
                    )
                    NtscParamSlider(
                        value = params.chromaPhaseError,
                        info = filter.paramsInfo[35],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(chromaPhaseError = it)
                        }
                    )
                    NtscParamSlider(
                        value = params.chromaDelayHorizontal,
                        info = filter.paramsInfo[36],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(chromaDelayHorizontal = it)
                        }
                    )
                    NtscIntSlider(
                        value = params.chromaDelayVertical,
                        info = filter.paramsInfo[37],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(chromaDelayVertical = it)
                        }
                    )
                    NtscToggleGroup(
                        title = R.string.vhs,
                        checked = params.vhsEnabled,
                        previewOnly = previewOnly,
                        onCheckedChange = {
                            params = params.copy(vhsEnabled = it)
                        },
                        content = {
                            NtscEnumSelector(
                                title = R.string.ntsc_vhs_tape_speed,
                                entries = NtscSettings.VHSTapeSpeed.entries,
                                selectedIndex = params.vhsTapeSpeed,
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.top,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(vhsTapeSpeed = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.vhsChromaLoss,
                                info = filter.paramsInfo[38],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(vhsChromaLoss = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.vhsSharpenIntensity,
                                info = filter.paramsInfo[39],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(vhsSharpenIntensity = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.vhsSharpenFrequency,
                                info = filter.paramsInfo[40],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(vhsSharpenFrequency = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.vhsEdgeWaveIntensity,
                                info = filter.paramsInfo[41],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(vhsEdgeWaveIntensity = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.vhsEdgeWaveSpeed,
                                info = filter.paramsInfo[42],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(vhsEdgeWaveSpeed = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.vhsEdgeWaveFrequency,
                                info = filter.paramsInfo[43],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.center,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(vhsEdgeWaveFrequency = it)
                                }
                            )
                            NtscIntSlider(
                                value = params.vhsEdgeWaveDetail,
                                info = filter.paramsInfo[44],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.bottom,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(vhsEdgeWaveDetail = it)
                                }
                            )
                        }
                    )
                    NtscToggleGroup(
                        title = R.string.scale,
                        checked = params.scaleEnabled,
                        previewOnly = previewOnly,
                        onCheckedChange = {
                            params = params.copy(scaleEnabled = it)
                        },
                        content = {
                            NtscParamSlider(
                                value = params.scaleHorizontal,
                                info = filter.paramsInfo[45],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.top,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(scaleHorizontal = it)
                                }
                            )
                            NtscParamSlider(
                                value = params.scaleVertical,
                                info = filter.paramsInfo[46],
                                previewOnly = previewOnly,
                                shape = ShapeDefaults.bottom,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                onValueChange = {
                                    params = params.copy(scaleVertical = it)
                                }
                            )
                        }
                    )
                    NtscParamSlider(
                        value = params.scaleFactorX,
                        info = filter.paramsInfo[47],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(scaleFactorX = it)
                        }
                    )
                    NtscParamSlider(
                        value = params.scaleFactorY,
                        info = filter.paramsInfo[48],
                        previewOnly = previewOnly,
                        onValueChange = {
                            params = params.copy(scaleFactorY = it)
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun NtscParamSlider(
    value: Float,
    info: FilterParam,
    previewOnly: Boolean,
    shape: Shape = ShapeDefaults.default,
    containerColor: Color = Color.Unspecified,
    onValueChange: (Float) -> Unit
) {
    val (title, valueRange, roundTo) = info
    EnhancedSliderItem(
        enabled = !previewOnly,
        value = value,
        title = stringResource(title!!),
        valueRange = valueRange,
        onValueChange = onValueChange,
        internalStateTransformation = {
            it.roundTo(roundTo)
        },
        shape = shape,
        containerColor = containerColor,
        behaveAsContainer = containerColor != Color.Unspecified
    )
}

@Composable
private fun NtscIntSlider(
    value: Int,
    info: FilterParam,
    previewOnly: Boolean,
    shape: Shape = ShapeDefaults.default,
    containerColor: Color = Color.Unspecified,
    onValueChange: (Int) -> Unit
) {
    NtscParamSlider(
        value = value.toFloat(),
        info = info,
        previewOnly = previewOnly,
        shape = shape,
        containerColor = containerColor,
        onValueChange = {
            onValueChange(it.roundToInt())
        }
    )
}

@Composable
private fun <T : Enum<T>> NtscEnumSelector(
    title: Int,
    entries: List<T>,
    selectedIndex: Int,
    previewOnly: Boolean,
    shape: Shape = ShapeDefaults.default,
    containerColor: Color = Color.Unspecified,
    onValueChange: (Int) -> Unit
) {
    val labels = remember(entries) {
        entries.map { it.name.ntscEnumName() }
    }
    if (labels.isEmpty()) return

    val safeIndex = selectedIndex.coerceIn(labels.indices)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (containerColor != Color.Unspecified) {
                    Modifier.container(
                        shape = shape,
                        color = containerColor
                    )
                } else Modifier
            )
    ) {
        val shouldScroll = labels.estimatedWidth() > maxWidth.value

        EnhancedButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            enabled = !previewOnly,
            entries = labels,
            value = labels[safeIndex],
            itemContent = {
                AutoSizeText(
                    text = it,
                    maxLines = 1,
                    style = LocalTextStyle.current
                )
            },
            title = stringResource(title),
            onValueChange = {
                onValueChange(labels.indexOf(it))
            },
            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            isScrollable = shouldScroll,
            contentPadding = if (containerColor != Color.Unspecified) {
                PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            } else {
                PaddingValues(vertical = 4.dp)
            }
        )
    }
}

@Composable
private fun NtscToggleGroup(
    title: Int,
    checked: Boolean,
    previewOnly: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    PreferenceRowSwitch(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        title = stringResource(title),
        enabled = !previewOnly,
        applyHorizontalPadding = false,
        resultModifier = Modifier.padding(16.dp),
        checked = checked,
        onClick = onCheckedChange,
        shape = ShapeDefaults.large,
        containerColor = MaterialTheme.colorScheme.surface,
        additionalContent = {
            AnimatedVisibility(
                visible = checked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    content()
                }
            }
        }
    )
}

private fun String.ntscEnumName(): String {
    if (length <= 3 && all { it.isUpperCase() }) return this

    return removePrefix("DEGREES_")
        .lowercase()
        .split('_')
        .joinToString(" ") { part ->
            part.replaceFirstChar { it.uppercase() }
        }
}

private fun List<String>.estimatedWidth(): Float {
    return sumOf { label ->
        label.length * 8 + 32
    }.toFloat()
}
