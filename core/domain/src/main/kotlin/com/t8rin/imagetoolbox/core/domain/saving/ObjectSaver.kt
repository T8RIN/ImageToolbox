/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.domain.saving

import kotlin.reflect.KClass

interface ObjectSaver {

    suspend fun <O : Any> saveObject(
        key: String,
        value: O,
    ): Boolean

    suspend fun <O : Any> restoreObject(
        key: String,
        kClass: KClass<O>,
    ): O?

}

suspend inline fun <reified O : Any> ObjectSaver.saveObject(value: O): Boolean = saveObject(
    key = O::class.simpleName.toString(),
    value = value
)

suspend inline fun <reified O : Any> ObjectSaver.restoreObject(): O? = restoreObject(
    key = O::class.simpleName.toString(),
    kClass = O::class
)