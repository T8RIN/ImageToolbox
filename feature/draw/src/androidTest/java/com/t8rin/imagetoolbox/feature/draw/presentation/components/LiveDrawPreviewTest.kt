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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.retainedComponent
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.core.utils.initAppContext
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.DrawRenderCache
import kotlinx.coroutines.delay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

private class LiveDrawTestState(context: ComponentContext) : ComponentContext by context {
    val cache = DrawRenderCache()
    val image = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        .apply { eraseColor(android.graphics.Color.WHITE) }.asImageBitmap()
    var paths by mutableStateOf(emptyList<UiPathPaint>())
    val filterCalls = AtomicInteger()
}

class LiveDrawTestActivity : ComponentActivity() {
    private lateinit var state: LiveDrawTestState
    val image get() = state.image.asAndroidBitmap()
    val cache get() = state.cache
    val filterCalls get() = state.filterCalls.get()
    var mode: DrawMode by mutableStateOf(DrawMode.Pen)
    var gradient: GradientPalette? by mutableStateOf(null)
    var gradientLength by mutableStateOf(1f)
    var background by mutableStateOf(Color.Transparent)
    var softness by mutableStateOf(0.pt)
    var alpha by mutableStateOf(1f)
    var width by mutableStateOf(65.pt)
    var viewport by mutableStateOf(256.dp)
    var paths: List<UiPathPaint>
        get() = state.paths
        set(value) {
            state.paths = value
        }

    @Volatile
    var frame: Bitmap? = null

    @Volatile
    var bounds = Rect.Zero

    @Volatile
    var ready = false

    @Volatile
    var readyPaths: List<UiPathPaint>? = null

    @Volatile
    var observeFrame: ((Bitmap) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppContext()
        state = retainedComponent { LiveDrawTestState(it) }
        viewport =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 208.dp else 256.dp
        current = this
        setContent {
            val settings = SettingsState.Default.toUiState().copy(
                magnifierEnabled = false, drawBitmapBorder = false
            )
            CompositionLocalProvider(LocalSettingsState provides settings) {
                MaterialTheme {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val displayedPaths = paths
                        BitmapDrawer(
                            imageBitmap = state.image,
                            renderCache = state.cache,
                            paths = displayedPaths,
                            onAddPath = { paths = paths + it },
                            onRequestFiltering = { source, _ ->
                                state.filterCalls.incrementAndGet()
                                delay(80)
                                Bitmap.createBitmap(
                                    source.width,
                                    source.height,
                                    Bitmap.Config.ARGB_8888
                                )
                                    .apply { eraseColor(android.graphics.Color.CYAN) }
                            },
                            strokeWidth = width,
                            brushSoftness = softness,
                            drawColor = Color.Red.copy(alpha = alpha),
                            gradientPalette = gradient,
                            gradientLength = gradientLength,
                            isEraserOn = false,
                            drawMode = mode,
                            drawPathMode = DrawPathMode.Free,
                            drawLineStyle = DrawLineStyle.None,
                            modifier = Modifier
                                .size(viewport)
                                .onGloballyPositioned { bounds = it.boundsInWindow() },
                            backgroundColor = background,
                            panEnabled = false,
                            helperGridParams = HelperGridParams(),
                            onDraw = { frame = it; observeFrame?.invoke(it) },
                            onRenderReady = {
                                ready = it
                                readyPaths = displayedPaths.takeIf { _ -> it }
                            }
                        )
                    }
                }
            }
        }
    }

    companion object {
        @Volatile
        var current: LiveDrawTestActivity? = null
    }
}

