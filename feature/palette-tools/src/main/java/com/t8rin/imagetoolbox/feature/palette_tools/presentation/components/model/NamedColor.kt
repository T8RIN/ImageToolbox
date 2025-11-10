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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model

import androidx.compose.ui.graphics.Color
import com.t8rin.palette.ColorGroup
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteColor

data class NamedColor(
    val color: Color,
    val name: String
)

data class NamedColorGroup(
    val name: String,
    val colors: List<NamedColor>
)

data class NamedPalette(
    val name: String = "",
    val colors: List<NamedColor> = emptyList(),
    val groups: List<NamedColorGroup> = emptyList()
) {
    fun isNotEmpty() = name.isNotBlank() || colors.isNotEmpty() || groups.isNotEmpty()
}

fun NamedPalette.toPalette(): Palette {
    return Palette(
        name = name,
        colors = colors.map {
            PaletteColor(
                color = it.color,
                name = it.name
            )
        },
        groups = groups.map { group ->
            ColorGroup(
                name = group.name,
                colors = group.colors.map {
                    PaletteColor(
                        color = it.color,
                        name = it.name
                    )
                }
            )
        }.distinct(),
    )
}

fun Palette.toNamed(): NamedPalette? {
    if (name.isEmpty() && colors.isEmpty() && groups.isEmpty()) return null

    return NamedPalette(
        name = name,
        colors = colors.map { it.toNamed() }.filter { it.color.alpha > 0f }.distinct(),
        groups = groups.map { group ->
            NamedColorGroup(
                name = group.name,
                colors = group.colors.map { it.toNamed() }.filter { it.color.alpha > 0f }
            )
        }.distinct()
    )
}

fun PaletteColor.toNamed(): NamedColor = NamedColor(
    color = toComposeColor(),
    name = name
)