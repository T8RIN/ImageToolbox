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

import android.app.Activity
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.showReview
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError

fun Context.parseSaveResult(
    saveResult: SaveResult,
    onSuccess: suspend () -> Unit,
    toastHostState: ToastHostState,
    scope: CoroutineScope
) {
    when (saveResult) {
        is SaveResult.Error.Exception -> {
            scope.launch {
                toastHostState.showError(
                    context = this@parseSaveResult,
                    error = saveResult.throwable
                )
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
            showReview(this@parseSaveResult)
        }

        SaveResult.Error.MissingPermissions -> Unit //Requesting permissions does FileController
    }
}

fun Context.parseFileSaveResult(
    saveResult: SaveResult,
    onSuccess: suspend () -> Unit,
    toastHostState: ToastHostState,
    scope: CoroutineScope
) {
    if (saveResult is SaveResult.Error.Exception) {
        scope.launch {
            toastHostState.showError(this@parseFileSaveResult, saveResult.throwable)
        }
    } else if (saveResult is SaveResult.Success) {
        scope.launch {
            onSuccess()
        }
        scope.launch {
            toastHostState.showToast(
                getString(
                    R.string.saved_to_without_filename,
                    ""
                ),
                Icons.Rounded.Save
            )
            showReview(this@parseFileSaveResult)
        }
    }
}

fun Activity.parseSaveResults(
    scope: CoroutineScope,
    results: List<SaveResult>,
    toastHostState: ToastHostState,
    isOverwritten: Boolean,
    showConfetti: () -> Unit
) {
    val failed = results.count { it is SaveResult.Error }
    val done = results.count { it is SaveResult.Success }

    if (results.any { it == SaveResult.Error.MissingPermissions }) requestStoragePermission()
    else if (failed == 0) {
        if (done == 1) {
            scope.launch {
                val saveResult = results.first { it is SaveResult.Success } as? SaveResult.Success
                val savingPath = saveResult?.savingPath ?: getString(R.string.default_folder)
                toastHostState.showToast(
                    message = saveResult?.message ?: getString(
                        R.string.saved_to_without_filename,
                        savingPath
                    ),
                    icon = Icons.Rounded.Save,
                    duration = ToastDuration.Long
                )
            }
        } else {
            if (isOverwritten) {
                scope.launch {
                    toastHostState.showToast(
                        message = getString(R.string.images_overwritten),
                        icon = Icons.Rounded.Save,
                        duration = ToastDuration.Long
                    )
                }
            } else {
                val saveResult =
                    results.firstOrNull { it is SaveResult.Success } as? SaveResult.Success
                val savingPath = saveResult?.savingPath ?: getString(R.string.default_folder)
                scope.launch {
                    toastHostState.showToast(
                        message = getString(
                            R.string.saved_to_without_filename,
                            savingPath
                        ),
                        icon = Icons.Rounded.Save,
                        duration = ToastDuration.Long
                    )
                }
            }
            showReview(this)
            showConfetti()
        }

        showReview(this)
        showConfetti()
    } else if (failed < done) {
        scope.launch {
            showConfetti()
            val saveResult = results.firstOrNull { it is SaveResult.Success } as? SaveResult.Success
            toastHostState.showToast(
                message = saveResult?.message
                    ?: getString(
                        R.string.saved_to_without_filename,
                        saveResult?.savingPath
                    ),
                icon = Icons.Rounded.Save,
                duration = ToastDuration.Long
            )
            toastHostState.showToast(
                message = getString(R.string.failed_to_save, failed),
                icon = Icons.Rounded.ErrorOutline,
                duration = ToastDuration.Long
            )
        }
    } else {
        scope.launch {
            toastHostState.showToast(
                message = getString(R.string.failed_to_save, failed),
                icon = Icons.Rounded.ErrorOutline,
                duration = ToastDuration.Long
            )
        }
    }
}