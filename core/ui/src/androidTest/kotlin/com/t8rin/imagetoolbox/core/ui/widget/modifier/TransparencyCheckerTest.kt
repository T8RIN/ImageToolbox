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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.filters.SdkSuppress
import com.t8rin.imagetoolbox.core.ui.theme.blend
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.roundToInt

@SdkSuppress(minSdkVersion = 29)
class TransparencyCheckerTest {

    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun matchesIndividualSquaresAcrossDensitiesColorsAndSizes() {
        val cases = listOf(1f, 2.625f, 3f).flatMap { density ->
            listOf(
                lightColorScheme(),
                darkColorScheme(),
                lightColorScheme(surface = Color(0x8055CC99)),
                lightColorScheme(
                    surface = Color(0.2f, 0.6f, 0.4f, colorSpace = ColorSpaces.DisplayP3)
                )
            ).flatMap { colors ->
                listOf(10.dp to 10.dp, 5.5.dp to 7.25.dp, 0.dp to 10.dp).map { (width, height) ->
                    Case(density, colors, width, height)
                }
            }
        }
        val current = mutableStateOf(cases.first())
        val reference = mutableStateOf(false)
        var bounds = Rect.Zero
        compose.setContent {
            val case = current.value
            CompositionLocalProvider(LocalDensity provides Density(case.density)) {
                Box(Modifier.padding(top = 100.dp)) {
                    Box(
                        Modifier
                            .size(137.dp, 91.dp)
                            .background(Color.Magenta)
                            .onGloballyPositioned { bounds = it.boundsInWindow() }
                            .then(
                                if (reference.value) Modifier.drawBehind {
                                    val width = case.width.toPx()
                                    val height = case.height.toPx()
                                    if (width <= 0f || height <= 0f) return@drawBehind
                                    val surface = case.colors.surface
                                    val elevated = case.colors.surfaceColorAtElevation(20.dp)
                                    drawRect(elevated.blend(surface, 0.5f))
                                    for (y in 0..(size.height / height).toInt()) {
                                        for (x in 0..(size.width / width).toInt()) {
                                            drawRect(
                                                color = if ((x + y) % 2 == 1) elevated else surface,
                                                topLeft = Offset(x * width, y * height),
                                                size = Size(width, height)
                                            )
                                        }
                                    }
                                } else Modifier.transparencyChecker(
                                    case.colors,
                                    case.width,
                                    case.height
                                )
                            )
                    )
                }
            }
        }
        for ((index, case) in cases.withIndex()) {
            compose.runOnIdle {
                current.value = case
                reference.value = false
            }
            compose.waitForIdle()
            val actual = captureWindow()
            try {
                compose.runOnIdle { reference.value = true }
                compose.waitForIdle()
                val expected = captureWindow()
                try {
                    var differences = 0
                    val right =
                        bounds.right.roundToInt() + ceil(case.width.value * case.density).toInt()
                    val bottom =
                        bounds.bottom.roundToInt() + ceil(case.height.value * case.density).toInt()
                    for (y in bounds.top.roundToInt() until bottom.coerceAtMost(actual.height)) {
                        for (x in bounds.left.roundToInt() until right.coerceAtMost(actual.width)) {
                            if (actual.getPixel(x, y) != expected.getPixel(x, y)) differences++
                        }
                    }
                    assertEquals("Changed pixels for case $index", 0, differences)
                } finally {
                    expected.recycle()
                }
            } finally {
                actual.recycle()
            }
        }
    }

    private fun captureWindow(): Bitmap {
        val window = compose.activity.window
        val committed = CountDownLatch(1)
        compose.runOnUiThread {
            window.decorView.viewTreeObserver.registerFrameCommitCallback(committed::countDown)
            window.decorView.invalidate()
        }
        check(committed.await(5, TimeUnit.SECONDS)) { "Frame was not committed" }
        val bitmap = Bitmap.createBitmap(
            window.decorView.width,
            window.decorView.height,
            Bitmap.Config.ARGB_8888
        )
        val copied = CountDownLatch(1)
        var result = PixelCopy.ERROR_UNKNOWN
        PixelCopy.request(window, bitmap, {
            result = it
            copied.countDown()
        }, Handler(Looper.getMainLooper()))
        check(copied.await(5, TimeUnit.SECONDS)) { "Frame was not copied" }
        assertEquals(PixelCopy.SUCCESS, result)
        return bitmap
    }

    private data class Case(
        val density: Float,
        val colors: ColorScheme,
        val width: Dp,
        val height: Dp
    )
}