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

package com.t8rin.imagetoolbox.core.data.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.domain.utils.FileMode
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.OutputStream
import java.net.URLDecoder
import java.util.LinkedList

fun ImageModel.toUri(): Uri? = when (data) {
    is Uri -> data as Uri
    is String -> (data as String).toUri()
    else -> null
}

private fun isDirectory(mimeType: String): Boolean {
    return DocumentsContract.Document.MIME_TYPE_DIR == mimeType
}

internal suspend fun Context.listFilesInDirectory(
    rootUri: Uri
): List<Uri> = listFilesInDirectoryAsFlowImpl(rootUri).filterIsInstance<DirUri.All>().first().uris

internal fun Context.listFilesInDirectoryProgressive(
    rootUri: Uri
): Flow<Uri> = listFilesInDirectoryAsFlowImpl(rootUri)
    .filterIsInstance<DirUri.Entry>()
    .map { it.uri }

private fun Context.listFilesInDirectoryAsFlowImpl(
    rootUri: Uri
): Flow<DirUri> = callbackFlow {
    var childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
        rootUri,
        DocumentsContract.getTreeDocumentId(rootUri)
    )

    val files: MutableList<Pair<Uri, Long>> = LinkedList()

    val dirNodes: MutableList<Uri> = LinkedList()
    dirNodes.add(childrenUri)
    while (dirNodes.isNotEmpty()) {
        childrenUri = dirNodes.removeAt(0)

        contentResolver.query(
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

fun Uri.fileSize(): Long? {
    if (this.toString().isEmpty()) return null

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

fun String?.getPath(
    context: Context
) = this?.takeIf { it.isNotEmpty() }?.toUri().toUiPath(
    context = context,
    default = context.getString(R.string.default_folder)
)

fun Uri?.toUiPath(
    context: Context,
    default: String,
    isTreeUri: Boolean = true
): String = this?.let { uri ->
    runCatching {
        val document = if (isTreeUri) DocumentFile.fromTreeUri(context, uri)
        else DocumentFile.fromSingleUri(context, uri)

        document?.uri?.path?.split(":")
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
    }.getOrNull()
} ?: default

fun Context.getFileDescriptorFor(
    uri: Uri?
): ParcelFileDescriptor? = uri?.let {
    runCatching {
        openFileDescriptor(
            uri = uri,
            mode = FileMode.ReadWrite
        )
    }.getOrNull()
}

internal fun Uri.tryRequireOriginal(context: Context): Uri {
    val tempUri = this
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        runCatching {
            MediaStore.setRequireOriginal(this).also {
                context.openFileDescriptor(it)?.close()
            }
        }.getOrNull() ?: tempUri
    } else this
}

fun Uri.getFilename(
    context: Context = appContext
): String? = if (this.toString().startsWith("file:///")) {
    this.toString().takeLastWhile { it != '/' }
} else {
    DocumentFile.fromSingleUri(context, this)?.name
}?.decodeEscaped()

fun String.decodeEscaped(): String = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        URLDecoder.decode(URLDecoder.decode(this, Charsets.UTF_8), Charsets.UTF_8)
    } else {
        @Suppress("DEPRECATION")
        URLDecoder.decode(URLDecoder.decode(this))
    }
}.getOrDefault(this)

fun Writeable.outputStream(): OutputStream = if (this is StreamWriteable) {
    stream
} else {
    object : OutputStream() {
        override fun write(b: Int) = writeBytes(byteArrayOf(b.toByte()))
    }
}