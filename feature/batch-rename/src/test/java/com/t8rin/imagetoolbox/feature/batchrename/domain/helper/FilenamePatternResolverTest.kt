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
import org.junit.Assert.assertTrue
import org.junit.Test

class FilenamePatternResolverTest {

    private val resolver = FilenamePatternResolver(prefix = "", suffix = "", randomSeed = 42L)
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

    @Test
    fun formatsWordsInTheOriginalName() {
        val source = file.copy(originalName = "Летний roadTrip_Фото.jpg")
        val names = mapOf(
            "lower" to "летний roadtrip_фото",
            "upper" to "ЛЕТНИЙ ROADTRIP_ФОТО",
            "title" to "Летний Road Trip Фото",
            "snake" to "летний_road_trip_фото",
            "kebab" to "летний-road-trip-фото",
            "camel" to "летнийRoadTripФото",
            "pascal" to "ЛетнийRoadTripФото"
        )
        names.forEach { (style, name) ->
            assertEquals(name, resolve("\\o{$style}", file = source))
        }
    }

    @Test
    fun separatesWordsWithSpacesAndPreservesCapitalization() {
        val source = file.copy(originalName = "NASA_PhotoSet-roadTrip.jpg")
        assertEquals("NASA Photo Set road Trip", resolve("\\o{spaces}", file = source))
    }

    @Test
    fun formatsImageDimensionsWhenAvailable() {
        val pattern = "\\g-\\g{orientation}-\\g{mp}"
        fun geometry(width: Int, height: Int?) = resolve(
            pattern = pattern,
            file = file.copy(width = width, height = height)
        )

        assertEquals("16x9-landscape-2.07", geometry(1920, 1080))
        assertEquals("9x16-portrait-2.07", geometry(1080, 1920))
        assertEquals("1x1-square-0.64", geometry(800, 800))
        assertEquals("--", geometry(1080, null))
        assertEquals(
            "16x9-\\g{ratio}-\\g{unknown}",
            resolve("\\g-\\g{ratio}-\\g{unknown}", file = file.copy(width = 1920, height = 1080))
        )
    }

    @Test
    fun randomProfilesUseTheSelectedAlphabetAndLength() {
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val upper = lower.uppercase()
        val digits = "0123456789"
        val profiles = mapOf(
            "lower" to listOf(lower),
            "upper" to listOf(upper),
            "mixed" to listOf(lower, upper),
            "lowernum" to listOf(lower, digits),
            "uppernum" to listOf(upper, digits),
            "alnum" to listOf(lower, upper, digits),
            "hex" to listOf(digits + "abcdef"),
            "HEX" to listOf(digits + "ABCDEF"),
            "binary" to listOf("01"),
            "base58" to listOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"),
            "[ЖЯЖ7]" to listOf("ЖЯ7")
        )
        profiles.forEach { (profile, groups) ->
            val alphabet = groups.joinToString("")
            for (length in listOf(1, groups.size, 12).distinct()) {
                val name = resolve("\\r{$length:$profile}")
                assertEquals(profile, length, name.length)
                assertTrue(profile, name.all { it in alphabet })
                if (length >= groups.size) {
                    groups.forEach { group -> assertTrue(profile, name.any { it in group }) }
                }
            }
        }
    }

    @Test
    fun repeatedBareRandomTokensKeepTheSameFourDigits() {
        val (first, second, explicit) = resolve("\\r-\\r-\\r{9}").split('-')
        assertTrue(first.matches(Regex("[0-9]{4}")))
        assertEquals(first, second)
        assertTrue(explicit.matches(Regex("[0-9]{9}")))
    }

    @Test
    fun boundsRandomLengthsAndHandlesUnknownAlphabets() {
        assertEquals("beforeafter", resolve("before\\r{0:mixed}after"))
        assertEquals("Я".repeat(500), resolve("\\r{501:[Я]}"))
        assertTrue(resolve("\\r{999999999999}").matches(Regex("[0-9]{4}")))
        assertTrue(resolve("\\r{8:[A/B]}-\\r{6:unknown}").matches(Regex("[0-9]{8}-[0-9]{6}")))
        assertTrue(resolve("\\r{bad}").matches(Regex("""[0-9]{4}\{bad\}""")))
    }

    @Test
    fun randomBatchNamesMatchTheirPreviews() {
        fun names(): List<String> {
            val seeded = FilenamePatternResolver(prefix = "", suffix = "", randomSeed = 19L)
            val pattern = RenamePatterns.sanitize("\\r{12:alnum}-\\r{3:[АБ]}.\\e")
            return List(3) { index ->
                seeded.resolve(pattern, file, sequence = index + 1, dateMillis = 0L)
            }
        }

        val preview = names()
        assertEquals(3, preview.distinct().size)
        assertEquals(preview, names())
    }

    private fun resolve(
        pattern: String,
        sequence: Int = 1,
        file: RenameFile = this.file
    ): String = resolver.resolve(
        pattern = pattern,
        file = file,
        sequence = sequence,
        dateMillis = 0L
    )
}