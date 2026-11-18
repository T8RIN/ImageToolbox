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

@file:Suppress("DEPRECATION")

package com.t8rin.colors.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class HexVisualTransformation(private val useAlpha: Boolean) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        if (text.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val output = buildString(text.length + 1) {
            append('#')
            text.forEachIndexed { index, char ->
                append(
                    if (useAlpha && text.length >= 7 && index < 2) char.lowercaseChar()
                    else char.uppercaseChar()
                )
            }
        }

        return TransformedText(AnnotatedString(output), hexOffsetMapping)
    }

    private val hexOffsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = offset + 1

        override fun transformedToOriginal(offset: Int): Int = (offset - 1).coerceAtLeast(0)
    }
}
