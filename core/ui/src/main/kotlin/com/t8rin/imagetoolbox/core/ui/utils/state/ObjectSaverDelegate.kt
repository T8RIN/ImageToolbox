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

package com.t8rin.imagetoolbox.core.ui.utils.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.t8rin.imagetoolbox.core.domain.saving.ObjectSaver
import com.t8rin.imagetoolbox.core.domain.utils.ReadWriteDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <O : Any> ObjectSaver.savable(
    delay: Long = 0,
    scope: CoroutineScope,
    initial: O,
    key: String = initial::class.simpleName.toString()
): ReadWriteDelegate<O> = ObjectSaverDelegate(
    delay = delay,
    saver = this,
    scope = scope,
    initial = initial,
    key = key
)

private class ObjectSaverDelegate<O : Any>(
    delay: Long,
    private val saver: ObjectSaver,
    private val scope: CoroutineScope,
    initial: O,
    private val key: String
) : ReadWriteDelegate<O> {

    private var value: O by mutableStateOf(initial)

    init {
        scope.launch {
            if (delay > 0) delay(delay)
            value = saver.restoreObject(
                key = key,
                kClass = initial::class
            ) ?: initial
        }
    }

    override fun set(value: O) {
        this.value = value
        scope.launch {
            saver.saveObject(
                key = key,
                value = value
            )
        }
    }

    override fun get(): O = value

}