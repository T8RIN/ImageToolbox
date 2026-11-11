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

package com.t8rin.imagetoolbox.core.games.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.pixelRect

internal fun DrawScope.arcadeCircuit(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.19f)
    for (side in 0..1) {
        val edge = if (side == 0) 7f else width - 7f
        val direction = if (side == 0) 1f else -1f
        repeat(6) { index ->
            val y = top + (height - top) * (index + 0.4f) / 6f
            val reach = (11f + index % 3 * 5f) * direction
            drawLine(color, Offset(edge, y), Offset(edge + reach, y), 1f)
            drawLine(color, Offset(edge + reach, y), Offset(edge + reach, y + 8f), 1f)
            pixelRect(color, edge + reach - 1f, y + 8f, 2f, 2f)
            pixelRect(color.copy(alpha = 0.1f), edge - direction * 2f, y + 17f, 4f, 1f)
        }
    }
}