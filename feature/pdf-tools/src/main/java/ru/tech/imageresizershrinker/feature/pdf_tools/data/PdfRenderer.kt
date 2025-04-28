package ru.tech.imageresizershrinker.feature.pdf_tools.data

import android.graphics.pdf.LoadParams
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.os.ext.SdkExtensions
import androidx.annotation.ChecksSdkIntAtLeast

fun ParcelFileDescriptor.createPdfRenderer(
    password: String?,
    onFailure: (Throwable) -> Unit,
    onPasswordRequest: (() -> Unit)?
): PdfRenderer? = runCatching {
    if (canUseNewPdf()) {
        runCatching {
            @Suppress("NewApi")
            PdfRenderer(
                this@createPdfRenderer,
                LoadParams.Builder().setPassword(password).build()
            )
        }.onFailure {
            if (it is SecurityException) onPasswordRequest?.invoke() ?: throw it
            else throw it
        }.getOrNull()
    } else {
        PdfRenderer(this)
    }
}.onFailure { throwable ->
    when (throwable) {
        is SecurityException -> onPasswordRequest?.invoke() ?: onFailure(throwable)
        else -> onFailure(throwable)
    }
}.getOrNull()


@ChecksSdkIntAtLeast(api = 13, extension = Build.VERSION_CODES.S)
fun canUseNewPdf(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 13