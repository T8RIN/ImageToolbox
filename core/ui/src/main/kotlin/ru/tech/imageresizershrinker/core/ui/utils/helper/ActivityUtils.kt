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
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.showReview
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState

fun Activity.failedToSaveImages(
    scope: CoroutineScope,
    failed: Int,
    done: Int,
    toastHostState: ToastHostState,
    savingPathString: String,
    showConfetti: () -> Unit
) {
    if (failed == -1) requestStoragePermission()
    else if (failed == 0) {
        scope.launch {
            toastHostState.showToast(
                getString(
                    R.string.saved_to_without_filename,
                    savingPathString
                ),
                Icons.Rounded.Save
            )
        }
        showReview(this)
        showConfetti()
    } else if (failed < done) {
        scope.launch {
            showConfetti()
            toastHostState.showToast(
                getString(
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