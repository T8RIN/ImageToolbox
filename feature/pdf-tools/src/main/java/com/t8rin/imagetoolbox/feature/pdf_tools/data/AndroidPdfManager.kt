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

package com.t8rin.imagetoolbox.feature.pdf_tools.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.size.Size
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import com.t8rin.imagetoolbox.core.data.utils.getSuitableConfig
import com.t8rin.imagetoolbox.core.data.utils.openFileDescriptor
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.dispatchers.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt


internal class AndroidPdfManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, PdfManager<Bitmap> {

    private val pagesCache = hashMapOf<String, List<IntegerSize>>()

    override suspend fun convertImagesToPdf(
        imageUris: List<String>,
        onProgressChange: suspend (Int) -> Unit,
        scaleSmallImagesToLarge: Boolean,
        preset: Preset.Percentage,
        tempFilename: String
    ): String = withContext(encodingDispatcher) {
        val pdfDocument = PdfDocument()

        val (size, images) = calculateCombinedImageDimensionsAndBitmaps(
            imageUris = imageUris,
            scaleSmallImagesToLarge = scaleSmallImagesToLarge,
            isHorizontal = false,
            imageSpacing = 0,
            percent = preset.value
        )

        val bitmaps = images.map { image ->
            if (scaleSmallImagesToLarge && image.shouldUpscale(false, size)) {
                image.upscale(false, size)
            } else image
        }

        bitmaps.forEachIndexed { index, imageBitmap ->
            val pageInfo = PdfDocument.PageInfo.Builder(
                imageBitmap.width,
                imageBitmap.height,
                index
            ).create()
            val page = pdfDocument.startPage(pageInfo)
            page.canvas.drawBitmap(imageBitmap)
            pdfDocument.finishPage(page)
            delay(10L)
            onProgressChange(index)
        }

        shareProvider.cacheData(
            writeData = {
                pdfDocument.writeTo(it.outputStream())
            },
            filename = tempFilename
        ) ?: throw IllegalAccessException("No PDF created")
    }

    override suspend fun convertPdfToImages(
        pdfUri: String,
        password: String?,
        onFailure: (Throwable) -> Unit,
        pages: List<Int>?,
        preset: Preset.Percentage,
        onGetPagesCount: suspend (Int) -> Unit,
        onProgressChange: suspend (Int, Bitmap) -> Unit,
        onComplete: suspend () -> Unit
    ): Unit = withContext(ioDispatcher) {
        context.openFileDescriptor(pdfUri.toUri())?.use { fileDescriptor ->
            withContext(defaultDispatcher) default@{
                val pdfRenderer = fileDescriptor.createPdfRenderer(
                    password = password,
                    onFailure = onFailure,
                    onPasswordRequest = null
                ) ?: return@default onFailure(NullPointerException("File cannot be read"))

                onGetPagesCount(pages?.size ?: pdfRenderer.pageCount)

                for (pageIndex in 0 until pdfRenderer.pageCount) {
                    if (pages == null || pages.contains(pageIndex)) {
                        val bitmap: Bitmap
                        pdfRenderer.openPage(pageIndex).use { page ->
                            bitmap = imageScaler.scaleUntilCanShow(
                                createBitmap(
                                    (page.width * (preset.value / 100f)).roundToInt(),
                                    (page.height * (preset.value / 100f)).roundToInt()
                                )
                            )!!
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                        }

                        val renderedBitmap = createBitmap(
                            width = bitmap.width,
                            height = bitmap.height,
                            config = getSuitableConfig(bitmap)
                        ).applyCanvas {
                            drawColor(Color.White.toArgb())
                            drawBitmap(bitmap)
                        }

                        onProgressChange(pageIndex, renderedBitmap)
                    }
                }
                onComplete()
                pdfRenderer.close()
            }
        }
    }

    override suspend fun getPdfPages(
        uri: String,
        password: String?
    ): List<Int> = withContext(decodingDispatcher) {
        runCatching {
            context.openFileDescriptor(uri.toUri())?.use { fileDescriptor ->
                val renderer = fileDescriptor.createPdfRenderer(
                    password = password,
                    onFailure = {},
                    onPasswordRequest = null
                )

                List(renderer?.pageCount ?: 0) { it }
            }
        }.getOrNull() ?: emptyList()
    }

