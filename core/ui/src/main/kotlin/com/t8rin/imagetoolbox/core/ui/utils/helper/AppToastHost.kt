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

import android.content.ActivityNotFoundException
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.confetti.ConfettiHostState
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastDuration
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastHostState
import com.t8rin.imagetoolbox.core.ui.widget.other.showFailureToast
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

data object AppToastHost {

    private var context: CoroutineContext = Dispatchers.Unconfined
    private val scope by lazy { CoroutineScope(context) }

    val state = ToastHostState()

    val confettiState = ConfettiHostState()

    fun init(context: CoroutineContext) {
        this.context = context
    }

    fun showToast(
        message: String,
        icon: ImageVector? = null,
        duration: ToastDuration = ToastDuration.Short
    ) {
        scope.launch {
            state.showToast(
                message = message,
                icon = icon,
                duration = duration
            )
        }
    }

    fun showToast(
        message: Int,
        icon: ImageVector? = null,
        duration: ToastDuration = ToastDuration.Short
    ) {
        scope.launch {
            state.showToast(
                message = getString(message),
                icon = icon,
                duration = duration
            )
        }
    }

    fun showFailureToast(throwable: Throwable) {
        scope.launch {
            state.showFailureToast(
                context = appContext,
                throwable = throwable
            )
        }
    }

    fun showFailureToast(message: String) {
        scope.launch {
            state.showFailureToast(
                message = message
            )
        }
    }

    fun showFailureToast(res: Int) {
        scope.launch {
            state.showFailureToast(
                message = appContext.getString(res)
            )
        }
    }

    fun dismissToasts() {
        state.currentToastData?.dismiss()
        confettiState.currentToastData?.dismiss()
    }

    fun showConfetti(
        duration: ToastDuration
    ) {
        scope.launch {
            confettiState.showConfetti(duration)
        }
    }

    fun showConfetti() {
        showConfetti(ToastDuration(4500L))
    }

    fun handleFileSystemFailure(throwable: Throwable) {
        when (throwable) {
            is ActivityNotFoundException -> showActivateFilesToast()
            else -> showFailureToast(throwable)
        }
    }

    const val PERMISSION = "REQUEST_PERMISSION"

    private fun showActivateFilesToast() {
        showToast(
            message = appContext.getString(R.string.activate_files),
            icon = Icons.Outlined.FolderOff,
            duration = ToastDuration.Long
        )
    }

}