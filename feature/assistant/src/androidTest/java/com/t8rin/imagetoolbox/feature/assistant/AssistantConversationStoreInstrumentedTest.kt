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

import android.content.ContextWrapper
import android.util.AtomicFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.feature.assistant.data.AssistantConversationStore
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantReference
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantTool
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AssistantConversationStoreInstrumentedTest {
    private val app = InstrumentationRegistry.getInstrumentation().targetContext
    private val directory = File(app.cacheDir, "assistant_store_${System.nanoTime()}")
        .apply { check(mkdirs()) }
    private val context = object : ContextWrapper(app) {
        override fun getFilesDir(): File = directory
    }
    private val file = AtomicFile(File(directory, "assistant/conversation.json"))
    private val fixture = AssistantMessage(
        id = 1,
        question = "Сожми фотографии до 500 КБ",
        answer = AssistantAnswer("Откройте изменение размера по весу."),
        context = AssistantScreenContext(
            selectedUris = listOf("content://private/selected/image"),
            parameters = mapOf("maxBytes" to "512000")
        )
    )

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun restoresConversationWithSentImageAttachments() = runBlocking {
        val store = AssistantConversationStore(context)
        store.load()
        store.update { listOf(fixture) }
        store.save()
        assertTrue(file.readFully().decodeToString().contains("content://private/selected/image"))
        val restored = AssistantConversationStore(context).load().single()
        assertEquals(fixture, restored)
    }

    @Test
    fun restoresLegacyHistoryWithoutExecutableActions() = runBlocking {
        check(file.baseFile.parentFile!!.mkdirs())
        file.baseFile.writeText(
            """
            [
              {
                "id": 1,
                "question": "Сожми фотографии до 500 КБ",
                "answer": {
                  "text": "Откройте изменение размера по весу.",
                  "references": [{
                    "title": "Размер по весу",
                    "text": "Выберите изображения и укажите размер файла.",
                    "tipId": "weight_resize",
                    "categoryKey": "editing"
                  }],
                  "tools": [{"id": 7, "title": "Размер по весу", "subtitle": "Сжатие"}],
                  "actions": [{"type": "open_tool", "toolId": 7, "parameters": {"maxBytes": "512000"}}],
                  "workflow": {"title": "Сжать фотографии", "steps": [{"type": "open_tool", "toolId": 7}]}
                },
                "context": {
                  "selectedUris": ["content://private/selected/image"],
                  "parameters": {"maxBytes": "512000"}
                },
                "model": "advanced",
                "isOperationResult": false
              },
              {
                "id": 2,
                "question": "Удаление метаданных",
                "answer": {"text": "Готово (1/1)"},
                "context": {"selectedUris": ["content://private/selected/image"]},
                "isOperationResult": true
              }
            ]
            """.trimIndent()
        )
        val expected = listOf(
            fixture.copy(
                model = "advanced",
                answer = fixture.answer.copy(
                    references = listOf(
                        AssistantReference(
                            title = "Размер по весу",
                            text = "Выберите изображения и укажите размер файла.",
                            tipId = "weight_resize",
                            categoryKey = "editing"
                        )
                    ),
                    tools = listOf(AssistantTool(7, "Размер по весу", "Сжатие"))
                )
            ),
            fixture.copy(
                id = 2,
                question = "Удаление метаданных",
                answer = AssistantAnswer("Готово (1/1)"),
                context = fixture.context.copy(parameters = emptyMap())
            )
        )
        val store = AssistantConversationStore(context)
        assertEquals(expected, store.load())
        store.save()
        val saved = file.readFully().decodeToString()
        listOf("actions", "workflow", "isOperationResult").forEach { field ->
            assertFalse(saved.contains("\"$field\""))
        }
        assertEquals(expected, AssistantConversationStore(context).load())
    }

    @Test
    fun preservesLastCommittedMessagesAfterInterruptedAtomicWrite() = runBlocking {
        val store = AssistantConversationStore(context)
        store.load()
        store.update { listOf(fixture) }
        store.save()
        val interrupted = file.startWrite()
        interrupted.write("incomplete JSON".encodeToByteArray())
        file.failWrite(interrupted)
        val restored = AssistantConversationStore(context).load()
        assertEquals(1, restored.size)
        assertEquals(fixture.question, restored.single().question)
        store.update { List(100) { fixture.copy(id = it.toLong()) } }
        store.save()
        val bounded = AssistantConversationStore(context).load()
        assertEquals(80, bounded.size)
        assertEquals(20L, bounded.first().id)
        file.baseFile.writeText("invalid conversation")
        assertEquals(80, store.load().size)
        assertTrue(AssistantConversationStore(context).load().isEmpty())
    }

    @Test
    fun commitsClearedHistoryEvenWhenItsCallerIsCancelled() = runBlocking {
        val store = AssistantConversationStore(context)
        store.load()
        store.update { listOf(fixture) }
        store.save()
        store.update { emptyList() }
        val saving = launch(start = CoroutineStart.UNDISPATCHED) { store.save() }
        saving.cancelAndJoin()
        assertTrue(AssistantConversationStore(context).load().isEmpty())
    }

    @Test
    fun allocatesUniqueMessageIdsAfterRestoringAFutureTimestamp() = runBlocking {
        val future = System.currentTimeMillis() + 60_000
        val store = AssistantConversationStore(context)
        store.load()
        store.update { listOf(fixture.copy(id = future)) }
        store.save()
        val restored = AssistantConversationStore(context)
        restored.load()
        val ids = List(100) { restored.nextMessageId() }
        assertTrue(ids.first() > future)
        assertEquals(ids.distinct().size, ids.size)
        assertEquals(ids.sorted(), ids)
    }

    @Test
    fun sharedConsumersCannotOverwriteNewMessagesOrRestoreClearedOnes() = runBlocking {
        val store = AssistantConversationStore(context)
        store.load()
        var olderConsumer = emptyList<AssistantMessage>()
        var newerConsumer = emptyList<AssistantMessage>()
        val olderObserver = launch(start = CoroutineStart.UNDISPATCHED) {
            store.messages.collect { olderConsumer = it }
        }
        val newerObserver = launch(start = CoroutineStart.UNDISPATCHED) {
            store.messages.collect { newerConsumer = it }
        }
        try {
            store.update { listOf(fixture) }
            yield()
            val obsoleteSnapshot = olderConsumer
            val oldSave = launch(start = CoroutineStart.LAZY) { store.save() }
            store.update { it + fixture.copy(id = 2, question = "Новый вопрос из другого экрана") }
            oldSave.start()
            oldSave.join()
            assertEquals(listOf(1L), obsoleteSnapshot.map { it.id })
            assertEquals(listOf(1L, 2L), olderConsumer.map { it.id })
            assertEquals(olderConsumer, newerConsumer)
            assertEquals(listOf(1L, 2L), AssistantConversationStore(context).load().map { it.id })

            val oldUpdateAndSave = launch(start = CoroutineStart.LAZY) {
                store.update { messages ->
                    messages.map { if (it.id == fixture.id) it.copy(answer = AssistantAnswer("Поздний ответ")) else it }
                }
                store.save()
            }
            store.update { emptyList() }
            oldUpdateAndSave.start()
            oldUpdateAndSave.join()
            assertTrue(olderConsumer.isEmpty())
            assertTrue(newerConsumer.isEmpty())
            assertTrue(AssistantConversationStore(context).load().isEmpty())
        } finally {
            olderObserver.cancelAndJoin()
            newerObserver.cancelAndJoin()
        }
    }
}
