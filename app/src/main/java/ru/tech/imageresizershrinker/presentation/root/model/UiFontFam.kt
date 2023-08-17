package ru.tech.imageresizershrinker.presentation.root.model

import android.os.Build
import androidx.compose.ui.text.font.FontFamily
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.FontFam
import ru.tech.imageresizershrinker.presentation.root.theme.fontFamilyResource

sealed class UiFontFam(
    val fontFamily: FontFamily,
    val name: String?
) {
    operator fun component1() = fontFamily
    operator fun component2() = name

    data object Montserrat : UiFontFam(
        fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fontFamilyResource(R.font.montserrat_variable)
        } else fontFamilyResource(R.font.montserrat_regular),
        name = "Montserrat"
    )

    data object Caveat : UiFontFam(
        fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fontFamilyResource(R.font.caveat_variable)
        } else fontFamilyResource(R.font.caveat_regular),
        name = "Caveat"
    )

    data object System : UiFontFam(FontFamily.Default, null)

    data object Comfortaa : UiFontFam(
        fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fontFamilyResource(R.font.comfortaa_varibale)
        } else fontFamilyResource(R.font.comfortaa_regular),
        "Comfortaa"
    )

    data object Handjet : UiFontFam(
        fontFamily = fontFamilyResource(R.font.handjet_varibale),
        "Handjet"
    )

    data object YsabeauSC : UiFontFam(
        fontFamily = fontFamilyResource(R.font.ysabeau_sc_variable),
        "YsabeauSC"
    )

    data object Jura : UiFontFam(
        fontFamily = fontFamilyResource(R.font.jura_variable),
        "Jura"
    )

    data object Tektur : UiFontFam(
        fontFamily = fontFamilyResource(R.font.tektur_variable),
        "Tektur"
    )

    data object Podkova : UiFontFam(
        fontFamily = fontFamilyResource(R.font.podkova_variable),
        "Podkova"
    )

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
            Montserrat -> FontFam.Montserrat
        }
    }

    companion object {
        val entries by lazy {
            listOf(
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
}