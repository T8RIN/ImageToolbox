/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.showReview
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError

fun parseSaveResult(
    saveResult: SaveResult,
    onSuccess: suspend () -> Unit,
    toastHostState: ToastHostState,
    context: Context,
    scope: CoroutineScope
) {
    when (saveResult) {
        is SaveResult.Error.Exception -> {
            scope.launch {
                toastHostState.showError(context, saveResult.throwable)
            }
        }

        is SaveResult.Success -> {
            saveResult.message?.let {
                scope.launch {
                    toastHostState.showToast(
                        message = it,
                        icon = Icons.Rounded.Save,
                        duration = ToastDuration.Long
                    )
                }
            }
            scope.launch { onSuccess() }
            showReview(context)
        }

        SaveResult.Error.MissingPermissions -> Unit //Requesting permissions does FileController
    }
}