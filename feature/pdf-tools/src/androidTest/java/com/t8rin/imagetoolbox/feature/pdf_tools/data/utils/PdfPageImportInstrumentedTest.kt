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
package com.t8rin.imagetoolbox.feature.pdf_tools.data.utils

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

@RunWith(AndroidJUnit4::class)
class PdfPageImportInstrumentedTest {

    @Test
    fun importedPageKeepsInheritedResourcesAndDropsSourcePageReferences() {
        val output = ByteArrayOutputStream()

        PDDocument().use { source ->
            source.document.version = 2.0f

            val firstPage = PDPage()
            val linkedPage = PDPage()
            source.addPage(firstPage)
            source.addPage(linkedPage)

            val inheritedResources = PDResources()
            source.documentCatalog.pages.cosObject.setItem(
                COSName.RESOURCES,
                inheritedResources.cosObject
            )
            assertFalse(firstPage.cosObject.containsKey(COSName.RESOURCES))
            assertNotNull(firstPage.resources)

            val destination = PDPageFitDestination().apply {
                page = linkedPage
            }
            firstPage.annotations = listOf(
                PDAnnotationLink().apply {
                    this.destination = destination
                    page = firstPage
                }
            )

            PDDocument().use { target ->
                target.document.version = source.version
                val importedPage = target.importPageForCopy(firstPage)

                assertTrue(importedPage.cosObject.containsKey(COSName.RESOURCES))
                assertNotNull(importedPage.resources)

                val importedLink = importedPage.annotations.single() as PDAnnotationLink
                assertNull(importedLink.page)
                assertNull((importedLink.destination as PDPageDestination).page)

                target.save(output)
            }
        }

        PDDocument.load(output.toByteArray()).use { result ->
            assertEquals(2.0f, result.version)
            assertEquals(1, result.numberOfPages)
            assertNotNull(result.getPage(0).resources)
        }
    }

    @Test
    fun pdf20Utf8PageStaysRenderableAfterSourceDocumentIsClosed() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val output = ByteArrayOutputStream()

        context.assets.open("pdf20-utf8-test.pdf").use { input ->
            PDDocument.load(input).use { source ->
                PDDocument().use { target ->
                    target.document.version = source.version
                    target.importPageForCopy(source.getPageSafe(0))
                    target.save(output)
                }
            }
        }

        PDDocument.load(output.toByteArray()).use { result ->
            assertEquals(2.0f, result.version)
            assertEquals(1, result.numberOfPages)

            val bitmap = PDFRenderer(result).renderImageWithDPI(
                0,
                72f,
                ImageType.RGB
            )

            try {
                assertTrue(bitmap.width > 0)
                assertTrue(bitmap.height > 0)

                val pixels = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(
                    pixels,
                    0,
                    bitmap.width,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height
                )

                assertTrue(
                    "Rendered page must contain non-white content",
                    pixels.any { pixel ->
                        Color.red(pixel) < 250 ||
                                Color.green(pixel) < 250 ||
                                Color.blue(pixel) < 250
                    }
                )
            } finally {
                bitmap.recycle()
            }
        }
    }
}
