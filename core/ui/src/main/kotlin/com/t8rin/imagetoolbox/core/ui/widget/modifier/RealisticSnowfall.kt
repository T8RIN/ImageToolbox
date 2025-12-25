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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.idapgroup.snowfall.snowfall
import com.idapgroup.snowfall.types.FlakeType
import kotlin.random.Random

fun Modifier.realisticSnowfall(
    enabled: Boolean = true
): Modifier = this.composed {
    if (enabled) {
        Modifier.snowfall(
            type = FlakeType.Custom(flakes),
            color = MaterialTheme.colorScheme.primary
        )
    } else Modifier
}

private val flakes = List(100) {
    val size = (40 * Random.nextDouble(0.3, 1.0)).toFloat()
    object : Painter() {
        override val intrinsicSize: Size = Size(size, size)

        override fun DrawScope.onDraw() {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color.White,
                        Color.Transparent,
                    )
                )
            )
        }
    }
}