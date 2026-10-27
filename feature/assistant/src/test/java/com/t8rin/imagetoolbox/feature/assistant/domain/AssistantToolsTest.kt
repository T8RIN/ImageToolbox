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

package com.t8rin.imagetoolbox.feature.assistant.domain

import org.junit.Assert.*
import org.junit.Test

class AssistantToolsTest {
    @Test
    fun hidesToolCallsWhileStreamingButKeepsNormalAnswers() {
        val call = """<tool_call>{"name":"find_photos","arguments":{}}"""
        for (size in 1..call.length) assertEquals("", AssistantTools.replyText(call.take(size)))
        assertEquals("Hola amigos!", AssistantTools.replyText("Hola amigos!"))
    }

    @Test
    fun answersSupportEveryLanguageWithoutAClassifierPrefix() {
        val answer = "أي ألبوم تريد؟"
        assertEquals(answer, (AssistantTools.parse(answer) as AssistantTools.Result.Answer).text)
    }
}