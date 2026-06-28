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

package com.t8rin.imagetoolbox.core.resources.utils.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.core.resources.utils.toSafeSrgb

@Composable
fun animateColorAsState(
    targetValue: Color,
    animationSpec: AnimationSpec<Color> = spring(),
    label: String = "ColorAnimation",
    finishedListener: ((Color) -> Unit)? = null
): State<Color> = animateValueAsState(
    targetValue = targetValue,
    typeConverter = SafeColorVectorConverter,
    animationSpec = animationSpec,
    label = label,
    finishedListener = finishedListener
)

private val SafeColorVectorConverter = TwoWayConverter<Color, AnimationVector4D>(
    convertToVector = { color ->
        color.toSafeSrgb().run {
            AnimationVector4D(
                v1 = red,
                v2 = green,
                v3 = blue,
                v4 = alpha
            )
        }
    },
    convertFromVector = {
        Color(
            red = it.v1.toColorComponent(),
            green = it.v2.toColorComponent(),
            blue = it.v3.toColorComponent(),
            alpha = it.v4.toColorComponent()
        )
    }
)

private fun Float.toColorComponent(): Float = if (isFinite()) coerceIn(0f, 1f) else 0f