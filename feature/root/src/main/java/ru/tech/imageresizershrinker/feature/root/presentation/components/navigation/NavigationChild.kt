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

import androidx.compose.runtime.Composable
import ru.tech.imageresizershrinker.colllage_maker.presentation.CollageMakerContent
import ru.tech.imageresizershrinker.colllage_maker.presentation.screenLogic.CollageMakerComponent
import ru.tech.imageresizershrinker.color_tools.presentation.ColorToolsContent
import ru.tech.imageresizershrinker.color_tools.presentation.screenLogic.ColorToolsComponent
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.ApngToolsContent
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.screenLogic.ApngToolsComponent
import ru.tech.imageresizershrinker.feature.base64_tools.presentation.Base64ToolsContent
import ru.tech.imageresizershrinker.feature.base64_tools.presentation.screenLogic.Base64ToolsComponent
import ru.tech.imageresizershrinker.feature.checksum_tools.presentation.ChecksumToolsContent
import ru.tech.imageresizershrinker.feature.checksum_tools.presentation.screenLogic.ChecksumToolsComponent
import ru.tech.imageresizershrinker.feature.cipher.presentation.CipherContent
import ru.tech.imageresizershrinker.feature.cipher.presentation.screenLogic.CipherComponent
import ru.tech.imageresizershrinker.feature.compare.presentation.CompareContent
import ru.tech.imageresizershrinker.feature.compare.presentation.screenLogic.CompareComponent
import ru.tech.imageresizershrinker.feature.crop.presentation.CropContent
import ru.tech.imageresizershrinker.feature.crop.presentation.screenLogic.CropComponent
import ru.tech.imageresizershrinker.feature.delete_exif.presentation.DeleteExifContent
import ru.tech.imageresizershrinker.feature.delete_exif.presentation.screenLogic.DeleteExifComponent
import ru.tech.imageresizershrinker.feature.document_scanner.presentation.DocumentScannerContent
import ru.tech.imageresizershrinker.feature.document_scanner.presentation.screenLogic.DocumentScannerComponent
import ru.tech.imageresizershrinker.feature.draw.presentation.DrawContent
import ru.tech.imageresizershrinker.feature.draw.presentation.screenLogic.DrawComponent
import ru.tech.imageresizershrinker.feature.easter_egg.presentation.EasterEggContent
import ru.tech.imageresizershrinker.feature.easter_egg.presentation.screenLogic.EasterEggComponent
import ru.tech.imageresizershrinker.feature.edit_exif.presentation.EditExifContent
import ru.tech.imageresizershrinker.feature.edit_exif.presentation.screenLogic.EditExifComponent
import ru.tech.imageresizershrinker.feature.erase_background.presentation.EraseBackgroundContent
import ru.tech.imageresizershrinker.feature.erase_background.presentation.screenLogic.EraseBackgroundComponent
import ru.tech.imageresizershrinker.feature.filters.presentation.FiltersContent
import ru.tech.imageresizershrinker.feature.filters.presentation.screenLogic.FiltersComponent
import ru.tech.imageresizershrinker.feature.format_conversion.presentation.FormatConversionContent
import ru.tech.imageresizershrinker.feature.format_conversion.presentation.screenLogic.FormatConversionComponent
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.GeneratePaletteContent
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.screenLogic.GeneratePaletteComponent
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.GifToolsContent
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.screenLogic.GifToolsComponent
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.GradientMakerContent
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent
import ru.tech.imageresizershrinker.feature.image_preview.presentation.ImagePreviewContent
import ru.tech.imageresizershrinker.feature.image_preview.presentation.screenLogic.ImagePreviewComponent
import ru.tech.imageresizershrinker.feature.image_stacking.presentation.ImageStackingContent
import ru.tech.imageresizershrinker.feature.image_stacking.presentation.screenLogic.ImageStackingComponent
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.ImageStitchingContent
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.screenLogic.ImageStitchingComponent
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.JxlToolsContent
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.screenLogic.JxlToolsComponent
import ru.tech.imageresizershrinker.feature.libraries_info.presentation.LibrariesInfoContent
import ru.tech.imageresizershrinker.feature.libraries_info.presentation.screenLogic.LibrariesInfoComponent
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.LimitsResizeContent
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.screenLogic.LimitsResizeComponent
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.LoadNetImageContent
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent
import ru.tech.imageresizershrinker.feature.main.presentation.MainContent
import ru.tech.imageresizershrinker.feature.main.presentation.screenLogic.MainComponent
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.MarkupLayersContent
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import ru.tech.imageresizershrinker.feature.mesh_gradients.presentation.MeshGradientsContent
import ru.tech.imageresizershrinker.feature.mesh_gradients.presentation.screenLogic.MeshGradientsComponent
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.PdfToolsContent
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.screenLogic.PdfToolsComponent
import ru.tech.imageresizershrinker.feature.pick_color.presentation.PickColorFromImageContent
import ru.tech.imageresizershrinker.feature.pick_color.presentation.screenLogic.PickColorFromImageComponent
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.RecognizeTextContent
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.screenLogic.RecognizeTextComponent
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.ResizeAndConvertContent
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.screenLogic.ResizeAndConvertComponent
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.ScanQrCodeContent
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent
import ru.tech.imageresizershrinker.feature.settings.presentation.SettingsContent
import ru.tech.imageresizershrinker.feature.settings.presentation.screenLogic.SettingsComponent
import ru.tech.imageresizershrinker.feature.single_edit.presentation.SingleEditContent
import ru.tech.imageresizershrinker.feature.single_edit.presentation.screenLogic.SingleEditComponent
import ru.tech.imageresizershrinker.feature.svg_maker.presentation.SvgMakerContent
import ru.tech.imageresizershrinker.feature.svg_maker.presentation.screenLogic.SvgMakerComponent
import ru.tech.imageresizershrinker.feature.watermarking.presentation.WatermarkingContent
import ru.tech.imageresizershrinker.feature.watermarking.presentation.screenLogic.WatermarkingComponent
import ru.tech.imageresizershrinker.feature.webp_tools.presentation.WebpToolsContent
import ru.tech.imageresizershrinker.feature.webp_tools.presentation.screenLogic.WebpToolsComponent
import ru.tech.imageresizershrinker.feature.weight_resize.presentation.WeightResizeContent
import ru.tech.imageresizershrinker.feature.weight_resize.presentation.screenLogic.WeightResizeComponent
import ru.tech.imageresizershrinker.feature.zip.presentation.ZipContent
import ru.tech.imageresizershrinker.feature.zip.presentation.screenLogic.ZipComponent
import ru.tech.imageresizershrinker.image_cutting.presentation.ImageCutterContent
import ru.tech.imageresizershrinker.image_cutting.presentation.screenLogic.ImageCutterComponent
import ru.tech.imageresizershrinker.image_splitting.presentation.ImageSplitterContent
import ru.tech.imageresizershrinker.image_splitting.presentation.screenLogic.ImageSplitterComponent
import ru.tech.imageresizershrinker.noise_generation.presentation.NoiseGenerationContent
import ru.tech.imageresizershrinker.noise_generation.presentation.screenLogic.NoiseGenerationComponent


