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

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.collage_maker.presentation.CollageMakerContent
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.color_library.presentation.ColorLibraryContent
import com.t8rin.imagetoolbox.color_library.presentation.screenLogic.ColorLibraryComponent
import com.t8rin.imagetoolbox.color_tools.presentation.ColorToolsContent
import com.t8rin.imagetoolbox.color_tools.presentation.screenLogic.ColorToolsComponent
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.AiToolsContent
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.screenLogic.AiToolsComponent
import com.t8rin.imagetoolbox.feature.apng_tools.presentation.ApngToolsContent
import com.t8rin.imagetoolbox.feature.apng_tools.presentation.screenLogic.ApngToolsComponent
import com.t8rin.imagetoolbox.feature.archive_tools.presentation.ArchiveToolsContent
import com.t8rin.imagetoolbox.feature.archive_tools.presentation.screenLogic.ArchiveToolsComponent
import com.t8rin.imagetoolbox.feature.ascii_art.presentation.AsciiArtContent
import com.t8rin.imagetoolbox.feature.ascii_art.presentation.screenLogic.AsciiArtComponent
import com.t8rin.imagetoolbox.feature.assistant.presentation.AssistantContent
import com.t8rin.imagetoolbox.feature.assistant.presentation.screenLogic.AssistantComponent
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.ui.AudioCoverExtractorContent
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.ui.screenLogic.AudioCoverExtractorComponent
import com.t8rin.imagetoolbox.feature.base64_tools.presentation.Base64ToolsContent
import com.t8rin.imagetoolbox.feature.base64_tools.presentation.screenLogic.Base64ToolsComponent
import com.t8rin.imagetoolbox.feature.batchrename.presentation.BatchRenameContent
import com.t8rin.imagetoolbox.feature.batchrename.presentation.screenLogic.BatchRenameComponent
import com.t8rin.imagetoolbox.feature.checksum_tools.presentation.ChecksumToolsContent
import com.t8rin.imagetoolbox.feature.checksum_tools.presentation.screenLogic.ChecksumToolsComponent
import com.t8rin.imagetoolbox.feature.cipher.presentation.CipherContent
import com.t8rin.imagetoolbox.feature.cipher.presentation.screenLogic.CipherComponent
import com.t8rin.imagetoolbox.feature.code_preview.presentation.CodePreviewContent
import com.t8rin.imagetoolbox.feature.code_preview.presentation.screenLogic.CodePreviewComponent
import com.t8rin.imagetoolbox.feature.compare.presentation.CompareContent
import com.t8rin.imagetoolbox.feature.compare.presentation.screenLogic.CompareComponent
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.CompressionLabContent
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.screenLogic.CompressionLabComponent
import com.t8rin.imagetoolbox.feature.crop.presentation.CropContent
import com.t8rin.imagetoolbox.feature.crop.presentation.screenLogic.CropComponent
import com.t8rin.imagetoolbox.feature.curves.presentation.CurvesContent
import com.t8rin.imagetoolbox.feature.curves.presentation.screenLogic.CurvesComponent
import com.t8rin.imagetoolbox.feature.delete_exif.presentation.DeleteExifContent
import com.t8rin.imagetoolbox.feature.delete_exif.presentation.screenLogic.DeleteExifComponent
import com.t8rin.imagetoolbox.feature.document_scanner.presentation.DocumentScannerContent
import com.t8rin.imagetoolbox.feature.document_scanner.presentation.screenLogic.DocumentScannerComponent
import com.t8rin.imagetoolbox.feature.draw.presentation.DrawContent
import com.t8rin.imagetoolbox.feature.draw.presentation.screenLogic.DrawComponent
import com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.DuplicateFinderContent
import com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.screenLogic.DuplicateFinderComponent
import com.t8rin.imagetoolbox.feature.easter_egg.presentation.EasterEggContent
import com.t8rin.imagetoolbox.feature.easter_egg.presentation.screenLogic.EasterEggComponent
import com.t8rin.imagetoolbox.feature.edit_exif.presentation.EditExifContent
import com.t8rin.imagetoolbox.feature.edit_exif.presentation.screenLogic.EditExifComponent
import com.t8rin.imagetoolbox.feature.erase_background.presentation.EraseBackgroundContent
import com.t8rin.imagetoolbox.feature.erase_background.presentation.screenLogic.EraseBackgroundComponent
import com.t8rin.imagetoolbox.feature.filters.presentation.FiltersContent
import com.t8rin.imagetoolbox.feature.filters.presentation.screenLogic.FiltersComponent
import com.t8rin.imagetoolbox.feature.format_conversion.presentation.FormatConversionContent
import com.t8rin.imagetoolbox.feature.format_conversion.presentation.screenLogic.FormatConversionComponent
import com.t8rin.imagetoolbox.feature.fractal_generation.presentation.FractalGenerationContent
import com.t8rin.imagetoolbox.feature.fractal_generation.presentation.screenLogic.FractalGenerationComponent
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.GifToolsContent
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.screenLogic.GifToolsComponent
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.GradientMakerContent
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent
import com.t8rin.imagetoolbox.feature.help.presentation.HelpContent
import com.t8rin.imagetoolbox.feature.help.presentation.screenLogic.HelpComponent
import com.t8rin.imagetoolbox.feature.image_preview.presentation.ImagePreviewContent
import com.t8rin.imagetoolbox.feature.image_preview.presentation.screenLogic.ImagePreviewComponent
import com.t8rin.imagetoolbox.feature.image_stacking.presentation.ImageStackingContent
import com.t8rin.imagetoolbox.feature.image_stacking.presentation.screenLogic.ImageStackingComponent
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.ImageStitchingContent
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.screenLogic.ImageStitchingComponent
import com.t8rin.imagetoolbox.feature.jxl_tools.presentation.JxlToolsContent
import com.t8rin.imagetoolbox.feature.jxl_tools.presentation.screenLogic.JxlToolsComponent
import com.t8rin.imagetoolbox.feature.libraries_info.presentation.LibrariesInfoContent
import com.t8rin.imagetoolbox.feature.libraries_info.presentation.screenLogic.LibrariesInfoComponent
import com.t8rin.imagetoolbox.feature.limits_resize.presentation.LimitsResizeContent
import com.t8rin.imagetoolbox.feature.limits_resize.presentation.screenLogic.LimitsResizeComponent
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.LoadNetImageContent
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent
import com.t8rin.imagetoolbox.feature.main.presentation.MainContent
import com.t8rin.imagetoolbox.feature.main.presentation.screenLogic.MainComponent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.MarkupLayersContent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import com.t8rin.imagetoolbox.feature.media_picker.presentation.components.MediaPickerRootContentEmbeddable
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent
import com.t8rin.imagetoolbox.feature.mesh_gradients.presentation.MeshGradientsContent
import com.t8rin.imagetoolbox.feature.mesh_gradients.presentation.screenLogic.MeshGradientsComponent
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.presentation.MultiFrameFusionContent
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.presentation.screenLogic.MultiFrameFusionComponent
import com.t8rin.imagetoolbox.feature.palette_pdf.presentation.PalettePdfToolContent
import com.t8rin.imagetoolbox.feature.palette_pdf.presentation.screenLogic.PalettePdfToolComponent
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.PaletteToolsContent
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.screenLogic.PaletteToolsComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.comic_to_pdf.ComicToPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.comic_to_pdf.screenLogic.ComicToPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compare.ComparePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compare.screenLogic.ComparePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compress.CompressPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compress.screenLogic.CompressPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.contact_sheet.PdfContactSheetToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.contact_sheet.screenLogic.PdfContactSheetToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop.CropPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop.screenLogic.CropPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode.PdfDarkModeToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode.screenLogic.PdfDarkModeToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.extract_images.ExtractImagesPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.extract_images.screenLogic.ExtractImagesPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.extract_pages.ExtractPagesPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.extract_pages.screenLogic.ExtractPagesPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.flatten.FlattenPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.flatten.screenLogic.FlattenPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.grayscale.GrayscalePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.grayscale.screenLogic.GrayscalePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.images_to_pdf.ImagesToPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.images_to_pdf.screenLogic.ImagesToPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.merge.MergePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.merge.screenLogic.MergePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.metadata.MetadataPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.metadata.screenLogic.MetadataPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.ocr.OCRPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.ocr.screenLogic.OCRPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.page_numbers.PageNumbersPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.page_numbers.screenLogic.PageNumbersPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.preview.PreviewPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.preview.screenLogic.PreviewPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.print.PrintPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.print.screenLogic.PrintPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.protect.ProtectPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.protect.screenLogic.ProtectPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rearrange.RearrangePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rearrange.screenLogic.RearrangePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.remove_annotations.RemoveAnnotationsPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.remove_annotations.screenLogic.RemoveAnnotationsPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.remove_pages.RemovePagesPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.remove_pages.screenLogic.RemovePagesPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.repair.RepairPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.repair.screenLogic.RepairPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.resize_pages.ResizePdfPagesToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.resize_pages.screenLogic.ResizePdfPagesToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.RootPdfToolsContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.screenLogic.RootPdfToolsComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rotate.RotatePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rotate.screenLogic.RotatePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.sanitize.SanitizePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.sanitize.screenLogic.SanitizePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.SignaturePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.screenLogic.SignaturePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.split.SplitPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.split.screenLogic.SplitPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.unlock.UnlockPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.unlock.screenLogic.UnlockPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.watermark.WatermarkPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.watermark.screenLogic.WatermarkPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.zip_convert.ZipConvertPdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.zip_convert.screenLogic.ZipConvertPdfToolComponent
import com.t8rin.imagetoolbox.feature.photomosaic.presentation.PhotomosaicContent
import com.t8rin.imagetoolbox.feature.photomosaic.presentation.screenLogic.PhotomosaicComponent
import com.t8rin.imagetoolbox.feature.pick_color.presentation.PickColorFromImageContent
import com.t8rin.imagetoolbox.feature.pick_color.presentation.screenLogic.PickColorFromImageComponent
import com.t8rin.imagetoolbox.feature.recognize.text.presentation.RecognizeTextContent
import com.t8rin.imagetoolbox.feature.recognize.text.presentation.screenLogic.RecognizeTextComponent
import com.t8rin.imagetoolbox.feature.resize_convert.presentation.ResizeAndConvertContent
import com.t8rin.imagetoolbox.feature.resize_convert.presentation.screenLogic.ResizeAndConvertComponent
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.ScanQrCodeContent
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.ScreenshotFramingContent
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.screenLogic.ScreenshotFramingComponent
import com.t8rin.imagetoolbox.feature.settings.presentation.SettingsCategoryContent
import com.t8rin.imagetoolbox.feature.settings.presentation.SettingsContent
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.ShaderStudioContent
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic.ShaderStudioComponent
import com.t8rin.imagetoolbox.feature.single_edit.presentation.SingleEditContent
import com.t8rin.imagetoolbox.feature.single_edit.presentation.screenLogic.SingleEditComponent
import com.t8rin.imagetoolbox.feature.svg_maker.presentation.SvgMakerContent
import com.t8rin.imagetoolbox.feature.svg_maker.presentation.screenLogic.SvgMakerComponent
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.UsageStatisticsContent
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.screenLogic.UsageStatisticsComponent
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.WallpapersExportContent
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.screenLogic.WallpapersExportComponent
import com.t8rin.imagetoolbox.feature.watermarking.presentation.WatermarkingContent
import com.t8rin.imagetoolbox.feature.watermarking.presentation.screenLogic.WatermarkingComponent
import com.t8rin.imagetoolbox.feature.webp_tools.presentation.WebpToolsContent
import com.t8rin.imagetoolbox.feature.webp_tools.presentation.screenLogic.WebpToolsComponent
import com.t8rin.imagetoolbox.feature.weight_resize.presentation.WeightResizeContent
import com.t8rin.imagetoolbox.feature.weight_resize.presentation.screenLogic.WeightResizeComponent
import com.t8rin.imagetoolbox.image_cutting.presentation.ImageCutterContent
import com.t8rin.imagetoolbox.image_cutting.presentation.screenLogic.ImageCutterComponent
import com.t8rin.imagetoolbox.image_splitting.presentation.ImageSplitterContent
import com.t8rin.imagetoolbox.image_splitting.presentation.screenLogic.ImageSplitterComponent
import com.t8rin.imagetoolbox.library_details.presentation.LibraryDetailsContent
import com.t8rin.imagetoolbox.library_details.presentation.screenLogic.LibraryDetailsComponent
import com.t8rin.imagetoolbox.noise_generation.presentation.NoiseGenerationContent
import com.t8rin.imagetoolbox.noise_generation.presentation.screenLogic.NoiseGenerationComponent
import com.t8rin.imagetoolbox.presentation.app_logs.AppLogsContent
import com.t8rin.imagetoolbox.presentation.app_logs.screenLogic.AppLogsComponent
import com.t8rin.imagetoolbox.texture_generation.presentation.TextureGenerationContent
import com.t8rin.imagetoolbox.texture_generation.presentation.screenLogic.TextureGenerationComponent


