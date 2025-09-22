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

@file:Suppress("MemberVisibilityCanBePrivate")

package com.t8rin.imagetoolbox.core.settings.presentation.model

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import java.io.File

sealed class UiFontFamily(
    val name: String?,
    private val variable: Boolean,
    val type: FontType? = null
) {
    val isVariable: Boolean?
        get() = variable.takeIf {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

    val fontFamily: FontFamily
        get() = type?.let {
            when (it) {
                is FontType.File -> fontFamilyFromFile(file = File(it.path))
                is FontType.Resource -> fontFamilyResource(resId = it.resId)
            }
        } ?: FontFamily.Default

    constructor(
        name: String?,
        variable: Boolean,
        fontRes: Int
    ) : this(
        name = name,
        variable = variable,
        type = FontType.Resource(fontRes)
    )

    constructor(
        name: String?,
        variable: Boolean,
        filePath: String
    ) : this(
        name = name,
        variable = variable,
        type = FontType.File(filePath)
    )

    operator fun component1() = fontFamily
    operator fun component2() = name
    operator fun component3() = isVariable
    operator fun component4() = type

    data object Montserrat : UiFontFamily(
        fontRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.montserrat_variable
        } else R.font.montserrat_regular,
        name = "Montserrat",
        variable = true
    )

    data object Caveat : UiFontFamily(
        fontRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.caveat_variable
        } else R.font.caveat_regular,
        name = "Caveat",
        variable = true
    )

    data object System : UiFontFamily(
        name = null,
        variable = true
    )

    data object Comfortaa : UiFontFamily(
        fontRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.comfortaa_varibale
        } else R.font.comfortaa_regular,
        name = "Comfortaa",
        variable = true
    )

    data object Handjet : UiFontFamily(
        fontRes = R.font.handjet_varibale,
        name = "Handjet",
        variable = true
    )

    data object YsabeauSC : UiFontFamily(
        fontRes = R.font.ysabeau_sc_variable,
        name = "Ysabeau SC",
        variable = true
    )

    data object Jura : UiFontFamily(
        fontRes = R.font.jura_variable,
        name = "Jura",
        variable = true
    )

    data object Tektur : UiFontFamily(
        fontRes = R.font.tektur_variable,
        name = "Tektur",
        variable = true
    )

    data object Podkova : UiFontFamily(
        fontRes = R.font.podkova_variable,
        name = "Podkova",
        variable = true
    )

    data object DejaVu : UiFontFamily(
        fontRes = R.font.dejavu_regular,
        name = "Deja Vu",
        variable = false
    )

    data object BadScript : UiFontFamily(
        fontRes = R.font.bad_script_regular,
        name = "Bad Script",
        variable = false
    )

    data object RuslanDisplay : UiFontFamily(
        fontRes = R.font.ruslan_display_regular,
        name = "Ruslan Display",
        variable = false
    )

    data object Catterdale : UiFontFamily(
        fontRes = R.font.cattedrale_regular,
        name = "Catterdale",
        variable = false
    )

    data object FRM32 : UiFontFamily(
        fontRes = R.font.frm32_regular,
        name = "FRM32",
        variable = false
    )

    data object TokeelyBrookings : UiFontFamily(
        fontRes = R.font.tokeely_brookings_regular,
        name = "Tokeely Brookings",
        variable = false
    )

    data object Nunito : UiFontFamily(
        fontRes = R.font.nunito_variable,
        name = "Nunito",
        variable = true
    )

    data object Nothing : UiFontFamily(
        fontRes = R.font.nothing_font_regular,
        name = "Nothing",
        variable = false
    )

    data object WOPRTweaked : UiFontFamily(
        fontRes = R.font.wopr_tweaked_regular,
        name = "WOPR Tweaked",
        variable = false
    )

    data object AlegreyaSans : UiFontFamily(
        fontRes = R.font.alegreya_sans_regular,
        name = "Alegreya Sans",
        variable = false
    )

    data object MinecraftGnu : UiFontFamily(
        fontRes = R.font.minecraft_gnu_regular,
        name = "Minecraft GNU",
        variable = false
    )

    data object GraniteFixed : UiFontFamily(
        fontRes = R.font.granite_fixed_regular,
        name = "Granite Fixed",
        variable = false
    )

    data object NokiaPixel : UiFontFamily(
        fontRes = R.font.nokia_pixel_regular,
        name = "Nokia Pixel",
        variable = false
    )

    data object Ztivalia : UiFontFamily(
        fontRes = R.font.ztivalia_regular,
        name = "Ztivalia",
        variable = false
    )

    data object Axotrel : UiFontFamily(
        fontRes = R.font.axotrel_regular,
        name = "Axotrel",
        variable = false
    )

    data object LcdOctagon : UiFontFamily(
        fontRes = R.font.lcd_octagon_regular,
        name = "LCD Octagon",
        variable = false
    )

    data object LcdMoving : UiFontFamily(
        fontRes = R.font.lcd_moving_regular,
        name = "LCD Moving",
        variable = false
    )

    data object Unisource : UiFontFamily(
        fontRes = R.font.unisource_regular,
        name = "Unisource",
        variable = false
    )

    class Custom(
        name: String?,
        val filePath: String
    ) : UiFontFamily(
        name = name,
        variable = false,
        filePath = filePath
    ) {
        override fun equals(other: Any?): Boolean {
            if (other !is Custom) return false

            return filePath == other.filePath
        }

        override fun hashCode(): Int {
            return filePath.hashCode()
        }

        override fun toString(): String {
            return "Custom(name = $name, filePath = $filePath)"
        }
    }

    fun asDomain(): DomainFontFamily {
        return when (this) {
            Caveat -> DomainFontFamily.Caveat
            Comfortaa -> DomainFontFamily.Comfortaa
            System -> DomainFontFamily.System
            Handjet -> DomainFontFamily.Handjet
            Jura -> DomainFontFamily.Jura
            Podkova -> DomainFontFamily.Podkova
            Tektur -> DomainFontFamily.Tektur
            YsabeauSC -> DomainFontFamily.YsabeauSC
            Montserrat -> DomainFontFamily.Montserrat
            DejaVu -> DomainFontFamily.DejaVu
            BadScript -> DomainFontFamily.BadScript
            RuslanDisplay -> DomainFontFamily.RuslanDisplay
            Catterdale -> DomainFontFamily.Catterdale
            FRM32 -> DomainFontFamily.FRM32
            TokeelyBrookings -> DomainFontFamily.TokeelyBrookings
            Nunito -> DomainFontFamily.Nunito
            Nothing -> DomainFontFamily.Nothing
            WOPRTweaked -> DomainFontFamily.WOPRTweaked
            AlegreyaSans -> DomainFontFamily.AlegreyaSans
            MinecraftGnu -> DomainFontFamily.MinecraftGnu
            GraniteFixed -> DomainFontFamily.GraniteFixed
            NokiaPixel -> DomainFontFamily.NokiaPixel
            Ztivalia -> DomainFontFamily.Ztivalia
            Axotrel -> DomainFontFamily.Axotrel
            LcdMoving -> DomainFontFamily.LcdMoving
            LcdOctagon -> DomainFontFamily.LcdOctagon
            Unisource -> DomainFontFamily.Unisource
            is Custom -> DomainFontFamily.Custom(name, filePath)
        }
    }

    companion object {

        val entries: List<UiFontFamily>
            @Composable
            get() = defaultEntries + customEntries

        val defaultEntries: List<UiFontFamily> by lazy {
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
                Nunito,
                Nothing,
                WOPRTweaked,
                AlegreyaSans,
                MinecraftGnu,
                GraniteFixed,
                NokiaPixel,
                Ztivalia,
                Axotrel,
                LcdOctagon,
                LcdMoving,
                Unisource,
                System
            ).sortedBy { it.name }
        }

        val customEntries: List<Custom>
            @Composable
            get() {
                val customFonts = LocalSettingsState.current.customFonts

                return remember(customFonts) {
                    derivedStateOf {
                        customFonts.sortedBy { it.name }
                    }
                }.value
            }
    }
}

