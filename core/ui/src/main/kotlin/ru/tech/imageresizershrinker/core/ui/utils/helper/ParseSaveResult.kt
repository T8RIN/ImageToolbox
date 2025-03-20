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
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.utils.ListUtils.firstOfType
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.Companion.showReview
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration

internal fun Activity.parseSaveResult(
    saveResult: SaveResult,
    essentials: LocalEssentials
) {
    when (saveResult) {
        is SaveResult.Error.Exception -> {
            essentials.showFailureToast(
                throwable = saveResult.throwable
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

internal fun Context.parseFileSaveResult(
    saveResult: SaveResult,
    essentials: LocalEssentials
) {
    if (saveResult is SaveResult.Error.Exception) {
        essentials.showFailureToast(
            throwable = saveResult.throwable
        )
    } else if (saveResult is SaveResult.Success) {
        essentials.showToast(
            message = getString(
                R.string.saved_to_without_filename,
                ""
            ),
            icon = Icons.Rounded.Save
        )
        showReview(this@parseFileSaveResult)
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

    val failed = results.count { it is SaveResult.Error }
    val done = results.count { it is SaveResult.Success }

    if (results.any { it == SaveResult.Error.MissingPermissions }) requestStoragePermission()
    else if (failed == 0) {
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

        showReview(this)
        essentials.showConfetti()

    } else if (failed < done) {
        essentials.showConfetti()

        val saveResult = results.firstOfType<SaveResult.Success>()
        val errorSaveResult = results.firstOfType<SaveResult.Error>()

        essentials.showToast(
            message = saveResult?.message
                ?: getString(
                    R.string.saved_to_without_filename,
                    saveResult?.savingPath
                ),
            icon = Icons.Rounded.Save,
            duration = ToastDuration.Long
        )
        essentials.showToast(
            message = getString(R.string.failed_to_save, failed),
            icon = Icons.Rounded.ErrorOutline,
            duration = ToastDuration.Long
        )
        essentials.showToast(
            message = getString(
                R.string.smth_went_wrong,
                errorSaveResult?.throwable?.localizedMessage ?: ""
            )
        )
    } else {
        val errorSaveResult = results.firstOfType<SaveResult.Error>()

        essentials.showToast(
            message = getString(R.string.failed_to_save, failed),
            icon = Icons.Rounded.ErrorOutline,
            duration = ToastDuration.Long
        )
        essentials.showToast(
            message = getString(
                R.string.smth_went_wrong,
                errorSaveResult?.throwable?.localizedMessage ?: ""
            )
        )
    }
}