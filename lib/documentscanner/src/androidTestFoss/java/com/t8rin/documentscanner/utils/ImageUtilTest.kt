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

package com.t8rin.documentscanner.utils

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import java.io.File

@RunWith(AndroidJUnit4::class)
class ImageUtilTest {

    @Before
    fun initOpenCV() {
        assertTrue(OpenCVLoader.initLocal())
    }

    @Test
    fun emptyCameraOutputReportsDecodeError() = withImageFile { file ->
        assertDecodeError(file)
    }

    @Test
    fun corruptCameraOutputReportsDecodeError() = withImageFile { file ->
        file.writeText("Invalid image")
        assertDecodeError(file)
    }

    @Test
    fun missingCameraOutputReportsFileError() = withImageFile { file ->
        assertTrue(file.delete())

        val error = assertThrows(Throwable::class.java) {
            ImageUtil().getImageFromFilePath(file.absolutePath)
        }

        assertEquals("File doesn't exist - ${file.absolutePath}", error.message)
    }

    @Test
    fun validCameraOutputKeepsImageDimensions() = withImageFile { file ->
        val photo = Bitmap.createBitmap(320, 240, Bitmap.Config.ARGB_8888)
        try {
            file.outputStream().use { stream ->
                assertTrue(photo.compress(Bitmap.CompressFormat.JPEG, 100, stream))
            }
        } finally {
            photo.recycle()
        }

        val result = ImageUtil().getImageFromFilePath(file.absolutePath)
        try {
            assertEquals(320, result.width)
            assertEquals(240, result.height)
        } finally {
            result.recycle()
        }
    }

    private fun assertDecodeError(file: File) {
        val error = assertThrows(Throwable::class.java) {
            ImageUtil().getImageFromFilePath(file.absolutePath)
        }

        assertEquals("Unable to decode image - ${file.absolutePath}", error.message)
    }

    private fun withImageFile(block: (File) -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File.createTempFile("document_scan_test", ".jpg", context.cacheDir)
        try {
            block(file)
        } finally {
            file.delete()
        }
    }
}