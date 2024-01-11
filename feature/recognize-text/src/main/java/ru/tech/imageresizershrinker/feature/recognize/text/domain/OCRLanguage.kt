package ru.tech.imageresizershrinker.feature.recognize.text.domain

data class OCRLanguage(
    val name: String,
    val code: String,
    val downloaded: Boolean
)