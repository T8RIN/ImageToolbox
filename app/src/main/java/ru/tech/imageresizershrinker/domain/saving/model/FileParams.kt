package ru.tech.imageresizershrinker.domain.saving.model

data class FileParams(
    val treeUri: String,
    val filenamePrefix: String,
    val addSizeInFilename: Boolean,
    val addOriginalFilename: Boolean,
    val addSequenceNumber: Boolean
)