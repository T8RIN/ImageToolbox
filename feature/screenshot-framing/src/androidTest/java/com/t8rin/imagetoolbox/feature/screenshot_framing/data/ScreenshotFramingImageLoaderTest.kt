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

package com.t8rin.imagetoolbox.feature.screenshot_framing.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.request.allowHardware
import com.t8rin.exif.ExifInterface
import com.t8rin.imagetoolbox.core.data.image.AndroidImageGetter
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.utils.initAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.reflect.Proxy

@RunWith(AndroidJUnit4::class)
class ScreenshotFramingImageLoaderTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext.apply {
        initAppContext()
    }
    private val imageLoader = ImageLoader.Builder(context).allowHardware(false).build()
    private val appScope = object : AppScope {
        override val coroutineContext = SupervisorJob() + Dispatchers.Default
    }
    private val getter = AndroidImageGetter(
        context = context,
        imageLoader = imageLoader,
        appScope = appScope,
        failureNotifier = dependency { name, args ->
            if (name == "send") throw args[0] as Throwable
            error("Unexpected call: $name")
        },
        metadataProvider = { error("Metadata is not needed for framing") },
        settingsProvider = dependency<SettingsProvider> { name, _ ->
            check(name == "getSettingsState")
            MutableStateFlow(SettingsState.Default)
        },
        dispatchersHolder = dependency<DispatchersHolder> { _, _ -> Dispatchers.Default }
    )

    @After
    fun tearDown() {
        imageLoader.shutdown()
        appScope.cancel()
    }

    @Test
    fun smallSourceIsNotUpscaledForPreviewOrExport() = runBlocking {
        val file = createSource(64, 96)
        try {
            for (maxSide in listOf(1600, 4096)) {
                val image = requireNotNull(getter.getScreenshotFramingImage(file.toUri(), maxSide))
                assertEquals("Width with maxSide=$maxSide", 64, image.width)
                assertEquals("Height with maxSide=$maxSide", 96, image.height)
                assertEquals(Color.RED, image.getPixel(32, 48))
            }
        } finally {
            file.delete()
        }
    }

    @Test
    fun largeSourceIsDownscaledToTheRequestedLimit() = runBlocking {
        val file = createSource(800, 1200)
        try {
            val image =
                requireNotNull(getter.getScreenshotFramingImage(file.toUri(), maxSide = 240))
            assertEquals(160, image.width)
            assertEquals(240, image.height)
            assertTrue(image.width.toLong() * image.height <= 240L * 240)
        } finally {
            file.delete()
        }
    }

    @Test
    fun exifRotationIsPreservedWithoutUpscaling() = runBlocking {
        val file = createSource(100, 40, Bitmap.CompressFormat.JPEG)
        try {
            ExifInterface(file).apply {
                setAttribute(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_ROTATE_90.toString()
                )
                saveAttributes()
            }
            val image =
                requireNotNull(getter.getScreenshotFramingImage(file.toUri(), maxSide = 4096))
            assertEquals(40, image.width)
            assertEquals(100, image.height)
        } finally {
            file.delete()
        }
    }

    private fun createSource(
        width: Int,
        height: Int,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ): File {
        val file = File.createTempFile("screenshot-framing-", ".image", context.cacheDir)
        val image = createBitmap(width, height).apply { eraseColor(Color.RED) }
        try {
            file.outputStream().use { image.compress(format, 100, it) }
        } finally {
            image.recycle()
        }
        return file
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> dependency(
        crossinline invoke: (String, Array<out Any?>) -> Any?
    ): T = Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, method, args ->
        invoke(method.name, args ?: emptyArray())
    } as T
}