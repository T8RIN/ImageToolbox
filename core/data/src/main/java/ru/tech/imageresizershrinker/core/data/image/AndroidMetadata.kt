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

package ru.tech.imageresizershrinker.core.data.image

import com.t8rin.exif.ExifInterface
import ru.tech.imageresizershrinker.core.domain.image.Metadata
import ru.tech.imageresizershrinker.core.domain.image.model.MetadataTag
import java.io.FileDescriptor

private data class ExifInterfaceMetadata(
    private val exifInterface: ExifInterface
) : Metadata {

    override fun saveAttributes(): Metadata = apply {
        exifInterface.saveAttributes()
    }

    override fun getAttribute(
        tag: MetadataTag
    ): String? = exifInterface.getAttribute(tag.key)

    override fun setAttribute(
        tag: MetadataTag,
        value: String?
    ): Metadata = apply {
        exifInterface.setAttribute(tag.key, value)
    }

}

internal fun ExifInterface.toMetadata(): Metadata = ExifInterfaceMetadata(this)

internal fun FileDescriptor.toMetadata(): Metadata = ExifInterface(this).toMetadata()
