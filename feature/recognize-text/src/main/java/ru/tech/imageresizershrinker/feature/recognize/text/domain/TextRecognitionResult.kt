package ru.tech.imageresizershrinker.feature.recognize.text.domain


sealed interface TextRecognitionResult {

    data class Error(val throwable: Throwable) : TextRecognitionResult

    data class Success(val data: RecognitionData) : TextRecognitionResult

    data class NoData(val data: List<DownloadData>) : TextRecognitionResult

}