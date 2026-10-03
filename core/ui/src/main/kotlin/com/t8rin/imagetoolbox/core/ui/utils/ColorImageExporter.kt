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

package com.t8rin.imagetoolbox.core.ui.utils

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import com.t8rin.colors.parser.ColorNameParser
import com.t8rin.colors.util.ColorUtil.hex
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.FileSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.utils.appContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ColorImageExporter @Inject constructor(
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    suspend fun save(
        color: Color,
        imageInfo: ImageInfo,
        oneTimeSaveLocationUri: String?
    ): SaveResult = withContext(defaultDispatcher) {
        require(imageInfo.width in 1..MAX_SIZE && imageInfo.height in 1..MAX_SIZE)

        val bitmap = createBitmap(imageInfo.width, imageInfo.height).apply {
            eraseColor(color.toArgb())
        }

        try {
            val data = imageCompressor.compressAndTransform(
                image = bitmap,
                imageInfo = imageInfo,
                applyImageTransformations = false
            )
            ensureActive()

            fileController.save(
                saveTarget = FileSaveTarget(
                    filename = "${ColorNameParser.parseColorName(color)}(${color.hex()}).png",
                    imageFormat = imageInfo.imageFormat,
                    originalUri = "",
                    data = data
                ),
                keepOriginalMetadata = false,
                oneTimeSaveLocationUri = oneTimeSaveLocationUri
            )
        } finally {
            bitmap.recycle()
        }
    }

    companion object {
        const val MAX_SIZE = 4096
    }

}

@Composable
internal fun rememberColorImageExporter(): ColorImageExporter = remember {
    appContext.entryPoint<ColorImageExportEntryPoint>().colorImageExporter
}

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface ColorImageExportEntryPoint {
    val colorImageExporter: ColorImageExporter
}