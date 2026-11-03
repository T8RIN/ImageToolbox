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

package com.t8rin.imagetoolbox.core.settings.data.model

import android.os.Build
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import java.io.File

fun FontType?.toDomainFontFamily(): DomainFontFamily = when (this) {
    is FontType.File -> DomainFontFamily.Custom(
        name = File(path).nameWithoutExtension.replace("[:\\-_.,]".toRegex(), " "),
        filePath = path
    )

    is FontType.Resource -> bundledFonts.entries.firstOrNull { it.value == resId }?.key
        ?: DomainFontFamily.System

    null -> DomainFontFamily.System
}

fun DomainFontFamily?.toFontType(): FontType? = when (this) {
    is DomainFontFamily.Custom -> FontType.File(filePath)
    else -> bundledFonts[this]?.let { FontType.Resource(it) }
}

private val bundledFonts: Map<DomainFontFamily, Int> by lazy {
    mapOf(
        DomainFontFamily.Montserrat to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.montserrat_variable
        } else R.font.montserrat_regular,
        DomainFontFamily.Caveat to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.caveat_variable
        } else R.font.caveat_regular,
        DomainFontFamily.Comfortaa to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.comfortaa_varibale
        } else R.font.comfortaa_regular,
        DomainFontFamily.Handjet to R.font.handjet_varibale,
        DomainFontFamily.YsabeauSC to R.font.ysabeau_sc_variable,
        DomainFontFamily.Jura to R.font.jura_variable,
        DomainFontFamily.Tektur to R.font.tektur_variable,
        DomainFontFamily.Podkova to R.font.podkova_variable,
        DomainFontFamily.DejaVu to R.font.dejavu_regular,
        DomainFontFamily.BadScript to R.font.bad_script_regular,
        DomainFontFamily.RuslanDisplay to R.font.ruslan_display_regular,
        DomainFontFamily.Catterdale to R.font.cattedrale_regular,
        DomainFontFamily.FRM32 to R.font.frm32_regular,
        DomainFontFamily.TokeelyBrookings to R.font.tokeely_brookings_regular,
        DomainFontFamily.Nunito to R.font.nunito_variable,
        DomainFontFamily.Nothing to R.font.nothing_font_regular,
        DomainFontFamily.WOPRTweaked to R.font.wopr_tweaked_regular,
        DomainFontFamily.AlegreyaSans to R.font.alegreya_sans_regular,
        DomainFontFamily.MinecraftGnu to R.font.minecraft_gnu_regular,
        DomainFontFamily.GraniteFixed to R.font.granite_fixed_regular,
        DomainFontFamily.NokiaPixel to R.font.nokia_pixel_regular,
        DomainFontFamily.Ztivalia to R.font.ztivalia_regular,
        DomainFontFamily.Axotrel to R.font.axotrel_regular,
        DomainFontFamily.LcdOctagon to R.font.lcd_octagon_regular,
        DomainFontFamily.LcdMoving to R.font.lcd_moving_regular,
        DomainFontFamily.Unisource to R.font.unisource_regular
    )
}
