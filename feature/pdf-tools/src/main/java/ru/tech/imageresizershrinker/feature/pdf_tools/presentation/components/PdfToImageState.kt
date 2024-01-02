package ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components

import android.net.Uri

data class PdfToImageState(
    val uri: Uri,
    val pages: List<Int>
)
