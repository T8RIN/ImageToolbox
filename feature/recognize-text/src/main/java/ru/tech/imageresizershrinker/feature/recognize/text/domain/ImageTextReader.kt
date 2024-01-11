package ru.tech.imageresizershrinker.feature.recognize.text.domain

interface ImageTextReader<Image> {

    suspend fun getTextFromImage(
        type: RecognitionType,
        language: String,
        segmentationMode: SegmentationMode,
        imageUri: String,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult

    suspend fun getTextFromImage(
        type: RecognitionType,
        language: String,
        segmentationMode: SegmentationMode,
        image: Image?,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult

    suspend fun downloadTrainingData(
        type: RecognitionType,
        language: String,
        onProgress: (Float, Long) -> Unit
    ): Boolean

    fun isLanguageDataExists(
        type: RecognitionType,
        language: String
    ): Boolean

    suspend fun getLanguages(
        type: RecognitionType
    ): List<OCRLanguage>

}