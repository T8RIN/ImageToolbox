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
    val amount: Float = 0.75f,
    val chromaBleed: Float = 0.45f,
    val tapeWear: Float = 0.35f,
    val noise: Float = 0.30f,
    val tracking: Float = 0.20f,
    val seed: Int = 0,
    val lumaSmear: Float = 0.50f,
    val compositeSharpening: Float = 0.70f,
    val ringing: Float = 0.18f,
    val snow: Float = 0.08f,
    val processingDownscale: Int = 0
)