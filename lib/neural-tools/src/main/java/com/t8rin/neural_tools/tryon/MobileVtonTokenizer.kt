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

package com.t8rin.neural_tools.tryon

import java.text.Normalizer
import java.util.Locale

internal class MobileVtonTokenizer(
    private val vocabulary: Map<String, Int>,
    merges: List<String>,
    private val padToken: Int
) {

    private val startToken = vocabulary.getValue("<|startoftext|>").toLong()
    private val endToken = vocabulary.getValue("<|endoftext|>").toLong()
    private val ranks = merges.asSequence()
        .filter { it.isNotBlank() && !it.startsWith("#version:") }
        .take(48894)
        .map { it.trim().split(' ') }
        .mapIndexed { index, pair ->
            require(pair.size == 2) { "Invalid CLIP merge" }
            (pair[0] to pair[1]) to index
        }
        .toMap()

    private val pattern = if (padToken == 0) {
        // CLIP's second tokenizer registers its literal padding token as an added token.
        Regex("!|<\\|startoftext\\|>|<\\|endoftext\\|>|'s|'t|'re|'ve|'m|'ll|'d|[\\p{L}]+|[\\p{N}]|[^\\s\\p{Z}\\p{L}\\p{N}!]+")
    } else {
        Regex("<\\|startoftext\\|>|<\\|endoftext\\|>|'s|'t|'re|'ve|'m|'ll|'d|[\\p{L}]+|[\\p{N}]|[^\\s\\p{Z}\\p{L}\\p{N}]+")
    }

    fun encode(text: String): LongArray {
        val result = LongArray(77) { padToken.toLong() }
        result[0] = startToken
        var position = 1
        for (match in pattern.findAll(normalize(text))) {
            val pieces = when (val token = match.value) {
                "<|startoftext|>", "<|endoftext|>" -> listOf(token)
                "!" -> if (padToken == 0) listOf(token) else merge(token)
                else -> merge(token)
            }
            for (piece in pieces) {
                if (position == result.lastIndex) break
                result[position++] = vocabulary[piece]?.toLong() ?: endToken
            }
            if (position == result.lastIndex) break
        }
        result[position] = endToken
        return result
    }

    private fun merge(token: String): List<String> {
        val parts = token.toByteArray(Charsets.UTF_8).map { byte ->
            byteCharacters[byte.toInt() and 255].toString()
        }.toMutableList()
        parts[parts.lastIndex] += "</w>"
        while (parts.size > 1) {
            var bestRank = Int.MAX_VALUE
            var bestPair: Pair<String, String>? = null
            for (index in 0 until parts.lastIndex) {
                val pair = parts[index] to parts[index + 1]
                val rank = ranks[pair] ?: continue
                if (rank < bestRank) {
                    bestRank = rank
                    bestPair = pair
                }
            }
            val pair = bestPair ?: break
            var index = 0
            while (index < parts.lastIndex) {
                if (parts[index] == pair.first && parts[index + 1] == pair.second) {
                    parts[index] += parts.removeAt(index + 1)
                }
                index++
            }
        }
        return parts
    }

    private fun normalize(text: String): String {
        var normalized = text
        repeat(2) {
            normalized = entities.replace(normalized) { match ->
                val entity = match.groupValues[1]
                val codePoint = when {
                    entity.startsWith("#x", ignoreCase = true) -> entity.substring(2)
                        .toIntOrNull(16)

                    entity.startsWith('#') -> entity.substring(1).toIntOrNull()
                    else -> null
                }
                if (codePoint != null && Character.isValidCodePoint(codePoint) && codePoint !in 0xD800..0xDFFF) {
                    String(Character.toChars(codePoint))
                } else {
                    when (entity) {
                        "amp" -> "&"
                        "quot" -> "\""
                        "apos" -> "'"
                        "lt" -> "<"
                        "gt" -> ">"
                        "nbsp" -> " "
                        else -> match.value
                    }
                }
            }
        }
        return Normalizer.normalize(normalized, Normalizer.Form.NFC).lowercase(Locale.ROOT)
    }

    private companion object {
        val entities = Regex("&(#x[0-9a-fA-F]+|#[0-9]+|amp|quot|apos|lt|gt|nbsp);")
        val byteCharacters = run {
            var next = 256
            CharArray(256) { byte ->
                if (byte in 33..126 || byte in 161..172 || byte in 174..255) {
                    byte.toChar()
                } else (next++).toChar()
            }
        }
    }
}