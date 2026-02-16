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

package com.t8rin.imagetoolbox.core.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.SortType
import com.t8rin.imagetoolbox.core.domain.utils.FileMode
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.sortedByKey
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.logger.makeLog
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.LinkedList

fun Uri?.uiPath(
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

fun Uri.lastModified(): Long? = tryExtractOriginal().run {
    runCatching {
        if (this.toString().startsWith("file:///")) {
            return toFile().lastModified()
        }

        with(appContext.contentResolver) {
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
        }

        return DocumentFile.fromSingleUri(appContext, this)?.lastModified()
    }.onFailure { it.printStackTrace() }

    return null
}

fun Uri.path(): String? = tryExtractOriginal().run {
    getStringColumn(MediaStore.MediaColumns.DATA)?.takeIf {
        ".transforms" !in it
    }.orEmpty().ifEmpty {
        runCatching {
            val path = DocumentFile.fromSingleUri(appContext, this)?.uri?.path?.split(":")
                ?.lastOrNull() ?: return@run null

            if ("cloud" in path) {
                "/cloud/${path.substringAfterLast('/')}"
            } else {
                path
            }
        }.getOrNull()
    }
}

fun Uri.dateAdded(): Long? = tryExtractOriginal().run {
    getLongColumn(MediaStore.MediaColumns.DATE_ADDED)?.times(1000)
}

fun Uri.filename(
    context: Context = appContext
): String? = tryExtractOriginal().run {
    if (this.toString().startsWith("file:///")) {
        this.toString().takeLastWhile { it != '/' }
    } else {
        DocumentFile.fromSingleUri(context, this)?.name
    }?.decodeEscaped()
}

fun Uri.fileSize(): Long? = tryExtractOriginal().run {
    if (this.scheme == "content") {
        runCatching {
            appContext.contentResolver
                .query(this, null, null, null, null, null)
                .use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (!cursor.isNull(sizeIndex)) {
                            return cursor.getLong(sizeIndex)
                        }
                    }
                }
        }
    } else {
        runCatching {
            return this.toFile().length()
        }
    }
    return null
}

fun Uri.tryExtractOriginal(): Uri = try {
    val mimeType = getStringColumn(MediaStore.MediaColumns.MIME_TYPE).orEmpty()

    val contentUri = when {
        "image" in mimeType -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        "video" in mimeType -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        "audio" in mimeType -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        else -> return this
    }

    ContentUris.withAppendedId(
        contentUri,
        this.toString().decodeEscaped().substringAfterLast('/').filter { it.isDigit() }.toLong()
    )
} catch (e: Throwable) {
    e.makeLog("tryExtractOriginal")
    this
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
    return filename().toString().endsWith(".png")
        .or(appContext.contentResolver.getType(this)?.contains("png") == true)
        .or(appContext.contentResolver.getType(this)?.contains("apng") == true)
}

fun Uri.isWebp(): Boolean {
    return filename().toString().endsWith(".webp")
        .or(appContext.contentResolver.getType(this)?.contains("webp") == true)
}

fun Uri.isJxl(): Boolean {
    return filename().toString().endsWith(".jxl")
        .or(appContext.contentResolver.getType(this)?.contains("jxl") == true)
}

fun Uri.isGif(): Boolean {
    return filename().toString().endsWith(".gif")
        .or(appContext.contentResolver.getType(this)?.contains("gif") == true)
}

suspend fun Uri.listFilesInDirectory(): List<Uri> =
    listFilesInDirectoryAsFlowImpl().filterIsInstance<DirUri.All>().first().uris

fun Uri.listFilesInDirectoryProgressive(): Flow<Uri> = listFilesInDirectoryAsFlowImpl()
    .filterIsInstance<DirUri.Entry>()
    .map { it.uri }

fun String?.getPath(
    context: Context = appContext
) = this?.takeIf { it.isNotEmpty() }?.toUri().uiPath(
    default = context.getString(R.string.default_folder)
)

fun Uri.tryRequireOriginal(context: Context): Uri {
    val tempUri = this
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        runCatching {
            MediaStore.setRequireOriginal(this).also {
                context.contentResolver.openFileDescriptor(it, FileMode.Read.mode)?.close()
            }
        }.getOrNull() ?: tempUri
    } else this
}

private fun Uri.listFilesInDirectoryAsFlowImpl(): Flow<DirUri> = callbackFlow {
    val rootUri = this@listFilesInDirectoryAsFlowImpl

    var childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
        rootUri,
        DocumentsContract.getTreeDocumentId(rootUri)
    )

    val files: MutableList<Pair<Uri, Long>> = LinkedList()

    val dirNodes: MutableList<Uri> = LinkedList()
    dirNodes.add(childrenUri)
    while (dirNodes.isNotEmpty()) {
        childrenUri = dirNodes.removeAt(0)

        appContext.contentResolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
            ),
            null,
            null,
            null
        ).use {
            while (it!!.moveToNext()) {
                val docId = it.getString(0)
                val lastModified = it.getLong(1)
                val mime = it.getString(2)
                if (isDirectory(mime)) {
                    val newNode = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId)
                    dirNodes.add(newNode)
                } else {
                    val uri = DocumentsContract.buildDocumentUriUsingTree(rootUri, docId)

                    channel.send(DirUri.Entry(uri))

                    files.add(
                        uri to lastModified
                    )
                }
            }
        }
    }

    files.sortedByDescending { it.second }.map { it.first }.also {
        channel.send(DirUri.All(it))
        channel.close()
    }
}

private sealed interface DirUri {
    data class Entry(val uri: Uri) : DirUri
    data class All(val uris: List<Uri>) : DirUri
}

private fun isDirectory(mimeType: String): Boolean {
    return DocumentsContract.Document.MIME_TYPE_DIR == mimeType
}

private fun List<Uri>.sortedByExtension(
    descending: Boolean = false
) = sortedByKey(descending) {
    it.filename()?.substringAfterLast(
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
    it.filename()
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
    it.dateAdded()
}

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
