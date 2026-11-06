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

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.os.Trace
import android.provider.MediaStore
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMetricApi::class)
@SdkSuppress(minSdkVersion = 29)
class MediaGridBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.context
    private val device = UiDevice.getInstance(instrumentation)
    private val images = mutableListOf<Uri>()

    @After
    fun removeImages() {
        if (InstrumentationRegistry.getArguments()
                .getString("galleryKeepFixtures") == "true"
        ) return
        images.forEach { context.contentResolver.delete(it, null, null) }
    }

    @Test
    fun scrollDenseDay() = measureGrid(20)

    @Test
    fun scrollDenseDayTenColumns() = measureGrid(10)

    @Test
    fun pinchDenseDay() = measureGrid(20, Gesture.Pinch)

    @Test
    fun flingDenseDay() = measureGrid(20, Gesture.Fling)

    @Test
    fun fastScrollDenseDay() = measureGrid(20, Gesture.FastScroll)

    @Test
    fun fastScrollDenseDayFourColumns() = measureGrid(4, Gesture.FastScroll)

    @Test
    fun flingDenseDayFourColumns() = measureGrid(4, Gesture.Fling)

    @Test
    fun scrollDenseDayFourColumns() = measureGrid(4)

    @Test
    fun scrollDenseDayOneColumn() = measureGrid(1)

    @Test
    fun scrollPhotosDenseDay() = measureGrid(20, rasterOnly = true)

    @Test
    fun pinchPhotosDenseDay() = measureGrid(20, Gesture.Pinch, rasterOnly = true)

    private enum class Gesture { Scroll, Fling, FastScroll, Pinch }

    private fun measureGrid(
        columns: Int,
        gesture: Gesture = Gesture.Scroll,
        rasterOnly: Boolean = false
    ) {
        val permission = if (Build.VERSION.SDK_INT >= 33) "READ_MEDIA_IMAGES"
        else "READ_EXTERNAL_STORAGE"
        device.executeShellCommand("pm grant $PACKAGE_NAME android.permission.$permission")
        device.executeShellCommand("pm grant $PACKAGE_NAME android.permission.ACCESS_MEDIA_LOCATION")
        createImages(rasterOnly)
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            setClassName(
                PACKAGE_NAME,
                "com.t8rin.imagetoolbox.feature.media_picker.presentation.MediaPickerActivity"
            )
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        lateinit var grid: UiObject
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(
                FrameTimingMetric(),
                MemoryUsageMetric(MemoryUsageMetric.Mode.Max)
            ),
            compilationMode = CompilationMode.Full(),
            iterations = InstrumentationRegistry.getArguments()
                .getString("galleryIterations")?.toInt() ?: 5,
            setupBlock = {
                killProcess()
                startActivityAndWait(intent)
                checkNotNull(
                    device.wait(
                        Until.findObject(By.text("ImageToolboxGridBenchmark")),
                        30_000
                    )
                ).click()
                var gridIndex = -1
                val deadline = SystemClock.uptimeMillis() + 30_000
                while (gridIndex < 0 && SystemClock.uptimeMillis() < deadline) {
                    gridIndex = device.findObjects(By.scrollable(true)).indexOfFirst {
                        it.visibleBounds.height() > device.displayHeight / 2
                    }
                    if (gridIndex < 0) SystemClock.sleep(100)
                }
                check(gridIndex >= 0)
                val gridSelector = UiSelector().scrollable(true).instance(gridIndex)
                grid = device.findObject(gridSelector)
                device.waitForIdle()
                SystemClock.sleep(1_000)
                pinch(grid, 0.4f, 0.01f)
                fastScroll(grid.visibleBounds, toEndOnly = true)
                if (columns != 20) pinch(grid, 0.02f, 0.4f / columns)
                if (Build.VERSION.SDK_INT >= 34) instrumentation.uiAutomation.clearCache()
                val bounds = grid.visibleBounds
                val expectedSize = bounds.width().toFloat() / columns
                val cells = (0 until minOf(grid.childCount, columns * 3 + 4)).map {
                    grid.getChild(UiSelector().index(it)).visibleBounds
                }.filter {
                    val tolerance = context.resources.displayMetrics.density + 1
                    bounds.contains(it) && if (columns == 1) {
                        kotlin.math.abs(it.width() - expectedSize) <= tolerance && it.height() > 0
                    } else {
                        it.width() in 1..(expectedSize * 2).toInt() &&
                                kotlin.math.abs(it.height() - expectedSize) <= tolerance
                    }
                }
                val cellHeight =
                    cells.groupingBy { it.height() }.eachCount().maxByOrNull { it.value }?.key
                val thumbnails =
                    cells.filter { kotlin.math.abs(it.height() - (cellHeight ?: 0)) <= 1 }
                val actualColumns = thumbnails.maxOfOrNull { cell ->
                    thumbnails.count { cell.centerY() in it.top until it.bottom }
                } ?: 0
                if (actualColumns != columns) {
                    device.dumpWindowHierarchy(
                        File(
                            context.getExternalFilesDir(null),
                            "grid-setup.xml"
                        )
                    )
                    device.takeScreenshot(File(context.getExternalFilesDir(null), "grid-setup.png"))
                }
                check(actualColumns == columns) { "Expected $columns columns, found $actualColumns; grid=$bounds" }
                Log.i("GalleryBenchmark", "columns=$actualColumns cells=${cells.size}")
                device.waitForIdle()
                SystemClock.sleep(1_000)
                device.takeScreenshot(File(context.getExternalFilesDir(null), "grid-$columns.png"))
            }
        ) {
            if (gesture == Gesture.Pinch) {
                repeat(3) {
                    pinch(grid, 0.06f, 0.3f)
                    pinch(grid, 0.3f, 0.06f)
                }
            } else if (gesture == Gesture.FastScroll) {
                fastScroll(grid.visibleBounds)
            } else {
                val x = device.displayWidth / 2
                val top = device.displayHeight / 4
                val bottom = device.displayHeight * 3 / 4
                repeat(3) {
                    val steps = if (gesture == Gesture.Fling) 8 else 60
                    device.swipe(x, top, x, bottom, steps)
                    if (gesture == Gesture.Fling) device.waitForIdle()
                    device.swipe(x, bottom, x, top, steps)
                    if (gesture == Gesture.Fling) device.waitForIdle()
                }
            }
        }
    }

    private fun pinch(grid: UiObject, start: Float, end: Float) {
        val bounds = grid.visibleBounds
        fun pointer(direction: Int) = Array(31) { step ->
            MotionEvent.PointerCoords().apply {
                x = bounds.centerX() + direction * bounds.width() *
                        (start + (end - start) * step / 30)
                y = bounds.centerY().toFloat()
                pressure = 1f
                size = 1f
            }
        }
        Trace.beginSection("GalleryPinch")
        try {
            check(grid.performMultiPointerGesture(pointer(-1), pointer(1)))
            device.waitForIdle()
        } finally {
            Trace.endSection()
        }
    }

    private fun fastScroll(bounds: Rect, toEndOnly: Boolean = false) {
        val density = context.resources.displayMetrics.density
        device.swipe(
            bounds.centerX(),
            bounds.centerY(),
            bounds.centerX(),
            bounds.centerY() - 100,
            20
        )
        val screenshot = File(context.cacheDir, "scroll-thumb.png")
        check(device.takeScreenshot(screenshot))
        val bitmap = checkNotNull(BitmapFactory.decodeFile(screenshot.path))
        val x = bounds.right - (2 * density).toInt()
        val bottom = try {
            var start = bounds.top
            var center = -1
            for (y in bounds.top + 1 until bounds.bottom) {
                if (bitmap.getPixel(x, y) != bitmap.getPixel(x, start)) {
                    if (y - start in (50 * density).toInt()..(70 * density).toInt()) {
                        val color = bitmap.getPixel(x, (start + y) / 2)
                        val isThin = listOf(0.25f, 0.5f, 0.75f).all { fraction ->
                            val row = start + ((y - start) * fraction).toInt()
                            bitmap.getPixel(bounds.right - 1, row) == color &&
                                    bitmap.getPixel(x - (8 * density).toInt(), row) != color
                        }
                        if (isThin) center = (start + y) / 2
                    }
                    start = y
                }
            }
            check(center >= 0) { "Fast scroll thumb was not visible" }
            center
        } finally {
            bitmap.recycle()
            screenshot.delete()
        }
        Log.i("GalleryBenchmark", "thumb=$x,$bottom grid=$bounds")
        val top = bounds.top + (32 * density).toInt()
        val downTime = SystemClock.uptimeMillis()
        fun send(action: Int, y: Float) {
            val event = MotionEvent.obtain(
                downTime, SystemClock.uptimeMillis(), action, x.toFloat(), y, 0
            ).apply { source = InputDevice.SOURCE_TOUCHSCREEN }
            try {
                check(instrumentation.uiAutomation.injectInputEvent(event, true))
            } finally {
                event.recycle()
            }
        }

        val positions = if (toEndOnly) listOf(bounds.bottom)
        else List(6) { if (it % 2 == 0) top else bounds.bottom }
        val steps = if (toEndOnly) 1 else 60
        var from = bottom
        Trace.beginSection("GalleryFastScroll")
        try {
            send(MotionEvent.ACTION_DOWN, from.toFloat())
            positions.forEach { to ->
                for (step in 1..steps) {
                    send(MotionEvent.ACTION_MOVE, from + (to - from) * step / steps.toFloat())
                    SystemClock.sleep(5)
                }
                from = to
            }
        } finally {
            send(MotionEvent.ACTION_UP, from.toFloat())
            Trace.endSection()
        }
        device.waitForIdle()
    }

    private fun createImages(rasterOnly: Boolean) {
        context.contentResolver.delete(
            MediaStore.Files.getContentUri("external"),
            "${MediaStore.MediaColumns.RELATIVE_PATH} = ? AND ${MediaStore.MediaColumns.OWNER_PACKAGE_NAME} = ?",
            arrayOf("Download/ImageToolboxGridBenchmark/", context.packageName)
        )
        val assets = context.createPackageContext(PACKAGE_NAME, 0).assets
        val bitmap = Bitmap.createBitmap(2048, 1536, Bitmap.Config.ARGB_8888)
        val raster = try {
            Canvas(bitmap).drawPaint(Paint().apply {
                shader = LinearGradient(
                    0f, 0f, 2048f, 1536f, Color.MAGENTA, Color.CYAN, Shader.TileMode.CLAMP
                )
            })
            listOf(
                Triple("jpg", "image/jpeg", Bitmap.CompressFormat.JPEG),
                Triple("png", "image/png", Bitmap.CompressFormat.PNG),
                Triple("webp", "image/webp", Bitmap.CompressFormat.WEBP)
            ).map { (extension, mime, format) ->
                Triple(extension, mime, ByteArrayOutputStream().use {
                    check(bitmap.compress(format, 90, it))
                    it.toByteArray()
                })
            }
        } finally {
            bitmap.recycle()
        }
        val vectors =
            if (rasterOnly) emptyList() else assets.list("svg/emotions").orEmpty().take(40)
                .map { name ->
            Triple("svg", "image/svg+xml", assets.open("svg/emotions/$name").use { it.readBytes() })
        }
        check(rasterOnly || vectors.isNotEmpty())
        val fixtures = raster + vectors
        val today = System.currentTimeMillis() / 86_400_000 * 86_400_000
        val paths = mutableListOf<String>()
        repeat(2_400) { index ->
            val (extension, mime, bytes) = fixtures[index % fixtures.size]
            val date = today - (if (index < 700) index / 100 else 7) * 86_400_000L
            val uri = checkNotNull(
                context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                ContentValues().apply {
                    put(
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        "grid_benchmark_$index.$extension"
                    )
                    put(MediaStore.MediaColumns.MIME_TYPE, mime)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "Download/ImageToolboxGridBenchmark"
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            ))
            images.add(uri)
            checkNotNull(context.contentResolver.openOutputStream(uri)).use { it.write(bytes) }
            context.contentResolver.update(uri, ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
            }, null, null)
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "ImageToolboxGridBenchmark/grid_benchmark_$index.$extension"
            )
            check(file.setLastModified(date))
            paths += file.path
        }
        val scanned = CountDownLatch(paths.size)
        MediaScannerConnection.scanFile(context, paths.toTypedArray(), null) { _, _ ->
            scanned.countDown()
        }
        check(scanned.await(60, TimeUnit.SECONDS))
    }

}