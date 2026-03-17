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

@file:Suppress("UNCHECKED_CAST")

package com.t8rin.neural_tools.bgremover

import android.graphics.Bitmap
import com.t8rin.neural_tools.DownloadProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import java.io.FileOutputStream

internal object U2NetPortableBackgroundRemover : GenericBackgroundRemover(
    path = "onnx/bgremove/u2netp.onnx",
    trainedSize = 320
) {

    init {
        extract()
    }

    override fun startDownload(forced: Boolean): Flow<DownloadProgress> = callbackFlow {
        extract()
        close()
    }

    override fun removeBackground(image: Bitmap, modelPath: String, trainedSize: Int?): Bitmap? {
        extract()
        return super.removeBackground(
            image = image,
            modelPath = modelPath,
            trainedSize = trainedSize
        )
    }

    override fun checkModel(): Boolean {
        extract()
        return super.checkModel()
    }

    private fun extract() {
        if (!modelFile.exists() || modelFile.length() <= 0) {
            context.assets.open("u2netp.onnx").use { input ->
                FileOutputStream(modelFile).use { output -> input.copyTo(output) }
            }
        }
        _isDownloaded.update { true }
    }
}
