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

package com.t8rin.imagetoolbox.core.data.image

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.gif.repeatCount
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Precision
import coil3.size.Size
import coil3.toBitmap
import com.awxkee.jxlcoder.coil.enableJxlAnimation
import com.github.awxkee.avifcoil.decoder.animation.enableAvifAnimation
import com.t8rin.imagetoolbox.core.data.coil.UpscaleSvgDecoder
import com.t8rin.imagetoolbox.core.data.utils.openFileDescriptor
import com.t8rin.imagetoolbox.core.data.utils.toCoil
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.tryRequireOriginal
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

internal class AndroidImageGetter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val appScope: AppScope,
    settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder,
) : DispatchersHolder by dispatchersHolder, ImageGetter<Bitmap> {

    private val _settingsState = settingsProvider.settingsState

    private val settingsState get() = _settingsState.value

    override suspend fun getImage(
        uri: String,
        originalSize: Boolean,
        onFailure: (Throwable) -> Unit
    ): ImageData<Bitmap>? = withContext(defaultDispatcher) {
        getImageImpl(
            data = uri,
            size = null,
            addSizeToRequest = originalSize,
            onFailure = onFailure
        )?.let { bitmap ->
            val newUri = uri.toUri().tryRequireOriginal(context)
            context.openFileDescriptor(newUri).use {
                ImageData(
                    image = bitmap,
                    imageInfo = ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        imageFormat = settingsState.defaultImageFormat
                            ?: ImageFormat[getExtension(uri)],
                        originalUri = uri,
                        resizeType = settingsState.defaultResizeType
                    ),
                    metadata = it?.fileDescriptor?.toMetadata()
                )
            }
        }
    }

    override suspend fun getImage(
        data: Any,
        originalSize: Boolean
    ): Bitmap? = getImageImpl(
        data = data,
        size = null,
        addSizeToRequest = originalSize
    )

    override suspend fun getImage(
        data: Any,
        size: IntegerSize?
    ): Bitmap? = getImageImpl(
        data = data,
        size = size
    )

    override suspend fun getImage(
        data: Any,
        size: Int?
    ): Bitmap? = getImageImpl(
        data = data,
        size = size?.let {
            IntegerSize(
                width = it,
                height = it
            )
        },
        precision = Precision.INEXACT
    )

    override suspend fun getImageData(
        uri: String,
        size: Int?,
        onFailure: (Throwable) -> Unit
    ): ImageData<Bitmap>? = withContext(defaultDispatcher) {
        getImageImpl(
            data = uri,
            size = size?.let {
                IntegerSize(
                    width = it,
                    height = it
                )
            },
            precision = Precision.INEXACT,
            onFailure = onFailure
        )?.let { bitmap ->
            val newUri = uri.toUri().tryRequireOriginal(context)
            context.openFileDescriptor(newUri).use {
                ImageData(
                    image = bitmap,
                    imageInfo = ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        imageFormat = settingsState.defaultImageFormat
                            ?: ImageFormat[getExtension(uri)],
                        originalUri = uri,
                        resizeType = settingsState.defaultResizeType
                    ),
                    metadata = it?.fileDescriptor?.toMetadata()
                )
            }
        }
    }

    override suspend fun getImageWithTransformations(
        uri: String,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): ImageData<Bitmap>? = withContext(defaultDispatcher) {
        getImageImpl(
            data = uri,
            transformations = transformations,
            size = null,
            addSizeToRequest = originalSize
        )?.let { bitmap ->
            val newUri = uri.toUri().tryRequireOriginal(context)
            context.openFileDescriptor(newUri).use {
                ImageData(
                    image = bitmap,
                    imageInfo = ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        imageFormat = ImageFormat[getExtension(uri)],
                        originalUri = uri,
                        resizeType = settingsState.defaultResizeType
                    ),
                    metadata = it?.fileDescriptor?.toMetadata()
                )
            }
        }
    }

    override suspend fun getImageWithTransformations(
        data: Any,
        transformations: List<Transformation<Bitmap>>,
        size: IntegerSize?
    ): Bitmap? = getImageImpl(
        data = data,
        transformations = transformations,
        size = size
    )

    override fun getImageAsync(
        uri: String,
        originalSize: Boolean,
        onGetImage: (ImageData<Bitmap>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        appScope.launch {
            getImage(
                uri = uri,
                originalSize = originalSize,
                onFailure = onFailure
            )?.let(onGetImage)
        }
    }

    override fun getExtension(uri: String): String? {
        val filename = uri.toUri().filename(context) ?: ""
        if (filename.endsWith(".qoi")) return "qoi"
        if (filename.endsWith(".jxl")) return "jxl"
        return if (ContentResolver.SCHEME_CONTENT == uri.toUri().scheme) {
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(
                    context.contentResolver.getType(uri.toUri())
                )
        } else {
            MimeTypeMap.getFileExtensionFromUrl(uri).lowercase(Locale.getDefault())
        }
    }

    private suspend fun getImageImpl(
        data: Any,
        size: IntegerSize?,
        precision: Precision = Precision.EXACT,
        transformations: List<Transformation<Bitmap>> = emptyList(),
        onFailure: (Throwable) -> Unit = {},
        addSizeToRequest: Boolean = true
    ): Bitmap? = withContext(defaultDispatcher) {
        if ((size == null || !addSizeToRequest) && data is Bitmap) return@withContext data

        val request = ImageRequest
            .Builder(context)
            .data(data)
            .repeatCount(0)
            .precision(precision)
            .enableAvifAnimation(false)
            .enableJxlAnimation(false)
            .transformations(
                transformations.map(Transformation<Bitmap>::toCoil)
            )
            .apply {
                if (addSizeToRequest) {
                    size(
                        size?.let {
                            Size(size.width, size.height)
                        } ?: Size.ORIGINAL
                    )
                }
            }
            .decoderFactory(UpscaleSvgDecoder.Factory())
            .build()

        runSuspendCatching {
            imageLoader.execute(request).image?.toBitmap()
        }.onFailure {
            it.makeLog("ImageGetter")
            onFailure(it)
        }.getOrNull()
    }

}