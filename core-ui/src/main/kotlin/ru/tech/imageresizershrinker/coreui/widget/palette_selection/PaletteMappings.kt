package ru.tech.imageresizershrinker.coreui.widget.palette_selection

import android.content.Context
import com.t8rin.dynamic.theme.PaletteStyle
import ru.tech.imageresizershrinker.coreui.R

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