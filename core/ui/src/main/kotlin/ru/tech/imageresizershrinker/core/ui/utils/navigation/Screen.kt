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
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.FilterHdr
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.GifBox
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Gif
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.material.Apng
import ru.tech.imageresizershrinker.core.resources.material.ApngBox
import ru.tech.imageresizershrinker.core.resources.material.CropSmall
import ru.tech.imageresizershrinker.core.resources.material.Encrypted
import ru.tech.imageresizershrinker.core.resources.material.Exif
import ru.tech.imageresizershrinker.core.resources.material.ImageCombine
import ru.tech.imageresizershrinker.core.resources.material.ImageDownload
import ru.tech.imageresizershrinker.core.resources.material.ImageEdit
import ru.tech.imageresizershrinker.core.resources.material.ImageLimit
import ru.tech.imageresizershrinker.core.resources.material.ImageSync
import ru.tech.imageresizershrinker.core.resources.material.ImageText
import ru.tech.imageresizershrinker.core.resources.material.ImageWeight
import ru.tech.imageresizershrinker.core.resources.material.Jpg
import ru.tech.imageresizershrinker.core.resources.material.Jxl
import ru.tech.imageresizershrinker.core.resources.material.MultipleImageEdit
import ru.tech.imageresizershrinker.core.resources.material.PaletteSwatch
import ru.tech.imageresizershrinker.core.resources.material.Toolbox
import ru.tech.imageresizershrinker.core.resources.material.Transparency