@RunWith(AndroidJUnit4::class)
class LiveDrawPreviewTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()

    private fun await(message: String, condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 10_000
        while (!condition() && SystemClock.uptimeMillis() < deadline) SystemClock.sleep(20)
        assertTrue(message, condition())
    }

    private fun withDrawer(test: (LiveDrawTestActivity) -> Unit) {
        val activity = instrumentation.startActivitySync(
            Intent(instrumentation.targetContext, LiveDrawTestActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ) as LiveDrawTestActivity
        try {
            await("Drawer did not initialize") { activity.ready && activity.bounds.width > 0 && activity.frame != null }
            SystemClock.sleep(300)
            test(activity)
        } finally {
            instrumentation.runOnMainSync { LiveDrawTestActivity.current?.finish() }
        }
    }

    private fun event(activity: LiveDrawTestActivity, action: Int, x: Float, y: Float, down: Long) {
        val bounds = activity.bounds
        val event = MotionEvent.obtain(
            down, SystemClock.uptimeMillis(), action,
            bounds.left + bounds.width * x, bounds.top + bounds.height * y, 0
        )
        instrumentation.sendPointerSync(event)
        event.recycle()
        SystemClock.sleep(40)
    }

    @Test
    fun effectFollowsTheFingerAndRemainsVisibleWhileHeld() = withDrawer { activity ->
        val modes = listOf(
            DrawMode.PathEffect.PrivacyBlur(),
            DrawMode.PathEffect.Pixelation(),
            DrawMode.PathEffect.Custom(),
            DrawMode.Image(
                imageData = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888)
                    .apply { eraseColor(android.graphics.Color.CYAN) }
            )
        )
        for (mode in modes) {
            instrumentation.runOnMainSync { activity.paths = emptyList(); activity.mode = mode }
            SystemClock.sleep(500)
            val sampleOffset = if (mode is DrawMode.Image) .02f else 0f
            val down = SystemClock.uptimeMillis()
            event(activity, MotionEvent.ACTION_DOWN, .15f, .5f, down)
            try {
                for (step in 1..8) {
                    event(activity, MotionEvent.ACTION_MOVE, .15f + step * .08f, .5f, down)
                    val expectedX = .15f + (step - 1) * .08f + sampleOffset
                    await("$mode is missing while the finger is down at step $step") {
                        val frame = activity.frame!!
                        val x = (frame.width * expectedX).toInt()
                        val y = (frame.height * (.5f + sampleOffset)).toInt()
                        val offsets = if (mode is DrawMode.Image) -2..2 else 0..0
                        offsets.any { dx ->
                            offsets.any { dy ->
                                frame.getPixel(x + dx, y + dy) == android.graphics.Color.CYAN
                            }
                        }
                    }
                }
                SystemClock.sleep(150)
                val held = activity.frame!!
                assertEquals(
                    "Held filter disappeared", android.graphics.Color.CYAN,
                    held.getPixel(held.width / 2, (held.height * (.5f + sampleOffset)).toInt())
                )
            } finally {
                event(activity, MotionEvent.ACTION_UP, .79f, .5f, down)
            }
            await("Filter did not finish") { activity.ready && activity.paths.size == 1 }
        }
    }

    @Test
    fun drawingAnotherTranslucentGradientDoesNotDuplicateThePreviousStroke() =
        withDrawer { activity ->
            instrumentation.runOnMainSync {
                activity.mode = DrawMode.Pen
                activity.gradient = GradientPalette.SoftRainbow
                activity.alpha = .4f
                activity.softness = 8.pt
            }
            SystemClock.sleep(300)
            fun stroke(y: Float, inspect: (() -> Unit)? = null) {
                val down = SystemClock.uptimeMillis()
                event(activity, MotionEvent.ACTION_DOWN, .15f, y, down)
                for (step in 1..10) {
                    event(activity, MotionEvent.ACTION_MOVE, .15f + step * .065f, y, down)
                    inspect?.invoke()
                }
                event(activity, MotionEvent.ACTION_UP, .8f, y, down)
            }
            stroke(.25f)
            await("First gradient did not commit") { activity.ready && activity.paths.size == 1 }
            val before = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
            fun checkFirstStroke() {
                val frame = activity.frame!!
                for (y in before.height / 10 until before.height * 4 / 10) {
                    for (x in 0 until before.width) {
                        assertEquals(
                            "A completed gradient changed while drawing another one at $x/$y",
                            before.getPixel(x, y), frame.getPixel(x, y)
                        )
                    }
                }
            }
            stroke(.7f, ::checkFirstStroke)
            await("Second gradient did not commit") { activity.ready && activity.paths.size == 2 }
            checkFirstStroke()
            before.recycle()
        }

    @Test
    fun switchingBrushAndWidthNeverRepaintsACompletedGradient() = withDrawer { activity ->
        instrumentation.runOnMainSync {
            activity.mode = DrawMode.Pen
            activity.gradient = GradientPalette.SoftRainbow
            activity.alpha = .4f
            activity.softness = 8.pt
        }
        SystemClock.sleep(300)
        val down = SystemClock.uptimeMillis()
        event(activity, MotionEvent.ACTION_DOWN, .15f, .25f, down)
        for (step in 1..12) event(activity, MotionEvent.ACTION_MOVE, .15f + step * .05f, .25f, down)
        event(activity, MotionEvent.ACTION_UP, .75f, .25f, down)
        await("Gradient did not commit") { activity.ready && activity.paths.size == 1 }
        val before = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
        val differences = java.util.concurrent.CopyOnWriteArrayList<String>()
        activity.observeFrame = { frame ->
            if (frame.width == before.width && frame.height == before.height && !frame.sameAs(before)) {
                differences += "${activity.mode}, width ${activity.width}"
            }
        }
        for (mode in DrawMode.entries) {
            instrumentation.runOnMainSync { activity.mode = mode; activity.width = 130.pt }
            SystemClock.sleep(150)
            instrumentation.runOnMainSync { activity.width = 20.pt }
            SystemClock.sleep(150)
        }
        activity.observeFrame = null
        assertTrue("Changing tools repainted completed ink: $differences", differences.isEmpty())
        activity.observeFrame = { frame ->
            for (y in frame.height / 2 until frame.height * 9 / 10 step 5) {
                for (x in frame.width / 10 until frame.width * 9 / 10 step 5) {
                    if (frame.getPixel(x, y) != android.graphics.Color.WHITE) {
                        differences += "Ghost after resizing to ${frame.width} at $x/$y"
                    }
                }
            }
        }
        for (size in listOf(208.dp, 256.dp, 224.dp, 256.dp)) {
            instrumentation.runOnMainSync { activity.viewport = size }
            SystemClock.sleep(300)
        }
        activity.observeFrame = null
        assertTrue("Resizing displayed a stale stroke: $differences", differences.isEmpty())
        await("Resized history did not settle") { activity.ready }
        assertTrue("Restoring the viewport changed the drawing", before.sameAs(activity.frame))
        before.recycle()
    }

    @Test
    fun changingBrushSettingsCannotKeepTheDiscardedLiveStrokeOnScreen() = withDrawer { activity ->
        instrumentation.runOnMainSync {
            activity.mode = DrawMode.Pen
            activity.gradient = GradientPalette.SoftRainbow
        }
        SystemClock.sleep(300)
        val down = SystemClock.uptimeMillis()
        event(activity, MotionEvent.ACTION_DOWN, .15f, .5f, down)
        try {
            for (step in 1..6) event(
                activity,
                MotionEvent.ACTION_MOVE,
                .15f + step * .08f,
                .5f,
                down
            )
            await("Live gradient was not drawn") {
                val frame = activity.frame!!
                frame.getPixel(frame.width / 3, frame.height / 2) != android.graphics.Color.WHITE
            }
            instrumentation.runOnMainSync { activity.softness = 12.pt }
            await("Discarded live path remained as a ghost after changing brush settings") {
                val frame = activity.frame!!
                frame.getPixel(frame.width / 3, frame.height / 2) == android.graphics.Color.WHITE
            }
        } finally {
            event(activity, MotionEvent.ACTION_UP, .63f, .5f, down)
        }
    }

    @Test
    fun rotationRetainsHealingAndUndoRedoHistory() = withDrawer { initial ->
        var activity = initial
        fun stroke(mode: DrawMode, y: Float) {
            instrumentation.runOnMainSync {
                activity.mode = mode
                activity.gradient = GradientPalette.SoftRainbow.takeIf { mode is DrawMode.Pen }
            }
            SystemClock.sleep(250)
            val count = activity.paths.size
            val down = SystemClock.uptimeMillis()
            event(activity, MotionEvent.ACTION_DOWN, .15f, y, down)
            for (step in 1..10) event(
                activity,
                MotionEvent.ACTION_MOVE,
                .15f + step * .065f,
                y,
                down
            )
            event(activity, MotionEvent.ACTION_UP, .8f, y, down)
            await("Stroke did not commit") { activity.ready && activity.paths.size == count + 1 }
        }
        stroke(DrawMode.Pen, .45f)
        stroke(DrawMode.SpotHeal(), .5f)
        stroke(DrawMode.Pen, .7f)
        val paths = activity.paths
        val original = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
        val calls = activity.filterCalls
        assertEquals(1, calls)
        val cache = activity.cache
        for (orientation in listOf(
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )) {
            val previous = activity
            instrumentation.runOnMainSync { previous.requestedOrientation = orientation }
            await("Rotation did not recreate the drawer") {
                val current = LiveDrawTestActivity.current
                current != null && current !== previous && current.ready && current.frame != null
            }
            activity = LiveDrawTestActivity.current!!
            assertSame("Activity recreation replaced the component cache", cache, activity.cache)
            assertEquals("Rotation recalculated SpotHeal", calls, activity.filterCalls)
            assertEquals(paths, activity.paths)
            val rotated = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
            instrumentation.runOnMainSync { activity.ready = false; activity.paths = paths.take(1) }
            await("Undo after rotation did not finish") {
                activity.ready && activity.readyPaths == paths.take(
                    1
                )
            }
            instrumentation.runOnMainSync { activity.ready = false; activity.paths = paths }
            await("Redo after rotation did not finish") { activity.ready && activity.readyPaths == paths }
            assertEquals("Undo/redo recalculated SpotHeal", calls, activity.filterCalls)
            if (!rotated.sameAs(activity.frame)) {
                for ((name, frame) in listOf("rotated" to rotated, "redo" to activity.frame!!)) {
                    File(activity.getExternalFilesDir(null), "rotation-$name.png").outputStream()
                        .use {
                            frame.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                }
            }
            assertTrue(
                "Redo changed the rotated drawing: ${rotated.width} vs ${activity.frame?.width}",
                rotated.sameAs(activity.frame)
            )
            rotated.recycle()
        }
        assertTrue("Returning to portrait changed the drawing", original.sameAs(activity.frame))
        original.recycle()
    }

    @Test
    fun preparingAPreviewAtSourceResolutionDoesNotChangeTheOriginal() = withDrawer { activity ->
        val original = activity.image.copy(Bitmap.Config.ARGB_8888, false)
        val generation = activity.image.generationId
        instrumentation.runOnMainSync {
            activity.ready = false
            activity.viewport =
                (activity.image.width / activity.resources.displayMetrics.density).dp
            activity.background = Color.Green
        }
        await("Preview at source resolution did not settle") {
            activity.ready && activity.frame?.width == original.width &&
                    activity.frame?.getPixel(0, 0) == android.graphics.Color.GREEN
        }
        assertEquals(generation, activity.image.generationId)
        assertTrue(
            "Preparing the preview modified the source image",
            original.sameAs(activity.image)
        )
        original.recycle()
    }


    @Test
    fun changingTheGradientLengthKeepsCommittedStrokesAndUndoRedoUnchanged() =
        withDrawer { activity ->
            fun stroke(y: Float) {
                val count = activity.paths.size
                val down = SystemClock.uptimeMillis()
                event(activity, MotionEvent.ACTION_DOWN, .15f, y, down)
                for (step in 1..10) event(
                    activity,
                    MotionEvent.ACTION_MOVE,
                    .15f + step * .065f,
                    y,
                    down
                )
                event(activity, MotionEvent.ACTION_UP, .8f, y, down)
                await("Gradient did not commit") { activity.ready && activity.readyPaths?.size == count + 1 }
            }
            instrumentation.runOnMainSync {
                activity.gradient = GradientPalette.SoftRainbow
                activity.gradientLength = .25f
            }
            SystemClock.sleep(200)
            stroke(.25f)
            val first = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
            instrumentation.runOnMainSync { activity.gradientLength = 4f }
            SystemClock.sleep(200)
            assertTrue("Changing length repainted existing ink", first.sameAs(activity.frame))
            stroke(.7f)
            val paths = activity.paths
            assertEquals(listOf(.25f, 4f), paths.map { it.gradientLength })
            assertEquals(
                paths.map { it.gradientLength },
                paths.map { it.toUiPathPaint().gradientLength })
            val after = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
            instrumentation.runOnMainSync { activity.paths = paths.take(1) }
            await("Undo did not restore the first length") {
                activity.ready && activity.readyPaths == paths.take(
                    1
                )
            }
            assertTrue(first.sameAs(activity.frame))
            instrumentation.runOnMainSync { activity.paths = paths }
            await("Redo did not restore both lengths") { activity.ready && activity.readyPaths == paths }
            assertTrue(after.sameAs(activity.frame))
            first.recycle()
            after.recycle()
        }

}
