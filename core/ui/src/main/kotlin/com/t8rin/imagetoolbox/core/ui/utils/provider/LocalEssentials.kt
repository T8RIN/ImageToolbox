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

package com.t8rin.imagetoolbox.core.ui.utils.provider

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.confetti.ConfettiHostState
import com.t8rin.imagetoolbox.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.createScreenShortcut
import com.t8rin.imagetoolbox.core.ui.utils.helper.asClip
import com.t8rin.imagetoolbox.core.ui.utils.helper.parseFileSaveResult
import com.t8rin.imagetoolbox.core.ui.utils.helper.parseSaveResult
import com.t8rin.imagetoolbox.core.ui.utils.helper.parseSaveResults
import com.t8rin.imagetoolbox.core.ui.utils.helper.toClipData
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastDuration
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastHostState
import com.t8rin.imagetoolbox.core.ui.widget.other.showFailureToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberLocalEssentials(): LocalEssentials {
    val toastHostState = LocalToastHostState.current
    val confettiHostState = LocalConfettiHostState.current
    val context = LocalComponentActivity.current
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    return remember(
        toastHostState,
        coroutineScope,
        confettiHostState,
        context,
        clipboard
    ) {
        LocalEssentials(
            toastHostState = toastHostState,
            confettiHostState = confettiHostState,
            coroutineScope = coroutineScope,
            context = context,
            clipboard = clipboard
        )
    }
}

@ConsistentCopyVisibility
@Stable
@Immutable
data class LocalEssentials internal constructor(
    val toastHostState: ToastHostState,
    val confettiHostState: ConfettiHostState,
    val coroutineScope: CoroutineScope,
    val context: ComponentActivity,
    val clipboard: Clipboard
) : CoroutineScope by coroutineScope {
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

    fun showToast(
        messageSelector: Context.() -> String,
        icon: ImageVector? = null,
        duration: ToastDuration = ToastDuration.Short
    ) = showToast(
        message = messageSelector(context),
        icon = icon,
        duration = duration
    )

    fun showFailureToast(throwable: Throwable) {
        coroutineScope.launch {
            toastHostState.showFailureToast(
                context = context,
                throwable = throwable
            )
        }
    }

    fun showFailureToast(message: String) {
        coroutineScope.launch {
            toastHostState.showFailureToast(
                message = message
            )
        }
    }

    fun showFailureToast(res: Int) {
        coroutineScope.launch {
            toastHostState.showFailureToast(
                message = context.getString(res)
            )
        }
    }

    fun dismissToasts() {
        toastHostState.currentToastData?.dismiss()
    }

    fun showConfetti(
        duration: ToastDuration
    ) = coroutineScope.launch {
        confettiHostState.showConfetti(duration)
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

    fun createScreenShortcut(
        screen: Screen,
        tint: Color = Color.Unspecified
    ) {
        coroutineScope.launch {
            context.createScreenShortcut(
                screen = screen,
                tint = tint,
                onFailure = ::showFailureToast
            )
        }
    }

    fun copyToClipboard(
        clipEntry: ClipEntry?,
        onSuccess: () -> Unit = {}
    ) {
        coroutineScope.launch {
            runSuspendCatching {
                clipboard.setClipEntry(clipEntry)
            }.onSuccess {
                onSuccess()
            }.onFailure {
                showFailureToast(context.getString(R.string.data_is_too_large_to_copy))
            }
        }
    }

    fun copyToClipboard(
        uri: Uri,
        @StringRes message: Int = R.string.copied,
        icon: ImageVector = Icons.Rounded.CopyAll
    ) {
        copyToClipboard(
            clipEntry = uri.asClip(context),
            onSuccess = {
                showConfetti()
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    showToast(
                        message = context.getString(message),
                        icon = icon
                    )
                }
            }
        )
    }

    fun copyToClipboard(
        text: CharSequence,
        @StringRes message: Int = R.string.copied,
        icon: ImageVector = Icons.Rounded.CopyAll
    ) {
        copyToClipboard(
            clipEntry = ClipEntry(text.toClipData()),
            onSuccess = {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    showToast(
                        message = context.getString(message),
                        icon = icon
                    )
                }
            }
        )
    }

    fun getTextFromClipboard(
        onSuccess: (CharSequence) -> Unit
    ) {
        coroutineScope.launch {
            runSuspendCatching {
                clipboard.getClipEntry()
                    ?.clipData?.let { primaryClip ->
                        if (primaryClip.itemCount > 0) {
                            primaryClip.getItemAt(0)?.text
                        } else {
                            null
                        }
                    }?.takeIf { it.isNotEmpty() }?.let(onSuccess)
            }.onFailure {
                showFailureToast(context.getString(R.string.clipboard_data_is_too_large))
            }
        }
    }

    fun clearClipboard() {
        coroutineScope.launch {
            clipboard.setClipEntry(null)
        }
    }

    private fun showActivateFilesToast() {
        showToast(
            message = context.getString(R.string.activate_files),
            icon = Icons.Outlined.FolderOff,
            duration = ToastDuration.Long
        )
    }
}