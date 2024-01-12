package ru.tech.imageresizershrinker.feature.recognize.text.domain

data class DownloadData(
    val type: RecognitionType,
    val languageCode: String,
    val name: String
)
