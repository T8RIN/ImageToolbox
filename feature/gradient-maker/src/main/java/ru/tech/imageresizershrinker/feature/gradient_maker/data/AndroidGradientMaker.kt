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

package ru.tech.imageresizershrinker.feature.gradient_maker.data

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.feature.gradient_maker.domain.ComposeGradientMaker
import ru.tech.imageresizershrinker.feature.gradient_maker.domain.GradientState

internal class AndroidGradientMaker : ComposeGradientMaker {

    override suspend fun createGradientBitmap(
        integerSize: IntegerSize,
        gradientState: GradientState<ShaderBrush, Size, Color, TileMode, Offset>
    ): Bitmap? = withContext(Dispatchers.IO) {
        val size = Size(
            integerSize.width.coerceAtLeast(1).toFloat(),
            integerSize.height.coerceAtLeast(1).toFloat(),
        )
        gradientState.getBrush(size)?.let { brush ->
            Bitmap.createBitmap(
                size.width.toInt(),
                size.height.toInt(),
                Bitmap.Config.ARGB_8888
            ).apply {
                Canvas(asImageBitmap()).apply {
                    drawRect(
                        paint = Paint().apply {
                            shader = brush.createShader(size)
                        },
                        rect = Rect(offset = Offset.Zero, size = size)
                    )
                }
            }
        }
    }

}