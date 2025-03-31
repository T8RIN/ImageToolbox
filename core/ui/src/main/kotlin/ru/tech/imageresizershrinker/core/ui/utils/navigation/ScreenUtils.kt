/*
 * ImageToolbox is Screen.an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this Screen.file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is Screen.distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this Screen.program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.ui.utils.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.BrandingWatermark
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterHdr
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.FilterHdr
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.GifBox
import androidx.compose.material.icons.outlined.Gradient
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ApngBox
import ru.tech.imageresizershrinker.core.resources.icons.Base64
import ru.tech.imageresizershrinker.core.resources.icons.Collage
import ru.tech.imageresizershrinker.core.resources.icons.CropSmall
import ru.tech.imageresizershrinker.core.resources.icons.Draw
import ru.tech.imageresizershrinker.core.resources.icons.Encrypted
import ru.tech.imageresizershrinker.core.resources.icons.Eraser
import ru.tech.imageresizershrinker.core.resources.icons.Exif
import ru.tech.imageresizershrinker.core.resources.icons.ExifEdit
import ru.tech.imageresizershrinker.core.resources.icons.ImageCombine
import ru.tech.imageresizershrinker.core.resources.icons.ImageConvert
import ru.tech.imageresizershrinker.core.resources.icons.ImageDownload
import ru.tech.imageresizershrinker.core.resources.icons.ImageEdit
import ru.tech.imageresizershrinker.core.resources.icons.ImageLimit
import ru.tech.imageresizershrinker.core.resources.icons.ImageOverlay
import ru.tech.imageresizershrinker.core.resources.icons.ImageText
import ru.tech.imageresizershrinker.core.resources.icons.ImageWeight
import ru.tech.imageresizershrinker.core.resources.icons.Jxl
import ru.tech.imageresizershrinker.core.resources.icons.MiniEditLarge
import ru.tech.imageresizershrinker.core.resources.icons.MultipleImageEdit
import ru.tech.imageresizershrinker.core.resources.icons.PaletteSwatch
import ru.tech.imageresizershrinker.core.resources.icons.SplitAlt
import ru.tech.imageresizershrinker.core.resources.icons.Stack
import ru.tech.imageresizershrinker.core.resources.icons.Toolbox
import ru.tech.imageresizershrinker.core.resources.icons.VectorPolyline
import ru.tech.imageresizershrinker.core.resources.icons.WebpBox
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ApngTools
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.AudioCoverExtractor
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Base64Tools
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ChecksumTools
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Cipher
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.CollageMaker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ColorTools
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Compare
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Crop
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.DeleteExif
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.DocumentScanner
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Draw
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.EasterEgg
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.EditExif
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.EraseBackground
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Filter
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.FormatConversion
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.GeneratePalette
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.GifTools
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.GradientMaker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ImageCutter
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ImagePreview
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ImageSplitting
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ImageStacking
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ImageStitching
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.JxlTools
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.LibrariesInfo
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.LimitResize
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.LoadNetImage
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Main
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.MarkupLayers
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.MeshGradients
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.NoiseGeneration
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.PdfTools
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.PickColorFromImage
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.RecognizeText
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ResizeAndConvert
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.ScanQrCode
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Settings
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.SingleEdit
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.SvgMaker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Watermarking
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.WebpTools
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.WeightResize
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen.Zip
import android.net.Uri as AndroidUri

internal fun Screen.isBetaFeature(): Boolean = when (this) {
    is MarkupLayers -> true
    else -> false
}

internal fun Screen.simpleName(): String = when (this) {
    is ApngTools -> "APNG_Tools"
    is Cipher -> "Cipher"
    is Compare -> "Compare"
    is Crop -> "Crop"
    is DeleteExif -> "Delete_Exif"
    is Draw -> "Draw"
    is EasterEgg -> "Easter_Egg"
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
    is Main -> "Main"
    is PdfTools -> "PDF_Tools"
    is PickColorFromImage -> "Pick_Color_From_Image"
    is RecognizeText -> "Recognize_Text"
    is ResizeAndConvert -> "Resize_And_Convert"
    is WeightResize -> "Resize_By_Bytes"
    is Settings -> "Settings"
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
    is LibrariesInfo -> "Libraries_Info"
    is MarkupLayers -> "Markup_Layers"
    is Base64Tools -> "Base64_Tools"
    is ChecksumTools -> "Checksum_Tools"
    is MeshGradients -> "Mesh_Gradients"
    is EditExif -> "Edit_EXIF"
    is ImageCutter -> "Image_Cutting"
    is AudioCoverExtractor -> "Audio_Cover_Extractor"
}

internal fun Screen.icon(): ImageVector? = when (this) {
    is EasterEgg,
    is Main,
    is Settings,
    is LibrariesInfo,
    is MeshGradients -> null

    is SingleEdit -> Icons.Outlined.ImageEdit
    is ApngTools -> Icons.Rounded.ApngBox
    is Cipher -> Icons.Outlined.Encrypted
    is Compare -> Icons.Rounded.Compare
    is Crop -> Icons.Rounded.CropSmall
    is DeleteExif -> Icons.Outlined.Exif
    is Draw -> Icons.Outlined.Draw
    is EraseBackground -> Icons.Rounded.Eraser
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
    is SvgMaker -> Icons.Outlined.VectorPolyline
    is FormatConversion -> Icons.Outlined.ImageConvert
    is DocumentScanner -> Icons.Outlined.DocumentScanner
    is ScanQrCode -> Icons.Outlined.QrCode
    is ImageStacking -> Icons.Outlined.ImageOverlay
    is ImageSplitting -> Icons.Outlined.SplitAlt
    is ColorTools -> Icons.Outlined.ColorLens
    is WebpTools -> Icons.Rounded.WebpBox
    is NoiseGeneration -> Icons.Outlined.Grain
    is CollageMaker -> Icons.Outlined.Collage
    is MarkupLayers -> Icons.Outlined.Stack
    is Base64Tools -> Icons.Outlined.Base64
    is ChecksumTools -> Icons.Rounded.Tag
    is EditExif -> Icons.Outlined.ExifEdit
    is ImageCutter -> Icons.Rounded.ContentCut
    is AudioCoverExtractor -> Icons.Outlined.Album
}

internal object UriSerializer : KSerializer<AndroidUri> {
    override val descriptor = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(
        decoder: Decoder
    ): AndroidUri = decoder.decodeString().toUri()

    override fun serialize(
        encoder: Encoder,
        value: AndroidUri
    ) = encoder.encodeString(value.toString())
}

internal typealias Uri = @Serializable(UriSerializer::class) AndroidUri

internal interface ScreenConstants {
    val typedEntries: List<ScreenGroup>

    val entries: List<Screen>

    val FEATURES_COUNT: Int
}

internal object ScreenConstantsImpl : ScreenConstants {
    override val typedEntries by lazy {
        listOf(
            ScreenGroup(
                entries = listOf(
                    SingleEdit(),
                    ResizeAndConvert(),
                    FormatConversion(),
                    Crop(),
                    ImageCutter(),
                    WeightResize(),
                    LimitResize(),
                    EditExif(),
                    DeleteExif(),
                ),
                title = R.string.edit,
                selectedIcon = Icons.Rounded.MiniEditLarge,
                baseIcon = Icons.Outlined.MiniEditLarge
            ),
            ScreenGroup(
                entries = listOf(
                    Filter(),
                    Draw(),
                    EraseBackground(),
                    MarkupLayers(),
                    CollageMaker(),
                    ImageStitching(),
                    ImageStacking(),
                    ImageSplitting(),
                    Watermarking(),
                    GradientMaker(),
                    NoiseGeneration,
                ),
                title = R.string.create,
                selectedIcon = Icons.Filled.AutoAwesome,
                baseIcon = Icons.Outlined.AutoAwesome
            ),
            ScreenGroup(
                entries = listOf(
                    PickColorFromImage(),
                    RecognizeText(),
                    Compare(),
                    ImagePreview(),
                    Base64Tools(),
                    SvgMaker(),
                    GeneratePalette(),
                    LoadNetImage(),
                ),
                title = R.string.image,
                selectedIcon = Icons.Filled.FilterHdr,
                baseIcon = Icons.Outlined.FilterHdr
            ),
            ScreenGroup(
                entries = listOf(
                    PdfTools(),
                    DocumentScanner,
                    ScanQrCode(),
                    ColorTools,
                    GifTools(),
                    Cipher(),
                    ChecksumTools(),
                    Zip(),
                    JxlTools(),
                    ApngTools(),
                    WebpTools(),
                    AudioCoverExtractor()
                ),
                title = R.string.tools,
                selectedIcon = Icons.Rounded.Toolbox,
                baseIcon = Icons.Outlined.Toolbox
            )
        )
    }

    override val entries by lazy {
        typedEntries.flatMap { it.entries }.sortedBy { it.id }
    }

    override val FEATURES_COUNT = 70
}