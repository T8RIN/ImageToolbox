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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.getString

object Clipboard {

    private val clipboard by lazy { AndroidClipboardManager() }

    fun copy(
        clipEntry: ClipEntry?,
        onSuccess: () -> Unit = {}
    ) {
        runCatching {
            clipboard.setClip(clipEntry)
        }.onSuccess {
            onSuccess()
        }.onFailure {
            AppToastHost.showFailureToast(getString(R.string.data_is_too_large_to_copy))
        }
    }

    fun copy(
        uri: Uri,
        @StringRes message: Int = R.string.copied,
        icon: ImageVector = Icons.Rounded.CopyAll
    ) {
        copy(
            clipEntry = uri.asClip(appContext),
            onSuccess = {
                AppToastHost.showConfetti()
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    AppToastHost.showToast(
                        message = getString(message),
                        icon = icon
                    )
                }
            }
        )
    }

    fun copy(
        text: CharSequence,
        @StringRes message: Int = R.string.copied,
        icon: ImageVector = Icons.Rounded.CopyAll
    ) {
        copy(
            clipEntry = ClipEntry(text.toClipData()),
            onSuccess = {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    AppToastHost.showToast(
                        message = getString(message),
                        icon = icon
                    )
                }
            }
        )
    }

    fun getText(
        onSuccess: (String) -> Unit
    ) {
        runCatching {
            clipboard.getClip()
                ?.clipData?.let { primaryClip ->
                    if (primaryClip.itemCount > 0) {
                        primaryClip.getItemAt(0)?.text
                    } else {
                        null
                    }
                }?.takeIf { it.isNotEmpty() }?.let {
                    onSuccess(it.toString())
                }
        }.onFailure {
            AppToastHost.showFailureToast(getString(R.string.clipboard_data_is_too_large))
        }
    }

    fun clear() {
        clipboard.setClip(null)
    }

}

private class AndroidClipboardManager(
    private val clipboardManager: ClipboardManager = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
) {
    fun getClip(): ClipEntry? = clipboardManager.primaryClip?.let(::ClipEntry)

    fun setClip(clipEntry: ClipEntry?) {
        if (clipEntry == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                clipboardManager.clearPrimaryClip()
            } else {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""))
            }
        } else {
            clipboardManager.setPrimaryClip(clipEntry.clipData)
        }
    }

}