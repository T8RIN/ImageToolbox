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

package com.t8rin.imagetoolbox.core.ui.utils.provider

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse

val LocalContainerShape = compositionLocalOf<Shape?> { null }

val LocalContainerColor = compositionLocalOf<Color?> { null }

val SafeLocalContainerColor
    @Composable
    get() = LocalContainerColor.current?.takeOrElse {
        MaterialTheme.colorScheme.surfaceContainerLow
    } ?: MaterialTheme.colorScheme.surfaceContainerLow

@Composable
fun ProvideContainerDefaults(
    shape: Shape? = null,
    color: Color? = null,
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    values = arrayOf(
        LocalContainerShape provides shape,
        LocalContainerColor provides color
    ),
    content = content
)