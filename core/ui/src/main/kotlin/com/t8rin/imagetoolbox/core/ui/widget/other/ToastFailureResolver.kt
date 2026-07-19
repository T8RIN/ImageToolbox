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

package com.t8rin.imagetoolbox.core.ui.widget.other

import android.content.ActivityNotFoundException
import android.system.ErrnoException
import android.system.OsConstants
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Database
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.HourglassEmpty
import com.t8rin.imagetoolbox.core.resources.icons.LinkOff
import com.t8rin.imagetoolbox.core.resources.icons.Memory
import com.t8rin.imagetoolbox.core.resources.icons.Security
import com.t8rin.imagetoolbox.core.resources.icons.TimerOff
import com.t8rin.imagetoolbox.core.resources.icons.WifiOff
import com.t8rin.imagetoolbox.core.resources.icons.WifiTetheringError
import com.t8rin.imagetoolbox.core.utils.getString
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import java.io.FileNotFoundException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

internal fun Throwable.knownFailure(): KnownFailure? = generateSequence(this) { current ->
    current.cause?.takeUnless { it === current }
}.firstNotNullOfOrNull {
    when (it) {
        is OutOfMemoryError -> KnownFailure(
            message = getString(R.string.oom_description),
            icon = Icons.Outlined.Memory
        )

        is UnknownHostException -> KnownFailure(
            message = getString(R.string.network_host_unreachable),
            icon = Icons.Rounded.WifiTetheringError
        )

        is SocketTimeoutException,
        is ConnectTimeoutException,
        is HttpRequestTimeoutException -> KnownFailure(
            message = getString(R.string.network_timeout),
            icon = Icons.Rounded.TimerOff
        )

        is ConnectException -> KnownFailure(
            message = getString(R.string.network_connection_failed),
            icon = Icons.Rounded.WifiOff
        )

        is SocketException -> KnownFailure(
            message = getString(R.string.network_connection_interrupted),
            icon = Icons.Rounded.LinkOff
        )

        is SSLException -> KnownFailure(
            message = getString(R.string.network_secure_connection_failed),
            icon = Icons.Rounded.Security
        )

        is FileNotFoundException -> KnownFailure(
            message = getString(R.string.file_not_found),
            icon = Icons.Rounded.FileOpen
        )

        is SecurityException -> KnownFailure(
            message = getString(R.string.permission_not_granted),
            icon = Icons.Rounded.Security
        )

        is ActivityNotFoundException -> KnownFailure(
            message = getString(R.string.failed_to_open),
            icon = Icons.Rounded.FileOpen
        )

        is ErrnoException -> it.toKnownFailure()

        is ResponseException -> it.toKnownFailure()

        else -> null
    }
}

private fun ErrnoException.toKnownFailure(): KnownFailure? = when (errno) {
    OsConstants.EACCES,
    OsConstants.EPERM -> KnownFailure(
        message = getString(R.string.permission_not_granted),
        icon = Icons.Rounded.Security
    )

    OsConstants.ENOENT -> KnownFailure(
        message = getString(R.string.file_not_found),
        icon = Icons.Rounded.FileOpen
    )

    OsConstants.ENOSPC -> KnownFailure(
        message = getString(R.string.storage_is_full),
        icon = Icons.Rounded.Database
    )

    else -> null
}

private fun ResponseException.toKnownFailure(): KnownFailure? = when (response.status.value) {
    401, 403 -> KnownFailure(
        message = getString(R.string.network_request_rejected),
        icon = Icons.Rounded.Security
    )

    404 -> KnownFailure(
        message = getString(R.string.network_resource_not_found),
        icon = Icons.Rounded.LinkOff
    )

    408 -> KnownFailure(
        message = getString(R.string.network_timeout),
        icon = Icons.Rounded.TimerOff
    )

    429 -> KnownFailure(
        message = getString(R.string.network_too_many_requests),
        icon = Icons.Rounded.HourglassEmpty
    )

    in 500..599 -> KnownFailure(
        message = getString(R.string.network_server_error),
        icon = Icons.Rounded.WifiOff
    )

    else -> null
}

internal inline fun <reified T : Throwable> Throwable.hasCause(): Boolean =
    generateSequence(this) { current ->
        current.cause?.takeUnless { it === current }
    }.any { it is T }

internal data class KnownFailure(
    val message: String,
    val icon: ImageVector
)
