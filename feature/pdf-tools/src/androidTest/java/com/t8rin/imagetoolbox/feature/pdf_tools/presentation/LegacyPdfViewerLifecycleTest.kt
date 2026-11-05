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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation

import android.graphics.pdf.PdfDocument
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.test.platform.app.InstrumentationRegistry
import coil3.imageLoader
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.viewer.LegacyPdfViewer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class LegacyPdfViewerLifecycleTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun leavingViewerClosesDocumentDescriptor() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.cacheDir, "pdf-lifecycle-regression.pdf")
        val document = PdfDocument()
        try {
            repeat(5) { index ->
                val page =
                    document.startPage(PdfDocument.PageInfo.Builder(200, 300, index).create())
                page.canvas.drawColor(android.graphics.Color.WHITE)
                document.finishPage(page)
            }
            file.outputStream().use(document::writeTo)
        } finally {
            document.close()
        }
        val visible = mutableStateOf(false)
        fun openDescriptors() = File("/proc/self/fd").listFiles().orEmpty().count {
            runCatching {
                android.system.Os.readlink(it.absolutePath).endsWith("/${file.name}")
            }.getOrDefault(false)
        }
        try {
            val initial = openDescriptors()
            compose.setContent {
                ImageToolboxThemeForPreview(isDarkTheme = false) {
                    if (visible.value) LegacyPdfViewer(
                        file.toUri()
                    )
                }
            }
            repeat(3) {
                compose.runOnIdle { visible.value = true }
                compose.waitUntil(10_000) { openDescriptors() > initial }
                compose.runOnIdle { visible.value = false }
                compose.waitUntil(10_000) { openDescriptors() == initial }
            }
        } finally {
            file.delete()
        }
    }

    @Test
    fun changingViewportRendersAtTheNewResolutionWithoutReopeningDocument() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.cacheDir, "pdf-resize-regression.pdf")
        val document = PdfDocument()
        try {
            val page = document.startPage(PdfDocument.PageInfo.Builder(200, 300, 0).create())
            page.canvas.drawColor(android.graphics.Color.RED)
            document.finishPage(page)
            file.outputStream().use(document::writeTo)
        } finally {
            document.close()
        }
        val width = mutableStateOf(60.dp)
        val visible = mutableStateOf(true)
        val cache = requireNotNull(context.imageLoader.memoryCache)
        val prefix = file.toUri().toString()
        fun cachedWidths() = cache.keys.filter { it.key.startsWith(prefix) }
            .mapNotNull { cache[it]?.image?.width }

        fun descriptors() = File("/proc/self/fd").listFiles().orEmpty().filter {
            runCatching { android.system.Os.readlink(it.absolutePath).endsWith("/${file.name}") }
                .getOrDefault(false)
        }.map { it.name }.toSet()
        try {
            compose.setContent {
                ImageToolboxThemeForPreview(isDarkTheme = false) {
                    if (visible.value) LegacyPdfViewer(
                        file.toUri(),
                        modifier = Modifier.width(width.value)
                    )
                }
            }
            compose.waitUntil(10_000) { cachedWidths().isNotEmpty() }
            val initialWidth = cachedWidths().max()
            val initialDescriptors = descriptors()
            assertTrue(initialDescriptors.isNotEmpty())
            compose.runOnIdle { width.value = 200.dp }
            compose.waitUntil(10_000) { cachedWidths().size >= 2 }
            assertTrue(
                "Page stayed at $initialWidth pixels: ${cachedWidths()}",
                cachedWidths().max() > initialWidth
            )
            assertEquals(initialDescriptors, descriptors())
            compose.runOnIdle { visible.value = false }
            compose.waitUntil(10_000) { descriptors().isEmpty() }
        } finally {
            cache.keys.filter { it.key.startsWith(prefix) }.forEach(cache::remove)
            file.delete()
        }
    }

}