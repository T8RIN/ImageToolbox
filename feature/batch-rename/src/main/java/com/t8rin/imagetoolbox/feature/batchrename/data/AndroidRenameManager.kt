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

package com.t8rin.imagetoolbox.feature.batchrename.data

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.get
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.core.utils.lastModified
import com.t8rin.imagetoolbox.core.utils.path
import com.t8rin.imagetoolbox.core.utils.tryExtractOriginal
import com.t8rin.imagetoolbox.feature.batchrename.domain.RenameManager
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameFile
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameResult
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameTarget
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

internal class AndroidRenameManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : RenameManager, DispatchersHolder by dispatchersHolder {

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    override suspend fun readFiles(uris: Iterable<String>): List<RenameFile> =
        withContext(ioDispatcher) {
            uris.distinct().map { it.toUri() }.mapNotNull { uri ->
                val name = uri.filename(context)
                    ?.takeIf(String::isNotBlank)
                    ?: uri.lastPathSegment?.substringAfterLast('/')?.takeIf(String::isNotBlank)
                    ?: return@mapNotNull null
                val size = uri.imageSize()
                val providerDates = uri.tryExtractOriginal().providerDates()
                val metadata =
                    runCatching { fileController.readMetadata(uri.toString()) }.getOrNull()
                RenameFile(
                    uri = uri.toString(),
                    originalName = name,
                    width = size?.width,
                    height = size?.height,
                    exifDateTaken = metadata?.exifDateTaken() ?: providerDates.dateTaken,
                    modifiedDate = uri.lastModified() ?: providerDates.modified,
                    createdDate = uri.creationDate() ?: providerDates.created,
                    fileSize = uri.fileSize(),
                    parentFolder = uri.path()?.let { path ->
                        if ('/' in path) {
                            path.substringBeforeLast('/').substringAfterLast('/')
                        } else ""
                    }.orEmpty()
                )
            }
        }

    override suspend fun rename(
        files: List<RenameTarget>,
        writableUris: Set<String>
    ): RenameResult = withContext(ioDispatcher) {
        val changed = files.filter { it.file.originalName != it.newName }
        val unchanged = files.size - changed.size
        if (changed.isEmpty()) return@withContext RenameResult.Success(
            renamed = 0,
            unchanged = unchanged
        )

        requestMediaStorePermissionIfNeeded(changed, writableUris)?.let { return@withContext it }

        val originalNames = changed.map { it.file.originalName.lowercase() }.toSet()
        val hasCycleOrOverlap = changed.any { target ->
            target.newName.lowercase() != target.file.originalName.lowercase() &&
                    target.newName.lowercase() in originalNames
        }

        if (!hasCycleOrOverlap) {
            val renamed = mutableListOf<Pair<RenameTarget, Uri>>()
            changed.forEach { target ->
                val result = runCatching {
                    renameOne(target.file.uri.toUri(), target.newName)
                }
                result.onSuccess { finalUri ->
                    renamed += target to finalUri
                }.onFailure { cause ->
                    renamed.asReversed().forEach { (completed, finalUri) ->
                        runCatching { renameOne(finalUri, completed.file.originalName) }
                    }
                    return@withContext result.permissionResult(target.file.uri.toUri())
                        ?: RenameResult.Failure(
                            renamed = 0,
                            failedNames = changed.map { it.file.originalName },
                            cause = cause
                        )
                }
            }
            return@withContext RenameResult.Success(
                renamed = renamed.size,
                unchanged = unchanged
            )
        }

        val staged = mutableListOf<StagedRename>()
        for (target in changed) {
            val temporaryName = createTemporaryName(target.file.extension)
            val result = runCatching {
                renameOne(
                    uri = target.file.uri.toUri(),
                    newName = temporaryName
                )
            }
            val temporaryUri = result.getOrNull()
            if (temporaryUri == null) {
                rollback(staged)
                return@withContext result.permissionResult(target.file.uri.toUri())
                    ?: RenameResult.Failure(
                        renamed = 0,
                        failedNames = changed.map { it.file.originalName },
                        cause = result.exceptionOrNull()
                    )
            }
            staged += StagedRename(
                temporaryUri = temporaryUri,
                originalName = target.file.originalName,
                finalName = target.newName
            )
        }

        val renamed = mutableListOf<Pair<StagedRename, Uri>>()
        staged.forEachIndexed { index, item ->
            val result = runCatching {
                renameOne(item.temporaryUri, item.finalName)
            }
            result.onSuccess { finalUri ->
                renamed += item to finalUri
            }.onFailure { cause ->
                var rollbackFailed = false
                val finalizedRollback = renamed.mapNotNull { (completed, finalUri) ->
                    runCatching {
                        val extension = completed.originalName.substringAfterLast(
                            delimiter = '.',
                            missingDelimiterValue = ""
                        )
                        completed to renameOne(finalUri, createTemporaryName(extension))
                    }.onFailure {
                        rollbackFailed = true
                    }.getOrNull()
                }
                rollbackFailed = runCatching {
                    renameOne(item.temporaryUri, item.originalName)
                }.isFailure || rollbackFailed
                staged.drop(index + 1).forEach { pending ->
                    rollbackFailed = runCatching {
                        renameOne(pending.temporaryUri, pending.originalName)
                    }.isFailure || rollbackFailed
                }
                finalizedRollback.asReversed().forEach { (completed, rollbackUri) ->
                    rollbackFailed = runCatching {
                        renameOne(rollbackUri, completed.originalName)
                    }.isFailure || rollbackFailed
                }
                return@withContext RenameResult.Failure(
                    renamed = renamed.size.takeIf { rollbackFailed } ?: 0,
                    failedNames = changed.map { it.file.originalName },
                    cause = cause
                )
            }
        }

        return@withContext RenameResult.Success(
            renamed = renamed.size,
            unchanged = unchanged
        )
    }

