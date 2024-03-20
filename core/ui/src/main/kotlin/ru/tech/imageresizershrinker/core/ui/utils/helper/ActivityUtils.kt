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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.showReview
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState

val AppActivityClass: Class<*> by lazy {
    Class.forName(
        "ru.tech.imageresizershrinker.app.presentation.AppActivity"
    )
}

val MediaPickerActivityClass: Class<*> by lazy {
    Class.forName(
        "ru.tech.imageresizershrinker.feature.media_picker.presentation.MediaPickerActivity"
    )
}

val AppVersionPreRelease: String by lazy {
    BuildConfig.VERSION_NAME
        .replace(BuildConfig.FLAVOR, "")
        .split("-")
        .takeIf { it.size > 1 }
        ?.drop(1)?.first()
        ?.takeWhile { it.isLetter() }
        ?.uppercase()?.takeIf { it.isNotEmpty() }?.let {
            " $it"
        } ?: ""
}

const val ColorSchemeName = "scheme"

fun Activity.failedToSaveImages(
    scope: CoroutineScope,
    results: List<SaveResult>,
    toastHostState: ToastHostState,
    savingPathString: String,
    isOverwritten: Boolean,
    showConfetti: () -> Unit
) {
    val failed = results.count { it is SaveResult.Error }
    val done = results.count { it is SaveResult.Success }

    if (results.any { it == SaveResult.Error.MissingPermissions }) requestStoragePermission()
    else if (failed == 0) {
        if (done == 1) {
            scope.launch {
                toastHostState.showToast(
                    (results.first() as? SaveResult.Success)?.message ?: getString(
                        R.string.saved_to_without_filename,
                        savingPathString
                    ),
                    Icons.Rounded.Save
                )
            }
        } else {
            if (isOverwritten) {
                scope.launch {
                    toastHostState.showToast(
                        getString(R.string.images_overwritten),
                        Icons.Rounded.Save
                    )
                }
            } else {
                scope.launch {
                    toastHostState.showToast(
                        getString(
                            R.string.saved_to_without_filename,
                            savingPathString
                        ),
                        Icons.Rounded.Save
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
            toastHostState.showToast(
                (results.first { it is SaveResult.Success } as SaveResult.Success).message
                    ?: getString(
                        R.string.saved_to_without_filename,
                        savingPathString
                    ),
                Icons.Rounded.Save
            )
            toastHostState.showToast(
                getString(R.string.failed_to_save, failed),
                Icons.Rounded.ErrorOutline,
                ToastDuration.Long
            )
        }
    } else {
        scope.launch {
            toastHostState.showToast(
                getString(R.string.failed_to_save, failed),
                Icons.Rounded.ErrorOutline,
                ToastDuration.Long
            )
        }
    }
}