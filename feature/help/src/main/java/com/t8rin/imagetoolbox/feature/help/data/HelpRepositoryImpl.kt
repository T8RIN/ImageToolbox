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
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.helpDescription
import com.t8rin.imagetoolbox.core.resources.Book2
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AccessTime
import com.t8rin.imagetoolbox.core.resources.icons.Album
import com.t8rin.imagetoolbox.core.resources.icons.AlignVerticalCenter
import com.t8rin.imagetoolbox.core.resources.icons.Analytics
import com.t8rin.imagetoolbox.core.resources.icons.Animation
import com.t8rin.imagetoolbox.core.resources.icons.ApngBox
import com.t8rin.imagetoolbox.core.resources.icons.Apps
import com.t8rin.imagetoolbox.core.resources.icons.ArtTrack
import com.t8rin.imagetoolbox.core.resources.icons.Ascii
import com.t8rin.imagetoolbox.core.resources.icons.AutoDelete
import com.t8rin.imagetoolbox.core.resources.icons.AutoFixHigh
import com.t8rin.imagetoolbox.core.resources.icons.AvTimer
import com.t8rin.imagetoolbox.core.resources.icons.Base64
import com.t8rin.imagetoolbox.core.resources.icons.BatchPrediction
import com.t8rin.imagetoolbox.core.resources.icons.Beta
import com.t8rin.imagetoolbox.core.resources.icons.Bolt
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BorderStyle
import com.t8rin.imagetoolbox.core.resources.icons.Brick
import com.t8rin.imagetoolbox.core.resources.icons.Brightness4
import com.t8rin.imagetoolbox.core.resources.icons.BrightnessAuto
import com.t8rin.imagetoolbox.core.resources.icons.BrightnessHigh
import com.t8rin.imagetoolbox.core.resources.icons.BrushColor
import com.t8rin.imagetoolbox.core.resources.icons.BubbleDelete
import com.t8rin.imagetoolbox.core.resources.icons.BugReport
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.resources.icons.Casino
import com.t8rin.imagetoolbox.core.resources.icons.Category
import com.t8rin.imagetoolbox.core.resources.icons.Celebration
import com.t8rin.imagetoolbox.core.resources.icons.Collage
import com.t8rin.imagetoolbox.core.resources.icons.Compare
import com.t8rin.imagetoolbox.core.resources.icons.ContentPasteGo
import com.t8rin.imagetoolbox.core.resources.icons.Contrast
import com.t8rin.imagetoolbox.core.resources.icons.Cool
import com.t8rin.imagetoolbox.core.resources.icons.Counter
import com.t8rin.imagetoolbox.core.resources.icons.Crashlytics
import com.t8rin.imagetoolbox.core.resources.icons.CropSmall
import com.t8rin.imagetoolbox.core.resources.icons.Curve
import com.t8rin.imagetoolbox.core.resources.icons.DarkMode
import com.t8rin.imagetoolbox.core.resources.icons.DataSaverOff
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.DeleteSweep
import com.t8rin.imagetoolbox.core.resources.icons.Description
import com.t8rin.imagetoolbox.core.resources.icons.Difference
import com.t8rin.imagetoolbox.core.resources.icons.DocumentScanner
import com.t8rin.imagetoolbox.core.resources.icons.DoorBack
import com.t8rin.imagetoolbox.core.resources.icons.DownloadFile
import com.t8rin.imagetoolbox.core.resources.icons.DragHandle
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.resources.icons.Emergency
import com.t8rin.imagetoolbox.core.resources.icons.EmojiMultiple
import com.t8rin.imagetoolbox.core.resources.icons.Encrypted
import com.t8rin.imagetoolbox.core.resources.icons.Eraser
import com.t8rin.imagetoolbox.core.resources.icons.EvShadow
import com.t8rin.imagetoolbox.core.resources.icons.Exercise
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.resources.icons.ExifEdit
import com.t8rin.imagetoolbox.core.resources.icons.Eyedropper
import com.t8rin.imagetoolbox.core.resources.icons.FabCorner
import com.t8rin.imagetoolbox.core.resources.icons.File
import com.t8rin.imagetoolbox.core.resources.icons.FileRename
import com.t8rin.imagetoolbox.core.resources.icons.FileReplace
import com.t8rin.imagetoolbox.core.resources.icons.FilterBAndW
import com.t8rin.imagetoolbox.core.resources.icons.FilterFrames
import com.t8rin.imagetoolbox.core.resources.icons.FinanceMode
import com.t8rin.imagetoolbox.core.resources.icons.FindInPage
import com.t8rin.imagetoolbox.core.resources.icons.FitScreen
import com.t8rin.imagetoolbox.core.resources.icons.FolderOpen
import com.t8rin.imagetoolbox.core.resources.icons.FolderSpecial
import com.t8rin.imagetoolbox.core.resources.icons.FolderZip
import com.t8rin.imagetoolbox.core.resources.icons.FontFamily
import com.t8rin.imagetoolbox.core.resources.icons.FormatColorFill
import com.t8rin.imagetoolbox.core.resources.icons.FormatLineSpacing
import com.t8rin.imagetoolbox.core.resources.icons.FormatPaintVariant
import com.t8rin.imagetoolbox.core.resources.icons.FormatParagraph
import com.t8rin.imagetoolbox.core.resources.icons.FormatShapes
import com.t8rin.imagetoolbox.core.resources.icons.Forum
import com.t8rin.imagetoolbox.core.resources.icons.Fullscreen
import com.t8rin.imagetoolbox.core.resources.icons.FullscreenExit
import com.t8rin.imagetoolbox.core.resources.icons.Gamepad
import com.t8rin.imagetoolbox.core.resources.icons.GifBox
import com.t8rin.imagetoolbox.core.resources.icons.Github
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.resources.icons.GridOn
import com.t8rin.imagetoolbox.core.resources.icons.HandshakeAlt
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.resources.icons.Help
import com.t8rin.imagetoolbox.core.resources.icons.Highlight
import com.t8rin.imagetoolbox.core.resources.icons.History
import com.t8rin.imagetoolbox.core.resources.icons.HorizontalSplit
import com.t8rin.imagetoolbox.core.resources.icons.HouseholdSupplies
import com.t8rin.imagetoolbox.core.resources.icons.Hub
import com.t8rin.imagetoolbox.core.resources.icons.ImageCombine
import com.t8rin.imagetoolbox.core.resources.icons.ImageConvert
import com.t8rin.imagetoolbox.core.resources.icons.ImageDownload
import com.t8rin.imagetoolbox.core.resources.icons.ImageEdit
import com.t8rin.imagetoolbox.core.resources.icons.ImageOverlay
import com.t8rin.imagetoolbox.core.resources.icons.ImageResize
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.resources.icons.ImageWeight
import com.t8rin.imagetoolbox.core.resources.icons.ImagesMode
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.resources.icons.JoinInner
import com.t8rin.imagetoolbox.core.resources.icons.Jxl
import com.t8rin.imagetoolbox.core.resources.icons.KeyVariant
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.LabelPercent
import com.t8rin.imagetoolbox.core.resources.icons.Labs
import com.t8rin.imagetoolbox.core.resources.icons.Landscape
import com.t8rin.imagetoolbox.core.resources.icons.Language
import com.t8rin.imagetoolbox.core.resources.icons.LastPage
import com.t8rin.imagetoolbox.core.resources.icons.LayersSearchOutline
import com.t8rin.imagetoolbox.core.resources.icons.License
import com.t8rin.imagetoolbox.core.resources.icons.LightMode
import com.t8rin.imagetoolbox.core.resources.icons.Lightbulb
import com.t8rin.imagetoolbox.core.resources.icons.LineWeight
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.resources.icons.LocationSearching
import com.t8rin.imagetoolbox.core.resources.icons.Manga
import com.t8rin.imagetoolbox.core.resources.icons.Memory
import com.t8rin.imagetoolbox.core.resources.icons.MeshGradient
import com.t8rin.imagetoolbox.core.resources.icons.MobileLandscape
import com.t8rin.imagetoolbox.core.resources.icons.MobileRotateLock
import com.t8rin.imagetoolbox.core.resources.icons.MobileShare
import com.t8rin.imagetoolbox.core.resources.icons.MotionMode
import com.t8rin.imagetoolbox.core.resources.icons.MotionPlay
import com.t8rin.imagetoolbox.core.resources.icons.MultipleImageEdit
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.NextPlan
import com.t8rin.imagetoolbox.core.resources.icons.NoiseAlt
import com.t8rin.imagetoolbox.core.resources.icons.Numeric
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.resources.icons.PaletteBox
import com.t8rin.imagetoolbox.core.resources.icons.PaletteSwatch
import com.t8rin.imagetoolbox.core.resources.icons.Panorama
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.resources.icons.Percent
import com.t8rin.imagetoolbox.core.resources.icons.PhotoSizeSelectSmall
import com.t8rin.imagetoolbox.core.resources.icons.PictureInPictureCenter
import com.t8rin.imagetoolbox.core.resources.icons.Place
import com.t8rin.imagetoolbox.core.resources.icons.Png
import com.t8rin.imagetoolbox.core.resources.icons.Prefix
import com.t8rin.imagetoolbox.core.resources.icons.Preview
import com.t8rin.imagetoolbox.core.resources.icons.Print
import com.t8rin.imagetoolbox.core.resources.icons.PushPin
import com.t8rin.imagetoolbox.core.resources.icons.QrCode
import com.t8rin.imagetoolbox.core.resources.icons.QualityHigh
import com.t8rin.imagetoolbox.core.resources.icons.ReleaseAlert
import com.t8rin.imagetoolbox.core.resources.icons.RestartAlt
import com.t8rin.imagetoolbox.core.resources.icons.Rotate90Cw
import com.t8rin.imagetoolbox.core.resources.icons.RoundedCorner
import com.t8rin.imagetoolbox.core.resources.icons.Routine
import com.t8rin.imagetoolbox.core.resources.icons.RssFeed
import com.t8rin.imagetoolbox.core.resources.icons.SaveAs
import com.t8rin.imagetoolbox.core.resources.icons.SaveConfirm
import com.t8rin.imagetoolbox.core.resources.icons.ScaleUnbalanced
import com.t8rin.imagetoolbox.core.resources.icons.Scanner
import com.t8rin.imagetoolbox.core.resources.icons.ScissorsSmall
import com.t8rin.imagetoolbox.core.resources.icons.ScreenRotationAlt
import com.t8rin.imagetoolbox.core.resources.icons.Security
import com.t8rin.imagetoolbox.core.resources.icons.Settings
import com.t8rin.imagetoolbox.core.resources.icons.SettingsTimelapse
import com.t8rin.imagetoolbox.core.resources.icons.Shadow
import com.t8rin.imagetoolbox.core.resources.icons.Share
import com.t8rin.imagetoolbox.core.resources.icons.ShareOff
import com.t8rin.imagetoolbox.core.resources.icons.ShieldLock
import com.t8rin.imagetoolbox.core.resources.icons.ShineDiamond
import com.t8rin.imagetoolbox.core.resources.icons.SkipNext
import com.t8rin.imagetoolbox.core.resources.icons.Slider
import com.t8rin.imagetoolbox.core.resources.icons.Snowflake
import com.t8rin.imagetoolbox.core.resources.icons.Speed
import com.t8rin.imagetoolbox.core.resources.icons.SplitAlt
import com.t8rin.imagetoolbox.core.resources.icons.Stacks
import com.t8rin.imagetoolbox.core.resources.icons.Stylus
import com.t8rin.imagetoolbox.core.resources.icons.Suffix
import com.t8rin.imagetoolbox.core.resources.icons.SwapVerticalCircle
import com.t8rin.imagetoolbox.core.resources.icons.SwipeDown
import com.t8rin.imagetoolbox.core.resources.icons.SwipeVertical
import com.t8rin.imagetoolbox.core.resources.icons.Symbol
import com.t8rin.imagetoolbox.core.resources.icons.SyncArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.TagText
import com.t8rin.imagetoolbox.core.resources.icons.Telegram
import com.t8rin.imagetoolbox.core.resources.icons.TelevisionAmbientLight
import com.t8rin.imagetoolbox.core.resources.icons.Terminal
import com.t8rin.imagetoolbox.core.resources.icons.TextFields
import com.t8rin.imagetoolbox.core.resources.icons.TextSearch
import com.t8rin.imagetoolbox.core.resources.icons.TextSticky
import com.t8rin.imagetoolbox.core.resources.icons.Thermostat
import com.t8rin.imagetoolbox.core.resources.icons.TiltArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.Timelapse
import com.t8rin.imagetoolbox.core.resources.icons.Timer
import com.t8rin.imagetoolbox.core.resources.icons.Title
import com.t8rin.imagetoolbox.core.resources.icons.ToggleOff
import com.t8rin.imagetoolbox.core.resources.icons.ToggleOn
import com.t8rin.imagetoolbox.core.resources.icons.TouchApp
import com.t8rin.imagetoolbox.core.resources.icons.Translate
import com.t8rin.imagetoolbox.core.resources.icons.Unarchive
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.resources.icons.VectorPolyline
import com.t8rin.imagetoolbox.core.resources.icons.Verified
import com.t8rin.imagetoolbox.core.resources.icons.ViewComfy
import com.t8rin.imagetoolbox.core.resources.icons.ViewQuilt
import com.t8rin.imagetoolbox.core.resources.icons.Visibility
import com.t8rin.imagetoolbox.core.resources.icons.VolunteerActivism
import com.t8rin.imagetoolbox.core.resources.icons.WallArt
import com.t8rin.imagetoolbox.core.resources.icons.WallpaperAlt
import com.t8rin.imagetoolbox.core.resources.icons.WaterDrop
import com.t8rin.imagetoolbox.core.resources.icons.Watermark
import com.t8rin.imagetoolbox.core.resources.icons.WatermarkAlt
import com.t8rin.imagetoolbox.core.resources.icons.WebpBox
import com.t8rin.imagetoolbox.core.resources.icons.WrapText
import com.t8rin.imagetoolbox.core.resources.icons.ZoomIn
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.help.domain.HelpRepository
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpCategory
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpPage
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpTip
import javax.inject.Inject

internal class HelpRepositoryImpl @Inject constructor() : HelpRepository {

    private object TipIds {
        const val ToneCurves = "tone-curves"
        const val StitchingModes = "stitching-modes"
        const val StackingLayers = "stacking-layers"
        const val MultiFrameFusion = "multi-frame-fusion"
        const val Photomosaic = "photomosaic"
        const val CompressionLab = "compression-lab"
        const val ScreenshotFraming = "screenshot-framing"
        const val CodePreview = "code-preview"
        const val FractalGeneration = "fractal-generation"
        const val ColorTools = "color-tools"
        const val PalettePdf = "palette-pdf"
        const val GifWorkflow = "gif-workflow"
        const val ApngWorkflow = "apng-workflow"
        const val WebpWorkflow = "webp-workflow"
        const val JxlWorkflow = "jxl-workflow"
        const val AsciiWorkflow = "ascii-workflow"
        const val DrawingWorkflow = "drawing-workflow"
        const val ExifRemoval = "exif-removal"
        const val GalleryWorkflow = "gallery-workflow"
        const val ArchiveWorkflow = "archive-workflow"
        const val Base64Workflow = "base64-workflow"
        const val PdfComparison = "pdf-comparison"
        const val PdfPageResizing = "pdf-page-resizing"
        const val PdfSanitizing = "pdf-sanitizing"
        const val PdfContactSheets = "pdf-contact-sheets"
        const val PdfDarkReading = "pdf-dark-reading"
        const val PdfImageExtraction = "pdf-image-extraction"
        const val SettingsNavigation = "settings-navigation"
        const val SettingsPanels = "settings-panels"
        const val ThemeCustomization = "theme-customization"
        const val InterfaceTypography = "interface-typography"
        const val InterfaceMotion = "interface-motion"
        const val SettingsGroupVisibility = "settings-group-visibility"
        const val ChooseTool = "choose-tool"
        const val ImportSaveShare = "import-save-share"
        const val BatchWorkflow = "batch-workflow"
        const val SingleEdit = "single-edit"
        const val PresetsSettings = "presets-settings"
        const val DefaultValues = "default-values"
        const val PickerLauncher = "picker-launcher"
        const val ImageSource = "image-source"
        const val GallerySearch = "gallery-search"
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
        const val GmicFilters = "gmic-filters"
        const val TextureGeneration = "texture-generation"
        const val BatchRename = "batch-rename"
        const val DuplicateFinder = "duplicate-finder"
        const val RawDevelopment = "raw-development"
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