@Parcelize
sealed class Screen(
    open val id: Int,
    val icon: @RawValue ImageVector?,
    @StringRes val title: Int,
    @StringRes val subtitle: Int
) : Parcelable {

    data object EasterEgg : Screen(
        id = -2,
        icon = null,
        title = 0,
        subtitle = 0
    )

    data object Main : Screen(
        id = -1,
        icon = null,
        title = 0,
        subtitle = 0
    )

    data class SingleEdit(
        val uri: Uri? = null
    ) : Screen(
        id = 0,
        icon = Icons.Outlined.ImageEdit,
        title = R.string.single_edit,
        subtitle = R.string.single_edit_sub
    )

    data class ResizeAndConvert(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 1,
        icon = Icons.Rounded.MultipleImageEdit,
        title = R.string.resize_and_convert,
        subtitle = R.string.resize_and_convert_sub
    )

    data class ResizeByBytes(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 2,
        icon = Icons.Rounded.ImageWeight,
        title = R.string.by_bytes_resize,
        subtitle = R.string.by_bytes_resize_sub
    )

    data class Crop(
        val uri: Uri? = null
    ) : Screen(
        id = 3,
        icon = Icons.Rounded.CropSmall,
        title = R.string.crop,
        subtitle = R.string.crop_sub
    )

    data class Filter(
        val type: Type? = null
    ) : Screen(
        id = 4,
        icon = Icons.Outlined.AutoFixHigh,
        title = R.string.filter,
        subtitle = R.string.filter_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int,
            @IgnoredOnParcel val icon: ImageVector? = null
        ) : Parcelable {

            data class Masking(
                val uri: Uri? = null
            ) : Type(
                title = R.string.mask_filter,
                subtitle = R.string.mask_filter_sub,
                icon = Icons.Rounded.Texture
            )

            data class Basic(
                val uris: List<Uri>? = null
            ) : Type(
                title = R.string.full_filter,
                subtitle = R.string.full_filter_sub,
                icon = Icons.Outlined.AutoFixHigh
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
        icon = Icons.Outlined.Draw,
        title = R.string.draw,
        subtitle = R.string.draw_sub
    )

    data class Cipher(
        val uri: Uri? = null
    ) : Screen(
        id = 6,
        icon = Icons.Outlined.Encrypted,
        title = R.string.cipher,
        subtitle = R.string.cipher_sub
    )

    data class EraseBackground(
        val uri: Uri? = null
    ) : Screen(
        id = 7,
        icon = Icons.Filled.Transparency,
        title = R.string.background_remover,
        subtitle = R.string.background_remover_sub
    )

    data class ImagePreview(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 8,
        icon = Icons.Outlined.Photo,
        title = R.string.image_preview,
        subtitle = R.string.image_preview_sub
    )

    data class ImageStitching(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 9,
        icon = Icons.Rounded.ImageCombine,
        title = R.string.image_stitching,
        subtitle = R.string.image_stitching_sub
    )

    data class LoadNetImage(
        val url: String = ""
    ) : Screen(
        id = 10,
        icon = Icons.Outlined.ImageDownload,
        title = R.string.load_image_from_net,
        subtitle = R.string.load_image_from_net_sub
    )

    data class PickColorFromImage(
        val uri: Uri? = null
    ) : Screen(
        id = 11,
        icon = Icons.Outlined.Colorize,
        title = R.string.pick_color,
        subtitle = R.string.pick_color_sub
    )

    data class GeneratePalette(
        val uri: Uri? = null
    ) : Screen(
        id = 12,
        icon = Icons.Outlined.PaletteSwatch,
        title = R.string.generate_palette,
        subtitle = R.string.palette_sub
    )

    data class DeleteExif(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 13,
        icon = Icons.Outlined.Exif,
        title = R.string.delete_exif,
        subtitle = R.string.delete_exif_sub
    )

    data class Compare(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 14,
        icon = Icons.Rounded.Compare,
        title = R.string.compare,
        subtitle = R.string.compare_sub
    )

    data class LimitResize(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 15,
        icon = Icons.Outlined.ImageLimit,
        title = R.string.limits_resize,
        subtitle = R.string.limits_resize_sub
    )

    data class PdfTools(
        val type: Type? = null
    ) : Screen(
        id = 16,
        icon = Icons.Outlined.PictureAsPdf,
        title = R.string.pdf_tools,
        subtitle = R.string.pdf_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int,
            @IgnoredOnParcel val icon: ImageVector? = null
        ) : Parcelable {
            data class Preview(
                val pdfUri: Uri? = null
            ) : Type(
                title = R.string.preview_pdf,
                subtitle = R.string.preview_pdf_sub,
                icon = Icons.Rounded.Preview
            )

            data class PdfToImages(
                val pdfUri: Uri? = null
            ) : Type(
                title = R.string.pdf_to_images,
                subtitle = R.string.pdf_to_images_sub,
                icon = Icons.Outlined.Collections
            )

            data class ImagesToPdf(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.images_to_pdf,
                subtitle = R.string.images_to_pdf_sub,
                icon = Icons.Outlined.PictureAsPdf
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
        icon = Icons.Outlined.ImageText,
        title = R.string.recognize_text,
        subtitle = R.string.recognize_text_sub
    )

    data class GradientMaker(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 18,
        icon = Icons.Rounded.Gradient,
        title = R.string.gradient_maker,
        subtitle = R.string.gradient_maker_sub,
    )

    data class Watermarking(
        val uris: List<Uri>? = null
    ) : Screen(
        id = 19,
        icon = Icons.AutoMirrored.Outlined.BrandingWatermark,
        title = R.string.watermarking,
        subtitle = R.string.watermarking_sub,
    )

    data class GifTools(
        val type: Type? = null
    ) : Screen(
        id = 20,
        icon = Icons.Outlined.GifBox,
        title = R.string.gif_tools,
        subtitle = R.string.gif_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int,
            @IgnoredOnParcel val icon: ImageVector? = null
        ) : Parcelable {

            data class GifToImage(
                val gifUri: Uri? = null
            ) : Type(
                title = R.string.gif_type_to_image,
                subtitle = R.string.gif_type_to_image_sub,
                icon = Icons.Outlined.Collections
            )

            data class ImageToGif(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.gif_type_to_gif,
                subtitle = R.string.gif_type_to_gif_sub,
                icon = Icons.Rounded.Gif
            )

            data class GifToJxl(
                val gifUris: List<Uri>? = null
            ) : Type(
                title = R.string.gif_type_to_jxl,
                subtitle = R.string.gif_type_to_jxl_sub,
                icon = Icons.Filled.Jxl
            )

            companion object {
                val entries by lazy {
                    listOf(
                        ImageToGif(),
                        GifToImage(),
                        GifToJxl()
                    )
                }
            }
        }
    }

    data class ApngTools(
        val type: Type? = null
    ) : Screen(
        id = 21,
        icon = Icons.Rounded.ApngBox,
        title = R.string.apng_tools,
        subtitle = R.string.apng_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int,
            @IgnoredOnParcel val icon: ImageVector? = null
        ) : Parcelable {

            data class ApngToImage(
                val apngUri: Uri? = null
            ) : Type(
                title = R.string.apng_type_to_image,
                subtitle = R.string.apng_type_to_image_sub,
                icon = Icons.Outlined.Collections
            )

            data class ImageToApng(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.apng_type_to_apng,
                subtitle = R.string.apng_type_to_apng_sub,
                icon = Icons.Rounded.Apng
            )

            data class ApngToJxl(
                val apngUris: List<Uri>? = null
            ) : Type(
                title = R.string.apng_type_to_jxl,
                subtitle = R.string.apng_type_to_jxl_sub,
                icon = Icons.Filled.Jxl
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
        icon = Icons.Outlined.FolderZip,
        title = R.string.zip,
        subtitle = R.string.zip_sub
    )

    data class JxlTools(
        val type: Type? = null
    ) : Screen(
        id = 20,
        icon = Icons.Filled.Jxl,
        title = R.string.jxl_tools,
        subtitle = R.string.jxl_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int,
            @IgnoredOnParcel val icon: ImageVector? = null
        ) : Parcelable {

            data class JxlToJpeg(
                val jxlImageUris: List<Uri>? = null
            ) : Type(
                title = R.string.jxl_type_to_jpeg,
                subtitle = R.string.jxl_type_to_jpeg_sub,
                icon = Icons.Outlined.Jpg
            )

            data class JpegToJxl(
                val jpegImageUris: List<Uri>? = null
            ) : Type(
                title = R.string.jpeg_type_to_jxl,
                subtitle = R.string.jpeg_type_to_jxl_sub,
                icon = Icons.Filled.Jxl
            )

            data class JxlToImage(
                val jxlUri: Uri? = null
            ) : Type(
                title = R.string.jxl_type_to_images,
                subtitle = R.string.jxl_type_to_images_sub,
                icon = Icons.Outlined.Collections
            )

            data class ImageToJxl(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.jxl_type_to_jxl,
                subtitle = R.string.jxl_type_to_jxl_sub,
                icon = Icons.Rounded.Animation
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

    companion object {
        val typedEntries by lazy {
            listOf(
                listOf(
                    SingleEdit(),
                    ResizeAndConvert(),
                    Crop(),
                    ResizeByBytes(),
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
                    ImageStitching(),
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
                    GeneratePalette(),
                    LoadNetImage(),
                ) to Triple(
                    R.string.image,
                    Icons.Filled.FilterHdr,
                    Icons.Outlined.FilterHdr
                ),
                listOf(
                    PdfTools(),
                    GifTools(),
                    JxlTools(),
                    ApngTools(),
                    Cipher(),
                    Zip()
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

        const val FEATURES_COUNT = 37
    }
}