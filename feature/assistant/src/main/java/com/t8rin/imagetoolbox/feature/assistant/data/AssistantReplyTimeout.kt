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

package com.t8rin.imagetoolbox.feature.assistant.data

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeout

internal suspend fun <T> withAssistantReplyTimeout(
    firstTextTimeoutMillis: Long,
    idleTimeoutMillis: Long = 60_000L,
    action: suspend (onText: () -> Unit) -> T
): T = coroutineScope {
    val updates = Channel<Unit>(Channel.CONFLATED)
    val response = async { action { updates.trySend(Unit) } }
    try {
        var timeout = firstTextTimeoutMillis
        var completed = false
        while (!completed) {
            completed = withTimeout(timeout) {
                select {
                    response.onAwait { true }
                    updates.onReceive { false }
                }
            }
            timeout = idleTimeoutMillis
        }
        response.await()
    } finally {
        response.cancel()
        updates.close()
    }
}
