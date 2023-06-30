package ru.tech.imageresizershrinker.core.android

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import ru.tech.imageresizershrinker.R


fun Uri.toPath(
    context: Context,
    isTreeUri: Boolean = true
): String? {
    return if (isTreeUri) {
        DocumentFile.fromTreeUri(context, this)
    } else {
        DocumentFile.fromSingleUri(context, this)
    }?.uri?.path?.split(":")?.lastOrNull()
}

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