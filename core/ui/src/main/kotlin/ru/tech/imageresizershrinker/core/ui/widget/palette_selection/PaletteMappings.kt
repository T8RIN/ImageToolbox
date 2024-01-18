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

package ru.tech.imageresizershrinker.core.ui.widget.palette_selection

import android.content.Context
import com.t8rin.dynamic.theme.PaletteStyle
import ru.tech.imageresizershrinker.core.resources.R

fun PaletteStyle.getTitle(context: Context): String {
    return when (this) {
        PaletteStyle.TonalSpot -> context.getString(R.string.tonal_spot)
        PaletteStyle.Neutral -> context.getString(R.string.neutral)
        PaletteStyle.Vibrant -> context.getString(R.string.vibrant)
        PaletteStyle.Expressive -> context.getString(R.string.expressive)
        PaletteStyle.Rainbow -> context.getString(R.string.rainbow)
        PaletteStyle.FruitSalad -> context.getString(R.string.fruit_salad)
        PaletteStyle.Monochrome -> context.getString(R.string.monochrome)
        PaletteStyle.Fidelity -> context.getString(R.string.fidelity)
        PaletteStyle.Content -> context.getString(R.string.content)
    }
}

fun PaletteStyle.getSubtitle(context: Context): String {
    return when (this) {
        PaletteStyle.TonalSpot -> context.getString(R.string.tonal_spot_sub)
        PaletteStyle.Neutral -> context.getString(R.string.neutral_sub)
        PaletteStyle.Vibrant -> context.getString(R.string.vibrant_sub)
        PaletteStyle.Expressive -> context.getString(R.string.playful_scheme)
        PaletteStyle.Rainbow -> context.getString(R.string.playful_scheme)
        PaletteStyle.FruitSalad -> context.getString(R.string.playful_scheme)
        PaletteStyle.Monochrome -> context.getString(R.string.monochrome_sub)
        PaletteStyle.Fidelity -> context.getString(R.string.fidelity_sub)
        PaletteStyle.Content -> context.getString(R.string.content_sub)
    }
}