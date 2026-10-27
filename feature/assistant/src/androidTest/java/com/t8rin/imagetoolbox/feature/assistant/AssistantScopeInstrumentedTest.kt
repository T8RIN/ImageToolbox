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

package com.t8rin.imagetoolbox.feature.assistant

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssistantScopeInstrumentedTest {
    @Test
    fun unrelatedQuestionsDoNotRetrieveAppInstructions() = runBlocking {
        val app = InstrumentationRegistry.getInstrumentation().targetContext
        val knowledge = app.entryPoint<AssistantEntryPoint>().knowledge
        listOf(
            AssistantRequest("как какать", language = "ru"),
            AssistantRequest("Write a recipe for chocolate cake.", language = "en"),
            AssistantRequest("Напиши стихотворение про осень.", language = "ru"),
            AssistantRequest("How does file teleportation between galaxies work?", language = "en")
        ).forEach { request ->
            val retrieved = knowledge.retrieve(request)
            assertTrue(
                request.query, retrieved.references.isEmpty() &&
                        retrieved.tools.isEmpty() && retrieved.settings.isEmpty()
            )
        }
    }
}
