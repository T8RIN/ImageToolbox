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

package com.t8rin.imagetoolbox.core.resources.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb

@Stable
fun Color.compositeOverSafe(
    background: Color
): Color = toSafeSrgb().compositeOver(background.toSafeSrgb())

@Stable
fun Color.toSafeSrgb(
    fallback: Color = Color.Transparent
): Color = if (isSpecified) {
    runCatching { Color(toArgb()) }.getOrDefault(fallback)
} else {
    fallback
}

@Stable
fun Int.toSafeSrgb(
    fallback: Color = Color.Transparent
): Color = runCatching { Color(this) }.getOrDefault(fallback)