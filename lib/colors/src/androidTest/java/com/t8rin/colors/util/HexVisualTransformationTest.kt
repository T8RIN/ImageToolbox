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

package com.t8rin.colors.util

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HexVisualTransformationTest {

    @Test
    fun everyComposingCharacterHasItsOwnBounds() {
        val cases = listOf(
            Triple("", false, ""),
            Triple("ab", true, "#AB"),
            Triple("ab12cd", false, "#AB12CD"),
            Triple("ab12cd34", false, "#AB12CD34"),
            Triple("ab12cd34", true, "#ab12CD34")
        )
        for ((text, useAlpha, expected) in cases) {
            val transformed = HexVisualTransformation(useAlpha).filter(AnnotatedString(text))
            val output = transformed.text
            val mapping = transformed.offsetMapping
            for (offset in text.indices) {
                val start = mapping.originalToTransformed(offset)
                val end = mapping.originalToTransformed(offset + 1)
                assertTrue("$offset in ${output.text}", start < end)
                assertEquals(offset, mapping.transformedToOriginal(start))
            }
            assertEquals(output.length, mapping.originalToTransformed(text.length))
            assertEquals(text.length, mapping.transformedToOriginal(output.length))
            assertEquals(expected, output.text)
        }
    }
}
