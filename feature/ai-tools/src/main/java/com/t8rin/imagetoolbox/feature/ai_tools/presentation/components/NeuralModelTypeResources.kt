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

import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel

fun NeuralModel.Type.title(): Int = when (this) {
    NeuralModel.Type.DEJPEG -> R.string.type_dejpeg
    NeuralModel.Type.DENOISE -> R.string.type_denoise
    NeuralModel.Type.COLORIZE -> R.string.type_colorize
    NeuralModel.Type.ARTIFACTS -> R.string.type_artifacts
    NeuralModel.Type.ENHANCE -> R.string.type_enhance
    NeuralModel.Type.ANIME -> R.string.type_anime
    NeuralModel.Type.SCANS -> R.string.type_scans
}