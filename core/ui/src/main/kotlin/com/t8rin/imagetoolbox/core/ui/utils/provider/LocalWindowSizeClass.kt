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

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> { error("SizeClass not present") }

fun ComponentActivity.setContentWithWindowSizeClass(
    content: @Composable () -> Unit
) = setContent {
    LocalWindowSizeClass.ProvidesValue(
        value = calculateWindowSizeClass(this),
        content = content
    )
}
