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

package com.t8rin.imagetoolbox.feature.batchrename.domain.helper

import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameFile
import org.junit.Assert.assertEquals
import org.junit.Test

class FilenamePatternResolverTest {

    private val resolver = FilenamePatternResolver(prefix = "", suffix = "")
    private val file = RenameFile(uri = "test", originalName = "image.jpg")

    @Test
    fun singleSequenceParameterSetsPaddingAndStartsAtOne() {
        assertEquals("001", resolve(pattern = "\\c{3}", sequence = 1))
        assertEquals("002", resolve(pattern = "\\c{3}", sequence = 2))
        assertEquals("0001", resolve(pattern = "\\c{4}", sequence = 1))
    }

    @Test
    fun multipleSequenceParametersSetStartStepAndPadding() {
        assertEquals("3", resolve(pattern = "\\c{3:2}", sequence = 1))
        assertEquals("5", resolve(pattern = "\\c{3:2}", sequence = 2))
        assertEquals("003", resolve(pattern = "\\c{3:2:3}", sequence = 1))
    }

    private fun resolve(pattern: String, sequence: Int): String = resolver.resolve(
        pattern = pattern,
        file = file,
        sequence = sequence,
        dateMillis = 0L
    )
}