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

import ru.tech.imageresizershrinker.colllage_maker.presentation.viewModel.CollageMakerViewModel
import ru.tech.imageresizershrinker.color_tools.presentation.viewModel.ColorToolsViewModel
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.viewModel.ApngToolsViewModel
import ru.tech.imageresizershrinker.feature.cipher.presentation.viewModel.CipherViewModel
import ru.tech.imageresizershrinker.feature.compare.presentation.viewModel.CompareViewModel
import ru.tech.imageresizershrinker.feature.crop.presentation.viewModel.CropViewModel
import ru.tech.imageresizershrinker.feature.delete_exif.presentation.viewModel.DeleteExifViewModel
import ru.tech.imageresizershrinker.feature.document_scanner.presentation.viewModel.DocumentScannerViewModel
import ru.tech.imageresizershrinker.feature.draw.presentation.viewModel.DrawViewModel
import ru.tech.imageresizershrinker.feature.easter_egg.presentation.viewModel.EasterEggViewModel
import ru.tech.imageresizershrinker.feature.erase_background.presentation.viewModel.EraseBackgroundViewModel
import ru.tech.imageresizershrinker.feature.filters.presentation.viewModel.FilterViewModel
import ru.tech.imageresizershrinker.feature.format_conversion.presentation.viewModel.FormatConversionViewModel
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.viewModel.GeneratePaletteViewModel
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.viewModel.GifToolsViewModel
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.viewModel.GradientMakerViewModel
import ru.tech.imageresizershrinker.feature.image_preview.presentation.viewModel.ImagePreviewViewModel
import ru.tech.imageresizershrinker.feature.image_stacking.presentation.viewModel.ImageStackingViewModel
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.viewModel.ImageStitchingViewModel
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.viewModel.JxlToolsViewModel
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.viewModel.LimitsResizeViewModel
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.viewModel.LoadNetImageViewModel
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.viewModel.PdfToolsViewModel
import ru.tech.imageresizershrinker.feature.pick_color.presentation.viewModel.PickColorViewModel
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.viewModel.RecognizeTextViewModel
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.viewModel.ResizeAndConvertViewModel
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.viewModel.ScanQrCodeViewModel
import ru.tech.imageresizershrinker.feature.settings.presentation.viewModel.SettingsViewModel
import ru.tech.imageresizershrinker.feature.single_edit.presentation.viewModel.SingleEditViewModel
import ru.tech.imageresizershrinker.feature.svg_maker.presentation.viewModel.SvgMakerViewModel
import ru.tech.imageresizershrinker.feature.watermarking.presentation.viewModel.WatermarkingViewModel
import ru.tech.imageresizershrinker.feature.webp_tools.presentation.viewModel.WebpToolsViewModel
import ru.tech.imageresizershrinker.feature.weight_resize.presentation.viewModel.WeightResizeViewModel
import ru.tech.imageresizershrinker.feature.zip.presentation.viewModel.ZipViewModel
import ru.tech.imageresizershrinker.image_splitting.presentation.viewModel.ImageSplitterViewModel
import ru.tech.imageresizershrinker.noise_generation.presentation.viewModel.NoiseGenerationViewModel

sealed class NavigationChild {
    class ApngTools(val component: ApngToolsViewModel) : NavigationChild()
    class Cipher(val component: CipherViewModel) : NavigationChild()
    class CollageMaker(val component: CollageMakerViewModel) : NavigationChild()
    class ColorTools(val component: ColorToolsViewModel) : NavigationChild()
    class Compare(val component: CompareViewModel) : NavigationChild()
    class Crop(val component: CropViewModel) : NavigationChild()
    class DeleteExif(val component: DeleteExifViewModel) : NavigationChild()
    class DocumentScanner(val component: DocumentScannerViewModel) : NavigationChild()
    class Draw(val component: DrawViewModel) : NavigationChild()
    class EasterEgg(val component: EasterEggViewModel) : NavigationChild()
    class EraseBackground(val component: EraseBackgroundViewModel) : NavigationChild()
    class Filter(val component: FilterViewModel) : NavigationChild()
    class FormatConversion(val component: FormatConversionViewModel) : NavigationChild()
    class GeneratePalette(val component: GeneratePaletteViewModel) : NavigationChild()
    class GifTools(val component: GifToolsViewModel) : NavigationChild()
    class GradientMaker(val component: GradientMakerViewModel) : NavigationChild()
    class ImagePreview(val component: ImagePreviewViewModel) : NavigationChild()
    class ImageSplitting(val component: ImageSplitterViewModel) : NavigationChild()
    class ImageStacking(val component: ImageStackingViewModel) : NavigationChild()
    class ImageStitching(val component: ImageStitchingViewModel) : NavigationChild()
    class JxlTools(val component: JxlToolsViewModel) : NavigationChild()
    class LimitResize(val component: LimitsResizeViewModel) : NavigationChild()
    class LoadNetImage(val component: LoadNetImageViewModel) : NavigationChild()
    class Main(val component: SettingsViewModel) : NavigationChild()
    class NoiseGeneration(val component: NoiseGenerationViewModel) : NavigationChild()
    class PdfTools(val component: PdfToolsViewModel) : NavigationChild()
    class PickColorFromImage(val component: PickColorViewModel) : NavigationChild()
    class RecognizeText(val component: RecognizeTextViewModel) : NavigationChild()
    class ResizeAndConvert(val component: ResizeAndConvertViewModel) : NavigationChild()
    class ScanQrCode(val component: ScanQrCodeViewModel) : NavigationChild()
    class Settings(val component: SettingsViewModel) : NavigationChild()
    class SingleEdit(val component: SingleEditViewModel) : NavigationChild()
    class SvgMaker(val component: SvgMakerViewModel) : NavigationChild()
    class Watermarking(val component: WatermarkingViewModel) : NavigationChild()
    class WebpTools(val component: WebpToolsViewModel) : NavigationChild()
    class WeightResize(val component: WeightResizeViewModel) : NavigationChild()
    class Zip(val component: ZipViewModel) : NavigationChild()
}