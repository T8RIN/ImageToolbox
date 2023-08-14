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

val Comfortaa = FontFamily(
    Font(
        resId = R.font.comfortaa_varibale,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.comfortaa_varibale,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.comfortaa_varibale,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.comfortaa_varibale,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.comfortaa_varibale,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
)

val Handjet = FontFamily(
    Font(
        resId = R.font.handjet_varibale,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.handjet_varibale,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.handjet_varibale,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.handjet_varibale,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.handjet_varibale,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
)

val Montserrat = FontFamily(
    Font(
        resId = R.font.montserrat_variable,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.montserrat_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.montserrat_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.montserrat_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.montserrat_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
)

val Caveat = FontFamily(
    Font(
        resId = R.font.caveat_variable,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.caveat_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.caveat_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.caveat_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = R.font.caveat_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
)

fun Typography(fontRes: FontRes = FontRes.Montserrat): Typography = Typography(
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

sealed class FontRes(
    val fontFamily: FontFamily?,
    val name: String?
) {
    operator fun component1() = fontFamily
    operator fun component2() = name

    data object Montserrat :
        FontRes(ru.tech.imageresizershrinker.presentation.root.theme.Montserrat, "Montserrat")

    data object Caveat :
        FontRes(ru.tech.imageresizershrinker.presentation.root.theme.Caveat, "Caveat")

    data object Default : FontRes(null, null)

    data object Comfortaa :
        FontRes(ru.tech.imageresizershrinker.presentation.root.theme.Comfortaa, "Comfortaa")

    data object Handjet :
        FontRes(ru.tech.imageresizershrinker.presentation.root.theme.Handjet, "Handjet")

    companion object {
        fun createFromInt(value: Int): FontRes {
            return when (value) {
                0 -> Montserrat
                1 -> Caveat
                2 -> Comfortaa
                3 -> Handjet
                else -> Default
            }
        }

        val entries = listOf(
            Montserrat,
            Caveat,
            Comfortaa,
            Handjet,
            Default
        )
    }
}
