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
            frame = value.processingDownscale.coerceIn(0, 4) + 1,
            scaleFactorX = value.resizeRatio.coerceIn(0.25f, 2f),
            scaleFactorY = value.outputScale.coerceIn(0.25f, 2f)
        )
    }

}

private fun NtscParams.toNtscSettings(): NtscSettings {
    val amount = amount.coerceIn(0f, 2f)
    val chromaBleed = chromaBleed.coerceIn(0f, 2f) * amount
    val tapeWear = tapeWear.coerceIn(0f, 2f) * amount
    val noise = noise.coerceIn(0f, 2f) * amount
    val tracking = tracking.coerceIn(0f, 2f) * amount
    val snow = snow.coerceIn(0f, 2f) * amount
    val ringing = ringing.coerceIn(0f, 8f) * amount
    val selectedTapeSpeed = NtscSettings.VHSTapeSpeed.entries.safeGet(vhsTapeSpeed)

    return NtscSettings(
        randomSeed = seed,
        useField = NtscSettings.UseField.entries.safeGet(useField),
        filterType = NtscSettings.FilterType.entries.safeGet(filterType),
        inputLumaFilter = NtscSettings.LumaLowpass.entries.safeGet(inputLumaFilter),
        chromaLowpassIn = NtscSettings.ChromaLowpass.entries.safeGet(chromaLowpassIn),
        chromaDemodulation = NtscSettings.ChromaDemodulationFilter.entries.safeGet(
            chromaDemodulation
        ),
        lumaSmear = lumaSmear.coerceIn(0f, 4f) * amount,
        compositeSharpening = compositeSharpening.coerceIn(0f, 4f) * amount,
        videoScanlinePhaseShift = NtscSettings.PhaseShift.entries.safeGet(videoScanlinePhaseShift),
        videoScanlinePhaseShiftOffset = videoScanlinePhaseShiftOffset.coerceIn(-8, 8),
        headSwitching = if (headSwitchingEnabled) NtscSettings.HeadSwitching(
            height = (headSwitchingHeight.coerceIn(0, 80) * maxOf(tracking, tapeWear * 0.7f))
                .roundToInt(),
            offset = (headSwitchingOffset.coerceIn(-30, 30) * tracking).roundToInt(),
            horizontalShift = headSwitchingHorizontalShift.coerceIn(-300f, 300f) *
                    maxOf(tracking, tapeWear * 0.7f),
            midLine = NtscSettings.HeadSwitchingMidLine(
                position = headSwitchingMidLinePosition.coerceIn(0f, 1f),
                jitter = headSwitchingMidLineJitter.coerceIn(0f, 1f) * tracking
            )
        ) else null,
        trackingNoise = if (trackingNoiseEnabled) NtscSettings.TrackingNoise(
            height = (trackingNoiseHeight.coerceIn(0, 80) * tracking).roundToInt(),
            waveIntensity = trackingNoiseWaveIntensity.coerceIn(0f, 80f) * tracking,
            snowIntensity = trackingNoiseSnowIntensity.coerceIn(0f, 0.25f) * tracking,
            snowAnisotropy = trackingNoiseSnowAnisotropy.coerceIn(0f, 1f),
            noiseIntensity = trackingNoiseNoiseIntensity.coerceIn(0f, 1f) * tracking
        ) else null,
        compositeNoise = if (compositeNoiseEnabled) NtscSettings.FbmNoise(
            frequency = compositeNoiseFrequency.coerceIn(0f, 3f),
            intensity = compositeNoiseIntensity.coerceIn(0f, 1f) * noise,
            detail = compositeNoiseDetail.coerceIn(0, 8)
        ) else null,
        ringing = if (ringingEnabled) NtscSettings.Ringing(
            frequency = ringingFrequency.coerceIn(0f, 3f),
            power = ringingPower.coerceIn(0f, 10f),
            intensity = ringing
        ) else null,
        lumaNoise = if (lumaNoiseEnabled) NtscSettings.FbmNoise(
            frequency = lumaNoiseFrequency.coerceIn(0f, 3f),
            intensity = lumaNoiseIntensity.coerceIn(0f, 1f) * noise,
            detail = lumaNoiseDetail.coerceIn(0, 8)
        ) else null,
        chromaNoise = if (chromaNoiseEnabled) NtscSettings.FbmNoise(
            frequency = chromaNoiseFrequency.coerceIn(0f, 3f),
            intensity = chromaNoiseIntensity.coerceIn(0f, 1f) * chromaBleed,
            detail = chromaNoiseDetail.coerceIn(0, 8)
        ) else null,
        snowIntensity = snowIntensity.coerceIn(0f, 0.02f) * snow,
        snowAnisotropy = snowAnisotropy.coerceIn(0f, 1f),
        chromaPhaseNoiseIntensity = chromaPhaseNoiseIntensity.coerceIn(0f, 0.08f) *
                chromaBleed,
        chromaPhaseError = chromaPhaseError.coerceIn(0f, 0.6f) * chromaBleed,
        chromaDelayHorizontal = chromaDelayHorizontal.coerceIn(-25f, 25f) * chromaBleed,
        chromaDelayVertical = (chromaDelayVertical.coerceIn(-10, 10) * chromaBleed).roundToInt(),
        vhs = if (vhsEnabled) NtscSettings.VHS(
            tapeSpeed = if (tapeWear > 0.03f) {
                selectedTapeSpeed
            } else {
                NtscSettings.VHSTapeSpeed.NONE
            },
            chromaLoss = vhsChromaLoss.coerceIn(0f, 0.001f) * tapeWear,
            sharpen = NtscSettings.VHSSharpen(
                intensity = vhsSharpenIntensity.coerceIn(0f, 2f) * amount,
                frequency = vhsSharpenFrequency.coerceIn(0f, 6f)
            ),
            edgeWave = NtscSettings.VHSEdgeWave(
                intensity = vhsEdgeWaveIntensity.coerceIn(0f, 3f) * tapeWear,
                speed = vhsEdgeWaveSpeed.coerceIn(0f, 12f),
                frequency = vhsEdgeWaveFrequency.coerceIn(0f, 0.3f),
                detail = vhsEdgeWaveDetail.coerceIn(0, 8)
            )
        ) else null,
        chromaLowpassOut = NtscSettings.ChromaLowpass.entries.safeGet(chromaLowpassOut),
        scale = if (scaleEnabled) NtscSettings.Scale(
            horizontal = scaleHorizontal.coerceIn(0.5f, 2f),
            vertical = scaleVertical.coerceIn(0.5f, 2f)
        ) else null
    )
}

private fun <T> List<T>.safeGet(index: Int): T {
    return this[index.coerceIn(indices)]
}