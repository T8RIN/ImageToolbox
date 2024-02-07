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

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified

fun Modifier.blink(
    key: Any? = Unit,
    blinkColor: Color = Color.Unspecified,
    iterations: Int = 2,
    enabled: Boolean = true
) = composed {
    val animatable = remember(key) { Animatable(Color.Transparent) }

    val color = if (blinkColor.isUnspecified) {
        MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f)
    } else blinkColor

    LaunchedEffect(key) {
        kotlinx.coroutines.delay(500L)
        repeat(iterations) {
            animatable.animateTo(color, animationSpec = tween(durationMillis = 1000))
            animatable.animateTo(Color.Transparent, animationSpec = tween(durationMillis = 1000))
        }
    }

    if (enabled) Modifier.drawWithContent {
        drawContent()
        drawRect(animatable.value, size = this.size)
    }
    else Modifier
}