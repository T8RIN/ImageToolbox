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
import androidx.compose.material.icons.automirrored.outlined.WrapText
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Margin
import androidx.compose.material.icons.outlined.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material.icons.rounded.PhotoSizeSelectLarge
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.icons.material.FingerprintOff
import ru.tech.imageresizershrinker.core.ui.icons.material.Interface
import ru.tech.imageresizershrinker.core.ui.icons.material.PaletteSwatch
import ru.tech.imageresizershrinker.core.ui.icons.material.Puzzle
import ru.tech.imageresizershrinker.core.ui.icons.material.Resize
import ru.tech.imageresizershrinker.core.ui.icons.material.Toolbox
import ru.tech.imageresizershrinker.core.ui.icons.material.Transparency

@Parcelize
sealed class Screen(
    open val id: Int,
    val icon: @RawValue ImageVector?,
    @StringRes val title: Int,
    @StringRes val subtitle: Int
) : Parcelable {
    data object Main : Screen(-1, null, 0, 0)

    class SingleEdit(val uri: Uri? = null) : Screen(
        id = 0,
        icon = Icons.Rounded.CreateAlt,
        title = R.string.single_edit,
        subtitle = R.string.single_edit_sub
    )

    class ResizeAndConvert(val uris: List<Uri>? = null) : Screen(
        id = 1,
        icon = Icons.Filled.Resize,
        title = R.string.resize_and_convert,
        subtitle = R.string.resize_and_convert_sub
    )

    class ResizeByBytes(val uris: List<Uri>? = null) : Screen(
        id = 2,
        icon = Icons.Filled.Interface,
        title = R.string.by_bytes_resize,
        subtitle = R.string.by_bytes_resize_sub
    )

    class Crop(val uri: Uri? = null) : Screen(
        id = 3,
        icon = Icons.Rounded.Crop,
        title = R.string.crop,
        subtitle = R.string.crop_sub
    )

    class Filter(val type: Type? = null) : Screen(
        id = 4,
        icon = Icons.Rounded.PhotoFilter,
        title = R.string.filter,
        subtitle = R.string.filter_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int,
            @IgnoredOnParcel val icon: ImageVector? = null
        ) : Parcelable {

            class Masking(
                val uri: Uri? = null
            ) : Type(
                title = R.string.mask_filter,
                subtitle = R.string.mask_filter_sub,
                icon = Icons.Rounded.Texture
            )

            class Basic(
                val uris: List<Uri>? = null
            ) : Type(
                title = R.string.full_filter,
                subtitle = R.string.full_filter_sub,
                icon = Icons.Rounded.PhotoFilter
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

    class Draw(val uri: Uri? = null) : Screen(
        id = 5,
        icon = Icons.Rounded.Draw,
        title = R.string.draw,
        subtitle = R.string.draw_sub
    )

    class Cipher(val uri: Uri? = null) : Screen(
        id = 6,
        icon = Icons.Rounded.Security,
        title = R.string.cipher,
        subtitle = R.string.cipher_sub
    )

    class EraseBackground(val uri: Uri? = null) : Screen(
        id = 7,
        icon = Icons.Filled.Transparency,
        title = R.string.background_remover,
        subtitle = R.string.background_remover_sub
    )

    class ImagePreview(val uris: List<Uri>? = null) : Screen(
        id = 8,
        icon = Icons.Rounded.Photo,
        title = R.string.image_preview,
        subtitle = R.string.image_preview_sub
    )

    class ImageStitching(val uris: List<Uri>? = null) : Screen(
        id = 9,
        icon = Icons.Outlined.Puzzle,
        title = R.string.image_stitching,
        subtitle = R.string.image_stitching_sub
    )

    class LoadNetImage(val url: String = "") : Screen(
        id = 10,
        icon = Icons.Rounded.Public,
        title = R.string.load_image_from_net,
        subtitle = R.string.load_image_from_net_sub
    )

    class PickColorFromImage(val uri: Uri? = null) : Screen(
        id = 11,
        icon = Icons.Rounded.Colorize,
        title = R.string.pick_color,
        subtitle = R.string.pick_color_sub
    )

    class GeneratePalette(val uri: Uri? = null) : Screen(
        id = 12,
        icon = Icons.Rounded.PaletteSwatch,
        title = R.string.generate_palette,
        subtitle = R.string.palette_sub
    )

    class DeleteExif(val uris: List<Uri>? = null) : Screen(
        id = 13,
        icon = Icons.Rounded.FingerprintOff,
        title = R.string.delete_exif,
        subtitle = R.string.delete_exif_sub
    )

    class Compare(val uris: List<Uri>? = null) : Screen(
        id = 14,
        icon = Icons.Rounded.Compare,
        title = R.string.compare,
        subtitle = R.string.compare_sub
    )

    class LimitResize(val uris: List<Uri>? = null) : Screen(
        id = 15,
        icon = Icons.Outlined.Margin,
        title = R.string.limits_resize,
        subtitle = R.string.limits_resize_sub
    )

    class PdfTools(
        val type: Type? = null
    ) : Screen(
        id = 16,
        icon = Icons.Rounded.PictureAsPdf,
        title = R.string.pdf_tools,
        subtitle = R.string.pdf_tools_sub
    ) {
        @Parcelize
        sealed class Type(
            @StringRes val title: Int,
            @StringRes val subtitle: Int,
            @IgnoredOnParcel val icon: ImageVector? = null
        ) : Parcelable {
            class Preview(
                val pdfUri: Uri? = null
            ) : Type(
                title = R.string.preview_pdf,
                subtitle = R.string.preview_pdf_sub,
                icon = Icons.Rounded.Preview
            )

            class PdfToImages(
                val pdfUri: Uri? = null
            ) : Type(
                title = R.string.pdf_to_images,
                subtitle = R.string.pdf_to_images_sub,
                icon = Icons.Rounded.Collections
            )

            class ImagesToPdf(
                val imageUris: List<Uri>? = null
            ) : Type(
                title = R.string.images_to_pdf,
                subtitle = R.string.images_to_pdf_sub,
                icon = Icons.Rounded.PictureAsPdf
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

    class RecognizeText(
        val uri: Uri? = null
    ) : Screen(
        id = 17,
        icon = Icons.AutoMirrored.Outlined.WrapText,
        title = R.string.recognize_text,
        subtitle = R.string.recognize_text_sub
    )

    data class GradientMaker(
        val uri: Uri? = null
    ) : Screen(
        id = 18,
        icon = Icons.Rounded.Gradient,
        title = R.string.gradient_maker,
        subtitle = R.string.gradient_maker_sub,
    )

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
                    Icons.Rounded.PhotoSizeSelectLarge,
                    Icons.Outlined.PhotoSizeSelectSmall
                ),
                listOf(
                    Filter(),
                    Draw(),
                    EraseBackground(),
                    ImageStitching(),
                    Cipher(),
                    GradientMaker()
                ) to Triple(
                    R.string.create,
                    Icons.Filled.AutoAwesome,
                    Icons.Outlined.AutoAwesome
                ),
                listOf(
                    PickColorFromImage(),
                    RecognizeText(),
                    PdfTools(),
                    Compare(),
                    ImagePreview(),
                    LoadNetImage(),
                    GeneratePalette(),
                    DeleteExif(),
                ) to Triple(
                    R.string.tools,
                    Icons.Rounded.Toolbox,
                    Icons.Outlined.Toolbox
                )
            )
        }
        val entries by lazy {
            listOf(
                SingleEdit(),
                ResizeAndConvert(),
                ResizeByBytes(),
                Crop(),
                Filter(),
                Draw(),
                Cipher(),
                EraseBackground(),
                ImageStitching(),
                PdfTools(),
                RecognizeText(),
                ImagePreview(),
                LoadNetImage(),
                PickColorFromImage(),
                GeneratePalette(),
                DeleteExif(),
                GradientMaker(),
                Compare(),
                LimitResize()
            )
        }
        const val featuresCount = 22
    }
}