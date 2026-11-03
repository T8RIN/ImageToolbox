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
import com.t8rin.imagetoolbox.core.settings.data.model.toDomainFontFamily
import com.t8rin.imagetoolbox.core.settings.data.model.toFontType
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
        type = DomainFontFamily.Montserrat.toFontType(),
        name = "Montserrat",
        variable = true
    )

    data object Caveat : UiFontFamily(
        type = DomainFontFamily.Caveat.toFontType(),
        name = "Caveat",
        variable = true
    )

    data object System : UiFontFamily(
        name = null,
        variable = true
    )

    data object Comfortaa : UiFontFamily(
        type = DomainFontFamily.Comfortaa.toFontType(),
        name = "Comfortaa",
        variable = true
    )

    data object Handjet : UiFontFamily(
        type = DomainFontFamily.Handjet.toFontType(),
        name = "Handjet",
        variable = true
    )

    data object YsabeauSC : UiFontFamily(
        type = DomainFontFamily.YsabeauSC.toFontType(),
        name = "Ysabeau SC",
        variable = true
    )

    data object Jura : UiFontFamily(
        type = DomainFontFamily.Jura.toFontType(),
        name = "Jura",
        variable = true
    )

    data object Tektur : UiFontFamily(
        type = DomainFontFamily.Tektur.toFontType(),
        name = "Tektur",
        variable = true
    )

    data object Podkova : UiFontFamily(
        type = DomainFontFamily.Podkova.toFontType(),
        name = "Podkova",
        variable = true
    )

    data object DejaVu : UiFontFamily(
        type = DomainFontFamily.DejaVu.toFontType(),
        name = "Deja Vu",
        variable = false
    )

    data object BadScript : UiFontFamily(
        type = DomainFontFamily.BadScript.toFontType(),
        name = "Bad Script",
        variable = false
    )

    data object RuslanDisplay : UiFontFamily(
        type = DomainFontFamily.RuslanDisplay.toFontType(),
        name = "Ruslan Display",
        variable = false
    )

    data object Catterdale : UiFontFamily(
        type = DomainFontFamily.Catterdale.toFontType(),
        name = "Catterdale",
        variable = false
    )

    data object FRM32 : UiFontFamily(
        type = DomainFontFamily.FRM32.toFontType(),
        name = "FRM32",
        variable = false
    )

    data object TokeelyBrookings : UiFontFamily(
        type = DomainFontFamily.TokeelyBrookings.toFontType(),
        name = "Tokeely Brookings",
        variable = false
    )

    data object Nunito : UiFontFamily(
        type = DomainFontFamily.Nunito.toFontType(),
        name = "Nunito",
        variable = true
    )

    data object Nothing : UiFontFamily(
        type = DomainFontFamily.Nothing.toFontType(),
        name = "Nothing",
        variable = false
    )

    data object WOPRTweaked : UiFontFamily(
        type = DomainFontFamily.WOPRTweaked.toFontType(),
        name = "WOPR Tweaked",
        variable = false
    )

    data object AlegreyaSans : UiFontFamily(
        type = DomainFontFamily.AlegreyaSans.toFontType(),
        name = "Alegreya Sans",
        variable = false
    )

    data object MinecraftGnu : UiFontFamily(
        type = DomainFontFamily.MinecraftGnu.toFontType(),
        name = "Minecraft GNU",
        variable = false
    )

    data object GraniteFixed : UiFontFamily(
        type = DomainFontFamily.GraniteFixed.toFontType(),
        name = "Granite Fixed",
        variable = false
    )

    data object NokiaPixel : UiFontFamily(
        type = DomainFontFamily.NokiaPixel.toFontType(),
        name = "Nokia Pixel",
        variable = false
    )

    data object Ztivalia : UiFontFamily(
        type = DomainFontFamily.Ztivalia.toFontType(),
        name = "Ztivalia",
        variable = false
    )

    data object Axotrel : UiFontFamily(
        type = DomainFontFamily.Axotrel.toFontType(),
        name = "Axotrel",
        variable = false
    )

    data object LcdOctagon : UiFontFamily(
        type = DomainFontFamily.LcdOctagon.toFontType(),
        name = "LCD Octagon",
        variable = false
    )

    data object LcdMoving : UiFontFamily(
        type = DomainFontFamily.LcdMoving.toFontType(),
        name = "LCD Moving",
        variable = false
    )

    data object Unisource : UiFontFamily(
        type = DomainFontFamily.Unisource.toFontType(),
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

    fun asDomain(): DomainFontFamily = when (this) {
        is Custom -> DomainFontFamily.Custom(name, filePath)
        else -> type.toDomainFontFamily()
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

fun FontType?.asDomain(): DomainFontFamily = toDomainFontFamily()

fun DomainFontFamily?.asFontType(): FontType? = toFontType()

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