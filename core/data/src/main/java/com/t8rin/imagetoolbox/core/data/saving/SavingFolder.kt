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

package com.t8rin.imagetoolbox.core.data.saving

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveTarget
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@ConsistentCopyVisibility
internal data class SavingFolder private constructor(
    val outputStream: OutputStream,
    val fileUri: Uri
) : StreamWriteable by StreamWriteable(outputStream) {
    companion object {
        suspend fun getInstance(
            context: Context,
            treeUri: Uri?,
            saveTarget: SaveTarget
        ): SavingFolder? = coroutineScope {
            if (treeUri == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val type = saveTarget.mimeType.entry
                    val path = "${Environment.DIRECTORY_DOCUMENTS}/ImageToolbox"
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, saveTarget.filename)
                        put(
                            MediaStore.MediaColumns.MIME_TYPE,
                            type
                        )
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            path
                        )
                    }
                    val imageUri = context.contentResolver.insert(
                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                        contentValues
                    ) ?: return@coroutineScope null

                    SavingFolder(
                        outputStream = context.contentResolver.openOutputStream(imageUri)
                            ?: return@coroutineScope null,
                        fileUri = imageUri
                    )
                } else {
                    val imagesDir = File(
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS
                        ), "ImageToolbox"
                    )
                    if (!imagesDir.exists()) imagesDir.mkdir()

                    val filename = saveTarget.filename ?: return@coroutineScope null

                    SavingFolder(
                        outputStream = FileOutputStream(File(imagesDir, filename)),
                        fileUri = File(imagesDir, filename).toUri()
                    )
                }
            } else if (DocumentFile.isDocumentUri(context, treeUri)) {
                SavingFolder(
                    outputStream = context.contentResolver.openOutputStream(treeUri)
                        ?: return@coroutineScope null,
                    fileUri = treeUri
                )
            } else {
                val documentFile = DocumentFile.fromTreeUri(context, treeUri)

                if (documentFile?.exists() == false || documentFile == null) {
                    throw NoSuchFileException(File(treeUri.toString()))
                }

                val filename = saveTarget.filename ?: return@coroutineScope null

                val file = documentFile.createFile(saveTarget.mimeType.entry, filename)

                val imageUri = file?.uri ?: return@coroutineScope null

                SavingFolder(
                    outputStream = context.contentResolver.openOutputStream(imageUri)
                        ?: return@coroutineScope null,
                    fileUri = imageUri
                )
            }
        }
    }
}