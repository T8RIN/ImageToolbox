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

package com.t8rin.neural_tools.assistant

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AssistantRepetitionTest {
    @Test
    fun stopsRepeatedWordsAndParagraphs() {
        listOf("какого-то ", "Use the same tool. \n", "повтор").forEach { phrase ->
            val prefix = "Ответ: "
            val text = prefix + phrase.repeat(12)
            val end = checkNotNull(repeatedAssistantSuffix(text))
            assertEquals((prefix + phrase).trim(), text.take(end).trim())
        }
    }

    @Test
    fun stopsBeforeAThirdRepeatedSentence() {
        val sentence = "Удаление EXIF: Стереть данные о месте съёмки.  \n"
        val text = sentence.repeat(2) + sentence.take(20)
        val end = checkNotNull(repeatedAssistantSuffix(text))
        assertEquals(sentence.trim(), text.take(end).trim())
    }

    @Test
    fun keepsNormalListsCodeAndShortRepetitions() {
        listOf(
            "Да, да, да.",
            "1. Выберите фото.\n2. Выберите формат.\n3. Выберите папку.",
            "if (ready) { save(); } else { retry(); }",
            "Нажмите Сохранить. Откройте результат и проверьте его."
        ).forEach { assertNull(it, repeatedAssistantSuffix(it)) }
    }
}
