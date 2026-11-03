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

        override val parameters = structuredParameters {
            Value::amount.float(R.string.amount, 1f, 0f..3f)
            Value::chromaBleed.float(R.string.chroma_bleed, 1f, 0f..4f)
            Value::tapeWear.float(R.string.tape_wear, 1f, 0f..4f)
            Value::noise.float(R.string.noise, 1f, 0f..4f)
            Value::tracking.float(R.string.tracking, 1f, 0f..4f)
            Value::seed.seed(R.string.seed, 0, 0..10_000)
            Value::lumaSmear.float(R.string.luma_smear, 0.5f, 0f..8f)
            Value::compositeSharpening.float(R.string.sharpen, 1f, 0f..8f)
            Value::ringing.float(R.string.ringing, 4f, 0f..16f)
            Value::snow.float(R.string.snow, 1f, 0f..4f)
            Value::useField.int(R.string.ntsc_use_field, 4, NtscSettings.UseField.entries.indices)
            Value::filterType.int(
                R.string.ntsc_filter_type,
                1,
                NtscSettings.FilterType.entries.indices
            )
            Value::inputLumaFilter.int(
                R.string.ntsc_input_luma_filter,
                2,
                NtscSettings.LumaLowpass.entries.indices
            )
            Value::chromaLowpassIn.int(
                R.string.ntsc_chroma_lowpass_in,
                2,
                NtscSettings.ChromaLowpass.entries.indices
            )
            Value::chromaDemodulation.int(
                R.string.ntsc_chroma_demodulation,
                1,
                NtscSettings.ChromaDemodulationFilter.entries.indices
            )
            Value::videoScanlinePhaseShift.int(
                R.string.ntsc_phase_shift,
                2,
                NtscSettings.PhaseShift.entries.indices
            )
            Value::videoScanlinePhaseShiftOffset.int(R.string.ntsc_phase_offset, 0, -16..16)
            Value::headSwitchingHeight.int(R.string.ntsc_head_switching_height, 8, 0..160)
            Value::headSwitchingOffset.int(R.string.ntsc_head_switching_offset, 3, -80..80)
            Value::headSwitchingHorizontalShift.float(
                R.string.ntsc_head_switching_shift,
                72f,
                -800f..800f,
                1
            )
            Value::headSwitchingMidLinePosition.float(R.string.ntsc_midline_position, 0.95f, 0f..1f)
            Value::headSwitchingMidLineJitter.float(R.string.ntsc_midline_jitter, 0.03f, 0f..2f)
            Value::trackingNoiseHeight.int(R.string.ntsc_tracking_noise_height, 12, 0..160)
            Value::trackingNoiseWaveIntensity.float(R.string.ntsc_tracking_wave, 15f, 0f..160f, 1)
            Value::trackingNoiseSnowIntensity.float(R.string.ntsc_tracking_snow, 0.025f, 0f..1f, 3)
            Value::trackingNoiseSnowAnisotropy.float(
                R.string.ntsc_tracking_snow_anisotropy,
                0.25f,
                0f..1f
            )
            Value::trackingNoiseNoiseIntensity.float(
                R.string.ntsc_tracking_noise_intensity,
                0.25f,
                0f..2f
            )
            Value::compositeNoiseFrequency.float(
                R.string.ntsc_composite_noise_frequency,
                0.5f,
                0f..8f
            )
            Value::compositeNoiseIntensity.float(
                R.string.ntsc_composite_noise_intensity,
                0.05f,
                0f..3f
            )
            Value::compositeNoiseDetail.int(R.string.ntsc_composite_noise_detail, 1, 0..16)
            Value::ringingFrequency.float(R.string.ntsc_ringing_frequency, 0.45f, 0f..8f)
            Value::ringingPower.float(R.string.ntsc_ringing_power, 4f, 0f..20f)
            Value::lumaNoiseFrequency.float(R.string.ntsc_luma_noise_frequency, 0.5f, 0f..8f)
            Value::lumaNoiseIntensity.float(R.string.ntsc_luma_noise_intensity, 0.01f, 0f..3f)
            Value::lumaNoiseDetail.int(R.string.ntsc_luma_noise_detail, 1, 0..16)
            Value::chromaNoiseFrequency.float(R.string.ntsc_chroma_noise_frequency, 0.05f, 0f..8f)
            Value::chromaNoiseIntensity.float(R.string.ntsc_chroma_noise_intensity, 0.1f, 0f..3f)
            Value::chromaNoiseDetail.int(R.string.ntsc_chroma_noise_detail, 2, 0..16)
            Value::snowIntensity.float(R.string.ntsc_snow_intensity, 0.00025f, 0f..0.1f, 4)
            Value::snowAnisotropy.float(R.string.ntsc_snow_anisotropy, 0.5f, 0f..1f)
            Value::chromaPhaseNoiseIntensity.float(
                R.string.ntsc_chroma_phase_noise,
                0.001f,
                0f..0.25f,
                4
            )
            Value::chromaPhaseError.float(R.string.ntsc_chroma_phase_error, 0f, -1f..1f, 3)
            Value::chromaDelayHorizontal.float(
                R.string.ntsc_chroma_delay_horizontal,
                0f,
                -100f..100f
            )
            Value::chromaDelayVertical.int(R.string.ntsc_chroma_delay_vertical, 0, -40..40)
            Value::vhsTapeSpeed.int(
                R.string.ntsc_vhs_tape_speed,
                2,
                NtscSettings.VHSTapeSpeed.entries.indices
            )
            Value::vhsChromaLoss.float(R.string.ntsc_vhs_chroma_loss, 0.000025f, 0f..0.01f, 5)
            Value::vhsSharpenIntensity.float(R.string.ntsc_vhs_sharpen_intensity, 0.25f, 0f..4f)
            Value::vhsSharpenFrequency.float(R.string.ntsc_vhs_sharpen_frequency, 1f, 0f..12f)
            Value::vhsEdgeWaveIntensity.float(R.string.ntsc_vhs_edge_wave_intensity, 0.5f, 0f..6f)
            Value::vhsEdgeWaveSpeed.float(R.string.ntsc_vhs_edge_wave_speed, 4f, 0f..24f)
            Value::vhsEdgeWaveFrequency.float(
                R.string.ntsc_vhs_edge_wave_frequency,
                0.05f,
                0f..1f,
                3
            )
            Value::vhsEdgeWaveDetail.int(R.string.ntsc_vhs_edge_wave_detail, 2, 0..16)
            Value::chromaLowpassOut.int(
                R.string.ntsc_chroma_lowpass_out,
                2,
                NtscSettings.ChromaLowpass.entries.indices
            )
            Value::scaleHorizontal.float(R.string.ntsc_scale_horizontal, 1f, 0.1f..4f)
            Value::scaleVertical.float(R.string.ntsc_scale_vertical, 1f, 0.1f..4f)
            Value::scaleFactorX.float(R.string.ntsc_scale_factor_x, 1f, 0.1f..4f)
            Value::scaleFactorY.float(R.string.ntsc_scale_factor_y, 1f, 0.1f..4f)
            Value::headSwitchingEnabled.boolean(R.string.ntsc_head_switching, true)
            Value::trackingNoiseEnabled.boolean(R.string.ntsc_tracking_noise, true)
            Value::compositeNoiseEnabled.boolean(R.string.ntsc_composite_noise, true)
            Value::ringingEnabled.boolean(R.string.ringing, true)
            Value::lumaNoiseEnabled.boolean(R.string.ntsc_luma_noise, true)
            Value::chromaNoiseEnabled.boolean(R.string.ntsc_chroma_noise, true)
            Value::vhsEnabled.boolean(R.string.vhs, true)
            Value::scaleEnabled.boolean(R.string.scale, true)
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