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

package com.t8rin.imagetoolbox.core.domain.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

private data class CacheItem<V>(
    val value: V,
    val created: Instant,
)

class Cache<K, V>(private val maxAge: Duration) {
    private val map: ConcurrentMap<K, CacheItem<V>> = ConcurrentHashMap()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun call(key: K, dataGetter: suspend () -> V): V {
        val now = Clock.System.now()
        val prevItem = map[key]?.takeIf {
            it.created + maxAge > now
        }

        return if (prevItem != null) {
            prevItem.value
        } else {
            val data = dataGetter()
            val item = CacheItem(
                value = data,
                created = now
            )
            map[key] = item
            scope.launch {
                delay(maxAge)
                map.remove(key, item)
            }

            data
        }
    }

    fun reset() {
        map.clear()
    }

    fun reset(key: K) {
        map.remove(key)
    }
}
