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

@file:Suppress("SpellCheckingInspection")

package ru.tech.imageresizershrinker.core.settings.domain.model

sealed class DomainFontFamily(val ordinal: Int) {
    data object Montserrat : DomainFontFamily(1)
    data object Caveat : DomainFontFamily(2)
    data object Comfortaa : DomainFontFamily(3)
    data object Handjet : DomainFontFamily(4)
    data object YsabeauSC : DomainFontFamily(5)
    data object Jura : DomainFontFamily(6)
    data object Podkova : DomainFontFamily(7)
    data object Tektur : DomainFontFamily(8)
    data object DejaVu : DomainFontFamily(9)
    data object BadScript : DomainFontFamily(10)
    data object RuslanDisplay : DomainFontFamily(11)
    data object Catterdale : DomainFontFamily(12)
    data object FRM32 : DomainFontFamily(13)
    data object TokeelyBrookings : DomainFontFamily(14)
    data object Nunito : DomainFontFamily(15)
    data object Nothing : DomainFontFamily(16)
    data object WOPRTweaked : DomainFontFamily(17)
    data object AlegreyaSans : DomainFontFamily(18)
    data object MinecraftGnu : DomainFontFamily(19)
    data object GraniteFixed : DomainFontFamily(20)
    data object NokiaPixel : DomainFontFamily(21)
    data object Ztivalia : DomainFontFamily(22)
    data object Axotrel : DomainFontFamily(23)
    data object LcdOctagon : DomainFontFamily(24)
    data object LcdMoving : DomainFontFamily(25)
    data object Unisource : DomainFontFamily(26)
    data object System : DomainFontFamily(0)

    class Custom(
        val name: String?,
        val filePath: String
    ) : DomainFontFamily(-1) {
        override fun asString(): String = "$name:$filePath"

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

    open fun asString(): String = ordinal.toString()

    companion object {
        fun fromString(string: String?): DomainFontFamily? {
            val int = string?.toIntOrNull()

            val family = when (int) {
                0 -> System
                1 -> Montserrat
                2 -> Caveat
                3 -> Comfortaa
                4 -> Handjet
                5 -> YsabeauSC
                6 -> Jura
                7 -> Podkova
                8 -> Tektur
                9 -> DejaVu
                10 -> BadScript
                11 -> RuslanDisplay
                12 -> Catterdale
                13 -> FRM32
                14 -> TokeelyBrookings
                15 -> Nunito
                16 -> Nothing
                17 -> WOPRTweaked
                18 -> AlegreyaSans
                19 -> MinecraftGnu
                20 -> GraniteFixed
                21 -> NokiaPixel
                22 -> Ztivalia
                23 -> Axotrel
                24 -> LcdOctagon
                25 -> LcdMoving
                26 -> Unisource
                else -> null
            }

            return family ?: string?.split(":")?.let {
                Custom(
                    name = it[0],
                    filePath = it[1]
                )
            }
        }
    }
}

sealed interface FontType {
    data class Resource(val resId: Int) : FontType
    data class File(val path: String) : FontType
}