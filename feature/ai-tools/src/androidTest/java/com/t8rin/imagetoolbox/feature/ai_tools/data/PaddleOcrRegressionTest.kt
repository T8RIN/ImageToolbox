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

package com.t8rin.imagetoolbox.feature.ai_tools.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.neural_tools.ocr.PaddleOCR
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

class PaddleOcrRegressionTest {

    @Test
    fun recognitionSurvivesRepeatedCloseAndReopen() {
        assumeTrue(
            "English OCR model is required",
            PaddleOCR.isModelDownloaded(PaddleOCR.Model.English)
        )
        val image = textImage()
        val rotated = Bitmap.createBitmap(
            image, 0, 0, image.width, image.height,
            Matrix().apply { postRotate(180f) }, true
        )
        try {
            repeat(3) {
                for (input in listOf(image, rotated)) {
                    val result = requireNotNull(PaddleOCR.recognize(input, PaddleOCR.Model.English))
                    assertEquals("Image Toolbox 123", result.text.trim())
                    assertTrue(result.accuracy in 80..100)
                    assertTrue(result.hocr.contains("ocrx_word"))
                }
                PaddleOCR.close()
            }
            image.eraseColor(Color.WHITE)
            assertEquals("", PaddleOCR.recognize(image, PaddleOCR.Model.English)?.text)
        } finally {
            PaddleOCR.close()
            image.recycle()
            rotated.recycle()
        }
    }

    @Test
    fun failedSessionInitializationCanBeRetried() {
        assumeTrue(
            "English OCR model is required",
            PaddleOCR.isModelDownloaded(PaddleOCR.Model.English)
        )
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val classifier = File(context.filesDir, "ai_models/paddleocr/English/cls.onnx")
        val original = classifier.readBytes()
        val image = textImage()
        PaddleOCR.close()
        try {
            classifier.writeBytes(byteArrayOf(0))
            repeat(3) {
                assertTrue(runCatching {
                    PaddleOCR.recognize(
                        image,
                        PaddleOCR.Model.English
                    )
                }.isFailure)
            }
            classifier.writeBytes(original)
            assertEquals(
                "Image Toolbox 123",
                PaddleOCR.recognize(image, PaddleOCR.Model.English)?.text?.trim()
            )
        } finally {
            classifier.writeBytes(original)
            PaddleOCR.close()
            image.recycle()
        }
    }

    private fun textImage() = Bitmap.createBitmap(640, 160, Bitmap.Config.ARGB_8888).apply {
        eraseColor(Color.WHITE)
        Canvas(this).drawText("Image Toolbox 123", 30f, 95f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 48f
        })
    }
}