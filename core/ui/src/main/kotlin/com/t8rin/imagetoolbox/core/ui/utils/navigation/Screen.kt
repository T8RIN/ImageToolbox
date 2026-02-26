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

@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package com.t8rin.imagetoolbox.core.ui.utils.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Gif
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Apng
import com.t8rin.imagetoolbox.core.resources.icons.ArtTrack
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.resources.icons.Jpg
import com.t8rin.imagetoolbox.core.resources.icons.Jxl
import com.t8rin.imagetoolbox.core.resources.icons.Preview
import com.t8rin.imagetoolbox.core.resources.icons.Scanner
import com.t8rin.imagetoolbox.core.resources.icons.TextSearch
import com.t8rin.imagetoolbox.core.resources.icons.Webp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Stable
@Immutable
sealed class Screen(
    open val id: Int,
    @StringRes val title: Int,
    @StringRes val subtitle: Int
) {

    val isBetaFeature: Boolean by lazy { isBetaFeature() }

    val simpleName: String by lazy { simpleName() }

    val icon: ImageVector? by lazy { icon() }

    val twoToneIcon: ImageVector? by lazy { twoToneIcon() }

    @Serializable
    data class LibraryDetails(
        val name: String,
        val htmlDescription: String,
        val link: String?
    ) : Screen(
        id = -5,
        title = 0,
        subtitle = 0
    )

    @Serializable
    data object LibrariesInfo : Screen(
        id = -4,
        title = 0,
        subtitle = 0
    )

    @Serializable
    data class Settings(
        val searchQuery: String = ""
    ) : Screen(
        id = -3,
        title = 0,
        subtitle = 0
    )

    @Serializable
    data object EasterEgg : Screen(
        id = -2,
        title = 0,
        subtitle = 0
    )

    @Serializable
    data object Main : Screen(
        id = -1,
        title = 0,
        subtitle = 0
    )

    @Serializable
    data class SingleEdit(
        val uri: Uri? = null
    ) : Screen(
        id = 0,
        title = R.string.single_edit,
        subtitle = R.string.single_edit_sub
    )

    @Serializable
    data class ResizeAndConvert(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 1,
        title = R.string.resize_and_convert,
        subtitle = R.string.resize_and_convert_sub
    )

    @Serializable
    data class WeightResize(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 2,
        title = R.string.by_bytes_resize,
        subtitle = R.string.by_bytes_resize_sub
    )

    @Serializable
    data class Crop(
        val uri: Uri? = null
    ) : Screen(
        id = 3,
        title = R.string.crop,
        subtitle = R.string.crop_sub
    )

    @Serializable
    data class Filter(
        @SerialName("dataType") val type: Type? = null
    ) : Screen(
        id = 4,
        title = R.string.filter,
        subtitle = R.string.filter_sub
    ) {
        @Serializable
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) {

            val icon: ImageVector
                get() = when (this) {
                    is Masking -> Icons.Rounded.Texture
                    is Basic -> Icons.Outlined.AutoFixHigh
                }

            @Serializable
            data class Masking(
                val uri: Uri? = null
            ) : Type(
                title = R.string.mask_filter,
                subtitle = R.string.mask_filter_sub
            )

            @Serializable
            data class Basic(
                val uris: List<Uri>? = null
            ) : Type(
                title = R.string.full_filter,
                subtitle = R.string.full_filter_sub
            )

            companion object {
                val entries by lazy {
                    listOf(
                        Basic(),
                        Masking()
                    )
                }
            }
        }
    }

    @Serializable
    data class Draw(
        val uri: Uri? = null
    ) : Screen(
        id = 5,
        title = R.string.draw,
        subtitle = R.string.draw_sub
    )

    @Serializable
    data class Cipher(
        val uri: Uri? = null
    ) : Screen(
        id = 6,
        title = R.string.cipher,
        subtitle = R.string.cipher_sub
    )

    @Serializable
    data class EraseBackground(
        val uri: Uri? = null
    ) : Screen(
        id = 7,
        title = R.string.background_remover,
        subtitle = R.string.background_remover_sub
    )

    @Serializable
    data class ImagePreview(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 8,
        title = R.string.image_preview,
        subtitle = R.string.image_preview_sub
    )

    @Serializable
    data class ImageStitching(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 9,
        title = R.string.image_stitching,
        subtitle = R.string.image_stitching_sub
    )

    @Serializable
    data class LoadNetImage(
        val url: String = ""
    ) : Screen(
        id = 10,
        title = R.string.load_image_from_net,
        subtitle = R.string.load_image_from_net_sub
    )

    @Serializable
    data class PickColorFromImage(
        val uri: Uri? = null
    ) : Screen(
        id = 11,
        title = R.string.pick_color,
        subtitle = R.string.pick_color_sub
    )

    @Serializable
    data class PaletteTools(
        val uri: Uri? = null
    ) : Screen(
        id = 12,
        title = R.string.palette_tools,
        subtitle = R.string.palette_tools_sub
    )

    @Serializable
    data class DeleteExif(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 13,
        title = R.string.delete_exif,
        subtitle = R.string.delete_exif_sub
    )

    @Serializable
    data class Compare(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 14,
        title = R.string.compare,
        subtitle = R.string.compare_sub
    )

    @Serializable
    data class LimitResize(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 15,
        title = R.string.limits_resize,
        subtitle = R.string.limits_resize_sub
    )

    @Serializable
    data class PdfTools(
        @SerialName("dataType") val type: Type? = null
    ) : Screen(
        id = 16,
        title = R.string.pdf_tools,
        subtitle = R.string.pdf_tools_sub
    ) {
        @Serializable
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) {

            val icon: ImageVector
                get() = when (this) {
                    is ImagesToPdf -> Icons.Outlined.Scanner
                    is PdfToImages -> Icons.Outlined.ArtTrack
                    is Preview -> Icons.Outlined.Preview
                }

            @Serializable
            data class Preview(
                val pdfUri: Uri? = null
            ) : Type(
                title = R.string.preview_pdf,
                subtitle = R.string.preview_pdf_sub
            )

            @Serializable
            data class PdfToImages(
                val pdfUri: Uri? = null
            ) : Type(
                title = R.string.pdf_to_images,
                subtitle = R.string.pdf_to_images_sub
            )

            @Serializable
            data class ImagesToPdf(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.images_to_pdf,
                subtitle = R.string.images_to_pdf_sub
            )

            companion object {
                val entries by lazy {
                    listOf(
                        Preview(),
                        PdfToImages(),
                        ImagesToPdf()
                    )
                }
            }
        }

        companion object {
            val options: List<Screen> by lazy {
                listOf(
                    Merge(),
                    Split(),
                    RemovePages(),
                    Rotate(),
                    Rearrange(),
                    Crop(),
                    PageNumbers(),
                    Watermark(),
                    Signature(),
                    Compress(),
                    Flatten(),
                    Print(),
                    Grayscale(),
                    Repair(),
                    Protect(),
                    Unlock(),
                    Metadata(),
                    ExtractImages(),
                    OCR(),
                    ZipConvert(),
                )
            }
        }

        @Serializable
        data class Merge(
            val uris: List<Uri>? = null
        ) : Screen(
            id = 44,
            title = R.string.merge_pdf,
            subtitle = R.string.merge_pdf_sub
        )

        @Serializable
        data class Split(
            val uri: Uri? = null
        ) : Screen(
            id = 45,
            title = R.string.split_pdf,
            subtitle = R.string.split_pdf_sub
        )

        @Serializable
        data class Rotate(
            val uri: Uri? = null
        ) : Screen(
            id = 46,
            title = R.string.rotate_pdf,
            subtitle = R.string.rotate_pdf_sub
        )

        @Serializable
        data class Rearrange(
            val uri: Uri? = null
        ) : Screen(
            id = 47,
            title = R.string.rearrange_pdf,
            subtitle = R.string.rearrange_pdf_sub
        )

        @Serializable
        data class PageNumbers(
            val uri: Uri? = null
        ) : Screen(
            id = 48,
            title = R.string.page_numbers,
            subtitle = R.string.page_numbers_sub
        )

        @Serializable
        data class OCR(
            val uri: Uri? = null
        ) : Screen(
            id = 49,
            title = R.string.pdf_to_text,
            subtitle = R.string.pdf_to_text_sub
        )

        @Serializable
        data class Watermark(
            val uri: Uri? = null
        ) : Screen(
            id = 50,
            title = R.string.watermarking,
            subtitle = R.string.watermark_pdf_sub
        )

        @Serializable
        data class Signature(
            val uri: Uri? = null
        ) : Screen(
            id = 51,
            title = R.string.signature,
            subtitle = R.string.signature_sub
        )

        @Serializable
        data class Protect(
            val uri: Uri? = null
        ) : Screen(
            id = 52,
            title = R.string.protect_pdf,
            subtitle = R.string.protect_pdf_sub
        )

        @Serializable
        data class Unlock(
            val uri: Uri? = null
        ) : Screen(
            id = 53,
            title = R.string.unlock_pdf,
            subtitle = R.string.unlock_pdf_sub
        )

        @Serializable
        data class Compress(
            val uri: Uri? = null
        ) : Screen(
            id = 54,
            title = R.string.compress_pdf,
            subtitle = R.string.compress_pdf_sub
        )

        @Serializable
        data class Grayscale(
            val uri: Uri? = null
        ) : Screen(
            id = 55,
            title = R.string.grayscale,
            subtitle = R.string.grayscale_pdf_sub
        )

        @Serializable
        data class Repair(
            val uri: Uri? = null
        ) : Screen(
            id = 56,
            title = R.string.repair_pdf,
            subtitle = R.string.repair_pdf_sub
        )

        @Serializable
        data class Metadata(
            val uri: Uri? = null
        ) : Screen(
            id = 57,
            title = R.string.metadata,
            subtitle = R.string.metadata_pdf_sub
        )

        @Serializable
        data class RemovePages(
            val uri: Uri? = null
        ) : Screen(
            id = 58,
            title = R.string.remove_pages_pdf,
            subtitle = R.string.remove_pages_pdf_sub
        )

        @Serializable
        data class Crop(
            val uri: Uri? = null
        ) : Screen(
            id = 59,
            title = R.string.crop_pdf,
            subtitle = R.string.crop_pdf_sub
        )

        @Serializable
        data class Flatten(
            val uri: Uri? = null
        ) : Screen(
            id = 60,
            title = R.string.flatten_pdf,
            subtitle = R.string.flatten_pdf_sub
        )

        @Serializable
        data class ExtractImages(
            val uri: Uri? = null
        ) : Screen(
            id = 61,
            title = R.string.extract_images,
            subtitle = R.string.extract_images_sub
        )

        @Serializable
        data class ZipConvert(
            val uri: Uri? = null
        ) : Screen(
            id = 62,
            title = R.string.zip_pdf,
            subtitle = R.string.zip_pdf_sub
        )

        @Serializable
        data class Print(
            val uri: Uri? = null
        ) : Screen(
            id = 63,
            title = R.string.print_pdf,
            subtitle = R.string.print_pdf_sub
        )

    }

    @Serializable
    data class RecognizeText(
        @SerialName("dataType") val type: Type? = null
    ) : Screen(
        id = 17,
        title = R.string.recognize_text,
        subtitle = R.string.recognize_text_sub
    ) {
        @Serializable
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) {

            val icon: ImageVector
                get() = when (this) {
                    is Extraction -> Icons.Outlined.TextSearch
                    is WriteToFile -> Icons.Outlined.FilePresent
                    is WriteToMetadata -> Icons.Outlined.Exif
                }

            @Serializable
            data class Extraction(
                val uri: Uri? = null
            ) : Type(
                title = R.string.recognize_text,
                subtitle = R.string.recognize_text_sub
            )

            @Serializable
            data class WriteToFile(
                val uris: List<Uri>? = null
            ) : Type(
                title = R.string.ocr_write_to_file,
                subtitle = R.string.ocr_write_to_file_sub
            )

            @Serializable
            data class WriteToMetadata(
                val uris: List<Uri>? = null
            ) : Type(
                title = R.string.ocr_write_to_metadata,
                subtitle = R.string.ocr_write_to_metadata_sub
            )

            companion object {
                val entries by lazy {
                    listOf(
                        Extraction(),
                        WriteToFile(),
                        WriteToMetadata()
                    )
                }
            }
        }
    }

    @Serializable
    data class GradientMaker(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 18,
        title = R.string.gradient_maker,
        subtitle = R.string.gradient_maker_sub,
    )

    @Serializable
    data class Watermarking(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 19,
        title = R.string.watermarking,
        subtitle = R.string.watermarking_sub,
    )

    @Serializable
    data class GifTools(
        @SerialName("dataType") val type: Type? = null
    ) : Screen(
        id = 20,
        title = R.string.gif_tools,
        subtitle = R.string.gif_tools_sub
    ) {
        @Serializable
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) {

            val icon: ImageVector
                get() = when (this) {
                    is GifToImage -> Icons.Outlined.Collections
                    is GifToJxl -> Icons.Filled.Jxl
                    is ImageToGif -> Icons.Rounded.Gif
                    is GifToWebp -> Icons.Rounded.Webp
                }

            @Serializable
            data class GifToImage(
                val gifUri: Uri? = null
            ) : Type(
                title = R.string.gif_type_to_image,
                subtitle = R.string.gif_type_to_image_sub
            )

            @Serializable
            data class ImageToGif(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.gif_type_to_gif,
                subtitle = R.string.gif_type_to_gif_sub
            )

            @Serializable
            data class GifToJxl(
                val gifUris: List<Uri>? = null
            ) : Type(
                title = R.string.gif_type_to_jxl,
                subtitle = R.string.gif_type_to_jxl_sub
            )

            @Serializable
            data class GifToWebp(
                val gifUris: List<Uri>? = null
            ) : Type(
                title = R.string.gif_type_to_webp,
                subtitle = R.string.gif_type_to_webp_sub
            )

            companion object {
                val entries by lazy {
                    listOf(
                        ImageToGif(),
                        GifToImage(),
                        GifToJxl(),
                        GifToWebp()
                    )
                }
            }
        }
    }

    @Serializable
    data class ApngTools(
        @SerialName("dataType") val type: Type? = null
    ) : Screen(
        id = 21,
        title = R.string.apng_tools,
        subtitle = R.string.apng_tools_sub
    ) {
        @Serializable
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) {

            val icon: ImageVector
                get() = when (this) {
                    is ApngToImage -> Icons.Outlined.Collections
                    is ApngToJxl -> Icons.Filled.Jxl
                    is ImageToApng -> Icons.Rounded.Apng
                }

            @Serializable
            data class ApngToImage(
                val apngUri: Uri? = null
            ) : Type(
                title = R.string.apng_type_to_image,
                subtitle = R.string.apng_type_to_image_sub
            )

            @Serializable
            data class ImageToApng(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.apng_type_to_apng,
                subtitle = R.string.apng_type_to_apng_sub
            )

            @Serializable
            data class ApngToJxl(
                val apngUris: List<Uri>? = null
            ) : Type(
                title = R.string.apng_type_to_jxl,
                subtitle = R.string.apng_type_to_jxl_sub
            )

            companion object {
                val entries by lazy {
                    listOf(
                        ImageToApng(),
                        ApngToImage(),
                        ApngToJxl()
                    )
                }
            }
        }
    }

    @Serializable
    data class Zip(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 22,
        title = R.string.zip,
        subtitle = R.string.zip_sub
    )

    @Serializable
    data class JxlTools(
        @SerialName("dataType") val type: Type? = null
    ) : Screen(
        id = 23,
        title = R.string.jxl_tools,
        subtitle = R.string.jxl_tools_sub
    ) {
        @Serializable
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) {

            val icon: ImageVector
                get() = when (this) {
                    is ImageToJxl -> Icons.Rounded.Animation
                    is JpegToJxl -> Icons.Filled.Jxl
                    is JxlToImage -> Icons.Outlined.Collections
                    is JxlToJpeg -> Icons.Outlined.Jpg
                }

            @Serializable
            data class JxlToJpeg(
                val jxlImageUris: List<Uri>? = null
            ) : Type(
                title = R.string.jxl_type_to_jpeg,
                subtitle = R.string.jxl_type_to_jpeg_sub
            )

            @Serializable
            data class JpegToJxl(
                val jpegImageUris: List<Uri>? = null
            ) : Type(
                title = R.string.jpeg_type_to_jxl,
                subtitle = R.string.jpeg_type_to_jxl_sub
            )

            @Serializable
            data class JxlToImage(
                val jxlUri: Uri? = null
            ) : Type(
                title = R.string.jxl_type_to_images,
                subtitle = R.string.jxl_type_to_images_sub
            )

            @Serializable
            data class ImageToJxl(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.jxl_type_to_jxl,
                subtitle = R.string.jxl_type_to_jxl_sub
            )

            companion object {
                val entries by lazy {
                    listOf(
                        JpegToJxl(),
                        JxlToJpeg(),
                        JxlToImage(),
                        ImageToJxl()
                    )
                }
            }
        }
    }

    @Serializable
    data class SvgMaker(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 24,
        title = R.string.images_to_svg,
        subtitle = R.string.images_to_svg_sub
    )

    @Serializable
    data class FormatConversion(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 25,
        title = R.string.format_conversion,
        subtitle = R.string.format_conversion_sub
    )

    @Serializable
    data object DocumentScanner : Screen(
        id = 26,
        title = R.string.document_scanner,
        subtitle = R.string.document_scanner_sub
    )

    @Serializable
    data class ScanQrCode(
        val qrCodeContent: String? = null,
        val uriToAnalyze: Uri? = null
    ) : Screen(
        id = 27,
        title = R.string.qr_code,
        subtitle = R.string.barcodes_sub
    )

    @Serializable
    data class ImageStacking(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 28,
        title = R.string.image_stacking,
        subtitle = R.string.image_stacking_sub
    )

    @Serializable
    data class ImageSplitting(
        val uri: Uri? = null
    ) : Screen(
        id = 29,
        title = R.string.image_splitting,
        subtitle = R.string.image_splitting_sub
    )

    @Serializable
    data object ColorTools : Screen(
        id = 30,
        title = R.string.color_tools,
        subtitle = R.string.color_tools_sub
    )

    @Serializable
    data class WebpTools(
        @SerialName("dataType") val type: Type? = null
    ) : Screen(
        id = 31,
        title = R.string.webp_tools,
        subtitle = R.string.webp_tools_sub
    ) {
        @Serializable
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) {

            val icon: ImageVector
                get() = when (this) {
                    is WebpToImage -> Icons.Outlined.Collections
                    is ImageToWebp -> Icons.Rounded.Webp
                }

            @Serializable
            data class WebpToImage(
                val webpUri: Uri? = null
            ) : Type(
                title = R.string.webp_type_to_image,
                subtitle = R.string.webp_type_to_image_sub
            )

            @Serializable
            data class ImageToWebp(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.webp_type_to_webp,
                subtitle = R.string.webp_type_to_webp_sub
            )

            companion object {
                val entries by lazy {
                    listOf(
                        ImageToWebp(),
                        WebpToImage()
                    )
                }
            }
        }
    }

    @Serializable
    data object NoiseGeneration : Screen(
        id = 32,
        title = R.string.noise_generation,
        subtitle = R.string.noise_generation_sub
    )

    @Serializable
    data class CollageMaker(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 33,
        title = R.string.collage_maker,
        subtitle = R.string.collage_maker_sub
    )

    @Serializable
    data class MarkupLayers(
        val uri: Uri? = null
    ) : Screen(
        id = 34,
        title = R.string.markup_layers,
        subtitle = R.string.markup_layers_sub
    )

    @Serializable
    data class Base64Tools(
        val uri: Uri? = null
    ) : Screen(
        id = 35,
        title = R.string.base_64_tools,
        subtitle = R.string.base_64_tools_sub
    )

    @Serializable
    data class ChecksumTools(
        val uri: Uri? = null,
    ) : Screen(
        id = 36,
        title = R.string.checksum_tools,
        subtitle = R.string.checksum_tools_sub
    )

    @Serializable
    data object MeshGradients : Screen(
        id = -5,
        title = 0,
        subtitle = 0
    )

    @Serializable
    data class EditExif(
        val uri: Uri? = null,
    ) : Screen(
        id = 37,
        title = R.string.edit_exif_screen,
        subtitle = R.string.edit_exif_screen_sub
    )

    @Serializable
    data class ImageCutter(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 38,
        title = R.string.image_cutting,
        subtitle = R.string.image_cutting_sub
    )

    @Serializable
    data class AudioCoverExtractor(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 39,
        title = R.string.audio_cover_extractor,
        subtitle = R.string.audio_cover_extractor_sub
    )

    @Serializable
    data object WallpapersExport : Screen(
        id = 40,
        title = R.string.wallpapers_export,
        subtitle = R.string.wallpapers_export_sub
    )

    @Serializable
    data class AsciiArt(
        val uri: Uri? = null,
    ) : Screen(
        id = 41,
        title = R.string.ascii_art,
        subtitle = R.string.ascii_art_sub
    )

    @Serializable
    data class AiTools(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 42,
        title = R.string.ai_tools,
        subtitle = R.string.ai_tools_sub
    )

    @Serializable
    data object ColorLibrary : Screen(
        id = 43,
        title = R.string.color_library,
        subtitle = R.string.color_library_sub
    )

    companion object : ScreenConstants by ScreenConstants
}

data class ScreenGroup(
    val entries: List<Screen>,
    @StringRes val title: Int,
    val selectedIcon: ImageVector,
    val baseIcon: ImageVector
) {
    fun icon(isSelected: Boolean) = if (isSelected) selectedIcon else baseIcon
}