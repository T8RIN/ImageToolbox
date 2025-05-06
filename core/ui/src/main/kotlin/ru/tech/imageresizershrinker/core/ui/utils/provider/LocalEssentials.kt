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

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.confetti.ConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.createScreenShortcut
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
import ru.tech.imageresizershrinker.core.ui.utils.helper.toClipData
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
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

    fun copyToClipboard(clipEntry: ClipEntry?) {
        coroutineScope.launch {
            clipboard.setClipEntry(clipEntry)
        }
    }

    fun copyToClipboard(uri: Uri) {
        coroutineScope.launch {
            clipboard.setClipEntry(uri.asClip(context))
            showConfetti()
        }
    }

    fun copyToClipboard(text: CharSequence) {
        copyToClipboard(ClipEntry(text.toClipData()))
        showToast(
            message = context.getString(R.string.copied),
            icon = Icons.Rounded.CopyAll
        )
    }

    fun getTextFromClipboard(
        onSuccess: (CharSequence) -> Unit
    ) {
        coroutineScope.launch {
            clipboard.getClipEntry()
                ?.clipData?.let { primaryClip ->
                    if (primaryClip.itemCount > 0) {
                        primaryClip.getItemAt(0)?.text
                    } else {
                        null
                    }
                }?.takeIf { it.isNotEmpty() }?.let(onSuccess)
        }
    }

    fun clearClipboard() {
        val clipboardManager = clipboard.nativeClipboard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            runCatching {
                clipboardManager.clearPrimaryClip()
            }.onFailure {
                clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(null, "")
                )
            }
        } else {
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(null, "")
            )
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