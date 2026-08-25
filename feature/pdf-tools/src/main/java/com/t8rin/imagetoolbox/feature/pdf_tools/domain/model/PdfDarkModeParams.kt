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

package com.t8rin.imagetoolbox.feature.pdf_tools.domain.model

import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode

data class PdfDarkModeParams(
    val theme: PdfDarkModeTheme = PdfDarkModeTheme.Warm,
    val customColor: Int = requireNotNull(PdfDarkModeTheme.Graphite.backgroundColor),
    val customBlendMode: BlendingMode = BlendingMode.Screen
) {
    val overlayColor: Int?
        get() = when (theme) {
            PdfDarkModeTheme.Negative -> null
            PdfDarkModeTheme.Custom -> customColor
            else -> theme.backgroundColor
        }

    val overlayBlendMode: BlendingMode
        get() = if (theme == PdfDarkModeTheme.Custom) {
            customBlendMode
        } else {
            BlendingMode.Screen
        }

    companion object {
        val SupportedBlendModes = listOf(
            BlendingMode.Screen,
            BlendingMode.Multiply,
            BlendingMode.Overlay,
            BlendingMode.Darken,
            BlendingMode.Lighten,
            BlendingMode.Softlight,
            BlendingMode.Difference,
            BlendingMode.Exclusion,
            BlendingMode.Hue,
            BlendingMode.Saturation,
            BlendingMode.Color,
            BlendingMode.Luminosity
        )
    }
}
