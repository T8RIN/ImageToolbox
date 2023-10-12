package ru.tech.imageresizershrinker.presentation.root.model

import android.os.Build
import androidx.compose.ui.text.font.FontFamily
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.FontFam
import ru.tech.imageresizershrinker.presentation.root.theme.fontFamilyResource

sealed class UiFontFam(
    val fontFamily: FontFamily,
    val name: String?,
    private val variable: Boolean
) {
    val isVariable: Boolean?
        get() = variable.takeIf {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

    operator fun component1() = fontFamily
    operator fun component2() = name
    operator fun component3() = isVariable

    data object Montserrat : UiFontFam(
        fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fontFamilyResource(R.font.montserrat_variable)
        } else fontFamilyResource(R.font.montserrat_regular),
        name = "Montserrat",
        variable = true
    )

    data object Caveat : UiFontFam(
        fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fontFamilyResource(R.font.caveat_variable)
        } else fontFamilyResource(R.font.caveat_regular),
        name = "Caveat",
        variable = true
    )

    data object System : UiFontFam(
        fontFamily = FontFamily.Default,
        name = null,
        variable = true
    )

    data object Comfortaa : UiFontFam(
        fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fontFamilyResource(R.font.comfortaa_varibale)
        } else fontFamilyResource(R.font.comfortaa_regular),
        name = "Comfortaa",
        variable = true
    )

    data object Handjet : UiFontFam(
        fontFamily = fontFamilyResource(R.font.handjet_varibale),
        name = "Handjet",
        variable = true
    )

    data object YsabeauSC : UiFontFam(
        fontFamily = fontFamilyResource(R.font.ysabeau_sc_variable),
        name = "YsabeauSC",
        variable = true
    )

    data object Jura : UiFontFam(
        fontFamily = fontFamilyResource(R.font.jura_variable),
        name = "Jura",
        variable = true
    )

    data object Tektur : UiFontFam(
        fontFamily = fontFamilyResource(R.font.tektur_variable),
        name = "Tektur",
        variable = true
    )

    data object Podkova : UiFontFam(
        fontFamily = fontFamilyResource(R.font.podkova_variable),
        name = "Podkova",
        variable = true
    )

    data object DejaVu : UiFontFam(
        fontFamily = fontFamilyResource(R.font.dejavu_regular),
        name = "DejaVu",
        variable = false
    )

    data object BadScript : UiFontFam(
        fontFamily = fontFamilyResource(R.font.bad_script_regular),
        name = "BadScript",
        variable = false
    )

    data object RuslanDisplay : UiFontFam(
        fontFamily = fontFamilyResource(R.font.ruslan_display_regular),
        name = "RuslanDisplay",
        variable = false
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
            DejaVu -> FontFam.DejaVu
            BadScript -> FontFam.BadScript
            RuslanDisplay -> FontFam.RuslanDisplay
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
                DejaVu,
                BadScript,
                RuslanDisplay,
                System
            )
        }
    }
}