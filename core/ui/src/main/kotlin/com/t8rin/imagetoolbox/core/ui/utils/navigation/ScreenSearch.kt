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

package com.t8rin.imagetoolbox.core.ui.utils.navigation

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getStringEnglish
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.AiTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ApngTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.AppLogs
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.AsciiArt
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.AudioCoverExtractor
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Base64Tools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ChecksumTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Cipher
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.CollageMaker
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ColorLibrary
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ColorTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Compare
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Crop
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.DeleteExif
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.DocumentScanner
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Draw
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.EasterEgg
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.EditExif
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.EraseBackground
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Filter
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.FormatConversion
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.GifTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.GradientMaker
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Help
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ImageCutter
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ImagePreview
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ImageSplitting
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ImageStacking
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ImageStitching
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.JxlTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.LibrariesInfo
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.LibraryDetails
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.LimitResize
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.LoadNetImage
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Main
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.MarkupLayers
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.MeshGradients
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.NoiseGeneration
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.PaletteTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.PdfTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.PickColorFromImage
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.RecognizeText
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ResizeAndConvert
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ScanQrCode
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Settings
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ShaderStudio
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.SingleEdit
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.SvgMaker
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.TextureGeneration
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.UsageStatistics
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.WallpapersExport
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Watermarking
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.WebpTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.WeightResize
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Zip
import com.t8rin.imagetoolbox.core.utils.appContext

fun Screen.matchesSearchQuery(query: String): Boolean {
    val trimmedQuery = query.trim()

    if (trimmedQuery.isEmpty()) return true

    val keywordsRes = searchKeywordsRes()

    val searchableText = listOf(
        searchableTextFor(
            title = title,
            subtitle = subtitle
        ),
        keywordsRes?.let(appContext::getString).orEmpty(),
        keywordsRes?.let { appContext.getStringEnglish(it) }.orEmpty(),
        simpleName.replace("_", " "),
        typeSearchableText()
    ).joinToString(separator = " ")

    return searchableText.contains(
        other = trimmedQuery,
        ignoreCase = true
    ) || trimmedQuery.split(Regex("\\s+")).all { word ->
        searchableText.contains(
            other = word,
            ignoreCase = true
        )
    }
}

private fun Screen.typeSearchableText(): String = when (this) {
    is ApngTools -> type?.let { searchableTextFor(it.title, it.subtitle) }
    is Filter -> type?.let { searchableTextFor(it.title, it.subtitle) }
    is GifTools -> type?.let { searchableTextFor(it.title, it.subtitle) }
    is JxlTools -> type?.let { searchableTextFor(it.title, it.subtitle) }
    is RecognizeText -> type?.let { searchableTextFor(it.title, it.subtitle) }
    is WebpTools -> type?.let { searchableTextFor(it.title, it.subtitle) }
    else -> null
}.orEmpty()

private fun searchableTextFor(
    @StringRes title: Int,
    @StringRes subtitle: Int
): String = listOf(
    appContext.getString(title),
    appContext.getString(subtitle),
    appContext.getStringEnglish(title),
    appContext.getStringEnglish(subtitle)
).joinToString(separator = " ")