    private val filters = HelpCategory(
        key = "filters",
        title = R.string.filters,
        subtitle = R.string.help_category_filters_subtitle,
        icon = Icons.Outlined.AutoFixHigh
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

    private val aiTools = HelpCategory(
        key = "ai-tools",
        title = R.string.help_category_ai_tools,
        subtitle = R.string.help_category_ai_tools_sub,
        icon = Icons.Outlined.Neurology
    )

    private val creativeTools = HelpCategory(
        key = "creative-tools",
        title = R.string.help_category_creative_tools,
        subtitle = R.string.help_category_creative_tools_sub,
        icon = Icons.Outlined.ImageConvert
    )

    private val customization = HelpCategory(
        key = "customization",
        title = R.string.help_category_customization,
        subtitle = R.string.help_category_customization_sub,
        icon = Icons.Outlined.Settings
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
        filters,
        filesAndMetadata,
        pdfAndDocuments,
        textAndData,
        colorTools,
        aiTools,
        creativeTools,
        customization,
        troubleshooting
    )

    override val tips: List<HelpTip> = listOf(
        pagesTip(
            id = "depth-effects",
            title = R.string.help_tip_depth_effects_title,
            subtitle = R.string.help_tip_depth_effects_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_depth_effects_title,
                    description = R.string.help_tip_depth_effects_description,
                    steps = listOf(
                        R.string.help_tip_depth_effects_step_1,
                        R.string.help_tip_depth_effects_step_2,
                        R.string.help_tip_depth_effects_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_depth_effects_page_2_title,
                    description = R.string.help_tip_depth_effects_page_2_description,
                    steps = listOf(
                        R.string.help_tip_depth_effects_page_2_step_1,
                        R.string.help_tip_depth_effects_page_2_step_2,
                        R.string.help_tip_depth_effects_page_2_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_depth_effects_page_3_title,
                    description = R.string.help_tip_depth_effects_page_3_description,
                    steps = listOf(
                        R.string.help_tip_depth_effects_page_3_step_1,
                        R.string.help_tip_depth_effects_page_3_step_2,
                        R.string.help_tip_depth_effects_page_3_step_3
                    )
                )
            )
        ),
        tip(
            id = "pdf-compression",
            title = R.string.compress_pdf,
            subtitle = R.string.help_tip_pdf_compression_subtitle,
            icon = Icons.Outlined.Bolt,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Compress(),
            pageTitle = R.string.compress_pdf,
            description = R.string.help_tip_pdf_compression_description,
            steps = listOf(
                R.string.help_tip_pdf_compression_step_1,
                R.string.help_tip_pdf_compression_step_2,
                R.string.help_tip_pdf_compression_step_3
            )
        ),
        tip(
            id = "pdf-annotation-cleanup",
            title = R.string.remove_annotations,
            subtitle = R.string.help_tip_pdf_annotation_cleanup_subtitle,
            icon = Icons.Outlined.BubbleDelete,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.RemoveAnnotations(),
            pageTitle = R.string.remove_annotations,
            description = R.string.help_tip_pdf_annotation_cleanup_description,
            steps = listOf(
                R.string.help_tip_pdf_annotation_cleanup_step_1,
                R.string.help_tip_pdf_annotation_cleanup_step_2,
                R.string.help_tip_pdf_annotation_cleanup_step_3
            )
        ),
        tip(
            id = "metadata-editing",
            title = R.string.edit_exif,
            subtitle = R.string.help_tip_metadata_editing_subtitle,
            icon = Icons.Outlined.ExifEdit,
            category = filesAndMetadata,
            deepLink = Screen.EditExif(),
            pageTitle = R.string.edit_exif,
            description = R.string.help_tip_metadata_editing_description,
            steps = listOf(
                R.string.help_tip_metadata_editing_step_1,
                R.string.help_tip_metadata_editing_step_2,
                R.string.help_tip_metadata_editing_step_3
            )
        ),
        tip(
            id = "cache-cleanup",
            title = R.string.cache_auto_clear_limit,
            subtitle = R.string.help_tip_cache_cleanup_subtitle,
            icon = Icons.Outlined.Speed,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.CacheAutoClearLimit),
            pageTitle = R.string.cache_auto_clear_limit,
            description = R.string.help_tip_cache_cleanup_description,
            steps = listOf(
                R.string.help_tip_cache_cleanup_step_1,
                R.string.help_tip_cache_cleanup_step_2,
                R.string.help_tip_cache_cleanup_step_3
            )
        ),
        tip(
            id = TipIds.SettingsNavigation,
            title = R.string.help_tip_settings_navigation_title,
            subtitle = R.string.help_tip_settings_navigation_subtitle,
            icon = Icons.Outlined.Category,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.UseCategorizedSettings),
            pageTitle = R.string.help_tip_settings_navigation_page_title,
            description = R.string.help_tip_settings_navigation_description,
            steps = listOf(
                R.string.help_tip_settings_navigation_step_1,
                R.string.help_tip_settings_navigation_step_2,
                R.string.help_tip_settings_navigation_step_3
            )
        ),
        tip(
            id = TipIds.SettingsPanels,
            title = R.string.help_tip_settings_panels_title,
            subtitle = R.string.help_tip_settings_panels_subtitle,
            icon = Icons.Rounded.Fullscreen,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.UseFullscreenSettings),
            pageTitle = R.string.help_tip_settings_panels_page_title,
            description = R.string.help_tip_settings_panels_description,
            steps = listOf(
                R.string.help_tip_settings_panels_step_1,
                R.string.help_tip_settings_panels_step_2,
                R.string.help_tip_settings_panels_step_3
            )
        ),
        tip(
            id = TipIds.ThemeCustomization,
            title = R.string.help_tip_theme_customization_title,
            subtitle = R.string.help_tip_theme_customization_subtitle,
            icon = Icons.Outlined.PaletteBox,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ColorScheme),
            pageTitle = R.string.help_tip_theme_customization_page_title,
            description = R.string.help_tip_theme_customization_description,
            steps = listOf(
                R.string.help_tip_theme_customization_step_1,
                R.string.help_tip_theme_customization_step_2,
                R.string.help_tip_theme_customization_step_3
            )
        ),
        tip(
            id = TipIds.InterfaceTypography,
            title = R.string.help_tip_interface_typography_title,
            subtitle = R.string.help_tip_interface_typography_subtitle,
            icon = Icons.Rounded.FontFamily,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ChangeFont),
            pageTitle = R.string.help_tip_interface_typography_page_title,
            description = R.string.help_tip_interface_typography_description,
            steps = listOf(
                R.string.help_tip_interface_typography_step_1,
                R.string.help_tip_interface_typography_step_2,
                R.string.help_tip_interface_typography_step_3
            )
        ),
        tip(
            id = TipIds.InterfaceMotion,
            title = R.string.help_tip_interface_motion_title,
            subtitle = R.string.help_tip_interface_motion_subtitle,
            icon = Icons.Outlined.MotionPlay,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.MotionDurationScale),
            pageTitle = R.string.help_tip_interface_motion_page_title,
            description = R.string.help_tip_interface_motion_description,
            steps = listOf(
                R.string.help_tip_interface_motion_step_1,
                R.string.help_tip_interface_motion_step_2,
                R.string.help_tip_interface_motion_step_3
            )
        ),
        tip(
            id = TipIds.SettingsGroupVisibility,
            title = R.string.help_tip_settings_group_visibility_title,
            subtitle = R.string.help_tip_settings_group_visibility_subtitle,
            icon = Icons.Rounded.KeyboardArrowDown,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.AllowCollapsingSettingsGroups),
            pageTitle = R.string.help_tip_settings_group_visibility_page_title,
            description = R.string.help_tip_settings_group_visibility_description,
            steps = listOf(
                R.string.help_tip_settings_group_visibility_step_1,
                R.string.help_tip_settings_group_visibility_step_2,
                R.string.help_tip_settings_group_visibility_step_3
            )
        ),
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
            icon = Icons.Outlined.Png,
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
            icon = Icons.Outlined.Apps,
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
            icon = Icons.Rounded.ImageSearch,
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
        pagesTip(
            id = TipIds.GallerySearch,
            title = R.string.help_tip_gallery_search_title,
            subtitle = R.string.help_tip_gallery_search_subtitle,
            icon = Icons.Outlined.ImageSearch,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.SemanticSearch),
            pages = listOf(
                page(
                    title = R.string.help_tip_gallery_search_page_title,
                    description = R.string.help_tip_gallery_search_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_step_1,
                        R.string.help_tip_gallery_search_step_2,
                        R.string.help_tip_gallery_search_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gallery_search_page_2_title,
                    description = R.string.help_tip_gallery_search_page_2_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_page_2_step_1,
                        R.string.help_tip_gallery_search_page_2_step_2,
                        R.string.help_tip_gallery_search_page_2_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gallery_search_image_title,
                    description = R.string.help_tip_gallery_search_image_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_image_step_1,
                        R.string.help_tip_gallery_search_image_step_2,
                        R.string.help_tip_gallery_search_image_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gallery_search_custom_title,
                    description = R.string.help_tip_gallery_search_custom_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_custom_step_1,
                        R.string.help_tip_gallery_search_custom_step_2,
                        R.string.help_tip_gallery_search_custom_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gallery_search_people_title,
                    description = R.string.help_tip_gallery_search_people_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_people_step_1,
                        R.string.help_tip_gallery_search_people_step_2,
                        R.string.help_tip_gallery_search_people_step_3,
                        R.string.help_tip_gallery_search_people_step_4,
                        R.string.help_tip_gallery_search_people_step_5
                    )
                ),
                page(
                    title = R.string.help_tip_gallery_search_accuracy_title,
                    description = R.string.help_tip_gallery_search_accuracy_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_accuracy_step_1,
                        R.string.help_tip_gallery_search_accuracy_step_2,
                        R.string.help_tip_gallery_search_accuracy_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gallery_search_page_3_title,
                    description = R.string.help_tip_gallery_search_page_3_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_page_3_step_1,
                        R.string.help_tip_gallery_search_page_3_step_2,
                        R.string.help_tip_gallery_search_page_3_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gallery_search_page_4_title,
                    description = R.string.help_tip_gallery_search_page_4_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_page_4_step_1,
                        R.string.help_tip_gallery_search_page_4_step_2,
                        R.string.help_tip_gallery_search_page_4_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gallery_search_page_5_title,
                    description = R.string.help_tip_gallery_search_page_5_description,
                    steps = listOf(
                        R.string.help_tip_gallery_search_page_5_step_1,
                        R.string.help_tip_gallery_search_page_5_step_2,
                        R.string.help_tip_gallery_search_page_5_step_3
                    )
                )
            )
        ),
        tip(
            id = TipIds.SkipFilePicking,
            title = R.string.help_tip_skip_file_picking_title,
            subtitle = R.string.help_tip_skip_file_picking_subtitle,
            icon = Icons.Outlined.SkipNext,
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
            icon = Icons.Rounded.EditAlt,
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
            icon = Icons.Outlined.ShareOff,
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
            icon = Icons.Rounded.FormatLineSpacing,
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
            icon = Icons.Outlined.UploadFile,
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
            icon = Icons.Rounded.WrapText,
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
        pagesTip(
            id = TipIds.ResizeConvert,
            title = R.string.help_tip_resize_convert_title,
            subtitle = R.string.help_tip_resize_convert_subtitle,
            icon = Icons.Outlined.MultipleImageEdit,
            category = imageEditing,
            deepLink = Screen.ResizeAndConvert(),
            pages = listOf(
                page(
                    title = R.string.help_tip_resize_convert_page_title,
                    description = R.string.help_tip_resize_convert_description,
                    steps = listOf(
                        R.string.help_tip_resize_convert_step_1,
                        R.string.help_tip_resize_convert_step_2,
                        R.string.help_tip_resize_convert_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_resize_convert_page_2_title,
                    description = R.string.help_tip_resize_convert_page_2_description,
                    steps = listOf(
                        R.string.help_tip_resize_convert_page_2_step_1,
                        R.string.help_tip_resize_convert_page_2_step_2,
                        R.string.help_tip_resize_convert_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.ResizeByFileSize,
            title = R.string.help_tip_resize_by_file_size_title,
            subtitle = R.string.help_tip_resize_by_file_size_subtitle,
            icon = Icons.Outlined.ImageWeight,
            category = imageEditing,
            deepLink = Screen.WeightResize(),
            pages = listOf(
                page(
                    title = R.string.help_tip_resize_by_file_size_page_title,
                    description = R.string.help_tip_resize_by_file_size_description,
                    steps = listOf(
                        R.string.help_tip_resize_by_file_size_step_1,
                        R.string.help_tip_resize_by_file_size_step_2,
                        R.string.help_tip_resize_by_file_size_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_resize_by_file_size_page_2_title,
                    description = R.string.help_tip_resize_by_file_size_page_2_description,
                    steps = listOf(
                        R.string.help_tip_resize_by_file_size_page_2_step_1,
                        R.string.help_tip_resize_by_file_size_page_2_step_2,
                        R.string.help_tip_resize_by_file_size_page_2_step_3
                    )
                )
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
            icon = Icons.Outlined.PhotoSizeSelectSmall,
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
            icon = Icons.Outlined.FitScreen,
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
            icon = Icons.Outlined.ImageResize,
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
            icon = Icons.Outlined.MultipleImageEdit,
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
            icon = Icons.Outlined.MultipleImageEdit,
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
        pagesTip(
            id = TipIds.CropStraighten,
            title = R.string.help_tip_crop_straighten_title,
            subtitle = R.string.help_tip_crop_straighten_subtitle,
            icon = Icons.Rounded.CropSmall,
            category = imageEditing,
            deepLink = Screen.Crop(),
            pages = listOf(
                page(
                    title = R.string.help_tip_crop_straighten_page_title,
                    description = R.string.help_tip_crop_straighten_description,
                    steps = listOf(
                        R.string.help_tip_crop_straighten_step_1,
                        R.string.help_tip_crop_straighten_step_2,
                        R.string.help_tip_crop_straighten_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_crop_straighten_page_2_title,
                    description = R.string.help_tip_crop_straighten_page_2_description,
                    steps = listOf(
                        R.string.help_tip_crop_straighten_page_2_step_1,
                        R.string.help_tip_crop_straighten_page_2_step_2,
                        R.string.help_tip_crop_straighten_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.Filters,
            title = R.string.help_tip_filters_title,
            subtitle = R.string.help_tip_filters_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = imageEditing,
            deepLink = Screen.Filter(),
            pages = listOf(
                page(
                    title = R.string.help_tip_filters_page_title,
                    description = R.string.help_tip_filters_description,
                    steps = listOf(
                        R.string.help_tip_filters_step_1,
                        R.string.help_tip_filters_step_2,
                        R.string.help_tip_filters_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_filters_page_2_title,
                    description = R.string.help_tip_filters_page_2_description,
                    steps = listOf(
                        R.string.help_tip_filters_page_2_step_1,
                        R.string.help_tip_filters_page_2_step_2,
                        R.string.help_tip_filters_page_2_step_3
                    )
                )
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
        pagesTip(
            id = TipIds.GmicFilters,
            title = R.string.help_tip_gmic_filters_title,
            subtitle = R.string.help_tip_gmic_filters_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = imageEditing,
            deepLink = Screen.Filter(),
            pages = listOf(
                page(
                    title = R.string.help_tip_gmic_filters_page_title,
                    description = R.string.help_tip_gmic_filters_description,
                    steps = listOf(
                        R.string.help_tip_gmic_filters_step_1,
                        R.string.help_tip_gmic_filters_step_2,
                        R.string.help_tip_gmic_filters_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gmic_filters_page_2_title,
                    description = R.string.help_tip_gmic_filters_page_2_description,
                    steps = listOf(
                        R.string.help_tip_gmic_filters_page_2_step_1,
                        R.string.help_tip_gmic_filters_page_2_step_2,
                        R.string.help_tip_gmic_filters_page_2_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_gmic_filters_page_3_title,
                    description = R.string.help_tip_gmic_filters_page_3_description,
                    steps = listOf(
                        R.string.help_tip_gmic_filters_page_3_step_1,
                        R.string.help_tip_gmic_filters_page_3_step_2,
                        R.string.help_tip_gmic_filters_page_3_step_3
                    )
                )
            )
        ),
        tip(
            id = TipIds.TextureGeneration,
            title = R.string.help_tip_texture_generation_title,
            subtitle = R.string.help_tip_texture_generation_subtitle,
            icon = Icons.Outlined.Brick,
            category = creativeTools,
            deepLink = Screen.TextureGeneration,
            pageTitle = R.string.help_tip_texture_generation_page_title,
            description = R.string.help_tip_texture_generation_description,
            steps = listOf(
                R.string.help_tip_texture_generation_step_1,
                R.string.help_tip_texture_generation_step_2,
                R.string.help_tip_texture_generation_step_3
            )
        ),
        tip(
            id = TipIds.BatchRename,
            title = R.string.help_tip_batch_rename_title,
            subtitle = R.string.help_tip_batch_rename_subtitle,
            icon = Icons.Outlined.FileRename,
            category = filesAndMetadata,
            deepLink = Screen.BatchRename(),
            pageTitle = R.string.help_tip_batch_rename_page_title,
            description = R.string.help_tip_batch_rename_description,
            steps = listOf(
                R.string.help_tip_batch_rename_step_1,
                R.string.help_tip_batch_rename_step_2,
                R.string.help_tip_batch_rename_step_3
            )
        ),
        tip(
            id = TipIds.DuplicateFinder,
            title = R.string.help_tip_duplicate_finder_title,
            subtitle = R.string.help_tip_duplicate_finder_subtitle,
            icon = Icons.Outlined.ImageSearch,
            category = filesAndMetadata,
            deepLink = Screen.DuplicateFinder(),
            pageTitle = R.string.help_tip_duplicate_finder_page_title,
            description = R.string.help_tip_duplicate_finder_description,
            steps = listOf(
                R.string.help_tip_duplicate_finder_step_1,
                R.string.help_tip_duplicate_finder_step_2,
                R.string.help_tip_duplicate_finder_step_3
            )
        ),
        pagesTip(
            id = TipIds.RawDevelopment,
            title = R.string.help_tip_raw_development_title,
            subtitle = R.string.help_tip_raw_development_subtitle,
            icon = Icons.Outlined.Thermostat,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawWhiteBalance),
            pages = listOf(
                page(
                    title = R.string.help_tip_raw_development_page_title,
                    description = R.string.help_tip_raw_development_description,
                    steps = listOf(
                        R.string.help_tip_raw_development_step_1,
                        R.string.help_tip_raw_development_step_2,
                        R.string.help_tip_raw_development_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_raw_development_page_2_title,
                    description = R.string.help_tip_raw_development_page_2_description,
                    steps = listOf(
                        R.string.help_tip_raw_development_page_2_step_1,
                        R.string.help_tip_raw_development_page_2_step_2,
                        R.string.help_tip_raw_development_page_2_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_raw_development_page_3_title,
                    description = R.string.help_tip_raw_development_page_3_description,
                    steps = listOf(
                        R.string.help_tip_raw_development_page_3_step_1,
                        R.string.help_tip_raw_development_page_3_step_2,
                        R.string.help_tip_raw_development_page_3_step_3
                    )
                )
            )
        ),
        tip(
            id = TipIds.PreviewBeforeEditing,
            title = R.string.help_tip_preview_before_editing_title,
            subtitle = R.string.help_tip_preview_before_editing_subtitle,
            icon = Icons.Outlined.Landscape,
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
        pagesTip(
            id = TipIds.BackgroundRemover,
            title = R.string.help_tip_background_remover_title,
            subtitle = R.string.help_tip_background_remover_subtitle,
            icon = Icons.Rounded.Eraser,
            category = imageEditing,
            deepLink = Screen.EraseBackground(),
            pages = listOf(
                page(
                    title = R.string.help_tip_background_remover_page_title,
                    description = R.string.help_tip_background_remover_description,
                    steps = listOf(
                        R.string.help_tip_background_remover_step_1,
                        R.string.help_tip_background_remover_step_2,
                        R.string.help_tip_background_remover_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_background_remover_page_2_title,
                    description = R.string.help_tip_background_remover_page_2_description,
                    steps = listOf(
                        R.string.help_tip_background_remover_page_2_step_1,
                        R.string.help_tip_background_remover_page_2_step_2,
                        R.string.help_tip_background_remover_page_2_step_3
                    )
                )
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
            icon = Icons.Outlined.Watermark,
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
            icon = Icons.Rounded.LineWeight,
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
            icon = Icons.Outlined.ScissorsSmall,
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
            icon = Icons.Outlined.DeleteSweep,
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
        pagesTip(
            id = TipIds.Filenames,
            title = R.string.help_tip_filenames_title,
            subtitle = R.string.help_tip_filenames_subtitle,
            icon = Icons.Outlined.File,
            category = filesAndMetadata,
            pages = listOf(
                page(
                    title = R.string.help_tip_filenames_page_title,
                    description = R.string.help_tip_filenames_description,
                    steps = listOf(
                        R.string.help_tip_filenames_step_1,
                        R.string.help_tip_filenames_step_2,
                        R.string.help_tip_filenames_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_filenames_page_2_title,
                    description = R.string.help_tip_filenames_page_2_description,
                    steps = listOf(
                        R.string.help_tip_filenames_page_2_step_1,
                        R.string.help_tip_filenames_page_2_step_2,
                        R.string.help_tip_filenames_page_2_step_3
                    )
                )
            )
        ),
        tip(
            id = TipIds.FilenameCollisionSafety,
            title = R.string.help_tip_filename_collision_safety_title,
            subtitle = R.string.help_tip_filename_collision_safety_subtitle,
            icon = Icons.Outlined.Description,
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
            icon = Icons.Outlined.FolderSpecial,
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
            icon = Icons.Outlined.FolderOpen,
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
            icon = Icons.Rounded.LocationSearching,
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
            icon = Icons.Outlined.NextPlan,
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
            icon = Icons.Outlined.MultipleImageEdit,
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
            icon = Icons.Outlined.WebpBox,
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
        pagesTip(
            id = TipIds.ImagesToPdf,
            title = R.string.help_tip_images_to_pdf_title,
            subtitle = R.string.help_tip_images_to_pdf_subtitle,
            icon = Icons.Outlined.Scanner,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.ImagesToPdf(),
            pages = listOf(
                page(
                    title = R.string.help_tip_images_to_pdf_page_title,
                    description = R.string.help_tip_images_to_pdf_description,
                    steps = listOf(
                        R.string.help_tip_images_to_pdf_step_1,
                        R.string.help_tip_images_to_pdf_step_2,
                        R.string.help_tip_images_to_pdf_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_images_to_pdf_page_2_title,
                    description = R.string.help_tip_images_to_pdf_page_2_description,
                    steps = listOf(
                        R.string.help_tip_images_to_pdf_page_2_step_1,
                        R.string.help_tip_images_to_pdf_page_2_step_2,
                        R.string.help_tip_images_to_pdf_page_2_step_3
                    )
                )
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
        pagesTip(
            id = TipIds.PdfProtection,
            title = R.string.help_tip_pdf_protection_title,
            subtitle = R.string.help_tip_pdf_protection_subtitle,
            icon = Icons.Outlined.ShieldLock,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pages = listOf(
                page(
                    title = R.string.help_tip_pdf_protection_page_title,
                    description = R.string.help_tip_pdf_protection_description,
                    steps = listOf(
                        R.string.help_tip_pdf_protection_step_1,
                        R.string.help_tip_pdf_protection_step_2,
                        R.string.help_tip_pdf_protection_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_pdf_protection_page_2_title,
                    description = R.string.help_tip_pdf_protection_page_2_description,
                    steps = listOf(
                        R.string.help_tip_pdf_protection_page_2_step_1,
                        R.string.help_tip_pdf_protection_page_2_step_2,
                        R.string.help_tip_pdf_protection_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.PdfPageOrganization,
            title = R.string.help_tip_pdf_page_organization_title,
            subtitle = R.string.help_tip_pdf_page_organization_subtitle,
            icon = Icons.Outlined.SwapVerticalCircle,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools,
            pages = listOf(
                page(
                    title = R.string.help_tip_pdf_page_organization_page_title,
                    description = R.string.help_tip_pdf_page_organization_description,
                    steps = listOf(
                        R.string.help_tip_pdf_page_organization_step_1,
                        R.string.help_tip_pdf_page_organization_step_2,
                        R.string.help_tip_pdf_page_organization_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_pdf_page_organization_page_2_title,
                    description = R.string.help_tip_pdf_page_organization_page_2_description,
                    steps = listOf(
                        R.string.help_tip_pdf_page_organization_page_2_step_1,
                        R.string.help_tip_pdf_page_organization_page_2_step_2,
                        R.string.help_tip_pdf_page_organization_page_2_step_3
                    )
                )
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
        pagesTip(
            id = TipIds.Ocr,
            title = R.string.help_tip_ocr_title,
            subtitle = R.string.help_tip_ocr_subtitle,
            icon = Icons.Outlined.TextSearch,
            category = textAndData,
            deepLink = Screen.RecognizeText(),
            pages = listOf(
                page(
                    title = R.string.help_tip_ocr_page_title,
                    description = R.string.help_tip_ocr_description,
                    steps = listOf(
                        R.string.help_tip_ocr_step_1,
                        R.string.help_tip_ocr_step_2,
                        R.string.help_tip_ocr_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_ocr_page_2_title,
                    description = R.string.help_tip_ocr_page_2_description,
                    steps = listOf(
                        R.string.help_tip_ocr_page_2_step_1,
                        R.string.help_tip_ocr_page_2_step_2,
                        R.string.help_tip_ocr_page_2_step_3
                    )
                )
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
            icon = Screen.RecognizeText.Type.WriteToSearchablePdf().icon,
            category = textAndData,
            deepLink = Screen.RecognizeText(type = Screen.RecognizeText.Type.WriteToSearchablePdf()),
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
            deepLink = Screen.ArchiveTools(),
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
            icon = Icons.Rounded.HashTag,
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
            icon = Icons.Rounded.ContentPasteGo,
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
            icon = Icons.Outlined.PushPin,
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
            icon = Icons.Outlined.Visibility,
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
            icon = Icons.Outlined.Gradient,
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
            category = aiTools,
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
            category = aiTools,
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
                ),
                page(
                    title = R.string.help_tip_ai_parameters_page_3_title,
                    description = R.string.help_tip_ai_parameters_page_3_description,
                    steps = listOf(
                        R.string.help_tip_ai_parameters_page_3_step_1,
                        R.string.help_tip_ai_parameters_page_3_step_2,
                        R.string.help_tip_ai_parameters_page_3_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = "assistant-workflow",
            title = R.string.help_tip_assistant_workflow_title,
            subtitle = R.string.help_tip_assistant_workflow_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.Assistant(),
            pages = listOf(
                page(
                    title = R.string.help_tip_assistant_workflow_title,
                    description = R.string.help_tip_assistant_workflow_description,
                    steps = listOf(
                        R.string.help_tip_assistant_workflow_step_1,
                        R.string.help_tip_assistant_workflow_step_2,
                        R.string.help_tip_assistant_workflow_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_assistant_workflow_page_2_title,
                    description = R.string.help_tip_assistant_workflow_page_2_description,
                    steps = listOf(
                        R.string.help_tip_assistant_workflow_page_2_step_1,
                        R.string.help_tip_assistant_workflow_page_2_step_2,
                        R.string.help_tip_assistant_workflow_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = "assistant-models",
            title = R.string.help_tip_assistant_models_title,
            subtitle = R.string.help_tip_assistant_models_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.Assistant(),
            pages = listOf(
                page(
                    title = R.string.help_tip_assistant_models_title,
                    description = R.string.help_tip_assistant_models_description,
                    steps = listOf(
                        R.string.help_tip_assistant_models_step_1,
                        R.string.help_tip_assistant_models_step_2,
                        R.string.help_tip_assistant_models_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_assistant_models_page_2_title,
                    description = R.string.help_tip_assistant_models_page_2_description,
                    steps = listOf(
                        R.string.help_tip_assistant_models_page_2_step_1,
                        R.string.help_tip_assistant_models_page_2_step_2,
                        R.string.help_tip_assistant_models_page_2_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_assistant_models_page_3_title,
                    description = R.string.help_tip_assistant_models_page_3_description,
                    steps = listOf(
                        R.string.help_tip_assistant_models_page_3_step_1,
                        R.string.help_tip_assistant_models_page_3_step_2,
                        R.string.help_tip_assistant_models_page_3_step_3
                    )
                )
            )
        ),
        tip(
            id = "assistant-images",
            title = R.string.help_tip_assistant_images_title,
            subtitle = R.string.help_tip_assistant_images_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.Assistant(),
            pageTitle = R.string.help_tip_assistant_images_title,
            description = R.string.help_tip_assistant_images_description,
            steps = listOf(
                R.string.help_tip_assistant_images_step_1,
                R.string.help_tip_assistant_images_step_2,
                R.string.help_tip_assistant_images_step_3
            )
        ),
        tip(
            id = "assistant-downloads",
            title = R.string.help_tip_assistant_downloads_title,
            subtitle = R.string.help_tip_assistant_downloads_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.Assistant(),
            pageTitle = R.string.help_tip_assistant_downloads_title,
            description = R.string.help_tip_assistant_downloads_description,
            steps = listOf(
                R.string.help_tip_assistant_downloads_step_1,
                R.string.help_tip_assistant_downloads_step_2,
                R.string.help_tip_assistant_downloads_step_3
            )
        ),
        tip(
            id = "ai-upscaling",
            title = R.string.help_tip_ai_upscaling_title,
            subtitle = R.string.help_tip_ai_upscaling_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pageTitle = R.string.help_tip_ai_upscaling_title,
            description = R.string.help_tip_ai_upscaling_description,
            steps = listOf(
                R.string.help_tip_ai_upscaling_step_1,
                R.string.help_tip_ai_upscaling_step_2,
                R.string.help_tip_ai_upscaling_step_3
            )
        ),
        tip(
            id = "ai-cleanup",
            title = R.string.help_tip_ai_cleanup_title,
            subtitle = R.string.help_tip_ai_cleanup_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pageTitle = R.string.help_tip_ai_cleanup_title,
            description = R.string.help_tip_ai_cleanup_description,
            steps = listOf(
                R.string.help_tip_ai_cleanup_step_1,
                R.string.help_tip_ai_cleanup_step_2,
                R.string.help_tip_ai_cleanup_step_3
            )
        ),
        tip(
            id = "ai-colorization",
            title = R.string.help_tip_ai_colorization_title,
            subtitle = R.string.help_tip_ai_colorization_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pageTitle = R.string.help_tip_ai_colorization_title,
            description = R.string.help_tip_ai_colorization_description,
            steps = listOf(
                R.string.help_tip_ai_colorization_step_1,
                R.string.help_tip_ai_colorization_step_2,
                R.string.help_tip_ai_colorization_step_3
            )
        ),
        tip(
            id = "ai-face-restoration",
            title = R.string.help_tip_ai_face_restoration_title,
            subtitle = R.string.help_tip_ai_face_restoration_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pageTitle = R.string.help_tip_ai_face_restoration_title,
            description = R.string.help_tip_ai_face_restoration_description,
            steps = listOf(
                R.string.help_tip_ai_face_restoration_step_1,
                R.string.help_tip_ai_face_restoration_step_2,
                R.string.help_tip_ai_face_restoration_step_3
            )
        ),
        tip(
            id = "ai-dehaze",
            title = R.string.help_tip_ai_dehaze_title,
            subtitle = R.string.help_tip_ai_dehaze_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pageTitle = R.string.help_tip_ai_dehaze_title,
            description = R.string.help_tip_ai_dehaze_description,
            steps = listOf(
                R.string.help_tip_ai_dehaze_step_1,
                R.string.help_tip_ai_dehaze_step_2,
                R.string.help_tip_ai_dehaze_step_3
            )
        ),
        tip(
            id = "ai-document-cleanup",
            title = R.string.help_tip_ai_document_cleanup_title,
            subtitle = R.string.help_tip_ai_document_cleanup_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pageTitle = R.string.help_tip_ai_document_cleanup_title,
            description = R.string.help_tip_ai_document_cleanup_description,
            steps = listOf(
                R.string.help_tip_ai_document_cleanup_step_1,
                R.string.help_tip_ai_document_cleanup_step_2,
                R.string.help_tip_ai_document_cleanup_step_3
            )
        ),
        tip(
            id = "ai-style-transfer",
            title = R.string.help_tip_ai_style_transfer_title,
            subtitle = R.string.help_tip_ai_style_transfer_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pageTitle = R.string.help_tip_ai_style_transfer_title,
            description = R.string.help_tip_ai_style_transfer_description,
            steps = listOf(
                R.string.help_tip_ai_style_transfer_step_1,
                R.string.help_tip_ai_style_transfer_step_2,
                R.string.help_tip_ai_style_transfer_step_3
            )
        ),
        pagesTip(
            id = "ai-detection",
            title = R.string.help_tip_ai_detection_title,
            subtitle = R.string.help_tip_ai_detection_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.AiTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_ai_detection_title,
                    description = R.string.help_tip_ai_detection_description,
                    steps = listOf(
                        R.string.help_tip_ai_detection_step_1,
                        R.string.help_tip_ai_detection_step_2,
                        R.string.help_tip_ai_detection_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_ai_detection_page_2_title,
                    description = R.string.help_tip_ai_detection_page_2_description,
                    steps = listOf(
                        R.string.help_tip_ai_detection_page_2_step_1,
                        R.string.help_tip_ai_detection_page_2_step_2,
                        R.string.help_tip_ai_detection_page_2_step_3
                    )
                )
            )
        ),
        tip(
            id = "ai-background-models",
            title = R.string.help_tip_ai_background_models_title,
            subtitle = R.string.help_tip_ai_background_models_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.EraseBackground(),
            pageTitle = R.string.help_tip_ai_background_models_title,
            description = R.string.help_tip_ai_background_models_description,
            steps = listOf(
                R.string.help_tip_ai_background_models_step_1,
                R.string.help_tip_ai_background_models_step_2,
                R.string.help_tip_ai_background_models_step_3
            )
        ),
        tip(
            id = "ai-inpainting",
            title = R.string.help_tip_ai_inpainting_title,
            subtitle = R.string.help_tip_ai_inpainting_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.Draw(),
            pageTitle = R.string.help_tip_ai_inpainting_title,
            description = R.string.help_tip_ai_inpainting_description,
            steps = listOf(
                R.string.help_tip_ai_inpainting_step_1,
                R.string.help_tip_ai_inpainting_step_2,
                R.string.help_tip_ai_inpainting_step_3
            )
        ),
        tip(
            id = "ai-ocr",
            title = R.string.help_tip_ai_ocr_title,
            subtitle = R.string.help_tip_ai_ocr_subtitle,
            icon = Icons.Outlined.TextSearch,
            category = aiTools,
            deepLink = Screen.RecognizeText(),
            pageTitle = R.string.help_tip_ai_ocr_title,
            description = R.string.help_tip_ai_ocr_description,
            steps = listOf(
                R.string.help_tip_ai_ocr_step_1,
                R.string.help_tip_ai_ocr_step_2,
                R.string.help_tip_ai_ocr_step_3
            )
        ),
        tip(
            id = "ai-gallery",
            title = R.string.help_tip_ai_gallery_title,
            subtitle = R.string.help_tip_ai_gallery_subtitle,
            icon = Icons.Outlined.Neurology,
            category = aiTools,
            deepLink = Screen.Gallery,
            pageTitle = R.string.help_tip_ai_gallery_title,
            description = R.string.help_tip_ai_gallery_description,
            steps = listOf(
                R.string.help_tip_ai_gallery_step_1,
                R.string.help_tip_ai_gallery_step_2,
                R.string.help_tip_ai_gallery_step_3
            )
        ),
        tip(
            id = "usage-statistics",
            title = R.string.help_tip_usage_statistics_title,
            subtitle = R.string.help_tip_usage_statistics_subtitle,
            icon = Icons.Outlined.Help,
            category = gettingStarted,
            deepLink = Screen.UsageStatistics,
            pageTitle = R.string.help_tip_usage_statistics_title,
            description = R.string.help_tip_usage_statistics_description,
            steps = listOf(
                R.string.help_tip_usage_statistics_step_1,
                R.string.help_tip_usage_statistics_step_2,
                R.string.help_tip_usage_statistics_step_3
            )
        ),
        tip(
            id = "libraries-licenses",
            title = R.string.help_tip_libraries_licenses_title,
            subtitle = R.string.help_tip_libraries_licenses_subtitle,
            icon = Icons.Outlined.Help,
            category = gettingStarted,
            deepLink = Screen.LibrariesInfo,
            pageTitle = R.string.help_tip_libraries_licenses_title,
            description = R.string.help_tip_libraries_licenses_description,
            steps = listOf(
                R.string.help_tip_libraries_licenses_step_1,
                R.string.help_tip_libraries_licenses_step_2,
                R.string.help_tip_libraries_licenses_step_3
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
        pagesTip(
            id = TipIds.SvgAscii,
            title = R.string.help_tip_svg_ascii_title,
            subtitle = R.string.help_tip_svg_ascii_subtitle,
            icon = Icons.Outlined.VectorPolyline,
            category = creativeTools,
            deepLink = Screen.SvgMaker(),
            pages = listOf(
                page(
                    title = R.string.help_tip_svg_ascii_page_title,
                    description = R.string.help_tip_svg_ascii_description,
                    steps = listOf(
                        R.string.help_tip_svg_ascii_step_1,
                        R.string.help_tip_svg_ascii_step_2,
                        R.string.help_tip_svg_ascii_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_svg_ascii_page_2_title,
                    description = R.string.help_tip_svg_ascii_page_2_description,
                    steps = listOf(
                        R.string.help_tip_svg_ascii_page_2_step_1,
                        R.string.help_tip_svg_ascii_page_2_step_2,
                        R.string.help_tip_svg_ascii_page_2_step_3
                    )
                )
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
        pagesTip(
            id = TipIds.MarkupProjects,
            title = R.string.help_tip_markup_projects_title,
            subtitle = R.string.help_tip_markup_projects_subtitle,
            icon = Icons.Outlined.Stacks,
            category = creativeTools,
            deepLink = Screen.MarkupLayers(),
            pages = listOf(
                page(
                    title = R.string.help_tip_markup_projects_page_title,
                    description = R.string.help_tip_markup_projects_description,
                    steps = listOf(
                        R.string.help_tip_markup_projects_step_1,
                        R.string.help_tip_markup_projects_step_2,
                        R.string.help_tip_markup_projects_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_markup_projects_page_2_title,
                    description = R.string.help_tip_markup_projects_page_2_description,
                    steps = listOf(
                        R.string.help_tip_markup_projects_page_2_step_1,
                        R.string.help_tip_markup_projects_page_2_step_2,
                        R.string.help_tip_markup_projects_page_2_step_3
                    )
                )
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
        pagesTip(
            id = TipIds.ToneCurves,
            title = R.string.help_tip_tone_curves_title,
            subtitle = R.string.help_tip_tone_curves_subtitle,
            icon = Icons.Outlined.Curve,
            category = imageEditing,
            deepLink = Screen.Curves(),
            pages = listOf(
                page(
                    title = R.string.help_tip_tone_curves_page_title,
                    description = R.string.help_tip_tone_curves_description,
                    steps = listOf(
                        R.string.help_tip_tone_curves_step_1,
                        R.string.help_tip_tone_curves_step_2,
                        R.string.help_tip_tone_curves_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_tone_curves_page_2_title,
                    description = R.string.help_tip_tone_curves_page_2_description,
                    steps = listOf(
                        R.string.help_tip_tone_curves_page_2_step_1,
                        R.string.help_tip_tone_curves_page_2_step_2,
                        R.string.help_tip_tone_curves_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.StitchingModes,
            title = R.string.help_tip_stitching_modes_title,
            subtitle = R.string.help_tip_stitching_modes_subtitle,
            icon = Icons.Rounded.ImageCombine,
            category = imageEditing,
            deepLink = Screen.ImageStitching(),
            pages = listOf(
                page(
                    title = R.string.help_tip_stitching_modes_page_title,
                    description = R.string.help_tip_stitching_modes_description,
                    steps = listOf(
                        R.string.help_tip_stitching_modes_step_1,
                        R.string.help_tip_stitching_modes_step_2,
                        R.string.help_tip_stitching_modes_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_stitching_modes_page_2_title,
                    description = R.string.help_tip_stitching_modes_page_2_description,
                    steps = listOf(
                        R.string.help_tip_stitching_modes_page_2_step_1,
                        R.string.help_tip_stitching_modes_page_2_step_2,
                        R.string.help_tip_stitching_modes_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.StackingLayers,
            title = R.string.help_tip_stacking_layers_title,
            subtitle = R.string.help_tip_stacking_layers_subtitle,
            icon = Icons.Outlined.ImageOverlay,
            category = imageEditing,
            deepLink = Screen.ImageStacking(),
            pages = listOf(
                page(
                    title = R.string.help_tip_stacking_layers_page_title,
                    description = R.string.help_tip_stacking_layers_description,
                    steps = listOf(
                        R.string.help_tip_stacking_layers_step_1,
                        R.string.help_tip_stacking_layers_step_2,
                        R.string.help_tip_stacking_layers_step_3,
                        R.string.help_tip_stacking_layers_step_4
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.MultiFrameFusion,
            title = R.string.help_tip_multi_frame_fusion_title,
            subtitle = R.string.help_tip_multi_frame_fusion_subtitle,
            icon = Icons.Outlined.JoinInner,
            category = imageEditing,
            deepLink = Screen.MultiFrameFusion(),
            pages = listOf(
                page(
                    title = R.string.help_tip_multi_frame_fusion_page_title,
                    description = R.string.help_tip_multi_frame_fusion_description,
                    steps = listOf(
                        R.string.help_tip_multi_frame_fusion_step_1,
                        R.string.help_tip_multi_frame_fusion_step_2,
                        R.string.help_tip_multi_frame_fusion_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_multi_frame_fusion_page_2_title,
                    description = R.string.help_tip_multi_frame_fusion_page_2_description,
                    steps = listOf(
                        R.string.help_tip_multi_frame_fusion_page_2_step_1,
                        R.string.help_tip_multi_frame_fusion_page_2_step_2,
                        R.string.help_tip_multi_frame_fusion_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.Photomosaic,
            title = R.string.help_tip_photomosaic_title,
            subtitle = R.string.help_tip_photomosaic_subtitle,
            icon = Icons.Outlined.ViewQuilt,
            category = creativeTools,
            deepLink = Screen.Photomosaic(),
            pages = listOf(
                page(
                    title = R.string.help_tip_photomosaic_page_title,
                    description = R.string.help_tip_photomosaic_description,
                    steps = listOf(
                        R.string.help_tip_photomosaic_step_1,
                        R.string.help_tip_photomosaic_step_2,
                        R.string.help_tip_photomosaic_step_3,
                        R.string.help_tip_photomosaic_step_4
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.CompressionLab,
            title = R.string.help_tip_compression_lab_title,
            subtitle = R.string.help_tip_compression_lab_subtitle,
            icon = Icons.Rounded.Labs,
            category = filesAndMetadata,
            deepLink = Screen.CompressionLab(),
            pages = listOf(
                page(
                    title = R.string.help_tip_compression_lab_page_title,
                    description = R.string.help_tip_compression_lab_description,
                    steps = listOf(
                        R.string.help_tip_compression_lab_step_1,
                        R.string.help_tip_compression_lab_step_2,
                        R.string.help_tip_compression_lab_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_compression_lab_page_2_title,
                    description = R.string.help_tip_compression_lab_page_2_description,
                    steps = listOf(
                        R.string.help_tip_compression_lab_page_2_step_1,
                        R.string.help_tip_compression_lab_page_2_step_2,
                        R.string.help_tip_compression_lab_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.ScreenshotFraming,
            title = R.string.help_tip_screenshot_framing_title,
            subtitle = R.string.help_tip_screenshot_framing_subtitle,
            icon = Icons.Outlined.WallArt,
            category = creativeTools,
            deepLink = Screen.ScreenshotFraming(),
            pages = listOf(
                page(
                    title = R.string.help_tip_screenshot_framing_page_title,
                    description = R.string.help_tip_screenshot_framing_description,
                    steps = listOf(
                        R.string.help_tip_screenshot_framing_step_1,
                        R.string.help_tip_screenshot_framing_step_2,
                        R.string.help_tip_screenshot_framing_step_3,
                        R.string.help_tip_screenshot_framing_step_4
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.CodePreview,
            title = R.string.help_tip_code_preview_title,
            subtitle = R.string.help_tip_code_preview_subtitle,
            icon = Icons.Outlined.Terminal,
            category = creativeTools,
            deepLink = Screen.CodePreview,
            pages = listOf(
                page(
                    title = R.string.help_tip_code_preview_page_title,
                    description = R.string.help_tip_code_preview_description,
                    steps = listOf(
                        R.string.help_tip_code_preview_step_1,
                        R.string.help_tip_code_preview_step_2,
                        R.string.help_tip_code_preview_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_code_preview_page_2_title,
                    description = R.string.help_tip_code_preview_page_2_description,
                    steps = listOf(
                        R.string.help_tip_code_preview_page_2_step_1,
                        R.string.help_tip_code_preview_page_2_step_2,
                        R.string.help_tip_code_preview_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.FractalGeneration,
            title = R.string.help_tip_fractal_generation_title,
            subtitle = R.string.help_tip_fractal_generation_subtitle,
            icon = Icons.Outlined.Hub,
            category = creativeTools,
            deepLink = Screen.FractalGeneration,
            pages = listOf(
                page(
                    title = R.string.help_tip_fractal_generation_page_title,
                    description = R.string.help_tip_fractal_generation_description,
                    steps = listOf(
                        R.string.help_tip_fractal_generation_step_1,
                        R.string.help_tip_fractal_generation_step_2,
                        R.string.help_tip_fractal_generation_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_fractal_generation_page_2_title,
                    description = R.string.help_tip_fractal_generation_page_2_description,
                    steps = listOf(
                        R.string.help_tip_fractal_generation_page_2_step_1,
                        R.string.help_tip_fractal_generation_page_2_step_2,
                        R.string.help_tip_fractal_generation_page_2_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.ColorTools,
            title = R.string.help_tip_color_tools_title,
            subtitle = R.string.help_tip_color_tools_subtitle,
            icon = Icons.Outlined.Palette,
            category = colorTools,
            deepLink = Screen.ColorTools,
            pages = listOf(
                page(
                    title = R.string.help_tip_color_tools_page_title,
                    description = R.string.help_tip_color_tools_description,
                    steps = listOf(
                        R.string.help_tip_color_tools_step_1,
                        R.string.help_tip_color_tools_step_2,
                        R.string.help_tip_color_tools_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.PalettePdf,
            title = R.string.help_tip_palette_pdf_title,
            subtitle = R.string.help_tip_palette_pdf_subtitle,
            icon = Icons.Outlined.PaletteSwatch,
            category = colorTools,
            deepLink = Screen.PalettePdfTool(),
            pages = listOf(
                page(
                    title = R.string.help_tip_palette_pdf_page_title,
                    description = R.string.help_tip_palette_pdf_description,
                    steps = listOf(
                        R.string.help_tip_palette_pdf_step_1,
                        R.string.help_tip_palette_pdf_step_2,
                        R.string.help_tip_palette_pdf_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.GifWorkflow,
            title = R.string.help_tip_gif_workflow_title,
            subtitle = R.string.help_tip_gif_workflow_subtitle,
            icon = Icons.Outlined.GifBox,
            category = filesAndMetadata,
            deepLink = Screen.GifTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_gif_workflow_page_title,
                    description = R.string.help_tip_gif_workflow_description,
                    steps = listOf(
                        R.string.help_tip_gif_workflow_step_1,
                        R.string.help_tip_gif_workflow_step_2,
                        R.string.help_tip_gif_workflow_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.ApngWorkflow,
            title = R.string.help_tip_apng_workflow_title,
            subtitle = R.string.help_tip_apng_workflow_subtitle,
            icon = Icons.Outlined.ApngBox,
            category = filesAndMetadata,
            deepLink = Screen.ApngTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_apng_workflow_page_title,
                    description = R.string.help_tip_apng_workflow_description,
                    steps = listOf(
                        R.string.help_tip_apng_workflow_step_1,
                        R.string.help_tip_apng_workflow_step_2,
                        R.string.help_tip_apng_workflow_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.WebpWorkflow,
            title = R.string.help_tip_webp_workflow_title,
            subtitle = R.string.help_tip_webp_workflow_subtitle,
            icon = Icons.Outlined.WebpBox,
            category = filesAndMetadata,
            deepLink = Screen.WebpTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_webp_workflow_page_title,
                    description = R.string.help_tip_webp_workflow_description,
                    steps = listOf(
                        R.string.help_tip_webp_workflow_step_1,
                        R.string.help_tip_webp_workflow_step_2,
                        R.string.help_tip_webp_workflow_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.JxlWorkflow,
            title = R.string.help_tip_jxl_workflow_title,
            subtitle = R.string.help_tip_jxl_workflow_subtitle,
            icon = Icons.Filled.Jxl,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_jxl_workflow_page_title,
                    description = R.string.help_tip_jxl_workflow_description,
                    steps = listOf(
                        R.string.help_tip_jxl_workflow_step_1,
                        R.string.help_tip_jxl_workflow_step_2,
                        R.string.help_tip_jxl_workflow_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.AsciiWorkflow,
            title = R.string.help_tip_ascii_workflow_title,
            subtitle = R.string.help_tip_ascii_workflow_subtitle,
            icon = Icons.Outlined.Ascii,
            category = creativeTools,
            deepLink = Screen.AsciiArt(),
            pages = listOf(
                page(
                    title = R.string.help_tip_ascii_workflow_page_title,
                    description = R.string.help_tip_ascii_workflow_description,
                    steps = listOf(
                        R.string.help_tip_ascii_workflow_step_1,
                        R.string.help_tip_ascii_workflow_step_2,
                        R.string.help_tip_ascii_workflow_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.DrawingWorkflow,
            title = R.string.help_tip_drawing_workflow_title,
            subtitle = R.string.help_tip_drawing_workflow_subtitle,
            icon = Icons.Outlined.Draw,
            category = imageEditing,
            deepLink = Screen.Draw(),
            pages = listOf(
                page(
                    title = R.string.help_tip_drawing_workflow_page_title,
                    description = R.string.help_tip_drawing_workflow_description,
                    steps = listOf(
                        R.string.help_tip_drawing_workflow_step_1,
                        R.string.help_tip_drawing_workflow_step_2,
                        R.string.help_tip_drawing_workflow_step_3,
                        R.string.help_tip_drawing_workflow_step_4
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.ExifRemoval,
            title = R.string.help_tip_exif_removal_title,
            subtitle = R.string.help_tip_exif_removal_subtitle,
            icon = Icons.Outlined.Exif,
            category = filesAndMetadata,
            deepLink = Screen.DeleteExif(),
            pages = listOf(
                page(
                    title = R.string.help_tip_exif_removal_page_title,
                    description = R.string.help_tip_exif_removal_description,
                    steps = listOf(
                        R.string.help_tip_exif_removal_step_1,
                        R.string.help_tip_exif_removal_step_2,
                        R.string.help_tip_exif_removal_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.GalleryWorkflow,
            title = R.string.help_tip_gallery_workflow_title,
            subtitle = R.string.help_tip_gallery_workflow_subtitle,
            icon = Icons.Outlined.ImagesMode,
            category = gettingStarted,
            deepLink = Screen.Gallery,
            pages = listOf(
                page(
                    title = R.string.help_tip_gallery_workflow_page_title,
                    description = R.string.help_tip_gallery_workflow_description,
                    steps = listOf(
                        R.string.help_tip_gallery_workflow_step_1,
                        R.string.help_tip_gallery_workflow_step_2,
                        R.string.help_tip_gallery_workflow_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.ArchiveWorkflow,
            title = R.string.help_tip_archive_workflow_title,
            subtitle = R.string.help_tip_archive_workflow_subtitle,
            icon = Icons.Outlined.FolderZip,
            category = textAndData,
            deepLink = Screen.ArchiveTools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_archive_workflow_page_title,
                    description = R.string.help_tip_archive_workflow_description,
                    steps = listOf(
                        R.string.help_tip_archive_workflow_step_1,
                        R.string.help_tip_archive_workflow_step_2,
                        R.string.help_tip_archive_workflow_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.Base64Workflow,
            title = R.string.help_tip_base64_workflow_title,
            subtitle = R.string.help_tip_base64_workflow_subtitle,
            icon = Icons.Outlined.Base64,
            category = textAndData,
            deepLink = Screen.Base64Tools(),
            pages = listOf(
                page(
                    title = R.string.help_tip_base64_workflow_page_title,
                    description = R.string.help_tip_base64_workflow_description,
                    steps = listOf(
                        R.string.help_tip_base64_workflow_step_1,
                        R.string.help_tip_base64_workflow_step_2,
                        R.string.help_tip_base64_workflow_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.PdfComparison,
            title = R.string.help_tip_pdf_comparison_title,
            subtitle = R.string.help_tip_pdf_comparison_subtitle,
            icon = Icons.Outlined.Compare,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.ComparePdf(),
            pages = listOf(
                page(
                    title = R.string.help_tip_pdf_comparison_page_title,
                    description = R.string.help_tip_pdf_comparison_description,
                    steps = listOf(
                        R.string.help_tip_pdf_comparison_step_1,
                        R.string.help_tip_pdf_comparison_step_2,
                        R.string.help_tip_pdf_comparison_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.PdfPageResizing,
            title = R.string.help_tip_pdf_page_resizing_title,
            subtitle = R.string.help_tip_pdf_page_resizing_subtitle,
            icon = Icons.Outlined.ImageResize,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.ResizePdfPages(),
            pages = listOf(
                page(
                    title = R.string.help_tip_pdf_page_resizing_page_title,
                    description = R.string.help_tip_pdf_page_resizing_description,
                    steps = listOf(
                        R.string.help_tip_pdf_page_resizing_step_1,
                        R.string.help_tip_pdf_page_resizing_step_2,
                        R.string.help_tip_pdf_page_resizing_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.PdfSanitizing,
            title = R.string.help_tip_pdf_sanitizing_title,
            subtitle = R.string.help_tip_pdf_sanitizing_subtitle,
            icon = Icons.Outlined.HouseholdSupplies,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.SanitizePdf(),
            pages = listOf(
                page(
                    title = R.string.help_tip_pdf_sanitizing_page_title,
                    description = R.string.help_tip_pdf_sanitizing_description,
                    steps = listOf(
                        R.string.help_tip_pdf_sanitizing_step_1,
                        R.string.help_tip_pdf_sanitizing_step_2,
                        R.string.help_tip_pdf_sanitizing_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.PdfContactSheets,
            title = R.string.help_tip_pdf_contact_sheets_title,
            subtitle = R.string.help_tip_pdf_contact_sheets_subtitle,
            icon = Icons.Outlined.GridOn,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.PdfContactSheet(),
            pages = listOf(
                page(
                    title = R.string.help_tip_pdf_contact_sheets_page_title,
                    description = R.string.help_tip_pdf_contact_sheets_description,
                    steps = listOf(
                        R.string.help_tip_pdf_contact_sheets_step_1,
                        R.string.help_tip_pdf_contact_sheets_step_2,
                        R.string.help_tip_pdf_contact_sheets_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.PdfDarkReading,
            title = R.string.help_tip_pdf_dark_reading_title,
            subtitle = R.string.help_tip_pdf_dark_reading_subtitle,
            icon = Icons.Outlined.DarkMode,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.DarkMode(),
            pages = listOf(
                page(
                    title = R.string.help_tip_pdf_dark_reading_page_title,
                    description = R.string.help_tip_pdf_dark_reading_description,
                    steps = listOf(
                        R.string.help_tip_pdf_dark_reading_step_1,
                        R.string.help_tip_pdf_dark_reading_step_2,
                        R.string.help_tip_pdf_dark_reading_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.PdfImageExtraction,
            title = R.string.help_tip_pdf_image_extraction_title,
            subtitle = R.string.help_tip_pdf_image_extraction_subtitle,
            icon = Icons.Outlined.ArtTrack,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.ExtractPages(),
            pages = listOf(
                page(
                    title = R.string.help_tip_pdf_image_extraction_page_title,
                    description = R.string.help_tip_pdf_image_extraction_description,
                    steps = listOf(
                        R.string.help_tip_pdf_image_extraction_step_1,
                        R.string.help_tip_pdf_image_extraction_step_2,
                        R.string.help_tip_pdf_image_extraction_step_3
                    )
                )
            )
        ),
        pagesTip(
            id = TipIds.LargeFiles,
            title = R.string.help_tip_large_files_title,
            subtitle = R.string.help_tip_large_files_subtitle,
            icon = Icons.Outlined.Help,
            category = troubleshooting,
            pages = listOf(
                page(
                    title = R.string.help_tip_large_files_page_title,
                    description = R.string.help_tip_large_files_description,
                    steps = listOf(
                        R.string.help_tip_large_files_step_1,
                        R.string.help_tip_large_files_step_2,
                        R.string.help_tip_large_files_step_3
                    )
                ),
                page(
                    title = R.string.help_tip_large_files_page_2_title,
                    description = R.string.help_tip_large_files_page_2_description,
                    steps = listOf(
                        R.string.help_tip_large_files_page_2_step_1,
                        R.string.help_tip_large_files_page_2_step_2,
                        R.string.help_tip_large_files_page_2_step_3
                    )
                )
            )
        ),
        tip(
            id = TipIds.CacheAndPreviews,
            title = R.string.help_tip_cache_and_previews_title,
            subtitle = R.string.help_tip_cache_and_previews_subtitle,
            icon = Icons.Outlined.Preview,
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
            icon = Icons.Rounded.Security,
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
            icon = Icons.Rounded.FullscreenExit,
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
        ),
        tip(
            id = "pdf-merge",
            title = R.string.merge_pdf,
            subtitle = R.string.merge_pdf_sub,
            icon = Icons.Rounded.ImageCombine,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Merge(),
            pageTitle = R.string.merge_pdf,
            description = R.string.help_tip_pdf_merge_description,
            steps = listOf(
                R.string.help_tip_pdf_merge_step_1,
                R.string.help_tip_pdf_merge_step_2,
                R.string.help_tip_pdf_merge_step_3
            )
        ),
        tip(
            id = "pdf-split-selection",
            title = R.string.split_pdf,
            subtitle = R.string.split_pdf_sub,
            icon = Icons.Outlined.SplitAlt,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Split(),
            pageTitle = R.string.split_pdf,
            description = R.string.help_tip_pdf_split_selection_description,
            steps = listOf(
                R.string.help_tip_pdf_split_selection_step_1,
                R.string.help_tip_pdf_split_selection_step_2,
                R.string.help_tip_pdf_split_selection_step_3
            )
        ),
        tip(
            id = "pdf-remove-pages",
            title = R.string.remove_pages_pdf,
            subtitle = R.string.remove_pages_pdf_sub,
            icon = Icons.Outlined.DeleteSweep,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.RemovePages(),
            pageTitle = R.string.remove_pages_pdf,
            description = R.string.help_tip_pdf_remove_pages_description,
            steps = listOf(
                R.string.help_tip_pdf_remove_pages_step_1,
                R.string.help_tip_pdf_remove_pages_step_2,
                R.string.help_tip_pdf_remove_pages_step_3
            )
        ),
        tip(
            id = "pdf-rearrange",
            title = R.string.rearrange_pdf,
            subtitle = R.string.rearrange_pdf_sub,
            icon = Icons.Outlined.SwapVerticalCircle,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Rearrange(),
            pageTitle = R.string.rearrange_pdf,
            description = R.string.help_tip_pdf_rearrange_description,
            steps = listOf(
                R.string.help_tip_pdf_rearrange_step_1,
                R.string.help_tip_pdf_rearrange_step_2,
                R.string.help_tip_pdf_rearrange_step_3
            )
        ),
        tip(
            id = "pdf-rotate-pages",
            title = R.string.rotate_pdf,
            subtitle = R.string.rotate_pdf_sub,
            icon = Icons.Outlined.Rotate90Cw,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Rotate(),
            pageTitle = R.string.rotate_pdf,
            description = R.string.help_tip_pdf_rotate_pages_description,
            steps = listOf(
                R.string.help_tip_pdf_rotate_pages_step_1,
                R.string.help_tip_pdf_rotate_pages_step_2,
                R.string.help_tip_pdf_rotate_pages_step_3
            )
        ),
        tip(
            id = "pdf-crop-pages",
            title = R.string.crop_pdf,
            subtitle = R.string.crop_pdf_sub,
            icon = Icons.Rounded.CropSmall,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Crop(),
            pageTitle = R.string.crop_pdf,
            description = R.string.help_tip_pdf_crop_pages_description,
            steps = listOf(
                R.string.help_tip_pdf_crop_pages_step_1,
                R.string.help_tip_pdf_crop_pages_step_2,
                R.string.help_tip_pdf_crop_pages_step_3
            )
        ),
        tip(
            id = "pdf-page-numbers",
            title = R.string.page_numbers,
            subtitle = R.string.page_numbers_sub,
            icon = Icons.Outlined.Counter,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.PageNumbers(),
            pageTitle = R.string.page_numbers,
            description = R.string.help_tip_pdf_page_numbers_description,
            steps = listOf(
                R.string.help_tip_pdf_page_numbers_step_1,
                R.string.help_tip_pdf_page_numbers_step_2,
                R.string.help_tip_pdf_page_numbers_step_3
            )
        ),
        tip(
            id = "pdf-text-watermark",
            title = R.string.watermark_pdf,
            subtitle = R.string.watermark_pdf_sub,
            icon = Icons.Outlined.WatermarkAlt,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Watermark(),
            pageTitle = R.string.watermark_pdf,
            description = R.string.help_tip_pdf_text_watermark_description,
            steps = listOf(
                R.string.help_tip_pdf_text_watermark_step_1,
                R.string.help_tip_pdf_text_watermark_step_2,
                R.string.help_tip_pdf_text_watermark_step_3
            )
        ),
        tip(
            id = "pdf-visible-signature",
            title = R.string.signature,
            subtitle = R.string.signature_sub,
            icon = Icons.Outlined.Stylus,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Signature(),
            pageTitle = R.string.signature,
            description = R.string.help_tip_pdf_visible_signature_description,
            steps = listOf(
                R.string.help_tip_pdf_visible_signature_step_1,
                R.string.help_tip_pdf_visible_signature_step_2,
                R.string.help_tip_pdf_visible_signature_step_3
            )
        ),
        tip(
            id = "pdf-password-protection",
            title = R.string.protect_pdf,
            subtitle = R.string.protect_pdf_sub,
            icon = Icons.Outlined.ShieldLock,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Protect(),
            pageTitle = R.string.protect_pdf,
            description = R.string.help_tip_pdf_password_protection_description,
            steps = listOf(
                R.string.help_tip_pdf_password_protection_step_1,
                R.string.help_tip_pdf_password_protection_step_2,
                R.string.help_tip_pdf_password_protection_step_3
            )
        ),
        tip(
            id = "pdf-unlock-password",
            title = R.string.unlock_pdf,
            subtitle = R.string.unlock_pdf_sub,
            icon = Icons.Outlined.KeyVariant,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Unlock(),
            pageTitle = R.string.unlock_pdf,
            description = R.string.help_tip_pdf_unlock_password_description,
            steps = listOf(
                R.string.help_tip_pdf_unlock_password_step_1,
                R.string.help_tip_pdf_unlock_password_step_2,
                R.string.help_tip_pdf_unlock_password_step_3
            )
        ),
        tip(
            id = "pdf-grayscale-images",
            title = R.string.grayscale,
            subtitle = R.string.grayscale_pdf_sub,
            icon = Icons.Rounded.FilterBAndW,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Grayscale(),
            pageTitle = R.string.grayscale,
            description = R.string.help_tip_pdf_grayscale_images_description,
            steps = listOf(
                R.string.help_tip_pdf_grayscale_images_step_1,
                R.string.help_tip_pdf_grayscale_images_step_2,
                R.string.help_tip_pdf_grayscale_images_step_3
            )
        ),
        tip(
            id = "pdf-repair-structure",
            title = R.string.repair_pdf,
            subtitle = R.string.repair_pdf_sub,
            icon = Icons.Outlined.Build,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Repair(),
            pageTitle = R.string.repair_pdf,
            description = R.string.help_tip_pdf_repair_structure_description,
            steps = listOf(
                R.string.help_tip_pdf_repair_structure_step_1,
                R.string.help_tip_pdf_repair_structure_step_2,
                R.string.help_tip_pdf_repair_structure_step_3
            )
        ),
        tip(
            id = "pdf-flatten-pages",
            title = R.string.flatten_pdf,
            subtitle = R.string.flatten_pdf_sub,
            icon = Icons.Outlined.Panorama,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Flatten(),
            pageTitle = R.string.flatten_pdf,
            description = R.string.help_tip_pdf_flatten_pages_description,
            steps = listOf(
                R.string.help_tip_pdf_flatten_pages_step_1,
                R.string.help_tip_pdf_flatten_pages_step_2,
                R.string.help_tip_pdf_flatten_pages_step_3
            )
        ),
        tip(
            id = "pdf-split-zip",
            title = R.string.zip_pdf,
            subtitle = R.string.zip_pdf_sub,
            icon = Icons.Outlined.FolderZip,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.ZipConvert(),
            pageTitle = R.string.zip_pdf,
            description = R.string.help_tip_pdf_split_zip_description,
            steps = listOf(
                R.string.help_tip_pdf_split_zip_step_1,
                R.string.help_tip_pdf_split_zip_step_2,
                R.string.help_tip_pdf_split_zip_step_3
            )
        ),
        tip(
            id = "pdf-comic-archive",
            title = R.string.comic_to_pdf,
            subtitle = R.string.comic_to_pdf_sub,
            icon = Icons.Outlined.Manga,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.ComicToPdf(),
            pageTitle = R.string.comic_to_pdf,
            description = R.string.help_tip_pdf_comic_archive_description,
            steps = listOf(
                R.string.help_tip_pdf_comic_archive_step_1,
                R.string.help_tip_pdf_comic_archive_step_2,
                R.string.help_tip_pdf_comic_archive_step_3
            )
        ),
        tip(
            id = "pdf-print-layout",
            title = R.string.print_pdf,
            subtitle = R.string.print_pdf_sub,
            icon = Icons.Outlined.Print,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Print(),
            pageTitle = R.string.print_pdf,
            description = R.string.help_tip_pdf_print_layout_description,
            steps = listOf(
                R.string.help_tip_pdf_print_layout_step_1,
                R.string.help_tip_pdf_print_layout_step_2,
                R.string.help_tip_pdf_print_layout_step_3
            )
        ),
        tip(
            id = "pdf-preview-search",
            title = R.string.preview_pdf,
            subtitle = R.string.preview_pdf_sub,
            icon = Icons.Outlined.Preview,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.Preview(),
            pageTitle = R.string.preview_pdf,
            description = R.string.help_tip_pdf_preview_search_description,
            steps = listOf(
                R.string.help_tip_pdf_preview_search_step_1,
                R.string.help_tip_pdf_preview_search_step_2,
                R.string.help_tip_pdf_preview_search_step_3
            )
        ),
        tip(
            id = "pdf-text-extraction",
            title = R.string.pdf_to_text,
            subtitle = R.string.pdf_to_text_sub,
            icon = Icons.Outlined.FindInPage,
            category = pdfAndDocuments,
            deepLink = Screen.PdfTools.OCR(),
            pageTitle = R.string.pdf_to_text,
            description = R.string.help_tip_pdf_text_extraction_description,
            steps = listOf(
                R.string.help_tip_pdf_text_extraction_step_1,
                R.string.help_tip_pdf_text_extraction_step_2,
                R.string.help_tip_pdf_text_extraction_step_3
            )
        ),
        tip(
            id = "gif-extract-frames",
            title = R.string.gif_type_to_image,
            subtitle = R.string.gif_type_to_image_sub,
            icon = Screen.GifTools.Type.GifToImage().icon,
            category = filesAndMetadata,
            deepLink = Screen.GifTools(type = Screen.GifTools.Type.GifToImage()),
            pageTitle = R.string.gif_type_to_image,
            description = R.string.help_tip_gif_extract_frames_description,
            steps = listOf(
                R.string.help_tip_gif_extract_frames_step_1,
                R.string.help_tip_gif_extract_frames_step_2,
                R.string.help_tip_gif_extract_frames_step_3
            )
        ),
        tip(
            id = "gif-from-images",
            title = R.string.gif_type_to_gif,
            subtitle = R.string.gif_type_to_gif_sub,
            icon = Screen.GifTools.Type.ImageToGif().icon,
            category = filesAndMetadata,
            deepLink = Screen.GifTools(type = Screen.GifTools.Type.ImageToGif()),
            pageTitle = R.string.gif_type_to_gif,
            description = R.string.help_tip_gif_from_images_description,
            steps = listOf(
                R.string.help_tip_gif_from_images_step_1,
                R.string.help_tip_gif_from_images_step_2,
                R.string.help_tip_gif_from_images_step_3
            )
        ),
        tip(
            id = "gif-merge-clips",
            title = R.string.gif_type_merge,
            subtitle = R.string.gif_type_merge_sub,
            icon = Screen.GifTools.Type.MergeGif().icon,
            category = filesAndMetadata,
            deepLink = Screen.GifTools(type = Screen.GifTools.Type.MergeGif()),
            pageTitle = R.string.gif_type_merge,
            description = R.string.help_tip_gif_merge_clips_description,
            steps = listOf(
                R.string.help_tip_gif_merge_clips_step_1,
                R.string.help_tip_gif_merge_clips_step_2,
                R.string.help_tip_gif_merge_clips_step_3
            )
        ),
        tip(
            id = "gif-to-apng",
            title = R.string.gif_type_to_apng,
            subtitle = R.string.gif_type_to_apng_sub,
            icon = Screen.GifTools.Type.GifToApng().icon,
            category = filesAndMetadata,
            deepLink = Screen.GifTools(type = Screen.GifTools.Type.GifToApng()),
            pageTitle = R.string.gif_type_to_apng,
            description = R.string.help_tip_gif_to_apng_description,
            steps = listOf(
                R.string.help_tip_gif_to_apng_step_1,
                R.string.help_tip_gif_to_apng_step_2,
                R.string.help_tip_gif_to_apng_step_3
            )
        ),
        tip(
            id = "gif-to-webp",
            title = R.string.gif_type_to_webp,
            subtitle = R.string.gif_type_to_webp_sub,
            icon = Screen.GifTools.Type.GifToWebp().icon,
            category = filesAndMetadata,
            deepLink = Screen.GifTools(type = Screen.GifTools.Type.GifToWebp()),
            pageTitle = R.string.gif_type_to_webp,
            description = R.string.help_tip_gif_to_webp_description,
            steps = listOf(
                R.string.help_tip_gif_to_webp_step_1,
                R.string.help_tip_gif_to_webp_step_2,
                R.string.help_tip_gif_to_webp_step_3
            )
        ),
        tip(
            id = "gif-to-jxl",
            title = R.string.gif_type_to_jxl,
            subtitle = R.string.gif_type_to_jxl_sub,
            icon = Screen.GifTools.Type.GifToJxl().icon,
            category = filesAndMetadata,
            deepLink = Screen.GifTools(type = Screen.GifTools.Type.GifToJxl()),
            pageTitle = R.string.gif_type_to_jxl,
            description = R.string.help_tip_gif_to_jxl_description,
            steps = listOf(
                R.string.help_tip_gif_to_jxl_step_1,
                R.string.help_tip_gif_to_jxl_step_2,
                R.string.help_tip_gif_to_jxl_step_3
            )
        ),
        tip(
            id = "apng-extract-frames",
            title = R.string.apng_type_to_image,
            subtitle = R.string.apng_type_to_image_sub,
            icon = Screen.ApngTools.Type.ApngToImage().icon,
            category = filesAndMetadata,
            deepLink = Screen.ApngTools(type = Screen.ApngTools.Type.ApngToImage()),
            pageTitle = R.string.apng_type_to_image,
            description = R.string.help_tip_apng_extract_frames_description,
            steps = listOf(
                R.string.help_tip_apng_extract_frames_step_1,
                R.string.help_tip_apng_extract_frames_step_2,
                R.string.help_tip_apng_extract_frames_step_3
            )
        ),
        tip(
            id = "apng-from-images",
            title = R.string.apng_type_to_apng,
            subtitle = R.string.apng_type_to_apng_sub,
            icon = Screen.ApngTools.Type.ImageToApng().icon,
            category = filesAndMetadata,
            deepLink = Screen.ApngTools(type = Screen.ApngTools.Type.ImageToApng()),
            pageTitle = R.string.apng_type_to_apng,
            description = R.string.help_tip_apng_from_images_description,
            steps = listOf(
                R.string.help_tip_apng_from_images_step_1,
                R.string.help_tip_apng_from_images_step_2,
                R.string.help_tip_apng_from_images_step_3
            )
        ),
        tip(
            id = "apng-merge-clips",
            title = R.string.apng_type_merge,
            subtitle = R.string.apng_type_merge_sub,
            icon = Screen.ApngTools.Type.MergeApng().icon,
            category = filesAndMetadata,
            deepLink = Screen.ApngTools(type = Screen.ApngTools.Type.MergeApng()),
            pageTitle = R.string.apng_type_merge,
            description = R.string.help_tip_apng_merge_clips_description,
            steps = listOf(
                R.string.help_tip_apng_merge_clips_step_1,
                R.string.help_tip_apng_merge_clips_step_2,
                R.string.help_tip_apng_merge_clips_step_3
            )
        ),
        tip(
            id = "apng-to-gif",
            title = R.string.apng_type_to_gif,
            subtitle = R.string.apng_type_to_gif_sub,
            icon = Screen.ApngTools.Type.ApngToGif().icon,
            category = filesAndMetadata,
            deepLink = Screen.ApngTools(type = Screen.ApngTools.Type.ApngToGif()),
            pageTitle = R.string.apng_type_to_gif,
            description = R.string.help_tip_apng_to_gif_description,
            steps = listOf(
                R.string.help_tip_apng_to_gif_step_1,
                R.string.help_tip_apng_to_gif_step_2,
                R.string.help_tip_apng_to_gif_step_3
            )
        ),
        tip(
            id = "apng-to-webp",
            title = R.string.apng_type_to_webp,
            subtitle = R.string.apng_type_to_webp_sub,
            icon = Screen.ApngTools.Type.ApngToWebp().icon,
            category = filesAndMetadata,
            deepLink = Screen.ApngTools(type = Screen.ApngTools.Type.ApngToWebp()),
            pageTitle = R.string.apng_type_to_webp,
            description = R.string.help_tip_apng_to_webp_description,
            steps = listOf(
                R.string.help_tip_apng_to_webp_step_1,
                R.string.help_tip_apng_to_webp_step_2,
                R.string.help_tip_apng_to_webp_step_3
            )
        ),
        tip(
            id = "apng-to-jxl",
            title = R.string.apng_type_to_jxl,
            subtitle = R.string.apng_type_to_jxl_sub,
            icon = Screen.ApngTools.Type.ApngToJxl().icon,
            category = filesAndMetadata,
            deepLink = Screen.ApngTools(type = Screen.ApngTools.Type.ApngToJxl()),
            pageTitle = R.string.apng_type_to_jxl,
            description = R.string.help_tip_apng_to_jxl_description,
            steps = listOf(
                R.string.help_tip_apng_to_jxl_step_1,
                R.string.help_tip_apng_to_jxl_step_2,
                R.string.help_tip_apng_to_jxl_step_3
            )
        ),
        tip(
            id = "webp-extract-frames",
            title = R.string.webp_type_to_image,
            subtitle = R.string.webp_type_to_image_sub,
            icon = Screen.WebpTools.Type.WebpToImage().icon,
            category = filesAndMetadata,
            deepLink = Screen.WebpTools(type = Screen.WebpTools.Type.WebpToImage()),
            pageTitle = R.string.webp_type_to_image,
            description = R.string.help_tip_webp_extract_frames_description,
            steps = listOf(
                R.string.help_tip_webp_extract_frames_step_1,
                R.string.help_tip_webp_extract_frames_step_2,
                R.string.help_tip_webp_extract_frames_step_3
            )
        ),
        tip(
            id = "webp-from-images",
            title = R.string.webp_type_to_webp,
            subtitle = R.string.webp_type_to_webp_sub,
            icon = Screen.WebpTools.Type.ImageToWebp().icon,
            category = filesAndMetadata,
            deepLink = Screen.WebpTools(type = Screen.WebpTools.Type.ImageToWebp()),
            pageTitle = R.string.webp_type_to_webp,
            description = R.string.help_tip_webp_from_images_description,
            steps = listOf(
                R.string.help_tip_webp_from_images_step_1,
                R.string.help_tip_webp_from_images_step_2,
                R.string.help_tip_webp_from_images_step_3
            )
        ),
        tip(
            id = "webp-merge-clips",
            title = R.string.webp_type_merge,
            subtitle = R.string.webp_type_merge_sub,
            icon = Screen.WebpTools.Type.MergeWebp().icon,
            category = filesAndMetadata,
            deepLink = Screen.WebpTools(type = Screen.WebpTools.Type.MergeWebp()),
            pageTitle = R.string.webp_type_merge,
            description = R.string.help_tip_webp_merge_clips_description,
            steps = listOf(
                R.string.help_tip_webp_merge_clips_step_1,
                R.string.help_tip_webp_merge_clips_step_2,
                R.string.help_tip_webp_merge_clips_step_3
            )
        ),
        tip(
            id = "webp-to-gif",
            title = R.string.webp_type_to_gif,
            subtitle = R.string.webp_type_to_gif_sub,
            icon = Screen.WebpTools.Type.WebpToGif().icon,
            category = filesAndMetadata,
            deepLink = Screen.WebpTools(type = Screen.WebpTools.Type.WebpToGif()),
            pageTitle = R.string.webp_type_to_gif,
            description = R.string.help_tip_webp_to_gif_description,
            steps = listOf(
                R.string.help_tip_webp_to_gif_step_1,
                R.string.help_tip_webp_to_gif_step_2,
                R.string.help_tip_webp_to_gif_step_3
            )
        ),
        tip(
            id = "webp-to-apng",
            title = R.string.webp_type_to_apng,
            subtitle = R.string.webp_type_to_apng_sub,
            icon = Screen.WebpTools.Type.WebpToApng().icon,
            category = filesAndMetadata,
            deepLink = Screen.WebpTools(type = Screen.WebpTools.Type.WebpToApng()),
            pageTitle = R.string.webp_type_to_apng,
            description = R.string.help_tip_webp_to_apng_description,
            steps = listOf(
                R.string.help_tip_webp_to_apng_step_1,
                R.string.help_tip_webp_to_apng_step_2,
                R.string.help_tip_webp_to_apng_step_3
            )
        ),
        tip(
            id = "webp-to-jxl",
            title = R.string.webp_type_to_jxl,
            subtitle = R.string.webp_type_to_jxl_sub,
            icon = Screen.WebpTools.Type.WebpToJxl().icon,
            category = filesAndMetadata,
            deepLink = Screen.WebpTools(type = Screen.WebpTools.Type.WebpToJxl()),
            pageTitle = R.string.webp_type_to_jxl,
            description = R.string.help_tip_webp_to_jxl_description,
            steps = listOf(
                R.string.help_tip_webp_to_jxl_step_1,
                R.string.help_tip_webp_to_jxl_step_2,
                R.string.help_tip_webp_to_jxl_step_3
            )
        ),
        tip(
            id = "jxl-extract-frames",
            title = R.string.jxl_type_to_images,
            subtitle = R.string.jxl_type_to_images_sub,
            icon = Screen.JxlTools.Type.JxlToImage().icon,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(type = Screen.JxlTools.Type.JxlToImage()),
            pageTitle = R.string.jxl_type_to_images,
            description = R.string.help_tip_jxl_extract_frames_description,
            steps = listOf(
                R.string.help_tip_jxl_extract_frames_step_1,
                R.string.help_tip_jxl_extract_frames_step_2,
                R.string.help_tip_jxl_extract_frames_step_3
            )
        ),
        tip(
            id = "jxl-from-images",
            title = R.string.jxl_type_to_jxl,
            subtitle = R.string.jxl_type_to_jxl_sub,
            icon = Screen.JxlTools.Type.ImageToJxl().icon,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(type = Screen.JxlTools.Type.ImageToJxl()),
            pageTitle = R.string.jxl_type_to_jxl,
            description = R.string.help_tip_jxl_from_images_description,
            steps = listOf(
                R.string.help_tip_jxl_from_images_step_1,
                R.string.help_tip_jxl_from_images_step_2,
                R.string.help_tip_jxl_from_images_step_3
            )
        ),
        tip(
            id = "jxl-merge-clips",
            title = R.string.jxl_type_merge,
            subtitle = R.string.jxl_type_merge_sub,
            icon = Screen.JxlTools.Type.MergeJxl().icon,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(type = Screen.JxlTools.Type.MergeJxl()),
            pageTitle = R.string.jxl_type_merge,
            description = R.string.help_tip_jxl_merge_clips_description,
            steps = listOf(
                R.string.help_tip_jxl_merge_clips_step_1,
                R.string.help_tip_jxl_merge_clips_step_2,
                R.string.help_tip_jxl_merge_clips_step_3
            )
        ),
        tip(
            id = "jxl-to-gif",
            title = R.string.jxl_type_to_gif,
            subtitle = R.string.jxl_type_to_gif_sub,
            icon = Screen.JxlTools.Type.JxlToGif().icon,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(type = Screen.JxlTools.Type.JxlToGif()),
            pageTitle = R.string.jxl_type_to_gif,
            description = R.string.help_tip_jxl_to_gif_description,
            steps = listOf(
                R.string.help_tip_jxl_to_gif_step_1,
                R.string.help_tip_jxl_to_gif_step_2,
                R.string.help_tip_jxl_to_gif_step_3
            )
        ),
        tip(
            id = "jxl-to-apng",
            title = R.string.jxl_type_to_apng,
            subtitle = R.string.jxl_type_to_apng_sub,
            icon = Screen.JxlTools.Type.JxlToApng().icon,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(type = Screen.JxlTools.Type.JxlToApng()),
            pageTitle = R.string.jxl_type_to_apng,
            description = R.string.help_tip_jxl_to_apng_description,
            steps = listOf(
                R.string.help_tip_jxl_to_apng_step_1,
                R.string.help_tip_jxl_to_apng_step_2,
                R.string.help_tip_jxl_to_apng_step_3
            )
        ),
        tip(
            id = "jxl-to-webp",
            title = R.string.jxl_type_to_webp,
            subtitle = R.string.jxl_type_to_webp_sub,
            icon = Screen.JxlTools.Type.JxlToWebp().icon,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(type = Screen.JxlTools.Type.JxlToWebp()),
            pageTitle = R.string.jxl_type_to_webp,
            description = R.string.help_tip_jxl_to_webp_description,
            steps = listOf(
                R.string.help_tip_jxl_to_webp_step_1,
                R.string.help_tip_jxl_to_webp_step_2,
                R.string.help_tip_jxl_to_webp_step_3
            )
        ),
        tip(
            id = "jpeg-to-jxl-reconstruction",
            title = R.string.jpeg_type_to_jxl,
            subtitle = R.string.jpeg_type_to_jxl_sub,
            icon = Screen.JxlTools.Type.JpegToJxl().icon,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(type = Screen.JxlTools.Type.JpegToJxl()),
            pageTitle = R.string.jpeg_type_to_jxl,
            description = R.string.help_tip_jpeg_to_jxl_reconstruction_description,
            steps = listOf(
                R.string.help_tip_jpeg_to_jxl_reconstruction_step_1,
                R.string.help_tip_jpeg_to_jxl_reconstruction_step_2,
                R.string.help_tip_jpeg_to_jxl_reconstruction_step_3
            )
        ),
        tip(
            id = "jxl-to-jpeg-reconstruction",
            title = R.string.jxl_type_to_jpeg,
            subtitle = R.string.jxl_type_to_jpeg_sub,
            icon = Screen.JxlTools.Type.JxlToJpeg().icon,
            category = filesAndMetadata,
            deepLink = Screen.JxlTools(type = Screen.JxlTools.Type.JxlToJpeg()),
            pageTitle = R.string.jxl_type_to_jpeg,
            description = R.string.help_tip_jxl_to_jpeg_reconstruction_description,
            steps = listOf(
                R.string.help_tip_jxl_to_jpeg_reconstruction_step_1,
                R.string.help_tip_jxl_to_jpeg_reconstruction_step_2,
                R.string.help_tip_jxl_to_jpeg_reconstruction_step_3
            )
        ),
        tip(
            id = "filters-whole-images",
            title = R.string.full_filter,
            subtitle = R.string.full_filter_sub,
            icon = Screen.Filter.Type.Basic().icon,
            category = imageEditing,
            deepLink = Screen.Filter(type = Screen.Filter.Type.Basic()),
            pageTitle = R.string.full_filter,
            description = R.string.help_tip_filters_whole_images_description,
            steps = listOf(
                R.string.help_tip_filters_whole_images_step_1,
                R.string.help_tip_filters_whole_images_step_2,
                R.string.help_tip_filters_whole_images_step_3
            )
        ),
        tip(
            id = "filters-local-masks",
            title = R.string.mask_filter,
            subtitle = R.string.mask_filter_sub,
            icon = Screen.Filter.Type.Masking().icon,
            category = imageEditing,
            deepLink = Screen.Filter(type = Screen.Filter.Type.Masking()),
            pageTitle = R.string.mask_filter,
            description = R.string.help_tip_filters_local_masks_description,
            steps = listOf(
                R.string.help_tip_filters_local_masks_step_1,
                R.string.help_tip_filters_local_masks_step_2,
                R.string.help_tip_filters_local_masks_step_3
            )
        ),
        tip(
            id = "ocr-extract-preview",
            title = R.string.recognize_text,
            subtitle = R.string.recognize_text_sub,
            icon = Screen.RecognizeText.Type.Extraction().icon,
            category = textAndData,
            deepLink = Screen.RecognizeText(type = Screen.RecognizeText.Type.Extraction()),
            pageTitle = R.string.recognize_text,
            description = R.string.help_tip_ocr_extract_preview_description,
            steps = listOf(
                R.string.help_tip_ocr_extract_preview_step_1,
                R.string.help_tip_ocr_extract_preview_step_2,
                R.string.help_tip_ocr_extract_preview_step_3
            )
        ),
        tip(
            id = "ocr-batch-text-file",
            title = R.string.ocr_write_to_file,
            subtitle = R.string.ocr_write_to_file_sub,
            icon = Screen.RecognizeText.Type.WriteToFile().icon,
            category = textAndData,
            deepLink = Screen.RecognizeText(type = Screen.RecognizeText.Type.WriteToFile()),
            pageTitle = R.string.ocr_write_to_file,
            description = R.string.help_tip_ocr_batch_text_file_description,
            steps = listOf(
                R.string.help_tip_ocr_batch_text_file_step_1,
                R.string.help_tip_ocr_batch_text_file_step_2,
                R.string.help_tip_ocr_batch_text_file_step_3
            )
        ),
        tip(
            id = "ocr-image-metadata",
            title = R.string.ocr_write_to_metadata,
            subtitle = R.string.ocr_write_to_metadata_sub,
            icon = Screen.RecognizeText.Type.WriteToMetadata().icon,
            category = textAndData,
            deepLink = Screen.RecognizeText(type = Screen.RecognizeText.Type.WriteToMetadata()),
            pageTitle = R.string.ocr_write_to_metadata,
            description = R.string.help_tip_ocr_image_metadata_description,
            steps = listOf(
                R.string.help_tip_ocr_image_metadata_step_1,
                R.string.help_tip_ocr_image_metadata_step_2,
                R.string.help_tip_ocr_image_metadata_step_3
            )
        ),
        tip(
            id = "setting-author",
            title = R.string.app_developer,
            subtitle = R.string.app_developer_nick,
            icon = Icons.Outlined.Forum,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.Author),
            pageTitle = R.string.app_developer,
            description = R.string.help_tip_setting_author_description,
            steps = listOf(
                R.string.help_tip_setting_author_step_1,
                R.string.help_tip_setting_author_step_2
            )
        ),
        tip(
            id = "setting-send-logs",
            title = R.string.send_logs,
            subtitle = R.string.send_logs_sub,
            icon = Icons.Outlined.MobileShare,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.SendLogs),
            pageTitle = R.string.send_logs,
            description = R.string.help_tip_setting_send_logs_description,
            steps = listOf(
                R.string.help_tip_setting_send_logs_step_1,
                R.string.help_tip_setting_send_logs_step_2
            )
        ),
        tip(
            id = "setting-donate",
            title = R.string.donation,
            subtitle = R.string.donation_sub,
            icon = Icons.Outlined.VolunteerActivism,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.Donate),
            pageTitle = R.string.donation,
            description = R.string.help_tip_setting_donate_description,
            steps = listOf(
                R.string.help_tip_setting_donate_step_1,
                R.string.help_tip_setting_donate_step_2
            )
        ),
        tip(
            id = "setting-dynamic-colors",
            title = R.string.dynamic_colors,
            subtitle = R.string.dynamic_colors_sub,
            icon = Icons.Rounded.FormatColorFill,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.DynamicColors),
            pageTitle = R.string.dynamic_colors,
            description = R.string.help_tip_setting_dynamic_colors_description,
            steps = listOf(
                R.string.help_tip_setting_dynamic_colors_step_1,
                R.string.help_tip_setting_dynamic_colors_step_2
            )
        ),
        tip(
            id = "setting-amoled-mode",
            title = R.string.amoled_mode,
            subtitle = R.string.amoled_mode_sub,
            icon = Icons.Outlined.Brightness4,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.AmoledMode),
            pageTitle = R.string.amoled_mode,
            description = R.string.help_tip_setting_amoled_mode_description,
            steps = listOf(
                R.string.help_tip_setting_amoled_mode_step_1,
                R.string.help_tip_setting_amoled_mode_step_2
            )
        ),
        tip(
            id = "setting-icon-shape",
            title = R.string.icon_shape,
            subtitle = R.string.icon_shape_sub,
            icon = Icons.Outlined.FormatShapes,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.IconShape),
            pageTitle = R.string.icon_shape,
            description = R.string.help_tip_setting_icon_shape_description,
            steps = listOf(
                R.string.help_tip_setting_icon_shape_step_1,
                R.string.help_tip_setting_icon_shape_step_2
            )
        ),
        tip(
            id = "setting-allow-image-monet",
            title = R.string.allow_image_monet,
            subtitle = R.string.allow_image_monet_sub,
            icon = Icons.Outlined.WaterDrop,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.AllowImageMonet),
            pageTitle = R.string.allow_image_monet,
            description = R.string.help_tip_setting_allow_image_monet_description,
            steps = listOf(
                R.string.help_tip_setting_allow_image_monet_step_1,
                R.string.help_tip_setting_allow_image_monet_step_2
            )
        ),
        tip(
            id = "setting-border-thickness",
            title = R.string.border_thickness,
            subtitle = R.string.help_tip_setting_border_thickness_description,
            icon = Icons.Rounded.BorderStyle,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.BorderThickness),
            pageTitle = R.string.border_thickness,
            description = R.string.help_tip_setting_border_thickness_description,
            steps = listOf(
                R.string.help_tip_setting_border_thickness_step_1,
                R.string.help_tip_setting_border_thickness_step_2
            )
        ),
        tip(
            id = "setting-main-screen-title",
            title = R.string.main_screen_title,
            subtitle = R.string.help_tip_setting_main_screen_title_description,
            icon = Icons.Rounded.Title,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.MainScreenTitle),
            pageTitle = R.string.main_screen_title,
            description = R.string.help_tip_setting_main_screen_title_description,
            steps = listOf(
                R.string.help_tip_setting_main_screen_title_step_1,
                R.string.help_tip_setting_main_screen_title_step_2
            )
        ),
        tip(
            id = "setting-switch-type",
            title = R.string.switch_type,
            subtitle = R.string.help_tip_setting_switch_type_description,
            icon = Icons.Outlined.ToggleOn,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.SwitchType),
            pageTitle = R.string.switch_type,
            description = R.string.help_tip_setting_switch_type_description,
            steps = listOf(
                R.string.help_tip_setting_switch_type_step_1,
                R.string.help_tip_setting_switch_type_step_2
            )
        ),
        tip(
            id = "setting-slider-type",
            title = R.string.slider_type,
            subtitle = R.string.help_tip_setting_slider_type_description,
            icon = Icons.Rounded.Slider,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.SliderType),
            pageTitle = R.string.slider_type,
            description = R.string.help_tip_setting_slider_type_description,
            steps = listOf(
                R.string.help_tip_setting_slider_type_step_1,
                R.string.help_tip_setting_slider_type_step_2
            )
        ),
        tip(
            id = "setting-shape-type",
            title = R.string.shapes_type,
            subtitle = R.string.help_tip_setting_shape_type_description,
            icon = Icons.Rounded.RoundedCorner,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ShapeType),
            pageTitle = R.string.shapes_type,
            description = R.string.help_tip_setting_shape_type_description,
            steps = listOf(
                R.string.help_tip_setting_shape_type_step_1,
                R.string.help_tip_setting_shape_type_step_2
            )
        ),
        tip(
            id = "setting-corners-size",
            title = R.string.corners_size,
            subtitle = R.string.help_tip_setting_corners_size_description,
            icon = Icons.Rounded.Percent,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.CornersSize),
            pageTitle = R.string.corners_size,
            description = R.string.help_tip_setting_corners_size_description,
            steps = listOf(
                R.string.help_tip_setting_corners_size_step_1,
                R.string.help_tip_setting_corners_size_step_2
            )
        ),
        tip(
            id = "setting-shape-by-interaction-throttle",
            title = R.string.shape_by_interaction_throttle,
            subtitle = R.string.help_tip_setting_shape_by_interaction_throttle_description,
            icon = Icons.Outlined.Timelapse,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ShapeByInteractionThrottle),
            pageTitle = R.string.shape_by_interaction_throttle,
            description = R.string.help_tip_setting_shape_by_interaction_throttle_description,
            steps = listOf(
                R.string.help_tip_setting_shape_by_interaction_throttle_step_1,
                R.string.help_tip_setting_shape_by_interaction_throttle_step_2
            )
        ),
        tip(
            id = "setting-fling-type",
            title = R.string.fling_type,
            subtitle = R.string.help_tip_setting_fling_type_description,
            icon = Icons.Rounded.Animation,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.FlingType),
            pageTitle = R.string.fling_type,
            description = R.string.help_tip_setting_fling_type_description,
            steps = listOf(
                R.string.help_tip_setting_fling_type_step_1,
                R.string.help_tip_setting_fling_type_step_2
            )
        ),
        tip(
            id = "setting-drag-handle-width",
            title = R.string.drag_handle_width,
            subtitle = R.string.help_tip_setting_drag_handle_width_description,
            icon = Icons.Rounded.DragHandle,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.DragHandleWidth),
            pageTitle = R.string.drag_handle_width,
            description = R.string.help_tip_setting_drag_handle_width_description,
            steps = listOf(
                R.string.help_tip_setting_drag_handle_width_step_1,
                R.string.help_tip_setting_drag_handle_width_step_2
            )
        ),
        tip(
            id = "setting-dialog-position",
            title = R.string.dialog_position,
            subtitle = R.string.dialog_position_sub,
            icon = Icons.Outlined.Place,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.DialogPosition),
            pageTitle = R.string.dialog_position,
            description = R.string.help_tip_setting_dialog_position_description,
            steps = listOf(
                R.string.help_tip_setting_dialog_position_step_1,
                R.string.help_tip_setting_dialog_position_step_2
            )
        ),
        tip(
            id = "setting-center-align-dialog-buttons",
            title = R.string.center_align_dialog_buttons,
            subtitle = R.string.center_align_dialog_buttons_sub,
            icon = Icons.Outlined.PictureInPictureCenter,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.CenterAlignDialogButtons),
            pageTitle = R.string.center_align_dialog_buttons,
            description = R.string.help_tip_setting_center_align_dialog_buttons_description,
            steps = listOf(
                R.string.help_tip_setting_center_align_dialog_buttons_step_1,
                R.string.help_tip_setting_center_align_dialog_buttons_step_2
            )
        ),
        tip(
            id = "setting-fab-alignment",
            title = R.string.fab_alignment,
            subtitle = R.string.help_tip_setting_fab_alignment_description,
            icon = Icons.Rounded.AlignVerticalCenter,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.FabAlignment),
            pageTitle = R.string.fab_alignment,
            description = R.string.help_tip_setting_fab_alignment_description,
            steps = listOf(
                R.string.help_tip_setting_fab_alignment_step_1,
                R.string.help_tip_setting_fab_alignment_step_2
            )
        ),
        tip(
            id = "setting-night-mode",
            title = R.string.night_mode,
            subtitle = R.string.help_tip_setting_night_mode_description,
            icon = Icons.Rounded.Routine,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.NightMode),
            pageTitle = R.string.night_mode,
            description = R.string.help_tip_setting_night_mode_description,
            steps = listOf(
                R.string.help_tip_setting_night_mode_step_1,
                R.string.help_tip_setting_night_mode_step_2
            )
        ),
        tip(
            id = "setting-container-shadows",
            title = R.string.containers_shadow,
            subtitle = R.string.containers_shadow_sub,
            icon = Icons.Outlined.ViewComfy,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ContainerShadows),
            pageTitle = R.string.containers_shadow,
            description = R.string.help_tip_setting_container_shadows_description,
            steps = listOf(
                R.string.help_tip_setting_container_shadows_step_1,
                R.string.help_tip_setting_container_shadows_step_2
            )
        ),
        tip(
            id = "setting-app-bar-shadows",
            title = R.string.app_bars_shadow,
            subtitle = R.string.app_bars_shadow_sub,
            icon = Icons.Rounded.HorizontalSplit,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.AppBarShadows),
            pageTitle = R.string.app_bars_shadow,
            description = R.string.help_tip_setting_app_bar_shadows_description,
            steps = listOf(
                R.string.help_tip_setting_app_bar_shadows_step_1,
                R.string.help_tip_setting_app_bar_shadows_step_2
            )
        ),
        tip(
            id = "setting-button-shadows",
            title = R.string.buttons_shadow,
            subtitle = R.string.buttons_shadow_sub,
            icon = Icons.Outlined.Gamepad,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ButtonShadows),
            pageTitle = R.string.buttons_shadow,
            description = R.string.help_tip_setting_button_shadows_description,
            steps = listOf(
                R.string.help_tip_setting_button_shadows_step_1,
                R.string.help_tip_setting_button_shadows_step_2
            )
        ),
        tip(
            id = "setting-f-a-b-shadows",
            title = R.string.fabs_shadow,
            subtitle = R.string.fabs_shadow_sub,
            icon = Icons.Outlined.FabCorner,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.FABShadows),
            pageTitle = R.string.fabs_shadow,
            description = R.string.help_tip_setting_f_a_b_shadows_description,
            steps = listOf(
                R.string.help_tip_setting_f_a_b_shadows_step_1,
                R.string.help_tip_setting_f_a_b_shadows_step_2
            )
        ),
        tip(
            id = "setting-switch-shadows",
            title = R.string.switches_shadow,
            subtitle = R.string.switches_shadow_sub,
            icon = Icons.Outlined.ToggleOff,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.SwitchShadows),
            pageTitle = R.string.switches_shadow,
            description = R.string.help_tip_setting_switch_shadows_description,
            steps = listOf(
                R.string.help_tip_setting_switch_shadows_step_1,
                R.string.help_tip_setting_switch_shadows_step_2
            )
        ),
        tip(
            id = "setting-slider-shadows",
            title = R.string.sliders_shadow,
            subtitle = R.string.sliders_shadow_sub,
            icon = Icons.Rounded.Slider,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.SliderShadows),
            pageTitle = R.string.sliders_shadow,
            description = R.string.help_tip_setting_slider_shadows_description,
            steps = listOf(
                R.string.help_tip_setting_slider_shadows_step_1,
                R.string.help_tip_setting_slider_shadows_step_2
            )
        ),
        tip(
            id = "setting-change-language",
            title = R.string.language,
            subtitle = R.string.help_tip_setting_change_language_description,
            icon = Icons.Rounded.Language,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ChangeLanguage),
            pageTitle = R.string.language,
            description = R.string.help_tip_setting_change_language_description,
            steps = listOf(
                R.string.help_tip_setting_change_language_step_1,
                R.string.help_tip_setting_change_language_step_2
            )
        ),
        tip(
            id = "setting-font-scale",
            title = R.string.font_scale,
            subtitle = R.string.help_tip_setting_font_scale_description,
            icon = Icons.Rounded.TextFields,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.FontScale),
            pageTitle = R.string.font_scale,
            description = R.string.help_tip_setting_font_scale_description,
            steps = listOf(
                R.string.help_tip_setting_font_scale_step_1,
                R.string.help_tip_setting_font_scale_step_2
            )
        ),
        tip(
            id = "setting-show-tools-history",
            title = R.string.show_recent_tools,
            subtitle = R.string.show_recent_tools_sub,
            icon = Icons.Rounded.History,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ShowToolsHistory),
            pageTitle = R.string.show_recent_tools,
            description = R.string.help_tip_setting_show_tools_history_description,
            steps = listOf(
                R.string.help_tip_setting_show_tools_history_step_1,
                R.string.help_tip_setting_show_tools_history_step_2
            )
        ),
        tip(
            id = "setting-screen-search",
            title = R.string.search_option,
            subtitle = R.string.search_option_sub,
            icon = Icons.Outlined.LayersSearchOutline,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ScreenSearch),
            pageTitle = R.string.search_option,
            description = R.string.help_tip_setting_screen_search_description,
            steps = listOf(
                R.string.help_tip_setting_screen_search_step_1,
                R.string.help_tip_setting_screen_search_step_2
            )
        ),
        tip(
            id = "setting-show-help-tips-button",
            title = R.string.show_help_tips_button,
            subtitle = R.string.show_help_tips_button_sub,
            icon = Icons.Outlined.Lightbulb,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ShowHelpTipsButton),
            pageTitle = R.string.show_help_tips_button,
            description = R.string.help_tip_setting_show_help_tips_button_description,
            steps = listOf(
                R.string.help_tip_setting_show_help_tips_button_step_1,
                R.string.help_tip_setting_show_help_tips_button_step_2
            )
        ),
        tip(
            id = "setting-group-options",
            title = R.string.group_tools_by_type,
            subtitle = R.string.group_tools_by_type_sub,
            icon = Icons.Outlined.BatchPrediction,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.GroupOptions),
            pageTitle = R.string.group_tools_by_type,
            description = R.string.help_tip_setting_group_options_description,
            steps = listOf(
                R.string.help_tip_setting_group_options_step_1,
                R.string.help_tip_setting_group_options_step_2
            )
        ),
        tip(
            id = "setting-favorite-tools-in-grouped-mode",
            title = R.string.favorite_tools_in_grouped_mode,
            subtitle = R.string.favorite_tools_in_grouped_mode_sub,
            icon = Icons.Outlined.Bookmark,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.FavoriteToolsInGroupedMode),
            pageTitle = R.string.favorite_tools_in_grouped_mode,
            description = R.string.help_tip_setting_favorite_tools_in_grouped_mode_description,
            steps = listOf(
                R.string.help_tip_setting_favorite_tools_in_grouped_mode_step_1,
                R.string.help_tip_setting_favorite_tools_in_grouped_mode_step_2
            )
        ),
        tip(
            id = "setting-show-favorite-as-last",
            title = R.string.show_favorite_as_last,
            subtitle = R.string.show_favorite_as_last_sub,
            icon = Icons.Outlined.LastPage,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ShowFavoriteAsLast),
            pageTitle = R.string.show_favorite_as_last,
            description = R.string.help_tip_setting_show_favorite_as_last_description,
            steps = listOf(
                R.string.help_tip_setting_show_favorite_as_last_step_1,
                R.string.help_tip_setting_show_favorite_as_last_step_2
            )
        ),
        tip(
            id = "setting-presets",
            title = R.string.presets,
            subtitle = R.string.presets_sub,
            icon = Icons.Rounded.HashTag,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.Presets),
            pageTitle = R.string.presets,
            description = R.string.help_tip_setting_presets_description,
            steps = listOf(
                R.string.help_tip_setting_presets_step_1,
                R.string.help_tip_setting_presets_step_2
            )
        ),
        tip(
            id = "setting-default-quality",
            title = R.string.quality,
            subtitle = R.string.help_tip_setting_default_quality_description,
            icon = Icons.Outlined.ShineDiamond,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.DefaultQuality),
            pageTitle = R.string.quality,
            description = R.string.help_tip_setting_default_quality_description,
            steps = listOf(
                R.string.help_tip_setting_default_quality_step_1,
                R.string.help_tip_setting_default_quality_step_2
            )
        ),
        tip(
            id = "setting-markup-layer-text-defaults",
            title = R.string.markup_layers,
            subtitle = R.string.markup_layer_text_defaults_sub,
            icon = Icons.Outlined.TextSticky,
            category = creativeTools,
            deepLink = Screen.Settings(targetSetting = Setting.MarkupLayerTextDefaults),
            pageTitle = R.string.markup_layers,
            description = R.string.help_tip_setting_markup_layer_text_defaults_description,
            steps = listOf(
                R.string.help_tip_setting_markup_layer_text_defaults_step_1,
                R.string.help_tip_setting_markup_layer_text_defaults_step_2
            )
        ),
        tip(
            id = "setting-lock-draw-orientation",
            title = R.string.lock_draw_orientation,
            subtitle = R.string.lock_draw_orientation_sub,
            icon = Icons.Outlined.MobileRotateLock,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.LockDrawOrientation),
            pageTitle = R.string.lock_draw_orientation,
            description = R.string.help_tip_setting_lock_draw_orientation_description,
            steps = listOf(
                R.string.help_tip_setting_lock_draw_orientation_step_1,
                R.string.help_tip_setting_lock_draw_orientation_step_2
            )
        ),
        tip(
            id = "setting-default-draw-color",
            title = R.string.default_draw_color,
            subtitle = R.string.help_tip_setting_default_draw_color_description,
            icon = Icons.Outlined.BrushColor,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.DefaultDrawColor),
            pageTitle = R.string.default_draw_color,
            description = R.string.help_tip_setting_default_draw_color_description,
            steps = listOf(
                R.string.help_tip_setting_default_draw_color_step_1,
                R.string.help_tip_setting_default_draw_color_step_2
            )
        ),
        tip(
            id = "setting-default-draw-path-mode",
            title = R.string.default_draw_path_mode,
            subtitle = R.string.help_tip_setting_default_draw_path_mode_description,
            icon = Icons.Outlined.TouchApp,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.DefaultDrawPathMode),
            pageTitle = R.string.default_draw_path_mode,
            description = R.string.help_tip_setting_default_draw_path_mode_description,
            steps = listOf(
                R.string.help_tip_setting_default_draw_path_mode_step_1,
                R.string.help_tip_setting_default_draw_path_mode_step_2
            )
        ),
        tip(
            id = "setting-magnifier",
            title = R.string.magnifier,
            subtitle = R.string.magnifier_sub,
            icon = Icons.Outlined.ZoomIn,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.Magnifier),
            pageTitle = R.string.magnifier,
            description = R.string.help_tip_setting_magnifier_description,
            steps = listOf(
                R.string.help_tip_setting_magnifier_step_1,
                R.string.help_tip_setting_magnifier_step_2
            )
        ),
        tip(
            id = "setting-draw-bitmap-border",
            title = R.string.draw_bitmap_border,
            subtitle = R.string.draw_bitmap_border_sub,
            icon = Icons.Outlined.FilterFrames,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.DrawBitmapBorder),
            pageTitle = R.string.draw_bitmap_border,
            description = R.string.help_tip_setting_draw_bitmap_border_description,
            steps = listOf(
                R.string.help_tip_setting_draw_bitmap_border_step_1,
                R.string.help_tip_setting_draw_bitmap_border_step_2
            )
        ),
        tip(
            id = "setting-exif-widget-initial-state",
            title = R.string.force_exif_widget_initial_value,
            subtitle = R.string.force_exif_widget_initial_value_sub,
            icon = Icons.Rounded.DataSaverOff,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.ExifWidgetInitialState),
            pageTitle = R.string.force_exif_widget_initial_value,
            description = R.string.help_tip_setting_exif_widget_initial_state_description,
            steps = listOf(
                R.string.help_tip_setting_exif_widget_initial_state_step_1,
                R.string.help_tip_setting_exif_widget_initial_state_step_2
            )
        ),
        tip(
            id = "setting-keep-date-time",
            title = R.string.keep_date_time,
            subtitle = R.string.keep_date_time_sub,
            icon = Icons.Outlined.AccessTime,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.KeepDateTime),
            pageTitle = R.string.keep_date_time,
            description = R.string.help_tip_setting_keep_date_time_description,
            steps = listOf(
                R.string.help_tip_setting_keep_date_time_step_1,
                R.string.help_tip_setting_keep_date_time_step_2
            )
        ),
        tip(
            id = "setting-add-image-toolbox-metadata",
            title = R.string.add_imagetoolbox_metadata,
            subtitle = R.string.add_imagetoolbox_metadata_sub,
            icon = Icons.Outlined.Info,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.AddImageToolboxMetadata),
            pageTitle = R.string.add_imagetoolbox_metadata,
            description = R.string.help_tip_setting_add_image_toolbox_metadata_description,
            steps = listOf(
                R.string.help_tip_setting_add_image_toolbox_metadata_step_1,
                R.string.help_tip_setting_add_image_toolbox_metadata_step_2
            )
        ),
        tip(
            id = "setting-delete-originals-after-save",
            title = R.string.delete_originals_after_save,
            subtitle = R.string.delete_originals_after_save_sub,
            icon = Icons.Outlined.Delete,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.DeleteOriginalsAfterSave),
            pageTitle = R.string.delete_originals_after_save,
            description = R.string.help_tip_setting_delete_originals_after_save_description,
            steps = listOf(
                R.string.help_tip_setting_delete_originals_after_save_step_1,
                R.string.help_tip_setting_delete_originals_after_save_step_2
            )
        ),
        tip(
            id = "setting-filename-prefix",
            title = R.string.prefix,
            subtitle = R.string.help_tip_setting_filename_prefix_description,
            icon = Icons.Filled.Prefix,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.FilenamePrefix),
            pageTitle = R.string.prefix,
            description = R.string.help_tip_setting_filename_prefix_description,
            steps = listOf(
                R.string.help_tip_setting_filename_prefix_step_1,
                R.string.help_tip_setting_filename_prefix_step_2
            )
        ),
        tip(
            id = "setting-filename-suffix",
            title = R.string.suffix,
            subtitle = R.string.help_tip_setting_filename_suffix_description,
            icon = Icons.Filled.Suffix,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.FilenameSuffix),
            pageTitle = R.string.suffix,
            description = R.string.help_tip_setting_filename_suffix_description,
            steps = listOf(
                R.string.help_tip_setting_filename_suffix_step_1,
                R.string.help_tip_setting_filename_suffix_step_2
            )
        ),
        tip(
            id = "setting-add-file-size",
            title = R.string.add_file_size,
            subtitle = R.string.add_file_size_sub,
            icon = Icons.Outlined.ScaleUnbalanced,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.AddFileSize),
            pageTitle = R.string.add_file_size,
            description = R.string.help_tip_setting_add_file_size_description,
            steps = listOf(
                R.string.help_tip_setting_add_file_size_step_1,
                R.string.help_tip_setting_add_file_size_step_2
            )
        ),
        tip(
            id = "setting-add-original-filename",
            title = R.string.add_original_filename,
            subtitle = R.string.add_original_filename_sub,
            icon = Icons.Outlined.Difference,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.AddOriginalFilename),
            pageTitle = R.string.add_original_filename,
            description = R.string.help_tip_setting_add_original_filename_description,
            steps = listOf(
                R.string.help_tip_setting_add_original_filename_step_1,
                R.string.help_tip_setting_add_original_filename_step_2
            )
        ),
        tip(
            id = "setting-replace-sequence-number",
            title = R.string.replace_sequence_number,
            subtitle = R.string.replace_sequence_number_sub,
            icon = Icons.Filled.Numeric,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.ReplaceSequenceNumber),
            pageTitle = R.string.replace_sequence_number,
            description = R.string.help_tip_setting_replace_sequence_number_description,
            steps = listOf(
                R.string.help_tip_setting_replace_sequence_number_step_1,
                R.string.help_tip_setting_replace_sequence_number_step_2
            )
        ),
        tip(
            id = "setting-add-timestamp-to-filename",
            title = R.string.add_timestamp,
            subtitle = R.string.add_timestamp_sub,
            icon = Icons.Outlined.Timer,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.AddTimestampToFilename),
            pageTitle = R.string.add_timestamp,
            description = R.string.help_tip_setting_add_timestamp_to_filename_description,
            steps = listOf(
                R.string.help_tip_setting_add_timestamp_to_filename_step_1,
                R.string.help_tip_setting_add_timestamp_to_filename_step_2
            )
        ),
        tip(
            id = "setting-use-formatted-filename-timestamp",
            title = R.string.formatted_timestamp,
            subtitle = R.string.formatted_timestamp_sub,
            icon = Icons.Rounded.FormatParagraph,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.UseFormattedFilenameTimestamp),
            pageTitle = R.string.formatted_timestamp,
            description = R.string.help_tip_setting_use_formatted_filename_timestamp_description,
            steps = listOf(
                R.string.help_tip_setting_use_formatted_filename_timestamp_step_1,
                R.string.help_tip_setting_use_formatted_filename_timestamp_step_2
            )
        ),
        tip(
            id = "setting-add-preset-to-filename",
            title = R.string.add_preset_to_filename,
            subtitle = R.string.add_preset_to_filename_sub,
            icon = Icons.Outlined.LabelPercent,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.AddPresetToFilename),
            pageTitle = R.string.add_preset_to_filename,
            description = R.string.help_tip_setting_add_preset_to_filename_description,
            steps = listOf(
                R.string.help_tip_setting_add_preset_to_filename_step_1,
                R.string.help_tip_setting_add_preset_to_filename_step_2
            )
        ),
        tip(
            id = "setting-add-image-scale-mode-to-filename",
            title = R.string.add_image_scale_mode_to_filename,
            subtitle = R.string.add_image_scale_mode_to_filename_sub,
            icon = Icons.Outlined.FitScreen,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.AddImageScaleModeToFilename),
            pageTitle = R.string.add_image_scale_mode_to_filename,
            description = R.string.help_tip_setting_add_image_scale_mode_to_filename_description,
            steps = listOf(
                R.string.help_tip_setting_add_image_scale_mode_to_filename_step_1,
                R.string.help_tip_setting_add_image_scale_mode_to_filename_step_2
            )
        ),
        tip(
            id = "setting-checksum-as-filename",
            title = R.string.checksum_as_filename,
            subtitle = R.string.checksum_as_filename_sub,
            icon = Icons.Rounded.HashTag,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.ChecksumAsFilename),
            pageTitle = R.string.checksum_as_filename,
            description = R.string.help_tip_setting_checksum_as_filename_description,
            steps = listOf(
                R.string.help_tip_setting_checksum_as_filename_step_1,
                R.string.help_tip_setting_checksum_as_filename_step_2
            )
        ),
        tip(
            id = "setting-randomize-filename",
            title = R.string.randomize_filename,
            subtitle = R.string.randomize_filename_sub,
            icon = Icons.Rounded.Symbol,
            category = filesAndMetadata,
            deepLink = Screen.Settings(targetSetting = Setting.RandomizeFilename),
            pageTitle = R.string.randomize_filename,
            description = R.string.help_tip_setting_randomize_filename_description,
            steps = listOf(
                R.string.help_tip_setting_randomize_filename_step_1,
                R.string.help_tip_setting_randomize_filename_step_2
            )
        ),
        tip(
            id = "setting-clear-cache",
            title = R.string.cache,
            subtitle = R.string.cache_size,
            icon = Icons.Outlined.Memory,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.ClearCache),
            pageTitle = R.string.cache,
            description = R.string.help_tip_setting_clear_cache_description,
            steps = listOf(
                R.string.help_tip_setting_clear_cache_step_1,
                R.string.help_tip_setting_clear_cache_step_2
            )
        ),
        tip(
            id = "setting-auto-cache-clear",
            title = R.string.auto_cache_clearing,
            subtitle = R.string.auto_cache_clearing_sub,
            icon = Icons.Outlined.AutoDelete,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.AutoCacheClear),
            pageTitle = R.string.auto_cache_clearing,
            description = R.string.help_tip_setting_auto_cache_clear_description,
            steps = listOf(
                R.string.help_tip_setting_auto_cache_clear_step_1,
                R.string.help_tip_setting_auto_cache_clear_step_2
            )
        ),
        tip(
            id = "setting-cache-auto-clear-interval",
            title = R.string.cache_auto_clear_interval,
            subtitle = R.string.cache_auto_clear_interval_sub,
            icon = Icons.Outlined.AvTimer,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.CacheAutoClearInterval),
            pageTitle = R.string.cache_auto_clear_interval,
            description = R.string.help_tip_setting_cache_auto_clear_interval_description,
            steps = listOf(
                R.string.help_tip_setting_cache_auto_clear_interval_step_1,
                R.string.help_tip_setting_cache_auto_clear_interval_step_2
            )
        ),
        tip(
            id = "setting-restore",
            title = R.string.restore,
            subtitle = R.string.restore_sub,
            icon = Icons.Outlined.DownloadFile,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.Restore),
            pageTitle = R.string.restore,
            description = R.string.help_tip_setting_restore_description,
            steps = listOf(
                R.string.help_tip_setting_restore_step_1,
                R.string.help_tip_setting_restore_step_2
            )
        ),
        tip(
            id = "setting-reset",
            title = R.string.reset,
            subtitle = R.string.reset_settings_sub,
            icon = Icons.Rounded.RestartAlt,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.Reset),
            pageTitle = R.string.reset,
            description = R.string.help_tip_setting_reset_description,
            steps = listOf(
                R.string.help_tip_setting_reset_step_1,
                R.string.help_tip_setting_reset_step_2
            )
        ),
        tip(
            id = "setting-crashlytics",
            title = R.string.crashlytics,
            subtitle = R.string.crashlytics_sub,
            icon = Icons.Rounded.Crashlytics,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.Crashlytics),
            pageTitle = R.string.crashlytics,
            description = R.string.help_tip_setting_crashlytics_description,
            steps = listOf(
                R.string.help_tip_setting_crashlytics_step_1,
                R.string.help_tip_setting_crashlytics_step_2
            )
        ),
        tip(
            id = "setting-analytics",
            title = R.string.analytics,
            subtitle = R.string.analytics_sub,
            icon = Icons.Rounded.Analytics,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.Analytics),
            pageTitle = R.string.analytics,
            description = R.string.help_tip_setting_analytics_description,
            steps = listOf(
                R.string.help_tip_setting_analytics_step_1,
                R.string.help_tip_setting_analytics_step_2
            )
        ),
        tip(
            id = "setting-auto-check-updates",
            title = R.string.check_updates,
            subtitle = R.string.check_updates_sub,
            icon = Icons.Outlined.ReleaseAlert,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.AutoCheckUpdates),
            pageTitle = R.string.check_updates,
            description = R.string.help_tip_setting_auto_check_updates_description,
            steps = listOf(
                R.string.help_tip_setting_auto_check_updates_step_1,
                R.string.help_tip_setting_auto_check_updates_step_2
            )
        ),
        tip(
            id = "setting-allow-betas",
            title = R.string.allow_betas,
            subtitle = R.string.allow_betas_sub,
            icon = Icons.Rounded.Beta,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.AllowBetas),
            pageTitle = R.string.allow_betas,
            description = R.string.help_tip_setting_allow_betas_description,
            steps = listOf(
                R.string.help_tip_setting_allow_betas_step_1,
                R.string.help_tip_setting_allow_betas_step_2
            )
        ),
        tip(
            id = "setting-check-updates-button",
            title = R.string.check_updates,
            subtitle = R.string.check_updates_sub,
            icon = Icons.Outlined.SyncArrowDown,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.CheckUpdatesButton),
            pageTitle = R.string.check_updates,
            description = R.string.help_tip_setting_check_updates_button_description,
            steps = listOf(
                R.string.help_tip_setting_check_updates_button_step_1,
                R.string.help_tip_setting_check_updates_button_step_2
            )
        ),
        tip(
            id = "setting-current-version-code",
            title = R.string.app_name,
            subtitle = R.string.version,
            icon = Icons.Outlined.Verified,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.CurrentVersionCode),
            pageTitle = R.string.app_name,
            description = R.string.help_tip_setting_current_version_code_description,
            steps = listOf(
                R.string.help_tip_setting_current_version_code_step_1,
                R.string.help_tip_setting_current_version_code_step_2
            )
        ),
        tip(
            id = "setting-app-usage-statistics",
            title = R.string.usage_statistics,
            subtitle = R.string.usage_statistics_sub,
            icon = Icons.Outlined.FinanceMode,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.AppUsageStatistics),
            pageTitle = R.string.usage_statistics,
            description = R.string.help_tip_setting_app_usage_statistics_description,
            steps = listOf(
                R.string.help_tip_setting_app_usage_statistics_step_1,
                R.string.help_tip_setting_app_usage_statistics_step_2
            )
        ),
        tip(
            id = "setting-app-logs",
            title = R.string.app_logs,
            subtitle = R.string.app_logs_sub,
            icon = Icons.Outlined.Book2,
            category = troubleshooting,
            deepLink = Screen.Settings(targetSetting = Setting.AppLogs),
            pageTitle = R.string.app_logs,
            description = R.string.help_tip_setting_app_logs_description,
            steps = listOf(
                R.string.help_tip_setting_app_logs_step_1,
                R.string.help_tip_setting_app_logs_step_2
            )
        ),
        tip(
            id = "setting-open-source-licenses",
            title = R.string.open_source_licenses,
            subtitle = R.string.open_source_licenses_sub,
            icon = Icons.Outlined.License,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.OpenSourceLicenses),
            pageTitle = R.string.open_source_licenses,
            description = R.string.help_tip_setting_open_source_licenses_description,
            steps = listOf(
                R.string.help_tip_setting_open_source_licenses_step_1,
                R.string.help_tip_setting_open_source_licenses_step_2
            )
        ),
        tip(
            id = "setting-help-translate",
            title = R.string.help_translate,
            subtitle = R.string.help_translate_sub,
            icon = Icons.Rounded.Translate,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.HelpTranslate),
            pageTitle = R.string.help_translate,
            description = R.string.help_tip_setting_help_translate_description,
            steps = listOf(
                R.string.help_tip_setting_help_translate_step_1,
                R.string.help_tip_setting_help_translate_step_2
            )
        ),
        tip(
            id = "setting-issue-tracker",
            title = R.string.issue_tracker,
            subtitle = R.string.issue_tracker_sub,
            icon = Icons.Outlined.BugReport,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.IssueTracker),
            pageTitle = R.string.issue_tracker,
            description = R.string.help_tip_setting_issue_tracker_description,
            steps = listOf(
                R.string.help_tip_setting_issue_tracker_step_1,
                R.string.help_tip_setting_issue_tracker_step_2
            )
        ),
        tip(
            id = "setting-free-software-partner",
            title = R.string.free_software_partner,
            subtitle = R.string.free_software_partner_sub,
            icon = Icons.Outlined.HandshakeAlt,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.FreeSoftwarePartner),
            pageTitle = R.string.free_software_partner,
            description = R.string.help_tip_setting_free_software_partner_description,
            steps = listOf(
                R.string.help_tip_setting_free_software_partner_step_1,
                R.string.help_tip_setting_free_software_partner_step_2
            )
        ),
        tip(
            id = "setting-telegram-group",
            title = R.string.tg_chat,
            subtitle = R.string.tg_chat_sub,
            icon = Icons.Rounded.Telegram,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.TelegramGroup),
            pageTitle = R.string.tg_chat,
            description = R.string.help_tip_setting_telegram_group_description,
            steps = listOf(
                R.string.help_tip_setting_telegram_group_step_1,
                R.string.help_tip_setting_telegram_group_step_2
            )
        ),
        tip(
            id = "setting-telegram-channel",
            title = R.string.ci_channel,
            subtitle = R.string.ci_channel_sub,
            icon = Icons.Rounded.RssFeed,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.TelegramChannel),
            pageTitle = R.string.ci_channel,
            description = R.string.help_tip_setting_telegram_channel_description,
            steps = listOf(
                R.string.help_tip_setting_telegram_channel_step_1,
                R.string.help_tip_setting_telegram_channel_step_2
            )
        ),
        tip(
            id = "setting-source-code",
            title = R.string.check_source_code,
            subtitle = R.string.check_source_code_sub,
            icon = Icons.Rounded.Github,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.SourceCode),
            pageTitle = R.string.check_source_code,
            description = R.string.help_tip_setting_source_code_description,
            steps = listOf(
                R.string.help_tip_setting_source_code_step_1,
                R.string.help_tip_setting_source_code_step_2
            )
        ),
        tip(
            id = "setting-help-tips",
            title = R.string.help_tips,
            subtitle = R.string.help_tips_settings_sub,
            icon = Icons.TwoTone.Lightbulb,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.HelpTips),
            pageTitle = R.string.help_tips,
            description = R.string.help_tip_setting_help_tips_description,
            steps = listOf(
                R.string.help_tip_setting_help_tips_step_1,
                R.string.help_tip_setting_help_tips_step_2
            )
        ),
        tip(
            id = "setting-debug-menu",
            title = R.string.debug_menu,
            subtitle = R.string.debug_menu_sub,
            icon = Icons.TwoTone.BugReport,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.DebugMenu),
            pageTitle = R.string.debug_menu,
            description = R.string.help_tip_setting_debug_menu_description,
            steps = listOf(
                R.string.help_tip_setting_debug_menu_step_1,
                R.string.help_tip_setting_debug_menu_step_2
            )
        ),
        tip(
            id = "setting-auto-pin-clipboard-only-clip",
            title = R.string.only_clip,
            subtitle = R.string.only_clip_sub,
            icon = Icons.Outlined.SaveAs,
            category = textAndData,
            deepLink = Screen.Settings(targetSetting = Setting.AutoPinClipboardOnlyClip),
            pageTitle = R.string.only_clip,
            description = R.string.help_tip_setting_auto_pin_clipboard_only_clip_description,
            steps = listOf(
                R.string.help_tip_setting_auto_pin_clipboard_only_clip_step_1,
                R.string.help_tip_setting_auto_pin_clipboard_only_clip_step_2
            )
        ),
        tip(
            id = "setting-vibration-strength",
            title = R.string.vibration_strength,
            subtitle = R.string.help_tip_setting_vibration_strength_description,
            icon = Icons.Outlined.Exercise,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.VibrationStrength),
            pageTitle = R.string.vibration_strength,
            description = R.string.help_tip_setting_vibration_strength_description,
            steps = listOf(
                R.string.help_tip_setting_vibration_strength_step_1,
                R.string.help_tip_setting_vibration_strength_step_2
            )
        ),
        tip(
            id = "setting-brightness-enforcement",
            title = R.string.brightness_enforcement,
            subtitle = R.string.help_tip_setting_brightness_enforcement_description,
            icon = Icons.Outlined.BrightnessHigh,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.BrightnessEnforcement),
            pageTitle = R.string.brightness_enforcement,
            description = R.string.help_tip_setting_brightness_enforcement_description,
            steps = listOf(
                R.string.help_tip_setting_brightness_enforcement_step_1,
                R.string.help_tip_setting_brightness_enforcement_step_2
            )
        ),
        tip(
            id = "setting-system-bars-visibility",
            title = R.string.system_bars_visibility,
            subtitle = R.string.help_tip_setting_system_bars_visibility_description,
            icon = Icons.Outlined.TelevisionAmbientLight,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.SystemBarsVisibility),
            pageTitle = R.string.system_bars_visibility,
            description = R.string.help_tip_setting_system_bars_visibility_description,
            steps = listOf(
                R.string.help_tip_setting_system_bars_visibility_step_1,
                R.string.help_tip_setting_system_bars_visibility_step_2
            )
        ),
        tip(
            id = "setting-show-system-bars-by-swipe",
            title = R.string.show_system_bars_by_swipe,
            subtitle = R.string.show_system_bars_by_swipe_sub,
            icon = Icons.Outlined.SwipeVertical,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ShowSystemBarsBySwipe),
            pageTitle = R.string.show_system_bars_by_swipe,
            description = R.string.help_tip_setting_show_system_bars_by_swipe_description,
            steps = listOf(
                R.string.help_tip_setting_show_system_bars_by_swipe_step_1,
                R.string.help_tip_setting_show_system_bars_by_swipe_step_2
            )
        ),
        tip(
            id = "setting-emoji",
            title = R.string.emoji,
            subtitle = R.string.emoji_sub,
            icon = Icons.Outlined.Cool,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.Emoji),
            pageTitle = R.string.emoji,
            description = R.string.help_tip_setting_emoji_description,
            steps = listOf(
                R.string.help_tip_setting_emoji_step_1,
                R.string.help_tip_setting_emoji_step_2
            )
        ),
        tip(
            id = "setting-emojis-count",
            title = R.string.emojis_count,
            subtitle = R.string.help_tip_setting_emojis_count_description,
            icon = Icons.Outlined.EmojiMultiple,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.EmojisCount),
            pageTitle = R.string.emojis_count,
            description = R.string.help_tip_setting_emojis_count_description,
            steps = listOf(
                R.string.help_tip_setting_emojis_count_step_1,
                R.string.help_tip_setting_emojis_count_step_2
            )
        ),
        tip(
            id = "setting-use-animated-emojis",
            title = R.string.animated_emojis,
            subtitle = R.string.animated_emojis_sub,
            icon = Icons.Outlined.MotionMode,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.UseAnimatedEmojis),
            pageTitle = R.string.animated_emojis,
            description = R.string.help_tip_setting_use_animated_emojis_description,
            steps = listOf(
                R.string.help_tip_setting_use_animated_emojis_step_1,
                R.string.help_tip_setting_use_animated_emojis_step_2
            )
        ),
        tip(
            id = "setting-use-random-emojis",
            title = R.string.random_emojis,
            subtitle = R.string.random_emojis_sub,
            icon = Icons.Outlined.Casino,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.UseRandomEmojis),
            pageTitle = R.string.random_emojis,
            description = R.string.help_tip_setting_use_random_emojis_description,
            steps = listOf(
                R.string.help_tip_setting_use_random_emojis_step_1,
                R.string.help_tip_setting_use_random_emojis_step_2
            )
        ),
        tip(
            id = "setting-confetti",
            title = R.string.confetti,
            subtitle = R.string.confetti_sub,
            icon = Icons.Outlined.Celebration,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.Confetti),
            pageTitle = R.string.confetti,
            description = R.string.help_tip_setting_confetti_description,
            steps = listOf(
                R.string.help_tip_setting_confetti_step_1,
                R.string.help_tip_setting_confetti_step_2
            )
        ),
        tip(
            id = "setting-confetti-harmonizer",
            title = R.string.harmonization_color,
            subtitle = R.string.help_tip_setting_confetti_harmonizer_description,
            icon = Icons.Outlined.Palette,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ConfettiHarmonizer),
            pageTitle = R.string.harmonization_color,
            description = R.string.help_tip_setting_confetti_harmonizer_description,
            steps = listOf(
                R.string.help_tip_setting_confetti_harmonizer_step_1,
                R.string.help_tip_setting_confetti_harmonizer_step_2
            )
        ),
        tip(
            id = "setting-confetti-harmonization-level",
            title = R.string.harmonization_level,
            subtitle = R.string.help_tip_setting_confetti_harmonization_level_description,
            icon = Icons.Outlined.Exercise,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ConfettiHarmonizationLevel),
            pageTitle = R.string.harmonization_level,
            description = R.string.help_tip_setting_confetti_harmonization_level_description,
            steps = listOf(
                R.string.help_tip_setting_confetti_harmonization_level_step_1,
                R.string.help_tip_setting_confetti_harmonization_level_step_2
            )
        ),
        tip(
            id = "setting-confetti-type",
            title = R.string.confetti_type,
            subtitle = R.string.help_tip_setting_confetti_type_description,
            icon = Icons.Rounded.Emergency,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ConfettiType),
            pageTitle = R.string.confetti_type,
            description = R.string.help_tip_setting_confetti_type_description,
            steps = listOf(
                R.string.help_tip_setting_confetti_type_step_1,
                R.string.help_tip_setting_confetti_type_step_2
            )
        ),
        tip(
            id = "setting-return-to-external-app-after-save",
            title = R.string.return_to_external_app_after_save,
            subtitle = R.string.return_to_external_app_after_save_sub,
            icon = Icons.Outlined.DoorBack,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ReturnToExternalAppAfterSave),
            pageTitle = R.string.return_to_external_app_after_save,
            description = R.string.help_tip_setting_return_to_external_app_after_save_description,
            steps = listOf(
                R.string.help_tip_setting_return_to_external_app_after_save_step_1,
                R.string.help_tip_setting_return_to_external_app_after_save_step_2
            )
        ),
        tip(
            id = "setting-show-settings-in-landscape",
            title = R.string.show_settings_in_landscape,
            subtitle = R.string.show_settings_in_landscape_sub,
            icon = Icons.Outlined.MobileLandscape,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ShowSettingsInLandscape),
            pageTitle = R.string.show_settings_in_landscape,
            description = R.string.help_tip_setting_show_settings_in_landscape_description,
            steps = listOf(
                R.string.help_tip_setting_show_settings_in_landscape_step_1,
                R.string.help_tip_setting_show_settings_in_landscape_step_2
            )
        ),
        tip(
            id = "setting-fast-settings-side",
            title = R.string.fast_settings_side,
            subtitle = R.string.fast_settings_side_sub,
            icon = Icons.Outlined.SettingsTimelapse,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.FastSettingsSide),
            pageTitle = R.string.fast_settings_side,
            description = R.string.help_tip_setting_fast_settings_side_description,
            steps = listOf(
                R.string.help_tip_setting_fast_settings_side_step_1,
                R.string.help_tip_setting_fast_settings_side_step_2
            )
        ),
        tip(
            id = "setting-ai-assistant",
            title = R.string.ai_assistant,
            subtitle = R.string.ai_assistant_subtitle,
            icon = Icons.Outlined.Neurology,
            category = gettingStarted,
            deepLink = Screen.Settings(targetSetting = Setting.AiAssistant),
            pageTitle = R.string.ai_assistant,
            description = R.string.help_tip_setting_ai_assistant_description,
            steps = listOf(
                R.string.help_tip_setting_ai_assistant_step_1,
                R.string.help_tip_setting_ai_assistant_step_2,
                R.string.help_tip_setting_ai_assistant_step_3
            )
        ),
        tip(
            id = "setting-snowfall-mode",
            title = R.string.snowfall_mode,
            subtitle = R.string.help_tip_setting_snowfall_mode_description,
            icon = Icons.Outlined.Snowflake,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.SnowfallMode),
            pageTitle = R.string.snowfall_mode,
            description = R.string.help_tip_setting_snowfall_mode_description,
            steps = listOf(
                R.string.help_tip_setting_snowfall_mode_step_1,
                R.string.help_tip_setting_snowfall_mode_step_2
            )
        ),
        tip(
            id = "setting-enable-links-preview",
            title = R.string.links_preview,
            subtitle = R.string.links_preview_sub,
            icon = Icons.Rounded.Link,
            category = textAndData,
            deepLink = Screen.Settings(targetSetting = Setting.EnableLinksPreview),
            pageTitle = R.string.links_preview,
            description = R.string.help_tip_setting_enable_links_preview_description,
            steps = listOf(
                R.string.help_tip_setting_enable_links_preview_step_1,
                R.string.help_tip_setting_enable_links_preview_step_2
            )
        ),
        tip(
            id = "setting-enable-sheet-gestures",
            title = R.string.sheet_gestures,
            subtitle = R.string.sheet_gestures_sub,
            icon = Icons.Outlined.SwipeDown,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.EnableSheetGestures),
            pageTitle = R.string.sheet_gestures,
            description = R.string.help_tip_setting_enable_sheet_gestures_description,
            steps = listOf(
                R.string.help_tip_setting_enable_sheet_gestures_step_1,
                R.string.help_tip_setting_enable_sheet_gestures_step_2
            )
        ),
        tip(
            id = "setting-sheet-nested-scroll",
            title = R.string.sheet_nested_scroll,
            subtitle = R.string.sheet_nested_scroll_sub,
            icon = Icons.Outlined.TiltArrowDown,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.SheetNestedScroll),
            pageTitle = R.string.sheet_nested_scroll,
            description = R.string.help_tip_setting_sheet_nested_scroll_description,
            steps = listOf(
                R.string.help_tip_setting_sheet_nested_scroll_step_1,
                R.string.help_tip_setting_sheet_nested_scroll_step_2
            )
        ),
        tip(
            id = "setting-raw-use-embedded-preview",
            title = R.string.raw_use_embedded_preview,
            subtitle = R.string.raw_use_embedded_preview_sub,
            icon = Icons.Outlined.Preview,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawUseEmbeddedPreview),
            pageTitle = R.string.raw_use_embedded_preview,
            description = R.string.help_tip_setting_raw_use_embedded_preview_description,
            steps = listOf(
                R.string.help_tip_setting_raw_use_embedded_preview_step_1,
                R.string.help_tip_setting_raw_use_embedded_preview_step_2
            )
        ),
        tip(
            id = "setting-raw-output-color-space",
            title = R.string.raw_output_color_space,
            subtitle = R.string.help_tip_setting_raw_output_color_space_description,
            icon = Icons.Outlined.Palette,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawOutputColorSpace),
            pageTitle = R.string.raw_output_color_space,
            description = R.string.help_tip_setting_raw_output_color_space_description,
            steps = listOf(
                R.string.help_tip_setting_raw_output_color_space_step_1,
                R.string.help_tip_setting_raw_output_color_space_step_2
            )
        ),
        tip(
            id = "setting-raw-highlight-recovery",
            title = R.string.raw_highlight_recovery,
            subtitle = R.string.help_tip_setting_raw_highlight_recovery_description,
            icon = Icons.Outlined.Highlight,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawHighlightRecovery),
            pageTitle = R.string.raw_highlight_recovery,
            description = R.string.help_tip_setting_raw_highlight_recovery_description,
            steps = listOf(
                R.string.help_tip_setting_raw_highlight_recovery_step_1,
                R.string.help_tip_setting_raw_highlight_recovery_step_2
            )
        ),
        tip(
            id = "setting-raw-exposure-compensation",
            title = R.string.raw_exposure_compensation,
            subtitle = R.string.help_tip_setting_raw_exposure_compensation_description,
            icon = Icons.Outlined.LightMode,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawExposureCompensation),
            pageTitle = R.string.raw_exposure_compensation,
            description = R.string.help_tip_setting_raw_exposure_compensation_description,
            steps = listOf(
                R.string.help_tip_setting_raw_exposure_compensation_step_1,
                R.string.help_tip_setting_raw_exposure_compensation_step_2
            )
        ),
        tip(
            id = "setting-raw-highlight-preservation",
            title = R.string.raw_highlight_preservation,
            subtitle = R.string.help_tip_setting_raw_highlight_preservation_description,
            icon = Icons.Rounded.Contrast,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawHighlightPreservation),
            pageTitle = R.string.raw_highlight_preservation,
            description = R.string.help_tip_setting_raw_highlight_preservation_description,
            steps = listOf(
                R.string.help_tip_setting_raw_highlight_preservation_step_1,
                R.string.help_tip_setting_raw_highlight_preservation_step_2
            )
        ),
        tip(
            id = "setting-raw-auto-brightness",
            title = R.string.raw_auto_brightness,
            subtitle = R.string.raw_auto_brightness_sub,
            icon = Icons.Outlined.BrightnessAuto,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawAutoBrightness),
            pageTitle = R.string.raw_auto_brightness,
            description = R.string.help_tip_setting_raw_auto_brightness_description,
            steps = listOf(
                R.string.help_tip_setting_raw_auto_brightness_step_1,
                R.string.help_tip_setting_raw_auto_brightness_step_2
            )
        ),
        tip(
            id = "setting-raw-demosaic-quality",
            title = R.string.raw_demosaic_quality,
            subtitle = R.string.raw_demosaic_quality_sub,
            icon = Icons.Outlined.QualityHigh,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawDemosaicQuality),
            pageTitle = R.string.raw_demosaic_quality,
            description = R.string.help_tip_setting_raw_demosaic_quality_description,
            steps = listOf(
                R.string.help_tip_setting_raw_demosaic_quality_step_1,
                R.string.help_tip_setting_raw_demosaic_quality_step_2
            )
        ),
        tip(
            id = "setting-raw-half-size",
            title = R.string.raw_half_size,
            subtitle = R.string.raw_half_size_sub,
            icon = Icons.Outlined.PhotoSizeSelectSmall,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawHalfSize),
            pageTitle = R.string.raw_half_size,
            description = R.string.help_tip_setting_raw_half_size_description,
            steps = listOf(
                R.string.help_tip_setting_raw_half_size_step_1,
                R.string.help_tip_setting_raw_half_size_step_2
            )
        ),
        tip(
            id = "setting-raw-apply-orientation",
            title = R.string.raw_apply_orientation,
            subtitle = R.string.raw_apply_orientation_sub,
            icon = Icons.Rounded.ScreenRotationAlt,
            category = imageEditing,
            deepLink = Screen.Settings(targetSetting = Setting.RawApplyOrientation),
            pageTitle = R.string.raw_apply_orientation,
            description = R.string.help_tip_setting_raw_apply_orientation_description,
            steps = listOf(
                R.string.help_tip_setting_raw_apply_orientation_step_1,
                R.string.help_tip_setting_raw_apply_orientation_step_2
            )
        ),
        tip(
            id = "image-opacity",
            title = R.string.help_tip_image_opacity_title,
            subtitle = R.string.help_tip_image_opacity_subtitle,
            icon = Icons.Outlined.AutoFixHigh,
            category = imageEditing,
            deepLink = Screen.Filter(type = Screen.Filter.Type.Basic(filterKey = "opacity")),
            pageTitle = R.string.help_tip_image_opacity_title,
            description = R.string.help_tip_image_opacity_description,
            steps = listOf(
                R.string.help_tip_image_opacity_step_1,
                R.string.help_tip_image_opacity_step_2,
                R.string.help_tip_image_opacity_step_3
            )
        ),
        tip(
            id = "interface-shadows",
            title = R.string.help_tip_interface_shadows_title,
            subtitle = R.string.help_tip_interface_shadows_subtitle,
            icon = Icons.Outlined.Shadow,
            category = customization,
            deepLink = Screen.Settings(targetSetting = Setting.ContainerShadows),
            pageTitle = R.string.help_tip_interface_shadows_title,
            description = R.string.help_tip_interface_shadows_description,
            steps = listOf(
                R.string.help_tip_interface_shadows_step_1,
                R.string.help_tip_interface_shadows_step_2,
                R.string.help_tip_interface_shadows_step_3
            )
        )
    ).plus(
        UiFilter.groups.flatMap { group ->
            group.filters(canAddTemplates = true).map { filter ->
                val key = appContext.resources.getResourceEntryName(filter.title)
                tip(
                    id = "filter-$key",
                    title = filter.title,
                    subtitle = group.title,
                    icon = group.icon,
                    category = filters,
                    deepLink = Screen.Filter(Screen.Filter.Type.Basic(filterKey = key)),
                    pageTitle = filter.title,
                    description = filter.helpDescription,
                    steps = emptyList()
                )
            }
        }
    ).filter { tip ->
        val setting = (tip.deepLink as? Screen.Settings)?.targetSetting
        setting == null || SettingsGroup.entries.any { setting in it.settingsList }
    }

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
