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

package com.t8rin.imagetoolbox.core.domain.image

import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation

interface ImageGetter<I> {

    suspend fun getImage(
        uri: String,
        originalSize: Boolean = true,
        onFailure: (Throwable) -> Unit = {}
    ): ImageData<I>?

    fun getImageAsync(
        uri: String,
        originalSize: Boolean = true,
        onGetImage: (ImageData<I>) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getImageWithTransformations(
        uri: String,
        transformations: List<Transformation<I>>,
        originalSize: Boolean = true
    ): ImageData<I>?

    suspend fun getImageWithTransformations(
        data: Any,
        transformations: List<Transformation<I>>,
        size: IntegerSize?
    ): I?

    suspend fun getImage(
        data: Any,
        originalSize: Boolean = true
    ): I?

    suspend fun getImage(
        data: Any,
        size: IntegerSize?
    ): I?

    suspend fun getImage(
        data: Any,
        size: Int?
    ): I?

    suspend fun getImageData(
        uri: String,
        size: Int?,
        onFailure: (Throwable) -> Unit
    ): ImageData<I>?

    fun getExtension(
        uri: String
    ): String?

}