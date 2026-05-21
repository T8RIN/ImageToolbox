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
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.NtscParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.trickle.NtscSettings
import com.t8rin.trickle.Trickle
import kotlin.math.roundToInt

@FilterInject
internal class VHSNtscFilter(
    override val value: NtscParams = NtscParams()
) : Transformation<Bitmap>, Filter.VHSNtsc {

    override val cacheKey: String
        get() = value.hashCode().toString()

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

}

private fun NtscParams.toNtscSettings(): NtscSettings {
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
