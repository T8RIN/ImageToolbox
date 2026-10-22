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

package com.t8rin.imagetoolbox.core.domain.model

enum class AssistantModel(
    val id: String,
    val displayName: String,
    val sourceUrl: String,
    val supportsVision: Boolean = false
) {
    Lite("lite", "Qwen3 0.6B", "https://huggingface.co/Qwen/Qwen3-0.6B"),
    Advanced("advanced", "Qwen3 1.7B", "https://huggingface.co/Qwen/Qwen3-1.7B"),
    Max("max", "Qwen3 8B", "https://huggingface.co/Qwen/Qwen3-8B"),
    VisionLite("vision_lite", "Qwen3.5 0.8B", "https://huggingface.co/Qwen/Qwen3.5-0.8B", true),
    VisionAdvanced("vision_advanced", "Qwen3.5 2B", "https://huggingface.co/Qwen/Qwen3.5-2B", true),
    VisionMax("vision_max", "Qwen3.5 9B", "https://huggingface.co/Qwen/Qwen3.5-9B", true);

    companion object {
        fun fromOrdinal(ordinal: Int?): AssistantModel? = ordinal?.let(entries::getOrNull)
    }
}