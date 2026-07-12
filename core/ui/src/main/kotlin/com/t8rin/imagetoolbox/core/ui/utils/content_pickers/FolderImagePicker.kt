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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.utils.listFilesInDirectoryProgressive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal fun interface FolderImagePicker {
    fun pickFolder()
}

@Composable
internal fun rememberFolderImagePicker(
    initialFolderUri: Uri? = LocalSettingsState.current.saveFolderUri,
    onFailure: () -> Unit,
    onSuccess: (List<Uri>) -> Unit
): FolderImagePicker {
    val context = LocalComponentActivity.current
    val eventEmitter = LocalImagePickerEventEmitter.current
    val scope = rememberCoroutineScope()

    val folderPicker = rememberFolderPicker(
        onFailure = onFailure,
        onSuccess = { folderUri ->
            scope.launch {
                val requestId = eventEmitter.onFolderProcessingStarted()
                try {
                    val uris = withContext(Dispatchers.IO) {
                        folderUri.listFilesInDirectoryProgressive()
                            .mapNotNull { uri ->
                                uri.takeIf { it.isAcceptedImage(context) }
                            }
                            .toList()
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
        FolderImagePicker {
            folderPicker.pickFolder(initialFolderUri)
        }
    }
}

private fun Uri.isAcceptedImage(context: Context): Boolean {
    if (EXCLUDED.any { toString().endsWith(".$it", true) }) return false

    val mime = context.contentResolver.getType(this).orEmpty()
    return "audio" !in mime && "video" !in mime
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