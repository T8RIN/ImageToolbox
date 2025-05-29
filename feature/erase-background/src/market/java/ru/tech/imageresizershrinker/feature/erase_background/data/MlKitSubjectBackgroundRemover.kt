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

package com.t8rin.imagetoolbox.feature.erase_background.data

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions

@RequiresApi(api = Build.VERSION_CODES.N)
internal object MlKitSubjectBackgroundRemover {

    private val segment: SubjectSegmenter

    init {
        val segmentOptions = SubjectSegmenterOptions.Builder()
            .enableForegroundBitmap()
            .build()
        segment = SubjectSegmentation.getClient(segmentOptions)
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
        segment.process(input)
            .addOnSuccessListener {
                onFinish(Result.success(it?.foregroundBitmap ?: bitmap))
            }
            .addOnFailureListener { e ->
                onFinish(Result.failure(e))
            }
    }

}