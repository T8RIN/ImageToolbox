package ru.tech.imageresizershrinker.feature.pdf_tools.data

import android.graphics.pdf.LoadParams
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.os.ext.SdkExtensions

fun ParcelFileDescriptor.createPdfRenderer(
    password: String?,
    onFailure: (Throwable) -> Unit,
    onPasswordRequest: () -> Unit
): PdfRenderer? {
    val hasNewPdf = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 13

    return try {
        if (hasNewPdf) {
            runCatching {
                @Suppress("NewApi")
                PdfRenderer(
                    this@createPdfRenderer,
                    LoadParams.Builder().setPassword(password).build()
                )
            }.onFailure {
                if (it is SecurityException) onPasswordRequest()
            }.getOrNull()
        } else {
            PdfRenderer(this)
        }
    } catch (throwable: Throwable) {
        when (throwable) {
            is SecurityException -> onPasswordRequest()
            else -> onFailure(throwable)
        }

        null
    }
}