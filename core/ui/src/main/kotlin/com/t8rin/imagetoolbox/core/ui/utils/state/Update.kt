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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

inline fun <T> MutableState<T>.update(
    transform: (T) -> T
): T = run {
    transform(this.value).also {
        this.value = it
    }
}

inline fun <T : Any> MutableState<T?>.updateNotNull(
    transform: (T) -> T
): T? = run {
    this.value?.let { nonNull ->
        transform(nonNull).also {
            this.value = it
        }
    } ?: this.value
}

inline fun <T> MutableState<T>.update(
    onValueChanged: () -> Unit,
    transform: (T) -> T
): T = run {
    transform(this.value).also {
        if (this.value != it) onValueChanged()
        this.value = it
    }
}

inline fun <T> MutableState<T>.updateIf(
    predicate: (T) -> Boolean,
    transform: (T) -> T
): MutableState<T> = apply {
    if (predicate(this.value)) {
        this.value = transform(this.value)
    }
}

@Composable
fun <T> derivedValueOf(
    vararg keys: Any?,
    calculation: () -> T
): T = remember(keys) {
    derivedStateOf(calculation)
}.value


//fun <T> T.asFun(): Function1<T, T> {
//    val value = this
//    return { value }
//}