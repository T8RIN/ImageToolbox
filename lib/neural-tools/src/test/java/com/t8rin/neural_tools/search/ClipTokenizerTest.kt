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

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class ClipTokenizerTest {

    private val tokenizer = ClipTokenizer(
        vocabulary = checkNotNull(javaClass.classLoader?.getResourceAsStream("clip/vocab.txt"))
            .bufferedReader().use { reader ->
                reader.readLines().associate { line ->
                    line.substringBefore('\t') to line.substringAfter('\t').toInt()
                }
            }
    )

    @Test
    fun matchesMultilingualModelTokensForWordsContractionsAndPunctuation() {
        assertTokens("hello world", 61694, 10133, 11356)
        assertTokens("A cat's eyes!", 138, 41163, 112, 187, 38144, 106)
    }

    @Test
    fun encodesRussianQueries() {
        assertTokens("собака на пляже", 10956, 79990, 10179, 10122, 556, 13826, 15920)
        assertTokens("красный квадрат", 551, 56680, 11092, 69055, 20004, 23444)
        assertTokens("синий квадрат", 21536, 11550, 69055, 20004, 23444)
        assertTokens("зелёный квадрат", 548, 13519, 17968, 13865, 69055, 20004, 23444)
    }

    @Test
    fun encodesChineseArabicAndFrenchQueries() {
        assertTokens("一个红色的正方形", 2072, 2102, 6305, 6670, 5718, 4791, 4335, 3745)
        assertTokens("كلب على الشاطئ", 16333, 11086, 10560, 59901, 70935, 14286, 32345)
        assertTokens("Un carré rouge", 11038, 101698, 26997)
    }

    @Test
    fun preservesCaseAndAccentsRegardlessOfDeviceLocale() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            assertTokens("I", 146)
            assertTokens("i", 177)
            assertTokens("café", 34551)
            assertTokens("cafe\u0301", 11135, 14601, 82091)
        } finally {
            Locale.setDefault(original)
        }
    }

    @Test
    fun normalizesWhitespaceAndRemovesControlCharacters() {
        assertTokens("HELLO\t\u00a0world", 145, 68999, 11369, 11403, 11356)
        assertTokens("hello\u2028\u2029world", 61694, 10133, 11356)
        assertTokens("hello\u0000world", 61694, 24731, 35703, 10162)
    }

    @Test
    fun recognizesSpecialTokensWithinText() {
        assertTokens("[CLS]hello[MASK]world[SEP]", 101, 61694, 10133, 103, 11356, 102)
    }

    @Test
    fun replacesWholeUnknownWordsAndWordsLongerThanOneHundredCharacters() {
        assertTokens("🐈", 100)
        assertTokens("zzzunknownword🛸", 100)
        assertTokens("a".repeat(101), 100)
    }

    @Test
    fun reservesEndTokenWhenTruncatingLongQueries() {
        val tokens = tokenizer.encode(List(150) { "a" }.joinToString(" "))
        assertEquals(128, tokens.inputIds.size)
        assertEquals(101L, tokens.inputIds.first())
        assertEquals(102L, tokens.inputIds.last())
        assertTrue(tokens.inputIds.sliceArray(1..126).all { it == 169L })
        assertTrue(tokens.attentionMask.all { it == 1L })
    }

    @Test
    fun includesStartAndEndTokensForEmptyTextWithoutUnnecessaryPadding() {
        assertTokens("")
        assertTokens(" \n\t")
    }

    private fun assertTokens(text: String, vararg expected: Int) {
        val tokens = tokenizer.encode(text)
        assertArrayEquals(
            longArrayOf(101, *expected.map(Int::toLong).toLongArray(), 102),
            tokens.inputIds
        )
        assertArrayEquals(LongArray(expected.size + 2) { 1L }, tokens.attentionMask)
    }

    // The fixture retains original IDs from the pinned multilingual vocabulary.
    // Expected sequences were generated independently with Hugging Face tokenizers.
}