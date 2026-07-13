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

package com.t8rin.imagetoolbox.feature.pdf_tools.data

import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.utils.DefaultPdfFont
import com.tom_roush.fontbox.ttf.TTFParser
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.nameWithoutExtension

class DefaultPdfFontTest {

    @Test
    fun `default PDF font covers every app language and special glyph`() {
        val fontResource = defaultPdfFontResource()
        val fontBytes = GZIPInputStream(Files.newInputStream(fontResource)).use {
            it.readBytes()
        }

        assertTrue(
            "Compressed PDF font must be smaller than the unpacked font",
            Files.size(fontResource) < fontBytes.size
        )

        val missingGlyphs = TTFParser(false, true)
            .parse(ByteArrayInputStream(fontBytes))
            .use { font ->
                assertTrue(
                    "Expected the complete Noto Emoji glyph set",
                    font.numberOfGlyphs >= 26_515
                )
                val cmap = font.unicodeCmapLookup

                (samples + localizedAppText()).flatMap { (group, text) ->
                    text.codePoints()
                        .distinct()
                        .filter { !Character.isWhitespace(it) && !Character.isISOControl(it) }
                        .filter { cmap.getGlyphId(it) == 0 }
                        .mapToObj { codePoint ->
                            String.format(
                                "%s: '%s' (U+%04X)",
                                group,
                                String(Character.toChars(codePoint)),
                                codePoint
                            )
                        }
                        .toList()
                }
            }

        assertTrue(
            "Missing glyphs in ${fontResource.fileName}:\n${missingGlyphs.joinToString("\n")}",
            missingGlyphs.isEmpty()
        )
    }

    private fun localizedAppText(): Map<String, String> {
        val resources = projectRoot().resolve("core/resources/src/main/res")
        val localeDirectory = Regex("values(?:-[a-z]{2,3}(?:-r[A-Z]{2})?)?")
        val localeFiles = Files.list(resources).use { files ->
            files.filter {
                Files.isDirectory(it) && it.fileName.toString().matches(localeDirectory)
            }
                .map { it.resolve("strings.xml") }
                .filter(Files::exists)
                .toList()
        }

        assertTrue("Expected resources for all 44 app languages", localeFiles.size >= 44)

        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

        return localeFiles.associate { file ->
            file.parent.fileName.toString() to
                    documentBuilder.parse(file.toFile()).documentElement.textContent
        }
    }

    private fun defaultPdfFontResource(): Path {
        val resourceName = R.raw::class.java.fields
            .first { it.getInt(null) == DefaultPdfFont }
            .name
        val rawResources = projectRoot()
            .resolve("core/resources/src/main/res/raw")

        return Files.list(rawResources).use { files ->
            files.filter { it.nameWithoutExtension == resourceName }
                .findFirst()
                .orElseThrow()
        }
    }

    private fun projectRoot(): Path = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
        .first { Files.exists(it.resolve("settings.gradle.kts")) }

    private companion object {
        val samples = mapOf(
            "Arabic" to "صفحة {n} من {total} صَفْحَةٌ ٠١٢٣٤٥٦٧٨٩",
            "Persian" to "صفحه {n} از {total} ۰۱۲۳۴۵۶۷۸۹",
            "Uyghur" to "بەت {n} / {total} ئۇيغۇرچە",
            "Hebrew" to "עמוד {n} מתוך {total}",
            "Armenian" to "Էջ {n}՝ {total}-ից",
            "Georgian" to "გვერდი {n} / {total}",
            "Cyrillic" to "Страница {n} из {total} Сторінка Беларуская Қазақша Српски",
            "Greek" to "Σελίδα {n} από {total}",
            "Latin Extended" to "Àéñø Łódź İstanbul Tiếng Việt Français Čeština",
            "Bengali" to "পৃষ্ঠা {n} / {total} বাংলা",
            "Devanagari" to "पृष्ठ {n} / {total} हिन्दी मराठी",
            "Gurmukhi" to "ਪੰਨਾ {n} / {total} ਪੰਜਾਬੀ",
            "Sinhala" to "පිටුව {n} / {total} සිංහල",
            "Tamil" to "பக்கம் {n} / {total} தமிழ்",
            "Telugu" to "పేజీ {n} / {total} తెలుగు",
            "Thai" to "หน้าที่ {n} / {total} ภาษาไทย",
            "Simplified Chinese" to "第 {n} 页，共 {total} 页 简体中文",
            "Traditional Chinese" to "第 {n} 頁，共 {total} 頁 繁體中文",
            "Japanese" to "全 {total} ページ中 {n} ページ 日本語 漢字 ひらがな カタカナ",
            "Korean" to "전체 {total}페이지 중 {n}페이지 한국어 한자",
            "Currencies" to "€ £ ¥ ₩ ₽ ₴ ₹ ₺ ₫ ₪",
            "Math and arrows" to "± × ÷ ≠ ≤ ≥ ∞ √ ∑ ∫ ≈ ← → ↔",
            "Punctuation" to "© ® ™ № … — – ‘ ’ “ ” « » ‹ ›",
            "Symbols" to "✓ ★ ● ○ ▾ ☀ ☂ ☺ ♠ ♣ ♥ ♦",
            "Emoji" to "😀 😄 🎉 👀 👍 🏽 🚀 🔥 ❤ ❤️ ♥ ♥️ 🩷 💔 ☕ ⚠ " +
                    "#️⃣ *️⃣ 0️⃣ 1️⃣ 🇺🇳 🇮🇴 🇭🇲 🇳🇴 🇸🇯 👨‍👩‍👧‍👦"
        )
    }
}