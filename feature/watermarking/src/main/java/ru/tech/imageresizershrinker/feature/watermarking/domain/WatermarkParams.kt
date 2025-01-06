/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.watermarking.domain

import ru.tech.imageresizershrinker.core.domain.image.model.BlendingMode
import ru.tech.imageresizershrinker.core.settings.domain.model.FontType

data class WatermarkParams(
    val positionX: Float,
    val positionY: Float,
    val rotation: Int,
    val alpha: Float,
    val isRepeated: Boolean,
    val overlayMode: BlendingMode,
    val watermarkingType: WatermarkingType
) {
    companion object {
        val Default by lazy {
            WatermarkParams(
                positionX = 0f,
                positionY = 0f,
                rotation = 45,
                alpha = 0.5f,
                isRepeated = true,
                overlayMode = BlendingMode.SrcOver,
                watermarkingType = WatermarkingType.Text.Default
            )
        }
    }
}

sealed interface WatermarkingType {
    data class Text(
        val color: Int,
        val style: Int,
        val size: Float,
        val font: FontType?,
        val backgroundColor: Int,
        val text: String,
    ) : WatermarkingType {
        companion object {
            val Default by lazy {
                Text(
                    color = -16777216,
                    style = 0,
                    size = 0.1f,
                    font = null,
                    backgroundColor = 0,
                    text = "Watermark"
                )
            }
        }
    }

    data class Image(
        val imageData: Any,
        val size: Float
    ) : WatermarkingType {
        companion object {
            val Default by lazy {
                Image(
                    size = 0.1f,
                    imageData = "file:///android_asset/svg/emotions/aasparkles.svg"
                )
            }
        }
    }

    companion object {
        val entries by lazy {
            listOf(Text.Default, Image.Default)
        }
    }
}