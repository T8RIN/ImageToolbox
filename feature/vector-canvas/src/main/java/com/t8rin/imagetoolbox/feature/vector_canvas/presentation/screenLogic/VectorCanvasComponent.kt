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

@file:Suppress("PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.FileSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasImage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class VectorCanvasComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _isBusy = mutableStateOf(false)
    val isBusy by _isBusy
    val pngFormat: ImageFormat = PngFormat

    private var activeJob: Job? by smartJob {
        _isBusy.update { false }
    }

    fun registerCanvasChange() = registerChanges()

    fun savePng(
        imageBitmap: ImageBitmap,
        oneTimeSaveLocationUri: String?
    ) = launchTask {
        val bitmap = imageBitmap.asAndroidBitmap()
        val imageInfo = ImageInfo(
            width = bitmap.width,
            height = bitmap.height,
            imageFormat = PngFormat
        )
        val result = fileController.save(
            saveTarget = ImageSaveTarget(
                imageInfo = imageInfo,
                originalUri = "",
                sequenceNumber = null,
                filename = exportFilename(PngExtension),
                data = imageCompressor.compressAndTransform(
                    image = bitmap,
                    imageInfo = imageInfo,
                    applyImageTransformations = false
                )
            ),
            keepOriginalMetadata = false,
            oneTimeSaveLocationUri = oneTimeSaveLocationUri
        )

        parseSaveResult(result.onSuccess(::registerSave))
    }

    fun saveSvg(svg: String) = saveDocument(
        data = svg,
        extension = SvgExtension,
        mimeType = MimeType.Svg
    )

    fun saveJson(json: String) = saveDocument(
        data = json,
        extension = JsonExtension,
        mimeType = MimeType.Json
    )

    fun importJson(
        uri: Uri,
        onLoaded: (String) -> Unit
    ) = launchTask {
        runCatching {
            fileController.readBytes(uri.toString()).decodeToString()
        }.onSuccess(onLoaded).onFailure(AppToastHost::showFailureToast)
    }

    fun loadImage(
        uri: Uri,
        onLoaded: (VectorCanvasImage) -> Unit
    ) = launchTask {
        runCatching {
            val uriString = uri.toString()
            val bitmap = requireNotNull(imageGetter.getImage(uriString)?.image)
            VectorCanvasImage(
                bytes = fileController.readBytes(uriString),
                width = bitmap.width,
                height = bitmap.height
            )
        }.onSuccess(onLoaded).onFailure(AppToastHost::showFailureToast)
    }

    fun cancel() {
        activeJob?.cancel()
        activeJob = null
        _isBusy.update { false }
    }

    private fun saveDocument(
        data: String,
        extension: String,
        mimeType: MimeType.Single
    ) = launchTask {
        val result = fileController.save(
            saveTarget = FileSaveTarget(
                originalUri = "",
                filename = exportFilename(extension),
                data = data.encodeToByteArray(),
                mimeType = mimeType,
                extension = extension
            ),
            keepOriginalMetadata = false
        )

        parseFileSaveResult(result.onSuccess(::registerSave))
    }

    private fun launchTask(block: suspend () -> Unit) {
        activeJob = trackProgress {
            _isBusy.update { true }
            try {
                block()
            } finally {
                _isBusy.update { false }
            }
        }
    }

    private fun exportFilename(extension: String): String =
        "VectorCanvas-${System.currentTimeMillis()}.$extension"

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit
        ): VectorCanvasComponent
    }

    private companion object {
        val PngFormat = ImageFormat.Png.Lossless
        const val PngExtension = "png"
        const val SvgExtension = "svg"
        const val JsonExtension = "json"
    }
}
