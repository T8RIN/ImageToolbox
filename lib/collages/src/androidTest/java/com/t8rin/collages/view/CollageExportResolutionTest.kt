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

package com.t8rin.collages.view

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class CollageExportResolutionTest {
    @Test
    fun imageLoadedAfterStateSaveIsCenteredInTallFrame() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val sourceFile = File(context.cacheDir, "collage_initial_position.jpg")
        val source = Bitmap.createBitmap(1600, 1067, Bitmap.Config.ARGB_8888)
        source.eraseColor(Color.RED)
        sourceFile.outputStream().use { source.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        source.recycle()

        try {
            val item = PhotoItem(
                bound = RectF(0f, 0f, 1f, 1f),
                pointList = arrayListOf(
                    PointF(0f, 0f), PointF(1f, 0f),
                    PointF(1f, 1f), PointF(0f, 1f)
                )
            )
            lateinit var view: FrameImageView
            instrumentation.runOnMainSync {
                val layout = FramePhotoLayout(context, listOf(item)).apply {
                    build(200, 600)
                    measure(
                        View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY)
                    )
                    layout(0, 0, 200, 600)
                }
                val savedState = Bundle()
                layout.saveInstanceState(savedState)
                item.imagePath = Uri.fromFile(sourceFile)
                layout.restoreInstanceState(savedState)
                view = layout.getChildAt(0) as FrameImageView
                view.reloadImageFromPhotoItem()
            }
            withTimeout(10_000) {
                while (withContext(Dispatchers.Main) { view.image == null }) delay(50)
            }
            withContext(Dispatchers.Main) {
                val image = view.image!!
                val bounds = RectF(0f, 0f, image.width.toFloat(), image.height.toFloat())
                view.imageMatrix.mapRect(bounds)
                assertEquals(view.width / 2f, bounds.centerX(), 1f)
                assertEquals(view.height / 2f, bounds.centerY(), 1f)
                assertTrue(bounds.top <= 0f && bounds.bottom >= view.height)
            }
        } finally {
            sourceFile.delete()
        }
    }

    @Test
    fun exportUsesSourceResolutionAndOriginalPixels() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val sourceFile = File(context.cacheDir, "collage_export_resolution.png")
        val source = Bitmap.createBitmap(2400, 2400, Bitmap.Config.ARGB_8888)
        val row = IntArray(source.width) { x ->
            if (x / 4 % 2 == 0) Color.RED else Color.BLUE
        }
        repeat(source.height) { y ->
            source.setPixels(row, 0, row.size, 0, y, row.size, 1)
        }
        sourceFile.outputStream().use { source.compress(Bitmap.CompressFormat.PNG, 100, it) }
        source.recycle()

        try {
            val item = PhotoItem(
                imagePath = Uri.fromFile(sourceFile),
                bound = RectF(0f, 0f, 1f, 1f),
                pointList = arrayListOf(
                    PointF(0f, 0f), PointF(1f, 0f),
                    PointF(1f, 1f), PointF(0f, 1f)
                )
            )
            lateinit var layout: FramePhotoLayout
            lateinit var view: FrameImageView
            instrumentation.runOnMainSync {
                layout = FramePhotoLayout(context, listOf(item)).apply {
                    build(200, 200)
                    measure(
                        View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY)
                    )
                    layout(0, 0, 200, 200)
                }
                view = layout.getChildAt(0) as FrameImageView
            }
            withTimeout(10_000) {
                while (withContext(Dispatchers.Main) { view.image == null }) delay(50)
            }

            assertEquals(1200 to 1200, withContext(Dispatchers.Main) {
                layout.outputDimensions(0.5f)
            })
            val output = withContext(Dispatchers.Main) { layout.createImage(1f) }
            try {
                assertEquals(2400, output.width)
                assertEquals(2400, output.height)
                assertEquals(Color.RED, output.getPixel(1201, 1200))
                assertEquals(Color.BLUE, output.getPixel(1205, 1200))
                assertTrue(withContext(Dispatchers.Main) {
                    layout.outputDimensions(2f).first > output.width
                })
            } finally {
                output.recycle()
            }
        } finally {
            sourceFile.delete()
        }
    }

    @Test
    fun three4kImagesCanBeExportedAtMaximumScale() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val source = Bitmap.createBitmap(3840, 2160, Bitmap.Config.ARGB_8888)
        val files = listOf(Color.RED, Color.GREEN, Color.BLUE).mapIndexed { index, color ->
            File(context.cacheDir, "collage_4k_$index.jpg").also { file ->
                source.eraseColor(color)
                file.outputStream().use { source.compress(Bitmap.CompressFormat.JPEG, 95, it) }
            }
        }
        source.recycle()

        try {
            val items = files.mapIndexed { index, file ->
                PhotoItem(
                    index = index,
                    imagePath = Uri.fromFile(file),
                    bound = RectF(index / 3f, 0f, (index + 1) / 3f, 1f),
                    pointList = arrayListOf(
                        PointF(0f, 0f), PointF(1f, 0f),
                        PointF(1f, 1f), PointF(0f, 1f)
                    )
                )
            }
            lateinit var layout: FramePhotoLayout
            instrumentation.runOnMainSync {
                layout = FramePhotoLayout(context, items).apply {
                    build(240, 240)
                    measure(
                        View.MeasureSpec.makeMeasureSpec(240, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(240, View.MeasureSpec.EXACTLY)
                    )
                    layout(0, 0, 240, 240)
                }
            }
            withTimeout(10_000) {
                while (withContext(Dispatchers.Main) {
                        (0 until layout.childCount).any { index ->
                            (layout.getChildAt(index) as FrameImageView).image == null
                        }
                    }) delay(50)
            }

            withContext(Dispatchers.Main) {
                repeat(layout.childCount) { index ->
                    val view = layout.getChildAt(index) as FrameImageView
                    val image = view.image!!
                    val imageBounds = RectF(0f, 0f, image.width.toFloat(), image.height.toFloat())
                    view.imageMatrix.mapRect(imageBounds)
                    assertEquals(view.width / 2f, imageBounds.centerX(), 1f)
                    assertEquals(view.height / 2f, imageBounds.centerY(), 1f)
                    assertTrue(imageBounds.top <= 0f && imageBounds.bottom >= view.height)
                }
            }

            val sizeAtOne = withContext(Dispatchers.Main) { layout.outputDimensions(1f) }
            assertEquals(3840 to 3840, sizeAtOne)
            val output = withContext(Dispatchers.Main) { layout.createImage(4f) }
            try {
                assertTrue(output.width >= sizeAtOne.first)
                assertEquals(output.width, output.height)
                assertTrue(Color.red(output.getPixel(output.width / 6, output.height / 2)) > 200)
                assertTrue(Color.green(output.getPixel(output.width / 2, output.height / 2)) > 100)
                assertTrue(
                    Color.blue(
                        output.getPixel(
                            output.width * 5 / 6,
                            output.height / 2
                        )
                    ) > 200
                )
            } finally {
                output.recycle()
            }
        } finally {
            files.forEach(File::delete)
        }
    }
}
