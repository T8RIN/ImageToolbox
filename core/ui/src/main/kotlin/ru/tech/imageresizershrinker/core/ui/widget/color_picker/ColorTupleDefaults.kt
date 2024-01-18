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

package ru.tech.imageresizershrinker.core.ui.widget.color_picker

import androidx.compose.ui.graphics.Color
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.calculateSecondaryColor
import com.t8rin.dynamic.theme.calculateSurfaceColor
import com.t8rin.dynamic.theme.calculateTertiaryColor
import ru.tech.imageresizershrinker.core.ui.theme.toColor

object ColorTupleDefaults {
    val defaultColorTuples by lazy {
        listOf(
            Color(0xFFf8130d),
            Color(0xFF7a000b),
            Color(0xFF8a3a00),
            Color(0xFFff7900),
            Color(0xFFfcf721),
            Color(0xFF88dd20),
            Color(0xFF16B16E),
            Color(0xFF01a0a3),
            Color(0xFF005FFF),
            Color(0xFFfa64e1),
            Color(0xFFd7036a),
            Color(0xFFdb94fe),
            Color(0xFF7b2bec),
            Color(0xFF022b6d),
            Color(0xFFFFFFFF),
            Color(0xFF000000),
        ).map {
            ColorTuple(
                primary = it,
                secondary = it.calculateSecondaryColor().toColor(),
                tertiary = it.calculateTertiaryColor().toColor(),
                surface = it.calculateSurfaceColor().toColor()
            )
        }
    }
}