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

package com.t8rin.opencv_tools.free_corners_crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.PointF
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.core.net.toUri
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.t8rin.exif.ExifInterface
import com.t8rin.opencv_tools.free_corners_crop.model.Quad
import com.t8rin.opencv_tools.free_corners_crop.model.distance
import com.t8rin.opencv_tools.free_corners_crop.model.toOpenCVPoint
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.geometry.Geometry
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import android.graphics.Rect as AndroidRect
import coil3.size.Size as CoilSize

object FreeCrop : OpenCV() {

    suspend fun cropToCache(
        context: Context,
        imageUri: Uri?,
        fallbackBitmap: Bitmap,
        fallbackPoints: List<Offset>,
        sourcePoints: List<Offset>
    ): Uri? = withContext(Dispatchers.Default) {
        runCatching {
            val region = context.loadRegionBitmap(imageUri, sourcePoints)
            if (region != null) {
                try {
                    crop(
                        bitmap = region.bitmap,
                        points = region.points
                    ).cacheAsPng(context)
                } finally {
                    region.bitmap.recycleIfNeeded()
                }
            } else {
                val sourceBitmap = context.loadBitmap(imageUri)
                crop(
                    bitmap = sourceBitmap ?: fallbackBitmap,
                    points = if (sourceBitmap != null) sourcePoints else fallbackPoints
                ).cacheAsPng(context)
            }
        }.getOrNull() ?: runCatching {
            crop(
                bitmap = fallbackBitmap,
                points = fallbackPoints
            ).cacheAsPng(context)
        }.getOrNull()
    }

    suspend fun crop(
        bitmap: Bitmap,
        points: List<Offset>
    ): Bitmap = coroutineScope {
        val corners = Quad(
            topLeftCorner = PointF(points[0].x, points[0].y),
            topRightCorner = PointF(points[1].x, points[1].y),
            bottomRightCorner = PointF(points[2].x, points[2].y),
            bottomLeftCorner = PointF(points[3].x, points[3].y)
        )

        val image = bitmap.toMat()

        // convert top left, top right, bottom right, and bottom left document corners from
        // Android points to OpenCV points
        val tLC = corners.topLeftCorner.toOpenCVPoint()
        val tRC = corners.topRightCorner.toOpenCVPoint()
        val bRC = corners.bottomRightCorner.toOpenCVPoint()
        val bLC = corners.bottomLeftCorner.toOpenCVPoint()

        val width = min(tLC.distance(tRC), bLC.distance(bRC))
        val height = min(tLC.distance(bLC), tRC.distance(bRC))

        // create empty image matrix with cropped and warped document width and height
        val croppedImage = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(width, 0.0),
            Point(width, height),
            Point(0.0, height),
        )

        val output = Mat()
        Imgproc.warpPerspective(
            image,
            output,
            Geometry.getPerspectiveTransform(
                MatOfPoint2f(tLC, tRC, bRC, bLC),
                croppedImage
            ),
            Size(width, height)
        )

        output.toBitmap()
    }

}

private data class RegionBitmap(
    val bitmap: Bitmap,
    val points: List<Offset>
)

@Suppress("DEPRECATION")
private fun Context.loadRegionBitmap(
    uri: Uri?,
    points: List<Offset>
): RegionBitmap? {
    uri ?: return null
    if (!hasNormalExifOrientation(uri)) return null

    return runCatching {
        contentResolver.openInputStream(uri)?.use { input ->
            val decoder = BitmapRegionDecoder.newInstance(input, false) ?: return null
            try {
                val rect = points.toBoundingRect(decoder.width, decoder.height)
                    ?: return null
                val bitmap = decoder.decodeRegion(
                    rect,
                    BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                ) ?: return null

                RegionBitmap(
                    bitmap = bitmap,
                    points = points.map { point ->
                        Offset(
                            x = point.x - rect.left,
                            y = point.y - rect.top
                        )
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

private fun List<Offset>.toBoundingRect(
    width: Int,
    height: Int
): AndroidRect? {
    if (isEmpty() || width <= 0 || height <= 0) return null

    val left = floor(minOf { it.x }).toInt()
        .coerceIn(0, (width - 1).coerceAtLeast(0))
    val top = floor(minOf { it.y }).toInt()
        .coerceIn(0, (height - 1).coerceAtLeast(0))
    val right = ceil(maxOf { it.x }).toInt()
        .coerceIn(left + 1, width)
    val bottom = ceil(maxOf { it.y }).toInt()
        .coerceIn(top + 1, height)

    return AndroidRect(left, top, right, bottom)
}

private fun Bitmap.recycleIfNeeded() {
    if (!isRecycled) recycle()
}

private fun Bitmap.cacheAsPng(context: Context): Uri {
    val file = File(
        File(context.cacheDir, "temp").apply(File::mkdirs),
        "temp_free_crop_${System.currentTimeMillis()}.png"
    )

    FileOutputStream(file).use { output ->
        check(compress(Bitmap.CompressFormat.PNG, 100, output))
    }

    return file.toUri()
}