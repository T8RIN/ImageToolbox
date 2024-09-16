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

package ru.tech.imageresizershrinker.core.ui.utils.navigation

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.BrandingWatermark
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterHdr
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.FilterHdr
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.GifBox
import androidx.compose.material.icons.outlined.Gradient
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Gif
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.parcelize.Parcelize
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Apng
import ru.tech.imageresizershrinker.core.resources.icons.ApngBox
import ru.tech.imageresizershrinker.core.resources.icons.CropSmall
import ru.tech.imageresizershrinker.core.resources.icons.Encrypted
import ru.tech.imageresizershrinker.core.resources.icons.Exif
import ru.tech.imageresizershrinker.core.resources.icons.ImageCombine
import ru.tech.imageresizershrinker.core.resources.icons.ImageConvert
import ru.tech.imageresizershrinker.core.resources.icons.ImageDownload
import ru.tech.imageresizershrinker.core.resources.icons.ImageEdit
import ru.tech.imageresizershrinker.core.resources.icons.ImageLimit
import ru.tech.imageresizershrinker.core.resources.icons.ImageSync
import ru.tech.imageresizershrinker.core.resources.icons.ImageText
import ru.tech.imageresizershrinker.core.resources.icons.ImageWeight
import ru.tech.imageresizershrinker.core.resources.icons.Jpg
import ru.tech.imageresizershrinker.core.resources.icons.Jxl
import ru.tech.imageresizershrinker.core.resources.icons.MultipleImageEdit
import ru.tech.imageresizershrinker.core.resources.icons.PaletteSwatch
import ru.tech.imageresizershrinker.core.resources.icons.Stack
import ru.tech.imageresizershrinker.core.resources.icons.Svg
import ru.tech.imageresizershrinker.core.resources.icons.Toolbox
import ru.tech.imageresizershrinker.core.resources.icons.Transparency
import ru.tech.imageresizershrinker.core.resources.icons.Webp
import ru.tech.imageresizershrinker.core.resources.icons.WebpBox

