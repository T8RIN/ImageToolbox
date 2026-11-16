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

package com.t8rin.imagetoolbox.feature.maze_generation.data

import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.FileSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import javax.inject.Inject

class MazeSvgFileFactory @Inject constructor(private val filenameCreator: FilenameCreator) {
    fun create(data: ByteArray, width: Int, height: Int): FileSaveTarget {
        // Include the actual SVG bytes so checksum-based names hash the exported document.
        val imageTarget =
            ImageSaveTarget(
                imageInfo = ImageInfo(width = width, height = height),
                originalUri = "",
                sequenceNumber = null,
                metadata = null,
                data = data,
                mimeType = MimeType.Svg,
                extension = "svg",
            )
        return FileSaveTarget(
            originalUri = "",
            filename = filenameCreator.constructImageFilename(imageTarget),
            data = data,
            mimeType = MimeType.Svg,
            extension = "svg",
        )
    }
}
