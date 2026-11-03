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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.VHSNtsc.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.NtscSettings
import com.t8rin.trickle.Trickle
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.GLITCH)
internal class VHSNtscFilter(
    override val value: Value
) : FilterTransformation, Filter.VHSNtsc {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        if (value.amount <= 0f) return input

        return Trickle.ntsc(
            src = input,
            settings = value.toNtscSettings(),
            scaleFactorX = value.scaleFactorX,
            scaleFactorY = value.scaleFactorY
        )
    }

    companion object : Filter.Schema.Custom<Value> {
        override val info = FilterInfo(R.string.vhs_ntsc, R.string.filter_help_vhs_ntsc)

        override val parameters = structuredParameters<Value> {
            field(Value::amount, floatParam(R.string.amount, 1f, 0f..3f))
            field(Value::chromaBleed, floatParam(R.string.chroma_bleed, 1f, 0f..4f))
            field(Value::tapeWear, floatParam(R.string.tape_wear, 1f, 0f..4f))
            field(Value::noise, floatParam(R.string.noise, 1f, 0f..4f))
            field(Value::tracking, floatParam(R.string.tracking, 1f, 0f..4f))
            field(Value::seed, intParam(R.string.seed, 0, 0..10_000))
            field(Value::lumaSmear, floatParam(R.string.luma_smear, 0.5f, 0f..8f))
            field(Value::compositeSharpening, floatParam(R.string.sharpen, 1f, 0f..8f))
            field(Value::ringing, floatParam(R.string.ringing, 4f, 0f..16f))
            field(Value::snow, floatParam(R.string.snow, 1f, 0f..4f))
            field(
                Value::useField,
                intParam(R.string.ntsc_use_field, 4, NtscSettings.UseField.entries.indices)
            )
            field(
                Value::filterType,
                intParam(R.string.ntsc_filter_type, 1, NtscSettings.FilterType.entries.indices)
            )
            field(
                Value::inputLumaFilter,
                intParam(
                    R.string.ntsc_input_luma_filter,
                    2,
                    NtscSettings.LumaLowpass.entries.indices
                )
            )
            field(
                Value::chromaLowpassIn,
                intParam(
                    R.string.ntsc_chroma_lowpass_in,
                    2,
                    NtscSettings.ChromaLowpass.entries.indices
                )
            )
            field(
                Value::chromaDemodulation,
                intParam(
                    R.string.ntsc_chroma_demodulation,
                    1,
                    NtscSettings.ChromaDemodulationFilter.entries.indices
                )
            )
            field(
                Value::videoScanlinePhaseShift,
                intParam(R.string.ntsc_phase_shift, 2, NtscSettings.PhaseShift.entries.indices)
            )
            field(
                Value::videoScanlinePhaseShiftOffset,
                intParam(R.string.ntsc_phase_offset, 0, -16..16)
            )
            field(
                Value::headSwitchingHeight,
                intParam(R.string.ntsc_head_switching_height, 8, 0..160)
            )
            field(
                Value::headSwitchingOffset,
                intParam(R.string.ntsc_head_switching_offset, 3, -80..80)
            )
            field(
                Value::headSwitchingHorizontalShift,
                floatParam(R.string.ntsc_head_switching_shift, 72f, -800f..800f, 1)
            )
            field(
                Value::headSwitchingMidLinePosition,
                floatParam(R.string.ntsc_midline_position, 0.95f, 0f..1f)
            )
            field(
                Value::headSwitchingMidLineJitter,
                floatParam(R.string.ntsc_midline_jitter, 0.03f, 0f..2f)
            )
            field(
                Value::trackingNoiseHeight,
                intParam(R.string.ntsc_tracking_noise_height, 12, 0..160)
            )
            field(
                Value::trackingNoiseWaveIntensity,
                floatParam(R.string.ntsc_tracking_wave, 15f, 0f..160f, 1)
            )
            field(
                Value::trackingNoiseSnowIntensity,
                floatParam(R.string.ntsc_tracking_snow, 0.025f, 0f..1f, 3)
            )
            field(
                Value::trackingNoiseSnowAnisotropy,
                floatParam(R.string.ntsc_tracking_snow_anisotropy, 0.25f, 0f..1f)
            )
            field(
                Value::trackingNoiseNoiseIntensity,
                floatParam(R.string.ntsc_tracking_noise_intensity, 0.25f, 0f..2f)
            )
            field(
                Value::compositeNoiseFrequency,
                floatParam(R.string.ntsc_composite_noise_frequency, 0.5f, 0f..8f)
            )
            field(
                Value::compositeNoiseIntensity,
                floatParam(R.string.ntsc_composite_noise_intensity, 0.05f, 0f..3f)
            )
            field(
                Value::compositeNoiseDetail,
                intParam(R.string.ntsc_composite_noise_detail, 1, 0..16)
            )
            field(
                Value::ringingFrequency,
                floatParam(R.string.ntsc_ringing_frequency, 0.45f, 0f..8f)
            )
            field(Value::ringingPower, floatParam(R.string.ntsc_ringing_power, 4f, 0f..20f))
            field(
                Value::lumaNoiseFrequency,
                floatParam(R.string.ntsc_luma_noise_frequency, 0.5f, 0f..8f)
            )
            field(
                Value::lumaNoiseIntensity,
                floatParam(R.string.ntsc_luma_noise_intensity, 0.01f, 0f..3f)
            )
            field(Value::lumaNoiseDetail, intParam(R.string.ntsc_luma_noise_detail, 1, 0..16))
            field(
                Value::chromaNoiseFrequency,
                floatParam(R.string.ntsc_chroma_noise_frequency, 0.05f, 0f..8f)
            )
            field(
                Value::chromaNoiseIntensity,
                floatParam(R.string.ntsc_chroma_noise_intensity, 0.1f, 0f..3f)
            )
            field(Value::chromaNoiseDetail, intParam(R.string.ntsc_chroma_noise_detail, 2, 0..16))
            field(
                Value::snowIntensity,
                floatParam(R.string.ntsc_snow_intensity, 0.00025f, 0f..0.1f, 4)
            )
            field(Value::snowAnisotropy, floatParam(R.string.ntsc_snow_anisotropy, 0.5f, 0f..1f))
            field(
                Value::chromaPhaseNoiseIntensity,
                floatParam(R.string.ntsc_chroma_phase_noise, 0.001f, 0f..0.25f, 4)
            )
            field(
                Value::chromaPhaseError,
                floatParam(R.string.ntsc_chroma_phase_error, 0f, -1f..1f, 3)
            )
            field(
                Value::chromaDelayHorizontal,
                floatParam(R.string.ntsc_chroma_delay_horizontal, 0f, -100f..100f)
            )
            field(
                Value::chromaDelayVertical,
                intParam(R.string.ntsc_chroma_delay_vertical, 0, -40..40)
            )
            field(
                Value::vhsTapeSpeed,
                intParam(R.string.ntsc_vhs_tape_speed, 2, NtscSettings.VHSTapeSpeed.entries.indices)
            )
            field(
                Value::vhsChromaLoss,
                floatParam(R.string.ntsc_vhs_chroma_loss, 0.000025f, 0f..0.01f, 5)
            )
            field(
                Value::vhsSharpenIntensity,
                floatParam(R.string.ntsc_vhs_sharpen_intensity, 0.25f, 0f..4f)
            )
            field(
                Value::vhsSharpenFrequency,
                floatParam(R.string.ntsc_vhs_sharpen_frequency, 1f, 0f..12f)
            )
            field(
                Value::vhsEdgeWaveIntensity,
                floatParam(R.string.ntsc_vhs_edge_wave_intensity, 0.5f, 0f..6f)
            )
            field(
                Value::vhsEdgeWaveSpeed,
                floatParam(R.string.ntsc_vhs_edge_wave_speed, 4f, 0f..24f)
            )
            field(
                Value::vhsEdgeWaveFrequency,
                floatParam(R.string.ntsc_vhs_edge_wave_frequency, 0.05f, 0f..1f, 3)
            )
            field(Value::vhsEdgeWaveDetail, intParam(R.string.ntsc_vhs_edge_wave_detail, 2, 0..16))
            field(
                Value::chromaLowpassOut,
                intParam(
                    R.string.ntsc_chroma_lowpass_out,
                    2,
                    NtscSettings.ChromaLowpass.entries.indices
                )
            )
            field(Value::scaleHorizontal, floatParam(R.string.ntsc_scale_horizontal, 1f, 0.1f..4f))
            field(Value::scaleVertical, floatParam(R.string.ntsc_scale_vertical, 1f, 0.1f..4f))
            field(Value::scaleFactorX, floatParam(R.string.ntsc_scale_factor_x, 1f, 0.1f..4f))
            field(Value::scaleFactorY, floatParam(R.string.ntsc_scale_factor_y, 1f, 0.1f..4f))
            field(Value::headSwitchingEnabled, booleanParam(R.string.ntsc_head_switching, true))
            field(Value::trackingNoiseEnabled, booleanParam(R.string.ntsc_tracking_noise, true))
            field(Value::compositeNoiseEnabled, booleanParam(R.string.ntsc_composite_noise, true))
            field(Value::ringingEnabled, booleanParam(R.string.ringing, true))
            field(Value::lumaNoiseEnabled, booleanParam(R.string.ntsc_luma_noise, true))
            field(Value::chromaNoiseEnabled, booleanParam(R.string.ntsc_chroma_noise, true))
            field(Value::vhsEnabled, booleanParam(R.string.vhs, true))
            field(Value::scaleEnabled, booleanParam(R.string.scale, true))
        }
    }
}

