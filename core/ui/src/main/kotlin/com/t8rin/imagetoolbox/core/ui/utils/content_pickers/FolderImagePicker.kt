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

package com.t8rin.imagetoolbox.core.ui.utils.content_pickers

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.utils.listFilesInDirectoryProgressive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal fun interface FolderImagePicker {
    fun pickFolder(allowMultiple: Boolean)
}

@Composable
internal fun rememberFolderImagePicker(
    initialFolderUri: Uri? = LocalSettingsState.current.saveFolderUri,
    onFailure: () -> Unit,
    onSuccess: (List<Uri>) -> Unit
): FolderImagePicker {
    val context = LocalComponentActivity.current
    val eventEmitter = LocalImagePickerEventEmitter.current
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val allowMultiple = remember { mutableStateOf(false) }

    val folderPicker = rememberFolderPicker(
        onFailure = onFailure,
        onSuccess = { folderUri ->
            scope.launch {
                val requestId = eventEmitter.onFolderProcessingStarted(
                    onCancel = coroutineContext.job::cancel
                )
                try {
                    val targetAllowMultiple = allowMultiple.value
                    val uris = withContext(Dispatchers.IO) {
                        folderUri.loadableImagesFromFolder(
                            context = context,
                            limit = if (targetAllowMultiple) Int.MAX_VALUE else 1,
                            onProgress = { count ->
                                eventEmitter.onFolderProcessingProgress(
                                    requestId = requestId,
                                    count = count
                                )
                            }
                        )
                    }

                    uris.takeIf { it.isNotEmpty() }?.let(onSuccess) ?: onFailure()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    onFailure()
                    AppToastHost.handleFileSystemFailure(e)
                } finally {
                    eventEmitter.onFolderProcessingFinished(requestId)
                }
            }
        }
    )

    return remember(folderPicker, initialFolderUri) {
        FolderImagePicker { targetAllowMultiple ->
            allowMultiple.value = targetAllowMultiple
            folderPicker.pickFolder(initialFolderUri)
        }
    }
}

suspend fun Uri.loadableImagesFromFolder(
    context: Context,
    limit: Int = Int.MAX_VALUE,
    onProgress: suspend (count: Int) -> Unit = {}
): List<Uri> {
    var count = 0

    return listFilesInDirectoryProgressive()
        .mapNotNull { uri ->
            uri.takeIf { it.isLoadableImage(context) }
        }
        .onEach {
            onProgress(++count)
        }
        .take(limit)
        .toList()
}

private suspend fun Uri.isLoadableImage(context: Context): Boolean {
    if (EXCLUDED.any { toString().endsWith(".$it", true) }) return false

    val mime = context.contentResolver.getType(this).orEmpty()
    if ("audio" in mime || "video" in mime) return false

    return try {
        val result = context.imageLoader.execute(
            ImageRequest.Builder(context)
                .data(this)
                .size(8)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()
        )
        result !is ErrorResult && result.image != null
    } catch (e: CancellationException) {
        throw e
    } catch (_: Throwable) {
        false
    }
}

private val EXCLUDED = listOf(
    "xml",
    "mov",
    "zip",
    "apk",
    "mp4",
    "mp3",
    "pdf",
    "ldb",
    "ttf",
    "gz",
    "rar"
)