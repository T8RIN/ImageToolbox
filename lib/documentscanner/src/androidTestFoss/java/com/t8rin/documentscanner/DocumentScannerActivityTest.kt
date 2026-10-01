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

package com.t8rin.documentscanner

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.IntentFilter
import android.provider.MediaStore
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DocumentScannerActivityTest {

    @Test
    fun successfulCameraResultWithoutImageReturnsError() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val camera = instrumentation.addMonitor(
            IntentFilter(MediaStore.ACTION_IMAGE_CAPTURE),
            Instrumentation.ActivityResult(Activity.RESULT_OK, null),
            true
        )
        try {
            val intent = Intent(
                instrumentation.targetContext,
                DocumentScannerActivity::class.java
            )
            ActivityScenario.launchActivityForResult<DocumentScannerActivity>(intent).use { scanner ->
                val result = scanner.result

                assertEquals(1, camera.hits)
                assertEquals(Activity.RESULT_OK, result.resultCode)
                assertTrue(
                    result.resultData.getStringExtra("error")
                        .orEmpty()
                        .contains("unable to read captured image: Unable to decode image")
                )
                assertNull(result.resultData.getStringArrayListExtra("croppedImageResults"))
            }
        } finally {
            instrumentation.removeMonitor(camera)
        }
    }
}