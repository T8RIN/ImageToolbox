package ru.tech.imageresizershrinker.feature.recognize.text.domain

interface ImageTextReader<Image> {

    suspend fun getTextFromImage(
        type: RecognitionType,
        languageCode: String,
        segmentationMode: SegmentationMode,
        imageUri: String,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult

    suspend fun getTextFromImage(
        type: RecognitionType,
        languageCode: String,
        segmentationMode: SegmentationMode,
        image: Image?,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult

    suspend fun downloadTrainingData(
        type: RecognitionType,
        languageCode: String,
        onProgress: (Float, Long) -> Unit
    ): Boolean

    fun isLanguageDataExists(
        type: RecognitionType,
        languageCode: String
    ): Boolean

    suspend fun getLanguages(
        type: RecognitionType
    ): List<OCRLanguage>

}