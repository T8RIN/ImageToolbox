/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.data.image.utils.compressor

import android.content.Context
import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.data.image.utils.ImageCompressorBackend
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import org.beyka.tiffbitmapfactory.CompressionScheme
import org.beyka.tiffbitmapfactory.Orientation
import org.beyka.tiffbitmapfactory.TiffSaver
import java.io.File

internal data class TiffBackend(
    private val context: Context
) : ImageCompressorBackend {

    override suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray {
        val tiffQuality = quality as? Quality.Tiff ?: Quality.Tiff()

        val file = File(context.cacheDir, "temp.tiff").apply { createNewFile() }
        val options = TiffSaver.SaveOptions().apply {
            compressionScheme = CompressionScheme.entries[tiffQuality.compressionScheme]
            orientation = Orientation.TOP_LEFT
        }
        TiffSaver.saveBitmap(file, image, options)

        return file.readBytes().also {
            file.delete()
        }
    }
}