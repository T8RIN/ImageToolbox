package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType

@Parcelize
data class UiDownloadData(
    val type: RecognitionType,
    val language: String
) : Parcelable