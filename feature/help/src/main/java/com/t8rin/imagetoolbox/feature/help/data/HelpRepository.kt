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
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AutoFixHigh
import com.t8rin.imagetoolbox.core.resources.icons.Base64
import com.t8rin.imagetoolbox.core.resources.icons.Eraser
import com.t8rin.imagetoolbox.core.resources.icons.ExifEdit
import com.t8rin.imagetoolbox.core.resources.icons.HelpOutline
import com.t8rin.imagetoolbox.core.resources.icons.ImageConvert
import com.t8rin.imagetoolbox.core.resources.icons.Lightbulb
import com.t8rin.imagetoolbox.core.resources.icons.MultipleImageEdit
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.resources.icons.TextSearch
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen

data class HelpCategory(
    val key: String,
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val icon: ImageVector
)

data class HelpTip(
    val id: String,
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val icon: ImageVector,
    val category: HelpCategory,
    val pages: List<HelpPage>,
    val deepLink: Screen? = null
)

data class HelpPage(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val steps: List<Int>
)

object HelpRepository {

    private val gettingStarted = HelpCategory(
        key = "getting-started",
        title = R.string.help_category_getting_started,
        subtitle = R.string.help_category_getting_started_sub,
        icon = Icons.Rounded.Lightbulb
    )

    private val imageEditing = HelpCategory(
        key = "image-editing",
        title = R.string.help_category_image_editing,
        subtitle = R.string.help_category_image_editing_sub,
        icon = Icons.Outlined.MultipleImageEdit
    )

    private val filesAndMetadata = HelpCategory(
        key = "files-metadata",
        title = R.string.help_category_files_metadata,
        subtitle = R.string.help_category_files_metadata_sub,
        icon = Icons.Outlined.ExifEdit
    )

    private val pdfAndDocuments = HelpCategory(
        key = "pdf-documents",
        title = R.string.help_category_pdf_documents,
        subtitle = R.string.help_category_pdf_documents_sub,
        icon = Icons.Outlined.Pdf
    )

    private val textAndData = HelpCategory(
        key = "text-data",
        title = R.string.help_category_text_data,
        subtitle = R.string.help_category_text_data_sub,
        icon = Icons.Outlined.TextSearch
    )

    private val colorTools = HelpCategory(
        key = "color-tools",
        title = R.string.help_category_color_tools,
        subtitle = R.string.help_category_color_tools_sub,
        icon = Icons.Outlined.AutoFixHigh
    )

    private val creativeTools = HelpCategory(
        key = "creative-tools",
        title = R.string.help_category_creative_tools,
        subtitle = R.string.help_category_creative_tools_sub,
        icon = Icons.Outlined.ImageConvert
    )

    private val troubleshooting = HelpCategory(
        key = "troubleshooting",
        title = R.string.help_category_troubleshooting,
        subtitle = R.string.help_category_troubleshooting_sub,
        icon = Icons.Rounded.HelpOutline
    )

    val categories: List<HelpCategory> = listOf(
        gettingStarted,
        imageEditing,
        filesAndMetadata,
        pdfAndDocuments,
        textAndData,
        colorTools,
        creativeTools,
        troubleshooting
    )

