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

import com.t8rin.palette.coders.PaletteFormatCoder

/**
 * Supported palette formats
 */
enum class PaletteFormat(
    val fileExtension: List<String>,
    val withPaletteName: Boolean
) {
    ACB(
        fileExtension = listOf("acb"),
        withPaletteName = true
    ), // Adobe Color Book

    ACO(
        fileExtension = listOf("aco"),
        withPaletteName = true
    ), // Adobe Photoshop Swatch

    ACT(
        fileExtension = listOf("act"),
        withPaletteName = false
    ), // Adobe Color Tables

    ANDROID_XML(
        fileExtension = listOf("xml"),
        withPaletteName = false
    ), // Android XML Palette file

    ASE(
        fileExtension = listOf("ase"),
        withPaletteName = true
    ), // Adobe Swatch Exchange

    BASIC_XML(
        fileExtension = listOf("xml"),
        withPaletteName = true
    ), // Basic XML palette format

    COREL_PAINTER(
        fileExtension = listOf("txt"),
        withPaletteName = false
    ), // Corel Painter Swatches

    COREL_DRAW(
        fileExtension = listOf("xml"),
        withPaletteName = true
    ), // CorelDraw XML

    SCRIBUS_XML(
        fileExtension = listOf("xml"),
        withPaletteName = true
    ), // Scribus XML swatches

    COREL_PALETTE(
        fileExtension = listOf("cpl"),
        withPaletteName = true
    ), // Corel Palette

    CSV(
        fileExtension = listOf("csv"),
        withPaletteName = false
    ), // CSV Palette

    DCP(
        fileExtension = listOf("dcp"),
        withPaletteName = true
    ), // ColorPaletteCodable binary format

    GIMP(
        fileExtension = listOf("gpl"),
        withPaletteName = true
    ), // GIMP gpl format

    HEX_RGBA(
        fileExtension = listOf("hex"),
        withPaletteName = false
    ), // Hex RGBA coded files

    IMAGE(
        fileExtension = listOf("png", "jpg", "jpeg"),
        withPaletteName = false
    ), // image-based palette coder

    JSON(
        fileExtension = listOf("jsoncolorpalette", "json"),
        withPaletteName = true
    ), // ColorPaletteCodable JSON format

    OPEN_OFFICE(
        fileExtension = listOf("soc"),
        withPaletteName = false
    ), // OpenOffice palette format (.soc)

    PAINT_NET(
        fileExtension = listOf("txt"),
        withPaletteName = true
    ), // Paint.NET palette file (.txt)

    PAINT_SHOP_PRO(
        fileExtension = listOf("psppalette", "pal"),
        withPaletteName = true
    ), // Paint Shop Pro palette (.pal, .psppalette)

    RGBA(
        fileExtension = listOf("rgba", "txt"),
        withPaletteName = false
    ), // RGBA encoded text files (.rgba, .txt)

    RGB(
        fileExtension = listOf("rgb", "txt"),
        withPaletteName = false
    ), // RGB encoded text files (.rgb, .txt)

    RIFF(
        fileExtension = listOf("pal"),
        withPaletteName = false
    ), // Microsoft RIFF palette (.pal)

    SKETCH(
        fileExtension = listOf("sketchpalette"),
        withPaletteName = false
    ), // Sketch palette file (.sketchpalette)

    SKP(
        fileExtension = listOf("skp"),
        withPaletteName = false
    ), // SKP Palette

    SVG(
        fileExtension = listOf("svg"),
        withPaletteName = true
    ), // Scalable Vector Graphics palette (.svg)

    SWIFT(
        fileExtension = listOf("swift"),
        withPaletteName = true
    ), // (export only) Swift source file (.swift)

    KOTLIN(
        fileExtension = listOf("kt"),
        withPaletteName = true
    ), // (export only) Kotlin/Jetpack Compose source file (.kt)

    COREL_DRAW_V3(
        fileExtension = listOf("pal"),
        withPaletteName = true
    ), // Corel Draw V3 file (.pal)

    CLF(
        fileExtension = listOf("clf"),
        withPaletteName = true
    ), // LAB colors

    SWATCHES(
        fileExtension = listOf("swatches"),
        withPaletteName = true
    ), // Procreate swatches

    AUTODESK_COLOR_BOOK(
        fileExtension = listOf("acb"),
        withPaletteName = true
    ), // Autodesk Color Book (unencrypted only) (.acb)

    SIMPLE_PALETTE(
        fileExtension = listOf("color-palette"),
        withPaletteName = true
    ), // Simple Palette format

    SWATCHBOOKER(
        fileExtension = listOf("sbz"),
        withPaletteName = true
    ), // Swatchbooker .sbz file

    AFPALETTE(
        fileExtension = listOf("afpalette"),
        withPaletteName = true
    ), // Affinity Designer .afpalette file

    XARA(
        fileExtension = listOf("jcw"),
        withPaletteName = true
    ), // Xara palette file (.jcw)

    KOFFICE(
        fileExtension = listOf("colors"),
        withPaletteName = false
    ), // KOffice palette file (.colors)

    HPL(
        fileExtension = listOf("hpl"),
        withPaletteName = true
    ), // Homesite Palette file (.hpl)

    SKENCIL(
        fileExtension = listOf("spl"),
        withPaletteName = true
    ), // Skencil Palette file (.spl)

    VGA_24BIT(
        fileExtension = listOf("vga24"),
        withPaletteName = false
    ), // 24-bit RGB VGA (3 bytes RRGGBB)

    VGA_18BIT(
        fileExtension = listOf("vga18"),
        withPaletteName = false
    ), // 18-bit RGB VGA (3 bytes RRGGBB)

    KRITA(
        fileExtension = listOf("kpl"),
        withPaletteName = true
    ); // KRITA Palette file (.kpl)

    companion object {
        /**
         * Get format from file extension
         * Searches through all enum entries to find matching file extension
         */
        fun fromFilename(filename: String): PaletteFormat? =
            PaletteFormat.entries.firstOrNull { format ->
                format.fileExtension.isNotEmpty() && format.fileExtension.any(filename::endsWith)
            }
    }
}

/**
 * Get coder for format
 */
fun PaletteFormat.getCoder(): PaletteCoder = PaletteFormatCoder(this)