@StringRes
private fun Screen.searchKeywordsRes(): Int? = when (this) {
    is SingleEdit -> R.string.search_keywords_single_edit
    is ResizeAndConvert -> R.string.search_keywords_resize_and_convert
    is WeightResize -> R.string.search_keywords_weight_resize
    is Crop -> R.string.search_keywords_crop
    is Filter -> R.string.search_keywords_filter
    is Draw -> R.string.search_keywords_draw
    is Cipher -> R.string.search_keywords_cipher
    is EraseBackground -> R.string.search_keywords_erase_background
    is ImagePreview -> R.string.search_keywords_image_preview
    is ImageStitching -> R.string.search_keywords_image_stitching
    is LoadNetImage -> R.string.search_keywords_load_net_image
    is PickColorFromImage -> R.string.search_keywords_pick_color_from_image
    is PaletteTools -> R.string.search_keywords_palette_tools
    is DeleteExif -> R.string.search_keywords_delete_exif
    is Compare -> R.string.search_keywords_compare
    is LimitResize -> R.string.search_keywords_limit_resize
    is PdfTools -> R.string.search_keywords_pdf_tools
    is RecognizeText -> R.string.search_keywords_recognize_text
    is GradientMaker -> R.string.search_keywords_gradient_maker
    is Watermarking -> R.string.search_keywords_watermarking
    is GifTools -> R.string.search_keywords_gif_tools
    is ApngTools -> R.string.search_keywords_apng_tools
    is Zip -> R.string.search_keywords_zip
    is JxlTools -> R.string.search_keywords_jxl_tools
    is SvgMaker -> R.string.search_keywords_svg_maker
    is FormatConversion -> R.string.search_keywords_format_conversion
    is DocumentScanner -> R.string.search_keywords_document_scanner
    is ScanQrCode -> R.string.search_keywords_scan_qr_code
    is ImageStacking -> R.string.search_keywords_image_stacking
    is ImageSplitting -> R.string.search_keywords_image_splitting
    is ColorTools -> R.string.search_keywords_color_tools
    is WebpTools -> R.string.search_keywords_webp_tools
    is NoiseGeneration -> R.string.search_keywords_noise_generation
    is TextureGeneration -> R.string.search_keywords_texture_generation
    is CollageMaker -> R.string.search_keywords_collage_maker
    is MarkupLayers -> R.string.search_keywords_markup_layers
    is Base64Tools -> R.string.search_keywords_base64_tools
    is ChecksumTools -> R.string.search_keywords_checksum_tools
    is MeshGradients -> R.string.search_keywords_mesh_gradients
    is EditExif -> R.string.search_keywords_edit_exif
    is ImageCutter -> R.string.search_keywords_image_cutter
    is AudioCoverExtractor -> R.string.search_keywords_audio_cover_extractor
    is WallpapersExport -> R.string.search_keywords_wallpapers_export
    is AsciiArt -> R.string.search_keywords_ascii_art
    is AiTools -> R.string.search_keywords_ai_tools
    is ColorLibrary -> R.string.search_keywords_color_library
    is ShaderStudio -> R.string.search_keywords_shader_studio
    is PdfTools.Merge -> R.string.search_keywords_pdf_merge
    is PdfTools.Split -> R.string.search_keywords_pdf_split
    is PdfTools.Rotate -> R.string.search_keywords_pdf_rotate
    is PdfTools.Rearrange -> R.string.search_keywords_pdf_rearrange
    is PdfTools.PageNumbers -> R.string.search_keywords_pdf_page_numbers
    is PdfTools.OCR -> R.string.search_keywords_pdf_ocr
    is PdfTools.Watermark -> R.string.search_keywords_pdf_watermark
    is PdfTools.Signature -> R.string.search_keywords_pdf_signature
    is PdfTools.Protect -> R.string.search_keywords_pdf_protect
    is PdfTools.Unlock -> R.string.search_keywords_pdf_unlock
    is PdfTools.Compress -> R.string.search_keywords_pdf_compress
    is PdfTools.Grayscale -> R.string.search_keywords_pdf_grayscale
    is PdfTools.Repair -> R.string.search_keywords_pdf_repair
    is PdfTools.Metadata -> R.string.search_keywords_pdf_metadata
    is PdfTools.RemovePages -> R.string.search_keywords_pdf_remove_pages
    is PdfTools.Crop -> R.string.search_keywords_pdf_crop
    is PdfTools.Flatten -> R.string.search_keywords_pdf_flatten
    is PdfTools.ExtractImages -> R.string.search_keywords_pdf_extract_images
    is PdfTools.ZipConvert -> R.string.search_keywords_pdf_zip_convert
    is PdfTools.Print -> R.string.search_keywords_pdf_print
    is PdfTools.Preview -> R.string.search_keywords_pdf_preview
    is PdfTools.ImagesToPdf -> R.string.search_keywords_pdf_images_to_pdf
    is PdfTools.ExtractPages -> R.string.search_keywords_pdf_extract_pages
    is PdfTools.RemoveAnnotations -> R.string.search_keywords_pdf_remove_annotations
    is EasterEgg,
    is Main,
    is Settings,
    is AppLogs,
    is LibrariesInfo,
    is LibraryDetails,
    is Help,
    is UsageStatistics -> null
}