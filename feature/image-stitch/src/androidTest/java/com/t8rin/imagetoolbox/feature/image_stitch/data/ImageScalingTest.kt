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
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleDirection
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.feature.image_stitch.domain.CombiningParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchMode
import com.t8rin.imagetoolbox.feature.image_stitch.domain.toParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.toSavable
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
                    scaleDirection = ImageScaleDirection.Up,
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
                scaleDirection = ImageScaleDirection.None,
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
                scaleDirection = ImageScaleDirection.Up,
                outputScale = 0.25f
            ),
            onProgress = {}
        )

        assertEquals(512, info.width)
        assertEquals(18432, info.height)
        result.recycle()
    }

    @Test
    fun allDirectionsPreserveAspectRatiosAndMatchCalculatedDimensions() = runBlocking {
        val images = mutableMapOf(
            "large" to createBitmap(40, 20),
            "square" to createBitmap(20, 20),
            "wide" to createBitmap(30, 10)
        )
        val getter = dependency<ImageGetter<Bitmap>> { name, args ->
            check(name == "getImage")
            images[args[0]]
        }
        val scaler = dependency<ImageScaler<Bitmap>> { name, args ->
            check(name == "scaleImage")
            Bitmap.createScaledBitmap(args[0] as Bitmap, args[1] as Int, args[2] as Int, false)
        }
        val shareProvider = dependency<ImageShareProvider<Bitmap>> { name, args ->
            check(name == "cacheImage")
            val uri = "cached-${images.size}"
            images[uri] = args[0] as Bitmap
            uri
        }
        val combiner = combiner(getter, scaler, shareProvider)
        val expectedSizes = mapOf(
            StitchMode.Horizontal to listOf(
                IntegerSize(90, 20),
                IntegerSize(120, 20),
                IntegerSize(60, 10)
            ),
            StitchMode.Vertical to listOf(
                IntegerSize(40, 50),
                IntegerSize(40, 73),
                IntegerSize(20, 36)
            ),
            StitchMode.Grid.Horizontal(2) to listOf(
                IntegerSize(60, 30),
                IntegerSize(60, 40),
                IntegerSize(30, 20)
            ),
            StitchMode.Grid.Vertical(2) to listOf(
                IntegerSize(70, 40),
                IntegerSize(220, 60),
                IntegerSize(36, 10)
            )
        )
        for ((mode, sizes) in expectedSizes) {
            for (direction in ImageScaleDirection.entries) {
                val params = CombiningParams(
                    stitchMode = mode,
                    scaleDirection = direction,
                    outputScale = 1f
                )
                val uris = listOf("large", "square", "wide")
                val expectedSize = sizes[direction.ordinal]
                assertEquals(expectedSize, combiner.calculateCombinedImageDimensions(uris, params))
                val (result, info) = combiner.combineImages(uris, params, {})
                assertEquals("Width in $mode with $direction", expectedSize.width, info.width)
                assertEquals("Height in $mode with $direction", expectedSize.height, info.height)
                result.recycle()
            }
        }
        images.values.forEach(Bitmap::recycle)
    }

    @Test
    fun scaleDirectionRestoresOldSettingsAndSurvivesSaving() {
        for (direction in ImageScaleDirection.entries) {
            val params = CombiningParams(scaleDirection = direction)
            assertEquals(params, params.toSavable().toParams())
        }
        val legacy = CombiningParams().toSavable().copy(scaleDirection = null)
        assertEquals(ImageScaleDirection.None, legacy.toParams().scaleDirection)
        assertEquals(
            ImageScaleDirection.Up,
            legacy.copy(scaleSmallImagesToLarge = true).toParams().scaleDirection
        )
    }

    private fun combiner(
        getter: ImageGetter<Bitmap>,
        scaler: ImageScaler<Bitmap>,
        shareProvider: ImageShareProvider<Bitmap> = dependency()
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
            shareProvider = shareProvider,
            filterProvider = dependency(),
            imagePreviewCreator = dependency(),
            cvStitchHelper = CvStitchHelper(getter, scaler),
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