internal sealed class NavigationChild {

    @Composable
    abstract fun Content()


    class ApngTools(val component: ApngToolsComponent) : NavigationChild() {
        @Composable
        override fun Content() = ApngToolsContent(component)
    }

    class Cipher(val component: CipherComponent) : NavigationChild() {
        @Composable
        override fun Content() = CipherContent(component)
    }

    class CollageMaker(val component: CollageMakerComponent) : NavigationChild() {
        @Composable
        override fun Content() = CollageMakerContent(component)
    }

    class ColorTools(val component: ColorToolsComponent) : NavigationChild() {
        @Composable
        override fun Content() = ColorToolsContent(component)
    }

    class Compare(val component: CompareComponent) : NavigationChild() {
        @Composable
        override fun Content() = CompareContent(component)
    }

    class Crop(val component: CropComponent) : NavigationChild() {
        @Composable
        override fun Content() = CropContent(component)
    }

    class DeleteExif(val component: DeleteExifComponent) : NavigationChild() {
        @Composable
        override fun Content() = DeleteExifContent(component)
    }

    class DocumentScanner(val component: DocumentScannerComponent) : NavigationChild() {
        @Composable
        override fun Content() = DocumentScannerContent(component)
    }

    class Draw(val component: DrawComponent) : NavigationChild() {
        @Composable
        override fun Content() = DrawContent(component)
    }

    class EasterEgg(val component: EasterEggComponent) : NavigationChild() {
        @Composable
        override fun Content() = EasterEggContent(component)
    }

    class EraseBackground(val component: EraseBackgroundComponent) : NavigationChild() {
        @Composable
        override fun Content() = EraseBackgroundContent(component)
    }

    class Filter(val component: FiltersComponent) : NavigationChild() {
        @Composable
        override fun Content() = FiltersContent(component)
    }

    class FormatConversion(val component: FormatConversionComponent) : NavigationChild() {
        @Composable
        override fun Content() = FormatConversionContent(component)
    }

    class GeneratePalette(val component: GeneratePaletteComponent) : NavigationChild() {
        @Composable
        override fun Content() = GeneratePaletteContent(component)
    }

