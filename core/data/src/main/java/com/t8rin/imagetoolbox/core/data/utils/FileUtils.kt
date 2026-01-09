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

package com.t8rin.imagetoolbox.core.data.utils

import android.os.Build
import android.os.FileObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

@Suppress("DEPRECATION")
fun File.observeHasChanges(
    flags: Int = FileObserver.ALL_EVENTS
): Flow<Unit> = callbackFlow {
    val observer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        object : FileObserver(this@observeHasChanges, flags) {
            override fun onEvent(event: Int, path: String?) {
                trySend(Unit)
            }
        }
    } else {
        object : FileObserver(absolutePath, flags) {
            override fun onEvent(event: Int, path: String?) {
                trySend(Unit)
            }
        }
    }
    send(Unit)
    observer.startWatching()
    awaitClose { observer.stopWatching() }
}