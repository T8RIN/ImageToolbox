@file:OptIn(ExperimentalTextApi::class)

package ru.tech.imageresizershrinker.presentation.root.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R

fun fontFamilyResource(resId: Int) = FontFamily(
    Font(
        resId = resId,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = resId,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = resId,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = resId,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = resId,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
)

fun Typography(
    fontRes: FontFam = FontFam.Montserrat
): Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),
    titleLarge = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        textAlign = TextAlign.Center
    ),
    bodySmall = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = fontRes.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
)

sealed class FontFam(
    val fontFamily: FontFamily,
    val name: String?
) {
    operator fun component1() = fontFamily
    operator fun component2() = name

    data object Montserrat : FontFam(
        fontFamily = fontFamilyResource(R.font.montserrat_variable),
        name = "Montserrat"
    )

    data object Caveat : FontFam(
        fontFamily = fontFamilyResource(R.font.caveat_variable),
        name = "Caveat"
    )

    data object Default : FontFam(FontFamily.Default, null)

    data object Comfortaa : FontFam(
        fontFamily = fontFamilyResource(R.font.comfortaa_varibale),
        "Comfortaa"
    )

    data object Handjet : FontFam(
        fontFamily = fontFamilyResource(R.font.handjet_varibale),
        "Handjet"
    )

    data object YsabeauSC : FontFam(
        fontFamily = fontFamilyResource(R.font.ysabeau_sc_variable),
        "YsabeauSC"
    )

    data object Jura : FontFam(
        fontFamily = fontFamilyResource(R.font.jura_variable),
        "Jura"
    )

    data object Tektur : FontFam(
        fontFamily = fontFamilyResource(R.font.tektur_variable),
        "Tektur"
    )

    data object Podkova : FontFam(
        fontFamily = fontFamilyResource(R.font.podkova_variable),
        "Podkova"
    )

    companion object {
        fun createFromInt(value: Int): FontFam {
            return when (value) {
                0 -> Montserrat
                1 -> Caveat
                2 -> Comfortaa
                3 -> Handjet
                4 -> Jura
                5 -> Podkova
                6 -> Tektur
                7 -> YsabeauSC
                else -> Default
            }
        }

        val entries = listOf(
            Montserrat,
            Caveat,
            Comfortaa,
            Handjet,
            Jura,
            Podkova,
            Tektur,
            YsabeauSC,
            Default
        )
    }
}
