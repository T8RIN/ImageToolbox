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

package ru.tech.imageresizershrinker.core.ui.utils.provider

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.confetti.ConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showFailureToast

@Composable
fun rememberLocalEssentials(): LocalEssentials {
    val toastHostState = LocalToastHostState.current
    val confettiHostState = LocalConfettiHostState.current
    val context = LocalComponentActivity.current
    val coroutineScope = rememberCoroutineScope()

    return remember(
        toastHostState,
        coroutineScope,
        confettiHostState,
        context
    ) {
        LocalEssentials(
            toastHostState = toastHostState,
            confettiHostState = confettiHostState,
            coroutineScope = coroutineScope,
            context = context
        )
    }
}

@Stable
@Immutable
data class LocalEssentials internal constructor(
    val toastHostState: ToastHostState,
    val confettiHostState: ConfettiHostState,
    val coroutineScope: CoroutineScope,
    val context: ComponentActivity,
) {
    fun showToast(
        message: String,
        icon: ImageVector? = null,
        duration: ToastDuration = ToastDuration.Short
    ) = coroutineScope.launch {
        toastHostState.showToast(
            message = message,
            icon = icon,
            duration = duration
        )
    }

    fun showFailureToast(
        throwable: Throwable
    ) {
        coroutineScope.launch {
            toastHostState.showFailureToast(
                context = context,
                throwable = throwable
            )
        }
    }

    fun showConfetti(
        duration: ToastDuration
    ) = coroutineScope.launch {
        confettiHostState.showConfetti(duration)
    }

    fun showConfetti() {
        showConfetti(ToastDuration(4500L))
    }

    fun showActivateFilesToast() {
        showToast(
            message = context.getString(R.string.activate_files),
            icon = Icons.Outlined.FolderOff,
            duration = ToastDuration.Long
        )
    }

    fun parseSaveResult(saveResult: SaveResult) {
        context.parseSaveResult(
            saveResult = saveResult,
            essentials = this
        )
    }

    fun parseSaveResults(saveResults: List<SaveResult>) {
        context.parseSaveResults(
            results = saveResults,
            essentials = this
        )
    }

    fun parseFileSaveResult(saveResult: SaveResult) {
        context.parseFileSaveResult(
            saveResult = saveResult,
            essentials = this
        )
    }
}