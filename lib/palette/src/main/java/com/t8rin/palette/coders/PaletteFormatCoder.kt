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

package com.t8rin.palette.coders

import com.t8rin.palette.PaletteCoder
import com.t8rin.palette.PaletteFormat

class PaletteFormatCoder(
    val paletteFormat: PaletteFormat
) : PaletteCoder by when (paletteFormat) {
    PaletteFormat.ACB -> ACBPaletteCoder()
    PaletteFormat.ACO -> ACOPaletteCoder()
    PaletteFormat.ACT -> ACTPaletteCoder()
    PaletteFormat.ANDROID_XML -> AndroidColorsXMLCoder()
    PaletteFormat.ASE -> ASEPaletteCoder()
    PaletteFormat.BASIC_XML -> BasicXMLCoder()
    PaletteFormat.COREL_PAINTER -> CorelPainterCoder()
    PaletteFormat.COREL_DRAW -> CorelXMLPaletteCoder()
    PaletteFormat.SCRIBUS_XML -> ScribusXMLPaletteCoder()
    PaletteFormat.COREL_PALETTE -> CPLPaletteCoder()
    PaletteFormat.CSV -> CSVPaletteCoder()
    PaletteFormat.DCP -> DCPPaletteCoder()
    PaletteFormat.GIMP -> GIMPPaletteCoder()
    PaletteFormat.HEX_RGBA -> HEXPaletteCoder()
    PaletteFormat.IMAGE -> ImagePaletteCoder()
    PaletteFormat.JSON -> JSONPaletteCoder()
    PaletteFormat.OPEN_OFFICE -> OpenOfficePaletteCoder()
    PaletteFormat.PAINT_NET -> PaintNETPaletteCoder()
    PaletteFormat.PAINT_SHOP_PRO -> PaintShopProPaletteCoder()
    PaletteFormat.RGBA -> RGBAPaletteCoder()
    PaletteFormat.RGB -> RGBPaletteCoder()
    PaletteFormat.RIFF -> RIFFPaletteCoder()
    PaletteFormat.SKETCH -> SketchPaletteCoder()
    PaletteFormat.SVG -> SVGPaletteCoder()
    PaletteFormat.SKP -> SKPPaletteCoder()
    PaletteFormat.SWIFT -> SwiftPaletteCoder()
    PaletteFormat.KOTLIN -> KotlinPaletteCoder()
    PaletteFormat.COREL_DRAW_V3 -> CorelDraw3PaletteCoder()
    PaletteFormat.CLF -> CLFPaletteCoder()
    PaletteFormat.SWATCHES -> ProcreateSwatchesCoder()
    PaletteFormat.AUTODESK_COLOR_BOOK -> AutodeskColorBookCoder()
    PaletteFormat.SIMPLE_PALETTE -> SimplePaletteCoder()
    PaletteFormat.SWATCHBOOKER -> SwatchbookerCoder()
    PaletteFormat.AFPALETTE -> AFPaletteCoder()
    PaletteFormat.XARA -> JCWPaletteCoder()
    PaletteFormat.KOFFICE -> KOfficePaletteCoder()
    PaletteFormat.HPL -> HPLPaletteCoder()
    PaletteFormat.SKENCIL -> SkencilPaletteCoder()
    PaletteFormat.VGA_24BIT -> VGA24BitPaletteCoder()
    PaletteFormat.VGA_18BIT -> VGA18BitPaletteCoder()
    PaletteFormat.KRITA -> KRITAPaletteCoder()
}