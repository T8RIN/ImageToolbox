package ru.tech.imageresizershrinker.utils.navigation

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Dataset
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PhotoSizeSelectLarge
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.PaletteSwatch

@Parcelize
sealed class Screen(
    val id: Int,
    val icon: @RawValue ImageVector?,
    @StringRes val title: Int,
    @StringRes val subtitle: Int
) : Parcelable {
    object Main : Screen(-1, null, 0, 0)
    class SingleResize(val uri: Uri? = null) : Screen(
        id = 0,
        icon = Icons.Rounded.PhotoSizeSelectLarge,
        title = R.string.single_resize,
        subtitle = R.string.resize_single_image
    )

    class BatchResize(val uris: List<Uri>? = null) : Screen(
        id = 1,
        icon = Icons.Rounded.PhotoLibrary,
        title = R.string.batch_resize,
        subtitle = R.string.resize_batch_image
    )

    class DeleteExif(val uris: List<Uri>? = null) : Screen(
        id = 2,
        icon = Icons.Rounded.Dataset,
        title = R.string.delete_exif,
        subtitle = R.string.delete_exif_sub
    )

    class PickColorFromImage(val uri: Uri? = null) : Screen(
        id = 3,
        icon = Icons.Rounded.Colorize,
        title = R.string.pick_color,
        subtitle = R.string.pick_color_sub
    )

    class ImagePreview(val uris: List<Uri>? = null) : Screen(
        id = 4,
        icon = Icons.Rounded.Photo,
        title = R.string.image_preview,
        subtitle = R.string.image_preview_sub
    )

    class GeneratePalette(val uri: Uri? = null) : Screen(
        id = 5,
        icon = Icons.Rounded.PaletteSwatch,
        title = R.string.generate_palette,
        subtitle = R.string.palette_sub
    )

    class Crop(val uri: Uri? = null) : Screen(
        id = 6,
        icon = Icons.Rounded.Crop,
        title = R.string.crop,
        subtitle = R.string.crop_sub
    )

    class ResizeByBytes(val uris: List<Uri>? = null) : Screen(
        id = 7,
        icon = Icons.Rounded.InsertDriveFile,
        title = R.string.by_bytes_resize,
        subtitle = R.string.by_bytes_resize_sub
    )

    class Compare(val uris: List<Uri>? = null) : Screen(
        id = 8,
        icon = Icons.Rounded.Compare,
        title = R.string.compare,
        subtitle = R.string.compare_sub
    )

    companion object {
        val entries = listOf(
            SingleResize(),
            BatchResize(),
            DeleteExif(),
            PickColorFromImage(),
            ImagePreview(),
            GeneratePalette(),
            Crop(),
            ResizeByBytes(),
            Compare()
        )
    }
}