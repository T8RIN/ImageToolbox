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

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.system.Os
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.utils.UriReplacements
import com.t8rin.imagetoolbox.core.utils.distinctUris
import com.t8rin.imagetoolbox.core.utils.tryExtractOriginal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class OriginalFileDeletionHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appScope: AppScope
) : FileControllerEventEmitter {

    private val eventChannel = Channel<FileControllerEvent>(Channel.UNLIMITED)
    override val events: Flow<FileControllerEvent> = eventChannel.receiveAsFlow()

    private val pendingDeleteUris = ConcurrentHashMap.newKeySet<Uri>()
    private val pendingDeleteRequests = ConcurrentHashMap<Long, List<Uri>>()
    private val pendingDeleteOperations = ConcurrentHashMap<Uri, DeleteOperation>()
    private val nextDeleteRequestId = AtomicLong()
    private val pendingDeleteLock = Any()
    private var pendingDeleteJob: Job? = null
    private val pendingDeletedCount = AtomicInteger()
    private val pendingFailedCount = AtomicInteger()
    private val deleteResultLock = Any()
    private var deleteResultJob: Job? = null

    override fun deleteFiles(
        uris: List<String>,
        onResult: (FileDeletionResult) -> Unit
    ) {
        val distinctUris = uris
            .map { it.toUri().tryExtractOriginal() }
            .distinctUris()
            .map(Uri::toString)
        if (distinctUris.isEmpty()) {
            onResult(FileDeletionResult(emptyList(), emptyList()))
            return
        }

        val batch = DeleteBatch(
            size = distinctUris.size,
            onResult = { result ->
                emitDeleteResult(
                    deleted = result.deletedUris.size,
                    failed = result.failedUris.size
                )
                onResult(result)
            }
        )
        appScope.launch {
            distinctUris.forEach { source ->
                val sourceUri = source.toUri()
                val resolvedUri = UriReplacements.resolve(sourceUri)
                deleteFile(
                    DeleteOperation(
                        originalUri = resolvedUri,
                        onResult = { deleted ->
                            batch.complete(source, deleted)
                        }
                    )
                )
            }
        }
    }

    fun deleteAfterSuccessfulSave(
        originalUri: String,
        outputUri: Uri
    ) {
        val sourceUri = originalUri
            .takeIf(String::isNotBlank)
            ?.toUri()
            ?: return
        val resolvedUri = UriReplacements.resolve(sourceUri)

        if (resolvedUri.scheme !in DELETABLE_URI_SCHEMES) return
        if (resolvedUri == outputUri) return

        val mediaStoreUri = resolvedUri.toMediaStoreItemUri()
        val outputMediaStoreUri = outputUri.toMediaStoreItemUri()
        if (mediaStoreUri != null && outputMediaStoreUri != null) {
            if (mediaStoreUri == outputMediaStoreUri) return
        } else if (resolvedUri.refersToSameFileAs(outputUri)) {
            return
        }

        val replacement = UriReplacement(
            sourceUri = sourceUri,
            originalUri = resolvedUri,
            outputUri = outputUri
        )
        deleteFile(
            DeleteOperation(
                originalUri = resolvedUri,
                onResult = { deleted ->
                    if (deleted) registerUriReplacement(replacement)
                    emitDeleteResult(
                        deleted = if (deleted) 1 else 0,
                        failed = if (deleted) 0 else 1
                    )
                }
            )
        )
    }

    private fun deleteFile(operation: DeleteOperation) {
        val resolvedUri = operation.originalUri

        if (resolvedUri.scheme !in DELETABLE_URI_SCHEMES) {
            operation.complete(deleted = false)
            return
        }

        if (resolvedUri.scheme == ContentResolver.SCHEME_FILE) {
            val file = resolvedUri.path?.let(::File)
            if (file == null) {
                operation.complete(deleted = false)
                return
            }

            operation.complete(deleted = !file.exists() || file.delete() || !file.exists())
            return
        }

        val mediaStoreUri = resolvedUri.toMediaStoreItemUri()
        val documentDeleteResult = runCatching {
            DocumentFile.fromSingleUri(context, resolvedUri)?.delete() == true
        }

        if (documentDeleteResult.getOrDefault(false)) {
            operation.complete(deleted = true)
            return
        }

        documentDeleteResult.exceptionOrNull()?.let { throwable ->
            if (!resolvedUri.exists()) {
                operation.complete(deleted = true)
                return
            }

            if (throwable is SecurityException) {
                handleDeleteSecurityException(
                    throwable = throwable,
                    mediaStoreUri = mediaStoreUri,
                    operation = operation
                )
                return
            }
        }

        try {
            val deleted = context.contentResolver.delete(
                mediaStoreUri ?: resolvedUri,
                null,
                null
            )

            if (deleted > 0 || !resolvedUri.exists()) {
                operation.complete(deleted = true)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && mediaStoreUri != null) {
                enqueueDeletePermissionRequest(
                    uri = mediaStoreUri,
                    operation = operation
                )
            } else {
                operation.complete(deleted = false)
            }
        } catch (throwable: SecurityException) {
            if (!resolvedUri.exists()) {
                operation.complete(deleted = true)
            } else {
                handleDeleteSecurityException(
                    throwable = throwable,
                    mediaStoreUri = mediaStoreUri,
                    operation = operation
                )
            }
        } catch (_: Throwable) {
            if (!resolvedUri.exists()) {
                operation.complete(deleted = true)
            } else {
                operation.complete(deleted = false)
            }
        }
    }

    private fun handleDeleteSecurityException(
        throwable: SecurityException,
        mediaStoreUri: Uri?,
        operation: DeleteOperation
    ) {
        when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q &&
                    throwable is RecoverableSecurityException -> {
                emitRecoverableDeletePermissionRequest(
                    uri = mediaStoreUri ?: operation.originalUri,
                    throwable = throwable,
                    operation = operation
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && mediaStoreUri != null -> {
                enqueueDeletePermissionRequest(
                    uri = mediaStoreUri,
                    operation = operation
                )
            }

            else -> operation.complete(deleted = false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun enqueueDeletePermissionRequest(
        uri: Uri,
        operation: DeleteOperation
    ) {
        pendingDeleteUris.add(uri)
        pendingDeleteOperations[uri] = operation
        scheduleDeletePermissionRequest()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun scheduleDeletePermissionRequest() {
        synchronized(pendingDeleteLock) {
            if (pendingDeleteJob?.isActive == true) return

            pendingDeleteJob = appScope.launch {
                delay(500L)

                val uris = pendingDeleteUris.toList().distinct()
                pendingDeleteUris.removeAll(uris.toSet())
                if (uris.isNotEmpty()) {
                    runCatching {
                        MediaStore.createDeleteRequest(
                            context.contentResolver,
                            uris
                        ).intentSender
                    }.onSuccess { intentSender ->
                        emitDeletePermissionRequest(
                            uris = uris,
                            intentSender = intentSender
                        )
                    }.onFailure {
                        uris.forEach { uri ->
                            pendingDeleteOperations.remove(uri)?.complete(deleted = false)
                        }
                    }
                }

                synchronized(pendingDeleteLock) {
                    pendingDeleteJob = null
                }
                if (pendingDeleteUris.isNotEmpty()) {
                    scheduleDeletePermissionRequest()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun emitRecoverableDeletePermissionRequest(
        uri: Uri,
        throwable: RecoverableSecurityException,
        operation: DeleteOperation
    ) {
        pendingDeleteOperations[uri] = operation
        emitDeletePermissionRequest(
            uris = listOf(uri),
            intentSender = throwable.userAction.actionIntent.intentSender
        )
    }

    private fun emitDeletePermissionRequest(
        uris: List<Uri>,
        intentSender: IntentSender
    ) {
        val requestId = nextDeleteRequestId.incrementAndGet()
        pendingDeleteRequests[requestId] = uris
        eventChannel.trySend(
            FileControllerEvent.RequestDeleteOriginalsPermission(
                requestId = requestId,
                intentSender = intentSender,
                count = uris.size
            )
        )
    }

    override fun onDeleteOriginalsPermissionResult(
        requestId: Long,
        granted: Boolean
    ) {
        val uris = pendingDeleteRequests.remove(requestId) ?: return
        uris.forEach { uri ->
            pendingDeleteOperations.remove(uri)?.complete(deleted = granted)
        }
    }

    private fun registerUriReplacement(replacement: UriReplacement) {
        UriReplacements.register(
            originalUri = replacement.sourceUri,
            replacementUri = replacement.outputUri
        )
        UriReplacements.register(
            originalUri = replacement.originalUri,
            replacementUri = replacement.outputUri
        )
    }

    private fun emitDeleteResult(
        deleted: Int,
        failed: Int
    ) {
        pendingDeletedCount.addAndGet(deleted)
        pendingFailedCount.addAndGet(failed)

        synchronized(deleteResultLock) {
            if (deleteResultJob?.isActive == true) return

            deleteResultJob = appScope.launch {
                delay(300L)
                val deletedCount = pendingDeletedCount.getAndSet(0)
                val failedCount = pendingFailedCount.getAndSet(0)

                if (deletedCount + failedCount > 0) {
                    eventChannel.send(
                        FileControllerEvent.OriginalFilesDeleteResult(
                            deleted = deletedCount,
                            failed = failedCount
                        )
                    )
                }

                synchronized(deleteResultLock) {
                    deleteResultJob = null
                }
                if (pendingDeletedCount.get() + pendingFailedCount.get() > 0) {
                    emitDeleteResult(deleted = 0, failed = 0)
                }
            }
        }
    }

    private fun Uri.exists(): Boolean {
        if (scheme == "file") return path?.let(::File)?.exists() == true

        val queryResult = runCatching {
            context.contentResolver.query(
                this,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor -> cursor.moveToFirst() }
        }.getOrNull()

        if (queryResult == true) {
            return runCatching {
                context.contentResolver.openFileDescriptor(this, "r")?.use { true } ?: false
            }.getOrElse { it is SecurityException }
        }

        return queryResult ?: runCatching {
            DocumentFile.fromSingleUri(context, this)?.exists()
        }.getOrNull() ?: true
    }

    private fun Uri.toMediaStoreItemUri(): Uri? {
        if (authority == MediaStore.AUTHORITY) {
            return takeIf { lastPathSegment?.toLongOrNull() != null }
        }
        if (authority != MEDIA_DOCUMENTS_AUTHORITY) return null

        val documentId = runCatching {
            DocumentsContract.getDocumentId(this)
        }.getOrNull() ?: return null
        val (type, idString) = documentId.split(':', limit = 2).takeIf {
            it.size == 2
        } ?: return null
        val id = idString.toLongOrNull() ?: return null

        val collection = when (type) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> return null
        }

        return ContentUris.withAppendedId(collection, id)
    }

    private fun Uri.refersToSameFileAs(other: Uri): Boolean {
        if (this == other) return true

        val firstFile = takeIf { scheme == ContentResolver.SCHEME_FILE }
            ?.path
            ?.let(::File)
        val secondFile = other.takeIf { it.scheme == ContentResolver.SCHEME_FILE }
            ?.path
            ?.let(::File)
        if (firstFile != null && secondFile != null) {
            return runCatching {
                firstFile.canonicalFile == secondFile.canonicalFile
            }.getOrDefault(false)
        }

        return runCatching {
            openFileDescriptor()?.use { firstDescriptor ->
                other.openFileDescriptor()?.use { secondDescriptor ->
                    val firstStat = Os.fstat(firstDescriptor.fileDescriptor)
                    val secondStat = Os.fstat(secondDescriptor.fileDescriptor)

                    firstStat.st_ino != 0L &&
                            firstStat.st_dev == secondStat.st_dev &&
                            firstStat.st_ino == secondStat.st_ino
                }
            }
        }.getOrNull() == true
    }

    private fun Uri.openFileDescriptor(): ParcelFileDescriptor? = when (scheme) {
        ContentResolver.SCHEME_FILE -> path?.let(::File)?.let {
            ParcelFileDescriptor.open(it, ParcelFileDescriptor.MODE_READ_ONLY)
        }

        ContentResolver.SCHEME_CONTENT -> context.contentResolver.openFileDescriptor(this, "r")
        else -> null
    }
}

private class DeleteBatch(
    size: Int,
    private val onResult: (FileDeletionResult) -> Unit
) {
    private val remaining = AtomicInteger(size)
    private val deletedUris = mutableListOf<String>()
    private val failedUris = mutableListOf<String>()

    fun complete(
        uri: String,
        deleted: Boolean
    ) {
        val result = synchronized(this) {
            if (deleted) deletedUris += uri else failedUris += uri

            if (remaining.decrementAndGet() == 0) {
                FileDeletionResult(
                    deletedUris = deletedUris.toList(),
                    failedUris = failedUris.toList()
                )
            } else null
        }
        result?.let(onResult)
    }
}

private data class DeleteOperation(
    val originalUri: Uri,
    val onResult: (Boolean) -> Unit
) {
    fun complete(deleted: Boolean) = onResult(deleted)
}

private const val MEDIA_DOCUMENTS_AUTHORITY = "com.android.providers.media.documents"

private val DELETABLE_URI_SCHEMES = setOf(
    ContentResolver.SCHEME_CONTENT,
    ContentResolver.SCHEME_FILE
)

private data class UriReplacement(
    val sourceUri: Uri,
    val originalUri: Uri,
    val outputUri: Uri
)