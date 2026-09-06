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

package com.t8rin.collages.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.graphics.Color as ComposeColor

@RunWith(AndroidJUnit4::class)
class GradientBackgroundTest {
    @Test
    fun previewMatchesExportAfterResizingAndSwitchingBackToColor() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val layout = FramePhotoLayout(instrumentation.targetContext, emptyList())
            layout.setBackgroundColor(ComposeColor.Green)
            layout.setBackgroundShader { width, height ->
                LinearGradient(
                    0f, 0f, width, height,
                    0x80FF0000.toInt(), 0x800000FF.toInt(), Shader.TileMode.CLAMP
                )
            }
            layout.build(160, 80)
            for ((width, height) in listOf(160 to 80, 80 to 160)) {
                layout.resize(width, height)
                layout.layout(0, 0, width, height)
                val preview = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                layout.draw(Canvas(preview))
                val output = layout.createImage(1f)
                assertTrue(
                    "Preview and export disagree at ${width}x$height",
                    preview.sameAs(output)
                )
                assertEquals(128, Color.alpha(output.getPixel(width / 2, height / 2)))
                val enlarged = layout.createImage(2f)
                assertEquals(width * 2, enlarged.width)
                assertEquals(height * 2, enlarged.height)
                assertEquals(128, Color.alpha(enlarged.getPixel(width, height)))
                assertTrue(Color.red(enlarged.getPixel(0, 0)) > 240)
                assertTrue(Color.blue(enlarged.getPixel(width * 2 - 1, height * 2 - 1)) > 240)
                preview.recycle()
                output.recycle()
                enlarged.recycle()
            }
            layout.setBackgroundShader(null)
            layout.setBackgroundColor(ComposeColor.Yellow)
            val solid = layout.createImage(1f)
            assertEquals(Color.YELLOW, solid.getPixel(0, 0))
            val preview = Bitmap.createBitmap(80, 160, Bitmap.Config.ARGB_8888)
            layout.draw(Canvas(preview))
            assertTrue(
                "Switching to a solid color left a gradient in preview",
                solid.sameAs(preview)
            )
            solid.recycle()
            preview.recycle()
        }
    }
}