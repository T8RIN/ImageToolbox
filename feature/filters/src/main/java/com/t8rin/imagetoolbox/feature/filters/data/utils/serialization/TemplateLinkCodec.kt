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

package com.t8rin.imagetoolbox.feature.filters.data.utils.serialization

import java.io.ByteArrayOutputStream
import java.nio.charset.CharacterCodingException
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import kotlin.io.encoding.Base64

internal object TemplateLinkCodec {
    private const val HEADER = "https://github.com/T8RIN/ImageToolbox?"
    private const val COMPRESSED = "z1."
    private const val MAX_BYTES = 1024 * 1024
    private val maxLinkLength = HEADER.length + (MAX_BYTES + 1024) * 4 / 3 + COMPRESSED.length

    fun encode(template: String): String {
        val bytes = template.encodeToByteArray()
        require(bytes.size <= MAX_BYTES) { "Filter template is too large" }
        val deflater = Deflater(Deflater.BEST_COMPRESSION)
        val compressed = try {
            val output = ByteArrayOutputStream()
            DeflaterOutputStream(output, deflater).use { it.write(bytes) }
            COMPRESSED + Base64.UrlSafe.encode(output.toByteArray())
        } finally {
            deflater.end()
        }
        return HEADER + if (compressed.length < bytes.size) compressed else template
    }

    fun decode(link: String): String {
        require(link.length <= maxLinkLength) { "Filter template link is too large" }
        require(link.startsWith(HEADER)) { "Invalid filter template link" }
        val payload = link.removePrefix(HEADER)
        // Released plain links always contain the template name separator. It is not in URL-safe Base64.
        if ('+' in payload) {
            require(payload.encodeToByteArray().size <= MAX_BYTES) { "Filter template is too large" }
            return payload
        }
        require(payload.startsWith(COMPRESSED)) { "Unsupported filter template link" }
        val inflater = Inflater()
        return try {
            inflater.setInput(Base64.UrlSafe.decode(payload.removePrefix(COMPRESSED)))
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                require(output.size() + count <= MAX_BYTES) { "Filter template is too large" }
                if (count > 0) output.write(buffer, 0, count)
                else require(inflater.finished()) { "Incomplete or unsupported filter template" }
            }
            require(inflater.remaining == 0) { "Unexpected data after filter template" }
            output.toByteArray().decodeToString(throwOnInvalidSequence = true)
        } catch (exception: DataFormatException) {
            throw IllegalArgumentException("Invalid compressed filter template", exception)
        } catch (exception: CharacterCodingException) {
            throw IllegalArgumentException("Invalid text in filter template", exception)
        } finally {
            inflater.end()
        }
    }
}