    override suspend fun getPdfPageSizes(
        uri: String,
        password: String?
    ): List<IntegerSize> = withContext(decodingDispatcher) {
        pagesCache[uri]?.takeIf { it.isNotEmpty() } ?: runCatching {
            context.openFileDescriptor(uri.toUri())?.use { fileDescriptor ->
                val renderer = fileDescriptor.createPdfRenderer(
                    password = password,
                    onFailure = {},
                    onPasswordRequest = null
                ) ?: return@withContext emptyList()

                List(renderer.pageCount) {
                    val page = renderer.openPage(it)
                    page.run {
                        IntegerSize(width, height)
                    }.also {
                        page.close()
                    }
                }.also {
                    pagesCache[uri] = it
                }
            }
        }.getOrNull() ?: emptyList()
    }

    private suspend fun calculateCombinedImageDimensionsAndBitmaps(
        imageUris: List<String>,
        isHorizontal: Boolean,
        scaleSmallImagesToLarge: Boolean,
        imageSpacing: Int,
        percent: Int
    ): Pair<IntegerSize, List<Bitmap>> = withContext(defaultDispatcher) {
        var w = 0
        var h = 0
        var maxHeight = 0
        var maxWidth = 0
        val drawables = imageUris.mapNotNull { uri ->
            imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .size(Size.ORIGINAL)
                    .build()
            ).image?.toBitmap()?.let {
                imageScaler.scaleImage(
                    image = it,
                    width = (it.width * percent / 100f).roundToInt(),
                    height = (it.height * percent / 100f).roundToInt(),
                    resizeType = ResizeType.Flexible
                )
            }?.apply {
                maxWidth = max(maxWidth, width)
                maxHeight = max(maxHeight, height)
            }
        }

        drawables.forEachIndexed { index, image ->
            val width = image.width
            val height = image.height

            val spacing = if (index != drawables.lastIndex) imageSpacing else 0

            if (scaleSmallImagesToLarge && image.shouldUpscale(
                    isHorizontal = isHorizontal,
                    size = IntegerSize(maxWidth, maxHeight)
                )
            ) {
                val targetHeight: Int
                val targetWidth: Int

                if (isHorizontal) {
                    targetHeight = maxHeight
                    targetWidth = (targetHeight * image.aspectRatio).toInt()
                } else {
                    targetWidth = maxWidth
                    targetHeight = (targetWidth / image.aspectRatio).toInt()
                }
                if (isHorizontal) {
                    w += (targetWidth + spacing).coerceAtLeast(1)
                } else {
                    h += (targetHeight + spacing).coerceAtLeast(1)
                }
            } else {
                if (isHorizontal) {
                    w += (width + spacing).coerceAtLeast(1)
                } else {
                    h += (height + spacing).coerceAtLeast(1)
                }
            }
        }

        if (isHorizontal) {
            h = maxHeight
        } else {
            w = maxWidth
        }

        IntegerSize(w, h) to drawables
    }

    private fun Bitmap.shouldUpscale(
        isHorizontal: Boolean,
        size: IntegerSize
    ): Boolean {
        return if (isHorizontal) this.height != size.height
        else this.width != size.width
    }

    private suspend fun Bitmap.upscale(
        isHorizontal: Boolean,
        size: IntegerSize
    ): Bitmap = withContext(encodingDispatcher) {
        if (isHorizontal) {
            createScaledBitmap(
                width = (size.height * aspectRatio).toInt(),
                height = size.height,
                imageScaleMode = ImageScaleMode.NotPresent
            )
        } else {
            createScaledBitmap(
                width = size.width,
                height = (size.width / aspectRatio).toInt(),
                imageScaleMode = ImageScaleMode.NotPresent
            )
        }
    }

    private suspend fun Bitmap.createScaledBitmap(
        width: Int,
        height: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap = imageScaler.scaleImage(
        image = this,
        width = width,
        height = height,
        imageScaleMode = imageScaleMode
    )

}