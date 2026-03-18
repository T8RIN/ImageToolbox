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

package com.t8rin.palette

sealed class PaletteCoderException(message: String) : Throwable(message) {
    class UnsupportedPaletteType : PaletteCoderException("Unsupported palette type")
    class InvalidFormat : PaletteCoderException("Invalid format")
    class InvalidASEHeader : PaletteCoderException("Invalid ASE header")
    class InvalidColorComponentCountForModelType :
        PaletteCoderException("Invalid color component count for model type")

    class UnknownBlockType : PaletteCoderException("Unknown block type")
    class GroupAlreadyOpen : PaletteCoderException("Group already open")
    class GroupNotOpen : PaletteCoderException("Group not open")
    class UnsupportedColorSpace : PaletteCoderException("Unsupported color space")
    class InvalidVersion : PaletteCoderException("Invalid version")
    class InvalidBOM : PaletteCoderException("Invalid BOM")
    class NotImplemented : PaletteCoderException("Not implemented")
    class CannotCreateColor : PaletteCoderException("Cannot create color")
    class TooFewColors : PaletteCoderException("Too few colors")
    class IndexOutOfRange : PaletteCoderException("Index out of range")

    data class UnknownColorMode(val mode: String) :
        PaletteCoderException("Unknown color mode: $mode")

    data class UnknownColorType(val type: Int) : PaletteCoderException("Unknown color type: $type")
    data class InvalidRGBHexString(val string: String) :
        PaletteCoderException("Invalid RGB hex string: $string")

    data class InvalidRGBAHexString(val string: String) :
        PaletteCoderException("Invalid RGBA hex string: $string")

}