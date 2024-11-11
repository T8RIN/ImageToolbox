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

package ru.tech.imageresizershrinker.feature.root.presentation.components.navigation

import ru.tech.imageresizershrinker.colllage_maker.presentation.screenLogic.CollageMakerComponent
import ru.tech.imageresizershrinker.color_tools.presentation.screenLogic.ColorToolsComponent
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.screenLogic.ApngToolsComponent
import ru.tech.imageresizershrinker.feature.cipher.presentation.screenLogic.CipherComponent
import ru.tech.imageresizershrinker.feature.compare.presentation.screenLogic.CompareComponent
import ru.tech.imageresizershrinker.feature.crop.presentation.screenLogic.CropComponent
import ru.tech.imageresizershrinker.feature.delete_exif.presentation.screenLogic.DeleteExifComponent
import ru.tech.imageresizershrinker.feature.document_scanner.presentation.screenLogic.DocumentScannerComponent
import ru.tech.imageresizershrinker.feature.draw.presentation.screenLogic.DrawComponent
import ru.tech.imageresizershrinker.feature.easter_egg.presentation.screenLogic.EasterEggComponent
import ru.tech.imageresizershrinker.feature.erase_background.presentation.screenLogic.EraseBackgroundComponent
import ru.tech.imageresizershrinker.feature.filters.presentation.screenLogic.FilterComponent
import ru.tech.imageresizershrinker.feature.format_conversion.presentation.screenLogic.FormatConversionComponent
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.screenLogic.GeneratePaletteComponent
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.screenLogic.GifToolsComponent
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent
import ru.tech.imageresizershrinker.feature.image_preview.presentation.screenLogic.ImagePreviewComponent
import ru.tech.imageresizershrinker.feature.image_stacking.presentation.screenLogic.ImageStackingComponent
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.screenLogic.ImageStitchingComponent
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.screenLogic.JxlToolsComponent
import ru.tech.imageresizershrinker.feature.libraries_info.presentation.screenLogic.LibrariesInfoComponent
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.screenLogic.LimitsResizeComponent
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.screenLogic.PdfToolsComponent
import ru.tech.imageresizershrinker.feature.pick_color.presentation.screenLogic.PickColorComponent
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.screenLogic.RecognizeTextComponent
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.screenLogic.ResizeAndConvertComponent
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent
import ru.tech.imageresizershrinker.feature.settings.presentation.screenLogic.SettingsComponent
import ru.tech.imageresizershrinker.feature.single_edit.presentation.screenLogic.SingleEditComponent
import ru.tech.imageresizershrinker.feature.svg_maker.presentation.screenLogic.SvgMakerComponent
import ru.tech.imageresizershrinker.feature.watermarking.presentation.screenLogic.WatermarkingComponent
import ru.tech.imageresizershrinker.feature.webp_tools.presentation.screenLogic.WebpToolsComponent
import ru.tech.imageresizershrinker.feature.weight_resize.presentation.screenLogic.WeightResizeComponent
import ru.tech.imageresizershrinker.feature.zip.presentation.screenLogic.ZipComponent
import ru.tech.imageresizershrinker.image_splitting.presentation.screenLogic.ImageSplitterComponent
import ru.tech.imageresizershrinker.noise_generation.presentation.screenLogic.NoiseGenerationComponent

sealed class NavigationChild {
    class ApngTools(val component: ApngToolsComponent) : NavigationChild()
    class Cipher(val component: CipherComponent) : NavigationChild()
    class CollageMaker(val component: CollageMakerComponent) : NavigationChild()
    class ColorTools(val component: ColorToolsComponent) : NavigationChild()
    class Compare(val component: CompareComponent) : NavigationChild()
    class Crop(val component: CropComponent) : NavigationChild()
    class DeleteExif(val component: DeleteExifComponent) : NavigationChild()
    class DocumentScanner(val component: DocumentScannerComponent) : NavigationChild()
    class Draw(val component: DrawComponent) : NavigationChild()
    class EasterEgg(val component: EasterEggComponent) : NavigationChild()
    class EraseBackground(val component: EraseBackgroundComponent) : NavigationChild()
    class Filter(val component: FilterComponent) : NavigationChild()
    class FormatConversion(val component: FormatConversionComponent) : NavigationChild()
    class GeneratePalette(val component: GeneratePaletteComponent) : NavigationChild()
    class GifTools(val component: GifToolsComponent) : NavigationChild()
    class GradientMaker(val component: GradientMakerComponent) : NavigationChild()
    class ImagePreview(val component: ImagePreviewComponent) : NavigationChild()
    class ImageSplitting(val component: ImageSplitterComponent) : NavigationChild()
    class ImageStacking(val component: ImageStackingComponent) : NavigationChild()
    class ImageStitching(val component: ImageStitchingComponent) : NavigationChild()
    class JxlTools(val component: JxlToolsComponent) : NavigationChild()
    class LimitResize(val component: LimitsResizeComponent) : NavigationChild()
    class LoadNetImage(val component: LoadNetImageComponent) : NavigationChild()
    class Main(val component: SettingsComponent) : NavigationChild()
    class NoiseGeneration(val component: NoiseGenerationComponent) : NavigationChild()
    class PdfTools(val component: PdfToolsComponent) : NavigationChild()
    class PickColorFromImage(val component: PickColorComponent) : NavigationChild()
    class RecognizeText(val component: RecognizeTextComponent) : NavigationChild()
    class ResizeAndConvert(val component: ResizeAndConvertComponent) : NavigationChild()
    class ScanQrCode(val component: ScanQrCodeComponent) : NavigationChild()
    class Settings(val component: SettingsComponent) : NavigationChild()
    class SingleEdit(val component: SingleEditComponent) : NavigationChild()
    class SvgMaker(val component: SvgMakerComponent) : NavigationChild()
    class Watermarking(val component: WatermarkingComponent) : NavigationChild()
    class WebpTools(val component: WebpToolsComponent) : NavigationChild()
    class WeightResize(val component: WeightResizeComponent) : NavigationChild()
    class Zip(val component: ZipComponent) : NavigationChild()
    class LibrariesInfo(val component: LibrariesInfoComponent) : NavigationChild()
}