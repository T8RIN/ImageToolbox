package ru.tech.imageresizershrinker.presentation.root.model

import androidx.compose.ui.text.font.FontFamily
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.FontFam
import ru.tech.imageresizershrinker.presentation.root.theme.fontFamilyResource

open class UiFontFam private constructor(
    val fontFamily: FontFamily,
    val name: String?
) {
    operator fun component1() = fontFamily
    operator fun component2() = name

    fun asDomain(): FontFam {
        return when (this) {
            Caveat -> FontFam.Caveat
            Comfortaa -> FontFam.Comfortaa
            System -> FontFam.System
            Handjet -> FontFam.Handjet
            Jura -> FontFam.Jura
            Podkova -> FontFam.Podkova
            Tektur -> FontFam.Tektur
            YsabeauSC -> FontFam.YsabeauSC
            else -> FontFam.Montserrat
        }
    }

    companion object {
        val Montserrat = UiFontFam(
            fontFamily = fontFamilyResource(R.font.montserrat_variable),
            name = "Montserrat"
        )

        val Caveat = UiFontFam(
            fontFamily = fontFamilyResource(R.font.caveat_variable),
            name = "Caveat"
        )

        val System = UiFontFam(FontFamily.Default, null)

        val Comfortaa = UiFontFam(
            fontFamily = fontFamilyResource(R.font.comfortaa_varibale),
            "Comfortaa"
        )

        val Handjet = UiFontFam(
            fontFamily = fontFamilyResource(R.font.handjet_varibale),
            "Handjet"
        )

        val YsabeauSC = UiFontFam(
            fontFamily = fontFamilyResource(R.font.ysabeau_sc_variable),
            "YsabeauSC"
        )

        val Jura = UiFontFam(
            fontFamily = fontFamilyResource(R.font.jura_variable),
            "Jura"
        )

        val Tektur = UiFontFam(
            fontFamily = fontFamilyResource(R.font.tektur_variable),
            "Tektur"
        )

        val Podkova = UiFontFam(
            fontFamily = fontFamilyResource(R.font.podkova_variable),
            "Podkova"
        )

        val entries = listOf(
            Montserrat,
            Caveat,
            Comfortaa,
            Handjet,
            Jura,
            Podkova,
            Tektur,
            YsabeauSC,
            System
        )
    }
}