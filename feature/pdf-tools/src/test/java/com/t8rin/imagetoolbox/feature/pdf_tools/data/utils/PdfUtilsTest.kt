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

package com.t8rin.imagetoolbox.feature.pdf_tools.data.utils

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfUtilsTest {

    @Test
    fun `appended page content resets inherited graphics state`() {
        PDDocument().use { document ->
            val page = PDPage()
            document.addPage(page)
            page.setContents(
                PDStream(document).apply {
                    createOutputStream().use {
                        it.write("BT 3 Tr ET".encodeToByteArray())
                    }
                }
            )

            document.writePage(page) {
                moveTo(0f, 0f)
                lineTo(1f, 1f)
                stroke()
            }

            val streams = page.contentStreams.asSequence().map { stream ->
                stream.createInputStream().use { it.readBytes().decodeToString() }
            }.toList()

            assertEquals(3, streams.size)
            assertTrue(streams.first().contains("q"))
            assertTrue(streams.last().startsWith("Q"))
        }
    }
}