    val tips: List<HelpTip> = listOf(
        tip(
            id = "choose-tool",
            title = R.string.help_tip_choose_tool_title,
            subtitle = R.string.help_tip_choose_tool_subtitle,
            icon = Icons.Rounded.Lightbulb,
            category = gettingStarted,
            deepLink = Screen.Main,
            pageTitle = R.string.help_tip_choose_tool_page_title,
            description = R.string.help_tip_choose_tool_description,
            steps = listOf(
                R.string.help_tip_choose_tool_step_1,
                R.string.help_tip_choose_tool_step_2,
                R.string.help_tip_choose_tool_step_3
            )
        ),
        tip(
            id = "import-save-share",
            title = R.string.help_tip_import_save_share_title,
            subtitle = R.string.help_tip_import_save_share_subtitle,
            icon = Icons.Outlined.MultipleImageEdit,
            category = gettingStarted,
            pageTitle = R.string.help_tip_import_save_share_page_title,
            description = R.string.help_tip_import_save_share_description,
            steps = listOf(
                R.string.help_tip_import_save_share_step_1,
                R.string.help_tip_import_save_share_step_2,
                R.string.help_tip_import_save_share_step_3
            )
        ),
        tip(
            id = "batch-workflow",
            title = R.string.help_tip_batch_workflow_title,
            subtitle = R.string.help_tip_batch_workflow_subtitle,
            icon = Icons.Outlined.MultipleImageEdit,
            category = gettingStarted,
            deepLink = Screen.ResizeAndConvert(),
            pageTitle = R.string.help_tip_batch_workflow_page_title,
            description = R.string.help_tip_batch_workflow_description,
            steps = listOf(
                R.string.help_tip_batch_workflow_step_1,
                R.string.help_tip_batch_workflow_step_2,
                R.string.help_tip_batch_workflow_step_3
            )
        ),
        tip(
            id = "presets-settings",
            title = R.string.help_tip_presets_settings_title,
            subtitle = R.string.help_tip_presets_settings_subtitle,
            icon = Icons.Rounded.Lightbulb,
            category = gettingStarted,
            pageTitle = R.string.help_tip_presets_settings_page_title,
            description = R.string.help_tip_presets_settings_description,
            steps = listOf(
                R.string.help_tip_presets_settings_step_1,
                R.string.help_tip_presets_settings_step_2,
                R.string.help_tip_presets_settings_step_3
            )
        ),
        tip(
            id = "resize-convert",
            title = R.string.help_tip_resize_convert_title,
            subtitle = R.string.help_tip_resize_convert_subtitle,
            icon = Icons.Outlined.MultipleImageEdit,
            category = imageEditing,
            deepLink = Screen.ResizeAndConvert(),
            pageTitle = R.string.help_tip_resize_convert_page_title,
            description = R.string.help_tip_resize_convert_description,
            steps = listOf(
                R.string.help_tip_resize_convert_step_1,
                R.string.help_tip_resize_convert_step_2,
                R.string.help_tip_resize_convert_step_3
            )
        ),
        tip(
            id = "crop-straighten",
            title = R.string.help_tip_crop_straighten_title,
            subtitle = R.string.help_tip_crop_straighten_subtitle,
            icon = Icons.Outlined.ImageConvert,
            category = imageEditing,
            deepLink = Screen.Crop(),
            pageTitle = R.string.help_tip_crop_straighten_page_title,
            description = R.string.help_tip_crop_straighten_description,
            steps = listOf(
                R.string.help_tip_crop_straighten_step_1,
                R.string.help_tip_crop_straighten_step_2,
                R.string.help_tip_crop_straighten_step_3
            )
        ),
        tip(
            id = "filters",
            title = R.string.help_tip_filters_title,
            subtitle = R.string.help_tip_filters_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = imageEditing,
            deepLink = Screen.Filter(),
            pageTitle = R.string.help_tip_filters_page_title,
            description = R.string.help_tip_filters_description,
            steps = listOf(
                R.string.help_tip_filters_step_1,
                R.string.help_tip_filters_step_2,
                R.string.help_tip_filters_step_3
            )
        ),
        tip(
            id = "background-remover",
            title = R.string.help_tip_background_remover_title,
            subtitle = R.string.help_tip_background_remover_subtitle,
            icon = Icons.Rounded.Eraser,
            category = imageEditing,
            deepLink = Screen.EraseBackground(),
            pageTitle = R.string.help_tip_background_remover_page_title,
            description = R.string.help_tip_background_remover_description,
            steps = listOf(
                R.string.help_tip_background_remover_step_1,
                R.string.help_tip_background_remover_step_2,
                R.string.help_tip_background_remover_step_3
            )
        ),
        tip(
            id = "draw-watermark",
            title = R.string.help_tip_draw_watermark_title,
            subtitle = R.string.help_tip_draw_watermark_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = imageEditing,
            deepLink = Screen.Watermarking(),
            pageTitle = R.string.help_tip_draw_watermark_page_title,
            description = R.string.help_tip_draw_watermark_description,
            steps = listOf(
                R.string.help_tip_draw_watermark_step_1,
                R.string.help_tip_draw_watermark_step_2,
                R.string.help_tip_draw_watermark_step_3
            )
        ),
        tip(
            id = "format-conversion",
            title = R.string.help_tip_format_conversion_title,
            subtitle = R.string.help_tip_format_conversion_subtitle,
            icon = Icons.Outlined.ImageConvert,
            category = filesAndMetadata,
            deepLink = Screen.FormatConversion(),
            pageTitle = R.string.help_tip_format_conversion_page_title,
            description = R.string.help_tip_format_conversion_description,
            steps = listOf(
                R.string.help_tip_format_conversion_step_1,
                R.string.help_tip_format_conversion_step_2,
                R.string.help_tip_format_conversion_step_3
            )
        ),
        tip(
            id = "exif-privacy",
            title = R.string.help_tip_exif_privacy_title,
            subtitle = R.string.help_tip_exif_privacy_subtitle,
            icon = Icons.Outlined.ExifEdit,
            category = filesAndMetadata,
            deepLink = Screen.EditExif(),
            pageTitle = R.string.help_tip_exif_privacy_page_title,
            description = R.string.help_tip_exif_privacy_description,
            steps = listOf(
                R.string.help_tip_exif_privacy_step_1,
                R.string.help_tip_exif_privacy_step_2,
                R.string.help_tip_exif_privacy_step_3
            )
        ),
        tip(
            id = "filenames",
            title = R.string.help_tip_filenames_title,
            subtitle = R.string.help_tip_filenames_subtitle,
            icon = Icons.Outlined.ImageConvert,
            category = filesAndMetadata,
            pageTitle = R.string.help_tip_filenames_page_title,
            description = R.string.help_tip_filenames_description,
            steps = listOf(
                R.string.help_tip_filenames_step_1,
                R.string.help_tip_filenames_step_2,
                R.string.help_tip_filenames_step_3
            )
        ),
        tip(
            id = "compare-preview",
            title = R.string.help_tip_compare_preview_title,
            subtitle = R.string.help_tip_compare_preview_subtitle,
            icon = Icons.Outlined.ImageConvert,
            category = filesAndMetadata,
            deepLink = Screen.Compare(),
            pageTitle = R.string.help_tip_compare_preview_page_title,
            description = R.string.help_tip_compare_preview_description,
            steps = listOf(
                R.string.help_tip_compare_preview_step_1,
                R.string.help_tip_compare_preview_step_2,
                R.string.help_tip_compare_preview_step_3
            )
        ),
        tip(
            id = "pdf-hub",
            title = R.string.help_tip_pdf_hub_title,
            subtitle = R.string.help_tip_pdf_hub_subtitle,
            icon = Icons.Outlined.Pdf,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pageTitle = R.string.help_tip_pdf_hub_page_title,
            description = R.string.help_tip_pdf_hub_description,
            steps = listOf(
                R.string.help_tip_pdf_hub_step_1,
                R.string.help_tip_pdf_hub_step_2,
                R.string.help_tip_pdf_hub_step_3
            )
        ),
        tip(
            id = "images-to-pdf",
            title = R.string.help_tip_images_to_pdf_title,
            subtitle = R.string.help_tip_images_to_pdf_subtitle,
            icon = Icons.Outlined.Pdf,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pageTitle = R.string.help_tip_images_to_pdf_page_title,
            description = R.string.help_tip_images_to_pdf_description,
            steps = listOf(
                R.string.help_tip_images_to_pdf_step_1,
                R.string.help_tip_images_to_pdf_step_2,
                R.string.help_tip_images_to_pdf_step_3
            )
        ),
        tip(
            id = "document-scanner",
            title = R.string.help_tip_document_scanner_title,
            subtitle = R.string.help_tip_document_scanner_subtitle,
            icon = Icons.Outlined.Pdf,
            category = pdfAndDocuments,
            deepLink = Screen.DocumentScanner,
            pageTitle = R.string.help_tip_document_scanner_page_title,
            description = R.string.help_tip_document_scanner_description,
            steps = listOf(
                R.string.help_tip_document_scanner_step_1,
                R.string.help_tip_document_scanner_step_2,
                R.string.help_tip_document_scanner_step_3
            )
        ),
        tip(
            id = "ocr",
            title = R.string.help_tip_ocr_title,
            subtitle = R.string.help_tip_ocr_subtitle,
            icon = Icons.Outlined.TextSearch,
            category = textAndData,
            deepLink = Screen.RecognizeText(),
            pageTitle = R.string.help_tip_ocr_page_title,
            description = R.string.help_tip_ocr_description,
            steps = listOf(
                R.string.help_tip_ocr_step_1,
                R.string.help_tip_ocr_step_2,
                R.string.help_tip_ocr_step_3
            )
        ),
        tip(
            id = "qr-code",
            title = R.string.help_tip_qr_code_title,
            subtitle = R.string.help_tip_qr_code_subtitle,
            icon = Icons.Outlined.TextSearch,
            category = textAndData,
            deepLink = Screen.ScanQrCode(),
            pageTitle = R.string.help_tip_qr_code_page_title,
            description = R.string.help_tip_qr_code_description,
            steps = listOf(
                R.string.help_tip_qr_code_step_1,
                R.string.help_tip_qr_code_step_2,
                R.string.help_tip_qr_code_step_3
            )
        ),
        tip(
            id = "base64-checksum",
            title = R.string.help_tip_base64_checksum_title,
            subtitle = R.string.help_tip_base64_checksum_subtitle,
            icon = Icons.Outlined.Base64,
            category = textAndData,
            deepLink = Screen.Base64Tools(),
            pageTitle = R.string.help_tip_base64_checksum_page_title,
            description = R.string.help_tip_base64_checksum_description,
            steps = listOf(
                R.string.help_tip_base64_checksum_step_1,
                R.string.help_tip_base64_checksum_step_2,
                R.string.help_tip_base64_checksum_step_3
            )
        ),
        tip(
            id = "cipher-zip",
            title = R.string.help_tip_cipher_zip_title,
            subtitle = R.string.help_tip_cipher_zip_subtitle,
            icon = Icons.Outlined.Base64,
            category = textAndData,
            deepLink = Screen.Zip(),
            pageTitle = R.string.help_tip_cipher_zip_page_title,
            description = R.string.help_tip_cipher_zip_description,
            steps = listOf(
                R.string.help_tip_cipher_zip_step_1,
                R.string.help_tip_cipher_zip_step_2,
                R.string.help_tip_cipher_zip_step_3
            )
        ),
        tip(
            id = "pick-color",
            title = R.string.help_tip_pick_color_title,
            subtitle = R.string.help_tip_pick_color_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = colorTools,
            deepLink = Screen.PickColorFromImage(),
            pageTitle = R.string.help_tip_pick_color_page_title,
            description = R.string.help_tip_pick_color_description,
            steps = listOf(
                R.string.help_tip_pick_color_step_1,
                R.string.help_tip_pick_color_step_2,
                R.string.help_tip_pick_color_step_3
            )
        ),
        tip(
            id = "palette-library",
            title = R.string.help_tip_palette_library_title,
            subtitle = R.string.help_tip_palette_library_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = colorTools,
            deepLink = Screen.PaletteTools(),
            pageTitle = R.string.help_tip_palette_library_page_title,
            description = R.string.help_tip_palette_library_description,
            steps = listOf(
                R.string.help_tip_palette_library_step_1,
                R.string.help_tip_palette_library_step_2,
                R.string.help_tip_palette_library_step_3
            )
        ),
        tip(
            id = "gradients",
            title = R.string.help_tip_gradients_title,
            subtitle = R.string.help_tip_gradients_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = colorTools,
            deepLink = Screen.GradientMaker(),
            pageTitle = R.string.help_tip_gradients_page_title,
            description = R.string.help_tip_gradients_description,
            steps = listOf(
                R.string.help_tip_gradients_step_1,
                R.string.help_tip_gradients_step_2,
                R.string.help_tip_gradients_step_3
            )
        ),
        tip(
            id = "shader-studio",
            title = R.string.help_tip_shader_studio_title,
            subtitle = R.string.help_tip_shader_studio_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = creativeTools,
            deepLink = Screen.ShaderStudio,
            pageTitle = R.string.help_tip_shader_studio_page_title,
            description = R.string.help_tip_shader_studio_description,
            steps = listOf(
                R.string.help_tip_shader_studio_step_1,
                R.string.help_tip_shader_studio_step_2,
                R.string.help_tip_shader_studio_step_3
            )
        ),
        tip(
            id = "svg-ascii",
            title = R.string.help_tip_svg_ascii_title,
            subtitle = R.string.help_tip_svg_ascii_subtitle,
            icon = Icons.Outlined.ImageConvert,
            category = creativeTools,
            deepLink = Screen.SvgMaker(),
            pageTitle = R.string.help_tip_svg_ascii_page_title,
            description = R.string.help_tip_svg_ascii_description,
            steps = listOf(
                R.string.help_tip_svg_ascii_step_1,
                R.string.help_tip_svg_ascii_step_2,
                R.string.help_tip_svg_ascii_step_3
            )
        ),
        tip(
            id = "noise-generation",
            title = R.string.help_tip_noise_generation_title,
            subtitle = R.string.help_tip_noise_generation_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = creativeTools,
            deepLink = Screen.NoiseGeneration,
            pageTitle = R.string.help_tip_noise_generation_page_title,
            description = R.string.help_tip_noise_generation_description,
            steps = listOf(
                R.string.help_tip_noise_generation_step_1,
                R.string.help_tip_noise_generation_step_2,
                R.string.help_tip_noise_generation_step_3
            )
        ),
        tip(
            id = "large-files",
            title = R.string.help_tip_large_files_title,
            subtitle = R.string.help_tip_large_files_subtitle,
            icon = Icons.Rounded.HelpOutline,
            category = troubleshooting,
            pageTitle = R.string.help_tip_large_files_page_title,
            description = R.string.help_tip_large_files_description,
            steps = listOf(
                R.string.help_tip_large_files_step_1,
                R.string.help_tip_large_files_step_2,
                R.string.help_tip_large_files_step_3
            )
        ),
        tip(
            id = "transparent-output",
            title = R.string.help_tip_transparent_output_title,
            subtitle = R.string.help_tip_transparent_output_subtitle,
            icon = Icons.Rounded.Eraser,
            category = troubleshooting,
            deepLink = Screen.EraseBackground(),
            pageTitle = R.string.help_tip_transparent_output_page_title,
            description = R.string.help_tip_transparent_output_description,
            steps = listOf(
                R.string.help_tip_transparent_output_step_1,
                R.string.help_tip_transparent_output_step_2,
                R.string.help_tip_transparent_output_step_3
            )
        ),
        tip(
            id = "sharing-import",
            title = R.string.help_tip_sharing_import_title,
            subtitle = R.string.help_tip_sharing_import_subtitle,
            icon = Icons.Rounded.HelpOutline,
            category = troubleshooting,
            pageTitle = R.string.help_tip_sharing_import_page_title,
            description = R.string.help_tip_sharing_import_description,
            steps = listOf(
                R.string.help_tip_sharing_import_step_1,
                R.string.help_tip_sharing_import_step_2,
                R.string.help_tip_sharing_import_step_3
            )
        ),
        tip(
            id = "logs-feedback",
            title = R.string.help_tip_logs_feedback_title,
            subtitle = R.string.help_tip_logs_feedback_subtitle,
            icon = Icons.Rounded.HelpOutline,
            category = troubleshooting,
            deepLink = Screen.AppLogs,
            pageTitle = R.string.help_tip_logs_feedback_page_title,
            description = R.string.help_tip_logs_feedback_description,
            steps = listOf(
                R.string.help_tip_logs_feedback_step_1,
                R.string.help_tip_logs_feedback_step_2,
                R.string.help_tip_logs_feedback_step_3
            )
        )
    )

    fun getTipsForCategory(category: HelpCategory): List<HelpTip> = tips.filter {
        it.category == category
    }

    fun getTip(id: String): HelpTip? = tips.firstOrNull {
        it.id == id
    }

    fun getCategory(categoryKey: String): HelpCategory? = categories.firstOrNull {
        it.key == categoryKey
    }

    private fun tip(
        id: String,
        @StringRes title: Int,
        @StringRes subtitle: Int,
        icon: ImageVector,
        category: HelpCategory,
        @StringRes pageTitle: Int,
        @StringRes description: Int,
        steps: List<Int>,
        deepLink: Screen? = null
    ) = HelpTip(
        id = id,
        title = title,
        subtitle = subtitle,
        icon = icon,
        category = category,
        pages = listOf(
            HelpPage(
                title = pageTitle,
                description = description,
                steps = steps
            )
        ),
        deepLink = deepLink
    )
}
