package ru.tech.imageresizershrinker.feature.media_picker.data.utils

import ru.tech.imageresizershrinker.feature.media_picker.domain.model.AllowedMedia

enum class FileType {
    Photo,
    Video
}

val SUPPORTED_FILES = mapOf(
    "jxl" to Pair("image/jxl", FileType.Photo),
    "qoi" to Pair("image/qoi", FileType.Photo)
)

fun getSupportedFileSequence(allowedMedia: AllowedMedia) = when(allowedMedia) {
    AllowedMedia.Both -> SUPPORTED_FILES.asSequence()
    is AllowedMedia.Photos -> SUPPORTED_FILES.asSequence().filter { (_, p) -> p.second == FileType.Photo }
    AllowedMedia.Videos -> SUPPORTED_FILES.asSequence().filter { (_, p) -> p.second == FileType.Video }
}.map { (ext, _) -> "%.$ext" }