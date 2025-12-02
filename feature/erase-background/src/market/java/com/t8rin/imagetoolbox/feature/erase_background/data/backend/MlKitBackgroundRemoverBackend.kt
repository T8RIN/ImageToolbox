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

package com.t8rin.imagetoolbox.feature.erase_background.data.backend

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.set
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemoverBackend
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import kotlin.coroutines.resume

internal object MlKitBackgroundRemoverBackend : AutoBackgroundRemoverBackend<Bitmap> {

    override suspend fun performBackgroundRemove(
        image: Bitmap
    ): Result<Bitmap> = suspendCancellableCoroutine { continuation ->
        runCatching {
            autoRemove(
                image = image,
                onFinish = { continuation.resume(it) }
            )
        }.onFailure {
            continuation.resume(Result.failure(it))
        }
    }

    @SuppressLint("NewApi")
    private fun autoRemove(
        type: ApiType = ApiType.Best,
        image: Bitmap,
        onFinish: (Result<Bitmap>) -> Unit
    ) {
        val old = {
            MlKitBackgroundRemover.removeBackground(
                bitmap = image,
                onFinish = onFinish
            )
        }
        val new = {
            MlKitSubjectBackgroundRemover.removeBackground(
                bitmap = image,
                onFinish = {
                    if (it.isFailure) old() else onFinish(it)
                }
            )
        }

        when (type) {
            ApiType.Old -> old()
            ApiType.New -> new()
        }
    }

    private enum class ApiType {
        Old, New;

        companion object Companion {
            val Best: ApiType get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) New else Old
        }
    }

}

private object MlKitBackgroundRemover {

    private var segment: Segmenter? = null
    private var buffer = ByteBuffer.allocate(0)
    private var width = 0
    private var height = 0


    init {
        runCatching {
            val segmentOptions = SelfieSegmenterOptions.Builder()
                .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
                .build()
            segment = Segmentation.getClient(segmentOptions)
        }
    }


    /**
     * Process the image to get buffer and image height and width
     * @param bitmap Bitmap which you want to remove background.
     * @param onFinish listener for success and failure callback.
     **/
    fun removeBackground(
        bitmap: Bitmap,
        onFinish: (Result<Bitmap>) -> Unit
    ) {
        //Generate a copy of bitmap just in case the if the bitmap is immutable.
        val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val input = InputImage.fromBitmap(copyBitmap, 0)
        val segmenter = segment ?: return onFinish(Result.failure(NoClassDefFoundError()))

        segmenter.process(input)
            .addOnSuccessListener { segmentationMask ->
                buffer = segmentationMask.buffer
                width = segmentationMask.width
                height = segmentationMask.height

                val resultBitmap = removeBackgroundFromImage(copyBitmap)
                onFinish(Result.success(resultBitmap))
            }
            .addOnFailureListener { e ->
                onFinish(Result.failure(e))
            }
    }


    /**
     * Change the background pixels color to transparent.
     * */
    private fun removeBackgroundFromImage(
        image: Bitmap
    ): Bitmap {
        image.setHasAlpha(true)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val bgConfidence = ((1.0 - buffer.float) * 255).toInt()
                if (bgConfidence >= 100) {
                    image[x, y] = 0
                }
            }
        }
        buffer.rewind()

        return image
    }

}

@RequiresApi(api = Build.VERSION_CODES.N)
private object MlKitSubjectBackgroundRemover {

    private var segment: SubjectSegmenter? = null

    init {
        runCatching {
            val segmentOptions = SubjectSegmenterOptions.Builder()
                .enableForegroundBitmap()
                .build()
            segment = SubjectSegmentation.getClient(segmentOptions)
        }
    }


    /**
     * Process the image to get buffer and image height and width
     * @param bitmap Bitmap which you want to remove background.
     * @param onFinish listener for success and failure callback.
     **/
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun removeBackground(
        bitmap: Bitmap,
        onFinish: (Result<Bitmap>) -> Unit
    ) {
        //Generate a copy of bitmap just in case the if the bitmap is immutable.
        val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val input = InputImage.fromBitmap(copyBitmap, 0)
        val segmenter = segment ?: return onFinish(Result.failure(NoClassDefFoundError()))

        segmenter.process(input)
            .addOnSuccessListener {
                onFinish(Result.success(it?.foregroundBitmap ?: bitmap))
            }
            .addOnFailureListener { e ->
                onFinish(Result.failure(e))
            }
    }

}