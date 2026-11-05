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
import android.system.Os
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.interactive.action.PDActionGoTo
import com.tom_roush.pdfbox.pdmodel.interactive.action.PDActionURI
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination
import com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PdfPageImportInstrumentedTest {

    @Test
    fun openingPdfClosesInputOnSuccessAndIncorrectPassword() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File.createTempFile("pdf-open-", ".pdf", context.cacheDir)
        try {
            PDDocument().use { document ->
                document.addPage(PDPage())
                document.protect(StandardProtectionPolicy("owner", "reader", AccessPermission()))
                document.save(file)
            }
            safeOpenPdf(file.toUri().toString(), "reader").use { document ->
                assertEquals(1, document.numberOfPages)
                assertInputClosed(file)
            }
            assertThrows(InvalidPasswordException::class.java) {
                safeOpenPdf(file.toUri().toString(), "wrong")
            }
            assertInputClosed(file)
        } finally {
            file.delete()
        }
    }

    @Test
    fun failedPdfRetryClosesBothInputs() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File.createTempFile("pdf-invalid-", ".pdf", context.cacheDir)
        try {
            file.writeText("Not a PDF")
            assertThrows(IOException::class.java) {
                safeOpenPdf(file.toUri().toString(), null)
            }
            assertInputClosed(file)
        } finally {
            file.delete()
        }
    }

    private fun assertInputClosed(file: File) {
        val openInputs = File("/proc/self/fd").listFiles().orEmpty().count {
            runCatching { Os.readlink(it.path) }.getOrNull() == file.canonicalPath
        }
        assertEquals("Input stream was not closed", 0, openInputs)
    }

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
                val importedPage = target.importPagesForCopy(listOf(firstPage)).single()

                assertTrue(importedPage.cosObject.containsKey(COSName.RESOURCES))
                assertNotNull(importedPage.resources)

                val importedLink = importedPage.annotations.single() as PDAnnotationLink
                assertNull(importedLink.page)
                assertNull((importedLink.destination as PDPageDestination).page)
                assertSame(firstPage.cosObject, firstPage.annotations.single().page.cosObject)
                assertSame(linkedPage.cosObject, destination.page.cosObject)

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
    fun reorderedPagesKeepInternalAndExternalLinksWithoutMutatingTheSource() {
        val output = ByteArrayOutputStream()
        PDDocument().use { source ->
            val first = PDPage().also(source::addPage)
            val second = PDPage().also(source::addPage)
            val destination = PDPageFitDestination().apply { page = second }
            first.annotations = listOf(
                PDAnnotationLink().apply {
                    this.destination = destination
                    page = first
                },
                PDAnnotationLink().apply {
                    action = PDActionGoTo().apply { this.destination = destination }
                },
                PDAnnotationLink().apply {
                    action = PDActionURI().apply { uri = "https://example.com" }
                }
            )
            PDDocument().use { target ->
                val pages = target.importPagesForCopy(listOf(second, first))
                val links = pages[1].annotations.map { it as PDAnnotationLink }
                assertSame(
                    pages[0].cosObject,
                    (links[0].destination as PDPageDestination).page.cosObject
                )
                assertSame(
                    pages[0].cosObject,
                    ((links[1].action as PDActionGoTo).destination as PDPageDestination).page.cosObject
                )
                assertEquals("https://example.com", (links[2].action as PDActionURI).uri)
                assertSame(second.cosObject, destination.page.cosObject)
                assertSame(first.cosObject, first.annotations.first().page.cosObject)
                target.save(output)
            }
        }
        PDDocument.load(output.toByteArray()).use { document ->
            assertEquals(2, document.numberOfPages)
            val links = document.getPage(1).annotations.map { it as PDAnnotationLink }
            assertSame(
                document.getPage(0).cosObject,
                (links[0].destination as PDPageDestination).page.cosObject
            )
            assertSame(
                document.getPage(0).cosObject,
                ((links[1].action as PDActionGoTo).destination as PDPageDestination).page.cosObject
            )
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
                    target.importPagesForCopy(listOf(source.getPageSafe(0)))
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
