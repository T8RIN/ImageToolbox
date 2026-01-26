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

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalEssentials
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials

@Composable
fun rememberSafeUriHandler(): UriHandler {
    val essentials = rememberLocalEssentials()

    return remember(essentials) {
        SafeUriHandler(
            essentials = essentials
        )
    }
}

@Stable
@Immutable
private class SafeUriHandler(
    private val essentials: LocalEssentials
) : UriHandler {

    override fun openUri(uri: String) {
        tryActions(
            first = { rawOpenUri(uri) },
            second = {
                val trimmed = uri.trim()

                val modifiedUrl = when {
                    trimmed.startsWith(WWW, ignoreCase = true) -> trimmed.replace(WWW, HTTPS)
                    !trimmed.startsWith(HTTP) && !trimmed.startsWith(HTTPS) -> HTTPS + trimmed
                    else -> trimmed
                }

                rawOpenUri(modifiedUrl)
            },
            onFailure = {
                essentials.showFailureToast(
                    essentials.getString(
                        R.string.cannot_open_uri, uri
                    )
                )
            }
        )
    }

    private fun rawOpenUri(uri: String) {
        essentials.startActivity(
            Intent(Intent.ACTION_VIEW, uri.toUri()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun tryActions(
        first: () -> Unit,
        second: () -> Unit,
        onFailure: () -> Unit
    ) {
        runCatching(first).onSuccess { return }
        runCatching(second).onSuccess { return }
        onFailure()
    }

    fun asUnsafe(): UriHandler = object : UriHandler {
        override fun openUri(uri: String) = rawOpenUri(uri)
    }

}

fun UriHandler.asUnsafe(): UriHandler = if (this is SafeUriHandler) asUnsafe() else this

private const val WWW = "www."
private const val HTTPS = "https://"
private const val HTTP = "http://"