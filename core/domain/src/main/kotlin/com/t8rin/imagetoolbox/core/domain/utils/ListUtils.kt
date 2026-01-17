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

import kotlin.reflect.KClass

object ListUtils {
    fun <T> List<T>.nearestFor(item: T): T? {
        return if (isEmpty()) null
        else if (size == 1) first()
        else {
            val curIndex = indexOf(item)
            if (curIndex - 1 >= 0) {
                get(curIndex - 1)
            } else if (curIndex + 1 <= lastIndex) {
                get(curIndex + 1)
            } else {
                null
            }
        }
    }

    inline fun <T, reified R> Iterable<T>.filterIsNotInstance(): List<T> {
        val destination = mutableListOf<T>()
        for (element in this) if (element !is R) destination.add(element)
        return destination
    }

    inline fun <T> Iterable<T>.filterIsNotInstance(vararg types: KClass<*>): List<T> {
        val destination = mutableListOf<T>()
        for (element in this) if (types.all { !it.isInstance(element) }) destination.add(element)
        return destination
    }

    fun <T> Iterable<T>.toggle(item: T): List<T> = run {
        if (item in this) this - item
        else this + item
    }

    fun <T : Any> Iterable<T>.toggleByClass(item: T): List<T> {
        val clazz = item::class
        val hasClass = any { clazz.isInstance(it) }

        return if (hasClass) filterNot { clazz.isInstance(it) }
        else this + item
    }

    inline fun <T> Iterable<T>.replaceAt(index: Int, transform: (T) -> T): List<T> =
        toMutableList().apply {
            this[index] = transform(this[index])
        }

    fun <T> Set<T>.toggle(item: T): Set<T> = run {
        if (item in this) this - item
        else this + item
    }

    inline fun <reified R : Any> Iterable<Any?>.firstOfType(): R? = firstOrNull { it is R } as? R

    operator fun <E> List<E>.component6(): E = get(5)
    operator fun <E> List<E>.component7(): E = get(6)
    operator fun <E> List<E>.component8(): E = get(7)
    operator fun <E> List<E>.component9(): E = get(8)
    operator fun <E> List<E>.component10(): E = get(9)

    inline fun <E> List<E>.leftFrom(index: Int): E = getOrNull(index - 1) ?: get(lastIndex)

    inline fun <E> List<E>.rightFrom(index: Int): E = getOrNull(index + 1) ?: get(0)

    inline fun <E, T : Comparable<T>> List<E>.sortedByKey(
        descending: Boolean,
        crossinline selector: (E) -> T?
    ): List<E> = if (descending) {
        sortedByDescending { selector(it) }
    } else {
        sortedBy { selector(it) }
    }
}