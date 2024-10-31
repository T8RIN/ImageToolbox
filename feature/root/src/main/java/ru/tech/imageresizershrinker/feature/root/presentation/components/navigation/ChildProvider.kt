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

import com.arkivanov.decompose.ComponentContext
import ru.tech.imageresizershrinker.colllage_maker.presentation.viewModel.CollageMakerViewModel
import ru.tech.imageresizershrinker.color_tools.presentation.viewModel.ColorToolsViewModel
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
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
import javax.inject.Inject

internal class ChildProvider @Inject constructor(
    private val apngToolsComponentFactory: ApngToolsViewModel.Factory,
    private val cipherComponentFactory: CipherViewModel.Factory,
    private val collageMakerComponentFactory: CollageMakerViewModel.Factory,
    private val compareComponentFactory: CompareViewModel.Factory,
    private val cropComponentFactory: CropViewModel.Factory,
    private val deleteExifComponentFactory: DeleteExifViewModel.Factory,
    private val documentScannerComponentFactory: DocumentScannerViewModel.Factory,
    private val drawComponentFactory: DrawViewModel.Factory,
    private val eraseBackgroundComponentFactory: EraseBackgroundViewModel.Factory,
    private val filterComponentFactory: FilterViewModel.Factory,
    private val formatConversionComponentFactory: FormatConversionViewModel.Factory,
    private val generatePaletteComponentFactory: GeneratePaletteViewModel.Factory,
    private val gifToolsComponentFactory: GifToolsViewModel.Factory,
    private val gradientMakerComponentFactory: GradientMakerViewModel.Factory,
    private val imagePreviewComponentFactory: ImagePreviewViewModel.Factory,
    private val imageSplittingComponentFactory: ImageSplitterViewModel.Factory,
    private val imageStackingComponentFactory: ImageStackingViewModel.Factory,
    private val imageStitchingComponentFactory: ImageStitchingViewModel.Factory,
    private val jxlToolsComponentFactory: JxlToolsViewModel.Factory,
    private val limitResizeComponentFactory: LimitsResizeViewModel.Factory,
    private val loadNetImageComponentFactory: LoadNetImageViewModel.Factory,
    private val noiseGenerationComponentFactory: NoiseGenerationViewModel.Factory,
    private val pdfToolsComponentFactory: PdfToolsViewModel.Factory,
    private val pickColorFromImageComponentFactory: PickColorViewModel.Factory,
    private val recognizeTextComponentFactory: RecognizeTextViewModel.Factory,
    private val resizeAndConvertComponentFactory: ResizeAndConvertViewModel.Factory,
    private val scanQrCodeComponentFactory: ScanQrCodeViewModel.Factory,
    private val settingsComponentFactory: SettingsViewModel.Factory,
    private val singleEditComponentFactory: SingleEditViewModel.Factory,
    private val svgMakerComponentFactory: SvgMakerViewModel.Factory,
    private val watermarkingComponentFactory: WatermarkingViewModel.Factory,
    private val webpToolsComponentFactory: WebpToolsViewModel.Factory,
    private val weightResizeComponentFactory: WeightResizeViewModel.Factory,
    private val zipComponentFactory: ZipViewModel.Factory,
    private val easterEggComponentFactory: EasterEggViewModel.Factory,
    private val colorToolsViewModelFactory: ColorToolsViewModel.Factory,
) {
    fun createChild(
        config: Screen,
        componentContext: ComponentContext
    ): NavigationChild = when (config) {
        Screen.ColorTools -> NavigationChild.ColorTools(
            colorToolsViewModelFactory(
                componentContext = componentContext
            )
        )

        Screen.EasterEgg -> NavigationChild.EasterEgg(
            easterEggComponentFactory(
                componentContext = componentContext
            )
        )

        Screen.Main -> NavigationChild.Main(
            settingsComponentFactory(
                componentContext = componentContext
            )
        )

        is Screen.ApngTools -> NavigationChild.ApngTools(
            apngToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.Cipher -> NavigationChild.Cipher(
            cipherComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.CollageMaker -> NavigationChild.CollageMaker(
            collageMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.Compare -> NavigationChild.Compare(
            compareComponentFactory(
                componentContext = componentContext,
                initialComparableUris = config.uris
                    ?.takeIf { it.size == 2 }
                    ?.let { it[0] to it[1] }
            )
        )

        is Screen.Crop -> NavigationChild.Crop(
            cropComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.DeleteExif -> NavigationChild.DeleteExif(
            deleteExifComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        Screen.DocumentScanner -> NavigationChild.DocumentScanner(
            documentScannerComponentFactory(
                componentContext = componentContext
            )
        )

        is Screen.Draw -> NavigationChild.Draw(
            drawComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.EraseBackground -> NavigationChild.EraseBackground(
            eraseBackgroundComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.Filter -> NavigationChild.Filter(
            filterComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.FormatConversion -> NavigationChild.FormatConversion(
            formatConversionComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.GeneratePalette -> NavigationChild.GeneratePalette(
            generatePaletteComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.GifTools -> NavigationChild.GifTools(
            gifToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.GradientMaker -> NavigationChild.GradientMaker(
            gradientMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.ImagePreview -> NavigationChild.ImagePreview(
            imagePreviewComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.ImageSplitting -> NavigationChild.ImageSplitting(
            imageSplittingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uri
            )
        )

        is Screen.ImageStacking -> NavigationChild.ImageStacking(
            imageStackingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.ImageStitching -> NavigationChild.ImageStitching(
            imageStitchingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.JxlTools -> NavigationChild.JxlTools(
            jxlToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.LimitResize -> NavigationChild.LimitResize(
            limitResizeComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.LoadNetImage -> NavigationChild.LoadNetImage(
            loadNetImageComponentFactory(
                componentContext = componentContext,
                initialUrl = config.url
            )
        )


        Screen.NoiseGeneration -> NavigationChild.NoiseGeneration(
            noiseGenerationComponentFactory(
                componentContext = componentContext
            )
        )

        is Screen.PdfTools -> NavigationChild.PdfTools(
            pdfToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.PickColorFromImage -> NavigationChild.PickColorFromImage(
            pickColorFromImageComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.RecognizeText -> NavigationChild.RecognizeText(
            recognizeTextComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.ResizeAndConvert -> NavigationChild.ResizeAndConvert(
            resizeAndConvertComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.ScanQrCode -> NavigationChild.ScanQrCode(
            scanQrCodeComponentFactory(
                componentContext = componentContext,
                initialQrCodeContent = config.qrCodeContent
            )
        )

        Screen.Settings -> NavigationChild.Settings(
            settingsComponentFactory(
                componentContext = componentContext
            )
        )

        is Screen.SingleEdit -> NavigationChild.SingleEdit(
            singleEditComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.SvgMaker -> NavigationChild.SvgMaker(
            svgMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.Watermarking -> NavigationChild.Watermarking(
            watermarkingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.WebpTools -> NavigationChild.WebpTools(
            webpToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.WeightResize -> NavigationChild.WeightResize(
            weightResizeComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.Zip -> NavigationChild.Zip(
            zipComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )
    }
}