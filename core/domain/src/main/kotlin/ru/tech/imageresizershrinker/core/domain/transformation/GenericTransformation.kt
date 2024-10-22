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

package ru.tech.imageresizershrinker.core.domain.transformation

import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import kotlin.random.Random

class GenericTransformation<T>(
    val key: Any? = Random.nextInt(),
    val action: suspend (T, IntegerSize) -> T
) : Transformation<T> {

    constructor(
        key: Any? = Random.nextInt(),
        action: suspend (T) -> T
    ) : this(
        key, { t, _ -> action(t) }
    )

    override val cacheKey: String
        get() = (action to key).hashCode().toString()

    override suspend fun transform(
        input: T,
        size: IntegerSize
    ): T = action(input, size)
}

class EmptyTransformation<T> : Transformation<T> by GenericTransformation(
    key = Random.nextInt(),
    action = { i -> i }
)