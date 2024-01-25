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

package ru.tech.imageresizershrinker.core.settings.presentation

import android.os.Build
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.domain.model.FontFam

sealed class UiFontFam(
    val name: String?,
    private val variable: Boolean,
    val fontRes: Int?
) {
    val isVariable: Boolean?
        get() = variable.takeIf {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

    val fontFamily: FontFamily
        get() = fontRes?.let {
            fontFamilyResource(resId = fontRes)
        } ?: FontFamily.Default

    operator fun component1() = fontFamily
    operator fun component2() = name
    operator fun component3() = isVariable
    operator fun component4() = fontRes

    data object Montserrat : UiFontFam(
        fontRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.montserrat_variable
        } else R.font.montserrat_regular,
        name = "Montserrat",
        variable = true
    )

    data object Caveat : UiFontFam(
        fontRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.caveat_variable
        } else R.font.caveat_regular,
        name = "Caveat",
        variable = true
    )

    data object System : UiFontFam(
        fontRes = null,
        name = null,
        variable = true
    )

    data object Comfortaa : UiFontFam(
        fontRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.comfortaa_varibale
        } else R.font.comfortaa_regular,
        name = "Comfortaa",
        variable = true
    )

    data object Handjet : UiFontFam(
        fontRes = R.font.handjet_varibale,
        name = "Handjet",
        variable = true
    )

    data object YsabeauSC : UiFontFam(
        fontRes = R.font.ysabeau_sc_variable,
        name = "Ysabeau SC",
        variable = true
    )

    data object Jura : UiFontFam(
        fontRes = R.font.jura_variable,
        name = "Jura",
        variable = true
    )

    data object Tektur : UiFontFam(
        fontRes = R.font.tektur_variable,
        name = "Tektur",
        variable = true
    )

    data object Podkova : UiFontFam(
        fontRes = R.font.podkova_variable,
        name = "Podkova",
        variable = true
    )

    data object DejaVu : UiFontFam(
        fontRes = R.font.dejavu_regular,
        name = "Deja Vu",
        variable = false
    )

    data object BadScript : UiFontFam(
        fontRes = R.font.bad_script_regular,
        name = "Bad Script",
        variable = false
    )

    data object RuslanDisplay : UiFontFam(
        fontRes = R.font.ruslan_display_regular,
        name = "Ruslan Display",
        variable = false
    )

    data object Catterdale : UiFontFam(
        fontRes = R.font.cattedrale_regular,
        name = "Catterdale",
        variable = false
    )

    data object FRM32 : UiFontFam(
        fontRes = R.font.frm32_regular,
        name = "FRM32",
        variable = false
    )

    data object TokeelyBrookings : UiFontFam(
        fontRes = R.font.tokeely_brookings_regular,
        name = "Tokeely Brookings",
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
            Catterdale -> FontFam.Catterdale
            FRM32 -> FontFam.FRM32
            TokeelyBrookings -> FontFam.TokeelyBrookings
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
                Catterdale,
                FRM32,
                TokeelyBrookings,
                System
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun fontFamilyResource(resId: Int) = FontFamily(
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