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

package com.t8rin.imagetoolbox.feature.image_stitch.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.data.image.utils.drawBackground
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.feature.image_stitch.domain.CombiningParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Proxy

@RunWith(AndroidJUnit4::class)
class GradientBackgroundTest {
    @Test
    fun backgroundIsContinuousAcrossTransparentImagesAndGridSpacing() = runBlocking {
        val images = mutableMapOf<String, Bitmap>()
        val uris = List(4) { index ->
            "image-$index".also {
                images[it] = createBitmap(16, 12).apply { setPixel(8, 6, Color.WHITE) }
            }
        }
        val getter = dependency<ImageGetter<Bitmap>> { name, args ->
            check(name == "getImage")
            checkNotNull(images[args[0]])
        }
        val scaler = dependency<ImageScaler<Bitmap>> { name, args ->
            check(name == "scaleImage")
            Bitmap.createScaledBitmap(args[0] as Bitmap, args[1] as Int, args[2] as Int, false)
        }
        val share = dependency<ImageShareProvider<Bitmap>> { name, args ->
            check(name == "cacheImage")
            "cache-${images.size}".also { images[it] = args[0] as Bitmap }
        }
        val settings = dependency<SettingsProvider> { name, _ ->
            check(name == "getSettingsState")
            MutableStateFlow(SettingsState.Default)
        }
        val dispatchers = dependency<DispatchersHolder> { _, _ -> Dispatchers.Unconfined }
        val combiner = AndroidImageCombiner(
            scaler, getter, dependency(), share, dependency(), dependency(),
            CvStitchHelper(getter, scaler), settings, dispatchers
        )
        val palette = GradientPalette.Custom(
            listOf(0x80FF0000.toInt(), 0x000000FF).map(::ColorModel)
        )
        for (mode in listOf(
            StitchMode.Horizontal, StitchMode.Vertical,
            StitchMode.Grid.Horizontal(), StitchMode.Grid.Vertical()
        )) for (gradient in listOf(
            null,
            GradientFill(palette),
            GradientFill(palette, 90f),
            GradientFill(palette, 37f)
        )) {
            val (actual, info) = combiner.combineImages(
                uris,
                CombiningParams(
                    stitchMode = mode,
                    horizontalSpacing = 8,
                    verticalSpacing = 8,
                    outputScale = 1f,
                    backgroundColor = Color.GREEN,
                    backgroundGradient = gradient
                ),
                onProgress = {}
            )
            assertEquals(info.width, actual.width)
            assertEquals(info.height, actual.height)
            val expected = createBitmap(actual.width, actual.height).applyCanvas {
                drawBackground(Color.GREEN, gradient)
            }
            var imagePixels = 0
            for (y in 0 until actual.height) for (x in 0 until actual.width) {
                if (actual.getPixel(x, y) == Color.WHITE) imagePixels++
                else assertEquals(
                    "Background seam for $mode at ($x, $y), angle ${gradient?.angle}",
                    expected.getPixel(x, y), actual.getPixel(x, y)
                )
            }
            assertEquals("An input image disappeared", 4, imagePixels)
            expected.recycle()
            actual.recycle()
        }
        images.values.distinct().filterNot(Bitmap::isRecycled).forEach(Bitmap::recycle)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> dependency(
        crossinline invoke: (String, Array<out Any?>) -> Any? = { name, _ -> error("Unexpected call: $name") }
    ): T = Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, method, args ->
        invoke(method.name, args ?: emptyArray())
    } as T
}