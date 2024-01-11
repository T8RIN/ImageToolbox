package ru.tech.imageresizershrinker.feature.recognize.text.domain

sealed class RecognitionType(val name: String) {
    data object Fast: RecognitionType("fast")
    data object Standard: RecognitionType("standard")
    data object Best: RecognitionType("best")

    companion object {
        val entries by lazy {
            listOf(Fast, Standard, Best)
        }
    }
}