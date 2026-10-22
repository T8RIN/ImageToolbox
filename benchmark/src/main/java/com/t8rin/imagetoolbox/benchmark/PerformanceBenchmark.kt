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

package com.t8rin.imagetoolbox.benchmark

import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File

@OptIn(ExperimentalMetricApi::class)
@LargeTest
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 29)
class PerformanceBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private fun captureFailure(name: String) {
        val directory = File(
            InstrumentationRegistry.getInstrumentation().context.externalMediaDirs.first(),
            "failures"
        ).apply { mkdirs() }
        device.takeScreenshot(File(directory, "$name.png"))
        device.dumpWindowHierarchy(File(directory, "$name.xml"))
    }

    private fun memoryMetric() = MemoryUsageMetric(
        mode = MemoryUsageMetric.Mode.Max,
        subMetrics = listOf(
            MemoryUsageMetric.SubMetric.RssAnon,
            MemoryUsageMetric.SubMetric.RssFile
        )
    )

    private val galleryImages = mutableListOf<Uri>()

    @Before
    fun prepareApp() = device.prepareBenchmarkApp()

    @Test
    fun coldStartupUncompiled() = startup(
        mode = StartupMode.COLD,
        compilationMode = CompilationMode.None()
    )

    @Test
    fun coldStartupCompiled() = startup(
        mode = StartupMode.COLD,
        compilationMode = CompilationMode.Full()
    )

    @Test
    fun coldStartupWithBaselineProfile() = startup(
        mode = StartupMode.COLD,
        compilationMode = CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Require
        )
    )

    @Test
    fun warmStartup() = startup(
        mode = StartupMode.WARM,
        compilationMode = CompilationMode.Full()
    )

    private fun startup(
        mode: StartupMode,
        compilationMode: CompilationMode
    ) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        startupMode = mode,
        iterations = 5,
        setupBlock = { pressHome() }
    ) {
        startActivityAndWait()
        check(device.wait(Until.hasObject(By.text(appString("single_edit"))), TIMEOUT))
    }

    @Test
    fun mainScreenScrolling() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(
            FrameTimingMetric(),
            memoryMetric()
        ),
        compilationMode = CompilationMode.Full(),
        iterations = 5,
        setupBlock = {
            killProcess()
            startActivityAndWait()
            check(device.wait(Until.hasObject(By.text(appString("single_edit"))), TIMEOUT))
            device.waitForIdle()
        }
    ) {
        device.scrollList()
    }

    @Test
    fun openSingleEditor() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(
            FrameTimingMetric(),
            memoryMetric()
        ),
        compilationMode = CompilationMode.Full(),
        iterations = 5,
        setupBlock = {
            killProcess()
            startActivityAndWait()
            check(device.wait(Until.hasObject(By.text(appString("single_edit"))), TIMEOUT))
            device.waitForIdle()
        }
    ) {
        device.openSingleEditor()
        check(device.wait(Until.hasObject(By.text(appString("pick_image"))), TIMEOUT)) {
            captureFailure("openSingleEditor")
            "Single editor is not visible"
        }
        device.waitForIdle()
    }

    @After
    fun removeGalleryImages() {
        val resolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
        galleryImages.forEach { resolver.delete(it, null, null) }
        galleryImages.clear()
    }

    private fun createGalleryImages(count: Int) {
        val context = InstrumentationRegistry.getInstrumentation().context
        val bitmap = Bitmap.createBitmap(4_000, 3_000, Bitmap.Config.ARGB_8888)
        val jpeg = try {
            Canvas(bitmap).drawPaint(Paint().apply {
                shader = LinearGradient(
                    0f, 0f, 4_000f, 3_000f,
                    Color.MAGENTA, Color.CYAN, Shader.TileMode.CLAMP
                )
            })
            ByteArrayOutputStream().use { output ->
                check(bitmap.compress(Bitmap.CompressFormat.JPEG, 95, output))
                output.toByteArray()
            }
        } finally {
            bitmap.recycle()
        }
        repeat(count) { index ->
            val uri = checkNotNull(
                context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        "imagetoolbox_benchmark_$index.jpg"
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/ImageToolboxBenchmark"
                    )
                }
            ))
            galleryImages.add(uri)
            checkNotNull(context.contentResolver.openOutputStream(uri)).use { it.write(jpeg) }
        }
    }

    @Test
    fun settingsScrolling() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric(), memoryMetric()),
        compilationMode = CompilationMode.Full(),
        iterations = 5,
        setupBlock = {
            killProcess()
            startActivityAndWait()
            device.waitForIdle()
            device.dismissPrompts()
            device.openSettings()
        }
    ) {
        device.scrollList(swipeCount = 12)
    }

    @Test
    fun toolGroupsScrolling() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric(), memoryMetric()),
        compilationMode = CompilationMode.Full(),
        iterations = 5,
        setupBlock = {
            killProcess()
            startActivityAndWait()
            device.waitForIdle()
            device.dismissPrompts()
        }
    ) {
        for (group in listOf("create", "image", "tools", "edit")) {
            var tab = checkNotNull(
                device.findObjects(By.text(appString(group)))
                    .lastOrNull { it.visibleBounds.top > device.displayHeight * 3 / 4 })
            while (!tab.isClickable) tab = checkNotNull(tab.parent)
            tab.click()
            device.waitForIdle()
            device.scrollList()
        }
    }

    @Test
    fun singleEditorImageControls() {
        createGalleryImages(1)
        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setClassName(PACKAGE_NAME, "com.t8rin.imagetoolbox.app.presentation.AppActivity")
            setDataAndType(galleryImages.first(), "image/jpeg")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric(), memoryMetric()),
            compilationMode = CompilationMode.Full(),
            iterations = 5,
            setupBlock = {
                killProcess()
                context.startActivity(intent)
                check(device.wait(Until.hasObject(By.desc(appString("edit"))), TIMEOUT))
                device.waitForIdle()
                checkNotNull(device.findObject(By.desc(appString("edit")))).click()
                check(device.wait(Until.hasObject(By.text(appString("single_edit_sub"))), TIMEOUT))
                device.openSingleEditor()
                check(device.wait(Until.hasObject(By.desc(appString("zoom"))), 30_000)) {
                    captureFailure("singleEditorImageControls")
                    "Image preview is not ready"
                }
                device.waitForIdle()
            }
        ) {
            repeat(2) {
                checkNotNull(device.findObject(By.desc("Rotate Right"))).click()
                device.waitForIdle()
                checkNotNull(device.findObject(By.desc("Rotate Left"))).click()
                device.waitForIdle()
            }
            device.scrollList()
        }
    }

    @Test
    fun galleryPaging() {
        val context = InstrumentationRegistry.getInstrumentation().context
        createGalleryImages(12)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setClassName(PACKAGE_NAME, "com.t8rin.imagetoolbox.app.presentation.AppActivity")
            setDataAndType(galleryImages.first(), "image/jpeg")
            clipData = ClipData.newRawUri("Benchmark images", galleryImages.first()).apply {
                galleryImages.drop(1).forEach { addItem(ClipData.Item(it)) }
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(
                FrameTimingMetric(),
                memoryMetric()
            ),
            compilationMode = CompilationMode.Full(),
            iterations = 5,
            setupBlock = {
                killProcess()
                pressHome()
            }
        ) {
            // Shell-based startActivityAndWait does not preserve ClipData.
            context.startActivity(intent)
            check(
                device.wait(
                    Until.hasObject(By.text("1/12")), TIMEOUT
                )
            ) { "Image pager is not visible" }
            device.waitForIdle()
            SystemClock.sleep(750)
            repeat(4) { index ->
                device.swipe(
                    device.displayWidth * 4 / 5, device.displayHeight / 2,
                    device.displayWidth / 5, device.displayHeight / 2,
                    20
                )
                check(
                    device.wait(
                        Until.hasObject(By.text("${index + 2}/12")), TIMEOUT
                    )
                ) {
                    captureFailure("galleryPaging")
                    "Image pager did not advance"
                }
                device.waitForIdle()
            }
        }
    }

}