internal sealed interface NavigationChild {

    @Composable
    fun Content()

    fun assistantScreen(source: Screen?): Screen.Assistant = source.assistantScreen()


    class ApngTools(private val component: ApngToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) =
            Screen.ApngTools(component.type).assistantScreen(
                when (val type = component.type) {
                    is Screen.ApngTools.Type.ApngToImage -> listOfNotNull(type.apngUri)
                    is Screen.ApngTools.Type.ImageToApng -> type.imageUris.orEmpty()
                    is Screen.ApngTools.Type.ApngToJxl -> type.apngUris.orEmpty()
                    is Screen.ApngTools.Type.ApngToGif -> type.apngUris.orEmpty()
                    is Screen.ApngTools.Type.ApngToWebp -> type.apngUris.orEmpty()
                    is Screen.ApngTools.Type.MergeApng -> type.apngUris.orEmpty()
                    null -> emptyList()
                },
                "mode" to component.type?.title?.let(appContext::getString),
                "imageFormat" to component.imageFormat.title,
                "isSaving" to component.isSaving
            )

        @Composable
        override fun Content() = ApngToolsContent(component)
    }

    class Cipher(private val component: CipherComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "mode" to if (component.isEncrypt) "encrypt" else "decrypt",
            "cipher" to component.cipherType
        )

        @Composable
        override fun Content() = CipherContent(component)
    }

    class CollageMaker(private val component: CollageMakerComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue,
            "aspectRatio" to component.aspectRatio
        )

        @Composable
        override fun Content() = CollageMakerContent(component)
    }

    class ColorTools(private val component: ColorToolsComponent) : NavigationChild {
        @Composable
        override fun Content() = ColorToolsContent(component)
    }

    class Compare(private val component: CompareComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.bitmapData?.first?.uri, component.bitmapData?.second?.uri)
        )

        @Composable
        override fun Content() = CompareContent(component)
    }

    class Crop(private val component: CropComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.currentUri),
            "imageFormat" to component.imageFormat.title,
            "rotation" to component.rotationState.floatValue,
            "keepExif" to component.saveExif
        )

        @Composable
        override fun Content() = CropContent(component)
    }

    class DeleteExif(private val component: DeleteExifComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = Screen.Assistant(
            source = Screen.DeleteExif(component.uris),
            parameters = mapOf("removalPreset" to component.removalPreset.name),
            selectedUris = component.uris.orEmpty()
        )

        @Composable
        override fun Content() = DeleteExifContent(component)
    }

    class BatchRename(private val component: BatchRenameComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.files.map { it.uri.toUri() },
            "dateSource" to component.dateSource
        )

        @Composable
        override fun Content() = BatchRenameContent(component)
    }

    class DocumentScanner(private val component: DocumentScannerComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue
        )

        @Composable
        override fun Content() = DocumentScannerContent(component)
    }

    class Draw(private val component: DrawComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOf(component.uri),
            "imageFormat" to component.imageFormat.title,
            "strokeCount" to component.paths.size,
            "keepExif" to component.saveExif
        )

        @Composable
        override fun Content() = DrawContent(component)
    }

    class DuplicateFinder(private val component: DuplicateFinderComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.sourceUris,
            "sensitivity" to component.sensitivity,
            "scanMode" to component.scanMode,
            "keepStrategy" to component.keepStrategy,
            "isAnalyzing" to component.isAnalyzing
        )

        @Composable
        override fun Content() = DuplicateFinderContent(component)
    }

    class EasterEgg(private val component: EasterEggComponent) : NavigationChild {
        @Composable
        override fun Content() = EasterEggContent(component)
    }

    class EraseBackground(private val component: EraseBackgroundComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = Screen.Assistant(
            source = Screen.EraseBackground(component.uri.takeUnless { it == Uri.EMPTY }),
            parameters = mapOf(
                "imageFormat" to component.imageFormat.title,
                "restoreMode" to component.isRecoveryOn.toString(),
                "trimImage" to component.trimImage.toString(),
                "isRemovingBackground" to component.isErasingBG.toString(),
                "keepExif" to component.saveExif.toString()
            ),
            selectedUris = listOfNotNull(component.uri.takeUnless { it == Uri.EMPTY })
        )

        @Composable
        override fun Content() = EraseBackgroundContent(component)
    }

    class Filter(private val component: FiltersComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = when (val type = component.filterType) {
            is Screen.Filter.Type.Basic -> {
                val state = component.basicFilterState
                val filterKey = state.filters.singleOrNull()?.let {
                    appContext.resources.getResourceEntryName(it.title)
                }
                Screen.Filter(Screen.Filter.Type.Basic(state.uris, filterKey)).assistantScreen(
                    state.uris,
                    "mode" to appContext.getString(type.title),
                    "filterCount" to state.filters.size,
                    "filterNames" to state.filters.take(3)
                        .joinToString { appContext.getString(it.title) },
                    "filterKey" to filterKey
                )
            }

            is Screen.Filter.Type.Masking -> Screen.Assistant(
                source = Screen.Filter(Screen.Filter.Type.Masking(component.maskingFilterState.uri)),
                parameters = mapOf(
                    "mode" to appContext.getString(type.title),
                    "filterCount" to component.maskingFilterState.masks.size.toString()
                ),
                selectedUris = listOfNotNull(component.maskingFilterState.uri)
            )

            null -> Screen.Assistant(source = source)
        }

        @Composable
        override fun Content() = FiltersContent(component)
    }

    class FormatConversion(private val component: FormatConversionComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageInfo.imageFormat.title,
            "quality" to component.imageInfo.quality.qualityValue,
            "keepExif" to component.keepExif
        )

        @Composable
        override fun Content() = FormatConversionContent(component)
    }

    class Curves(private val component: CurvesComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageInfo.imageFormat.title,
            "keepExif" to component.keepExif
        )

        @Composable
        override fun Content() = CurvesContent(component)
    }

    class PaletteTools(private val component: PaletteToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            null,
            "paletteType" to component.paletteType
        )

        @Composable
        override fun Content() = PaletteToolsContent(component)
    }

    class PalettePdfTool(private val component: PalettePdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.sourceUri),
            "sourceType" to component.sourceType,
            "maximumColorCount" to component.params.maximumColorCount
        )

        @Composable
        override fun Content() = PalettePdfToolContent(component)
    }

    class GifTools(private val component: GifToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) =
            Screen.GifTools(component.type).assistantScreen(
                when (val type = component.type) {
                    is Screen.GifTools.Type.GifToImage -> listOfNotNull(type.gifUri)
                    is Screen.GifTools.Type.MergeGif -> type.gifUris.orEmpty()
                    is Screen.GifTools.Type.ImageToGif -> type.imageUris.orEmpty()
                    is Screen.GifTools.Type.GifToJxl -> type.gifUris.orEmpty()
                    is Screen.GifTools.Type.GifToApng -> type.gifUris.orEmpty()
                    is Screen.GifTools.Type.GifToWebp -> type.gifUris.orEmpty()
                    null -> emptyList()
                },
                "mode" to component.type?.title?.let(appContext::getString),
                "imageFormat" to component.imageFormat.title,
                "isSaving" to component.isSaving
            )

        @Composable
        override fun Content() = GifToolsContent(component)
    }

    class GradientMaker(private val component: GradientMakerComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageFormat.title,
            "gradientType" to component.gradientType,
            "angle" to component.angle,
            "opacity" to component.gradientAlpha
        )

        @Composable
        override fun Content() = GradientMakerContent(component)
    }

    class ImagePreview(private val component: ImagePreviewComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris
        )

        @Composable
        override fun Content() = ImagePreviewContent(component)
    }

    class ImageSplitting(private val component: ImageSplitterComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri)
        )

        @Composable
        override fun Content() = ImageSplitterContent(component)
    }

    class ImageStacking(private val component: ImageStackingComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.stackImages.map { it.uri.toUri() },
            "imageFormat" to component.imageInfo.imageFormat.title
        )

        @Composable
        override fun Content() = ImageStackingContent(component)
    }

    class Photomosaic(private val component: PhotomosaicComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue,
            "tileCount" to component.tileUris.size
        )

        @Composable
        override fun Content() = PhotomosaicContent(component)
    }

    class MultiFrameFusion(private val component: MultiFrameFusionComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue,
            "canProcess" to component.canProcess
        )

        @Composable
        override fun Content() = MultiFrameFusionContent(component)
    }

    class CompressionLab(private val component: CompressionLabComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "mode" to component.searchMode,
            "quality" to component.manualQuality,
            "targetQuality" to component.targetQuality,
            "targetSizeKb" to component.targetSizeKb,
            "formats" to component.selectedFormats.joinToString { it.title }
        )

        @Composable
        override fun Content() = CompressionLabContent(component)
    }

    class CodePreview(private val component: CodePreviewComponent) : NavigationChild {
        @Composable
        override fun Content() = CodePreviewContent(component)
    }

    class ScreenshotFraming(private val component: ScreenshotFramingComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "isPreviewLoading" to component.isPreviewLoading
        )

        @Composable
        override fun Content() = ScreenshotFramingContent(component)
    }

    class Gallery(
        private val component: MediaPickerComponent,
        private val onGoBack: () -> Unit,
        private val onPicked: (List<Uri>) -> Unit
    ) : NavigationChild {
        @Composable
        override fun Content() = MediaPickerRootContentEmbeddable(
            component = component,
            onPicked = onPicked,
            isGalleryMode = true,
            onBack = onGoBack
        )
    }

    class ImageStitching(private val component: ImageStitchingComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageInfo.imageFormat.title,
            "quality" to component.imageInfo.quality.qualityValue
        )

        @Composable
        override fun Content() = ImageStitchingContent(component)
    }

    class JxlTools(private val component: JxlToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) =
            Screen.JxlTools(component.type).assistantScreen(
                when (val type = component.type) {
                    is Screen.JxlTools.Type.JxlToJpeg -> type.jxlImageUris.orEmpty()
                    is Screen.JxlTools.Type.JpegToJxl -> type.jpegImageUris.orEmpty()
                    is Screen.JxlTools.Type.JxlToImage -> listOfNotNull(type.jxlUri)
                    is Screen.JxlTools.Type.ImageToJxl -> type.imageUris.orEmpty()
                    is Screen.JxlTools.Type.JxlToGif -> type.jxlUris.orEmpty()
                    is Screen.JxlTools.Type.JxlToApng -> type.jxlUris.orEmpty()
                    is Screen.JxlTools.Type.JxlToWebp -> type.jxlUris.orEmpty()
                    is Screen.JxlTools.Type.MergeJxl -> type.jxlUris.orEmpty()
                    null -> emptyList()
                },
                "mode" to component.type?.title?.let(appContext::getString),
                "imageFormat" to component.imageFormat.title,
                "isSaving" to component.isSaving
            )

        @Composable
        override fun Content() = JxlToolsContent(component)
    }

    class LimitResize(private val component: LimitsResizeComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageInfo.imageFormat.title,
            "width" to component.imageInfo.width,
            "height" to component.imageInfo.height,
            "keepExif" to component.keepExif
        )

        @Composable
        override fun Content() = LimitsResizeContent(component)
    }

    class LoadNetImage(private val component: LoadNetImageComponent) : NavigationChild {
        @Composable
        override fun Content() = LoadNetImageContent(component)
    }

    class Main(private val component: MainComponent) : NavigationChild {
        @Composable
        override fun Content() = MainContent(component)
    }

    class FractalGeneration(private val component: FractalGenerationComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            null,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue,
            "width" to component.outputSize.width,
            "height" to component.outputSize.height
        )

        @Composable
        override fun Content() = FractalGenerationContent(component)
    }

    class NoiseGeneration(private val component: NoiseGenerationComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            null,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue,
            "width" to component.noiseSize.width,
            "height" to component.noiseSize.height
        )

        @Composable
        override fun Content() = NoiseGenerationContent(component)
    }

    class TextureGeneration(private val component: TextureGenerationComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            null,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue,
            "width" to component.textureSize.width,
            "height" to component.textureSize.height
        )

        @Composable
        override fun Content() = TextureGenerationContent(component)
    }

    class RootPdfTools(private val component: RootPdfToolsComponent) : NavigationChild {
        @Composable
        override fun Content() = RootPdfToolsContent(component)
    }

    class PickColorFromImage(private val component: PickColorFromImageComponent) : NavigationChild {
        @Composable
        override fun Content() = PickColorFromImageContent(component)
    }

    class RecognizeText(private val component: RecognizeTextComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) =
            Screen.RecognizeText(component.type).assistantScreen(
                component.uris.ifEmpty { listOfNotNull(component.currentImageUri) },
                "mode" to component.type?.title?.let(appContext::getString)
        )

        @Composable
        override fun Content() = RecognizeTextContent(component)
    }

    class ResizeAndConvert(private val component: ResizeAndConvertComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = Screen.Assistant(
            source = Screen.ResizeAndConvert(component.uris),
            parameters = mapOf(
                "imageFormat" to component.imageInfo.imageFormat.title,
                "quality" to component.imageInfo.quality.qualityValue.toString(),
                "width" to component.imageInfo.width.toString(),
                "height" to component.imageInfo.height.toString(),
                "keepExif" to component.keepExif.toString()
            ),
            selectedUris = component.uris.orEmpty()
        )

        @Composable
        override fun Content() = ResizeAndConvertContent(component)
    }

    class ScanQrCode(private val component: ScanQrCodeComponent) : NavigationChild {
        @Composable
        override fun Content() = ScanQrCodeContent(component)
    }

    class Settings(private val component: SettingsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = Screen.Assistant(
            source = Screen.Settings(component.searchKeyword, component.targetSetting),
            settingKey = (component.filteredSettings?.singleOrNull()?.second
                ?: component.targetSetting)?.key()
        )

        @Composable
        override fun Content() {
            val category = SettingsGroup.entries.firstOrNull {
                component.targetSetting in it.settingsList
            }.takeIf { LocalSettingsState.current.useCategorizedSettings }

            if (category != null) {
                SettingsCategoryContent(component, category.id)
            } else {
                SettingsContent(component)
            }
        }
    }

    class SettingsCategory(
        private val component: SettingsComponent,
        private val categoryId: Int
    ) : NavigationChild {
        override fun assistantScreen(source: Screen?) = Screen.Assistant(
            source = Screen.Settings(component.searchKeyword, component.targetSetting),
            settingKey = (component.filteredSettings?.singleOrNull()?.second
                ?: component.targetSetting)?.key()
        )

        @Composable
        override fun Content() = SettingsCategoryContent(component, categoryId)
    }

    class ShaderStudio(private val component: ShaderStudioComponent) : NavigationChild {
        @Composable
        override fun Content() = ShaderStudioContent(component)
    }

    class Help(private val component: HelpComponent) : NavigationChild {
        @Composable
        override fun Content() = HelpContent(component)
    }

    class Assistant(private val component: AssistantComponent) : NavigationChild {
        @Composable
        override fun Content() = AssistantContent(component)
    }

    class SingleEdit(private val component: SingleEditComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = Screen.Assistant(
            source = Screen.SingleEdit(component.uri.takeUnless { it == Uri.EMPTY }),
            parameters = mapOf(
                "imageFormat" to component.imageInfo.imageFormat.title,
                "quality" to component.imageInfo.quality.qualityValue.toString(),
                "width" to component.imageInfo.width.toString(),
                "height" to component.imageInfo.height.toString(),
                "filterCount" to component.filterList.size.toString(),
                "strokeCount" to component.drawPaths.size.toString()
            ),
            selectedUris = listOfNotNull(component.uri.takeUnless { it == Uri.EMPTY })
        )

        @Composable
        override fun Content() = SingleEditContent(component)
    }

    class SvgMaker(private val component: SvgMakerComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "isSaving" to component.isSaving
        )

        @Composable
        override fun Content() = SvgMakerContent(component)
    }

    class Watermarking(private val component: WatermarkingComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue,
            "keepExif" to component.keepExif
        )

        @Composable
        override fun Content() = WatermarkingContent(component)
    }

    class WebpTools(private val component: WebpToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) =
            Screen.WebpTools(component.type).assistantScreen(
                when (val type = component.type) {
                    is Screen.WebpTools.Type.WebpToImage -> listOfNotNull(type.webpUri)
                    is Screen.WebpTools.Type.ImageToWebp -> type.imageUris.orEmpty()
                    is Screen.WebpTools.Type.WebpToGif -> type.webpUris.orEmpty()
                    is Screen.WebpTools.Type.WebpToApng -> type.webpUris.orEmpty()
                    is Screen.WebpTools.Type.WebpToJxl -> type.webpUris.orEmpty()
                    is Screen.WebpTools.Type.MergeWebp -> type.webpUris.orEmpty()
                    null -> emptyList()
                },
                "mode" to component.type?.title?.let(appContext::getString),
                "imageFormat" to component.imageFormat.title,
                "isSaving" to component.isSaving
            )

        @Composable
        override fun Content() = WebpToolsContent(component)
    }

    class WeightResize(private val component: WeightResizeComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = Screen.Assistant(
            source = Screen.WeightResize(uris = component.uris),
            parameters = mapOf(
                "maxBytes" to component.maxBytes.toString(),
                "imageFormat" to component.imageFormat.title,
                "keepExif" to component.keepExif.toString(),
                "saveIfSmaller" to component.saveIfSmaller.toString()
            ),
            selectedUris = component.uris.orEmpty()
        )

        @Composable
        override fun Content() = WeightResizeContent(component)
    }

    class ArchiveTools(private val component: ArchiveToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "mode" to component.mode,
            "passwordProtection" to component.protectWithPassword
        )

        @Composable
        override fun Content() = ArchiveToolsContent(component)
    }

    class LibrariesInfo(private val component: LibrariesInfoComponent) : NavigationChild {
        @Composable
        override fun Content() = LibrariesInfoContent(component)
    }

    class AppLogs(private val component: AppLogsComponent) : NavigationChild {
        @Composable
        override fun Content() = AppLogsContent(component)
    }

    class UsageStatistics(private val component: UsageStatisticsComponent) : NavigationChild {
        @Composable
        override fun Content() = UsageStatisticsContent(component)
    }

    class MarkupLayers(private val component: MarkupLayersComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            null,
            "imageFormat" to component.imageFormat.title,
            "layerCount" to component.layers.size,
            "keepExif" to component.saveExif
        )

        @Composable
        override fun Content() = MarkupLayersContent(component)
    }

    class Base64Tools(private val component: Base64ToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue
        )

        @Composable
        override fun Content() = Base64ToolsContent(component)
    }

    class ChecksumTools(private val component: ChecksumToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.calculateFromUriPage.uri, component.compareWithUriPage.uri) +
                    component.compareWithUrisPage.uris.map { it.uri },
            "hashingType" to component.hashingType
        )

        @Composable
        override fun Content() = ChecksumToolsContent(component)
    }

    class MeshGradients(private val component: MeshGradientsComponent) : NavigationChild {
        @Composable
        override fun Content() = MeshGradientsContent(component)
    }

    class EditExif(private val component: EditExifComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOf(component.uri),
            "imageFormat" to component.imageFormat.title
        )

        @Composable
        override fun Content() = EditExifContent(component)
    }

    class ImageCutter(private val component: ImageCutterComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue
        )

        @Composable
        override fun Content() = ImageCutterContent(component)
    }

    class AudioCoverExtractor(
        private val component: AudioCoverExtractorComponent
    ) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.covers.map { it.audioUri },
            "imageFormat" to component.imageFormat.title,
            "quality" to component.quality.qualityValue,
            "isSaving" to component.isSaving
        )

        @Composable
        override fun Content() = AudioCoverExtractorContent(component)
    }

    class LibraryDetails(private val component: LibraryDetailsComponent) : NavigationChild {
        @Composable
        override fun Content() = LibraryDetailsContent(component)
    }

    class WallpapersExport(private val component: WallpapersExportComponent) : NavigationChild {
        @Composable
        override fun Content() = WallpapersExportContent(component)
    }

    class AsciiArt(private val component: AsciiArtComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOf(component.uri),
            "fontSize" to component.fontSize,
            "invertImage" to component.isInvertImage
        )

        @Composable
        override fun Content() = AsciiArtContent(component)
    }

    class AiTools(private val component: AiToolsComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "model" to component.selectedModel.value?.title,
            "imageFormat" to component.imageFormat?.title
        )

        @Composable
        override fun Content() = AiToolsContent(component)
    }

    class ColorLibrary(private val component: ColorLibraryComponent) : NavigationChild {
        @Composable
        override fun Content() = ColorLibraryContent(component)
    }

    class MergePdfTool(private val component: MergePdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = MergePdfToolContent(component)
    }

    class ComparePdfTool(private val component: ComparePdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.firstUri, component.secondUri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = ComparePdfToolContent(component)
    }

    class ResizePdfPagesTool(private val component: ResizePdfPagesToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = ResizePdfPagesToolContent(component)
    }

    class SanitizePdfTool(private val component: SanitizePdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = SanitizePdfToolContent(component)
    }

    class SplitPdfTool(private val component: SplitPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = SplitPdfToolContent(component)
    }

    class RotatePdfTool(private val component: RotatePdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = RotatePdfToolContent(component)
    }

    class RearrangePdfTool(private val component: RearrangePdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = RearrangePdfToolContent(component)
    }

    class PageNumbersPdfTool(private val component: PageNumbersPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = PageNumbersPdfToolContent(component)
    }

    class OCRPdfTool(private val component: OCRPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = OCRPdfToolContent(component)
    }

    class WatermarkPdfTool(private val component: WatermarkPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = WatermarkPdfToolContent(component)
    }

    class SignaturePdfTool(private val component: SignaturePdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = SignaturePdfToolContent(component)
    }

    class ProtectPdfTool(private val component: ProtectPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = ProtectPdfToolContent(component)
    }

    class UnlockPdfTool(private val component: UnlockPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = UnlockPdfToolContent(component)
    }

    class CompressPdfTool(private val component: CompressPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = CompressPdfToolContent(component)
    }

    class GrayscalePdfTool(private val component: GrayscalePdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = GrayscalePdfToolContent(component)
    }

    class PdfDarkModeTool(private val component: PdfDarkModeToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = PdfDarkModeToolContent(component)
    }

    class RepairPdfTool(private val component: RepairPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = RepairPdfToolContent(component)
    }

    class MetadataPdfTool(private val component: MetadataPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = MetadataPdfToolContent(component)
    }

    class RemovePagesPdfTool(private val component: RemovePagesPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = RemovePagesPdfToolContent(component)
    }

    class CropPdfTool(private val component: CropPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = CropPdfToolContent(component)
    }

    class FlattenPdfTool(private val component: FlattenPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = FlattenPdfToolContent(component)
    }

    class ExtractImagesPdfTool(private val component: ExtractImagesPdfToolComponent) :
        NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = ExtractImagesPdfToolContent(component)
    }

    class ZipConvertPdfTool(private val component: ZipConvertPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = ZipConvertPdfToolContent(component)
    }

    class ComicToPdfTool(private val component: ComicToPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = ComicToPdfToolContent(component)
    }

    class PrintPdfTool(private val component: PrintPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = PrintPdfToolContent(component)
    }

    class PreviewPdfTool(private val component: PreviewPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = PreviewPdfToolContent(component)
    }

    class ImagesToPdfTool(private val component: ImagesToPdfToolComponent) : NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "quality" to component.quality,
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = ImagesToPdfToolContent(component)
    }

    class PdfContactSheetTool(private val component: PdfContactSheetToolComponent) :
        NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            component.uris,
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = PdfContactSheetToolContent(component)
    }

    class ExtractPagesPdfTool(private val component: ExtractPagesPdfToolComponent) :
        NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = ExtractPagesPdfToolContent(component)
    }

    class RemoveAnnotationsPdfTool(private val component: RemoveAnnotationsPdfToolComponent) :
        NavigationChild {
        override fun assistantScreen(source: Screen?) = source.assistantScreen(
            listOfNotNull(component.uri),
            "haveChanges" to component.haveChanges
        )

        @Composable
        override fun Content() = RemoveAnnotationsPdfToolContent(component)
    }
}


private fun Screen?.assistantScreen(
    uris: List<Uri>? = null,
    vararg parameters: Pair<String, Any?>
): Screen.Assistant = Screen.Assistant(
    source = this,
    selectedUris = uris.orEmpty().filter { it != Uri.EMPTY }.distinct(),
    parameters = buildMap {
        this@assistantScreen?.let { put("toolKey", it.simpleName) }
        parameters.forEach { (key, value) ->
            value?.let { put(key, it.toString()) }
        }
    }
)
