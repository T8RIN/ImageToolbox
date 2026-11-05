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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
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
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayOutputStream

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
        images.forEach { context.contentResolver.delete(it, null, null) }
    }

    @Test
    fun scrollDenseDay() = scroll(20)

    @Test
    fun scrollDenseDayTenColumns() = scroll(10)

    private fun scroll(columns: Int) {
        val permission = if (Build.VERSION.SDK_INT >= 33) "READ_MEDIA_IMAGES"
        else "READ_EXTERNAL_STORAGE"
        device.executeShellCommand("pm grant $PACKAGE_NAME android.permission.$permission")
        device.executeShellCommand("pm grant $PACKAGE_NAME android.permission.ACCESS_MEDIA_LOCATION")
        createImages()
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            setClassName(
                PACKAGE_NAME,
                "com.t8rin.imagetoolbox.feature.media_picker.presentation.MediaPickerActivity"
            )
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(
                FrameTimingMetric(),
                MemoryUsageMetric(MemoryUsageMetric.Mode.Max)
            ),
            compilationMode = CompilationMode.Full(),
            iterations = 5,
            setupBlock = {
                killProcess()
                startActivityAndWait(intent)
                check(device.wait(Until.hasObject(By.scrollable(true)), 30_000))
                device.waitForIdle()
                // The maximum zoom level is deterministic regardless of the saved column setting.
                val grid = device.findObject(UiSelector().scrollable(true))
                check(grid.pinchIn(95, 30))
                device.waitForIdle()
                if (columns == 10) {
                    val bounds = grid.visibleBounds
                    val span = bounds.width() / 6f
                    fun pointer(direction: Int) = Array(31) { step ->
                        MotionEvent.PointerCoords().apply {
                            x = bounds.centerX() + direction * span * (1 + step / 30f)
                            y = bounds.centerY().toFloat()
                            pressure = 1f
                            size = 1f
                        }
                    }
                    check(grid.performMultiPointerGesture(pointer(-1), pointer(1)))
                    device.waitForIdle()
                }
                val cells = device.findObjects(By.clickable(true)).map { it.visibleBounds }
                    .filter { it.width() == it.height() && it.width() < device.displayWidth / 5 }
                    .groupBy { it.width() }.maxByOrNull { it.value.size }?.value.orEmpty()
                val actualColumns = cells.distinctBy { it.left }.size
                check(actualColumns == columns) { "Expected $columns columns, found $actualColumns" }
                Log.i("GalleryBenchmark", "columns=$actualColumns cells=${cells.size}")
                UiScrollable(UiSelector().scrollable(true)).scrollToEnd(30, 10)
                device.waitForIdle()
                SystemClock.sleep(1_000)
            }
        ) {
            val x = device.displayWidth / 2
            val top = device.displayHeight / 4
            val bottom = device.displayHeight * 3 / 4
            repeat(3) {
                device.swipe(x, top, x, bottom, 60)
                device.swipe(x, bottom, x, top, 60)
            }
        }
    }

    private fun createImages() {
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
        val vectors = assets.list("svg/emotions").orEmpty().take(40).map { name ->
            Triple("svg", "image/svg+xml", assets.open("svg/emotions/$name").use { it.readBytes() })
        }
        check(vectors.isNotEmpty())
        val fixtures = raster + vectors
        val today = System.currentTimeMillis() / 86_400_000 * 86_400_000
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
                put(MediaStore.MediaColumns.DATE_MODIFIED, date / 1_000)
                put(MediaStore.MediaColumns.DATE_TAKEN, date)
            }, null, null)
        }
    }

}