    private fun Metadata.exifDateTaken(): Long? {
        val rawDate = this[MetadataTag.DatetimeOriginal]
            ?: this[MetadataTag.DatetimeDigitized]
            ?: return null
        return EXIF_DATE_FORMATS.firstNotNullOfOrNull { format ->
            runCatching {
                SimpleDateFormat(format, Locale.getDefault()).parse(rawDate)?.time
            }.getOrNull()
        }
    }

    private fun Uri.creationDate(): Long? {
        val file = directFile(this) ?: return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
                    .creationTime()
                    .toMillis()
            }.getOrNull()
        } else {
            null
        }
    }

    private fun Uri.providerDates(): ProviderDates = runCatching {
        contentResolver.query(this, null, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use ProviderDates()

            fun millis(column: String, storedInSeconds: Boolean): Long? {
                val index = cursor.getColumnIndex(column)
                if (index < 0 || cursor.isNull(index)) return null
                return cursor.getLong(index).takeIf { it > 0 }?.let {
                    if (storedInSeconds) {
                        it * MILLIS_IN_SECOND
                    } else {
                        it
                    }
                }
            }

            ProviderDates(
                dateTaken = millis(MediaStore.MediaColumns.DATE_TAKEN, false),
                modified = millis(MediaStore.MediaColumns.DATE_MODIFIED, true),
                created = millis(MediaStore.MediaColumns.DATE_ADDED, true)
            )
        }
    }.getOrNull() ?: ProviderDates()

    private suspend fun rollback(staged: List<StagedRename>) {
        staged.asReversed().forEach { item ->
            runCatching { renameOne(item.temporaryUri, item.originalName) }
        }
    }

    private suspend fun renameOne(uri: Uri, newName: String): Uri {
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            val file = uri.path?.let(::File)?.takeIf(File::exists)
                ?: error("File does not exist: $uri")
            return renameFile(file, newName)
        }

        check(uri.scheme == ContentResolver.SCHEME_CONTENT) { "Unsupported URI: $uri" }

        val providerResult = runCatching {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                var lastException: Throwable? = null
                repeat(3) { attempt ->
                    runCatching {
                        DocumentsContract.renameDocument(contentResolver, uri, newName)
                    }.onSuccess {
                        if (it != null) return@runCatching it
                    }.onFailure {
                        lastException = it
                    }
                    if (attempt < 2) delay(100)
                }
                lastException?.let { throw it }
            }
            val values = ContentValues(1).apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, newName)
            }
            contentResolver.update(uri, values, null, null)
                .takeIf { it > 0 }
                ?.let { uri }
                ?: error("The content provider did not rename the file")
        }

        providerResult.getOrNull()?.let { return it }

        directFile(uri)?.let { return renameFile(it, newName) }
        throw providerResult.exceptionOrNull() ?: error("Failed to rename $uri")
    }

    private fun renameFile(file: File, newName: String): Uri {
        val destination = File(file.parentFile, newName)
        check(!destination.exists()) { "A file named $newName already exists" }
        check(file.renameTo(destination)) { "Failed to rename ${file.name}" }
        return Uri.fromFile(destination)
    }

    private fun directFile(uri: Uri): File? {
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            return uri.path?.let(::File)?.takeIf(File::exists)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || !Environment.isExternalStorageManager()) {
            return null
        }
        return uri.path()?.let(::File)?.takeIf(File::exists)
    }

    private fun requestMediaStorePermissionIfNeeded(
        targets: List<RenameTarget>,
        writableUris: Set<String>
    ): RenameResult.PermissionRequired? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
            return null
        }

        val uris = targets.asSequence()
            .map { it.file.uri.toUri() }
            .filter { it.authority == MediaStore.AUTHORITY }
            .mapNotNull { it.toMediaStoreItemUri() }
            .filterNot { writableUris.contains(it.toString()) }
            .filterNot(::canWriteWithoutRequest)
            .distinct()
            .take(MAX_WRITE_REQUEST_SIZE)
            .toList()
        if (uris.isEmpty()) return null

        return RenameResult.PermissionRequired(
            intentSender = MediaStore.createWriteRequest(contentResolver, uris).intentSender,
            uris = uris.mapTo(mutableSetOf()) { it.toString() }
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun canWriteWithoutRequest(uri: Uri): Boolean {
        val hasGrant = context.checkUriPermission(
            uri,
            Process.myPid(),
            Process.myUid(),
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasGrant) return true

        return runCatching {
            contentResolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns.OWNER_PACKAGE_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                cursor.moveToFirst() && cursor.getString(0) == context.packageName
            }
        }.getOrNull() == true
    }

    private fun Result<Uri>.permissionResult(uri: Uri): RenameResult.PermissionRequired? {
        val exception = exceptionOrNull()
        return when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && exception is RecoverableSecurityException -> {
                RenameResult.PermissionRequired(
                    intentSender = exception.userAction.actionIntent.intentSender,
                    uris = setOf(uri.toString())
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && exception is SecurityException -> {
                val mediaStoreUri = uri.toMediaStoreItemUri() ?: return null
                RenameResult.PermissionRequired(
                    intentSender = MediaStore.createWriteRequest(
                        contentResolver,
                        listOf(mediaStoreUri)
                    ).intentSender,
                    uris = setOf(mediaStoreUri.toString())
                )
            }

            else -> null
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun Uri.toMediaStoreItemUri(): Uri? {
        if (authority == MediaStore.AUTHORITY && lastPathSegment?.toLongOrNull() != null) {
            return this
        }
        if (authority != MEDIA_DOCUMENTS_AUTHORITY) return null

        val parts = runCatching { DocumentsContract.getDocumentId(this) }
            .getOrNull()
            ?.split(':', limit = 2)
            ?.takeIf { it.size == 2 }
            ?: return null
        val id = parts[1].toLongOrNull() ?: return null
        val collection = when (parts[0]) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        }
        return ContentUris.withAppendedId(collection, id)
    }

    private fun createTemporaryName(extension: String): String = buildString {
        append("ImageToolbox_temp_")
        append(UUID.randomUUID().toString().replace("-", ""))
        if (extension.isNotEmpty()) {
            append('.')
            append(extension)
        }
    }

    private data class StagedRename(
        val temporaryUri: Uri,
        val originalName: String,
        val finalName: String
    )

    private data class ProviderDates(
        val dateTaken: Long? = null,
        val modified: Long? = null,
        val created: Long? = null
    )

    private companion object {
        const val MAX_WRITE_REQUEST_SIZE = 2000
        const val MILLIS_IN_SECOND = 1000
        const val MEDIA_DOCUMENTS_AUTHORITY = "com.android.providers.media.documents"
        val EXIF_DATE_FORMATS = listOf(
            "yyyy:MM:dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy:MM:dd HH:mm:ssXXX"
        )
    }
}
