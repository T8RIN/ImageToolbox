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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import kotlin.math.cos
import kotlin.math.sin

fun Path.scaleToFitCanvas(
    currentSize: IntegerSize,
    oldSize: IntegerSize,
    onGetScale: (Float, Float) -> Unit = { _, _ -> }
): Path {
    val sx = currentSize.width.toFloat() / oldSize.width
    val sy = currentSize.height.toFloat() / oldSize.height
    onGetScale(sx, sy)
    return android.graphics.Path(this.asAndroidPath()).apply {
        transform(
            Matrix().apply {
                setScale(sx, sy)
            }
        )
    }.asComposePath()
}

fun Offset.rotate(
    angle: Double
): Offset = Offset(
    x = (x * cos(Math.toRadians(angle)) - y * sin(Math.toRadians(angle))).toFloat(),
    y = (x * sin(Math.toRadians(angle)) + y * cos(Math.toRadians(angle))).toFloat()
)

fun Path.moveTo(offset: Offset) = moveTo(offset.x, offset.y)

fun Path.lineTo(offset: Offset) = lineTo(offset.x, offset.y)