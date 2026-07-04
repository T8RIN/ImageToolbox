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

package com.t8rin.imagetoolbox.feature.root.presentation.components

import android.app.Activity
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.t8rin.imagetoolbox.core.data.saving.FileControllerEvent
import com.t8rin.imagetoolbox.core.data.saving.FileControllerEventEmitter
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.utils.appContext

@Composable
internal fun FileControllerEventsHandler(
    eventEmitter: FileControllerEventEmitter
) {
    var activeRequestId by rememberSaveable { mutableStateOf<Long?>(null) }
    var pendingRequests by rememberSaveable(stateSaver = deleteRequestsSaver) {
        mutableStateOf(emptyList())
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        activeRequestId?.let { requestId ->
            eventEmitter.onDeleteOriginalsPermissionResult(
                requestId = requestId,
                granted = result.resultCode == Activity.RESULT_OK
            )
        }
        activeRequestId = null
    }

    LaunchedEffect(eventEmitter) {
        eventEmitter.events.collect { event ->
            when (event) {
                is FileControllerEvent.RequestDeleteOriginalsPermission -> {
                    pendingRequests = pendingRequests + event
                }

                is FileControllerEvent.OriginalFilesDeleteResult -> {
                    val message = when {
                        event.failed == 0 -> appContext.getString(
                            R.string.original_files_deleted,
                            event.deleted
                        )

                        event.deleted == 0 -> appContext.getString(
                            R.string.original_files_delete_failed,
                            event.failed
                        )

                        else -> appContext.getString(
                            R.string.original_files_delete_result,
                            event.deleted,
                            event.failed
                        )
                    }
                    AppToastHost.showToast(
                        message = message,
                        icon = Icons.Outlined.Delete
                    )
                }
            }
        }
    }

    LaunchedEffect(activeRequestId, pendingRequests, launcher) {
        if (activeRequestId == null) {
            pendingRequests.firstOrNull()?.let { event ->
                pendingRequests = pendingRequests.drop(1)
                activeRequestId = event.requestId
                launcher.launch(
                    IntentSenderRequest.Builder(event.intentSender).build()
                )
            }
        }
    }
}

private val deleteRequestsSaver = listSaver(
    save = { requests ->
        requests.flatMap { request ->
            listOf(request.requestId, request.intentSender, request.count)
        }
    },
    restore = { values ->
        values.chunked(3).map { request ->
            FileControllerEvent.RequestDeleteOriginalsPermission(
                requestId = request[0] as Long,
                intentSender = request[1] as IntentSender,
                count = request[2] as Int
            )
        }
    }
)