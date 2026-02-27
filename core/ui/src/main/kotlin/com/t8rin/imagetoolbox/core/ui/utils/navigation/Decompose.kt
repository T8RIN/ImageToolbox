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

package com.t8rin.imagetoolbox.core.ui.utils.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.updateAndGet
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.t8rin.logger.makeLog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty

val ComponentContext.coroutineScope: CoroutineScope
    get() = coroutineScope()

/**
 * Creates and returns a new [CoroutineScope] with the specified [context].
 * The returned [CoroutineScope] is automatically cancelled when the [Lifecycle] is destroyed.
 *
 * @param context a [CoroutineContext] to be used for creating the [CoroutineScope], default
 * is [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate]
 * if available on the current platform, or [Dispatchers.Main] otherwise.
 */
fun LifecycleOwner.coroutineScope(
    context: CoroutineContext = Dispatchers.Main.immediate + SupervisorJob() + CoroutineExceptionHandler { _, t ->
        t.makeLog(
            "Component CRITICAL ISSUE"
        )
    },
): CoroutineScope =
    CoroutineScope(context = context).withLifecycle(lifecycle)

/**
 * Automatically cancels this [CoroutineScope] when the specified [lifecycle] is destroyed.
 *
 * @return the same (this) [CoroutineScope].
 */
fun CoroutineScope.withLifecycle(lifecycle: Lifecycle): CoroutineScope {
    lifecycle.doOnDestroy(::cancel)

    return this
}

@JvmInline
value class Nullable<T>(
    val value: T?
) {
    operator fun component1(): T? = value
    operator fun getValue(
        t: T?,
        property: KProperty<*>
    ): T? = value

    fun copy(value: T?) = Nullable(value)
}

fun <T> T?.wrap(): Nullable<T> = Nullable(this)

fun <T> MutableValue<Nullable<T>>.updateNullable(function: (T?) -> T?) {
    updateAndGet {
        function(this.value.value).wrap()
    }
}

typealias NullableValue<T> = Value<Nullable<T>>

typealias MutableNullableValue<T> = MutableValue<Nullable<T>>
