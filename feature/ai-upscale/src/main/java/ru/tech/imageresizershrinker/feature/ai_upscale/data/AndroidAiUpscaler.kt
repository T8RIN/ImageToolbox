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

package ru.tech.imageresizershrinker.feature.ai_upscale.data

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.extensions.OrtxPackage
import android.content.Context
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.feature.ai_upscale.domain.AiUpscaler
import java.nio.FloatBuffer
import javax.inject.Inject

internal class AndroidAiUpscaler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    dispatchersHolder: DispatchersHolder,
) : AiUpscaler<Bitmap>, DispatchersHolder by dispatchersHolder {

    private val env = OrtEnvironment.getEnvironment()

    override suspend fun upscale(
        image: Bitmap,
        upscaleModelUri: String
    ): Result<Bitmap> = upscaleImpl(
        image = image,
        upscaleModelUri = upscaleModelUri
    )

    override suspend fun upscale(
        imageUri: String,
        upscaleModelUri: String
    ): Result<Bitmap> {
        return upscale(
            image = imageGetter.getImage(data = imageUri) ?: return Result.failure(
                NullPointerException()
            ),
            upscaleModelUri = upscaleModelUri
        )
    }

    private suspend fun upscaleImpl(
        image: Bitmap,
        upscaleModelUri: String
    ): Result<Bitmap> = try {
        val sessionOptions = OrtSession.SessionOptions().apply {
            registerCustomOpLibrary(OrtxPackage.getLibraryPath())
        }

        var resultImage: Bitmap? = null

        env.createSession(
            fileController.readBytes(upscaleModelUri),
            sessionOptions
        ).use { session ->
            upscale(
                image = image,
                env = env,
                session = session
            )?.let {
                resultImage = imageGetter.getImage(it)
            }
        }

        resultImage?.let { Result.success(it) } ?: Result.failure(NullPointerException())
    } catch (t: Throwable) {
        Result.failure(t)
    }

    private suspend fun upscale(
        image: Bitmap,
        env: OrtEnvironment,
        session: OrtSession
    ): Any? = withContext(decodingDispatcher) {
        val inputName = "onnx::Conv_0"
        val shape = longArrayOf(1, 3, 256, 256)
        val image = Bitmap.createScaledBitmap(image, 256, 256, true)

        val inputData = FloatArray(1 * 3 * image.width * image.height)
        val pixels = IntArray(image.width * image.height)
        image.getPixels(pixels, 0, image.width, 0, 0, image.width, image.height)

        for (i in 0 until image.width * image.height) {
            val pixel = pixels[i]
            val r = (pixel shr 16 and 0xFF) / 255f
            val g = (pixel shr 8 and 0xFF) / 255f
            val b = (pixel and 0xFF) / 255f

            inputData[i] = r
            inputData[i + image.width * image.height] = g
            inputData[i + 2 * image.width * image.height] = b
        }

        val inputTensor = OnnxTensor.createTensor(
            env,
            FloatBuffer.wrap(inputData),
            shape
        )

        inputTensor.use {
            val output = session.run(
                mapOf(
                    inputName to inputTensor
                )
            )

            output.use {
                return@withContext output?.get(0)?.value
            }
        }
    }

}