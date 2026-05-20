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
            input,
            value.toNtscSettings(),
            value.processingDownscale.coerceIn(0, 4) + 1,
            1f,
            1f
        )
    }

}

private fun NtscParams.toNtscSettings(): NtscSettings {
    val amount = amount.coerceIn(0f, 1f)
    val chromaBleed = chromaBleed.coerceIn(0f, 1f) * amount
    val tapeWear = tapeWear.coerceIn(0f, 1f) * amount
    val noise = noise.coerceIn(0f, 1f) * amount
    val tracking = tracking.coerceIn(0f, 1f) * amount
    val lumaSmear = lumaSmear.coerceIn(0f, 1f) * amount
    val sharpening = compositeSharpening.coerceIn(0f, 1f)
    val ringing = ringing.coerceIn(0f, 1f) * amount
    val snow = snow.coerceIn(0f, 1f) * amount

    return NtscSettings(
        randomSeed = seed,
        useField = NtscSettings.UseField.INTERLEAVED_UPPER,
        filterType = NtscSettings.FilterType.BUTTERWORTH,
        inputLumaFilter = NtscSettings.LumaLowpass.NOTCH,
        chromaLowpassIn = NtscSettings.ChromaLowpass.FULL,
        chromaDemodulation = NtscSettings.ChromaDemodulationFilter.NOTCH,
        lumaSmear = lumaSmear,
        compositeSharpening = 0.5f + sharpening * amount * 1.5f,
        videoScanlinePhaseShift = NtscSettings.PhaseShift.DEGREES_180,
        videoScanlinePhaseShiftOffset = 0,
        headSwitching = NtscSettings.HeadSwitching(
            height = ((tracking * 12f) + (tapeWear * 6f)).roundToInt(),
            offset = (tracking * 4f).roundToInt(),
            horizontalShift = tracking * 80f + tapeWear * 24f,
            midLine = NtscSettings.HeadSwitchingMidLine(
                position = 0.30f + tracking * 0.40f,
                jitter = tracking * 0.25f
            )
        ),
        trackingNoise = NtscSettings.TrackingNoise(
            height = if (tracking > 0.01f) 4 + (tracking * 20f).roundToInt() else 0,
            waveIntensity = tracking * 24f,
            snowIntensity = tracking * 0.035f,
            snowAnisotropy = 0.30f,
            noiseIntensity = tracking * 0.25f
        ),
        compositeNoise = NtscSettings.FbmNoise(
            frequency = 0.50f,
            intensity = noise * 0.16f,
            detail = 2
        ),
        ringing = NtscSettings.Ringing(
            frequency = 0.35f + ringing * 0.75f,
            power = 1f + ringing * 2f,
            intensity = ringing * 0.28f
        ),
        lumaNoise = NtscSettings.FbmNoise(
            frequency = 0.50f,
            intensity = noise * 0.05f,
            detail = 1 + (noise * 2f).roundToInt()
        ),
        chromaNoise = NtscSettings.FbmNoise(
            frequency = 0.05f,
            intensity = chromaBleed * 0.18f,
            detail = 2
        ),
        snowIntensity = snow * 0.0015f,
        snowAnisotropy = 0.45f,
        chromaPhaseNoiseIntensity = chromaBleed * 0.008f,
        chromaPhaseError = chromaBleed * 0.08f,
        chromaDelayHorizontal = chromaBleed * 5f,
        chromaDelayVertical = (chromaBleed * 2f).roundToInt(),
        vhs = NtscSettings.VHS(
            tapeSpeed = tapeWear.toTapeSpeed(),
            chromaLoss = tapeWear * 0.00008f,
            sharpen = NtscSettings.VHSSharpen(
                intensity = sharpening * amount * 0.35f,
                frequency = 1f + sharpening
            ),
            edgeWave = NtscSettings.VHSEdgeWave(
                intensity = tapeWear * 0.65f,
                speed = 3.0f + tapeWear * 3f,
                frequency = 0.03f + tapeWear * 0.07f,
                detail = 2 + (tapeWear * 3f).roundToInt()
            )
        ),
        chromaVertBlend = true,
        chromaLowpassOut = NtscSettings.ChromaLowpass.FULL,
        scale = NtscSettings.Scale()
    )
}

private fun Float.toTapeSpeed(): NtscSettings.VHSTapeSpeed = when {
    this <= 0.03f -> NtscSettings.VHSTapeSpeed.NONE
    this < 0.28f -> NtscSettings.VHSTapeSpeed.SP
    this < 0.58f -> NtscSettings.VHSTapeSpeed.LP
    else -> NtscSettings.VHSTapeSpeed.EP
}