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

@file:Suppress("ConstPropertyName")

package com.t8rin.imagetoolbox.feature.help.data

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Album
import com.t8rin.imagetoolbox.core.resources.icons.Animation
import com.t8rin.imagetoolbox.core.resources.icons.AspectRatio
import com.t8rin.imagetoolbox.core.resources.icons.AutoFixHigh
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.resources.icons.Base64
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BubbleDelete
import com.t8rin.imagetoolbox.core.resources.icons.Collage
import com.t8rin.imagetoolbox.core.resources.icons.Compare
import com.t8rin.imagetoolbox.core.resources.icons.ContentPaste
import com.t8rin.imagetoolbox.core.resources.icons.CropSmall
import com.t8rin.imagetoolbox.core.resources.icons.Description
import com.t8rin.imagetoolbox.core.resources.icons.DocumentScanner
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.Encrypted
import com.t8rin.imagetoolbox.core.resources.icons.Eraser
import com.t8rin.imagetoolbox.core.resources.icons.EvShadow
import com.t8rin.imagetoolbox.core.resources.icons.ExifEdit
import com.t8rin.imagetoolbox.core.resources.icons.Eyedropper
import com.t8rin.imagetoolbox.core.resources.icons.File
import com.t8rin.imagetoolbox.core.resources.icons.FileReplace
import com.t8rin.imagetoolbox.core.resources.icons.FolderZip
import com.t8rin.imagetoolbox.core.resources.icons.FormatPaintVariant
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.resources.icons.HardDrive
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.resources.icons.Help
import com.t8rin.imagetoolbox.core.resources.icons.ImageConvert
import com.t8rin.imagetoolbox.core.resources.icons.ImageDownload
import com.t8rin.imagetoolbox.core.resources.icons.ImageEdit
import com.t8rin.imagetoolbox.core.resources.icons.ImageResize
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.resources.icons.ImageWeight
import com.t8rin.imagetoolbox.core.resources.icons.LabelPercent
import com.t8rin.imagetoolbox.core.resources.icons.Lightbulb
import com.t8rin.imagetoolbox.core.resources.icons.MeshGradient
import com.t8rin.imagetoolbox.core.resources.icons.MultipleImageEdit
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.NoiseAlt
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.resources.icons.PaletteSwatch
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.resources.icons.PhotoSizeSelectLarge
import com.t8rin.imagetoolbox.core.resources.icons.PhotoSizeSelectSmall
import com.t8rin.imagetoolbox.core.resources.icons.Preview
import com.t8rin.imagetoolbox.core.resources.icons.QrCode
import com.t8rin.imagetoolbox.core.resources.icons.QualityHigh
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.resources.icons.SaveAs
import com.t8rin.imagetoolbox.core.resources.icons.SaveConfirm
import com.t8rin.imagetoolbox.core.resources.icons.Scanner
import com.t8rin.imagetoolbox.core.resources.icons.Scissors
import com.t8rin.imagetoolbox.core.resources.icons.Share
import com.t8rin.imagetoolbox.core.resources.icons.ShieldLock
import com.t8rin.imagetoolbox.core.resources.icons.SplitAlt
import com.t8rin.imagetoolbox.core.resources.icons.SquareFoot
import com.t8rin.imagetoolbox.core.resources.icons.StackSticky
import com.t8rin.imagetoolbox.core.resources.icons.SwapVerticalCircle
import com.t8rin.imagetoolbox.core.resources.icons.TagText
import com.t8rin.imagetoolbox.core.resources.icons.TextSearch
import com.t8rin.imagetoolbox.core.resources.icons.Unarchive
import com.t8rin.imagetoolbox.core.resources.icons.VectorPolyline
import com.t8rin.imagetoolbox.core.resources.icons.WallpaperAlt
import com.t8rin.imagetoolbox.core.resources.icons.Watermark
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.help.domain.HelpRepository
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpCategory
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpPage
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpTip
import javax.inject.Inject

internal class HelpRepositoryImpl @Inject constructor() : HelpRepository {

    private object TipIds {
        const val ChooseTool = "choose-tool"
        const val ImportSaveShare = "import-save-share"
        const val BatchWorkflow = "batch-workflow"
        const val SingleEdit = "single-edit"
        const val PresetsSettings = "presets-settings"
        const val DefaultValues = "default-values"
        const val PickerLauncher = "picker-launcher"
        const val ImageSource = "image-source"
        const val SkipFilePicking = "skip-file-picking"
        const val OpenEditDirectly = "open-edit-directly"
        const val FavoritesShareTools = "favorites-share-tools"
        const val BackupRestoreSettings = "backup-restore-settings"
        const val PresetTextEntry = "preset-text-entry"
        const val LoadNetImage = "load-net-image"
        const val ResizeConvert = "resize-convert"
        const val ResizeByFileSize = "resize-by-file-size"
        const val LimitResize = "limit-resize"
        const val ResizeType = "resize-type"
        const val ImageScaleMode = "image-scale-mode"
        const val ScaleColorSpace = "scale-color-space"
        const val LimitResizeModes = "limit-resize-modes"
        const val AspectRatioTargets = "aspect-ratio-targets"
        const val UpscaleVsResize = "upscale-vs-resize"
        const val CropStraighten = "crop-straighten"
        const val Filters = "filters"
        const val FilterOrder = "filter-order"
        const val PreviewBeforeEditing = "preview-before-editing"
        const val BackgroundRemover = "background-remover"
        const val BackgroundEdges = "background-edges"
        const val DrawWatermark = "draw-watermark"
        const val WatermarkReadability = "watermark-readability"
        const val DrawDefaults = "draw-defaults"
        const val CollageStitchStack = "collage-stitch-stack"
        const val ImageSplitting = "image-splitting"
        const val ImageCutting = "image-cutting"
        const val FormatConversion = "format-conversion"
        const val FormatChoice = "format-choice"
        const val AudioCoverExtractor = "audio-cover-extractor"
        const val ExifPrivacy = "exif-privacy"
        const val MetadataDatesLocation = "metadata-dates-location"
        const val AutomaticExifCleanup = "automatic-exif-cleanup"
        const val Filenames = "filenames"
        const val FilenameCollisionSafety = "filename-collision-safety"
        const val OverwriteFiles = "overwrite-files"
        const val FilenamePatterns = "filename-patterns"
        const val SaveLocation = "save-location"
        const val SaveVsShare = "save-vs-share"
        const val SaveOriginalFolder = "save-original-folder"
        const val OneTimeSaveLocation = "one-time-save-location"
        const val SkipLargerOutputs = "skip-larger-outputs"
        const val QualityVsSize = "quality-vs-size"
        const val AnimatedFormats = "animated-formats"
        const val ComparePreview = "compare-preview"
        const val PdfHub = "pdf-hub"
        const val ImagesToPdf = "images-to-pdf"
        const val PdfPageSizeMargins = "pdf-page-size-margins"
        const val DocumentScanner = "document-scanner"
        const val ScanCleanup = "scan-cleanup"
        const val PdfProtection = "pdf-protection"
        const val PdfPageOrganization = "pdf-page-organization"
        const val PdfAnnotations = "pdf-annotations"
        const val PdfOptimizeRepair = "pdf-optimize-repair"
        const val PdfExtractImages = "pdf-extract-images"
        const val PdfMetadataFlattenPrint = "pdf-metadata-flatten-print"
        const val Ocr = "ocr"
        const val OcrPreprocessing = "ocr-preprocessing"
        const val OcrLanguageData = "ocr-language-data"
        const val SearchablePdf = "searchable-pdf"
        const val QrCode = "qr-code"
        const val QrContentSafety = "qr-content-safety"
        const val QrGeneration = "qr-generation"
        const val Base64Checksum = "base64-checksum"
        const val CipherZip = "cipher-zip"
        const val ChecksumsForNames = "checksums-for-names"
        const val ChecksumToolTabs = "checksum-tool-tabs"
        const val ClipboardAndLinks = "clipboard-and-links"
        const val ClipboardPrivacy = "clipboard-privacy"
        const val PickColor = "pick-color"
        const val ColorFormats = "color-formats"
        const val PaletteLibrary = "palette-library"
        const val Gradients = "gradients"
        const val ColorLibrarySearch = "color-library-search"
        const val MeshGradientsCollection = "mesh-gradients-collection"
        const val ColorAccessibility = "color-accessibility"
        const val AlphaBackgroundColor = "alpha-background-color"
        const val AiModels = "ai-models"
        const val AiParameters = "ai-parameters"
        const val AiStability = "ai-stability"
        const val ShaderStudio = "shader-studio"
        const val SvgAscii = "svg-ascii"
        const val NoiseGeneration = "noise-generation"
        const val GeneratedAssetResolution = "generated-asset-resolution"
        const val WallpaperExport = "wallpaper-export"
        const val MarkupProjects = "markup-projects"
        const val EncryptionWorkflow = "encryption-workflow"
        const val ToolArrangement = "tool-arrangement"
        const val LargeFiles = "large-files"
        const val CacheAndPreviews = "cache-and-previews"
        const val ScreenBehavior = "screen-behavior"
        const val CompactControls = "compact-controls"
        const val TransparentOutput = "transparent-output"
        const val SharingImport = "sharing-import"
        const val ExitConfirmation = "exit-confirmation"
        const val LogsFeedback = "logs-feedback"
    }

