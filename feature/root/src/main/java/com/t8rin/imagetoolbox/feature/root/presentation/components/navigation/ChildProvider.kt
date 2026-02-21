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

package com.t8rin.imagetoolbox.feature.root.presentation.components.navigation

import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.color_library.presentation.screenLogic.ColorLibraryComponent
import com.t8rin.imagetoolbox.color_tools.presentation.screenLogic.ColorToolsComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.screenLogic.AiToolsComponent
import com.t8rin.imagetoolbox.feature.apng_tools.presentation.screenLogic.ApngToolsComponent
import com.t8rin.imagetoolbox.feature.ascii_art.presentation.screenLogic.AsciiArtComponent
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.ui.screenLogic.AudioCoverExtractorComponent
import com.t8rin.imagetoolbox.feature.base64_tools.presentation.screenLogic.Base64ToolsComponent
import com.t8rin.imagetoolbox.feature.checksum_tools.presentation.screenLogic.ChecksumToolsComponent
import com.t8rin.imagetoolbox.feature.cipher.presentation.screenLogic.CipherComponent
import com.t8rin.imagetoolbox.feature.compare.presentation.screenLogic.CompareComponent
import com.t8rin.imagetoolbox.feature.crop.presentation.screenLogic.CropComponent
import com.t8rin.imagetoolbox.feature.delete_exif.presentation.screenLogic.DeleteExifComponent
import com.t8rin.imagetoolbox.feature.document_scanner.presentation.screenLogic.DocumentScannerComponent
import com.t8rin.imagetoolbox.feature.draw.presentation.screenLogic.DrawComponent
import com.t8rin.imagetoolbox.feature.easter_egg.presentation.screenLogic.EasterEggComponent
import com.t8rin.imagetoolbox.feature.edit_exif.presentation.screenLogic.EditExifComponent
import com.t8rin.imagetoolbox.feature.erase_background.presentation.screenLogic.EraseBackgroundComponent
import com.t8rin.imagetoolbox.feature.filters.presentation.screenLogic.FiltersComponent
import com.t8rin.imagetoolbox.feature.format_conversion.presentation.screenLogic.FormatConversionComponent
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.screenLogic.GifToolsComponent
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent
import com.t8rin.imagetoolbox.feature.image_preview.presentation.screenLogic.ImagePreviewComponent
import com.t8rin.imagetoolbox.feature.image_stacking.presentation.screenLogic.ImageStackingComponent
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.screenLogic.ImageStitchingComponent
import com.t8rin.imagetoolbox.feature.jxl_tools.presentation.screenLogic.JxlToolsComponent
import com.t8rin.imagetoolbox.feature.libraries_info.presentation.screenLogic.LibrariesInfoComponent
import com.t8rin.imagetoolbox.feature.limits_resize.presentation.screenLogic.LimitsResizeComponent
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent
import com.t8rin.imagetoolbox.feature.main.presentation.screenLogic.MainComponent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import com.t8rin.imagetoolbox.feature.mesh_gradients.presentation.screenLogic.MeshGradientsComponent
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.screenLogic.PaletteToolsComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compress.screenLogic.CompressPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop.screenLogic.CropPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.extract_images.screenLogic.ExtractImagesPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.flatten.screenLogic.FlattenPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.grayscale.screenLogic.GrayscalePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.merge.screenLogic.MergePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.metadata.screenLogic.MetadataPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.ocr.screenLogic.OCRPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.page_numbers.screenLogic.PageNumbersPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.protect.screenLogic.ProtectPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rearrange.screenLogic.RearrangePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.remove_pages.screenLogic.RemovePagesPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.repair.screenLogic.RepairPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.screenLogic.PdfToolsComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rotate.screenLogic.RotatePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.screenLogic.SignaturePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.split.screenLogic.SplitPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.unlock.screenLogic.UnlockPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.watermark.screenLogic.WatermarkPdfToolComponent
import com.t8rin.imagetoolbox.feature.pick_color.presentation.screenLogic.PickColorFromImageComponent
import com.t8rin.imagetoolbox.feature.recognize.text.presentation.screenLogic.RecognizeTextComponent
import com.t8rin.imagetoolbox.feature.resize_convert.presentation.screenLogic.ResizeAndConvertComponent
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.AiTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ApngTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.AsciiArt
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.AudioCoverExtractor
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Base64Tools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ChecksumTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Cipher
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.CollageMaker
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ColorLibrary
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ColorTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Compare
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.CompressPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Crop
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.CropPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.DeleteExif
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.DocumentScanner
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Draw
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.EasterEgg
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.EditExif
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.EraseBackground
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ExtractImagesPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Filter
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.FlattenPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.FormatConversion
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.GifTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.GradientMaker
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.GrayscalePdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ImageCutter
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ImagePreview
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ImageSplitting
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ImageStacking
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ImageStitching
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.JxlTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.LibrariesInfo
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.LibraryDetails
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.LimitResize
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.LoadNetImage
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Main
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.MarkupLayers
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.MergePdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.MeshGradients
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.MetadataPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.NoiseGeneration
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.OCRPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.PageNumbersPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.PaletteTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.PdfTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.PickColorFromImage
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ProtectPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.RearrangePdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.RecognizeText
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.RemovePagesPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.RepairPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ResizeAndConvert
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.RotatePdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.ScanQrCode
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Settings
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.SignaturePdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.SingleEdit
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.SplitPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.SvgMaker
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.UnlockPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.WallpapersExport
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.WatermarkPdfTool
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Watermarking
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.WebpTools
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.WeightResize
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild.Zip
import com.t8rin.imagetoolbox.feature.root.presentation.screenLogic.RootComponent
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent
import com.t8rin.imagetoolbox.feature.single_edit.presentation.screenLogic.SingleEditComponent
import com.t8rin.imagetoolbox.feature.svg_maker.presentation.screenLogic.SvgMakerComponent
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.screenLogic.WallpapersExportComponent
import com.t8rin.imagetoolbox.feature.watermarking.presentation.screenLogic.WatermarkingComponent
import com.t8rin.imagetoolbox.feature.webp_tools.presentation.screenLogic.WebpToolsComponent
import com.t8rin.imagetoolbox.feature.weight_resize.presentation.screenLogic.WeightResizeComponent
import com.t8rin.imagetoolbox.feature.zip.presentation.screenLogic.ZipComponent
import com.t8rin.imagetoolbox.image_cutting.presentation.screenLogic.ImageCutterComponent
import com.t8rin.imagetoolbox.image_splitting.presentation.screenLogic.ImageSplitterComponent
import com.t8rin.imagetoolbox.library_details.presentation.screenLogic.LibraryDetailsComponent
import com.t8rin.imagetoolbox.noise_generation.presentation.screenLogic.NoiseGenerationComponent
import javax.inject.Inject

