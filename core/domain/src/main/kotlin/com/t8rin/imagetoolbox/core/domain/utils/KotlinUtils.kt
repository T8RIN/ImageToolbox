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
@file:Suppress("NOTHING_TO_INLINE")

package com.t8rin.imagetoolbox.core.domain.utils

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.transform
import java.io.Closeable
import kotlin.reflect.KClass


inline fun <reified T> T?.notNullAnd(
    predicate: (T) -> Boolean
): Boolean = if (this != null) predicate(this)
else false

fun CharSequence.isBase64() = isNotEmpty() && BASE64_PATTERN.matches(this)

fun CharSequence.trimToBase64() = toString().filter { !it.isWhitespace() }.substringAfter(",")

private val BASE64_PATTERN = Regex(
    "^(?=(.{4})*$)[A-Za-z0-9+/]*={0,2}$"
)

inline fun <reified R> Any.cast(): R = this as R

inline fun <reified T, reified R> T.autoCast(block: T.() -> Any): R = block() as R

inline fun <reified R> Any.safeCast(): R? = this as? R

inline fun <reified R> Any?.ifCasts(action: (R) -> Unit) = (this as? R)?.let(action)


inline operator fun CharSequence.times(
    count: Int
): CharSequence = repeat(count.coerceAtLeast(0))


suspend inline fun <T, R> T.runSuspendCatching(block: T.() -> R): Result<R> {
    currentCoroutineContext().ensureActive()

    return runCatching(block).onFailure {
        println("ERROR: $it")
        currentCoroutineContext().ensureActive()
    }
}

inline fun <T : Any> KClass<T>.simpleName() = simpleName!!

inline fun <T> Boolean.then(value: T): T? = if (this) value else null

inline fun tryAll(
    vararg actions: () -> Unit
): Boolean {
    for (action in actions) {
        runCatching { action() }.onSuccess { return true }
    }

    return false
}

inline fun <T> Result<T>.onResult(
    action: (isSuccess: Boolean) -> Unit
): Result<T> = apply { action(isSuccess) }

fun <T> Flow<T>.throttleLatest(delayMillis: Long): Flow<T> = this
    .conflate()
    .transform {
        emit(it)
        delay(delayMillis)
    }

inline fun <T : Closeable?, R> T.applyUse(block: T.() -> R): R = use(block)