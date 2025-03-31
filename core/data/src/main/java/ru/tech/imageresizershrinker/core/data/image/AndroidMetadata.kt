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

import androidx.exifinterface.media.ExifInterface
import ru.tech.imageresizershrinker.core.domain.image.Metadata

private data class AndroidMetadata(
    private val exifInterface: ExifInterface
) : Metadata {

    override fun saveAttributes() = exifInterface.saveAttributes()

    override fun getAttribute(tag: String): String? = exifInterface.getAttribute(tag)

    override fun setAttribute(
        tag: String,
        value: String?
    ) = exifInterface.setAttribute(tag, value)

    override fun removeAttribute(
        tag: String
    ) = setAttribute(
        tag = tag,
        value = null
    )

}

internal fun ExifInterface.toMetadata(): Metadata = AndroidMetadata(this)