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
import ru.tech.imageresizershrinker.core.domain.model.Position
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
        val params: TextParams,
        val text: String,
        val digitalParams: DigitalParams
    ) : WatermarkingType {
        companion object {
            val Default by lazy {
                Text(
                    params = TextParams.Default,
                    text = "Watermark",
                    digitalParams = DigitalParams.Default
                )
            }
        }
    }

    data class Image(
        val imageData: Any,
        val size: Float,
        val digitalParams: DigitalParams
    ) : WatermarkingType {
        companion object {
            val Default by lazy {
                Image(
                    size = 0.1f,
                    imageData = "file:///android_asset/svg/emotions/aasparkles.svg",
                    digitalParams = DigitalParams.Default
                )
            }
        }
    }

    sealed interface Stamp : WatermarkingType {
        val position: Position
        val padding: Float
        val params: TextParams

        data class Text(
            override val position: Position,
            override val padding: Float,
            override val params: TextParams,
            val text: String,
        ) : Stamp {
            companion object {
                val Default by lazy {
                    Text(
                        params = TextParams.Default.copy(size = 0.2f),
                        padding = 20f,
                        position = Position.BottomRight,
                        text = "Stamp"
                    )
                }
            }
        }

        data class Time(
            override val position: Position,
            override val padding: Float,
            override val params: TextParams,
            val format: String,
        ) : Stamp {
            companion object {
                val Default by lazy {
                    Time(
                        params = TextParams.Default.copy(size = 0.2f),
                        padding = 20f,
                        position = Position.BottomRight,
                        format = "dd/MM/yyyy HH:mm"
                    )
                }
            }
        }
    }

    companion object {
        val entries by lazy {
            listOf(
                Text.Default,
                Image.Default,
                Stamp.Text.Default,
                Stamp.Time.Default
            )
        }
    }
}

fun WatermarkingType.Stamp.copy(
    position: Position = this.position,
    padding: Float = this.padding,
    params: TextParams = this.params,
): WatermarkingType.Stamp = when (this) {
    is WatermarkingType.Stamp.Text -> {
        copy(
            position = position,
            padding = padding,
            params = params,
            text = this.text
        )
    }

    is WatermarkingType.Stamp.Time -> {
        copy(
            position = position,
            padding = padding,
            params = params,
            format = this.format
        )
    }
}

data class TextParams(
    val color: Int,
    val size: Float,
    val font: FontType?,
    val backgroundColor: Int,
) {
    companion object {
        val Default by lazy {
            TextParams(
                color = -16777216,
                size = 0.1f,
                font = null,
                backgroundColor = 0,
            )
        }
    }
}

data class DigitalParams(
    val isInvisible: Boolean,
    val isLSB: Boolean
) {
    companion object {
        val Default by lazy {
            DigitalParams(
                isInvisible = false,
                isLSB = true
            )
        }
    }
}

fun WatermarkingType.isStamp() = this is WatermarkingType.Stamp

fun WatermarkingType.digitalParams(): DigitalParams? = when (this) {
    is WatermarkingType.Image -> digitalParams
    is WatermarkingType.Text -> digitalParams
    else -> null
}

fun WatermarkParams.copy(
    digitalParams: DigitalParams? = this.watermarkingType.digitalParams()
): WatermarkParams = when (watermarkingType) {
    is WatermarkingType.Image -> copy(
        watermarkingType = watermarkingType.copy(
            digitalParams = digitalParams ?: watermarkingType.digitalParams
        )
    )

    is WatermarkingType.Text -> copy(
        watermarkingType = watermarkingType.copy(
            digitalParams = digitalParams ?: watermarkingType.digitalParams
        )
    )

    else -> this
}