@Parcelize
sealed class Screen(
    open val id: Int,
    @StringRes val title: Int,
    @StringRes val subtitle: Int
) : Parcelable {

    @Suppress("unused")
    val simpleName: String?
        get() = when (this) {
            is ApngTools -> "APNG_Tools"
            is Cipher -> "Cipher"
            is Compare -> "Compare"
            is Crop -> "Crop"
            is DeleteExif -> "Delete_Exif"
            is Draw -> "Draw"
            EasterEgg -> "Easter_Egg"
            is EraseBackground -> "Erase_Background"
            is Filter -> "Filter"
            is GeneratePalette -> "Generate_Palette"
            is GifTools -> "GIF_Tools"
            is GradientMaker -> "Gradient_Maker"
            is ImagePreview -> "Image_Preview"
            is ImageStitching -> "Image_Stitching"
            is JxlTools -> "JXL_Tools"
            is LimitResize -> "Limit_Resize"
            is LoadNetImage -> "Load_Net_Image"
            Main -> null
            is PdfTools -> "PDF_Tools"
            is PickColorFromImage -> "Pick_Color_From_Image"
            is RecognizeText -> "Recognize_Text"
            is ResizeAndConvert -> "Resize_And_Convert"
            is WeightResize -> "Resize_By_Bytes"
            Settings -> "Settings"
            is SingleEdit -> "Single_Edit"
            is Watermarking -> "Watermarking"
            is Zip -> "Zip"
            is SvgMaker -> "Svg"
            is FormatConversion -> "Convert"
            is DocumentScanner -> "Document_Scanner"
            is ScanQrCode -> "QR_Code"
            is ImageStacking -> "Image_Stacking"
            is ImageSplitting -> "Image_Splitting"
            is ColorTools -> "Color_Tools"
            is WebpTools -> "WEBP_Tools"
            is NoiseGeneration -> "Noise_Generation"
            is CollageMaker -> "Collage_Maker"
        }

    val icon: ImageVector?
        get() = when (this) {
            EasterEgg,
            Main,
            Settings -> null

            is SingleEdit -> Icons.Outlined.ImageEdit
            is ApngTools -> Icons.Rounded.ApngBox
            is Cipher -> Icons.Outlined.Encrypted
            is Compare -> Icons.Rounded.Compare
            is Crop -> Icons.Rounded.CropSmall
            is DeleteExif -> Icons.Outlined.Exif
            is Draw -> Icons.Outlined.Draw
            is EraseBackground -> Icons.Filled.Transparency
            is Filter -> Icons.Outlined.AutoFixHigh
            is GeneratePalette -> Icons.Outlined.PaletteSwatch
            is GifTools -> Icons.Outlined.GifBox
            is GradientMaker -> Icons.Outlined.Gradient
            is ImagePreview -> Icons.Outlined.Photo
            is ImageStitching -> Icons.Rounded.ImageCombine
            is JxlTools -> Icons.Filled.Jxl
            is LimitResize -> Icons.Outlined.ImageLimit
            is LoadNetImage -> Icons.Outlined.ImageDownload
            is PdfTools -> Icons.Outlined.PictureAsPdf
            is PickColorFromImage -> Icons.Outlined.Colorize
            is RecognizeText -> Icons.Outlined.ImageText
            is ResizeAndConvert -> Icons.Rounded.MultipleImageEdit
            is WeightResize -> Icons.Rounded.ImageWeight
            is Watermarking -> Icons.AutoMirrored.Outlined.BrandingWatermark
            is Zip -> Icons.Outlined.FolderZip
            is SvgMaker -> Icons.Outlined.Svg
            is FormatConversion -> Icons.Outlined.ImageConvert
            is DocumentScanner -> Icons.Outlined.DocumentScanner
            is ScanQrCode -> Icons.Outlined.QrCode
            is ImageStacking -> Icons.Outlined.Stack
            is ImageSplitting -> Icons.Outlined.ContentCut
            ColorTools -> Icons.Outlined.ColorLens
            is WebpTools -> Icons.Rounded.WebpBox
            NoiseGeneration -> Icons.Outlined.Grain
            is CollageMaker -> Icons.Outlined.AutoAwesomeMosaic
        }

    data object Settings : Screen(
        id = -3,
        title = 0,
        subtitle = 0
    )

    data object EasterEgg : Screen(
        id = -2,
        title = 0,
        subtitle = 0
    )

    data object Main : Screen(
        id = -1,
        title = 0,
        subtitle = 0
    )

    data class SingleEdit(
        val uri: Uri? = null
    ) : Screen(
        id = 0,
        title = R.string.single_edit,
        subtitle = R.string.single_edit_sub
    )

    data class ResizeAndConvert(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 1,
        title = R.string.resize_and_convert,
        subtitle = R.string.resize_and_convert_sub
    )

    data class WeightResize(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 2,
        title = R.string.by_bytes_resize,
        subtitle = R.string.by_bytes_resize_sub
    )

    data class Crop(
        val uri: Uri? = null
    ) : Screen(
        id = 3,
        title = R.string.crop,
        subtitle = R.string.crop_sub
    )

    data class Filter(
        val type: Type? = null
    ) : Screen(
        id = 4,
        title = R.string.filter,
        subtitle = R.string.filter_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) : Parcelable {

            val icon: ImageVector
                get() = when (this) {
                    is Masking -> Icons.Rounded.Texture
                    is Basic -> Icons.Outlined.AutoFixHigh
                }

            data class Masking(
                val uri: Uri? = null
            ) : Type(
                title = R.string.mask_filter,
                subtitle = R.string.mask_filter_sub
            )

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

    data class Draw(
        val uri: Uri? = null
    ) : Screen(
        id = 5,
        title = R.string.draw,
        subtitle = R.string.draw_sub
    )

    data class Cipher(
        val uri: Uri? = null
    ) : Screen(
        id = 6,
        title = R.string.cipher,
        subtitle = R.string.cipher_sub
    )

    data class EraseBackground(
        val uri: Uri? = null
    ) : Screen(
        id = 7,
        title = R.string.background_remover,
        subtitle = R.string.background_remover_sub
    )

    data class ImagePreview(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 8,
        title = R.string.image_preview,
        subtitle = R.string.image_preview_sub
    )

    data class ImageStitching(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 9,
        title = R.string.image_stitching,
        subtitle = R.string.image_stitching_sub
    )

    data class LoadNetImage(
        val url: String = ""
    ) : Screen(
        id = 10,
        title = R.string.load_image_from_net,
        subtitle = R.string.load_image_from_net_sub
    )

    data class PickColorFromImage(
        val uri: Uri? = null
    ) : Screen(
        id = 11,
        title = R.string.pick_color,
        subtitle = R.string.pick_color_sub
    )

    data class GeneratePalette(
        val uri: Uri? = null
    ) : Screen(
        id = 12,
        title = R.string.generate_palette,
        subtitle = R.string.palette_sub
    )

    data class DeleteExif(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 13,
        title = R.string.delete_exif,
        subtitle = R.string.delete_exif_sub
    )

    data class Compare(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 14,
        title = R.string.compare,
        subtitle = R.string.compare_sub
    )

    data class LimitResize(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 15,
        title = R.string.limits_resize,
        subtitle = R.string.limits_resize_sub
    )

    data class PdfTools(
        val type: Type? = null
    ) : Screen(
        id = 16,
        title = R.string.pdf_tools,
        subtitle = R.string.pdf_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) : Parcelable {

            val icon: ImageVector
                get() = when (this) {
                    is ImagesToPdf -> Icons.Outlined.PictureAsPdf
                    is PdfToImages -> Icons.Outlined.Collections
                    is Preview -> Icons.Rounded.Preview
                }

            data class Preview(
                val pdfUri: Uri? = null
            ) : Type(
                title = R.string.preview_pdf,
                subtitle = R.string.preview_pdf_sub
            )

            data class PdfToImages(
                val pdfUri: Uri? = null
            ) : Type(
                title = R.string.pdf_to_images,
                subtitle = R.string.pdf_to_images_sub
            )

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
    }

    data class RecognizeText(
        val uri: Uri? = null
    ) : Screen(
        id = 17,
        title = R.string.recognize_text,
        subtitle = R.string.recognize_text_sub
    )

    data class GradientMaker(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 18,
        title = R.string.gradient_maker,
        subtitle = R.string.gradient_maker_sub,
    )

    data class Watermarking(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 19,
        title = R.string.watermarking,
        subtitle = R.string.watermarking_sub,
    )

    data class GifTools(
        val type: Type? = null
    ) : Screen(
        id = 20,
        title = R.string.gif_tools,
        subtitle = R.string.gif_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) : Parcelable {

            val icon: ImageVector
                get() = when (this) {
                    is GifToImage -> Icons.Outlined.Collections
                    is GifToJxl -> Icons.Filled.Jxl
                    is ImageToGif -> Icons.Rounded.Gif
                    is GifToWebp -> Icons.Rounded.Webp
                }

            data class GifToImage(
                val gifUri: Uri? = null
            ) : Type(
                title = R.string.gif_type_to_image,
                subtitle = R.string.gif_type_to_image_sub
            )

            data class ImageToGif(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.gif_type_to_gif,
                subtitle = R.string.gif_type_to_gif_sub
            )

            data class GifToJxl(
                val gifUris: List<Uri>? = null
            ) : Type(
                title = R.string.gif_type_to_jxl,
                subtitle = R.string.gif_type_to_jxl_sub
            )

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

    data class ApngTools(
        val type: Type? = null
    ) : Screen(
        id = 21,
        title = R.string.apng_tools,
        subtitle = R.string.apng_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) : Parcelable {

            val icon: ImageVector
                get() = when (this) {
                    is ApngToImage -> Icons.Outlined.Collections
                    is ApngToJxl -> Icons.Filled.Jxl
                    is ImageToApng -> Icons.Rounded.Apng
                }

            data class ApngToImage(
                val apngUri: Uri? = null
            ) : Type(
                title = R.string.apng_type_to_image,
                subtitle = R.string.apng_type_to_image_sub
            )

            data class ImageToApng(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.apng_type_to_apng,
                subtitle = R.string.apng_type_to_apng_sub
            )

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

    data class Zip(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 22,
        title = R.string.zip,
        subtitle = R.string.zip_sub
    )

    data class JxlTools(
        val type: Type? = null
    ) : Screen(
        id = 23,
        title = R.string.jxl_tools,
        subtitle = R.string.jxl_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) : Parcelable {

            val icon: ImageVector
                get() = when (this) {
                    is ImageToJxl -> Icons.Rounded.Animation
                    is JpegToJxl -> Icons.Filled.Jxl
                    is JxlToImage -> Icons.Outlined.Collections
                    is JxlToJpeg -> Icons.Outlined.Jpg
                }

            data class JxlToJpeg(
                val jxlImageUris: List<Uri>? = null
            ) : Type(
                title = R.string.jxl_type_to_jpeg,
                subtitle = R.string.jxl_type_to_jpeg_sub
            )

            data class JpegToJxl(
                val jpegImageUris: List<Uri>? = null
            ) : Type(
                title = R.string.jpeg_type_to_jxl,
                subtitle = R.string.jpeg_type_to_jxl_sub
            )

            data class JxlToImage(
                val jxlUri: Uri? = null
            ) : Type(
                title = R.string.jxl_type_to_images,
                subtitle = R.string.jxl_type_to_images_sub
            )

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

    data class SvgMaker(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 24,
        title = R.string.images_to_svg,
        subtitle = R.string.images_to_svg_sub
    )

    data class FormatConversion(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 25,
        title = R.string.format_conversion,
        subtitle = R.string.format_conversion_sub
    )

    data object DocumentScanner : Screen(
        id = 26,
        title = R.string.document_scanner,
        subtitle = R.string.document_scanner_sub
    )

    data class ScanQrCode(
        val qrCodeContent: String? = null
    ) : Screen(
        id = 27,
        title = R.string.qr_code,
        subtitle = R.string.qr_code_sub
    )

    data class ImageStacking(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 28,
        title = R.string.image_stacking,
        subtitle = R.string.image_stacking_sub
    )

    data class ImageSplitting(
        val uri: Uri? = null
    ) : Screen(
        id = 29,
        title = R.string.image_splitting,
        subtitle = R.string.image_splitting_sub
    )

    data object ColorTools : Screen(
        id = 30,
        title = R.string.color_tools,
        subtitle = R.string.color_tools_sub
    )

    data class WebpTools(
        val type: Type? = null
    ) : Screen(
        id = 31,
        title = R.string.webp_tools,
        subtitle = R.string.webp_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int
        ) : Parcelable {

            val icon: ImageVector
                get() = when (this) {
                    is WebpToImage -> Icons.Outlined.Collections
                    is ImageToWebp -> Icons.Rounded.Webp
                }

            data class WebpToImage(
                val webpUri: Uri? = null
            ) : Type(
                title = R.string.webp_type_to_image,
                subtitle = R.string.webp_type_to_image_sub
            )

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

    data object NoiseGeneration : Screen(
        id = 32,
        title = R.string.noise_generation,
        subtitle = R.string.noise_generation_sub
    )

    data class CollageMaker(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 33,
        title = R.string.collage_maker,
        subtitle = R.string.collage_maker_sub
    )

    companion object {
        val typedEntries by lazy {
            listOf(
                listOf(
                    SingleEdit(),
                    ResizeAndConvert(),
                    FormatConversion(),
                    Crop(),
                    WeightResize(),
                    LimitResize(),
                ) to Triple(
                    R.string.edit,
                    Icons.Rounded.ImageSync,
                    Icons.Outlined.ImageSync
                ),
                listOf(
                    Filter(),
                    Draw(),
                    EraseBackground(),
                    CollageMaker(),
                    ImageStitching(),
                    ImageStacking(),
                    ImageSplitting(),
                    Watermarking(),
                    GradientMaker(),
                    DeleteExif(),
                ) to Triple(
                    R.string.create,
                    Icons.Filled.AutoAwesome,
                    Icons.Outlined.AutoAwesome
                ),
                listOf(
                    PickColorFromImage(),
                    RecognizeText(),
                    Compare(),
                    ImagePreview(),
                    SvgMaker(),
                    GeneratePalette(),
                    LoadNetImage(),
                ) to Triple(
                    R.string.image,
                    Icons.Filled.FilterHdr,
                    Icons.Outlined.FilterHdr
                ),
                listOf(
                    PdfTools(),
                    DocumentScanner,
                    ScanQrCode(),
                    ColorTools,
                    GifTools(),
                    JxlTools(),
                    ApngTools(),
                    Cipher(),
                    NoiseGeneration,
                    Zip(),
                    WebpTools()
                ) to Triple(
                    R.string.tools,
                    Icons.Rounded.Toolbox,
                    Icons.Outlined.Toolbox
                )
            )
        }
        val entries by lazy {
            typedEntries.flatMap { it.first }.sortedBy { it.id }
        }

        const val FEATURES_COUNT = 53
    }
}