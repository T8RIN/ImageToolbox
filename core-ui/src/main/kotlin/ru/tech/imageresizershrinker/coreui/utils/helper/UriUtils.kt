package ru.tech.imageresizershrinker.coreui.utils.helper

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import ru.tech.imageresizershrinker.coreui.R

fun Uri?.toUiPath(context: Context, default: String): String = this?.let { uri ->
    DocumentFile
        .fromTreeUri(context, uri)
        ?.uri?.path?.split(":")
        ?.lastOrNull()?.let { p ->
            val endPath = p.takeIf {
                it.isNotEmpty()
            }?.let { "/$it" } ?: ""
            val startPath = if (
                uri.toString()
                    .split("%")[0]
                    .contains("primary")
            ) context.getString(R.string.device_storage)
            else context.getString(R.string.external_storage)

            startPath + endPath
        }
} ?: default