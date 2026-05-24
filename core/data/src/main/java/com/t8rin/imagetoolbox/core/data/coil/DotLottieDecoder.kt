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

package com.t8rin.imagetoolbox.core.data.coil

import coil3.ImageLoader
import coil3.asImage
import coil3.decode.AssetMetadata
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.pxOrElse
import com.dotlottie.dlplayer.Config
import com.dotlottie.dlplayer.Mode
import com.dotlottie.dlplayer.createDefaultLayout
import com.lottiefiles.dotlottie.core.drawable.DotLottieDrawable
import com.lottiefiles.dotlottie.core.util.DotLottieContent

internal class DotLottieDecoder(
    private val source: ImageSource,
    private val options: Options
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val size = options.size
        val width = size.width.pxOrElse { DEFAULT_SIZE }
        val height = size.height.pxOrElse { DEFAULT_SIZE }

        val drawable = DotLottieDrawable(
            animationData = DotLottieContent.Binary(source.source().readByteArray()),
            width = width,
            height = height,
            dotLottieEventListener = mutableListOf(),
            config = Config(
                autoplay = true,
                loopAnimation = true,
                mode = Mode.FORWARD,
                speed = 1f,
                useFrameInterpolation = true,
                segment = emptyList(),
                backgroundColor = 0u,
                marker = "",
                layout = createDefaultLayout(),
                themeId = "",
                stateMachineId = "",
                animationId = "",
                loopCount = 0u
            )
        )

        return DecodeResult(
            image = drawable.asImage(),
            isSampled = false
        )
    }

    class Factory : Decoder.Factory {

        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? = if (isApplicable(result)) {
            DotLottieDecoder(
                source = result.source,
                options = options
            )
        } else null

        private fun isApplicable(result: SourceFetchResult): Boolean {
            val assetPath = (result.source.metadata as? AssetMetadata)?.filePath
            val filePath = result.source.fileOrNull()?.toString()

            return assetPath?.endsWith(".lottie") == true ||
                    filePath?.endsWith(".lottie") == true ||
                    result.mimeType == "application/dotlottie"
        }
    }

    private companion object {
        private const val DEFAULT_SIZE = 84
    }
}
