/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.utils.content_pickers

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.t8rin.logger.makeLog
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials


private data class FolderOpenerImpl(
    val openDocumentTree: ManagedActivityResultLauncher<Uri?, Uri?>,
    val onFailure: (Throwable) -> Unit
) : FolderOpener {

    override fun open(initialLocation: Uri?) {
        "Folder Open Start".makeLog()
        runCatching {
            openDocumentTree.launch(initialLocation)
        }.onFailure {
            it.makeLog("Folder Open Failure")
            onFailure(it)
        }.onSuccess {
            "Folder Open Success".makeLog()
        }
    }

}


@Stable
@Immutable
interface FolderOpener {
    fun open(initialLocation: Uri? = null)
}

@Composable
fun rememberFolderOpener(
    onFailure: () -> Unit = {},
    onSuccess: (Uri) -> Unit,
): FolderOpener {
    val openDocumentTree = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            uri?.takeIf {
                it != Uri.EMPTY
            }?.let {
                onSuccess(it)
            } ?: onFailure()
        }
    )
    val essentials = rememberLocalEssentials()

    return remember(openDocumentTree) {
        derivedStateOf {
            FolderOpenerImpl(
                openDocumentTree = openDocumentTree,
                onFailure = {
                    onFailure()
                    essentials.handleFileSystemFailure(it)
                }
            )
        }
    }.value
}