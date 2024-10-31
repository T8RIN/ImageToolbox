package ru.tech.imageresizershrinker.core.ui.utils.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.updateAndGet
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
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
    context: CoroutineContext = Dispatchers.Main.immediate + SupervisorJob(),
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
