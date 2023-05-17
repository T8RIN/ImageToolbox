package ru.tech.imageresizershrinker.main_screen.components

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Screen : Parcelable {
    object Main : Screen()
    class SingleResize(val uri: Uri? = null) : Screen()
    class BatchResize(val uris: List<Uri>? = null) : Screen()
    class DeleteExif(val uris: List<Uri>? = null) : Screen()
    class PickColorFromImage(val uri: Uri? = null) : Screen()
    class ImagePreview(val uris: List<Uri>? = null) : Screen()
    class GeneratePalette(val uri: Uri? = null) : Screen()
    class Crop(val uri: Uri? = null) : Screen()
    class ResizeByBytes(val uris: List<Uri>? = null) : Screen()
    class Compare(val uris: List<Uri>? = null) : Screen()
}