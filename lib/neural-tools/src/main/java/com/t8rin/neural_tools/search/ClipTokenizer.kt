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

package com.t8rin.neural_tools.search

internal class ClipTokenizer(
    private val vocabulary: Map<String, Int>
) {

    fun encode(text: String): Tokens {
        val ids = mutableListOf(START_TOKEN.toLong())
        for (word in basicTokens(text)) {
            for (token in wordPieces(word)) {
                if (ids.size == CONTEXT_LENGTH - 1) break
                ids.add(token.toLong())
            }
            if (ids.size == CONTEXT_LENGTH - 1) break
        }
        ids.add(END_TOKEN.toLong())
        return Tokens(
            inputIds = ids.toLongArray(),
            attentionMask = LongArray(ids.size) { 1L }
        )
    }

    private fun wordPieces(word: String): List<Int> {
        vocabulary[word]?.let { return listOf(it) }
        val count = word.codePointCount(0, word.length)
        if (count > MAX_WORD_LENGTH) return listOf(UNKNOWN_TOKEN)
        val positions = IntArray(count + 1)
        repeat(count) { index ->
            positions[index + 1] =
                positions[index] + Character.charCount(word.codePointAt(positions[index]))
        }
        val result = mutableListOf<Int>()
        var start = 0
        while (start < count) {
            var end = count
            var token: Int? = null
            while (end > start) {
                val piece = word.substring(positions[start], positions[end])
                token = vocabulary[if (start == 0) piece else "##$piece"]
                if (token != null) break
                end--
            }
            result.add(token ?: return listOf(UNKNOWN_TOKEN))
            start = end
        }
        return result
    }

    private fun basicTokens(text: String): Sequence<String> = sequence {
        val word = StringBuilder()
        var offset = 0
        while (offset < text.length) {
            val special = SPECIAL_TOKENS.firstOrNull { text.startsWith(it, offset) }
            if (special != null) {
                if (word.isNotEmpty()) {
                    yield(word.toString())
                    word.clear()
                }
                yield(special)
                offset += special.length
                continue
            }
            val codePoint = text.codePointAt(offset)
            offset += Character.charCount(codePoint)
            val type = Character.getType(codePoint)
            val whitespace =
                codePoint == 32 || codePoint == 9 || codePoint == 10 || codePoint == 13 ||
                        type == Character.SPACE_SEPARATOR.toInt() ||
                        type == Character.LINE_SEPARATOR.toInt() || type == Character.PARAGRAPH_SEPARATOR.toInt()
            val punctuation = codePoint in 33..47 || codePoint in 58..64 ||
                    codePoint in 91..96 || codePoint in 123..126 || type in PUNCTUATION_TYPES
            when {
                whitespace || punctuation || isChinese(codePoint) -> {
                    if (word.isNotEmpty()) {
                        yield(word.toString())
                        word.clear()
                    }
                    if (!whitespace) yield(String(Character.toChars(codePoint)))
                }

                codePoint == 0 || codePoint == 0xfffd || type == Character.CONTROL.toInt() ||
                        type == Character.FORMAT.toInt() -> Unit

                else -> word.appendCodePoint(codePoint)
            }
        }
        if (word.isNotEmpty()) yield(word.toString())
    }

    internal data class Tokens(
        val inputIds: LongArray,
        val attentionMask: LongArray
    )

    companion object {
        const val CONTEXT_LENGTH = 128
        private const val START_TOKEN = 101
        private const val END_TOKEN = 102
        private const val UNKNOWN_TOKEN = 100
        private const val MAX_WORD_LENGTH = 100

        private val SPECIAL_TOKENS = listOf("[UNK]", "[SEP]", "[PAD]", "[CLS]", "[MASK]")
        private val PUNCTUATION_TYPES = setOf(
            Character.CONNECTOR_PUNCTUATION.toInt(),
            Character.DASH_PUNCTUATION.toInt(),
            Character.START_PUNCTUATION.toInt(),
            Character.END_PUNCTUATION.toInt(),
            Character.INITIAL_QUOTE_PUNCTUATION.toInt(),
            Character.FINAL_QUOTE_PUNCTUATION.toInt(),
            Character.OTHER_PUNCTUATION.toInt()
        )

        private fun isChinese(codePoint: Int): Boolean =
            codePoint in 0x4e00..0x9fff || codePoint in 0x3400..0x4dbf ||
                    codePoint in 0x20000..0x2a6df || codePoint in 0x2a700..0x2b73f ||
                    codePoint in 0x2b740..0x2b81f || codePoint in 0x2b820..0x2ceaf ||
                    codePoint in 0xf900..0xfaff || codePoint in 0x2f800..0x2fa1f
    }
}