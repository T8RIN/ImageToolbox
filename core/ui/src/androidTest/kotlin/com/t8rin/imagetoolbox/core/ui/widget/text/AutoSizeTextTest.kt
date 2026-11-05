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

package com.t8rin.imagetoolbox.core.ui.widget.text

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class AutoSizeTextTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun tightHeightAndSameLengthTextChangesSettleWithoutLayoutLoop() {
        val text = mutableStateOf("WWWW")
        val width = mutableStateOf(32.dp)
        val measures = AtomicInteger()
        compose.setContent {
            MaterialTheme {
                AutoSizeText(
                    text = text.value,
                    style = TextStyle(fontSize = 16.sp, lineHeight = 20.sp),
                    modifier = Modifier
                        .size(width.value, 12.dp)
                        .layout { measurable, constraints ->
                            measures.incrementAndGet()
                            val placeable = measurable.measure(constraints)
                            layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                        }
                )
            }
        }
        compose.runOnIdle {
            assertTrue("Initial measurements: ${measures.get()}", measures.get() < 8)
            measures.set(0)
            text.value = "iiii"
            width.value = 16.dp
        }
        compose.runOnIdle {
            assertTrue("Updated measurements: ${measures.get()}", measures.get() in 1..7)
        }
    }
}