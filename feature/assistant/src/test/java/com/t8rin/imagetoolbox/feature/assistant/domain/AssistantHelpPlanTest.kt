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

class AssistantHelpPlanTest {
    @Test
    fun normalizesDocumentationSourcesAndQueries() {
        val plan = AssistantHelpPlan.parse(
            """{"queries":[" image file size ","image file size","compression"],
                "sources":[" resize-by-file-size ","resize-by-file-size"]}"""
        )
        assertEquals(listOf("image file size", "compression"), plan.queries)
        assertEquals(listOf("resize-by-file-size"), plan.sources)
    }

    @Test
    fun rejectsOversizedPlansAndGalleryCommands() {
        listOf(
            """{"queries":[""]}""",
            """{"sources":[""]}""",
            """{"queries":["one","two","three","four"]}""",
            """{"action":"search","semantic":"a cat"}""",
            """Some prose {"sources":[]}"""
        ).forEach { assertTrue(it, runCatching { AssistantHelpPlan.parse(it) }.isFailure) }
    }
}