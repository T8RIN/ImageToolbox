package ru.tech.imageresizershrinker.core.domain.saving.model

import ru.tech.imageresizershrinker.core.domain.model.CopyToClipboardMode

data class FileParams(
    val treeUri: String?,
    val filenamePrefix: String,
    val filenameSuffix: String,
    val addSizeInFilename: Boolean,
    val addOriginalFilename: Boolean,
    val addSequenceNumber: Boolean,
    val randomizeFilename: Boolean,
    val copyToClipboardMode: CopyToClipboardMode,
    val overwriteFile: Boolean
)