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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantImageReplyTest {
    @Test
    fun displaysTheActualAnswerWithoutInternalReasoning() {
        assertEquals(
            "Two birds are flying.",
            AssistantImageReply.parse("<think>check the image</think>Two birds are flying.")
        )
        assertEquals(
            "The label is not legible.",
            AssistantImageReply.parse(" The label is not legible. ")
        )
        assertEquals("Розовый.", AssistantImageReply.parse("Розовый."))
    }

    @Test
    fun rejectsInternalPlansAndEmptyAnswers() {
        assertTrue(runCatching { AssistantImageReply.parse("<think>checking the image") }.isFailure)
        assertTrue(runCatching { AssistantImageReply.parse("""{"action":"search","mode":"new","query":{}}""") }.isFailure)
    }
}
