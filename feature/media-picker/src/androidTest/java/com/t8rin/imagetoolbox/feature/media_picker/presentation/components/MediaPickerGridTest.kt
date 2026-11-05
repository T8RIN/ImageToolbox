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
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
                listOf(android.view.MotionEvent.ACTION_DOWN, android.view.MotionEvent.ACTION_UP)
                    .forEachIndexed { index, action ->
                        val event = android.view.MotionEvent.obtain(
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

    private fun scroll(columns: Int) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val loader = coil3.SingletonImageLoader.get(context)
        loader.memoryCache?.clear()
        val directory = File(context.cacheDir, "gallery-scroll-test").apply { mkdirs() }
        val bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888).apply {
            eraseColor(0xff4488cc.toInt())
        }
        val bytes = java.io.ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.toByteArray()
        }
        bitmap.recycle()
        try {
            val media = List(2400) { index ->
                val file = File(directory, "$index.png").apply { writeBytes(bytes) }
                Media(
                    id = index.toLong(),
                    label = file.name,
                    uri = file.toURI().toString(),
                    path = file.path,
                    relativePath = "",
                    albumID = 1,
                    albumLabel = "Test",
                    timestamp = 1,
                    mimeType = "image/png",
                    size = bytes.size.toLong()
                )
            }
            val state = MediaState(
                media = media,
                mappedMedia = media.chunked(100).flatMapIndexed { index, group ->
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
                                grid.scrollToItem(0)
                                val sampling = launch {
                                    var previous = withFrameNanos { it }
                                    while (true) {
                                        val current = withFrameNanos { it }
                                        frames += current - previous
                                        previous = current
                                    }
                                }
                                try {
                                    grid.animateScrollBy(4500f, tween(3000, easing = LinearEasing))
                                } finally {
                                    sampling.cancel()
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
                            "Oversized thumbnail: ${image.width}x${image.height}",
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