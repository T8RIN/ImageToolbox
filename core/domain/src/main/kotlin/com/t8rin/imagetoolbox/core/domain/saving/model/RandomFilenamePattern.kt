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

package com.t8rin.imagetoolbox.core.domain.saving.model

import kotlin.random.Random

private const val DIGITS = "0123456789"
private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

enum class RandomFilenamePattern(
    private val key: String,
    private vararg val groups: String
) {
    Digits("", DIGITS),
    Lower("lower", LOWER),
    Upper("upper", UPPER),
    Mixed("mixed", LOWER, UPPER),
    LowerNum("lowernum", LOWER, DIGITS),
    UpperNum("uppernum", UPPER, DIGITS),
    Alnum("alnum", LOWER, UPPER, DIGITS),
    Hex("hex", DIGITS + "abcdef"),
    HexUpper("HEX", DIGITS + "ABCDEF"),
    Binary("binary", "01"),
    Base58("base58", "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");

    private val alphabet = groups.joinToString("")

    fun token(length: Int): String = if (this == Digits) {
        "${FilenamePattern.Rand}{$length}"
    } else {
        "${FilenamePattern.Rand}{$length:$key}"
    }

    fun generate(
        length: Int = DEFAULT_LENGTH,
        random: Random = Random.Default
    ): String {
        if (groups.size == 1 || length < groups.size) return generate(alphabet, length, random)

        val characters = CharArray(length.coerceAtMost(MAX_LENGTH)) { index ->
            groups.getOrElse(index) { alphabet }.random(random)
        }
        characters.shuffle(random)
        return characters.concatToString()
    }

    companion object {
        private const val DEFAULT_LENGTH = 4
        private const val MAX_LENGTH = 500

        private val patternWithLength = Regex("""\\r\{(\d+)(?::([^}]*))?\}""")

        fun replaceIn(pattern: String, random: Random = Random.Default): String {
            val result = pattern.replace(patternWithLength) { match ->
                val length = match.groupValues[1].toIntOrNull() ?: DEFAULT_LENGTH
                val key = match.groupValues[2]
                if (key.startsWith('[') && key.endsWith(']')) {
                    val alphabet = key.substring(1, key.lastIndex).customAlphabet() ?: DIGITS
                    generate(alphabet, length, random)
                } else {
                    val preset = entries.firstOrNull { it.key == key } ?: Digits
                    preset.generate(length, random)
                }
            }
            return if (FilenamePattern.Rand.value in result) {
                result.replace(FilenamePattern.Rand.value, Digits.generate(random = random))
            } else result
        }

        private fun String.customAlphabet(): String? = takeIf {
            isNotEmpty() && all(Char::isLetterOrDigit)
        }?.toSet()?.joinToString("")

        private fun generate(alphabet: String, length: Int, random: Random): String = buildString {
            repeat(length.coerceIn(0, MAX_LENGTH)) {
                append(alphabet.random(random))
            }
        }
    }
}