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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.BrandingWatermark
import androidx.compose.material.icons.automirrored.twotone.BrandingWatermark
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.GifBox
import androidx.compose.material.icons.outlined.Gradient
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.twotone.Album
import androidx.compose.material.icons.twotone.AutoFixHigh
import androidx.compose.material.icons.twotone.ColorLens
import androidx.compose.material.icons.twotone.Compare
import androidx.compose.material.icons.twotone.DocumentScanner
import androidx.compose.material.icons.twotone.FolderZip
import androidx.compose.material.icons.twotone.GifBox
import androidx.compose.material.icons.twotone.Gradient
import androidx.compose.material.icons.twotone.Photo
import androidx.compose.material.icons.twotone.PictureAsPdf
import androidx.compose.material.icons.twotone.QrCode2
import androidx.compose.material.icons.twotone.Tag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ApngBox
import com.t8rin.imagetoolbox.core.resources.icons.Ascii
import com.t8rin.imagetoolbox.core.resources.icons.Base64
import com.t8rin.imagetoolbox.core.resources.icons.Collage
import com.t8rin.imagetoolbox.core.resources.icons.CropSmall
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.Encrypted
import com.t8rin.imagetoolbox.core.resources.icons.Eraser
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.resources.icons.ExifEdit
import com.t8rin.imagetoolbox.core.resources.icons.Eyedropper
import com.t8rin.imagetoolbox.core.resources.icons.FileImage
import com.t8rin.imagetoolbox.core.resources.icons.FormatPaintVariant
import com.t8rin.imagetoolbox.core.resources.icons.ImageCombine
import com.t8rin.imagetoolbox.core.resources.icons.ImageConvert
import com.t8rin.imagetoolbox.core.resources.icons.ImageDownload
import com.t8rin.imagetoolbox.core.resources.icons.ImageEdit
import com.t8rin.imagetoolbox.core.resources.icons.ImageOverlay
import com.t8rin.imagetoolbox.core.resources.icons.ImageResize
import com.t8rin.imagetoolbox.core.resources.icons.ImageSaw
import com.t8rin.imagetoolbox.core.resources.icons.ImageWeight
import com.t8rin.imagetoolbox.core.resources.icons.Jxl
import com.t8rin.imagetoolbox.core.resources.icons.MiniEditLarge
import com.t8rin.imagetoolbox.core.resources.icons.MultipleImageEdit
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.NoiseAlt
import com.t8rin.imagetoolbox.core.resources.icons.PaletteSwatch
import com.t8rin.imagetoolbox.core.resources.icons.ServiceToolbox
import com.t8rin.imagetoolbox.core.resources.icons.SplitAlt
import com.t8rin.imagetoolbox.core.resources.icons.Stack
import com.t8rin.imagetoolbox.core.resources.icons.TextSearch
import com.t8rin.imagetoolbox.core.resources.icons.VectorPolyline
import com.t8rin.imagetoolbox.core.resources.icons.WallpaperAlt
import com.t8rin.imagetoolbox.core.resources.icons.WandShine
import com.t8rin.imagetoolbox.core.resources.icons.WebpBox
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.AiTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.ApngTools
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
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.SingleEdit
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.SvgMaker
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.WallpapersExport
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Watermarking
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.WebpTools
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.WeightResize
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen.Zip
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
    is PaletteTools -> "Palette_Tools"
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
    is LibraryDetails -> "Library_Details"
    is WallpapersExport -> "Wallpapers_Export"
    is AsciiArt -> "Ascii_Art"
    is AiTools -> "Ai_Tools"
    is ColorLibrary -> "ColorLibrary"
}

internal fun Screen.icon(): ImageVector? = when (this) {
    is EasterEgg,
    is Main,
    is Settings,
    is LibrariesInfo,
    is MeshGradients,
    is LibraryDetails -> null

    is SingleEdit -> Icons.Outlined.ImageEdit
    is ApngTools -> Icons.Rounded.ApngBox
    is Cipher -> Icons.Outlined.Encrypted
    is Compare -> Icons.Rounded.Compare
    is Crop -> Icons.Rounded.CropSmall
    is DeleteExif -> Icons.Outlined.Exif
    is Draw -> Icons.Outlined.Draw
    is EraseBackground -> Icons.Rounded.Eraser
    is Filter -> Icons.Outlined.AutoFixHigh
    is PaletteTools -> Icons.Outlined.PaletteSwatch
    is GifTools -> Icons.Outlined.GifBox
    is GradientMaker -> Icons.Outlined.Gradient
    is ImagePreview -> Icons.Outlined.Photo
    is ImageStitching -> Icons.Rounded.ImageCombine
    is JxlTools -> Icons.Filled.Jxl
    is LimitResize -> Icons.Outlined.ImageResize
    is LoadNetImage -> Icons.Outlined.ImageDownload
    is PdfTools -> Icons.Outlined.PictureAsPdf
    is PickColorFromImage -> Icons.Outlined.Eyedropper
    is RecognizeText -> Icons.Outlined.TextSearch
    is ResizeAndConvert -> Icons.Outlined.MultipleImageEdit
    is WeightResize -> Icons.Outlined.ImageWeight
    is Watermarking -> Icons.AutoMirrored.Outlined.BrandingWatermark
    is Zip -> Icons.Outlined.FolderZip
    is SvgMaker -> Icons.Outlined.VectorPolyline
    is FormatConversion -> Icons.Outlined.ImageConvert
    is DocumentScanner -> Icons.Outlined.DocumentScanner
    is ScanQrCode -> Icons.Outlined.QrCode2
    is ImageStacking -> Icons.Outlined.ImageOverlay
    is ImageSplitting -> Icons.Outlined.SplitAlt
    is ColorTools -> Icons.Outlined.ColorLens
    is WebpTools -> Icons.Outlined.WebpBox
    is NoiseGeneration -> Icons.Outlined.NoiseAlt
    is CollageMaker -> Icons.Outlined.Collage
    is MarkupLayers -> Icons.Outlined.Stack
    is Base64Tools -> Icons.Outlined.Base64
    is ChecksumTools -> Icons.Rounded.Tag
    is EditExif -> Icons.Outlined.ExifEdit
    is ImageCutter -> Icons.Outlined.ImageSaw
    is AudioCoverExtractor -> Icons.Outlined.Album
    is WallpapersExport -> Icons.Outlined.WallpaperAlt
    is AsciiArt -> Icons.Outlined.Ascii
    is AiTools -> Icons.Outlined.Neurology
    is ColorLibrary -> Icons.Outlined.FormatPaintVariant
}