    class GifTools(val component: GifToolsComponent) : NavigationChild() {
        @Composable
        override fun Content() = GifToolsContent(component)
    }

    class GradientMaker(val component: GradientMakerComponent) : NavigationChild() {
        @Composable
        override fun Content() = GradientMakerContent(component)
    }

    class ImagePreview(val component: ImagePreviewComponent) : NavigationChild() {
        @Composable
        override fun Content() = ImagePreviewContent(component)
    }

    class ImageSplitting(val component: ImageSplitterComponent) : NavigationChild() {
        @Composable
        override fun Content() = ImageSplitterContent(component)
    }

    class ImageStacking(val component: ImageStackingComponent) : NavigationChild() {
        @Composable
        override fun Content() = ImageStackingContent(component)
    }

    class ImageStitching(val component: ImageStitchingComponent) : NavigationChild() {
        @Composable
        override fun Content() = ImageStitchingContent(component)
    }

    class JxlTools(val component: JxlToolsComponent) : NavigationChild() {
        @Composable
        override fun Content() = JxlToolsContent(component)
    }

    class LimitResize(val component: LimitsResizeComponent) : NavigationChild() {
        @Composable
        override fun Content() = LimitsResizeContent(component)
    }

    class LoadNetImage(val component: LoadNetImageComponent) : NavigationChild() {
        @Composable
        override fun Content() = LoadNetImageContent(component)
    }

    class Main(val component: MainComponent) : NavigationChild() {
        @Composable
        override fun Content() = MainContent(component)
    }

    class NoiseGeneration(val component: NoiseGenerationComponent) : NavigationChild() {
        @Composable
        override fun Content() = NoiseGenerationContent(component)
    }

    class PdfTools(val component: PdfToolsComponent) : NavigationChild() {
        @Composable
        override fun Content() = PdfToolsContent(component)
    }

    class PickColorFromImage(val component: PickColorFromImageComponent) : NavigationChild() {
        @Composable
        override fun Content() = PickColorFromImageContent(component)
    }

    class RecognizeText(val component: RecognizeTextComponent) : NavigationChild() {
        @Composable
        override fun Content() = RecognizeTextContent(component)
    }

    class ResizeAndConvert(val component: ResizeAndConvertComponent) : NavigationChild() {
        @Composable
        override fun Content() = ResizeAndConvertContent(component)
    }

    class ScanQrCode(val component: ScanQrCodeComponent) : NavigationChild() {
        @Composable
        override fun Content() = ScanQrCodeContent(component)
    }

    class Settings(val component: SettingsComponent) : NavigationChild() {
        @Composable
        override fun Content() = SettingsContent(component)
    }

    class SingleEdit(val component: SingleEditComponent) : NavigationChild() {
        @Composable
        override fun Content() = SingleEditContent(component)
    }

    class SvgMaker(val component: SvgMakerComponent) : NavigationChild() {
        @Composable
        override fun Content() = SvgMakerContent(component)
    }

    class Watermarking(val component: WatermarkingComponent) : NavigationChild() {
        @Composable
        override fun Content() = WatermarkingContent(component)
    }

    class WebpTools(val component: WebpToolsComponent) : NavigationChild() {
        @Composable
        override fun Content() = WebpToolsContent(component)
    }

    class WeightResize(val component: WeightResizeComponent) : NavigationChild() {
        @Composable
        override fun Content() = WeightResizeContent(component)
    }

    class Zip(val component: ZipComponent) : NavigationChild() {
        @Composable
        override fun Content() = ZipContent(component)
    }

    class LibrariesInfo(val component: LibrariesInfoComponent) : NavigationChild() {
        @Composable
        override fun Content() = LibrariesInfoContent(component)
    }

    class MarkupLayers(val component: MarkupLayersComponent) : NavigationChild() {
        @Composable
        override fun Content() = MarkupLayersContent(component)
    }

    class Base64Tools(val component: Base64ToolsComponent) : NavigationChild() {
        @Composable
        override fun Content() = Base64ToolsContent(component)
    }

    class ChecksumTools(val component: ChecksumToolsComponent) : NavigationChild() {
        @Composable
        override fun Content() = ChecksumToolsContent(component)
    }

    class MeshGradients(val component: MeshGradientsComponent) : NavigationChild() {
        @Composable
        override fun Content() = MeshGradientsContent(component)
    }

    class EditExif(val component: EditExifComponent) : NavigationChild() {
        @Composable
        override fun Content() = EditExifContent(component)
    }

    class ImageCutter(val component: ImageCutterComponent) : NavigationChild() {
        @Composable
        override fun Content() = ImageCutterContent(component)
    }

}