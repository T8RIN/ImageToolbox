/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.util.fastRoundToInt

@Composable
fun Dp.toSp(): TextUnit = with(LocalDensity.current) { toSp() }

@Composable
fun Dp.toPx(): Float = with(LocalDensity.current) { toPx() }

@Composable
fun Dp.roundToPx(): Int = toPx().fastRoundToInt()

@Composable
fun TextUnit.toPx(): Float = with(LocalDensity.current) { toPx() }

@Composable
fun TextUnit.roundToPx(): Int = toPx().fastRoundToInt()

@Composable
fun Int.toDp(): Dp = with(LocalDensity.current) { toDp() }

@Composable
fun Int.toSp(): TextUnit = toDp().toSp()

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { toDp() }

@Composable
fun Float.toSp(): TextUnit = toDp().toSp()