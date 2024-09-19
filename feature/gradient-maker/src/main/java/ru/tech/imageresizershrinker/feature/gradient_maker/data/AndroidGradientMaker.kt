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
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.safeConfig
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.feature.gradient_maker.domain.GradientMaker
import ru.tech.imageresizershrinker.feature.gradient_maker.domain.GradientState
import javax.inject.Inject

internal class AndroidGradientMaker @Inject constructor(
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder,
    GradientMaker<Bitmap, ShaderBrush, Size, Color, TileMode, Offset> {

    override suspend fun createGradientBitmap(
        integerSize: IntegerSize,
        gradientState: GradientState<ShaderBrush, Size, Color, TileMode, Offset>
    ): Bitmap? = createGradientBitmap(
        src = integerSize.toSize().run {
            Bitmap.createBitmap(
                width.toInt(),
                height.toInt(),
                Bitmap.Config.ARGB_8888
            )
        },
        gradientState = gradientState,
        gradientAlpha = 1f
    )

    override suspend fun createGradientBitmap(
        src: Bitmap,
        gradientState: GradientState<ShaderBrush, Size, Color, TileMode, Offset>,
        gradientAlpha: Float
    ): Bitmap? = withContext(defaultDispatcher) {
        val size = IntegerSize(
            src.width,
            src.height
        ).toSize()
        gradientState.getBrush(size)?.let { brush ->
            src.copy(src.safeConfig, true).apply {
                setHasAlpha(true)

                Canvas(asImageBitmap()).apply {
                    drawImage(asImageBitmap(), Offset.Zero, Paint())
                    drawRect(
                        paint = Paint().apply {
                            shader = brush.createShader(size)
                            alpha = gradientAlpha
                        },
                        rect = Rect(offset = Offset.Zero, size = size)
                    )
                }
            }
        }
    }

    private fun IntegerSize.toSize(): Size = Size(
        width.coerceAtLeast(1).toFloat(),
        height.coerceAtLeast(1).toFloat(),
    )

}