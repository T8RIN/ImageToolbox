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

package com.t8rin.imagetoolbox.feature.pdf_tools.data

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleDirection
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.tom_roush.pdfbox.pdmodel.PDDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.lang.reflect.Proxy

@RunWith(AndroidJUnit4::class)
class PdfImageScalingTest {

    @Test
    fun allDirectionsSetPageSizesWithoutChangingPageOrder() = runBlocking {
        val images = listOf(createBitmap(40, 20), createBitmap(20, 20), createBitmap(30, 10))
        val scope = object : AppScope {
            override val coroutineContext = SupervisorJob() + Dispatchers.Unconfined
        }
        val output = ByteArrayOutputStream()
        val shareProvider = dependency<ShareProvider> { name, args ->
            check(name == "cacheData")
            @Suppress("UNCHECKED_CAST")
            val writeData = args[1] as suspend (Writeable) -> Unit
            runBlocking { writeData(StreamWriteable(output)) }
            "pdf"
        }
        try {
            val helper = AndroidPdfHelper(
                context = InstrumentationRegistry.getInstrumentation().targetContext,
                appScope = scope,
                shareProvider = shareProvider,
                imageGetter = dependency(),
                imageScaler = dependency(),
                dispatchersHolder = dependency<DispatchersHolder> { _, _ -> Dispatchers.Unconfined }
            )
            val expectedSizes = listOf(
                listOf(IntegerSize(40, 20), IntegerSize(20, 20), IntegerSize(30, 10)),
                listOf(IntegerSize(40, 20), IntegerSize(40, 40), IntegerSize(40, 13)),
                listOf(IntegerSize(20, 10), IntegerSize(20, 20), IntegerSize(20, 6))
            )
            for (direction in ImageScaleDirection.entries) {
                output.reset()
                helper.createPdfFromPreparedImages(
                    images = images,
                    quality = 1f,
                    scaleDirection = direction,
                    addTextLayer = null
                )
                PDDocument.load(output.toByteArray()).use { document ->
                    assertEquals(images.size, document.numberOfPages)
                    document.pages.forEachIndexed { index, page ->
                        val expected = expectedSizes[direction.ordinal][index]
                        assertEquals(expected.width.toFloat(), page.mediaBox.width, 0f)
                        assertEquals(expected.height.toFloat(), page.mediaBox.height, 0f)
                    }
                }
            }
        } finally {
            scope.cancel()
            images.forEach(Bitmap::recycle)
        }
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