internal fun Screen.twoToneIcon(): ImageVector? = when (this) {
    is EasterEgg,
    is Main,
    is Settings,
    is LibrariesInfo,
    is MeshGradients,
    is LibraryDetails -> null

    is SingleEdit -> Icons.TwoTone.ImageEdit
    is ApngTools -> Icons.TwoTone.ApngBox
    is Cipher -> Icons.TwoTone.Encrypted
    is Compare -> Icons.TwoTone.Compare
    is Crop -> Icons.TwoTone.CropSmall
    is DeleteExif -> Icons.TwoTone.Exif
    is Draw -> Icons.TwoTone.Draw
    is EraseBackground -> Icons.TwoTone.Eraser
    is Filter -> Icons.TwoTone.AutoFixHigh
    is PaletteTools -> Icons.TwoTone.PaletteSwatch
    is GifTools -> Icons.TwoTone.GifBox
    is GradientMaker -> Icons.TwoTone.Gradient
    is ImagePreview -> Icons.TwoTone.Photo
    is ImageStitching -> Icons.TwoTone.ImageCombine
    is JxlTools -> Icons.Filled.Jxl
    is LimitResize -> Icons.TwoTone.ImageResize
    is LoadNetImage -> Icons.TwoTone.ImageDownload
    is PdfTools -> Icons.TwoTone.PictureAsPdf
    is PickColorFromImage -> Icons.TwoTone.Eyedropper
    is RecognizeText -> Icons.Outlined.TextSearch
    is ResizeAndConvert -> Icons.TwoTone.MultipleImageEdit
    is WeightResize -> Icons.TwoTone.ImageWeight
    is Watermarking -> Icons.AutoMirrored.TwoTone.BrandingWatermark
    is Zip -> Icons.TwoTone.FolderZip
    is SvgMaker -> Icons.TwoTone.VectorPolyline
    is FormatConversion -> Icons.TwoTone.ImageConvert
    is DocumentScanner -> Icons.TwoTone.DocumentScanner
    is ScanQrCode -> Icons.TwoTone.QrCode2
    is ImageStacking -> Icons.TwoTone.ImageOverlay
    is ImageSplitting -> Icons.TwoTone.SplitAlt
    is ColorTools -> Icons.TwoTone.ColorLens
    is WebpTools -> Icons.TwoTone.WebpBox
    is NoiseGeneration -> Icons.Outlined.NoiseAlt
    is CollageMaker -> Icons.TwoTone.Collage
    is MarkupLayers -> Icons.TwoTone.Stack
    is Base64Tools -> Icons.TwoTone.Base64
    is ChecksumTools -> Icons.TwoTone.Tag
    is EditExif -> Icons.TwoTone.ExifEdit
    is ImageCutter -> Icons.TwoTone.ImageSaw
    is AudioCoverExtractor -> Icons.TwoTone.Album
    is WallpapersExport -> Icons.Outlined.WallpaperAlt
    is AsciiArt -> Icons.Outlined.Ascii
    is AiTools -> Icons.TwoTone.Neurology
    is ColorLibrary -> Icons.TwoTone.FormatPaintVariant
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

    companion object : ScreenConstants by ScreenConstantsImpl
}

private object ScreenConstantsImpl : ScreenConstants {
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
                    AiTools(),
                    CollageMaker(),
                    ImageStitching(),
                    ImageStacking(),
                    ImageSplitting(),
                    Watermarking(),
                    GradientMaker(),
                    NoiseGeneration,
                ),
                title = R.string.create,
                selectedIcon = Icons.Rounded.WandShine,
                baseIcon = Icons.Outlined.WandShine
            ),
            ScreenGroup(
                entries = listOf(
                    PickColorFromImage(),
                    RecognizeText(),
                    Compare(),
                    ImagePreview(),
                    WallpapersExport,
                    Base64Tools(),
                    SvgMaker(),
                    PaletteTools(),
                    LoadNetImage(),
                ),
                title = R.string.image,
                selectedIcon = Icons.Rounded.FileImage,
                baseIcon = Icons.Outlined.FileImage
            ),
            ScreenGroup(
                entries = listOf(
                    PdfTools(),
                    DocumentScanner,
                    ScanQrCode(),
                    ColorTools,
                    ColorLibrary,
                    GifTools(),
                    Cipher(),
                    ChecksumTools(),
                    Zip(),
                    AsciiArt(),
                    JxlTools(),
                    ApngTools(),
                    WebpTools(),
                    AudioCoverExtractor()
                ),
                title = R.string.tools,
                selectedIcon = Icons.Rounded.ServiceToolbox,
                baseIcon = Icons.Outlined.ServiceToolbox
            )
        )
    }

    override val entries by lazy {
        typedEntries.flatMap { it.entries }.sortedBy { it.id }
    }

    override val FEATURES_COUNT = 82
}