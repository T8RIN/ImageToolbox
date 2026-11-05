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

import android.os.SystemClock
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.fastscroller.FastScrollerState
import com.t8rin.fastscroller.VerticalFastScroller
import com.t8rin.fastscroller.rememberFastScrollerState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MediaFastScrollerTest {

    @Test
    fun widerTouchTargetKeepsThumbThinAndDoesNotCaptureTouchesOutsideIt() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val density = instrumentation.targetContext.resources.displayMetrics.density
        lateinit var grid: LazyGridState
        lateinit var scroller: FastScrollerState
        lateinit var scope: CoroutineScope
        var bounds = Rect.Zero
        val laidOut = CountDownLatch(1)
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    ImageToolboxThemeForPreview(false) {
                        grid = rememberLazyGridState()
                        scroller = rememberFastScrollerState()
                        scope = rememberCoroutineScope()
                        Box(Modifier
                            .fillMaxSize()
                            .background(Color.Black)) {
                            LazyVerticalGrid(GridCells.Fixed(20), state = grid) {
                                items(3_000) { Box(Modifier.aspectRatio(1f)) }
                            }
                            VerticalFastScroller(
                                state = grid,
                                fastScrollerState = scroller,
                                thickness = 4.dp,
                                minThumbHeight = 64.dp,
                                thumbColor = Color.Magenta,
                                scrollByItemIndex = true,
                                autoHide = true,
                                thumbTouchWidth = 48.dp,
                                modifier = Modifier
                                    .align(AbsoluteAlignment.TopRight)
                                    .fillMaxHeight()
                                    .onGloballyPositioned {
                                        bounds = it.boundsInWindow()
                                        laidOut.countDown()
                                    }
                            )
                        }
                    }
                }
            }
            assertTrue(laidOut.await(10, TimeUnit.SECONDS))
            val scrolled = CountDownLatch(1)
            scenario.onActivity {
                scope.launch {
                    grid.animateScrollBy(300f, tween(200))
                    scrolled.countDown()
                }
            }
            assertTrue(scrolled.await(5, TimeUnit.SECONDS))
            SystemClock.sleep(250)
            var center = 0f
            scenario.onActivity {
                assertTrue(scroller.isVisible)
                center = bounds.top + scroller.thumbCenter
                assertEquals(48f * density, bounds.width, 1f)
            }
            val time = SystemClock.uptimeMillis()
            fun touch(action: Int, x: Float, y: Float) {
                val event = MotionEvent.obtain(time, SystemClock.uptimeMillis(), action, x, y, 0)
                try {
                    scenario.onActivity { it.dispatchTouchEvent(event) }
                } finally {
                    event.recycle()
                }
            }
            touch(MotionEvent.ACTION_DOWN, bounds.right - 56 * density, center)
            scenario.onActivity { assertFalse(scroller.isDragging) }
            touch(MotionEvent.ACTION_CANCEL, bounds.right - 56 * density, center)
            touch(MotionEvent.ACTION_DOWN, bounds.right - 40 * density, center + 100 * density)
            scenario.onActivity { assertFalse(scroller.isDragging) }
            touch(MotionEvent.ACTION_CANCEL, bounds.right - 40 * density, center + 100 * density)

            val before = grid.firstVisibleItemIndex
            touch(MotionEvent.ACTION_DOWN, bounds.right - 40 * density, center)
            scenario.onActivity { assertTrue(scroller.isDragging) }
            val screenshot = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
            try {
                val coloredPixels = (bounds.left.roundToInt() until bounds.right.roundToInt())
                    .count {
                        screenshot.getPixel(
                            it,
                            center.roundToInt()
                        ) == android.graphics.Color.MAGENTA
                    }
                assertEquals((4 * density).roundToInt(), coloredPixels)
            } finally {
                screenshot.recycle()
            }
            touch(MotionEvent.ACTION_MOVE, bounds.right - 40 * density, center + 200 * density)
            SystemClock.sleep(150)
            scenario.onActivity { assertTrue(grid.firstVisibleItemIndex > before + 100) }
            touch(MotionEvent.ACTION_UP, bounds.right - 40 * density, center + 200 * density)
            scenario.onActivity { assertFalse(scroller.isDragging) }
            SystemClock.sleep(1_200)
            scenario.onActivity {
                assertFalse(scroller.isVisible)
                center = bounds.top + scroller.thumbCenter
            }
            touch(MotionEvent.ACTION_DOWN, bounds.right - 40 * density, center)
            scenario.onActivity { assertFalse(scroller.isDragging) }
            touch(MotionEvent.ACTION_CANCEL, bounds.right - 40 * density, center)
        }
    }

}