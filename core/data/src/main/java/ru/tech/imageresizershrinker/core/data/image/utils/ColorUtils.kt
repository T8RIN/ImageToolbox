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

@file:Suppress("NOTHING_TO_INLINE")

package ru.tech.imageresizershrinker.core.data.image.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import ru.tech.imageresizershrinker.core.domain.model.ColorModel

object ColorUtils {

    inline fun ColorModel.toColor() = Color(colorInt)

    inline fun Color.toModel() = ColorModel(toArgb())

    inline val ColorModel.red: Float
        get() = toColor().red

    inline val ColorModel.green: Float
        get() = toColor().green

    inline val ColorModel.blue: Float
        get() = toColor().blue

    inline val ColorModel.alpha: Float
        get() = toColor().alpha

    inline fun Color.toAbgr() = run {
        Color(
            red = alpha,
            green = blue,
            blue = green,
            alpha = red,
            colorSpace = colorSpace
        ).toArgb()
    }
}