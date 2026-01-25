/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlinx.coroutines.coroutineScope
import java.net.URLDecoder
import java.net.URLEncoder

fun Uri?.toUiPath(
    default: String
): String = this?.let { uri ->
    DocumentFile
        .fromTreeUri(appContext, uri)
        ?.uri?.path?.split(":")
        ?.lastOrNull()?.let { p ->
            val endPath = p.takeIf {
                it.isNotEmpty()
            }?.let { "/$it" } ?: ""
            val startPath = if (
                uri.toString()
                    .split("%")[0]
                    .contains("primary")
            ) appContext.getString(R.string.device_storage)
            else appContext.getString(R.string.external_storage)

            startPath + endPath
        }
} ?: default

private fun Uri.lastModified(): Long? = with(appContext.contentResolver) {
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

private fun Uri.addedTime(): Long? =
    getLongColumn(MediaStore.MediaColumns.DATE_ADDED)?.times(1000)


private fun Uri.getLongColumn(column: String): Long? =
    appContext.contentResolver.query(this, arrayOf(column), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(column)
            if (index != -1 && !cursor.isNull(index)) cursor.getLong(index) else null
        } else null
    }

private fun Uri.getStringColumn(column: String): String? =
    appContext.contentResolver.query(this, arrayOf(column), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(column)
            if (index != -1 && !cursor.isNull(index)) cursor.getString(index) else null
        } else null
    }


suspend fun List<Uri>.sortedByType(
    sortType: SortType,
): List<Uri> = coroutineScope {
    when (sortType) {
        SortType.DateModified -> sortedByDateModified()
        SortType.DateModifiedReversed -> sortedByDateModified(descending = true)
        SortType.Name -> sortedByName()
        SortType.NameReversed -> sortedByName(descending = true)
        SortType.Size -> sortedBySize()
        SortType.SizeReversed -> sortedBySize(descending = true)
        SortType.MimeType -> sortedByMimeType()
        SortType.MimeTypeReversed -> sortedByMimeType(descending = true)
        SortType.Extension -> sortedByExtension()
        SortType.ExtensionReversed -> sortedByExtension(descending = true)
        SortType.DateAdded -> sortedByDateAdded()
        SortType.DateAddedReversed -> sortedByDateAdded(descending = true)
    }
}

private fun List<Uri>.sortedByExtension(
    descending: Boolean = false
) = sortedByKey(descending) {
    it.getFilename()?.substringAfterLast(
        delimiter = '.',
        missingDelimiterValue = ""
    )?.lowercase()
}

private fun List<Uri>.sortedByDateModified(
    descending: Boolean = false
) = sortedByKey(descending) { it.lastModified() }

private fun List<Uri>.sortedByName(
    descending: Boolean = false
) = sortedByKey(descending) {
    it.getFilename()
}

private fun List<Uri>.sortedBySize(
    descending: Boolean = false
) = sortedByKey(descending) {
    it.getLongColumn(OpenableColumns.SIZE)
}

private fun List<Uri>.sortedByMimeType(
    descending: Boolean = false
) = sortedByKey(descending) {
    it.getStringColumn(
        column = DocumentsContract.Document.COLUMN_MIME_TYPE
    )
}

private fun List<Uri>.sortedByDateAdded(
    descending: Boolean = false
) = sortedByKey(descending) {
    it.addedTime()
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

fun Uri.isApng(): Boolean {
    return getFilename().toString().endsWith(".png")
        .or(appContext.contentResolver.getType(this)?.contains("png") == true)
        .or(appContext.contentResolver.getType(this)?.contains("apng") == true)
}

fun Uri.isWebp(): Boolean {
    return getFilename().toString().endsWith(".webp")
        .or(appContext.contentResolver.getType(this)?.contains("webp") == true)
}

fun Uri.isJxl(): Boolean {
    return getFilename().toString().endsWith(".jxl")
        .or(appContext.contentResolver.getType(this)?.contains("jxl") == true)
}

fun Uri.isGif(): Boolean {
    return getFilename().toString().endsWith(".gif")
        .or(appContext.contentResolver.getType(this)?.contains("gif") == true)
}

fun Uri.getFilename(
    context: Context = appContext
): String? = if (this.toString().startsWith("file:///")) {
    this.toString().takeLastWhile { it != '/' }
} else {
    DocumentFile.fromSingleUri(context, this)?.name
}?.decodeEscaped()