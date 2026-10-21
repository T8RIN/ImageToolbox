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

package com.t8rin.imagetoolbox.feature.assistant.data

import android.content.Context
import android.util.AtomicFile
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssistantConversationStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val file = AtomicFile(File(context.filesDir, "assistant/conversation.json"))
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }
    private val messageId = AtomicLong(System.currentTimeMillis())
    private val _messages = MutableStateFlow<List<AssistantMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    @Volatile
    private var loaded = false

    suspend fun load(): List<AssistantMessage> = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (!loaded) {
                _messages.value = runCatching {
                    file.openRead().use { input ->
                        check(input.channel.size() <= MAX_FILE_SIZE)
                        json.decodeFromString<List<AssistantMessage>>(
                            input.readBytes().decodeToString()
                        )
                            .takeLast(MAX_MESSAGES)
                    }
                }.getOrDefault(emptyList())
                messageId.updateAndGet {
                    maxOf(
                        it,
                        _messages.value.maxOfOrNull(AssistantMessage::id) ?: 0L
                    )
                }
                loaded = true
            }
            _messages.value
        }
    }

    fun update(transform: (List<AssistantMessage>) -> List<AssistantMessage>) {
        check(loaded) { "Load the conversation before updating it" }
        _messages.update { transform(it).takeLast(MAX_MESSAGES) }
    }

    fun nextMessageId(): Long = messageId.updateAndGet { maxOf(it + 1, System.currentTimeMillis()) }

    suspend fun save() = withContext(NonCancellable + Dispatchers.IO) {
        mutex.withLock {
            check(loaded) { "Load the conversation before saving it" }
            check(file.baseFile.parentFile!!.mkdirs() || file.baseFile.parentFile!!.isDirectory)
            val stream = file.startWrite()
            try {
                var persisted = _messages.value
                var encoded = json.encodeToString(persisted).encodeToByteArray()
                while (encoded.size > MAX_SAVED_SIZE && persisted.size > 1) {
                    persisted = persisted.drop(1)
                    encoded = json.encodeToString(persisted).encodeToByteArray()
                }
                check(encoded.size <= MAX_SAVED_SIZE) { "Conversation is too large" }
                stream.write(encoded)
                file.finishWrite(stream)
            } catch (error: Throwable) {
                file.failWrite(stream)
                throw error
            }
        }
    }

    private companion object {
        const val MAX_MESSAGES = 80
        const val MAX_FILE_SIZE = 2_000_000
        const val MAX_SAVED_SIZE = 1_900_000
    }
}