    private val gettingStarted = HelpCategory(
        key = "getting-started",
        title = R.string.help_category_getting_started,
        subtitle = R.string.help_category_getting_started_sub,
        icon = Icons.Outlined.Lightbulb
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
        icon = Icons.Outlined.Palette
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
        icon = Icons.Outlined.Help
    )

    override val categories: List<HelpCategory> = listOf(
        gettingStarted,
        imageEditing,
        filesAndMetadata,
        pdfAndDocuments,
        textAndData,
        colorTools,
        creativeTools,
        troubleshooting
    )

    override val tips: List<HelpTip> = listOf(
        tip(
            id = TipIds.ChooseTool,
            title = R.string.help_tip_choose_tool_title,
            subtitle = R.string.help_tip_choose_tool_subtitle,
            icon = Icons.Outlined.Lightbulb,
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
            id = TipIds.ImportSaveShare,
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
            id = TipIds.BatchWorkflow,
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
            id = TipIds.SingleEdit,
            title = R.string.help_tip_single_edit_title,
            subtitle = R.string.help_tip_single_edit_subtitle,
            icon = Icons.Outlined.ImageEdit,
            category = gettingStarted,
            deepLink = Screen.SingleEdit(),
            pageTitle = R.string.help_tip_single_edit_page_title,
            description = R.string.help_tip_single_edit_description,
            steps = listOf(
                R.string.help_tip_single_edit_step_1,
                R.string.help_tip_single_edit_step_2,
                R.string.help_tip_single_edit_step_3
            )
        ),
        tip(
            id = TipIds.PresetsSettings,
            title = R.string.help_tip_presets_settings_title,
            subtitle = R.string.help_tip_presets_settings_subtitle,
            icon = Icons.Outlined.LabelPercent,
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
            id = TipIds.DefaultValues,
            title = R.string.help_tip_default_values_title,
            subtitle = R.string.help_tip_default_values_subtitle,
            icon = Icons.Rounded.SquareFoot,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.DefaultImageFormat),
            pageTitle = R.string.help_tip_default_values_page_title,
            description = R.string.help_tip_default_values_description,
            steps = listOf(
                R.string.help_tip_default_values_step_1,
                R.string.help_tip_default_values_step_2,
                R.string.help_tip_default_values_step_3
            )
        ),
        tip(
            id = TipIds.PickerLauncher,
            title = R.string.help_tip_picker_launcher_title,
            subtitle = R.string.help_tip_picker_launcher_subtitle,
            icon = Icons.Outlined.Lightbulb,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.EnableLauncherMode),
            pageTitle = R.string.help_tip_picker_launcher_page_title,
            description = R.string.help_tip_picker_launcher_description,
            steps = listOf(
                R.string.help_tip_picker_launcher_step_1,
                R.string.help_tip_picker_launcher_step_2,
                R.string.help_tip_picker_launcher_step_3
            )
        ),
        tip(
            id = TipIds.ImageSource,
            title = R.string.help_tip_image_source_title,
            subtitle = R.string.help_tip_image_source_subtitle,
            icon = Icons.Outlined.ImageSearch,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.ImagePickerMode),
            pageTitle = R.string.help_tip_image_source_page_title,
            description = R.string.help_tip_image_source_description,
            steps = listOf(
                R.string.help_tip_image_source_step_1,
                R.string.help_tip_image_source_step_2,
                R.string.help_tip_image_source_step_3
            )
        ),
        tip(
            id = TipIds.SkipFilePicking,
            title = R.string.help_tip_skip_file_picking_title,
            subtitle = R.string.help_tip_skip_file_picking_subtitle,
            icon = Icons.Outlined.ImageSearch,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.SkipFilePicking),
            pageTitle = R.string.help_tip_skip_file_picking_page_title,
            description = R.string.help_tip_skip_file_picking_description,
            steps = listOf(
                R.string.help_tip_skip_file_picking_step_1,
                R.string.help_tip_skip_file_picking_step_2,
                R.string.help_tip_skip_file_picking_step_3
            )
        ),
        tip(
            id = TipIds.OpenEditDirectly,
            title = R.string.help_tip_open_edit_directly_title,
            subtitle = R.string.help_tip_open_edit_directly_subtitle,
            icon = Icons.Outlined.ImageEdit,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.OpenEditInsteadOfPreview),
            pageTitle = R.string.help_tip_open_edit_directly_page_title,
            description = R.string.help_tip_open_edit_directly_description,
            steps = listOf(
                R.string.help_tip_open_edit_directly_step_1,
                R.string.help_tip_open_edit_directly_step_2,
                R.string.help_tip_open_edit_directly_step_3
            )
        ),
        tip(
            id = TipIds.FavoritesShareTools,
            title = R.string.help_tip_favorites_share_tools_title,
            subtitle = R.string.help_tip_favorites_share_tools_subtitle,
            icon = Icons.Outlined.Bookmark,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.ToolsHiddenForShare),
            pageTitle = R.string.help_tip_favorites_share_tools_page_title,
            description = R.string.help_tip_favorites_share_tools_description,
            steps = listOf(
                R.string.help_tip_favorites_share_tools_step_1,
                R.string.help_tip_favorites_share_tools_step_2,
                R.string.help_tip_favorites_share_tools_step_3
            )
        ),
        tip(
            id = TipIds.ToolArrangement,
            title = R.string.help_tip_tool_arrangement_title,
            subtitle = R.string.help_tip_tool_arrangement_subtitle,
            icon = Icons.Outlined.Lightbulb,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.ScreenOrder),
            pageTitle = R.string.help_tip_tool_arrangement_page_title,
            description = R.string.help_tip_tool_arrangement_description,
            steps = listOf(
                R.string.help_tip_tool_arrangement_step_1,
                R.string.help_tip_tool_arrangement_step_2,
                R.string.help_tip_tool_arrangement_step_3
            )
        ),
        tip(
            id = TipIds.BackupRestoreSettings,
            title = R.string.help_tip_backup_restore_settings_title,
            subtitle = R.string.help_tip_backup_restore_settings_subtitle,
            icon = Icons.Outlined.HardDrive,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.Backup),
            pageTitle = R.string.help_tip_backup_restore_settings_page_title,
            description = R.string.help_tip_backup_restore_settings_description,
            steps = listOf(
                R.string.help_tip_backup_restore_settings_step_1,
                R.string.help_tip_backup_restore_settings_step_2,
                R.string.help_tip_backup_restore_settings_step_3
            )
        ),
        tip(
            id = TipIds.PresetTextEntry,
            title = R.string.help_tip_preset_text_entry_title,
            subtitle = R.string.help_tip_preset_text_entry_subtitle,
            icon = Icons.Outlined.LabelPercent,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.CanEnterPresetsByTextField),
            pageTitle = R.string.help_tip_preset_text_entry_page_title,
            description = R.string.help_tip_preset_text_entry_description,
            steps = listOf(
                R.string.help_tip_preset_text_entry_step_1,
                R.string.help_tip_preset_text_entry_step_2,
                R.string.help_tip_preset_text_entry_step_3
            )
        ),
        tip(
            id = TipIds.LoadNetImage,
            title = R.string.help_tip_load_net_image_title,
            subtitle = R.string.help_tip_load_net_image_subtitle,
            icon = Icons.Outlined.ImageDownload,
            category = gettingStarted,
            deepLink = Screen.LoadNetImage(),
            pageTitle = R.string.help_tip_load_net_image_page_title,
            description = R.string.help_tip_load_net_image_description,
            steps = listOf(
                R.string.help_tip_load_net_image_step_1,
                R.string.help_tip_load_net_image_step_2,
                R.string.help_tip_load_net_image_step_3
            )
        ),
        tip(
            id = TipIds.ResizeConvert,
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
            id = TipIds.ResizeByFileSize,
            title = R.string.help_tip_resize_by_file_size_title,
            subtitle = R.string.help_tip_resize_by_file_size_subtitle,
            icon = Icons.Outlined.ImageWeight,
            category = imageEditing,
            deepLink = Screen.WeightResize(),
            pageTitle = R.string.help_tip_resize_by_file_size_page_title,
            description = R.string.help_tip_resize_by_file_size_description,
            steps = listOf(
                R.string.help_tip_resize_by_file_size_step_1,
                R.string.help_tip_resize_by_file_size_step_2,
                R.string.help_tip_resize_by_file_size_step_3
            )
        ),
        tip(
            id = TipIds.LimitResize,
            title = R.string.help_tip_limit_resize_title,
            subtitle = R.string.help_tip_limit_resize_subtitle,
            icon = Icons.Outlined.ImageResize,
            category = imageEditing,
            deepLink = Screen.LimitResize(),
            pageTitle = R.string.help_tip_limit_resize_page_title,
            description = R.string.help_tip_limit_resize_description,
            steps = listOf(
                R.string.help_tip_limit_resize_step_1,
                R.string.help_tip_limit_resize_step_2,
                R.string.help_tip_limit_resize_step_3
            )
        ),
        tip(
            id = TipIds.ResizeType,
            title = R.string.help_tip_resize_type_title,
            subtitle = R.string.help_tip_resize_type_subtitle,
            icon = Icons.Outlined.PhotoSizeSelectLarge,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.DefaultResizeType),
            pageTitle = R.string.help_tip_resize_type_page_title,
            description = R.string.help_tip_resize_type_description,
            steps = listOf(
                R.string.help_tip_resize_type_step_1,
                R.string.help_tip_resize_type_step_2,
                R.string.help_tip_resize_type_step_3
            )
        ),
        tip(
            id = TipIds.ImageScaleMode,
            title = R.string.help_tip_image_scale_mode_title,
            subtitle = R.string.help_tip_image_scale_mode_subtitle,
            icon = Icons.Outlined.QualityHigh,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.DefaultScaleMode),
            pageTitle = R.string.help_tip_image_scale_mode_page_title,
            description = R.string.help_tip_image_scale_mode_description,
            steps = listOf(
                R.string.help_tip_image_scale_mode_step_1,
                R.string.help_tip_image_scale_mode_step_2,
                R.string.help_tip_image_scale_mode_step_3
            )
        ),
        tip(
            id = TipIds.ScaleColorSpace,
            title = R.string.help_tip_scale_color_space_title,
            subtitle = R.string.help_tip_scale_color_space_subtitle,
            icon = Icons.Outlined.Palette,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.DefaultColorSpace),
            pageTitle = R.string.help_tip_scale_color_space_page_title,
            description = R.string.help_tip_scale_color_space_description,
            steps = listOf(
                R.string.help_tip_scale_color_space_step_1,
                R.string.help_tip_scale_color_space_step_2,
                R.string.help_tip_scale_color_space_step_3
            )
        ),
        tip(
            id = TipIds.LimitResizeModes,
            title = R.string.help_tip_limit_resize_modes_title,
            subtitle = R.string.help_tip_limit_resize_modes_subtitle,
            icon = Icons.Outlined.ImageWeight,
            category = imageEditing,
            deepLink = Screen.LimitResize(),
            pageTitle = R.string.help_tip_limit_resize_modes_page_title,
            description = R.string.help_tip_limit_resize_modes_description,
            steps = listOf(
                R.string.help_tip_limit_resize_modes_step_1,
                R.string.help_tip_limit_resize_modes_step_2,
                R.string.help_tip_limit_resize_modes_step_3
            )
        ),
        tip(
            id = TipIds.AspectRatioTargets,
            title = R.string.help_tip_aspect_ratio_targets_title,
            subtitle = R.string.help_tip_aspect_ratio_targets_subtitle,
            icon = Icons.Outlined.AspectRatio,
            category = imageEditing,
            deepLink = Screen.ResizeAndConvert(),
            pageTitle = R.string.help_tip_aspect_ratio_targets_page_title,
            description = R.string.help_tip_aspect_ratio_targets_description,
            steps = listOf(
                R.string.help_tip_aspect_ratio_targets_step_1,
                R.string.help_tip_aspect_ratio_targets_step_2,
                R.string.help_tip_aspect_ratio_targets_step_3
            )
        ),
        tip(
            id = TipIds.UpscaleVsResize,
            title = R.string.help_tip_upscale_vs_resize_title,
            subtitle = R.string.help_tip_upscale_vs_resize_subtitle,
            icon = Icons.Outlined.Neurology,
            category = imageEditing,
            deepLink = Screen.ResizeAndConvert(),
            pageTitle = R.string.help_tip_upscale_vs_resize_page_title,
            description = R.string.help_tip_upscale_vs_resize_description,
            steps = listOf(
                R.string.help_tip_upscale_vs_resize_step_1,
                R.string.help_tip_upscale_vs_resize_step_2,
                R.string.help_tip_upscale_vs_resize_step_3
            )
        ),
        tip(
            id = TipIds.CropStraighten,
            title = R.string.help_tip_crop_straighten_title,
            subtitle = R.string.help_tip_crop_straighten_subtitle,
            icon = Icons.Rounded.CropSmall,
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
            id = TipIds.Filters,
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
            id = TipIds.FilterOrder,
            title = R.string.help_tip_filter_order_title,
            subtitle = R.string.help_tip_filter_order_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = imageEditing,
            deepLink = Screen.Filter(),
            pageTitle = R.string.help_tip_filter_order_page_title,
            description = R.string.help_tip_filter_order_description,
            steps = listOf(
                R.string.help_tip_filter_order_step_1,
                R.string.help_tip_filter_order_step_2,
                R.string.help_tip_filter_order_step_3
            )
        ),
        tip(
            id = TipIds.PreviewBeforeEditing,
            title = R.string.help_tip_preview_before_editing_title,
            subtitle = R.string.help_tip_preview_before_editing_subtitle,
            icon = Icons.Outlined.Preview,
            category = imageEditing,
            deepLink = Screen.ImagePreview(),
            pageTitle = R.string.help_tip_preview_before_editing_page_title,
            description = R.string.help_tip_preview_before_editing_description,
            steps = listOf(
                R.string.help_tip_preview_before_editing_step_1,
                R.string.help_tip_preview_before_editing_step_2,
                R.string.help_tip_preview_before_editing_step_3
            )
        ),
        tip(
            id = TipIds.BackgroundRemover,
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
            id = TipIds.BackgroundEdges,
            title = R.string.help_tip_background_edges_title,
            subtitle = R.string.help_tip_background_edges_subtitle,
            icon = Icons.Rounded.Eraser,
            category = imageEditing,
            deepLink = Screen.EraseBackground(),
            pageTitle = R.string.help_tip_background_edges_page_title,
            description = R.string.help_tip_background_edges_description,
            steps = listOf(
                R.string.help_tip_background_edges_step_1,
                R.string.help_tip_background_edges_step_2,
                R.string.help_tip_background_edges_step_3
            )
        ),
        tip(
            id = TipIds.DrawWatermark,
            title = R.string.help_tip_draw_watermark_title,
            subtitle = R.string.help_tip_draw_watermark_subtitle,
            icon = Icons.Outlined.Draw,
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
            id = TipIds.WatermarkReadability,
            title = R.string.help_tip_watermark_readability_title,
            subtitle = R.string.help_tip_watermark_readability_subtitle,
            icon = Icons.Outlined.Watermark,
            category = imageEditing,
            deepLink = Screen.Watermarking(),
            pageTitle = R.string.help_tip_watermark_readability_page_title,
            description = R.string.help_tip_watermark_readability_description,
            steps = listOf(
                R.string.help_tip_watermark_readability_step_1,
                R.string.help_tip_watermark_readability_step_2,
                R.string.help_tip_watermark_readability_step_3
            )
        ),
        tip(
            id = TipIds.DrawDefaults,
            title = R.string.help_tip_draw_defaults_title,
            subtitle = R.string.help_tip_draw_defaults_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.DefaultDrawLineWidth),
            pageTitle = R.string.help_tip_draw_defaults_page_title,
            description = R.string.help_tip_draw_defaults_description,
            steps = listOf(
                R.string.help_tip_draw_defaults_step_1,
                R.string.help_tip_draw_defaults_step_2,
                R.string.help_tip_draw_defaults_step_3
            )
        ),
        tip(
            id = TipIds.CollageStitchStack,
            title = R.string.help_tip_collage_stitch_stack_title,
            subtitle = R.string.help_tip_collage_stitch_stack_subtitle,
            icon = Icons.Outlined.Collage,
            category = imageEditing,
            deepLink = Screen.CollageMaker(),
            pageTitle = R.string.help_tip_collage_stitch_stack_page_title,
            description = R.string.help_tip_collage_stitch_stack_description,
            steps = listOf(
                R.string.help_tip_collage_stitch_stack_step_1,
                R.string.help_tip_collage_stitch_stack_step_2,
                R.string.help_tip_collage_stitch_stack_step_3
            )
        ),
        tip(
            id = TipIds.ImageSplitting,
            title = R.string.help_tip_image_splitting_title,
            subtitle = R.string.help_tip_image_splitting_subtitle,
            icon = Icons.Outlined.SplitAlt,
            category = imageEditing,
            deepLink = Screen.ImageSplitting(),
            pageTitle = R.string.help_tip_image_splitting_page_title,
            description = R.string.help_tip_image_splitting_description,
            steps = listOf(
                R.string.help_tip_image_splitting_step_1,
                R.string.help_tip_image_splitting_step_2,
                R.string.help_tip_image_splitting_step_3
            )
        ),
        tip(
            id = TipIds.ImageCutting,
            title = R.string.help_tip_image_cutting_title,
            subtitle = R.string.help_tip_image_cutting_subtitle,
            icon = Icons.Rounded.Scissors,
            category = imageEditing,
            deepLink = Screen.ImageCutter(),
            pageTitle = R.string.help_tip_image_cutting_page_title,
            description = R.string.help_tip_image_cutting_description,
            steps = listOf(
                R.string.help_tip_image_cutting_step_1,
                R.string.help_tip_image_cutting_step_2,
                R.string.help_tip_image_cutting_step_3
            )
        ),
        tip(
            id = TipIds.FormatConversion,
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
            id = TipIds.FormatChoice,
            title = R.string.help_tip_format_choice_title,
            subtitle = R.string.help_tip_format_choice_subtitle,
            icon = Icons.Outlined.ImageConvert,
            category = filesAndMetadata,
            deepLink = Screen.FormatConversion(),
            pageTitle = R.string.help_tip_format_choice_page_title,
            description = R.string.help_tip_format_choice_description,
            steps = listOf(
                R.string.help_tip_format_choice_step_1,
                R.string.help_tip_format_choice_step_2,
                R.string.help_tip_format_choice_step_3
            )
        ),
        tip(
            id = TipIds.AudioCoverExtractor,
            title = R.string.help_tip_audio_cover_extractor_title,
            subtitle = R.string.help_tip_audio_cover_extractor_subtitle,
            icon = Icons.Outlined.Album,
            category = filesAndMetadata,
            deepLink = Screen.AudioCoverExtractor(),
            pageTitle = R.string.help_tip_audio_cover_extractor_page_title,
            description = R.string.help_tip_audio_cover_extractor_description,
            steps = listOf(
                R.string.help_tip_audio_cover_extractor_step_1,
                R.string.help_tip_audio_cover_extractor_step_2,
                R.string.help_tip_audio_cover_extractor_step_3
            )
        ),
        tip(
            id = TipIds.ExifPrivacy,
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
            id = TipIds.MetadataDatesLocation,
            title = R.string.help_tip_metadata_dates_location_title,
            subtitle = R.string.help_tip_metadata_dates_location_subtitle,
            icon = Icons.Outlined.ExifEdit,
            category = filesAndMetadata,
            deepLink = Screen.EditExif(),
            pageTitle = R.string.help_tip_metadata_dates_location_page_title,
            description = R.string.help_tip_metadata_dates_location_description,
            steps = listOf(
                R.string.help_tip_metadata_dates_location_step_1,
                R.string.help_tip_metadata_dates_location_step_2,
                R.string.help_tip_metadata_dates_location_step_3
            )
        ),
        tip(
            id = TipIds.AutomaticExifCleanup,
            title = R.string.help_tip_automatic_exif_cleanup_title,
            subtitle = R.string.help_tip_automatic_exif_cleanup_subtitle,
            icon = Icons.Outlined.ExifEdit,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.AlwaysClearExif),
            pageTitle = R.string.help_tip_automatic_exif_cleanup_page_title,
            description = R.string.help_tip_automatic_exif_cleanup_description,
            steps = listOf(
                R.string.help_tip_automatic_exif_cleanup_step_1,
                R.string.help_tip_automatic_exif_cleanup_step_2,
                R.string.help_tip_automatic_exif_cleanup_step_3
            )
        ),
        tip(
            id = TipIds.Filenames,
            title = R.string.help_tip_filenames_title,
            subtitle = R.string.help_tip_filenames_subtitle,
            icon = Icons.Outlined.File,
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
            id = TipIds.FilenameCollisionSafety,
            title = R.string.help_tip_filename_collision_safety_title,
            subtitle = R.string.help_tip_filename_collision_safety_subtitle,
            icon = Icons.Outlined.File,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.FilenamePattern),
            pageTitle = R.string.help_tip_filename_collision_safety_page_title,
            description = R.string.help_tip_filename_collision_safety_description,
            steps = listOf(
                R.string.help_tip_filename_collision_safety_step_1,
                R.string.help_tip_filename_collision_safety_step_2,
                R.string.help_tip_filename_collision_safety_step_3
            )
        ),
        tip(
            id = TipIds.OverwriteFiles,
            title = R.string.help_tip_overwrite_files_title,
            subtitle = R.string.help_tip_overwrite_files_subtitle,
            icon = Icons.Outlined.FileReplace,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.OverwriteFiles),
            pageTitle = R.string.help_tip_overwrite_files_page_title,
            description = R.string.help_tip_overwrite_files_description,
            steps = listOf(
                R.string.help_tip_overwrite_files_step_1,
                R.string.help_tip_overwrite_files_step_2,
                R.string.help_tip_overwrite_files_step_3
            )
        ),
        tip(
            id = TipIds.FilenamePatterns,
            title = R.string.help_tip_filename_patterns_title,
            subtitle = R.string.help_tip_filename_patterns_subtitle,
            icon = Icons.Outlined.Description,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.FilenamePattern),
            pageTitle = R.string.help_tip_filename_patterns_page_title,
            description = R.string.help_tip_filename_patterns_description,
            steps = listOf(
                R.string.help_tip_filename_patterns_step_1,
                R.string.help_tip_filename_patterns_step_2,
                R.string.help_tip_filename_patterns_step_3
            )
        ),
        tip(
            id = TipIds.SaveLocation,
            title = R.string.help_tip_save_location_title,
            subtitle = R.string.help_tip_save_location_subtitle,
            icon = Icons.Outlined.SaveAs,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.SavingFolder),
            pageTitle = R.string.help_tip_save_location_page_title,
            description = R.string.help_tip_save_location_description,
            steps = listOf(
                R.string.help_tip_save_location_step_1,
                R.string.help_tip_save_location_step_2,
                R.string.help_tip_save_location_step_3
            )
        ),
        tip(
            id = TipIds.SaveVsShare,
            title = R.string.help_tip_save_vs_share_title,
            subtitle = R.string.help_tip_save_vs_share_subtitle,
            icon = Icons.Outlined.Share,
            category = filesAndMetadata,
            pageTitle = R.string.help_tip_save_vs_share_page_title,
            description = R.string.help_tip_save_vs_share_description,
            steps = listOf(
                R.string.help_tip_save_vs_share_step_1,
                R.string.help_tip_save_vs_share_step_2,
                R.string.help_tip_save_vs_share_step_3
            )
        ),
        tip(
            id = TipIds.SaveOriginalFolder,
            title = R.string.help_tip_save_original_folder_title,
            subtitle = R.string.help_tip_save_original_folder_subtitle,
            icon = Icons.Outlined.SaveAs,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.SaveToOriginalFolder),
            pageTitle = R.string.help_tip_save_original_folder_page_title,
            description = R.string.help_tip_save_original_folder_description,
            steps = listOf(
                R.string.help_tip_save_original_folder_step_1,
                R.string.help_tip_save_original_folder_step_2,
                R.string.help_tip_save_original_folder_step_3
            )
        ),
        tip(
            id = TipIds.OneTimeSaveLocation,
            title = R.string.help_tip_one_time_save_location_title,
            subtitle = R.string.help_tip_one_time_save_location_subtitle,
            icon = Icons.Outlined.Save,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.OneTimeSaveLocation),
            pageTitle = R.string.help_tip_one_time_save_location_page_title,
            description = R.string.help_tip_one_time_save_location_description,
            steps = listOf(
                R.string.help_tip_one_time_save_location_step_1,
                R.string.help_tip_one_time_save_location_step_2,
                R.string.help_tip_one_time_save_location_step_3
            )
        ),
        tip(
            id = TipIds.SkipLargerOutputs,
            title = R.string.help_tip_skip_larger_outputs_title,
            subtitle = R.string.help_tip_skip_larger_outputs_subtitle,
            icon = Icons.Outlined.ImageWeight,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.AllowSkipIfLarger),
            pageTitle = R.string.help_tip_skip_larger_outputs_page_title,
            description = R.string.help_tip_skip_larger_outputs_description,
            steps = listOf(
                R.string.help_tip_skip_larger_outputs_step_1,
                R.string.help_tip_skip_larger_outputs_step_2,
                R.string.help_tip_skip_larger_outputs_step_3
            )
        ),
        tip(
            id = TipIds.QualityVsSize,
            title = R.string.help_tip_quality_vs_size_title,
            subtitle = R.string.help_tip_quality_vs_size_subtitle,
            icon = Icons.Outlined.QualityHigh,
            category = filesAndMetadata,
            deepLink = Screen.ResizeAndConvert(),
            pageTitle = R.string.help_tip_quality_vs_size_page_title,
            description = R.string.help_tip_quality_vs_size_description,
            steps = listOf(
                R.string.help_tip_quality_vs_size_step_1,
                R.string.help_tip_quality_vs_size_step_2,
                R.string.help_tip_quality_vs_size_step_3
            )
        ),
        tip(
            id = TipIds.AnimatedFormats,
            title = R.string.help_tip_animated_formats_title,
            subtitle = R.string.help_tip_animated_formats_subtitle,
            icon = Icons.Rounded.Animation,
            category = filesAndMetadata,
            deepLink = Screen.WebpTools(),
            pageTitle = R.string.help_tip_animated_formats_page_title,
            description = R.string.help_tip_animated_formats_description,
            steps = listOf(
                R.string.help_tip_animated_formats_step_1,
                R.string.help_tip_animated_formats_step_2,
                R.string.help_tip_animated_formats_step_3
            )
        ),
        tip(
            id = TipIds.ComparePreview,
            title = R.string.help_tip_compare_preview_title,
            subtitle = R.string.help_tip_compare_preview_subtitle,
            icon = Icons.Outlined.Compare,
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
            id = TipIds.PdfHub,
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
            id = TipIds.ImagesToPdf,
            title = R.string.help_tip_images_to_pdf_title,
            subtitle = R.string.help_tip_images_to_pdf_subtitle,
            icon = Icons.Outlined.Scanner,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.ImagesToPdf(),
            pageTitle = R.string.help_tip_images_to_pdf_page_title,
            description = R.string.help_tip_images_to_pdf_description,
            steps = listOf(
                R.string.help_tip_images_to_pdf_step_1,
                R.string.help_tip_images_to_pdf_step_2,
                R.string.help_tip_images_to_pdf_step_3
            )
        ),
        tip(
            id = TipIds.PdfPageSizeMargins,
            title = R.string.help_tip_pdf_page_size_margins_title,
            subtitle = R.string.help_tip_pdf_page_size_margins_subtitle,
            icon = Icons.Outlined.Pdf,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pageTitle = R.string.help_tip_pdf_page_size_margins_page_title,
            description = R.string.help_tip_pdf_page_size_margins_description,
            steps = listOf(
                R.string.help_tip_pdf_page_size_margins_step_1,
                R.string.help_tip_pdf_page_size_margins_step_2,
                R.string.help_tip_pdf_page_size_margins_step_3
            )
        ),
        tip(
            id = TipIds.DocumentScanner,
            title = R.string.help_tip_document_scanner_title,
            subtitle = R.string.help_tip_document_scanner_subtitle,
            icon = Icons.Outlined.DocumentScanner,
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
            id = TipIds.ScanCleanup,
            title = R.string.help_tip_scan_cleanup_title,
            subtitle = R.string.help_tip_scan_cleanup_subtitle,
            icon = Icons.Outlined.DocumentScanner,
            category = pdfAndDocuments,
            deepLink = Screen.DocumentScanner,
            pageTitle = R.string.help_tip_scan_cleanup_page_title,
            description = R.string.help_tip_scan_cleanup_description,
            steps = listOf(
                R.string.help_tip_scan_cleanup_step_1,
                R.string.help_tip_scan_cleanup_step_2,
                R.string.help_tip_scan_cleanup_step_3
            )
        ),
        tip(
            id = TipIds.PdfProtection,
            title = R.string.help_tip_pdf_protection_title,
            subtitle = R.string.help_tip_pdf_protection_subtitle,
            icon = Icons.Outlined.ShieldLock,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pageTitle = R.string.help_tip_pdf_protection_page_title,
            description = R.string.help_tip_pdf_protection_description,
            steps = listOf(
                R.string.help_tip_pdf_protection_step_1,
                R.string.help_tip_pdf_protection_step_2,
                R.string.help_tip_pdf_protection_step_3
            )
        ),
        tip(
            id = TipIds.PdfPageOrganization,
            title = R.string.help_tip_pdf_page_organization_title,
            subtitle = R.string.help_tip_pdf_page_organization_subtitle,
            icon = Icons.Outlined.SwapVerticalCircle,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pageTitle = R.string.help_tip_pdf_page_organization_page_title,
            description = R.string.help_tip_pdf_page_organization_description,
            steps = listOf(
                R.string.help_tip_pdf_page_organization_step_1,
                R.string.help_tip_pdf_page_organization_step_2,
                R.string.help_tip_pdf_page_organization_step_3
            )
        ),
        tip(
            id = TipIds.PdfAnnotations,
            title = R.string.help_tip_pdf_annotations_title,
            subtitle = R.string.help_tip_pdf_annotations_subtitle,
            icon = Icons.Outlined.BubbleDelete,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pageTitle = R.string.help_tip_pdf_annotations_page_title,
            description = R.string.help_tip_pdf_annotations_description,
            steps = listOf(
                R.string.help_tip_pdf_annotations_step_1,
                R.string.help_tip_pdf_annotations_step_2,
                R.string.help_tip_pdf_annotations_step_3
            )
        ),
        tip(
            id = TipIds.PdfOptimizeRepair,
            title = R.string.help_tip_pdf_optimize_repair_title,
            subtitle = R.string.help_tip_pdf_optimize_repair_subtitle,
            icon = Icons.Outlined.Pdf,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pageTitle = R.string.help_tip_pdf_optimize_repair_page_title,
            description = R.string.help_tip_pdf_optimize_repair_description,
            steps = listOf(
                R.string.help_tip_pdf_optimize_repair_step_1,
                R.string.help_tip_pdf_optimize_repair_step_2,
                R.string.help_tip_pdf_optimize_repair_step_3
            )
        ),
        tip(
            id = TipIds.PdfExtractImages,
            title = R.string.help_tip_pdf_extract_images_title,
            subtitle = R.string.help_tip_pdf_extract_images_subtitle,
            icon = Icons.Outlined.Unarchive,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.ExtractImages(),
            pageTitle = R.string.help_tip_pdf_extract_images_page_title,
            description = R.string.help_tip_pdf_extract_images_description,
            steps = listOf(
                R.string.help_tip_pdf_extract_images_step_1,
                R.string.help_tip_pdf_extract_images_step_2,
                R.string.help_tip_pdf_extract_images_step_3
            )
        ),
        tip(
            id = TipIds.PdfMetadataFlattenPrint,
            title = R.string.help_tip_pdf_metadata_flatten_print_title,
            subtitle = R.string.help_tip_pdf_metadata_flatten_print_subtitle,
            icon = Icons.Outlined.TagText,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Metadata(),
            pageTitle = R.string.help_tip_pdf_metadata_flatten_print_page_title,
            description = R.string.help_tip_pdf_metadata_flatten_print_description,
            steps = listOf(
                R.string.help_tip_pdf_metadata_flatten_print_step_1,
                R.string.help_tip_pdf_metadata_flatten_print_step_2,
                R.string.help_tip_pdf_metadata_flatten_print_step_3
            )
        ),
        tip(
            id = TipIds.Ocr,
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
            id = TipIds.OcrPreprocessing,
            title = R.string.help_tip_ocr_preprocessing_title,
            subtitle = R.string.help_tip_ocr_preprocessing_subtitle,
            icon = Icons.Outlined.TextSearch,
            category = textAndData,
            deepLink = Screen.RecognizeText(),
            pageTitle = R.string.help_tip_ocr_preprocessing_page_title,
            description = R.string.help_tip_ocr_preprocessing_description,
            steps = listOf(
                R.string.help_tip_ocr_preprocessing_step_1,
                R.string.help_tip_ocr_preprocessing_step_2,
                R.string.help_tip_ocr_preprocessing_step_3
            )
        ),
        tip(
            id = TipIds.OcrLanguageData,
            title = R.string.help_tip_ocr_language_data_title,
            subtitle = R.string.help_tip_ocr_language_data_subtitle,
            icon = Icons.Outlined.TextSearch,
            category = textAndData,
            deepLink = Screen.RecognizeText(),
            pageTitle = R.string.help_tip_ocr_language_data_page_title,
            description = R.string.help_tip_ocr_language_data_description,
            steps = listOf(
                R.string.help_tip_ocr_language_data_step_1,
                R.string.help_tip_ocr_language_data_step_2,
                R.string.help_tip_ocr_language_data_step_3
            )
        ),
        tip(
            id = TipIds.SearchablePdf,
            title = R.string.help_tip_searchable_pdf_title,
            subtitle = R.string.help_tip_searchable_pdf_subtitle,
            icon = Icons.Outlined.Pdf,
            category = textAndData,
            deepLink = Screen.RecognizeText(),
            pageTitle = R.string.help_tip_searchable_pdf_page_title,
            description = R.string.help_tip_searchable_pdf_description,
            steps = listOf(
                R.string.help_tip_searchable_pdf_step_1,
                R.string.help_tip_searchable_pdf_step_2,
                R.string.help_tip_searchable_pdf_step_3
            )
        ),
        tip(
            id = TipIds.QrCode,
            title = R.string.help_tip_qr_code_title,
            subtitle = R.string.help_tip_qr_code_subtitle,
            icon = Icons.Outlined.QrCode,
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
            id = TipIds.QrContentSafety,
            title = R.string.help_tip_qr_content_safety_title,
            subtitle = R.string.help_tip_qr_content_safety_subtitle,
            icon = Icons.Outlined.QrCode,
            category = textAndData,
            deepLink = Screen.ScanQrCode(),
            pageTitle = R.string.help_tip_qr_content_safety_page_title,
            description = R.string.help_tip_qr_content_safety_description,
            steps = listOf(
                R.string.help_tip_qr_content_safety_step_1,
                R.string.help_tip_qr_content_safety_step_2,
                R.string.help_tip_qr_content_safety_step_3
            )
        ),
        tip(
            id = TipIds.QrGeneration,
            title = R.string.help_tip_qr_generation_title,
            subtitle = R.string.help_tip_qr_generation_subtitle,
            icon = Icons.Outlined.QrCode,
            category = textAndData,
            deepLink = Screen.ScanQrCode(),
            pageTitle = R.string.help_tip_qr_generation_page_title,
            description = R.string.help_tip_qr_generation_description,
            steps = listOf(
                R.string.help_tip_qr_generation_step_1,
                R.string.help_tip_qr_generation_step_2,
                R.string.help_tip_qr_generation_step_3
            )
        ),
        tip(
            id = TipIds.Base64Checksum,
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
            id = TipIds.CipherZip,
            title = R.string.help_tip_cipher_zip_title,
            subtitle = R.string.help_tip_cipher_zip_subtitle,
            icon = Icons.Outlined.FolderZip,
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
            id = TipIds.ChecksumsForNames,
            title = R.string.help_tip_checksums_for_names_title,
            subtitle = R.string.help_tip_checksums_for_names_subtitle,
            icon = Icons.Outlined.Base64,
            category = textAndData,
            deepLink = Screen.ChecksumTools(),
            pageTitle = R.string.help_tip_checksums_for_names_page_title,
            description = R.string.help_tip_checksums_for_names_description,
            steps = listOf(
                R.string.help_tip_checksums_for_names_step_1,
                R.string.help_tip_checksums_for_names_step_2,
                R.string.help_tip_checksums_for_names_step_3
            )
        ),
        tip(
            id = TipIds.ChecksumToolTabs,
            title = R.string.help_tip_checksum_tool_tabs_title,
            subtitle = R.string.help_tip_checksum_tool_tabs_subtitle,
            icon = Icons.Rounded.HashTag,
            category = textAndData,
            deepLink = Screen.ChecksumTools(),
            pageTitle = R.string.help_tip_checksum_tool_tabs_page_title,
            description = R.string.help_tip_checksum_tool_tabs_description,
            steps = listOf(
                R.string.help_tip_checksum_tool_tabs_step_1,
                R.string.help_tip_checksum_tool_tabs_step_2,
                R.string.help_tip_checksum_tool_tabs_step_3
            )
        ),
        tip(
            id = TipIds.ClipboardAndLinks,
            title = R.string.help_tip_clipboard_and_links_title,
            subtitle = R.string.help_tip_clipboard_and_links_subtitle,
            icon = Icons.Outlined.Share,
            category = textAndData,
            deepLink = Screen.Settings(targetSetting = Setting.AllowAutoClipboardPaste),
            pageTitle = R.string.help_tip_clipboard_and_links_page_title,
            description = R.string.help_tip_clipboard_and_links_description,
            steps = listOf(
                R.string.help_tip_clipboard_and_links_step_1,
                R.string.help_tip_clipboard_and_links_step_2,
                R.string.help_tip_clipboard_and_links_step_3
            )
        ),
        tip(
            id = TipIds.ClipboardPrivacy,
            title = R.string.help_tip_clipboard_privacy_title,
            subtitle = R.string.help_tip_clipboard_privacy_subtitle,
            icon = Icons.Rounded.ContentPaste,
            category = textAndData,
            deepLink = Screen.Settings(targetSetting = Setting.AutoPinClipboard),
            pageTitle = R.string.help_tip_clipboard_privacy_page_title,
            description = R.string.help_tip_clipboard_privacy_description,
            steps = listOf(
                R.string.help_tip_clipboard_privacy_step_1,
                R.string.help_tip_clipboard_privacy_step_2,
                R.string.help_tip_clipboard_privacy_step_3
            )
        ),
        tip(
            id = TipIds.PickColor,
            title = R.string.help_tip_pick_color_title,
            subtitle = R.string.help_tip_pick_color_subtitle,
            icon = Icons.Outlined.Eyedropper,
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
            id = TipIds.ColorFormats,
            title = R.string.help_tip_color_formats_title,
            subtitle = R.string.help_tip_color_formats_subtitle,
            icon = Icons.Outlined.Eyedropper,
            category = colorTools,
            deepLink = Screen.PickColorFromImage(),
            pageTitle = R.string.help_tip_color_formats_page_title,
            description = R.string.help_tip_color_formats_description,
            steps = listOf(
                R.string.help_tip_color_formats_step_1,
                R.string.help_tip_color_formats_step_2,
                R.string.help_tip_color_formats_step_3
            )
        ),
        tip(
            id = TipIds.PaletteLibrary,
            title = R.string.help_tip_palette_library_title,
            subtitle = R.string.help_tip_palette_library_subtitle,
            icon = Icons.Outlined.PaletteSwatch,
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
            id = TipIds.ColorLibrarySearch,
            title = R.string.help_tip_color_library_search_title,
            subtitle = R.string.help_tip_color_library_search_subtitle,
            icon = Icons.Outlined.FormatPaintVariant,
            category = colorTools,
            deepLink = Screen.ColorLibrary,
            pageTitle = R.string.help_tip_color_library_search_page_title,
            description = R.string.help_tip_color_library_search_description,
            steps = listOf(
                R.string.help_tip_color_library_search_step_1,
                R.string.help_tip_color_library_search_step_2,
                R.string.help_tip_color_library_search_step_3
            )
        ),
        tip(
            id = TipIds.Gradients,
            title = R.string.help_tip_gradients_title,
            subtitle = R.string.help_tip_gradients_subtitle,
            icon = Icons.Outlined.Gradient,
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
            id = TipIds.MeshGradientsCollection,
            title = R.string.help_tip_mesh_gradients_collection_title,
            subtitle = R.string.help_tip_mesh_gradients_collection_subtitle,
            icon = Icons.Outlined.MeshGradient,
            category = colorTools,
            deepLink = Screen.MeshGradients,
            pageTitle = R.string.help_tip_mesh_gradients_collection_page_title,
            description = R.string.help_tip_mesh_gradients_collection_description,
            steps = listOf(
                R.string.help_tip_mesh_gradients_collection_step_1,
                R.string.help_tip_mesh_gradients_collection_step_2,
                R.string.help_tip_mesh_gradients_collection_step_3
            )
        ),
        tip(
            id = TipIds.ColorAccessibility,
            title = R.string.help_tip_color_accessibility_title,
            subtitle = R.string.help_tip_color_accessibility_subtitle,
            icon = Icons.Outlined.Palette,
            category = colorTools,
            deepLink = Screen.Settings(targetSetting = Setting.ColorBlindScheme),
            pageTitle = R.string.help_tip_color_accessibility_page_title,
            description = R.string.help_tip_color_accessibility_description,
            steps = listOf(
                R.string.help_tip_color_accessibility_step_1,
                R.string.help_tip_color_accessibility_step_2,
                R.string.help_tip_color_accessibility_step_3
            )
        ),
        tip(
            id = TipIds.AlphaBackgroundColor,
            title = R.string.help_tip_alpha_background_color_title,
            subtitle = R.string.help_tip_alpha_background_color_subtitle,
            icon = Icons.Outlined.BackgroundColor,
            category = colorTools,
            deepLink = Screen.Settings(targetSetting = Setting.EnableBackgroundColorForAlphaFormats),
            pageTitle = R.string.help_tip_alpha_background_color_page_title,
            description = R.string.help_tip_alpha_background_color_description,
            steps = listOf(
                R.string.help_tip_alpha_background_color_step_1,
                R.string.help_tip_alpha_background_color_step_2,
                R.string.help_tip_alpha_background_color_step_3
            )
        ),
        pagesTip(
            id = TipIds.AiModels,
            title = R.string.help_tip_ai_models_title,
            subtitle = R.string.help_tip_ai_models_subtitle,
            icon = Icons.Outlined.Neurology,
            category = creativeTools,
            deepLink = Screen.AiTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_ai_models_page_title,
                    description = R.string.help_tip_ai_models_description,
                    steps = listOf(
                        R.string.help_tip_ai_models_step_1,
                        R.string.help_tip_ai_models_step_2,
                        R.string.help_tip_ai_models_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_ai_models_page_2_title,
                    description = R.string.help_tip_ai_models_page_2_description,
                    steps = listOf(
                        R.string.help_tip_ai_models_page_2_step_1,
                        R.string.help_tip_ai_models_page_2_step_2,
                        R.string.help_tip_ai_models_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.AiParameters,
            title = R.string.help_tip_ai_parameters_title,
            subtitle = R.string.help_tip_ai_parameters_subtitle,
            icon = Icons.Outlined.Neurology,
            category = creativeTools,
            deepLink = Screen.AiTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_ai_parameters_page_title,
                    description = R.string.help_tip_ai_parameters_description,
                    steps = listOf(
                        R.string.help_tip_ai_parameters_step_1,
                        R.string.help_tip_ai_parameters_step_2,
                        R.string.help_tip_ai_parameters_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_ai_parameters_page_2_title,
                    description = R.string.help_tip_ai_parameters_page_2_description,
                    steps = listOf(
                        R.string.help_tip_ai_parameters_page_2_step_1,
                        R.string.help_tip_ai_parameters_page_2_step_2,
                        R.string.help_tip_ai_parameters_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.AiStability,
            title = R.string.help_tip_ai_stability_title,
            subtitle = R.string.help_tip_ai_stability_subtitle,
            icon = Icons.Outlined.Neurology,
            category = troubleshooting,
            deepLink = Screen.AiTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_ai_stability_page_title,
                    description = R.string.help_tip_ai_stability_description,
                    steps = listOf(
                        R.string.help_tip_ai_stability_step_1,
                        R.string.help_tip_ai_stability_step_2,
                        R.string.help_tip_ai_stability_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_ai_stability_page_2_title,
                    description = R.string.help_tip_ai_stability_page_2_description,
                    steps = listOf(
                        R.string.help_tip_ai_stability_page_2_step_1,
                        R.string.help_tip_ai_stability_page_2_step_2,
                        R.string.help_tip_ai_stability_page_2_step_3
                    )
                )
            )
        ),
        tip(
            id = TipIds.ShaderStudio,
            title = R.string.help_tip_shader_studio_title,
            subtitle = R.string.help_tip_shader_studio_subtitle,
            icon = Icons.Rounded.EvShadow,
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
            id = TipIds.SvgAscii,
            title = R.string.help_tip_svg_ascii_title,
            subtitle = R.string.help_tip_svg_ascii_subtitle,
            icon = Icons.Outlined.VectorPolyline,
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
            id = TipIds.NoiseGeneration,
            title = R.string.help_tip_noise_generation_title,
            subtitle = R.string.help_tip_noise_generation_subtitle,
            icon = Icons.Outlined.NoiseAlt,
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
            id = TipIds.GeneratedAssetResolution,
            title = R.string.help_tip_generated_asset_resolution_title,
            subtitle = R.string.help_tip_generated_asset_resolution_subtitle,
            icon = Icons.Outlined.PhotoSizeSelectSmall,
            category = creativeTools,
            pageTitle = R.string.help_tip_generated_asset_resolution_page_title,
            description = R.string.help_tip_generated_asset_resolution_description,
            steps = listOf(
                R.string.help_tip_generated_asset_resolution_step_1,
                R.string.help_tip_generated_asset_resolution_step_2,
                R.string.help_tip_generated_asset_resolution_step_3
            )
        ),
        tip(
            id = TipIds.WallpaperExport,
            title = R.string.help_tip_wallpaper_export_title,
            subtitle = R.string.help_tip_wallpaper_export_subtitle,
            icon = Icons.Outlined.WallpaperAlt,
            category = creativeTools,
            deepLink = Screen.WallpapersExport,
            pageTitle = R.string.help_tip_wallpaper_export_page_title,
            description = R.string.help_tip_wallpaper_export_description,
            steps = listOf(
                R.string.help_tip_wallpaper_export_step_1,
                R.string.help_tip_wallpaper_export_step_2,
                R.string.help_tip_wallpaper_export_step_3
            )
        ),
        tip(
            id = TipIds.MarkupProjects,
            title = R.string.help_tip_markup_projects_title,
            subtitle = R.string.help_tip_markup_projects_subtitle,
            icon = Icons.Outlined.StackSticky,
            category = creativeTools,
            deepLink = Screen.MarkupLayers(),
            pageTitle = R.string.help_tip_markup_projects_page_title,
            description = R.string.help_tip_markup_projects_description,
            steps = listOf(
                R.string.help_tip_markup_projects_step_1,
                R.string.help_tip_markup_projects_step_2,
                R.string.help_tip_markup_projects_step_3
            )
        ),
        tip(
            id = TipIds.EncryptionWorkflow,
            title = R.string.help_tip_encryption_workflow_title,
            subtitle = R.string.help_tip_encryption_workflow_subtitle,
            icon = Icons.Outlined.Encrypted,
            category = textAndData,
            deepLink = Screen.Cipher(),
            pageTitle = R.string.help_tip_encryption_workflow_page_title,
            description = R.string.help_tip_encryption_workflow_description,
            steps = listOf(
                R.string.help_tip_encryption_workflow_step_1,
                R.string.help_tip_encryption_workflow_step_2,
                R.string.help_tip_encryption_workflow_step_3
            )
        ),
        tip(
            id = TipIds.LargeFiles,
            title = R.string.help_tip_large_files_title,
            subtitle = R.string.help_tip_large_files_subtitle,
            icon = Icons.Outlined.Help,
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
            id = TipIds.CacheAndPreviews,
            title = R.string.help_tip_cache_and_previews_title,
            subtitle = R.string.help_tip_cache_and_previews_subtitle,
            icon = Icons.Outlined.Help,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.GeneratePreviews),
            pageTitle = R.string.help_tip_cache_and_previews_page_title,
            description = R.string.help_tip_cache_and_previews_description,
            steps = listOf(
                R.string.help_tip_cache_and_previews_step_1,
                R.string.help_tip_cache_and_previews_step_2,
                R.string.help_tip_cache_and_previews_step_3
            )
        ),
        tip(
            id = TipIds.ScreenBehavior,
            title = R.string.help_tip_screen_behavior_title,
            subtitle = R.string.help_tip_screen_behavior_subtitle,
            icon = Icons.Outlined.Help,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.SecureMode),
            pageTitle = R.string.help_tip_screen_behavior_page_title,
            description = R.string.help_tip_screen_behavior_description,
            steps = listOf(
                R.string.help_tip_screen_behavior_step_1,
                R.string.help_tip_screen_behavior_step_2,
                R.string.help_tip_screen_behavior_step_3
            )
        ),
        tip(
            id = TipIds.CompactControls,
            title = R.string.help_tip_compact_controls_title,
            subtitle = R.string.help_tip_compact_controls_subtitle,
            icon = Icons.Outlined.Help,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.UseCompactSelectors),
            pageTitle = R.string.help_tip_compact_controls_page_title,
            description = R.string.help_tip_compact_controls_description,
            steps = listOf(
                R.string.help_tip_compact_controls_step_1,
                R.string.help_tip_compact_controls_step_2,
                R.string.help_tip_compact_controls_step_3
            )
        ),
        tip(
            id = TipIds.TransparentOutput,
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
            id = TipIds.SharingImport,
            title = R.string.help_tip_sharing_import_title,
            subtitle = R.string.help_tip_sharing_import_subtitle,
            icon = Icons.Outlined.Share,
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
            id = TipIds.ExitConfirmation,
            title = R.string.help_tip_exit_confirmation_title,
            subtitle = R.string.help_tip_exit_confirmation_subtitle,
            icon = Icons.Outlined.SaveConfirm,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.EnableToolExitConfirmation),
            pageTitle = R.string.help_tip_exit_confirmation_page_title,
            description = R.string.help_tip_exit_confirmation_description,
            steps = listOf(
                R.string.help_tip_exit_confirmation_step_1,
                R.string.help_tip_exit_confirmation_step_2,
                R.string.help_tip_exit_confirmation_step_3
            )
        ),
        tip(
            id = TipIds.LogsFeedback,
            title = R.string.help_tip_logs_feedback_title,
            subtitle = R.string.help_tip_logs_feedback_subtitle,
            icon = Icons.Outlined.Help,
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

    override fun getTipsForCategory(category: HelpCategory): List<HelpTip> = tips.filter {
        it.category == category
    }

    override fun getTip(id: String): HelpTip? = tips.firstOrNull {
        it.id == id
    }

    override fun getCategory(categoryKey: String): HelpCategory? = categories.firstOrNull {
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

    private fun pagesTip(
        id: String,
        @StringRes title: Int,
        @StringRes subtitle: Int,
        icon: ImageVector,
        category: HelpCategory,
        pages: List<HelpPage>,
        deepLink: Screen? = null
    ) = HelpTip(
        id = id,
        title = title,
        subtitle = subtitle,
        icon = icon,
        category = category,
        pages = pages,
        deepLink = deepLink
    )

    private fun page(
        @StringRes title: Int,
        @StringRes description: Int,
        steps: List<Int>
    ) = HelpPage(
        title = title,
        description = description,
        steps = steps
    )
}
