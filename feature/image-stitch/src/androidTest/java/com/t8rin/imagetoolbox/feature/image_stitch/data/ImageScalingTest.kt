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
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
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
class ImageScalingTest {
    @Test
    fun smallerImageKeepsItsDetailWhenOutputIsScaledDown() = runBlocking {
        val large = createBitmap(40, 20).apply { eraseColor(Color.RED) }
        val small = createBitmap(20, 10).apply {
            for (y in 0 until height) for (x in 0 until width) {
                setPixel(x, y, if ((x + y) % 2 == 0) Color.WHITE else Color.BLACK)
            }
        }
        val images = mapOf("large" to large, "small" to small)
        val getter = dependency<ImageGetter<Bitmap>> { name, args ->
            check(name == "getImage")
            images[args[0]]
        }
        val scaler = dependency<ImageScaler<Bitmap>> { name, args ->
            check(name == "scaleImage")
            Bitmap.createScaledBitmap(args[0] as Bitmap, args[1] as Int, args[2] as Int, false)
        }
        val combiner = combiner(getter, scaler)

        for (mode in listOf(StitchMode.Vertical, StitchMode.Horizontal)) {
            val (result, info) = combiner.combineImages(
                imageUris = listOf("large", "small"),
                combiningParams = CombiningParams(
                    stitchMode = mode,
                    scaleSmallImagesToLarge = true,
                    outputScale = 0.5f
                ),
                onProgress = {}
            )

            assertEquals(if (mode == StitchMode.Vertical) 20 else 40, info.width)
            assertEquals(if (mode == StitchMode.Vertical) 20 else 10, info.height)
            for (y in 0 until small.height) for (x in 0 until small.width) {
                val resultX = x + if (mode == StitchMode.Horizontal) 20 else 0
                val resultY = y + if (mode == StitchMode.Vertical) 10 else 0
                assertEquals(
                    "Pixel ($x, $y) was resampled in $mode",
                    small.getPixel(x, y), result.getPixel(resultX, resultY)
                )
            }
            result.recycle()
        }

        val (withoutUpscaling, info) = combiner.combineImages(
            imageUris = listOf("large", "small"),
            combiningParams = CombiningParams(
                stitchMode = StitchMode.Vertical,
                scaleSmallImagesToLarge = false,
                outputScale = 0.5f
            ),
            onProgress = {}
        )
        assertEquals(20, info.width)
        assertEquals(15, info.height)
        withoutUpscaling.recycle()
        large.recycle()
        small.recycle()
    }

    @Test
    fun manyLargeImagesDoNotStayDecodedAtOnce() = runBlocking {
        val getter = dependency<ImageGetter<Bitmap>> { name, _ ->
            check(name == "getImage")
            createBitmap(2048, 1536)
        }
        val scaler = dependency<ImageScaler<Bitmap>> { name, args ->
            check(name == "scaleImage")
            Bitmap.createScaledBitmap(args[0] as Bitmap, args[1] as Int, args[2] as Int, false)
        }
        val (result, info) = combiner(getter, scaler).combineImages(
            imageUris = List(48) { "image-$it" },
            combiningParams = CombiningParams(
                stitchMode = StitchMode.Vertical,
                scaleSmallImagesToLarge = true,
                outputScale = 0.25f
            ),
            onProgress = {}
        )

        assertEquals(512, info.width)
        assertEquals(18432, info.height)
        result.recycle()
    }

    private fun combiner(
        getter: ImageGetter<Bitmap>,
        scaler: ImageScaler<Bitmap>
    ): AndroidImageCombiner {
        val settings = dependency<SettingsProvider> { name, _ ->
            check(name == "getSettingsState")
            MutableStateFlow(SettingsState.Default)
        }
        val dispatchers = dependency<DispatchersHolder> { _, _ -> Dispatchers.Unconfined }
        return AndroidImageCombiner(
            imageScaler = scaler,
            imageGetter = getter,
            imageTransformer = dependency(),
            shareProvider = dependency(),
            filterProvider = dependency(),
            imagePreviewCreator = dependency(),
            cvStitchHelper = dependency(),
            settingsProvider = settings,
            dispatchersHolder = dispatchers
        )
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
