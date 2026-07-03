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
import android.content.ContentUris
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.utils.UriReplacements
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
    private val pendingDeleteReplacements = ConcurrentHashMap<Uri, UriReplacement>()
    private val nextDeleteRequestId = AtomicLong()
    private val pendingDeleteLock = Any()
    private var pendingDeleteJob: Job? = null
    private val pendingDeletedCount = AtomicInteger()
    private val pendingFailedCount = AtomicInteger()
    private val deleteResultLock = Any()
    private var deleteResultJob: Job? = null

    fun deleteAfterSuccessfulSave(
        originalUri: String,
        outputUri: Uri
    ) {
        val sourceUri = originalUri
            .takeIf(String::isNotBlank)
            ?.toUri()
            ?: return
        val resolvedUri = UriReplacements.resolve(sourceUri)

        if (resolvedUri == Uri.EMPTY || resolvedUri == outputUri) return

        val mediaStoreUri = resolvedUri.toMediaStoreItemUri()
        if (mediaStoreUri != null && mediaStoreUri == outputUri.toMediaStoreItemUri()) return

        val replacement = UriReplacement(
            sourceUri = sourceUri,
            originalUri = resolvedUri,
            outputUri = outputUri
        )
        val documentDeleteResult = runCatching {
            DocumentFile.fromSingleUri(context, resolvedUri)?.delete() == true
        }

        if (documentDeleteResult.getOrDefault(false)) {
            onOriginalDeleted(replacement)
            return
        }

        documentDeleteResult.exceptionOrNull()?.let { throwable ->
            if (!resolvedUri.exists()) {
                onOriginalDeleted(replacement)
                return
            }

            if (throwable is SecurityException) {
                handleDeleteSecurityException(
                    throwable = throwable,
                    mediaStoreUri = mediaStoreUri,
                    replacement = replacement
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
                onOriginalDeleted(replacement)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && mediaStoreUri != null) {
                enqueueDeletePermissionRequest(
                    uri = mediaStoreUri,
                    replacement = replacement
                )
            } else {
                emitDeleteResult(deleted = 0, failed = 1)
            }
        } catch (throwable: SecurityException) {
            if (!resolvedUri.exists()) {
                onOriginalDeleted(replacement)
            } else {
                handleDeleteSecurityException(
                    throwable = throwable,
                    mediaStoreUri = mediaStoreUri,
                    replacement = replacement
                )
            }
        } catch (_: Throwable) {
            if (!resolvedUri.exists()) {
                onOriginalDeleted(replacement)
            } else {
                emitDeleteResult(deleted = 0, failed = 1)
            }
        }
    }

    private fun handleDeleteSecurityException(
        throwable: SecurityException,
        mediaStoreUri: Uri?,
        replacement: UriReplacement
    ) {
        when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q &&
                    throwable is RecoverableSecurityException -> {
                emitRecoverableDeletePermissionRequest(
                    uri = mediaStoreUri ?: replacement.originalUri,
                    throwable = throwable,
                    replacement = replacement
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && mediaStoreUri != null -> {
                enqueueDeletePermissionRequest(
                    uri = mediaStoreUri,
                    replacement = replacement
                )
            }

            else -> emitDeleteResult(deleted = 0, failed = 1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun enqueueDeletePermissionRequest(
        uri: Uri,
        replacement: UriReplacement
    ) {
        pendingDeleteUris.add(uri)
        pendingDeleteReplacements[uri] = replacement
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
                        uris.forEach(pendingDeleteReplacements::remove)
                        emitDeleteResult(deleted = 0, failed = uris.size)
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
        replacement: UriReplacement
    ) {
        pendingDeleteReplacements[uri] = replacement
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
            pendingDeleteReplacements.remove(uri)?.let { replacement ->
                if (granted) registerUriReplacement(replacement)
            }
        }
        emitDeleteResult(
            deleted = uris.size.takeIf { granted } ?: 0,
            failed = uris.size.takeUnless { granted } ?: 0
        )
    }

    private fun onOriginalDeleted(replacement: UriReplacement) {
        registerUriReplacement(replacement)
        emitDeleteResult(deleted = 1, failed = 0)
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
}

private const val MEDIA_DOCUMENTS_AUTHORITY = "com.android.providers.media.documents"

private data class UriReplacement(
    val sourceUri: Uri,
    val originalUri: Uri,
    val outputUri: Uri
)