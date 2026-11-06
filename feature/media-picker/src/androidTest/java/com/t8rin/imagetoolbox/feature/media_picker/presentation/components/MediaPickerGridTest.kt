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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Debug
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MediaPickerGridTest {

    @Test
    fun scrollTenColumns() = scroll(10)

    @Test
    fun scrollTwentyColumns() = scroll(20)

    @Test
    fun replacingSelectionWithoutChangingItsSizeUpdatesTheHeaderAndDragSelection() {
        val first = Media(
            id = 1, label = "first", uri = "file:///missing-first.png", path = "",
            relativePath = "", albumID = 1, albumLabel = "", timestamp = 1,
            mimeType = "image/png", size = 0
        )
        val second = first.copy(id = 2, label = "second", uri = "file:///missing-second.png")
        val selected = mutableStateListOf(first)
        val state = MediaState(
            media = listOf(first, second),
            mappedMedia = listOf(
                MediaItem.Header("header_first", "First", listOf(first)),
                MediaItem.MediaViewItem("first", first),
                MediaItem.MediaViewItem("second", second)
            ),
            isLoading = false
        )
        lateinit var grid: LazyGridState
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    ImageToolboxThemeForPreview(
                        false,
                        mapSettings = { it.copy(galleryColumns = 10) }) {
                        grid = rememberLazyGridState()
                        Box(Modifier.fillMaxSize()) {
                            MediaPickerGrid(
                                albumId = 1, state = state, gridState = grid,
                                isSelectionOfAll = true, selectedMedia = selected,
                                allowMultiple = true, isGalleryMode = false,
                                isButtonVisible = false, useDateTaken = false,
                                onRequestManagePermission = {}, isManagePermissionAllowed = true
                            )
                        }
                    }
                }
            }
            SystemClock.sleep(500)
            scenario.onActivity { selected[0] = second }
            SystemClock.sleep(300)

            fun tap(index: Int) {
                var x = 0f
                var y = 0f
                scenario.onActivity { activity ->
                    val item = grid.layoutInfo.visibleItemsInfo.first { it.index == index }
                    val location = IntArray(2)
                    activity.findViewById<android.view.View>(android.R.id.content)
                        .getLocationOnScreen(location)
                    x = location[0] + item.offset.x + if (index == 0) 60f else item.size.width / 2f
                    y = location[1] + item.offset.y + item.size.height / 2f
                }
                val time = SystemClock.uptimeMillis()
                listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP)
                    .forEachIndexed { index, action ->
                        val event = MotionEvent.obtain(
                            time,
                            time + index * 50L,
                            action,
                            x,
                            y,
                            0
                        )
                        try {
                            scenario.onActivity { it.dispatchTouchEvent(event) }
                        } finally {
                            event.recycle()
                        }
                    }
                SystemClock.sleep(300)
            }

            tap(0)
            scenario.onActivity {
                org.junit.Assert.assertEquals(
                    listOf(second, first),
                    selected.toList()
                )
            }
            tap(0)
            scenario.onActivity { org.junit.Assert.assertEquals(listOf(second), selected.toList()) }
            tap(2)
            scenario.onActivity { assertTrue(selected.isEmpty()) }
        }
    }

    @Test
    fun zoomWithVisibleSelectionKeepsCellsSquareAndSelectionStable() {
        val media = List(1700) { index ->
            Media(
                id = index.toLong(), label = "$index", uri = "file:///missing-$index.png",
                path = "", relativePath = "", albumID = 1, albumLabel = "", timestamp = 1,
                mimeType = "image/png", size = 0
            )
        }
        val state = MediaState(
            media = media,
            mappedMedia = listOf(MediaItem.Header("header_day", "Day", media)) +
                    media.map { MediaItem.MediaViewItem(it.id.toString(), it) },
            isLoading = false
        )
        val columns = mutableIntStateOf(4)
        val selected = mutableStateListOf<Media>()
        lateinit var grid: LazyGridState
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    ImageToolboxThemeForPreview(
                        false,
                        mapSettings = { it.copy(galleryColumns = columns.intValue) }
                    ) {
                        grid = rememberLazyGridState(initialFirstVisibleItemIndex = 801)
                        Box(Modifier.fillMaxSize()) {
                            MediaPickerGrid(
                                albumId = 1, state = state, gridState = grid,
                                isSelectionOfAll = true, selectedMedia = selected,
                                allowMultiple = true, isGalleryMode = true,
                                isButtonVisible = false, useDateTaken = false,
                                onRequestManagePermission = {}, isManagePermissionAllowed = true
                            )
                        }
                    }
                }
            }
            SystemClock.sleep(1000)
            for (selectionSize in listOf(0, 1, 1700)) {
                scenario.onActivity {
                    selected.clear()
                    selected.addAll(
                        if (selectionSize == 1) listOf(media[800]) else media.take(
                            selectionSize
                        )
                    )
                    grid.requestScrollToItem(801)
                }
                SystemClock.sleep(500)
                for (count in (0..20) + (19 downTo 0)) {
                    scenario.onActivity { columns.intValue = count }
                    SystemClock.sleep(100)
                    val idleStart = SystemClock.uptimeMillis()
                    scenario.onActivity {
                        val visible = grid.layoutInfo.visibleItemsInfo.filter { it.index > 0 }
                        assertTrue(
                            "Grid did not settle at $count columns with $selectionSize selected",
                            SystemClock.uptimeMillis() - idleStart < 5_000
                        )
                        visible.forEach {
                            assertTrue(
                                "Non-square item ${it.index}: ${it.size}",
                                kotlin.math.abs(it.size.width - it.size.height) <= 1
                            )
                        }
                        org.junit.Assert.assertEquals(selectionSize, selected.size)
                    }
                }
            }
            scenario.onActivity {
                selected.clear()
                selected.addAll(media.subList(800, 850))
                grid.requestScrollToItem(801)
            }
            SystemClock.sleep(500)
            var activity: ComponentActivity? = null
            scenario.onActivity { activity = it }
            repeat(2) {
                pinch(checkNotNull(activity), 0.3f, 0.06f)
                scenario.onActivity {
                    val columnsAfter = grid.layoutInfo.visibleItemsInfo.maxOf { it.column } + 1
                    org.junit.Assert.assertEquals(20, columnsAfter)
                    assertTrue(
                        "Zoom lost anchor: ${grid.firstVisibleItemIndex}",
                        grid.firstVisibleItemIndex in 782..801
                    )
                    org.junit.Assert.assertEquals(media.subList(800, 850), selected.toList())
                }
                pinch(checkNotNull(activity), 0.06f, 0.3f)
                var position = 0 to 0
                scenario.onActivity {
                    val columnsAfter = grid.layoutInfo.visibleItemsInfo.maxOf { it.column } + 1
                    org.junit.Assert.assertEquals(4, columnsAfter)
                    assertTrue(
                        "Zoom lost anchor: ${grid.firstVisibleItemIndex}",
                        grid.firstVisibleItemIndex in 779..801
                    )
                    position = grid.firstVisibleItemIndex to grid.firstVisibleItemScrollOffset
                    org.junit.Assert.assertEquals(media.subList(800, 850), selected.toList())
                }
                SystemClock.sleep(500)
                scenario.onActivity {
                    org.junit.Assert.assertEquals(
                        position,
                        grid.firstVisibleItemIndex to grid.firstVisibleItemScrollOffset
                    )
                }
            }
        }
    }

    @Test
    fun repeatedPinchLoadsLargePhotosWithoutErrors() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val originalLoader = coil3.SingletonImageLoader.get(context)
        val failures = java.util.Collections.synchronizedList(mutableListOf<String>())
        val loader = originalLoader.newBuilder().eventListenerFactory {
            object : coil3.EventListener() {
                override fun onError(
                    request: coil3.request.ImageRequest,
                    result: coil3.request.ErrorResult
                ) {
                    failures.add("${request.data}: ${result.throwable.stackTraceToString()}")
                }
            }
        }.build()
        val uris = mutableListOf<android.net.Uri>()
        val bitmap = Bitmap.createBitmap(4080, 3072, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawPaint(Paint().apply {
            shader = LinearGradient(
                0f, 0f, 4080f, 3072f,
                0xff4488cc.toInt(), 0xffcc8844.toInt(), Shader.TileMode.CLAMP
            )
        })
        val bytes = java.io.ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)
            it.toByteArray()
        }
        bitmap.recycle()
        try {
            coil3.SingletonImageLoader.setUnsafe(loader)
            loader.memoryCache?.clear()
            val media = List(1700) { index ->
                val values = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "$index.jpg")
                    put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        android.provider.MediaStore.Images.Media.RELATIVE_PATH,
                        "Pictures/GalleryPinchTest/"
                    )
                }
                val uri = checkNotNull(
                    context.contentResolver.insert(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    )
                ).also(uris::add)
                checkNotNull(context.contentResolver.openOutputStream(uri)).use { it.write(bytes) }
                Media(
                    id = index.toLong(), label = "$index.jpg", uri = uri.toString(),
                    path = "", relativePath = "", albumID = 1, albumLabel = "Test",
                    timestamp = 1, mimeType = "image/jpeg", size = bytes.size.toLong()
                )
            }
            val state = MediaState(
                media = media,
                mappedMedia = listOf(MediaItem.Header("header_day", "Day", media)) +
                        media.map { MediaItem.MediaViewItem(it.id.toString(), it) },
                isLoading = false
            )
            lateinit var grid: LazyGridState
            lateinit var activity: ComponentActivity
            val selected = mutableStateListOf<Media>()
            val laidOut = CountDownLatch(1)
            ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
                scenario.onActivity {
                    activity = it
                    it.setContent {
                        ImageToolboxThemeForPreview(
                            false,
                            mapSettings = { settings -> settings.copy(galleryColumns = 4) }
                        ) {
                            grid = rememberLazyGridState(initialFirstVisibleItemIndex = 801)
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .onGloballyPositioned { laidOut.countDown() }) {
                                MediaPickerGrid(
                                    albumId = 1, state = state, gridState = grid,
                                    isSelectionOfAll = true, selectedMedia = selected,
                                    allowMultiple = true, isGalleryMode = false,
                                    isButtonVisible = false, useDateTaken = false,
                                    onRequestManagePermission = {}, isManagePermissionAllowed = true
                                )
                            }
                        }
                    }
                }
                assertTrue(laidOut.await(10, TimeUnit.SECONDS))
                repeat(6) {
                    pinch(activity, 0.4f, 0.02f)
                    pinch(activity, 0.02f, 0.4f)
                    scenario.onActivity {
                        assertTrue(
                            "Grid became empty",
                            grid.layoutInfo.visibleItemsInfo.isNotEmpty()
                        )
                        assertTrue("Zoom lost anchor", grid.firstVisibleItemIndex in 681..801)
                    }
                }
                assertTrue(failures.joinToString("\n"), failures.isEmpty())
            }
        } finally {
            coil3.SingletonImageLoader.setUnsafe(originalLoader)
            uris.forEach { context.contentResolver.delete(it, null, null) }
        }
    }

    private fun pinch(activity: ComponentActivity, start: Float, end: Float) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val view = activity.window.decorView
        val centerX = view.width / 2f
        val centerY = view.height / 2f
        val downTime = SystemClock.uptimeMillis()
        fun send(action: Int, spread: Float, count: Int = 2) {
            val event = MotionEvent.obtain(
                downTime, SystemClock.uptimeMillis(), action, count,
                Array(count) {
                    MotionEvent.PointerProperties().apply {
                        id = it
                        toolType = MotionEvent.TOOL_TYPE_FINGER
                    }
                },
                Array(count) {
                    MotionEvent.PointerCoords().apply {
                        x = centerX + (if (it == 0) -1 else 1) * view.width * spread
                        y = centerY
                        pressure = 1f
                        size = 1f
                    }
                },
                0, 0, 1f, 1f, 0, 0, android.view.InputDevice.SOURCE_TOUCHSCREEN, 0
            )
            try {
                instrumentation.runOnMainSync { activity.dispatchTouchEvent(event) }
            } finally {
                event.recycle()
            }
        }
        send(MotionEvent.ACTION_DOWN, start, 1)
        send(
            MotionEvent.ACTION_POINTER_DOWN or (1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
            start
        )
        repeat(40) { step ->
            SystemClock.sleep(20)
            send(MotionEvent.ACTION_MOVE, start + (end - start) * (step + 1) / 40)
        }
        send(MotionEvent.ACTION_POINTER_UP or (1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT), end)
        send(MotionEvent.ACTION_UP, end, 1)
        SystemClock.sleep(500)
    }

    private fun scroll(columns: Int) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val loader = coil3.SingletonImageLoader.get(context)
        loader.memoryCache?.clear()
        val directory = File(context.cacheDir, "gallery-scroll-test").apply { mkdirs() }
        val bitmap = Bitmap.createBitmap(2048, 1536, Bitmap.Config.ARGB_8888).apply {
            Canvas(this).drawPaint(Paint().apply {
                shader = LinearGradient(
                    0f, 0f, 2048f, 1536f,
                    0xff4488cc.toInt(), 0xffcc8844.toInt(), Shader.TileMode.CLAMP
                )
            })
        }

        fun encode(format: Bitmap.CompressFormat) = java.io.ByteArrayOutputStream().use {
            bitmap.compress(format, 90, it)
            it.toByteArray()
        }

        val fixtures = listOf(
            Triple("jpg", "image/jpeg", encode(Bitmap.CompressFormat.JPEG)),
            Triple("png", "image/png", encode(Bitmap.CompressFormat.PNG)),
            Triple("webp", "image/webp", encode(Bitmap.CompressFormat.WEBP))
        ) + context.assets.list("svg/emotions").orEmpty().take(40).map { name ->
            Triple(
                "svg", "image/svg+xml",
                context.assets.open("svg/emotions/$name").use { it.readBytes() }
            )
        }
        bitmap.recycle()
        try {
            val media = List(2400) { index ->
                val (extension, mime, bytes) = fixtures[index % fixtures.size]
                val file = File(directory, "$index.$extension").apply { writeBytes(bytes) }
                Media(
                    id = index.toLong(),
                    label = file.name,
                    uri = file.toURI().toString(),
                    path = file.path,
                    relativePath = "",
                    albumID = 1,
                    albumLabel = "Test",
                    timestamp = 1,
                    mimeType = mime,
                    size = bytes.size.toLong()
                )
            }
            val state = MediaState(
                media = media,
                mappedMedia = (media.take(700).chunked(100) + listOf(media.drop(700)))
                    .flatMapIndexed { index, group ->
                        listOf(MediaItem.Header("header_$index", "Day $index", group)) +
                                group.map { MediaItem.MediaViewItem(it.id.toString(), it) }
                    },
                isLoading = false
            )
            lateinit var grid: LazyGridState
            lateinit var scope: CoroutineScope
            val selected = mutableStateListOf<Media>()
            val laidOut = CountDownLatch(1)
            ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
                scenario.onActivity { activity ->
                    activity.setContent {
                        ImageToolboxThemeForPreview(
                            false,
                            mapSettings = { it.copy(galleryColumns = columns) }) {
                            grid = rememberLazyGridState()
                            scope = rememberCoroutineScope()
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .onGloballyPositioned { laidOut.countDown() }) {
                                MediaPickerGrid(
                                    albumId = 1,
                                    state = state,
                                    gridState = grid,
                                    isSelectionOfAll = true,
                                    selectedMedia = selected,
                                    allowMultiple = true,
                                    isGalleryMode = false,
                                    isButtonVisible = false,
                                    useDateTaken = false,
                                    onRequestManagePermission = {},
                                    isManagePermissionAllowed = true
                                )
                            }
                        }
                    }
                }
                assertTrue(laidOut.await(10, TimeUnit.SECONDS))
                repeat(3) { pass ->
                    val frames = mutableListOf<Long>()
                    val done = CountDownLatch(1)
                    scenario.onActivity {
                        scope.launch {
                            try {
                                grid.scrollToItem(707)
                                withFrameNanos { }
                                val profile = InstrumentationRegistry.getArguments()
                                    .getString("profileGallery") == "true"
                                if (profile) Debug.startMethodTracingSampling(
                                    File(context.filesDir, "gallery-$columns-$pass.trace").path,
                                    64 * 1024 * 1024, 1000
                                )
                                val sampling = launch {
                                    var previous = withFrameNanos { it }
                                    while (true) {
                                        val current = withFrameNanos { it }
                                        frames += current - previous
                                        previous = current
                                    }
                                }
                                try {
                                    grid.animateScrollBy(2400f, tween(2000, easing = LinearEasing))
                                    grid.animateScrollBy(-2400f, tween(2000, easing = LinearEasing))
                                } finally {
                                    sampling.cancel()
                                    if (profile) Debug.stopMethodTracing()
                                }
                            } finally {
                                done.countDown()
                            }
                        }
                    }
                    assertTrue(done.await(30, TimeUnit.SECONDS))
                    assertTrue(grid.firstVisibleItemIndex > 0)
                    val sorted = frames.sorted()
                    assertTrue(sorted.isNotEmpty())
                    Log.i(
                        "GalleryScroll",
                        "columns=$columns pass=$pass frames=${frames.size} " +
                                "p50ms=${sorted[sorted.size / 2] / 1e6} " +
                                "p95ms=${sorted[sorted.size * 95 / 100] / 1e6}"
                    )
                }
                val cell = grid.layoutInfo.visibleItemsInfo.first {
                    !it.key.toString().startsWith("header_")
                }.size.width
                val cache = requireNotNull(loader.memoryCache)
                val thumbnails = cache.keys.filter { it.key.contains(directory.name) }
                assertTrue(thumbnails.isNotEmpty())
                thumbnails.forEach { key ->
                    cache[key]?.image?.let { image ->
                        assertTrue(
                            "Oversized thumbnail for cell $cell: ${image.width}x${image.height}",
                            image.width <= cell * 2
                        )
                    }
                }
            }
        } finally {
            loader.memoryCache?.clear()
            directory.deleteRecursively()
        }
    }
}