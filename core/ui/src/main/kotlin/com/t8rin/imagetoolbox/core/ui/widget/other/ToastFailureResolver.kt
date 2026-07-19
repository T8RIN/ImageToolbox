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
import android.os.TransactionTooLargeException
import android.system.ErrnoException
import android.system.OsConstants
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.model.WrongKeyException
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BreakingNews
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.resources.icons.Database
import com.t8rin.imagetoolbox.core.resources.icons.DiscFull
import com.t8rin.imagetoolbox.core.resources.icons.File
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.FolderZip
import com.t8rin.imagetoolbox.core.resources.icons.Hourglass
import com.t8rin.imagetoolbox.core.resources.icons.KeyVertical
import com.t8rin.imagetoolbox.core.resources.icons.LinkOff
import com.t8rin.imagetoolbox.core.resources.icons.Memory
import com.t8rin.imagetoolbox.core.resources.icons.PolicyAlert
import com.t8rin.imagetoolbox.core.resources.icons.Security
import com.t8rin.imagetoolbox.core.resources.icons.TimerOff
import com.t8rin.imagetoolbox.core.resources.icons.WifiOff
import com.t8rin.imagetoolbox.core.resources.icons.WifiTetheringError
import com.t8rin.imagetoolbox.core.utils.getString
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import java.io.EOFException
import java.io.FileNotFoundException
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import java.util.zip.ZipException
import javax.crypto.IllegalBlockSizeException
import javax.net.ssl.SSLException

internal fun Throwable.knownFailure(): KnownFailure? = generateSequence(this) { current ->
    current.cause?.takeUnless { it === current }
}.firstNotNullOfOrNull {
    when (it) {
        is IllegalBlockSizeException,
        is WrongKeyException -> KnownFailure(
            message = getString(R.string.invalid_password_or_not_encrypted),
            icon = Icons.Rounded.KeyVertical
        )

        is OutOfMemoryError -> KnownFailure(
            message = getString(R.string.oom_description),
            icon = Icons.Rounded.Memory
        )

        is UnknownHostException -> KnownFailure(
            message = getString(R.string.network_host_unreachable),
            icon = Icons.Rounded.WifiTetheringError
        )

        is SocketTimeoutException,
        is ConnectTimeoutException,
        is HttpRequestTimeoutException,
        is TimeoutException -> KnownFailure(
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

        is ProtocolException -> KnownFailure(
            message = getString(R.string.network_invalid_response),
            icon = Icons.Rounded.LinkOff
        )

        is FileNotFoundException -> KnownFailure(
            message = getString(R.string.file_not_found),
            icon = Icons.Rounded.BreakingNews
        )

        is ZipException -> KnownFailure(
            message = getString(R.string.archive_is_corrupted),
            icon = Icons.Rounded.FolderZip
        )

        is EOFException -> KnownFailure(
            message = getString(R.string.file_is_corrupted_or_unsupported),
            icon = Icons.Rounded.BrokenImageAlt
        )

        is TransactionTooLargeException -> KnownFailure(
            message = getString(R.string.data_is_too_large),
            icon = Icons.Rounded.Database
        )

        is SecurityException -> KnownFailure(
            message = getString(R.string.permission_not_granted),
            icon = Icons.Rounded.PolicyAlert
        )

        is ActivityNotFoundException -> KnownFailure(
            message = getString(R.string.failed_to_open),
            icon = Icons.Rounded.BreakingNews
        )

        is ErrnoException -> it.toKnownFailure()

        is ResponseException -> it.toKnownFailure()

        else -> it.toImageDecodingFailure()
    }
}

private fun Throwable.toImageDecodingFailure(): KnownFailure? {
    val errorMessage = message.orEmpty()

    return when {
        errorMessage.contains(
            other = "Failed to create image decoder",
            ignoreCase = true
        ) || errorMessage.contains(
            other = "Unable to create a decoder",
            ignoreCase = true
        ) -> imageDecodingFailure()

        errorMessage.contains(
            other = "BitmapFactory returned a null bitmap",
            ignoreCase = true
        ) -> imageDecodingFailure()

        else -> null
    }
}

private fun imageDecodingFailure() = KnownFailure(
    message = getString(R.string.file_is_corrupted_or_unsupported),
    icon = Icons.Rounded.BrokenImageAlt
)

private fun ErrnoException.toKnownFailure(): KnownFailure? = when (errno) {
    OsConstants.EACCES,
    OsConstants.EPERM -> KnownFailure(
        message = getString(R.string.permission_not_granted),
        icon = Icons.Rounded.PolicyAlert
    )

    OsConstants.ENOENT -> KnownFailure(
        message = getString(R.string.file_not_found),
        icon = Icons.Rounded.FileOpen
    )

    OsConstants.ENOSPC -> KnownFailure(
        message = getString(R.string.storage_is_full),
        icon = Icons.Rounded.DiscFull
    )

    OsConstants.EROFS -> KnownFailure(
        message = getString(R.string.storage_is_read_only),
        icon = Icons.Rounded.PolicyAlert
    )

    OsConstants.EFBIG -> KnownFailure(
        message = getString(R.string.file_is_too_large),
        icon = Icons.Rounded.Database
    )

    OsConstants.EIO -> KnownFailure(
        message = getString(R.string.file_io_error),
        icon = Icons.Rounded.File
    )

    else -> null
}

private fun ResponseException.toKnownFailure(): KnownFailure? = when (response.status.value) {
    400, 401, 403, 409 -> KnownFailure(
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

    413 -> KnownFailure(
        message = getString(R.string.data_is_too_large),
        icon = Icons.Rounded.Database
    )

    415 -> KnownFailure(
        message = getString(R.string.file_is_corrupted_or_unsupported),
        icon = Icons.Rounded.BrokenImageAlt
    )

    429 -> KnownFailure(
        message = getString(R.string.network_too_many_requests),
        icon = Icons.Rounded.Hourglass
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
