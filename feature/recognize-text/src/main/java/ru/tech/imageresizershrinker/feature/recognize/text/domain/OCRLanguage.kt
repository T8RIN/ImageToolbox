package ru.tech.imageresizershrinker.feature.recognize.text.domain

data class OCRLanguage(
    val name: String,
    val code: String,
    val downloaded: List<RecognitionType>
) {
    companion object {
        val Default by lazy {
            OCRLanguage(
                "English",
                "eng",
                emptyList()
            )
        }
    }
}