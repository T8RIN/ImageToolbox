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

import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import org.junit.Assert.*
import org.junit.Test

class MazeSvgFileFactoryTest {
    @Test
    fun svgUsesConfiguredNamesIncludingTheExtensionForEverySave() {
        val creator = RecordingFilenameCreator()
        val factory = MazeSvgFileFactory(creator)
        val bytes = "<svg xmlns=\"http://www.w3.org/2000/svg\"/>".toByteArray()
        val first = factory.create(bytes, 800, 600)
        val second = factory.create(bytes, 800, 600)
        assertEquals("configured_prefix_1_suffix.svg", first.filename)
        assertEquals("configured_prefix_2_suffix.svg", second.filename)
        assertEquals(MimeType.Svg, first.mimeType)
        assertEquals("svg", first.extension)
        assertArrayEquals(bytes, first.data)
        assertEquals(2, creator.inputs.size)
    }

    @Test
    fun filenameSettingsReceiveRealSvgBytesAndOutputDimensions() {
        val creator = RecordingFilenameCreator()
        val bytes = "<svg><path d=\"M0 0 L1 1\"/></svg>".toByteArray()
        MazeSvgFileFactory(creator).create(bytes, 2048, 1024)
        val target = creator.inputs.single()
        // Checksum naming must hash this document, not an empty placeholder or a PNG.
        assertArrayEquals(bytes, target.data)
        assertEquals(2048, target.imageInfo.width)
        assertEquals(1024, target.imageInfo.height)
        assertEquals("svg", target.extension)
        assertEquals(MimeType.Svg, target.mimeType)
        assertEquals("", target.originalUri)
    }

    private class RecordingFilenameCreator : FilenameCreator {
        val inputs = mutableListOf<ImageSaveTarget>()

        override fun constructImageFilename(
            saveTarget: ImageSaveTarget,
            oneTimePrefix: String?,
            forceNotAddSizeInFilename: Boolean,
            pattern: String?,
        ): String {
            inputs.add(saveTarget)
            return "configured_prefix_${inputs.size}_suffix.svg"
        }

        override fun constructRandomFilename(extension: String, length: Int): String =
            error("Unexpected random filename")

        override fun getFilename(uri: String): String =
            error("Generated images have no original URI")
    }
}
