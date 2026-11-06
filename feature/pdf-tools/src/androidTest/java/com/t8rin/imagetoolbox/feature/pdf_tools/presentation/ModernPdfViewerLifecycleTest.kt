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

import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.core.net.toUri
import androidx.pdf.ExperimentalPdfApi
import androidx.pdf.view.PdfView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleCallback
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.t8rin.imagetoolbox.core.ui.utils.ComposeActivity
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.viewer.ModernPdfViewerDelegate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPdfApi::class)
class ModernPdfViewerLifecycleTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test
    fun changingDocumentAndThemeAndRecreatingViewKeepsViewerUsable() {
        val files = listOf(pdf(4), pdf(7))
        val activity = startActivity()
        try {
            lateinit var fragment: ModernPdfViewerDelegate
            instrumentation.runOnMainSync {
                val container = FrameLayout(activity).apply { id = View.generateViewId() }
                activity.setContentView(container)
                fragment = ModernPdfViewerDelegate()
                activity.supportFragmentManager.beginTransaction().add(container.id, fragment)
                    .commitNow()
                fragment.setScheme(lightColorScheme())
                fragment.documentUri = files[0].toUri()
            }
            await { findPdfView(fragment.requireView())?.pdfDocument?.pageCount == 4 }
            instrumentation.runOnMainSync {
                fragment.setScheme(darkColorScheme())
                fragment.documentUri = files[1].toUri()
            }
            await { findPdfView(fragment.requireView())?.pdfDocument?.pageCount == 7 }
            instrumentation.runOnMainSync {
                fragment.setScheme(lightColorScheme())
                activity.supportFragmentManager.beginTransaction().detach(fragment).commitNow()
                activity.supportFragmentManager.beginTransaction().attach(fragment).commitNow()
                fragment.setScheme(darkColorScheme())
            }
            await { findPdfView(fragment.requireView())?.pdfDocument?.pageCount == 7 }
            instrumentation.runOnMainSync {
                assertEquals(false, fragment.loadingState.value)
                assertNotNull(findPdfView(fragment.requireView()))
                fragment.setScheme(lightColorScheme())
                activity.supportFragmentManager.beginTransaction().remove(fragment).commitNow()
            }
        } finally {
            instrumentation.runOnMainSync { activity.finish() }
            files.forEach(File::delete)
        }
    }

    private fun pdf(pages: Int): File {
        val file = File(context.cacheDir, "modern-pdf-regression-$pages.pdf")
        val document = PdfDocument()
        try {
            repeat(pages) { index ->
                val page =
                    document.startPage(PdfDocument.PageInfo.Builder(200, 300, index).create())
                page.canvas.drawColor(android.graphics.Color.WHITE)
                document.finishPage(page)
            }
            file.outputStream().use(document::writeTo)
        } finally {
            document.close()
        }
        return file
    }

    private fun findPdfView(view: View): PdfView? {
        if (view is PdfView) return view
        if (view is ViewGroup) {
            repeat(view.childCount) { index ->
                findPdfView(view.getChildAt(index))?.let { return it }
            }
        }
        return null
    }

    private fun startActivity(): ComposeActivity {
        var activity: ComposeActivity? = null
        val resumed = CountDownLatch(1)
        val monitor = ActivityLifecycleMonitorRegistry.getInstance()
        val callback = ActivityLifecycleCallback { current, stage ->
            if (current is ComposeActivity && stage == Stage.RESUMED) {
                activity = current
                resumed.countDown()
            }
        }
        instrumentation.runOnMainSync { monitor.addLifecycleCallback(callback) }
        try {
            context.startActivity(
                Intent().setClassName(
                    context, "com.t8rin.imagetoolbox.app.presentation.AppActivity"
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            assertTrue("App activity did not resume", resumed.await(15, TimeUnit.SECONDS))
            return requireNotNull(activity)
        } finally {
            instrumentation.runOnMainSync { monitor.removeLifecycleCallback(callback) }
        }
    }

    private fun await(condition: () -> Boolean) {
        val deadline = android.os.SystemClock.uptimeMillis() + 15_000
        var done = false
        while (!done && android.os.SystemClock.uptimeMillis() < deadline) {
            instrumentation.runOnMainSync { done = condition() }
            if (!done) Thread.sleep(20)
        }
        assertTrue("PDF did not reach the expected state", done)
    }
}