private fun Value.toNtscSettings(): NtscSettings {
    val amountFactor = amount
    val chromaBleedAmount = chromaBleed * amountFactor
    val tapeWearAmount = tapeWear * amountFactor
    val noiseAmount = noise * amountFactor
    val trackingAmount = tracking * amountFactor
    val snowAmount = snow * amountFactor
    val ringingAmount = ringing * amountFactor
    val selectedTapeSpeed = NtscSettings.VHSTapeSpeed.entries[vhsTapeSpeed]

    return NtscSettings(
        randomSeed = seed,
        useField = NtscSettings.UseField.entries[useField],
        filterType = NtscSettings.FilterType.entries[filterType],
        inputLumaFilter = NtscSettings.LumaLowpass.entries[inputLumaFilter],
        chromaLowpassIn = NtscSettings.ChromaLowpass.entries[chromaLowpassIn],
        chromaDemodulation = NtscSettings.ChromaDemodulationFilter.entries[chromaDemodulation],
        lumaSmear = lumaSmear * amountFactor,
        compositeSharpening = compositeSharpening * amountFactor,
        videoScanlinePhaseShift = NtscSettings.PhaseShift.entries[videoScanlinePhaseShift],
        videoScanlinePhaseShiftOffset = videoScanlinePhaseShiftOffset,
        headSwitching = if (headSwitchingEnabled) NtscSettings.HeadSwitching(
            height = (headSwitchingHeight * maxOf(trackingAmount, tapeWearAmount * 0.7f))
                .roundToInt(),
            offset = (headSwitchingOffset * trackingAmount).roundToInt(),
            horizontalShift = headSwitchingHorizontalShift *
                    maxOf(trackingAmount, tapeWearAmount * 0.7f),
            midLine = NtscSettings.HeadSwitchingMidLine(
                position = headSwitchingMidLinePosition,
                jitter = headSwitchingMidLineJitter * trackingAmount
            )
        ) else null,
        trackingNoise = if (trackingNoiseEnabled) NtscSettings.TrackingNoise(
            height = (trackingNoiseHeight * trackingAmount).roundToInt(),
            waveIntensity = trackingNoiseWaveIntensity * trackingAmount,
            snowIntensity = trackingNoiseSnowIntensity * trackingAmount,
            snowAnisotropy = trackingNoiseSnowAnisotropy,
            noiseIntensity = trackingNoiseNoiseIntensity * trackingAmount
        ) else null,
        compositeNoise = if (compositeNoiseEnabled) NtscSettings.FbmNoise(
            frequency = compositeNoiseFrequency,
            intensity = compositeNoiseIntensity * noiseAmount,
            detail = compositeNoiseDetail
        ) else null,
        ringing = if (ringingEnabled) NtscSettings.Ringing(
            frequency = ringingFrequency,
            power = ringingPower,
            intensity = ringingAmount
        ) else null,
        lumaNoise = if (lumaNoiseEnabled) NtscSettings.FbmNoise(
            frequency = lumaNoiseFrequency,
            intensity = lumaNoiseIntensity * noiseAmount,
            detail = lumaNoiseDetail
        ) else null,
        chromaNoise = if (chromaNoiseEnabled) NtscSettings.FbmNoise(
            frequency = chromaNoiseFrequency,
            intensity = chromaNoiseIntensity * chromaBleedAmount,
            detail = chromaNoiseDetail
        ) else null,
        snowIntensity = snowIntensity * snowAmount,
        snowAnisotropy = snowAnisotropy,
        chromaPhaseNoiseIntensity = chromaPhaseNoiseIntensity * chromaBleedAmount,
        chromaPhaseError = chromaPhaseError * chromaBleedAmount,
        chromaDelayHorizontal = chromaDelayHorizontal * chromaBleedAmount,
        chromaDelayVertical = (chromaDelayVertical * chromaBleedAmount).roundToInt(),
        vhs = if (vhsEnabled) NtscSettings.VHS(
            tapeSpeed = if (tapeWearAmount > 0.03f) {
                selectedTapeSpeed
            } else {
                NtscSettings.VHSTapeSpeed.NONE
            },
            chromaLoss = vhsChromaLoss * tapeWearAmount,
            sharpen = NtscSettings.VHSSharpen(
                intensity = vhsSharpenIntensity * amountFactor,
                frequency = vhsSharpenFrequency
            ),
            edgeWave = NtscSettings.VHSEdgeWave(
                intensity = vhsEdgeWaveIntensity * tapeWearAmount,
                speed = vhsEdgeWaveSpeed,
                frequency = vhsEdgeWaveFrequency,
                detail = vhsEdgeWaveDetail
            )
        ) else null,
        chromaLowpassOut = NtscSettings.ChromaLowpass.entries[chromaLowpassOut],
        scale = if (scaleEnabled) NtscSettings.Scale(
            horizontal = scaleHorizontal,
            vertical = scaleVertical
        ) else null
    )
}