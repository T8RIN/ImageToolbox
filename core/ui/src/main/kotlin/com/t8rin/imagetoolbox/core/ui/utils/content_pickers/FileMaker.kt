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

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.logger.makeLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private data class FileMakerImpl(
    val createDocument: ManagedActivityResultLauncher<String, Uri?>,
    val onFailure: (Throwable) -> Unit
) : FileMaker {

    override fun make(name: String) {
        "File Make Start".makeLog()
        runCatching {
            createDocument.launch(name)
        }.onFailure {
            it.makeLog("File Make Failure")
            onFailure(it)
        }.onSuccess {
            "File Make Success".makeLog()
        }
    }

}


@Stable
@Immutable
interface FileMaker {
    fun make(name: String)
}

@Composable
fun rememberFileCreator(
    mimeType: MimeType.Single = MimeType.All,
    onFailure: () -> Unit = {},
    onSuccess: (Uri) -> Unit,
): FileMaker {
    val essentials = rememberLocalEssentials()
    val createDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(mimeType.entry),
        onResult = { uri ->
            essentials.launch {
                delay(300)
                uri?.takeIf {
                    it != Uri.EMPTY
                }?.let {
                    onSuccess(it)
                } ?: onFailure()
            }
        }
    )

    return remember(createDocument) {
        derivedStateOf {
            FileMakerImpl(
                createDocument = createDocument,
                onFailure = {
                    onFailure()
                    essentials.handleFileSystemFailure(it)
                }
            )
        }
    }.value
}