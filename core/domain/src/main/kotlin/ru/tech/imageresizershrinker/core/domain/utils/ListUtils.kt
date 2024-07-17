/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.domain.utils

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
}