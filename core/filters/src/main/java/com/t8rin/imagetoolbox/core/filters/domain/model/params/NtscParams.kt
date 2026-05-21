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

package com.t8rin.imagetoolbox.core.filters.domain.model.params

data class NtscParams(
    val amount: Float = 1f,
    val chromaBleed: Float = 1f,
    val tapeWear: Float = 1f,
    val noise: Float = 1f,
    val tracking: Float = 1f,
    val seed: Int = 0,
    val lumaSmear: Float = 0.5f,
    val compositeSharpening: Float = 1f,
    val ringing: Float = 4f,
    val snow: Float = 1f,
    val useField: Int = 4,
    val filterType: Int = 1,
    val inputLumaFilter: Int = 2,
    val chromaLowpassIn: Int = 2,
    val chromaDemodulation: Int = 1,
    val videoScanlinePhaseShift: Int = 2,
    val videoScanlinePhaseShiftOffset: Int = 0,
    val headSwitchingEnabled: Boolean = true,
    val headSwitchingHeight: Int = 8,
    val headSwitchingOffset: Int = 3,
    val headSwitchingHorizontalShift: Float = 72f,
    val headSwitchingMidLinePosition: Float = 0.95f,
    val headSwitchingMidLineJitter: Float = 0.03f,
    val trackingNoiseEnabled: Boolean = true,
    val trackingNoiseHeight: Int = 12,
    val trackingNoiseWaveIntensity: Float = 15f,
    val trackingNoiseSnowIntensity: Float = 0.025f,
    val trackingNoiseSnowAnisotropy: Float = 0.25f,
    val trackingNoiseNoiseIntensity: Float = 0.25f,
    val compositeNoiseEnabled: Boolean = true,
    val compositeNoiseFrequency: Float = 0.5f,
    val compositeNoiseIntensity: Float = 0.05f,
    val compositeNoiseDetail: Int = 1,
    val ringingEnabled: Boolean = true,
    val ringingFrequency: Float = 0.45f,
    val ringingPower: Float = 4f,
    val lumaNoiseEnabled: Boolean = true,
    val lumaNoiseFrequency: Float = 0.5f,
    val lumaNoiseIntensity: Float = 0.01f,
    val lumaNoiseDetail: Int = 1,
    val chromaNoiseEnabled: Boolean = true,
    val chromaNoiseFrequency: Float = 0.05f,
    val chromaNoiseIntensity: Float = 0.1f,
    val chromaNoiseDetail: Int = 2,
    val snowIntensity: Float = 0.00025f,
    val snowAnisotropy: Float = 0.5f,
    val chromaPhaseNoiseIntensity: Float = 0.001f,
    val chromaPhaseError: Float = 0f,
    val chromaDelayHorizontal: Float = 0f,
    val chromaDelayVertical: Int = 0,
    val vhsEnabled: Boolean = true,
    val vhsTapeSpeed: Int = 2,
    val vhsChromaLoss: Float = 0.000025f,
    val vhsSharpenIntensity: Float = 0.25f,
    val vhsSharpenFrequency: Float = 1f,
    val vhsEdgeWaveIntensity: Float = 0.5f,
    val vhsEdgeWaveSpeed: Float = 4f,
    val vhsEdgeWaveFrequency: Float = 0.05f,
    val vhsEdgeWaveDetail: Int = 2,
    val chromaLowpassOut: Int = 2,
    val scaleEnabled: Boolean = true,
    val scaleHorizontal: Float = 1f,
    val scaleVertical: Float = 1f,
    val scaleFactorX: Float = 1f,
    val scaleFactorY: Float = 1f
)