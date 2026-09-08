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

package com.t8rin.imagetoolbox.texture_generation.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.withDefaultsFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class RaymarchTextureGeneratorTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun everySceneRendersGeometryAndPreservesOutputDimensions() {
        for (type in TextureFilterType.entries.filter { it.isRaymarch }) {
            val params = TextureParams.Default.withDefaultsFor(type)
            for ((width, height) in listOf(64 to 48, 33 to 49)) {
                val result = generateRaymarchTexture(width, height, params, context)
                assertEquals(width, result.width)
                assertEquals(height, result.height)
                val pixels = IntArray(width * height)
                result.getPixels(pixels, 0, width, 0, 0, width, height)
                assertTrue("$type must render a surface", pixels.any { Color.alpha(it) == 255 })
                assertTrue("$type must show shading", pixels.toSet().size > 8)
                result.recycle()
            }
            val preview = generateRaymarchTexture(256, 256, params, context)
            File(context.getExternalFilesDir(null), "$type.png").outputStream().use {
                preview.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            preview.recycle()
        }
    }

    @Test
    fun environmentImageChangesReflectionsWithoutChangingTheSilhouette() {
        val params = TextureParams.Default.withDefaultsFor(TextureFilterType.Sphere3D)
        val red = createBitmap(17, 11).apply { eraseColor(Color.RED) }
        val blue = createBitmap(17, 11).apply { eraseColor(Color.BLUE) }
        val first = generateRaymarchTexture(65, 49, params, context, red)
        val second = generateRaymarchTexture(65, 49, params, context, blue)
        assertTrue(!first.sameAs(second))
        for (y in 0 until 49) for (x in 0 until 65) {
            assertEquals(Color.alpha(first.getPixel(x, y)), Color.alpha(second.getPixel(x, y)))
        }
        assertTrue(Color.red(first.getPixel(32, 24)) > Color.blue(first.getPixel(32, 24)))
        assertTrue(Color.blue(second.getPixel(32, 24)) > Color.red(second.getPixel(32, 24)))
        first.recycle()
        second.recycle()
        red.recycle()
        blue.recycle()
    }

    @Test
    fun sceneColorsPreserveAlphaAndCameraChangesProjection() {
        val params = TextureParams.Default.withDefaultsFor(TextureFilterType.Cube3D).copy(
            foregroundColor = ColorModel(Color.argb(128, 180, 160, 140)),
            backgroundColor = ColorModel(Color.argb(64, 40, 60, 80))
        )
        val first = generateRaymarchTexture(65, 49, params, context)
        val repeated = generateRaymarchTexture(65, 49, params, context)
        val changed = generateRaymarchTexture(
            65, 49, params.copy(
                raymarchParams = params.raymarchParams.copy(cameraYaw = 70f)
            ), context
        )
        assertTrue(first.sameAs(repeated))
        assertTrue(!first.sameAs(changed))
        val pixels = IntArray(65 * 49)
        first.getPixels(pixels, 0, 65, 0, 0, 65, 49)
        assertEquals(setOf(64, 128), pixels.map { Color.alpha(it) }.toSet())
        first.recycle()
        repeated.recycle()
        changed.recycle()
    }
}