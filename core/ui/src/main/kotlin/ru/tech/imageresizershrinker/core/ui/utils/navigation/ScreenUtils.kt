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

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.BrandingWatermark
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.DocumentScanner
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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
import ru.tech.imageresizershrinker.core.resources.icons.MultipleImageEdit
import ru.tech.imageresizershrinker.core.resources.icons.PaletteSwatch
import ru.tech.imageresizershrinker.core.resources.icons.SplitAlt
import ru.tech.imageresizershrinker.core.resources.icons.Stack
import ru.tech.imageresizershrinker.core.resources.icons.VectorPolyline
import ru.tech.imageresizershrinker.core.resources.icons.WebpBox

internal fun Screen.isBetaFeature(): Boolean = when (this) {
    is Screen.MarkupLayers -> true
    else -> false
}

internal fun Screen.simpleName(): String? = when (this) {
    is Screen.ApngTools -> "APNG_Tools"
    is Screen.Cipher -> "Cipher"
    is Screen.Compare -> "Compare"
    is Screen.Crop -> "Crop"
    is Screen.DeleteExif -> "Delete_Exif"
    is Screen.Draw -> "Draw"
    is Screen.EasterEgg -> "Easter_Egg"
    is Screen.EraseBackground -> "Erase_Background"
    is Screen.Filter -> "Filter"
    is Screen.GeneratePalette -> "Generate_Palette"
    is Screen.GifTools -> "GIF_Tools"
    is Screen.GradientMaker -> "Gradient_Maker"
    is Screen.ImagePreview -> "Image_Preview"
    is Screen.ImageStitching -> "Image_Stitching"
    is Screen.JxlTools -> "JXL_Tools"
    is Screen.LimitResize -> "Limit_Resize"
    is Screen.LoadNetImage -> "Load_Net_Image"
    is Screen.Main -> null
    is Screen.PdfTools -> "PDF_Tools"
    is Screen.PickColorFromImage -> "Pick_Color_From_Image"
    is Screen.RecognizeText -> "Recognize_Text"
    is Screen.ResizeAndConvert -> "Resize_And_Convert"
    is Screen.WeightResize -> "Resize_By_Bytes"
    is Screen.Settings -> "Settings"
    is Screen.SingleEdit -> "Single_Edit"
    is Screen.Watermarking -> "Watermarking"
    is Screen.Zip -> "Zip"
    is Screen.SvgMaker -> "Svg"
    is Screen.FormatConversion -> "Convert"
    is Screen.DocumentScanner -> "Document_Scanner"
    is Screen.ScanQrCode -> "QR_Code"
    is Screen.ImageStacking -> "Image_Stacking"
    is Screen.ImageSplitting -> "Image_Splitting"
    is Screen.ColorTools -> "Color_Tools"
    is Screen.WebpTools -> "WEBP_Tools"
    is Screen.NoiseGeneration -> "Noise_Generation"
    is Screen.CollageMaker -> "Collage_Maker"
    is Screen.LibrariesInfo -> "Libraries_Info"
    is Screen.MarkupLayers -> "Markup_Layers"
    is Screen.Base64Tools -> "Base64_Tools"
    is Screen.ChecksumTools -> "Checksum_Tools"
    is Screen.MeshGradients -> "Mesh_Gradients"
    is Screen.EditExif -> "Edit_EXIF"
    is Screen.ImageCutter -> "Image_Cutting"
}

internal fun Screen.icon(): ImageVector? = when (this) {
    is Screen.EasterEgg,
    is Screen.Main,
    is Screen.Settings,
    is Screen.LibrariesInfo,
    is Screen.MeshGradients -> null

    is Screen.SingleEdit -> Icons.Outlined.ImageEdit
    is Screen.ApngTools -> Icons.Rounded.ApngBox
    is Screen.Cipher -> Icons.Outlined.Encrypted
    is Screen.Compare -> Icons.Rounded.Compare
    is Screen.Crop -> Icons.Rounded.CropSmall
    is Screen.DeleteExif -> Icons.Outlined.Exif
    is Screen.Draw -> Icons.Outlined.Draw
    is Screen.EraseBackground -> Icons.Rounded.Eraser
    is Screen.Filter -> Icons.Outlined.AutoFixHigh
    is Screen.GeneratePalette -> Icons.Outlined.PaletteSwatch
    is Screen.GifTools -> Icons.Outlined.GifBox
    is Screen.GradientMaker -> Icons.Outlined.Gradient
    is Screen.ImagePreview -> Icons.Outlined.Photo
    is Screen.ImageStitching -> Icons.Rounded.ImageCombine
    is Screen.JxlTools -> Icons.Filled.Jxl
    is Screen.LimitResize -> Icons.Outlined.ImageLimit
    is Screen.LoadNetImage -> Icons.Outlined.ImageDownload
    is Screen.PdfTools -> Icons.Outlined.PictureAsPdf
    is Screen.PickColorFromImage -> Icons.Outlined.Colorize
    is Screen.RecognizeText -> Icons.Outlined.ImageText
    is Screen.ResizeAndConvert -> Icons.Rounded.MultipleImageEdit
    is Screen.WeightResize -> Icons.Rounded.ImageWeight
    is Screen.Watermarking -> Icons.AutoMirrored.Outlined.BrandingWatermark
    is Screen.Zip -> Icons.Outlined.FolderZip
    is Screen.SvgMaker -> Icons.Outlined.VectorPolyline
    is Screen.FormatConversion -> Icons.Outlined.ImageConvert
    is Screen.DocumentScanner -> Icons.Outlined.DocumentScanner
    is Screen.ScanQrCode -> Icons.Outlined.QrCode
    is Screen.ImageStacking -> Icons.Outlined.ImageOverlay
    is Screen.ImageSplitting -> Icons.Outlined.SplitAlt
    is Screen.ColorTools -> Icons.Outlined.ColorLens
    is Screen.WebpTools -> Icons.Rounded.WebpBox
    is Screen.NoiseGeneration -> Icons.Outlined.Grain
    is Screen.CollageMaker -> Icons.Outlined.Collage
    is Screen.MarkupLayers -> Icons.Outlined.Stack
    is Screen.Base64Tools -> Icons.Outlined.Base64
    is Screen.ChecksumTools -> Icons.Rounded.Tag
    is Screen.EditExif -> Icons.Outlined.ExifEdit
    is Screen.ImageCutter -> Icons.Rounded.ContentCut
}

internal object UriSerializer : KSerializer<Uri> {
    override val descriptor = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(
        decoder: Decoder
    ): Uri = Uri.parse(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: Uri
    ) = encoder.encodeString(value.toString())
}

internal typealias KUri = @Serializable(UriSerializer::class) Uri