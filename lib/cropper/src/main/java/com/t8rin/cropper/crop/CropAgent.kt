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

package com.t8rin.cropper.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.scale
import androidx.core.net.toUri
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.t8rin.cropper.model.CropImageMask
import com.t8rin.cropper.model.CropOutline
import com.t8rin.cropper.model.CropPath
import com.t8rin.cropper.model.CropShape
import com.t8rin.exif.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.graphics.Rect as AndroidRect
import coil3.size.Size as CoilSize


/**
 * Crops imageBitmap based on path that is passed in [crop] function
 */
internal class CropAgent {

    private val imagePaint = Paint().apply {
        blendMode = BlendMode.SrcIn
    }

    private val paint = Paint()


    fun crop(
        imageBitmap: ImageBitmap,
        cropRect: Rect,
        cropOutline: CropOutline,
        layoutDirection: LayoutDirection,
        density: Density,
    ): ImageBitmap {
        return runCatching {
            val croppedBitmap: Bitmap = Bitmap.createBitmap(
                imageBitmap.asAndroidBitmap(),
                cropRect.left.toInt(),
                cropRect.top.toInt(),
                cropRect.width.toInt(),
                cropRect.height.toInt(),
            )

            val imageToCrop = croppedBitmap
                .copy(Bitmap.Config.ARGB_8888, true)!!
                .asImageBitmap()

            drawCroppedImage(cropOutline, cropRect, layoutDirection, density, imageToCrop)

            imageToCrop
        }.getOrNull() ?: imageBitmap
    }

    suspend fun cropToCache(
        context: Context,
        imageUri: Uri?,
        fallbackImageBitmap: ImageBitmap,
        fallbackCropRect: Rect,
        sourceCropRect: Rect,
        cropOutline: CropOutline,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Uri? = withContext(Dispatchers.Default) {
        runCatching {
            context.loadCroppedBitmap(imageUri, sourceCropRect)?.let { sourceBitmap ->
                crop(
                    imageBitmap = sourceBitmap.asImageBitmap(),
                    cropRect = Rect(
                        left = 0f,
                        top = 0f,
                        right = sourceBitmap.width.toFloat(),
                        bottom = sourceBitmap.height.toFloat()
                    ),
                    cropOutline = cropOutline,
                    layoutDirection = layoutDirection,
                    density = density
                ).asAndroidBitmap().cacheAsPng(context)
            } ?: run {
                val sourceBitmap = context.loadBitmap(imageUri)?.asImageBitmap()
                crop(
                    imageBitmap = sourceBitmap ?: fallbackImageBitmap,
                    cropRect = if (sourceBitmap != null) {
                        sourceCropRect.coerceIn(sourceBitmap.width, sourceBitmap.height)
                    } else {
                        fallbackCropRect.coerceIn(
                            fallbackImageBitmap.width,
                            fallbackImageBitmap.height
                        )
                    },
                    cropOutline = cropOutline,
                    layoutDirection = layoutDirection,
                    density = density
                ).asAndroidBitmap().cacheAsPng(context)
            }
        }.getOrNull() ?: runCatching {
            crop(
                imageBitmap = fallbackImageBitmap,
                cropRect = fallbackCropRect.coerceIn(
                    fallbackImageBitmap.width,
                    fallbackImageBitmap.height
                ),
                cropOutline = cropOutline,
                layoutDirection = layoutDirection,
                density = density
            ).asAndroidBitmap().cacheAsPng(context)
        }.getOrNull()
    }

    private fun drawCroppedImage(
        cropOutline: CropOutline,
        cropRect: Rect,
        layoutDirection: LayoutDirection,
        density: Density,
        imageToCrop: ImageBitmap,
    ) {

        when (cropOutline) {
            is CropShape -> {

                val path = Path().apply {
                    val outline =
                        cropOutline.shape.createOutline(cropRect.size, layoutDirection, density)
                    addOutline(outline)
                }

                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)

                    // Destination
                    drawPath(path, paint)

                    // Source
                    drawImage(
                        image = imageToCrop,
                        topLeftOffset = Offset.Zero,
                        paint = imagePaint
                    )
                    restore()
                }
            }

            is CropPath -> {

                val path = Path().apply {

                    addPath(cropOutline.path)

                    val pathSize = getBounds().size
                    val rectSize = cropRect.size

                    val matrix = android.graphics.Matrix()
                    matrix.postScale(
                        rectSize.width / pathSize.width,
                        cropRect.height / pathSize.height
                    )
                    this.asAndroidPath().transform(matrix)

                    val left = getBounds().left
                    val top = getBounds().top

                    translate(Offset(-left, -top))
                }

                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)

                    // Destination
                    drawPath(path, paint)

                    // Source
                    drawImage(image = imageToCrop, topLeftOffset = Offset.Zero, imagePaint)
                    restore()
                }
            }

            is CropImageMask -> {

                val imageMask = cropOutline.image.asAndroidBitmap()
                    .scale(cropRect.width.toInt(), cropRect.height.toInt()).asImageBitmap()

                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)

                    // Destination
                    drawImage(imageMask, topLeftOffset = Offset.Zero, paint)

                    // Source
                    drawImage(image = imageToCrop, topLeftOffset = Offset.Zero, imagePaint)

                    restore()
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun Context.loadCroppedBitmap(
    uri: Uri?,
    cropRect: Rect
): Bitmap? {
    uri ?: return null
    if (!hasNormalExifOrientation(uri)) return null

    return runCatching {
        contentResolver.openInputStream(uri)?.use { input ->
            val decoder = BitmapRegionDecoder.newInstance(input, false) ?: return null
            try {
                decoder.decodeRegion(
                    cropRect.toAndroidRect(decoder.width, decoder.height),
                    BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                )
            } finally {
                decoder.recycle()
            }
        }
    }.getOrNull()
}

private fun Context.hasNormalExifOrientation(uri: Uri): Boolean {
    return runCatching {
        contentResolver.openInputStream(uri)?.use { input ->
            ExifInterface(input).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } == ExifInterface.ORIENTATION_NORMAL
    }.getOrDefault(true)
}

private suspend fun Context.loadBitmap(uri: Uri?): Bitmap? {
    uri ?: return null

    return imageLoader.execute(
        ImageRequest.Builder(this)
            .data(uri)
            .size(CoilSize.ORIGINAL)
            .allowHardware(false)
            .build()
    ).image?.toBitmap()
}

private fun Bitmap.cacheAsPng(context: Context): Uri {
    val file = File(
        File(context.cacheDir, "temp").apply(File::mkdirs),
        "temp_crop_${System.currentTimeMillis()}.png"
    )

    FileOutputStream(file).use { output ->
        check(compress(Bitmap.CompressFormat.PNG, 100, output))
    }

    return file.toUri()
}

private fun Rect.coerceIn(
    width: Int,
    height: Int
): Rect {
    val left = left.coerceIn(0f, (width - 1).coerceAtLeast(0).toFloat())
    val top = top.coerceIn(0f, (height - 1).coerceAtLeast(0).toFloat())
    val right = right.coerceIn(left + 1f, width.toFloat())
    val bottom = bottom.coerceIn(top + 1f, height.toFloat())

    return Rect(left, top, right, bottom)
}

private fun Rect.toAndroidRect(
    width: Int,
    height: Int
): AndroidRect {
    val safeRect = coerceIn(width, height)

    return AndroidRect(
        safeRect.left.toInt(),
        safeRect.top.toInt(),
        safeRect.right.toInt(),
        safeRect.bottom.toInt()
    )
}
