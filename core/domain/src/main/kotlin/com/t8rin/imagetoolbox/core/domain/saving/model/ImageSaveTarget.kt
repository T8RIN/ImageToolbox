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

package com.t8rin.imagetoolbox.core.domain.saving.model

import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.MimeType

data class ImageSaveTarget(
    val imageInfo: ImageInfo,
    override val originalUri: String,
    val sequenceNumber: Int?,
    val metadata: Metadata? = null,
    override val filename: String? = null,
    val imageFormat: ImageFormat = imageInfo.imageFormat,
    override val data: ByteArray,
    override val mimeType: MimeType.Single = imageFormat.mimeType,
    override val extension: String = imageFormat.extension,
    val readFromUriInsteadOfData: Boolean = false,
    val presetInfo: Preset? = null,
    val canSkipIfLarger: Boolean = false
) : SaveTarget {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageSaveTarget

        if (imageInfo != other.imageInfo) return false
        if (originalUri != other.originalUri) return false
        if (sequenceNumber != other.sequenceNumber) return false
        if (metadata != other.metadata) return false
        if (filename != other.filename) return false
        if (imageFormat != other.imageFormat) return false
        if (!data.contentEquals(other.data)) return false
        if (mimeType != other.mimeType) return false
        if (extension != other.extension) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageInfo.hashCode()
        result = 31 * result + originalUri.hashCode()
        result = 31 * result + (sequenceNumber ?: 0)
        result = 31 * result + (metadata?.hashCode() ?: 0)
        result = 31 * result + (filename?.hashCode() ?: 0)
        result = 31 * result + imageFormat.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + extension.hashCode()
        return result
    }

}