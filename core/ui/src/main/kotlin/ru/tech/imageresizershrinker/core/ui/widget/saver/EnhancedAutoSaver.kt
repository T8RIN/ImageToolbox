/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.saver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


private const val dpPrefix = "d9a15bcf68209cbe92f48224d62b17970cdcb301270a80b92f16fdae63919de1"
private const val stateDpPrefix = "STATE$dpPrefix"
private const val colorPrefix = "8dea69e6f230070aa4eb11c8b416d680f1d1df60dd95ca91df35703a336e1ee7"
private const val stateColorPrefix = "STATE$colorPrefix"

val EnhancedAutoSaver = Saver<Any?, Any>(
    save = {
        when (it) {
            is Dp -> {
                dpPrefix + "/${it.value}"
            }

            is Color -> {
                colorPrefix + "/${it.toArgb()}"
            }

            is MutableState<*> -> {
                when (val value = it.value) {
                    is Dp -> {
                        stateDpPrefix + "/${value}"
                    }

                    is Color -> {
                        stateColorPrefix + "/${value.toArgb()}"
                    }

                    else -> it
                }
            }

            else -> it
        }
    },
    restore = {
        if (it is String) {
            val floatValue = it.split("/")
                .getOrNull(1)
                ?.toFloatOrNull() ?: 0f

            if (it.startsWith(dpPrefix)) {
                floatValue.dp
            } else if (it.startsWith(stateDpPrefix)) {
                mutableStateOf(floatValue.dp)
            } else if (it.startsWith(colorPrefix)) {
                Color(floatValue.toInt())
            } else if (it.startsWith(stateColorPrefix)) {
                mutableStateOf(Color(floatValue.toInt()))
            } else it
        } else it
    }
)

@Composable
fun EnhancedAutoSaverInit() {
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(initialized) {
        if (!initialized) {
            runCatching {
                Class.forName("androidx.compose.runtime.saveable.SaverKt")
                    .getDeclaredField("AutoSaver").apply {
                        isAccessible.let { accessible ->
                            isAccessible = true
                            set(this, EnhancedAutoSaver)
                            isAccessible = accessible
                        }
                    }
            }
            initialized = true
        }
    }
}