internal class ChildProvider @Inject constructor(
    private val apngToolsComponentFactory: ApngToolsComponent.Factory,
    private val cipherComponentFactory: CipherComponent.Factory,
    private val collageMakerComponentFactory: CollageMakerComponent.Factory,
    private val compareComponentFactory: CompareComponent.Factory,
    private val cropComponentFactory: CropComponent.Factory,
    private val deleteExifComponentFactory: DeleteExifComponent.Factory,
    private val documentScannerComponentFactory: DocumentScannerComponent.Factory,
    private val drawComponentFactory: DrawComponent.Factory,
    private val eraseBackgroundComponentFactory: EraseBackgroundComponent.Factory,
    private val filtersComponentFactory: FiltersComponent.Factory,
    private val formatConversionComponentFactory: FormatConversionComponent.Factory,
    private val paletteToolsComponentFactory: PaletteToolsComponent.Factory,
    private val gifToolsComponentFactory: GifToolsComponent.Factory,
    private val gradientMakerComponentFactory: GradientMakerComponent.Factory,
    private val imagePreviewComponentFactory: ImagePreviewComponent.Factory,
    private val imageSplittingComponentFactory: ImageSplitterComponent.Factory,
    private val imageStackingComponentFactory: ImageStackingComponent.Factory,
    private val imageStitchingComponentFactory: ImageStitchingComponent.Factory,
    private val jxlToolsComponentFactory: JxlToolsComponent.Factory,
    private val limitResizeComponentFactory: LimitsResizeComponent.Factory,
    private val loadNetImageComponentFactory: LoadNetImageComponent.Factory,
    private val noiseGenerationComponentFactory: NoiseGenerationComponent.Factory,
    private val pdfToolsComponentFactory: PdfToolsComponent.Factory,
    private val pickColorFromImageComponentFactory: PickColorFromImageComponent.Factory,
    private val recognizeTextComponentFactory: RecognizeTextComponent.Factory,
    private val resizeAndConvertComponentFactory: ResizeAndConvertComponent.Factory,
    private val scanQrCodeComponentFactory: ScanQrCodeComponent.Factory,
    private val settingsComponentFactory: SettingsComponent.Factory,
    private val singleEditComponentFactory: SingleEditComponent.Factory,
    private val svgMakerComponentFactory: SvgMakerComponent.Factory,
    private val watermarkingComponentFactory: WatermarkingComponent.Factory,
    private val webpToolsComponentFactory: WebpToolsComponent.Factory,
    private val weightResizeComponentFactory: WeightResizeComponent.Factory,
    private val zipComponentFactory: ZipComponent.Factory,
    private val easterEggComponentFactory: EasterEggComponent.Factory,
    private val colorToolsComponentFactory: ColorToolsComponent.Factory,
    private val librariesInfoComponentFactory: LibrariesInfoComponent.Factory,
    private val mainComponentFactory: MainComponent.Factory,
    private val markupLayersComponentFactory: MarkupLayersComponent.Factory,
    private val base64ToolsComponentFactory: Base64ToolsComponent.Factory,
    private val checksumToolsComponentFactory: ChecksumToolsComponent.Factory,
    private val meshGradientsComponentFactory: MeshGradientsComponent.Factory,
    private val editExifComponentFactory: EditExifComponent.Factory,
    private val imageCutterComponentFactory: ImageCutterComponent.Factory,
    private val audioCoverExtractorComponentFactory: AudioCoverExtractorComponent.Factory,
    private val libraryDetailsComponentFactory: LibraryDetailsComponent.Factory,
    private val wallpapersExportComponentFactory: WallpapersExportComponent.Factory,
    private val asciiArtComponentFactory: AsciiArtComponent.Factory,
    private val aiToolsComponentFactory: AiToolsComponent.Factory,
    private val colorLibraryComponentFactory: ColorLibraryComponent.Factory,
    private val mergePdfToolComponentFactory: MergePdfToolComponent.Factory,
    private val splitPdfToolComponentFactory: SplitPdfToolComponent.Factory,
    private val rotatePdfToolComponentFactory: RotatePdfToolComponent.Factory,
    private val rearrangePdfToolComponentFactory: RearrangePdfToolComponent.Factory,
    private val pageNumbersPdfToolComponentFactory: PageNumbersPdfToolComponent.Factory,
    private val ocrPdfToolComponentFactory: OCRPdfToolComponent.Factory,
    private val watermarkPdfToolComponentFactory: WatermarkPdfToolComponent.Factory,
    private val signaturePdfToolComponentFactory: SignaturePdfToolComponent.Factory,
    private val protectPdfToolComponentFactory: ProtectPdfToolComponent.Factory,
    private val unlockPdfToolComponentFactory: UnlockPdfToolComponent.Factory,
    private val compressPdfToolComponentFactory: CompressPdfToolComponent.Factory,
    private val grayscalePdfToolComponentFactory: GrayscalePdfToolComponent.Factory,
    private val repairPdfToolComponentFactory: RepairPdfToolComponent.Factory,
    private val metadataPdfToolComponentFactory: MetadataPdfToolComponent.Factory,
    private val removePagesPdfToolComponentFactory: RemovePagesPdfToolComponent.Factory,
    private val cropPdfToolComponentFactory: CropPdfToolComponent.Factory,
    private val flattenPdfToolComponentFactory: FlattenPdfToolComponent.Factory,
    private val extractImagesPdfToolComponentFactory: ExtractImagesPdfToolComponent.Factory,
) {
    fun RootComponent.createChild(
        config: Screen,
        componentContext: ComponentContext
    ): NavigationChild = when (config) {
        Screen.ColorTools -> ColorTools(
            colorToolsComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack
            )
        )

        Screen.EasterEgg -> EasterEgg(
            easterEggComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack
            )
        )

        Screen.Main -> Main(
            mainComponentFactory(
                componentContext = componentContext,
                onTryGetUpdate = ::tryGetUpdate,
                onGetClipList = ::updateUris,
                onNavigate = ::navigateToNew,
                isUpdateAvailable = isUpdateAvailable
            )
        )

        is Screen.ApngTools -> ApngTools(
            apngToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.Cipher -> Cipher(
            cipherComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack
            )
        )

        is Screen.CollageMaker -> CollageMaker(
            collageMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.Compare -> Compare(
            compareComponentFactory(
                componentContext = componentContext,
                initialComparableUris = config.uris
                    ?.takeIf { it.size == 2 }
                    ?.let { it[0] to it[1] },
                onGoBack = ::navigateBack
            )
        )

        is Screen.Crop -> Crop(
            cropComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.DeleteExif -> DeleteExif(
            deleteExifComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        Screen.DocumentScanner -> DocumentScanner(
            documentScannerComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.Draw -> Draw(
            drawComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.EraseBackground -> EraseBackground(
            eraseBackgroundComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.Filter -> Filter(
            filtersComponentFactory(
                componentContext = componentContext,
                initialType = config.type,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.FormatConversion -> FormatConversion(
            formatConversionComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.PaletteTools -> PaletteTools(
            paletteToolsComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack
            )
        )

        is Screen.GifTools -> GifTools(
            gifToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.GradientMaker -> GradientMaker(
            gradientMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.ImagePreview -> ImagePreview(
            imagePreviewComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.ImageSplitting -> ImageSplitting(
            imageSplittingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uri,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.ImageStacking -> ImageStacking(
            imageStackingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.ImageStitching -> ImageStitching(
            imageStitchingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.JxlTools -> JxlTools(
            jxlToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.LimitResize -> LimitResize(
            limitResizeComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.LoadNetImage -> LoadNetImage(
            loadNetImageComponentFactory(
                componentContext = componentContext,
                initialUrl = config.url,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )


        Screen.NoiseGeneration -> NoiseGeneration(
            noiseGenerationComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo,
            )
        )

        is Screen.PdfTools -> PdfTools(
            pdfToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.PickColorFromImage -> PickColorFromImage(
            pickColorFromImageComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack
            )
        )

        is Screen.RecognizeText -> RecognizeText(
            recognizeTextComponentFactory(
                componentContext = componentContext,
                initialType = config.type,
                onGoBack = ::navigateBack
            )
        )

        is Screen.ResizeAndConvert -> ResizeAndConvert(
            resizeAndConvertComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.ScanQrCode -> ScanQrCode(
            scanQrCodeComponentFactory(
                componentContext = componentContext,
                initialQrCodeContent = config.qrCodeContent,
                uriToAnalyze = config.uriToAnalyze,
                onGoBack = ::navigateBack
            )
        )

        is Screen.Settings -> Settings(
            settingsComponentFactory(
                componentContext = componentContext,
                onTryGetUpdate = ::tryGetUpdate,
                onNavigate = ::navigateToNew,
                isUpdateAvailable = isUpdateAvailable,
                onGoBack = ::navigateBack,
                initialSearchQuery = config.searchQuery
            )
        )

        is Screen.SingleEdit -> SingleEdit(
            singleEditComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onNavigate = ::navigateTo,
                onGoBack = ::navigateBack
            )
        )

        is Screen.SvgMaker -> SvgMaker(
            svgMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack
            )
        )

        is Screen.Watermarking -> Watermarking(
            watermarkingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.WebpTools -> WebpTools(
            webpToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.WeightResize -> WeightResize(
            weightResizeComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.Zip -> Zip(
            zipComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack
            )
        )

        Screen.LibrariesInfo -> LibrariesInfo(
            librariesInfoComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.MarkupLayers -> MarkupLayers(
            markupLayersComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.Base64Tools -> Base64Tools(
            base64ToolsComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.ChecksumTools -> ChecksumTools(
            checksumToolsComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack
            )
        )

        is Screen.MeshGradients -> MeshGradients(
            meshGradientsComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.EditExif -> EditExif(
            editExifComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.ImageCutter -> ImageCutter(
            imageCutterComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.AudioCoverExtractor -> AudioCoverExtractor(
            audioCoverExtractorComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.LibraryDetails -> LibraryDetails(
            libraryDetailsComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                libraryName = config.name,
                libraryDescription = config.htmlDescription,
                libraryLink = config.link
            )
        )

        is Screen.WallpapersExport -> WallpapersExport(
            wallpapersExportComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.AsciiArt -> AsciiArt(
            asciiArtComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri,
                onGoBack = ::navigateBack
            )
        )

        is Screen.AiTools -> AiTools(
            aiToolsComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris,
                onGoBack = ::navigateBack,
                onNavigate = ::navigateTo
            )
        )

        is Screen.ColorLibrary -> ColorLibrary(
            colorLibraryComponentFactory(
                componentContext = componentContext,
                onGoBack = ::navigateBack
            )
        )

        is Screen.PdfTools.Merge -> MergePdfTool(
            mergePdfToolComponentFactory(
                initialUris = config.uris,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Split -> SplitPdfTool(
            splitPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Rotate -> RotatePdfTool(
            rotatePdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Rearrange -> RearrangePdfTool(
            rearrangePdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.PageNumbers -> PageNumbersPdfTool(
            pageNumbersPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.OCR -> OCRPdfTool(
            ocrPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Watermark -> WatermarkPdfTool(
            watermarkPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Signature -> SignaturePdfTool(
            signaturePdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Protect -> ProtectPdfTool(
            protectPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Unlock -> UnlockPdfTool(
            unlockPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Compress -> CompressPdfTool(
            compressPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Grayscale -> GrayscalePdfTool(
            grayscalePdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Repair -> RepairPdfTool(
            repairPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Metadata -> MetadataPdfTool(
            metadataPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.RemovePages -> RemovePagesPdfTool(
            removePagesPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Crop -> CropPdfTool(
            cropPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.Flatten -> FlattenPdfTool(
            flattenPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )

        is Screen.PdfTools.ExtractImages -> ExtractImagesPdfTool(
            extractImagesPdfToolComponentFactory(
                initialUri = config.uri,
                componentContext = componentContext,
                onGoBack = ::navigateBack,
                onNavigate = ::replaceTo
            )
        )
    }
}