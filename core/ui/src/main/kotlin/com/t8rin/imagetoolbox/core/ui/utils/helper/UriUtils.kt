/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.SortType
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.sortedByKey
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getFilename
import kotlinx.coroutines.coroutineScope
import java.net.URLDecoder
import java.net.URLEncoder


fun Uri?.toUiPath(
    context: Context,
    default: String
): String = this?.let { uri ->
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

private fun Uri.lastModified(context: Context): Long? = with(context.contentResolver) {
    val query = query(this@lastModified, null, null, null, null)

    query?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnNames = listOf(
                DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                "datetaken", // When sharing an Image from Google Photos into the app.
            )

            val millis = columnNames.firstNotNullOfOrNull {
                val index = cursor.getColumnIndex(it)
                if (!cursor.isNull(index)) {
                    cursor.getLong(index)
                } else {
                    null
                }
            }

            return millis
        }
    }

    return null
}

private fun Uri.addedTime(context: Context): Long? =
    getLongColumn(context, MediaStore.MediaColumns.DATE_ADDED)?.times(1000)


private fun Uri.getLongColumn(context: Context, column: String): Long? =
    context.contentResolver.query(this, arrayOf(column), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(column)
            if (index != -1 && !cursor.isNull(index)) cursor.getLong(index) else null
        } else null
    }

private fun Uri.getStringColumn(context: Context, column: String): String? =
    context.contentResolver.query(this, arrayOf(column), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(column)
            if (index != -1 && !cursor.isNull(index)) cursor.getString(index) else null
        } else null
    }


suspend fun List<Uri>.sortedByType(
    sortType: SortType,
    context: Context
): List<Uri> = coroutineScope {
    when (sortType) {
        SortType.DateModified -> sortedByDateModified(context = context)
        SortType.DateModifiedReversed -> sortedByDateModified(context = context, descending = true)
        SortType.Name -> sortedByName(context = context)
        SortType.NameReversed -> sortedByName(context = context, descending = true)
        SortType.Size -> sortedBySize(context = context)
        SortType.SizeReversed -> sortedBySize(context = context, descending = true)
        SortType.MimeType -> sortedByMimeType(context = context)
        SortType.MimeTypeReversed -> sortedByMimeType(context = context, descending = true)
        SortType.Extension -> sortedByExtension(context = context)
        SortType.ExtensionReversed -> sortedByExtension(context = context, descending = true)
        SortType.DateAdded -> sortedByDateAdded(context = context)
        SortType.DateAddedReversed -> sortedByDateAdded(context = context, descending = true)
    }
}

private fun List<Uri>.sortedByExtension(
    context: Context,
    descending: Boolean = false
) = sortedByKey(descending) {
    context.getFilename(it)?.substringAfterLast(
        delimiter = '.',
        missingDelimiterValue = ""
    )?.lowercase()
}

private fun List<Uri>.sortedByDateModified(
    context: Context,
    descending: Boolean = false
) = sortedByKey(descending) { it.lastModified(context) }

private fun List<Uri>.sortedByName(
    context: Context,
    descending: Boolean = false
) = sortedByKey(descending) {
    context.getFilename(it)
}

private fun List<Uri>.sortedBySize(
    context: Context,
    descending: Boolean = false
) = sortedByKey(descending) {
    it.getLongColumn(context, OpenableColumns.SIZE)
}

private fun List<Uri>.sortedByMimeType(
    context: Context,
    descending: Boolean = false
) = sortedByKey(descending) {
    it.getStringColumn(
        context = context,
        column = DocumentsContract.Document.COLUMN_MIME_TYPE
    )
}

private fun List<Uri>.sortedByDateAdded(
    context: Context,
    descending: Boolean = false
) = sortedByKey(descending) {
    it.addedTime(context)
}

fun ImageModel.toUri(): Uri? = when (data) {
    is Uri -> data as Uri
    is String -> (data as String).toUri()
    else -> null
}

fun Any.toImageModel() = ImageModel(this)

fun String.toFileModel() = FileModel(this)

fun String.decodeEscaped(): String = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        URLDecoder.decode(URLDecoder.decode(this, Charsets.UTF_8), Charsets.UTF_8)
    } else {
        @Suppress("DEPRECATION")
        URLDecoder.decode(URLDecoder.decode(this))
    }
}.getOrDefault(this)

fun String.encodeEscaped(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        URLEncoder.encode(this, Charsets.UTF_8)
    } else {
        @Suppress("DEPRECATION")
        URLEncoder.encode(this)
    }
}

fun Uri.isApng(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".png")
        .or(context.contentResolver.getType(this)?.contains("png") == true)
        .or(context.contentResolver.getType(this)?.contains("apng") == true)
}

fun Uri.isWebp(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".webp")
        .or(context.contentResolver.getType(this)?.contains("webp") == true)
}

fun Uri.isJxl(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".jxl")
        .or(context.contentResolver.getType(this)?.contains("jxl") == true)
}

fun Uri.isGif(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".gif")
        .or(context.contentResolver.getType(this)?.contains("gif") == true)
}