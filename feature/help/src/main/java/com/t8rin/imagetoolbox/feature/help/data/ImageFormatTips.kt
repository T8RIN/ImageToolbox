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

package com.t8rin.imagetoolbox.feature.help.data

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageConvert
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpCategory
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpPage
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpTip

internal fun imageFormatTips(category: HelpCategory): List<HelpTip> = listOf(
    formatTip(
        id = "format-jpeg",
        category = category,
        title = R.string.help_tip_format_jpeg_title,
        subtitle = R.string.help_tip_format_jpeg_subtitle,
        description = R.string.help_tip_format_jpeg_description,
        steps = listOf(
            R.string.help_tip_format_jpeg_step_1,
            R.string.help_tip_format_jpeg_step_2,
            R.string.help_tip_format_jpeg_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-mozjpeg",
        category = category,
        title = R.string.help_tip_format_mozjpeg_title,
        subtitle = R.string.help_tip_format_mozjpeg_subtitle,
        description = R.string.help_tip_format_mozjpeg_description,
        steps = listOf(
            R.string.help_tip_format_mozjpeg_step_1,
            R.string.help_tip_format_mozjpeg_step_2,
            R.string.help_tip_format_mozjpeg_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-jpegli",
        category = category,
        title = R.string.help_tip_format_jpegli_title,
        subtitle = R.string.help_tip_format_jpegli_subtitle,
        description = R.string.help_tip_format_jpegli_description,
        steps = listOf(
            R.string.help_tip_format_jpegli_step_1,
            R.string.help_tip_format_jpegli_step_2,
            R.string.help_tip_format_jpegli_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-png",
        category = category,
        title = R.string.help_tip_format_png_title,
        subtitle = R.string.help_tip_format_png_subtitle,
        description = R.string.help_tip_format_png_description,
        steps = listOf(
            R.string.help_tip_format_png_step_1,
            R.string.help_tip_format_png_step_2,
            R.string.help_tip_format_png_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-png-lossy",
        category = category,
        title = R.string.help_tip_format_png_lossy_title,
        subtitle = R.string.help_tip_format_png_lossy_subtitle,
        description = R.string.help_tip_format_png_lossy_description,
        steps = listOf(
            R.string.help_tip_format_png_lossy_step_1,
            R.string.help_tip_format_png_lossy_step_2,
            R.string.help_tip_format_png_lossy_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-oxipng",
        category = category,
        title = R.string.help_tip_format_oxipng_title,
        subtitle = R.string.help_tip_format_oxipng_subtitle,
        description = R.string.help_tip_format_oxipng_description,
        steps = listOf(
            R.string.help_tip_format_oxipng_step_1,
            R.string.help_tip_format_oxipng_step_2,
            R.string.help_tip_format_oxipng_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-imagequant",
        category = category,
        title = R.string.help_tip_format_imagequant_title,
        subtitle = R.string.help_tip_format_imagequant_subtitle,
        description = R.string.help_tip_format_imagequant_description,
        steps = listOf(
            R.string.help_tip_format_imagequant_step_1,
            R.string.help_tip_format_imagequant_step_2,
            R.string.help_tip_format_imagequant_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-webp",
        category = category,
        title = R.string.help_tip_format_webp_title,
        subtitle = R.string.help_tip_format_webp_subtitle,
        description = R.string.help_tip_format_webp_description,
        steps = listOf(
            R.string.help_tip_format_webp_step_1,
            R.string.help_tip_format_webp_step_2,
            R.string.help_tip_format_webp_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-avif",
        category = category,
        title = R.string.help_tip_format_avif_title,
        subtitle = R.string.help_tip_format_avif_subtitle,
        description = R.string.help_tip_format_avif_description,
        steps = listOf(
            R.string.help_tip_format_avif_step_1,
            R.string.help_tip_format_avif_step_2,
            R.string.help_tip_format_avif_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-av2",
        category = category,
        title = R.string.help_tip_format_av2_title,
        subtitle = R.string.help_tip_format_av2_subtitle,
        description = R.string.help_tip_format_av2_description,
        steps = listOf(
            R.string.help_tip_format_av2_step_1,
            R.string.help_tip_format_av2_step_2,
            R.string.help_tip_format_av2_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-heic",
        category = category,
        title = R.string.help_tip_format_heic_title,
        subtitle = R.string.help_tip_format_heic_subtitle,
        description = R.string.help_tip_format_heic_description,
        steps = listOf(
            R.string.help_tip_format_heic_step_1,
            R.string.help_tip_format_heic_step_2,
            R.string.help_tip_format_heic_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-vvc",
        category = category,
        title = R.string.help_tip_format_vvc_title,
        subtitle = R.string.help_tip_format_vvc_subtitle,
        description = R.string.help_tip_format_vvc_description,
        steps = listOf(
            R.string.help_tip_format_vvc_step_1,
            R.string.help_tip_format_vvc_step_2,
            R.string.help_tip_format_vvc_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-jxl",
        category = category,
        title = R.string.help_tip_format_jxl_title,
        subtitle = R.string.help_tip_format_jxl_subtitle,
        description = R.string.help_tip_format_jxl_description,
        steps = listOf(
            R.string.help_tip_format_jxl_step_1,
            R.string.help_tip_format_jxl_step_2,
            R.string.help_tip_format_jxl_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-jixel",
        category = category,
        title = R.string.help_tip_format_jixel_title,
        subtitle = R.string.help_tip_format_jixel_subtitle,
        description = R.string.help_tip_format_jixel_description,
        steps = listOf(
            R.string.help_tip_format_jixel_step_1,
            R.string.help_tip_format_jixel_step_2,
            R.string.help_tip_format_jixel_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-jp2",
        category = category,
        title = R.string.help_tip_format_jp2_title,
        subtitle = R.string.help_tip_format_jp2_subtitle,
        description = R.string.help_tip_format_jp2_description,
        steps = listOf(
            R.string.help_tip_format_jp2_step_1,
            R.string.help_tip_format_jp2_step_2,
            R.string.help_tip_format_jp2_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-j2k",
        category = category,
        title = R.string.help_tip_format_j2k_title,
        subtitle = R.string.help_tip_format_j2k_subtitle,
        description = R.string.help_tip_format_j2k_description,
        steps = listOf(
            R.string.help_tip_format_j2k_step_1,
            R.string.help_tip_format_j2k_step_2,
            R.string.help_tip_format_j2k_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-tiff",
        category = category,
        title = R.string.help_tip_format_tiff_title,
        subtitle = R.string.help_tip_format_tiff_subtitle,
        description = R.string.help_tip_format_tiff_description,
        steps = listOf(
            R.string.help_tip_format_tiff_step_1,
            R.string.help_tip_format_tiff_step_2,
            R.string.help_tip_format_tiff_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-bmp",
        category = category,
        title = R.string.help_tip_format_bmp_title,
        subtitle = R.string.help_tip_format_bmp_subtitle,
        description = R.string.help_tip_format_bmp_description,
        steps = listOf(
            R.string.help_tip_format_bmp_step_1,
            R.string.help_tip_format_bmp_step_2,
            R.string.help_tip_format_bmp_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-qoi",
        category = category,
        title = R.string.help_tip_format_qoi_title,
        subtitle = R.string.help_tip_format_qoi_subtitle,
        description = R.string.help_tip_format_qoi_description,
        steps = listOf(
            R.string.help_tip_format_qoi_step_1,
            R.string.help_tip_format_qoi_step_2,
            R.string.help_tip_format_qoi_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-ico",
        category = category,
        title = R.string.help_tip_format_ico_title,
        subtitle = R.string.help_tip_format_ico_subtitle,
        description = R.string.help_tip_format_ico_description,
        steps = listOf(
            R.string.help_tip_format_ico_step_1,
            R.string.help_tip_format_ico_step_2,
            R.string.help_tip_format_ico_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-gif",
        category = category,
        title = R.string.help_tip_format_gif_title,
        subtitle = R.string.help_tip_format_gif_subtitle,
        description = R.string.help_tip_format_gif_description,
        steps = listOf(
            R.string.help_tip_format_gif_step_1,
            R.string.help_tip_format_gif_step_2,
            R.string.help_tip_format_gif_step_3
        ),
        deepLink = Screen.GifTools()
    ),
    formatTip(
        id = "format-apng",
        category = category,
        title = R.string.help_tip_format_apng_title,
        subtitle = R.string.help_tip_format_apng_subtitle,
        description = R.string.help_tip_format_apng_description,
        steps = listOf(
            R.string.help_tip_format_apng_step_1,
            R.string.help_tip_format_apng_step_2,
            R.string.help_tip_format_apng_step_3
        ),
        deepLink = Screen.ApngTools()
    ),
    formatTip(
        id = "format-svg",
        category = category,
        title = R.string.help_tip_format_svg_title,
        subtitle = R.string.help_tip_format_svg_subtitle,
        description = R.string.help_tip_format_svg_description,
        steps = listOf(
            R.string.help_tip_format_svg_step_1,
            R.string.help_tip_format_svg_step_2,
            R.string.help_tip_format_svg_step_3
        ),
        deepLink = Screen.SvgMaker()
    ),
    formatTip(
        id = "format-pdf",
        category = category,
        title = R.string.help_tip_format_pdf_title,
        subtitle = R.string.help_tip_format_pdf_subtitle,
        description = R.string.help_tip_format_pdf_description,
        steps = listOf(
            R.string.help_tip_format_pdf_step_1,
            R.string.help_tip_format_pdf_step_2,
            R.string.help_tip_format_pdf_step_3
        ),
        deepLink = Screen.PdfTools
    ),
    formatTip(
        id = "format-raw",
        category = category,
        title = R.string.help_tip_format_raw_title,
        subtitle = R.string.help_tip_format_raw_subtitle,
        description = R.string.help_tip_format_raw_description,
        steps = listOf(
            R.string.help_tip_format_raw_step_1,
            R.string.help_tip_format_raw_step_2,
            R.string.help_tip_format_raw_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-psd",
        category = category,
        title = R.string.help_tip_format_psd_title,
        subtitle = R.string.help_tip_format_psd_subtitle,
        description = R.string.help_tip_format_psd_description,
        steps = listOf(
            R.string.help_tip_format_psd_step_1,
            R.string.help_tip_format_psd_step_2,
            R.string.help_tip_format_psd_step_3
        ),
        deepLink = Screen.FormatConversion()
    ),
    formatTip(
        id = "format-djvu",
        category = category,
        title = R.string.help_tip_format_djvu_title,
        subtitle = R.string.help_tip_format_djvu_subtitle,
        description = R.string.help_tip_format_djvu_description,
        steps = listOf(
            R.string.help_tip_format_djvu_step_1,
            R.string.help_tip_format_djvu_step_2,
            R.string.help_tip_format_djvu_step_3
        ),
        deepLink = Screen.FormatConversion()
    )
)

private fun formatTip(
    id: String,
    category: HelpCategory,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    @StringRes description: Int,
    steps: List<Int>,
    deepLink: Screen
) = HelpTip(
    id = id,
    title = title,
    subtitle = subtitle,
    icon = Icons.Outlined.ImageConvert,
    category = category,
    pages = listOf(
        HelpPage(
            title = title,
            description = description,
            steps = steps
        )
    ),
    deepLink = deepLink
)