@Composable
fun FontType?.toUiFont(): UiFontFamily {
    val entries = UiFontFamily.entries

    return remember(entries, this) {
        derivedStateOf {
            when (this) {
                is FontType.File -> UiFontFamily.Custom(
                    name = File(path).nameWithoutExtension.replace("[:\\-_.,]".toRegex(), " "),
                    filePath = path
                )

                is FontType.Resource -> entries.find { it.type == this } ?: UiFontFamily.System
                null -> UiFontFamily.System
            }
        }
    }.value
}

fun FontType?.asUi(): UiFontFamily {
    val entries = UiFontFamily.defaultEntries

    return when (this) {
        is FontType.File -> UiFontFamily.Custom(
            name = File(path).nameWithoutExtension.replace("[:\\-_.,]".toRegex(), " "),
            filePath = path
        )

        is FontType.Resource -> entries.find { it.type == this } ?: UiFontFamily.System
        null -> UiFontFamily.System
    }
}

fun FontType?.asDomain(): DomainFontFamily = this?.asUi()?.asDomain() ?: DomainFontFamily.System

fun DomainFontFamily?.asFontType(): FontType? = this?.toUiFont()?.type

fun DomainFontFamily.toUiFont(): UiFontFamily = when (this) {
    DomainFontFamily.Caveat -> UiFontFamily.Caveat
    DomainFontFamily.Comfortaa -> UiFontFamily.Comfortaa
    DomainFontFamily.System -> UiFontFamily.System
    DomainFontFamily.Handjet -> UiFontFamily.Handjet
    DomainFontFamily.Jura -> UiFontFamily.Jura
    DomainFontFamily.Montserrat -> UiFontFamily.Montserrat
    DomainFontFamily.Podkova -> UiFontFamily.Podkova
    DomainFontFamily.Tektur -> UiFontFamily.Tektur
    DomainFontFamily.YsabeauSC -> UiFontFamily.YsabeauSC
    DomainFontFamily.DejaVu -> UiFontFamily.DejaVu
    DomainFontFamily.BadScript -> UiFontFamily.BadScript
    DomainFontFamily.RuslanDisplay -> UiFontFamily.RuslanDisplay
    DomainFontFamily.Catterdale -> UiFontFamily.Catterdale
    DomainFontFamily.FRM32 -> UiFontFamily.FRM32
    DomainFontFamily.TokeelyBrookings -> UiFontFamily.TokeelyBrookings
    DomainFontFamily.Nunito -> UiFontFamily.Nunito
    DomainFontFamily.Nothing -> UiFontFamily.Nothing
    DomainFontFamily.WOPRTweaked -> UiFontFamily.WOPRTweaked
    DomainFontFamily.AlegreyaSans -> UiFontFamily.AlegreyaSans
    DomainFontFamily.MinecraftGnu -> UiFontFamily.MinecraftGnu
    DomainFontFamily.GraniteFixed -> UiFontFamily.GraniteFixed
    DomainFontFamily.NokiaPixel -> UiFontFamily.NokiaPixel
    DomainFontFamily.Ztivalia -> UiFontFamily.Ztivalia
    DomainFontFamily.Axotrel -> UiFontFamily.Axotrel
    DomainFontFamily.LcdMoving -> UiFontFamily.LcdMoving
    DomainFontFamily.LcdOctagon -> UiFontFamily.LcdOctagon
    DomainFontFamily.Unisource -> UiFontFamily.Unisource
    is DomainFontFamily.Custom -> UiFontFamily.Custom(
        name = name,
        filePath = filePath
    )
}

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

private fun fontFamilyFromFile(file: File) = FontFamily(
    Font(
        file = file,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal
        )
    ),
    Font(
        file = file,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    ),
    Font(
        file = file,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        )
    ),
    Font(
        file = file,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        )
    ),
    Font(
        file = file,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
)