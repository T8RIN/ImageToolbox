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

package com.t8rin.imagetoolbox.core.ui.utils.state

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import io.github.alexzhirkevich.qrose.QrCodePainter
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test

class ComposeMemoizationTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun unchangedKeysReuseDerivedStateAndQrOptions() {
        val key = mutableIntStateOf(1)
        val source = mutableIntStateOf(10)
        val redraw = mutableIntStateOf(0)
        var calculations = 0
        var optionBuilds = 0
        var observedRedraw = 0
        var observedValue = 0
        lateinit var painter: QrCodePainter
        lateinit var initialPainter: QrCodePainter
        compose.setContent {
            val currentRedraw = redraw.intValue
            val value = derivedValueOf(key.intValue) {
                calculations++
                source.intValue + key.intValue
            }
            val currentPainter =
                rememberQrCodePainter("Image Toolbox", key.intValue) { optionBuilds++ }
            SideEffect {
                observedRedraw = currentRedraw
                observedValue = value
                painter = currentPainter
            }
        }
        compose.runOnIdle {
            initialPainter = painter
            assertEquals(11, observedValue)
            assertEquals(1, calculations)
            assertEquals(1, optionBuilds)
            redraw.intValue++
        }
        compose.runOnIdle {
            assertEquals(1, observedRedraw)
            assertSame(initialPainter, painter)
            assertEquals(1, calculations)
            assertEquals(1, optionBuilds)
            source.intValue++
        }
        compose.runOnIdle {
            assertEquals(12, observedValue)
            assertEquals(2, calculations)
            assertSame(initialPainter, painter)
            key.intValue++
        }
        compose.runOnIdle {
            assertEquals(13, observedValue)
            assertEquals(3, calculations)
            assertEquals(2, optionBuilds)
            assertNotSame(initialPainter, painter)
        }
    }
}