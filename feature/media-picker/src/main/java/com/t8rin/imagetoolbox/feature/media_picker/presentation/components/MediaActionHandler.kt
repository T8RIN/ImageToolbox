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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Restore
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaAction
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaActionRequest
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
internal fun MediaActionHandler(component: MediaPickerComponent) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        component.onMediaPermissionResult(it.resultCode == Activity.RESULT_OK)
    }

    LaunchedEffect(component.pendingMediaPermission) {
        component.pendingMediaPermission?.let { sender ->
            try {
                launcher.launch(IntentSenderRequest.Builder(sender).build())
            } catch (exception: Exception) {
                AppToastHost.showFailureToast(exception)
                component.onMediaPermissionResult(false)
            }
        }
    }
    val confirmation = component.mediaActionConfirmation
    var previousConfirmation by remember { mutableStateOf<MediaActionRequest?>(null) }

    SideEffect {
        confirmation?.let { previousConfirmation = it }
    }

    val request = confirmation ?: previousConfirmation
    val action = request?.action ?: MediaAction.Trash
    val count = request?.media?.size ?: 1
    val deleting = action == MediaAction.Delete
    val restoring = action == MediaAction.Restore

    EnhancedAlertDialog(
        visible = confirmation != null,
        onDismissRequest = component::dismissMediaAction,
        placeAboveAll = true,
        icon = {
            Icon(
                imageVector = if (restoring) Icons.Rounded.Restore else Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(
                    when {
                        deleting -> R.string.gallery_delete_permanently
                        restoring -> R.string.gallery_restore
                        else -> R.string.gallery_move_to_trash
                    }
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = stringResource(
                    when {
                        deleting && count > 1 -> R.string.gallery_delete_selected_confirmation
                        restoring && count > 1 -> R.string.gallery_restore_selected_confirmation
                        action == MediaAction.Trash && count > 1 -> R.string.gallery_trash_selected_confirmation
                        deleting -> R.string.gallery_delete_confirmation
                        restoring -> R.string.gallery_restore_confirmation
                        else -> R.string.gallery_trash_confirmation
                    },
                    count
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = if (deleting) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                onClick = component::confirmMediaAction
            ) {
                Text(
                    text = stringResource(
                        when {
                            deleting -> R.string.delete
                            restoring -> R.string.gallery_restore
                            else -> R.string.gallery_move_to_trash
                        }
                    )
                )
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = component::dismissMediaAction
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}