package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tech.imageresizershrinker.feature.recognize.text.domain.DownloadData
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType

@Parcelize
data class UiDownloadData(
    val type: RecognitionType,
    val languageCode: String,
    val name: String
) : Parcelable

fun DownloadData.toUi() = UiDownloadData(type, languageCode, name)