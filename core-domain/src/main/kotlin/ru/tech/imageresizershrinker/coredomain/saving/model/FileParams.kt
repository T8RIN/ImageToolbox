package ru.tech.imageresizershrinker.coredomain.saving.model

data class FileParams(
    val treeUri: String?,
    val filenamePrefix: String,
    val filenameSuffix: String,
    val addSizeInFilename: Boolean,
    val addOriginalFilename: Boolean,
    val addSequenceNumber: Boolean,
    val randomizeFilename: Boolean,
    val copyToClipBoard: Boolean,
    val overwriteFile: Boolean
)