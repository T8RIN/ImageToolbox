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

package com.t8rin.imagetoolbox.core.domain.saving

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

interface KeepAliveService {
    fun updateOrStart(
        title: String = "",
        description: String = "",
        progress: Float = PROGRESS_INDETERMINATE
    )

    fun stop(removeNotification: Boolean = true)

    companion object {
        const val PROGRESS_NO_PROGRESS = -2f
        const val PROGRESS_INDETERMINATE = -1f
    }
}

fun KeepAliveService.updateProgress(
    title: String = "",
    done: Int,
    total: Int
) {
    updateOrStart(
        title = title,
        description = "$done / $total",
        progress = (done / total.toFloat()).takeIf { it > 0f }
            ?: KeepAliveService.PROGRESS_INDETERMINATE
    )
}

suspend fun <R> KeepAliveService.track(
    initial: KeepAliveService.() -> Unit = { updateOrStart() },
    onCancel: () -> Unit = {},
    onFailure: (Throwable) -> Unit = {},
    onComplete: KeepAliveService.(isSuccess: Boolean) -> Unit = { stop(true) },
    action: suspend KeepAliveService.() -> R
): R? = coroutineScope {
    val deferred = async {
        initial()
        action()
    }

    deferred.invokeOnCompletion { onComplete(true) }

    try {
        deferred.await()
    } catch (e: CancellationException) {
        onComplete(false)
        onCancel()
        throw e
    } catch (e: Throwable) {
        onComplete(false)
        onFailure(e)
        null
    } finally {
        onComplete(true)
    }
}

suspend fun <R> KeepAliveService.trackSafe(
    initial: KeepAliveService.() -> Unit = { updateOrStart() },
    onCancel: () -> Unit = {},
    onComplete: KeepAliveService.(isSuccess: Boolean) -> Unit = { stop(true) },
    action: suspend KeepAliveService.() -> R
): R = coroutineScope {
    val deferred = async {
        initial()
        action()
    }

    deferred.invokeOnCompletion { onComplete(true) }

    try {
        deferred.await()
    } catch (e: CancellationException) {
        onComplete(false)
        onCancel()
        throw e
    } finally {
        onComplete(true)
    }
}