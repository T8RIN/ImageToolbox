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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.coroutineScope
import ru.tech.imageresizershrinker.core.resources.R
import java.util.LinkedList


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

suspend fun Activity.listFilesInDirectory(
    rootUri: Uri
): List<Uri> = coroutineScope {
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
                    files.add(
                        DocumentsContract.buildDocumentUriUsingTree(
                            rootUri,
                            docId
                        ) to lastModified
                    )
                }
            }
        }
    }

    files.sortedByDescending { it.second }.map { it.first }
}

private fun isDirectory(mimeType: String): Boolean {
    return DocumentsContract.Document.MIME_TYPE_DIR == mimeType
}