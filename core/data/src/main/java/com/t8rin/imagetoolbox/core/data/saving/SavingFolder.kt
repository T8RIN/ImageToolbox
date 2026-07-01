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
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveTarget
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.core.utils.path
import com.t8rin.imagetoolbox.core.utils.tryExtractOriginal
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@ConsistentCopyVisibility
internal data class SavingFolder private constructor(
    val outputStream: OutputStream,
    val fileUri: Uri,
    private val savingPath: String? = null
) : StreamWriteable by StreamWriteable(outputStream) {

    val normalizedSavingPath = savingPath?.removeSuffix("/")?.removePrefix("/storage/emulated/0/")

    companion object {
        suspend fun getInstance(
            context: Context,
            treeUri: Uri?,
            saveTarget: SaveTarget,
            saveToOriginalFolder: Boolean
        ): SavingFolder? = coroutineScope {
            val originalFolder = if (saveToOriginalFolder) {
                runCatching {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                        createLegacyFile(
                            saveTarget = saveTarget,
                            parent = saveTarget.originalParentFile(context)
                        )
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        saveTarget.originalRelativePath(context)?.let {
                            context.createViaMediaStore(
                                saveTarget = saveTarget,
                                relativePath = it
                            )
                        }
                    } else {
                        createLegacyFile(
                            saveTarget = saveTarget,
                            parent = saveTarget.originalParentFile(context)
                        )
                    }
                }.onFailure { it.makeLog("saveToOriginalFolder") }.getOrNull()
            } else null

            originalFolder ?: if (treeUri == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.createViaMediaStore(
                        saveTarget = saveTarget,
                        relativePath = null
                    )
                } else {
                    createDefaultFolderFile(saveTarget)
                }
            } else if (DocumentFile.isDocumentUri(context, treeUri)) {
                SavingFolder(
                    outputStream = context.contentResolver.openOutputStream(treeUri)
                        ?: return@coroutineScope null,
                    fileUri = treeUri,
                    savingPath = treeUri.path()
                )
            } else {
                val documentFile = DocumentFile.fromTreeUri(context, treeUri)

                if (documentFile == null || !documentFile.exists()) {
                    return@coroutineScope null
                }

                val filename = saveTarget.filename ?: return@coroutineScope null

                val file = documentFile.createFile(
                    saveTarget.mimeType.entry,
                    filename
                )

                val imageUri = file?.uri ?: return@coroutineScope null

                SavingFolder(
                    outputStream = context.contentResolver.openOutputStream(imageUri)
                        ?: return@coroutineScope null,
                    fileUri = imageUri,
                    savingPath = documentFile.uri.path()
                )
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun Context.createViaMediaStore(
            saveTarget: SaveTarget,
            relativePath: String?
        ): SavingFolder? {
            val filename = saveTarget.filename ?: return null
            val mimeType = saveTarget.mimeType.entry

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    relativePath ?: "${Environment.DIRECTORY_DOCUMENTS}/ImageToolbox"
                )
            }

            val collectionUri = when {
                mimeType.startsWith("image/") -> MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )

                mimeType.startsWith("video/") -> MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )

                mimeType.startsWith("audio/") -> MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )

                else -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }

            val uri = contentResolver.insert(collectionUri, contentValues) ?: return null

            return SavingFolder(
                outputStream = contentResolver.openOutputStream(uri)
                    ?: return null,
                fileUri = uri,
                savingPath = relativePath ?: "${Environment.DIRECTORY_DOCUMENTS}/ImageToolbox"
            )
        }

        private fun createDefaultFolderFile(
            saveTarget: SaveTarget
        ): SavingFolder? = createLegacyFile(
            saveTarget = saveTarget,
            parent = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS
                ),
                "ImageToolbox"
            )
        )

        private fun createLegacyFile(
            saveTarget: SaveTarget,
            parent: File?
        ): SavingFolder? {
            val filename = saveTarget.filename ?: return null
            val dir = parent ?: return null

            if (!dir.exists()) dir.mkdirs()
            if (!dir.exists() || !dir.isDirectory) return null

            val file = File(dir, filename)

            return SavingFolder(
                outputStream = FileOutputStream(file),
                fileUri = file.toUri(),
                savingPath = dir.absolutePath
            )
        }

        private fun SaveTarget.originalParentFile(context: Context): File? {
            val originalUri = (this as? ImageSaveTarget)
                ?.originalUri
                ?.toUri()
                ?: return null

            val originalPath = originalUri.externalStoragePath(context) ?: originalUri.path()
                ?.takeIf { it.isNotBlank() }
            ?: return null

            return File(originalPath)
                .parentFile
                ?.takeIf { it.exists() && it.isDirectory }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun SaveTarget.originalRelativePath(
            context: Context
        ): String? {
            val originalUri = (this as? ImageSaveTarget)
                ?.originalUri
                ?.toUri()
                ?.tryExtractOriginal()
                ?: return null

            originalUri.externalStorageRelativePath(context)?.let { return it }

            return context.contentResolver.query(
                originalUri,
                arrayOf(MediaStore.MediaColumns.RELATIVE_PATH),
                null,
                null,
                null
            )?.use { cursor ->
                if (!cursor.moveToFirst()) return@use null

                cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
                )
            }?.takeIf {
                it.isNotBlank() && ".transforms" !in it
            }
        }

        private fun Uri.externalStorageRelativePath(context: Context): String? = runCatching {
            if (!DocumentsContract.isDocumentUri(context, this)) return@runCatching null

            val documentId = DocumentsContract.getDocumentId(this)
            val type = documentId.substringBefore(':')
            val path = documentId.substringAfter(':', "")

            if (type.equals("primary", ignoreCase = true)) {
                path.substringBeforeLast('/', "")
                    .takeIf { it.isNotBlank() }
                    ?.plus('/')
            } else null
        }.onFailure { it.makeLog("externalStorageRelativePath") }.getOrNull()

        private fun Uri.externalStoragePath(context: Context): String? = runCatching {
            if (!DocumentsContract.isDocumentUri(context, this)) return@runCatching null

            val documentId = DocumentsContract.getDocumentId(this)
            val type = documentId.substringBefore(':')
            val path = documentId.substringAfter(':', "")

            if (type.equals("primary", ignoreCase = true) && path.isNotBlank()) {
                File(Environment.getExternalStorageDirectory(), path).absolutePath
            } else null
        }.onFailure { it.makeLog("externalStoragePath") }.getOrNull()
    }
}