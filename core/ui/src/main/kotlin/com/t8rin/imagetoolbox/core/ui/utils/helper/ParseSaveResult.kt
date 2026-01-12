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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Save
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.firstOfType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.requestStoragePermission
import com.t8rin.imagetoolbox.core.ui.utils.helper.ReviewHandler.Companion.showReview
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastDuration
import com.t8rin.logger.makeLog

internal fun Activity.parseSaveResult(
    saveResult: SaveResult,
    essentials: LocalEssentials
) {
    when (saveResult) {
        is SaveResult.Error.Exception -> {
            saveResult.throwable.makeLog("parseSaveResult")
            essentials.showFailureToast(
                throwable = saveResult.throwable
            )
        }

        is SaveResult.Skipped -> {
            essentials.showToast(
                message = getString(R.string.skipped_saving),
                icon = Icons.Outlined.Info,
                duration = ToastDuration.Short
            )
        }

        is SaveResult.Success -> {
            saveResult.message?.let {
                essentials.showToast(
                    message = it,
                    icon = Icons.Rounded.Save,
                    duration = ToastDuration.Long
                )
            }
            essentials.showConfetti()
            showReview(this@parseSaveResult)
        }

        SaveResult.Error.MissingPermissions -> requestStoragePermission()
    }
}

internal fun Activity.parseFileSaveResult(
    saveResult: SaveResult,
    essentials: LocalEssentials
) {
    when (saveResult) {
        is SaveResult.Error.Exception -> {
            essentials.showFailureToast(
                throwable = saveResult.throwable
            )
        }

        is SaveResult.Skipped -> {
            essentials.showToast(
                message = getString(R.string.skipped_saving),
                icon = Icons.Outlined.Info
            )
        }

        is SaveResult.Success -> {
            essentials.showToast(
                message = getString(R.string.saved_to_without_filename, ""),
                icon = Icons.Rounded.Save
            )
            essentials.showConfetti()
            showReview(this@parseFileSaveResult)
        }

        SaveResult.Error.MissingPermissions -> requestStoragePermission()
    }
}

internal fun Activity.parseSaveResults(
    results: List<SaveResult>,
    essentials: LocalEssentials
) {
    if (results.size == 1) {
        return parseSaveResult(
            saveResult = results.first(),
            essentials = essentials
        )
    }

    if (results.any { it == SaveResult.Error.MissingPermissions }) {
        requestStoragePermission()
        return
    }

    val skipped = results.count { it is SaveResult.Skipped }
    val failed = results.count { it is SaveResult.Error }
    val done = results.count { it is SaveResult.Success }

    if (failed == 0 && done > 0) {
        if (done == 1) {
            val saveResult = results.firstOfType<SaveResult.Success>()
            val savingPath = saveResult?.savingPath ?: getString(R.string.default_folder)
            essentials.showToast(
                message = saveResult?.message ?: getString(
                    R.string.saved_to_without_filename,
                    savingPath
                ),
                icon = Icons.Rounded.Save,
                duration = ToastDuration.Long
            )
        } else {
            val saveResult = results.firstOfType<SaveResult.Success>()

            if (saveResult?.isOverwritten == true) {
                essentials.showToast(
                    message = getString(R.string.images_overwritten),
                    icon = Icons.Rounded.Save,
                    duration = ToastDuration.Long
                )
            } else {
                val savingPath = saveResult?.savingPath ?: getString(R.string.default_folder)

                essentials.showToast(
                    message = getString(
                        R.string.saved_to_without_filename,
                        savingPath
                    ),
                    icon = Icons.Rounded.Save,
                    duration = ToastDuration.Long
                )
            }
        }

        if (skipped > 0) {
            essentials.showToast(
                message = getString(R.string.skipped_saving_multiple, skipped),
                icon = Icons.Outlined.Info,
                duration = ToastDuration.Short
            )
        }

        essentials.showConfetti()
        showReview(this)
        return
    }

    if (failed > 0) {
        val saveResult = results.firstOfType<SaveResult.Success>()
        val errorSaveResult = results.firstOfType<SaveResult.Error>()

        if (done > 0) {
            essentials.showToast(
                message = saveResult?.message
                    ?: getString(
                        R.string.saved_to_without_filename,
                        saveResult?.savingPath
                    ),
                icon = Icons.Rounded.Save,
                duration = ToastDuration.Long
            )
        }
        essentials.showFailureToast(getString(R.string.failed_to_save, failed))
        essentials.showToast(
            message = getString(
                R.string.smth_went_wrong,
                errorSaveResult?.throwable?.localizedMessage ?: ""
            )
        )

        if (skipped > 0) {
            essentials.showToast(
                message = getString(R.string.skipped_saving_multiple, skipped),
                icon = Icons.Outlined.Info,
                duration = ToastDuration.Short
            )
        }
        return
    }

    if (skipped > 0 && done == 0 && failed == 0) {
        essentials.showToast(
            message = getString(R.string.skipped_saving_multiple, skipped),
            icon = Icons.Outlined.Info,
            duration = ToastDuration.Short
        )
    }
}