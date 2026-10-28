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

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.applySpotHeal
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.combineSpotHealPaths
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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

    private fun cancelHealing(manual: Boolean) = withDrawer { activity ->
        fun stroke(y: Float) {
            val down = SystemClock.uptimeMillis()
            event(activity, MotionEvent.ACTION_DOWN, .2f, y, down)
            for (step in 1..8) {
                event(activity, MotionEvent.ACTION_MOVE, .2f + step * .075f, y, down)
            }
            event(activity, MotionEvent.ACTION_UP, .8f, y, down)
        }

        stroke(.3f)
        await("Initial drawing did not finish") { activity.ready && activity.readyPaths?.size == 1 }
        val original = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
        val prefix = activity.paths
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val finished = CountDownLatch(1)
        activity.beforeFiltering = {
            entered.countDown()
            try {
                check(release.await(60, TimeUnit.SECONDS))
            } finally {
                finished.countDown()
            }
        }
        try {
            instrumentation.runOnMainSync {
                activity.mode = DrawMode.SpotHeal()
                activity.manualHealing = manual
            }
            instrumentation.waitForIdleSync()
            stroke(.7f)
            if (manual) {
                await("Manual mask did not finish") {
                    activity.ready && activity.paths.lastOrNull()?.isPendingSpotHeal == true
                }
                instrumentation.runOnMainSync {
                    activity.paths = activity.paths.applySpotHeal(DrawMode.SpotHeal())
                }
            }
            assertTrue("Healing did not start", entered.await(5, TimeUnit.SECONDS))
            SystemClock.sleep(300)
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            val cancel = activity.getString(com.t8rin.imagetoolbox.core.resources.R.string.cancel)
            val automation = instrumentation.uiAutomation
            automation.serviceInfo = automation.serviceInfo.apply {
                flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            }
            fun findCancel(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
                node ?: return null
                if (node.isVisibleToUser && node.text?.toString() == cancel) return node
                for (index in 0 until node.childCount) {
                    findCancel(node.getChild(index))?.let { return it }
                }
                return null
            }

            fun cancelButton() = automation.windows.firstNotNullOfOrNull { findCancel(it.root) }
            await("Cancel confirmation did not appear") { cancelButton() != null }
            var button = checkNotNull(cancelButton())
            while (!button.isClickable && button.parent != null) button = button.parent
            assertTrue(
                "Cancel button did not respond",
                button.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            )
            await("Cancel did not remove the active heal") { activity.paths == prefix }
            await("Cancel did not restore the drawing while healing was still running") {
                activity.ready && activity.readyPaths == prefix && original.sameAs(activity.frame)
            }
            SystemClock.sleep(600)
            assertEquals(
                "The native operation finished before cancellation was checked",
                1L,
                release.count
            )
            activity.beforeFiltering = null
            assertEquals(prefix, activity.paths)
            stroke(.5f)
            if (manual) {
                await("New mask did not finish") {
                    activity.ready && activity.paths.lastOrNull()?.isPendingSpotHeal == true
                }
                instrumentation.runOnMainSync {
                    activity.paths = activity.paths.applySpotHeal(DrawMode.SpotHeal())
                }
            }
            await("A new heal could not be applied after cancellation") {
                activity.ready && activity.readyPaths?.size == 2 && activity.frame!!.let {
                    it.getPixel(it.width / 2, it.height / 2) == android.graphics.Color.CYAN
                }
            }
            val healed = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
            release.countDown()
            assertTrue("Cancelled operation did not finish", finished.await(5, TimeUnit.SECONDS))
            SystemClock.sleep(300)
            assertTrue(
                "Cancelled healing appeared after it finished",
                healed.sameAs(activity.frame)
            )
            assertEquals(2, activity.filterCalls)
            healed.recycle()
        } finally {
            activity.beforeFiltering = null
            release.countDown()
            original.recycle()
        }
    }

    @Test
    fun cancellingHealingRestoresTheDrawingBeforeNativeWorkFinishes() = cancelHealing(false)

    @Test
    fun cancellingManualHealingRestoresTheDrawingBeforeNativeWorkFinishes() = cancelHealing(true)

    @Test
    fun highlighterSelfCrossingKeepsUntouchedInkDuringAndAfterTheGesture() =
        withDrawer { activity ->
            instrumentation.runOnMainSync {
                activity.mode = DrawMode.Highlighter
                activity.gradient = GradientPalette.Turbo
                activity.alpha = .4f
            }
            instrumentation.waitForIdleSync()
            val down = SystemClock.uptimeMillis()
            event(activity, MotionEvent.ACTION_DOWN, .1f, .45f, down)
            for (step in 1..16) {
                event(activity, MotionEvent.ACTION_MOVE, .1f + step * .05f, .45f, down)
            }
            SystemClock.sleep(200)
            val first = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
            val x = first.width / 4
            val y = (first.height * .45f).toInt()
            val before = first.getPixel(x, y)
            assertTrue(
                "The first highlighter segment is missing",
                before != android.graphics.Color.WHITE
            )
            for (step in 1..7) {
                event(activity, MotionEvent.ACTION_MOVE, .9f, .45f + step * .05f, down)
            }
            for (step in 1..16) {
                event(
                    activity,
                    MotionEvent.ACTION_MOVE,
                    .9f - step * .05f,
                    .8f - step * .04375f,
                    down
                )
            }
            SystemClock.sleep(200)
            assertEquals(
                "Live crossing recoloured untouched ink",
                before,
                activity.frame!!.getPixel(x, y)
            )
            event(activity, MotionEvent.ACTION_UP, .1f, .1f, down)
            await("Highlighter did not commit") { activity.ready && activity.readyPaths?.size == 1 }
            assertEquals(
                "Committed crossing recoloured untouched ink",
                before,
                activity.frame!!.getPixel(x, y)
            )
            val paths = activity.paths
            val finished = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
            instrumentation.runOnMainSync { activity.paths = emptyList() }
            await("Undo left the highlighter on screen") {
                activity.ready && activity.readyPaths?.isEmpty() == true &&
                        activity.frame!!.getPixel(x, y) == android.graphics.Color.WHITE
            }
            instrumentation.runOnMainSync { activity.paths = paths }
            await("Redo changed the crossing") {
                activity.ready && activity.readyPaths == paths && finished.sameAs(activity.frame)
            }
            first.recycle()
            finished.recycle()
        }

    @Test
    fun translucentBackgroundSurvivesErasingUndoAndColorChanges() = withDrawer { activity ->
        val palette = GradientPalette.Custom(
            listOf(0x80FF0000.toInt(), 0x800000FF.toInt()).map(::ColorModel)
        )
        instrumentation.runOnMainSync {
            activity.image.eraseColor(android.graphics.Color.TRANSPARENT)
            activity.background = Color.Green
            activity.backgroundGradient = GradientFill(palette)
        }
        await("Translucent background did not render") {
            activity.ready && activity.frame?.getPixel(0, 0)?.let {
                android.graphics.Color.alpha(it) == 128
            } == true
        }
        val background = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
        fun stroke() {
            val count = activity.paths.size
            val down = SystemClock.uptimeMillis()
            event(activity, MotionEvent.ACTION_DOWN, .15f, .5f, down)
            for (step in 1..10) {
                event(activity, MotionEvent.ACTION_MOVE, .15f + step * .065f, .5f, down)
            }
            event(activity, MotionEvent.ACTION_UP, .8f, .5f, down)
            await("Stroke did not commit") {
                activity.ready && activity.readyPaths?.size == count + 1
            }
        }
        stroke()
        val painted = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
        val x = painted.width / 2
        val y = painted.height / 2
        assertEquals(android.graphics.Color.RED, painted.getPixel(x, y))
        assertEquals(background.getPixel(0, 0), painted.getPixel(0, 0))
        instrumentation.runOnMainSync { activity.erasing = true }
        instrumentation.waitForIdleSync()
        stroke()
        assertEquals(
            "Eraser changed the background",
            background.getPixel(x, y),
            activity.frame!!.getPixel(x, y)
        )
        val paths = activity.paths
        val erased = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
        instrumentation.runOnMainSync { activity.paths = paths.take(1) }
        await("Undo did not restore the stroke") {
            activity.ready && activity.readyPaths == paths.take(1) && painted.sameAs(activity.frame)
        }
        instrumentation.runOnMainSync { activity.paths = paths }
        await("Redo changed the transparent background") {
            activity.ready && activity.readyPaths == paths && erased.sameAs(activity.frame)
        }
        instrumentation.runOnMainSync {
            activity.backgroundGradient = GradientFill(palette, 180f)
        }
        await("Changing direction did not invalidate the background") {
            activity.ready && activity.frame!!.getPixel(0, 0).let {
                android.graphics.Color.blue(it) > 240 && android.graphics.Color.alpha(it) == 128
            }
        }
        instrumentation.runOnMainSync {
            activity.backgroundGradient = null
            activity.background = Color.Blue
        }
        await("Switching to a solid color left the gradient in the cache") {
            activity.ready && activity.frame!!.let {
                it.getPixel(0, 0) == android.graphics.Color.BLUE && it.getPixel(
                    x,
                    y
                ) == android.graphics.Color.BLUE
            }
        }
        background.recycle()
        painted.recycle()
        erased.recycle()
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
    fun manualHealingKeepsMarksUntilOneCombinedApply() = withDrawer { activity ->
        instrumentation.runOnMainSync {
            activity.mode = DrawMode.SpotHeal()
            activity.manualHealing = true
        }
        instrumentation.waitForIdleSync()
        for ((index, y) in listOf(.3f, .7f).withIndex()) {
            val down = SystemClock.uptimeMillis()
            event(activity, MotionEvent.ACTION_DOWN, .2f, y, down)
            for (step in 1..8) {
                event(activity, MotionEvent.ACTION_MOVE, .2f + step * .075f, y, down)
            }
            event(activity, MotionEvent.ACTION_UP, .8f, y, down)
            await("Manual mark did not finish") {
                activity.ready && activity.paths.size == index + 1 &&
                        activity.frame!!.getPixel(
                            activity.frame!!.width / 2,
                            (activity.frame!!.height * y).toInt()
                        ) != android.graphics.Color.WHITE
            }
            assertEquals("Marking ran the healing filter", 0, activity.filterCalls)
        }
        val marked = activity.paths
        assertTrue(marked.all { it.isPendingSpotHeal })
        instrumentation.runOnMainSync { activity.paths = marked.take(1) }
        await("Undo did not remove the second mask") {
            val frame = activity.frame!!
            activity.ready && frame.getPixel(
                frame.width / 2,
                (frame.height * .7f).toInt()
            ) == android.graphics.Color.WHITE
        }
        val previousWidth = activity.frame!!.width
        instrumentation.runOnMainSync { activity.paths = marked; activity.viewport = 208.dp }
        await("Resized mask was lost") {
            val frame = activity.frame!!
            activity.ready && frame.width != previousWidth && frame.getPixel(
                frame.width / 2,
                (frame.height * .7f).toInt()
            ) != android.graphics.Color.WHITE
        }
        assertEquals("Undo, redo or resizing ran healing", 0, activity.filterCalls)
        val batch = checkNotNull(marked.combineSpotHealPaths(DrawMode.SpotHeal()))
        instrumentation.runOnMainSync { activity.ready = false; activity.paths = listOf(batch) }
        await("Combined healing did not finish") {
            activity.ready && activity.readyPaths == listOf(
                batch
            )
        }
        assertEquals("The batch was processed more than once", 1, activity.filterCalls)
        val healed = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
        for (y in listOf(.3f, .7f)) {
            assertEquals(
                android.graphics.Color.CYAN,
                healed.getPixel(healed.width / 2, (healed.height * y).toInt())
            )
        }
        instrumentation.runOnMainSync { activity.ready = false; activity.paths = emptyList() }
        await("Undo did not finish") { activity.ready && activity.readyPaths?.isEmpty() == true }
        instrumentation.runOnMainSync { activity.ready = false; activity.paths = listOf(batch) }
        await("Redo did not finish") { activity.ready && activity.readyPaths == listOf(batch) }
        assertEquals("Redo recalculated the batch", 1, activity.filterCalls)
        assertTrue("Redo changed the healed image", healed.sameAs(activity.frame))
        healed.recycle()
    }

    @Test
    fun manualEraserOnlyCutsTheMaskDuringAndAfterDrawing() = withDrawer { activity ->
        fun pixel(x: Float, y: Float = .5f) = activity.frame!!.let {
            it.getPixel((it.width * x).toInt(), (it.height * y).toInt())
        }

        fun stroke() {
            val count = activity.paths.size
            val down = SystemClock.uptimeMillis()
            event(activity, MotionEvent.ACTION_DOWN, .2f, .5f, down)
            for (step in 1..8) event(
                activity, MotionEvent.ACTION_MOVE, .2f + step * .075f, .5f, down
            )
            event(activity, MotionEvent.ACTION_UP, .8f, .5f, down)
            await("Stroke did not finish") { activity.ready && activity.paths.size == count + 1 }
        }
        instrumentation.runOnMainSync { activity.alpha = .4f }
        instrumentation.waitForIdleSync()
        stroke()
        val original = pixel(.5f)
        instrumentation.runOnMainSync {
            activity.mode = DrawMode.SpotHeal()
            activity.manualHealing = true
            activity.width = 140.pt
        }
        instrumentation.waitForIdleSync()
        stroke()
        await("Mask did not cover the drawing") { pixel(.5f) != original }
        val marked = activity.paths
        val maskColor = pixel(.3f)
        instrumentation.runOnMainSync {
            activity.erasing = true
            activity.width = 40.pt
        }
        instrumentation.waitForIdleSync()
        val down = SystemClock.uptimeMillis()
        event(activity, MotionEvent.ACTION_DOWN, .4f, .5f, down)
        for (step in 1..8) event(
            activity, MotionEvent.ACTION_MOVE, .4f + step * .025f, .5f, down
        )
        await("Live eraser did not reveal the original drawing") { pixel(.5f) == original }
        assertEquals(maskColor, pixel(.3f))
        event(activity, MotionEvent.ACTION_UP, .6f, .5f, down)
        await("Mask eraser did not commit") {
            activity.ready && activity.paths.size == 3 && pixel(.5f) == original
        }
        val erased = activity.paths
        assertTrue(erased.last().isPendingSpotHeal && erased.last().isErasing)
        assertEquals("Erasing invoked healing", 0, activity.filterCalls)
        instrumentation.runOnMainSync { activity.paths = marked }
        await("Undo did not restore the mask") { pixel(.5f) == maskColor }
        instrumentation.runOnMainSync { activity.paths = erased }
        await("Redo erased the drawing or left the mask") { pixel(.5f) == original }
        val applied = erased.applySpotHeal(DrawMode.SpotHeal())
        instrumentation.runOnMainSync { activity.ready = false; activity.paths = applied }
        await("Healing did not apply") { activity.ready && activity.readyPaths == applied }
        assertEquals(original, pixel(.5f))
        assertEquals(android.graphics.Color.CYAN, pixel(.3f))
        assertEquals(1, activity.filterCalls)
    }

    @Test
    fun disablingManualHealingCancelsTheActiveMaskStroke() = withDrawer { activity ->
        instrumentation.runOnMainSync {
            activity.mode = DrawMode.SpotHeal()
            activity.manualHealing = true
        }
        instrumentation.waitForIdleSync()
        val down = SystemClock.uptimeMillis()
        event(activity, MotionEvent.ACTION_DOWN, .2f, .5f, down)
        for (step in 1..8) event(
            activity, MotionEvent.ACTION_MOVE, .2f + step * .075f, .5f, down
        )
        await("Live mask did not appear") {
            activity.frame!!.let {
                it.getPixel(
                    it.width / 2,
                    it.height / 2
                )
            } != android.graphics.Color.WHITE
        }
        instrumentation.runOnMainSync { activity.manualHealing = false }
        instrumentation.waitForIdleSync()
        event(activity, MotionEvent.ACTION_UP, .8f, .5f, down)
        await("Discarded mask remained on screen") {
            activity.frame!!.let {
                it.getPixel(
                    it.width / 2,
                    it.height / 2
                )
            } == android.graphics.Color.WHITE
        }
        assertTrue(activity.paths.isEmpty())
        assertEquals("Disabling manual mode processed the active mask", 0, activity.filterCalls)
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
            await("Stroke did not commit") { activity.ready && activity.readyPaths?.size == count + 1 }
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
            await("Redo after rotation did not finish") {
                activity.ready && activity.readyPaths == paths && rotated.sameAs(activity.frame)
            }
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
    fun changingGradientSettingsKeepsCommittedStrokesAndUndoRedoUnchanged() =
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
            instrumentation.runOnMainSync {
                activity.gradient = GradientPalette.Custom(
                    listOf(0x40FF4500, 0xCF39DDFF.toInt(), 0x805522FF.toInt()).map(::ColorModel)
                )
                activity.gradientLength = .1f
                activity.gradientMirrored = true
            }
            SystemClock.sleep(200)
            assertTrue("Changing length repainted existing ink", first.sameAs(activity.frame))
            stroke(.7f)
            val paths = activity.paths
            assertEquals(listOf(.25f, .1f), paths.map { it.gradientLength })
            assertEquals(listOf(false, true), paths.map { it.isGradientMirrored })
            assertEquals(
                paths.map { it.isGradientMirrored },
                paths.map { it.toUiPathPaint().isGradientMirrored }
            )
            assertEquals(
                paths.map { it.gradientLength },
                paths.map { it.toUiPathPaint().gradientLength })
            val after = activity.frame!!.copy(Bitmap.Config.ARGB_8888, false)
            instrumentation.runOnMainSync { activity.paths = paths.take(1) }
            await("Undo did not restore the first length") {
                activity.ready && activity.readyPaths == paths.take(1) &&
                        first.sameAs(activity.frame)
            }
            instrumentation.runOnMainSync { activity.paths = paths }
            await("Redo did not restore both lengths") {
                activity.ready && activity.readyPaths == paths && after.sameAs(activity.frame)
            }
            first.recycle()
            after.recycle()
        }

}
