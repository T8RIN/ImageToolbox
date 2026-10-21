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

package com.t8rin.imagetoolbox.feature.assistant.presentation.components

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantAnswerTextTest {
    @Test
    fun preservesMarkdownStructureAndRussianText() {
        val html =
            assistantMarkdownHtml("## Справка\n\n**Выберите** инструмент.\n\n- Первый шаг\n- Второй шаг")

        assertTrue(html.contains("<h2>Справка</h2>"))
        assertTrue(html.contains("<strong>Выберите</strong>"))
        assertTrue(html.contains("<li>Первый шаг</li>"))
        assertTrue(html.contains("<li>Второй шаг</li>"))
    }

    @Test
    fun preservesLinkLabelsAndCodeWithoutTreatingHtmlAsMarkup() {
        val html = assistantMarkdownHtml("[Help](https://example.org/help) `EXIF` <b>text</b>")

        assertTrue(html.contains("Help"))
        assertFalse(html.contains("href="))
        assertTrue(html.contains("<tt>EXIF</tt>"))
        assertTrue(html.contains("&lt;b&gt;text&lt;/b&gt;"))
    }

    @Test
    fun generatedNavigationAndImagesAreRenderedAsText() {
        val html = assistantMarkdownHtml(
            "[**Настройка**](imagetoolbox://settings/unknown) " +
                    "![Пример](https://example.org/invented.png) <https://example.org/help>"
        )

        assertTrue(html.contains("<strong>Настройка</strong>"))
        assertTrue(html.contains("Пример"))
        assertFalse(html.contains("<a "))
        assertFalse(html.contains("<img"))
        assertFalse(html.contains("imagetoolbox://"))
    }

    @Test
    fun preservesCodeIndentationAndOrderedStepsForAndroidHtml() {
        val html = assistantMarkdownHtml("3. First\n4. Second\n\n```\na < b\n  next\n```")

        assertTrue(html.contains("3. First"))
        assertTrue(html.contains("4. Second"))
        assertTrue(html.contains("a\u00A0&lt;\u00A0b"))
        assertTrue(html.contains("<br><tt>\u00A0\u00A0next</tt>"))
        assertFalse(html.contains("<li>"))
    }

    @Test
    fun stripsExecutableLinkDestinations() {
        val html = assistantMarkdownHtml("[Open](javascript:alert%281%29)")

        assertFalse(html.contains("javascript:"))
        assertTrue(html.contains("Open"))
    }
}