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

package ru.tech.imageresizershrinker.core.domain.image.model

import ru.tech.imageresizershrinker.core.domain.image.Metadata

interface ImageData<I> {
    val image: I
    val imageInfo: ImageInfo
    val metadata: Metadata?

    fun copy(
        image: I = this.image,
        imageInfo: ImageInfo = this.imageInfo,
        metadata: Metadata? = this.metadata,
    ): ImageData<I> = ImageData(image, imageInfo, metadata)

    operator fun component1() = image
    operator fun component2() = imageInfo
    operator fun component3() = metadata

    companion object {
        operator fun <I> invoke(
            image: I,
            imageInfo: ImageInfo,
            metadata: Metadata? = null,
        ): ImageData<I> = ImageDataWrapper(image, imageInfo, metadata)
    }
}

private class ImageDataWrapper<I>(
    override val image: I,
    override val imageInfo: ImageInfo,
    override val metadata: Metadata? = null,
) : ImageData<I>