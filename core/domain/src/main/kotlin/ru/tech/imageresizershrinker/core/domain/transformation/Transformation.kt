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

interface Transformation<T> {

    /**
     * The unique cache key for this transformation.
     *
     * The key is added to the image request's memory cache key and should contain any params that
     * are part of this transformation (e.g. size, scale, color, radius, etc.).
     */
    val cacheKey: String

    /**
     * Apply the transformation to [input] and return the transformed [T].
     *
     * @param input The input [T] to transform.
     *  Its config will always be ARGB_8888 or RGBA_F16.
     * @param size The size of the image request.
     * @return The transformed [T].
     */
    suspend fun transform(
        input: T,
        size: IntegerSize
    ): T
}