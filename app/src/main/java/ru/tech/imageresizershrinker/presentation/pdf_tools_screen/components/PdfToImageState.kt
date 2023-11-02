package ru.tech.imageresizershrinker.presentation.pdf_tools_screen.components

import android.net.Uri

data class PdfToImageState(
    val uri: Uri,
    val pages: List<Int>
)
