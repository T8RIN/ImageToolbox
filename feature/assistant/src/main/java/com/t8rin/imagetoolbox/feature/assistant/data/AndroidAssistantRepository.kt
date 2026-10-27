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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.core.utils.mimeType
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantConversationStore
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryCommand
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryCommand.Action
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantKnowledge
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRepository
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantGalleryPrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantPrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.needsGalleryRouting
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

internal class AndroidAssistantRepository @Inject constructor(
    private val knowledge: AssistantKnowledge,
    private val store: AssistantConversationStore,
    private val gallerySearch: AssistantGallerySearch,
    private val settings: SettingsManager,
    private val resources: ResourceManager,
    private val imageGetter: ImageGetter<Bitmap>,
    private val keepAliveService: KeepAliveService,
    private val appScope: AppScope,
    dispatchers: DispatchersHolder
) : AssistantRepository, DispatchersHolder by dispatchers {
    private val models = AssistantModels()
    private var generation: Job? = null
    private var releaseJob: Job? = null
    private var releaseRequested = false

    override val messages = store.messages
    override val installations = models.states
    private val _isLoading = MutableStateFlow(true)
    override val isLoading = _isLoading.asStateFlow()
    private val _isCheckingModels = MutableStateFlow(true)
    override val isCheckingModels = _isCheckingModels.asStateFlow()
    private val _isGenerating = MutableStateFlow(false)
    override val isGenerating = _isGenerating.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    override val error = _error.asStateFlow()

    private val enabled: Boolean
        get() = settings.settingsState.value.isAiAssistantEnabled

    private val selectedModel: AssistantModel
        get() = settings.settingsState.value.aiAssistantModel

    private val maxSelectedImages: Int
        get() = if (selectedModel.supportsVision) AssistantModels.MAX_IMAGES
        else AssistantRepository.MAX_SELECTED_IMAGES

    private val isBusy: Boolean
        get() = isLoading.value || isGenerating.value

    init {
        appScope.launch(uiDispatcher) {
            try {
                store.load()
                refreshModels()
            } finally {
                _isLoading.value = false
            }
        }
        appScope.launch(uiDispatcher) {
            models.states.collect {
                if (it.any { state -> state.installed } &&
                    !settings.settingsState.value.isAiAssistantSetupComplete
                ) {
                    settings.setAiAssistantSetupComplete()
                }
            }
        }
        appScope.launch(uiDispatcher) {
            var model = selectedModel
            settings.settingsState.map { it.isAiAssistantEnabled to it.aiAssistantModel }
                .distinctUntilChanged().collect { (isEnabled, variant) ->
                    if (!isEnabled || model != variant) {
                        stop()
                        releaseWhenIdle()
                    }
                    model = variant
                    if (!isEnabled) AssistantModel.entries.forEach(models::cancelDownload)
                }
        }
    }

    override fun send(request: AssistantRequest): Boolean = send(request, null)

    @SuppressLint("WakelockTimeout")
    private fun send(
        request: AssistantRequest,
        replaceMessageId: Long?,
        galleryAction: (suspend () -> AssistantAnswer)? = null
    ): Boolean {
        if (!appScope.isActive || isBusy) return false
        if (request.query.isBlank() && request.context.selectedUris.isEmpty()) return false
        if (request.query.length > AssistantRepository.MAX_QUERY_LENGTH) return false
        if (!enabled) {
            _error.value = resources.getString(R.string.assistant_enabled_required)
            return false
        }
        val variant = selectedModel
        val context = request.context.copy(
            selectedUris = request.context.selectedUris.distinct().take(maxSelectedImages)
        )
        if (galleryAction == null && !AssistantModels.isSupported()) {
            _error.value = resources.getString(R.string.assistant_model_unavailable)
            return false
        }

        if (!_isGenerating.compareAndSet(
                expect = false,
                update = true
            )
        ) return false

        _error.value = null
        releaseRequested = false
        generation = appScope.launch(uiDispatcher, start = CoroutineStart.LAZY) {
            val id = store.nextMessageId()
            var releaseModel = false
            try {
                store.update {
                    it.filterNot { message -> message.id == replaceMessageId } + AssistantMessage(
                        id,
                        request.query,
                        AssistantAnswer(""),
                        context,
                        variant.id
                    )
                }
                keepAliveService.updateOrStart(
                    title = resources.getString(R.string.ai_assistant),
                    description = resources.getString(R.string.assistant_thinking)
                )
                persistConversation()
                releaseJob?.join()
                val answer = if (galleryAction != null) {
                    galleryAction()
                } else {
                    withTimeout(REPLY_TIMEOUT_MS) {
                        generateAnswer(variant, id, request.copy(context = context))
                    }
                }
                updateMessage(id) { it.copy(answer = answer, error = null) }
            } catch (_: TimeoutCancellationException) {
                releaseModel = true
                updateMessage(id) {
                    if (it.answer.text.isBlank()) {
                        it.copy(error = resources.getString(R.string.assistant_generation_timeout))
                    } else it
                }
            } catch (cancelled: CancellationException) {
                releaseModel = true
                throw cancelled
            } catch (failure: Exception) {
                failure.makeLog()
                val error = when (failure) {
                    is AssistantModels.InputTooLongException -> R.string.assistant_input_too_long
                    is ImageLoadException -> R.string.assistant_image_load_error
                    else -> {
                        releaseModel = true
                        R.string.assistant_generation_error
                    }
                }
                updateMessage(id) { it.copy(error = resources.getString(error)) }
            } finally {
                withContext(NonCancellable) {
                    try {
                        persistConversation()
                    } finally {
                        try {
                            if (releaseRequested || releaseModel) models.release()
                        } finally {
                            keepAliveService.stop()
                        }
                    }
                }
            }
        }
        generation?.invokeOnCompletion { _isGenerating.value = false }
        generation?.start()
        return true
    }

    private suspend fun generateAnswer(
        variant: AssistantModel,
        id: Long,
        originalRequest: AssistantRequest
    ): AssistantAnswer {
        val images = loadImages(variant, originalRequest.context)
        if (images.isEmpty() && needsGalleryRouting(
                originalRequest.query,
                gallerySearch.currentQuery()
            )
        ) {
            val capabilities = try {
                gallerySearch.capabilities()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                "Photo library status unavailable; validate all capabilities before searching."
            }
            val output = models.generate(
                variant = variant,
                prompt = assistantGalleryPrompt(
                    question = originalRequest.query,
                    capabilities = capabilities,
                    current = gallerySearch.currentQuery(),
                    hasAttachments = originalRequest.context.selectedUris.isNotEmpty()
                ),
                maxOutputTokens = 256,
                stopOnRepetition = false
            ) {}
            val command = runCatching { AssistantGalleryCommand.parse(output) }
                .onFailure { it.makeLog() }.getOrNull()
            makeLog("action=${command?.action}", tag = "AssistantRouting")
            when (command?.action) {
                Action.Search, Action.Refine -> return gallerySearch.search(
                    query = checkNotNull(command.query),
                    refine = command.isRefinement(originalRequest.query),
                    sessionId = id
                )

                Action.More -> return messages.value.dropLast(1)
                    .lastOrNull { it.answer.gallery != null }
                    ?.answer?.gallery?.let { gallerySearch.more(it.sessionId) }
                    ?: AssistantAnswer(resources.getString(R.string.assistant_gallery_no_search))

                Action.Clarify -> return AssistantAnswer(resources.getString(R.string.assistant_gallery_parse_error))
                Action.Help -> Unit
                null -> Unit
            }
        }
        val metadataStarted = System.nanoTime()
        val request = originalRequest.copy(
            query = originalRequest.query.ifBlank {
                resources.getString(
                    if (images.isNotEmpty()) R.string.assistant_vision_photo_prompt
                    else R.string.assistant_photo_prompt
                )
            },
            context = withFileInfo(originalRequest.context)
        )
        updateMessage(id) { it.copy(context = request.context) }
        val retrievalStarted = System.nanoTime()
        val previousAnswer = messages.value.dropLast(1).lastOrNull()?.takeIf {
            it.error == null && request.context.toolId == null && request.context.settingKey == null &&
                    followUpQuestion.containsMatchIn(request.query)
        }?.answer
        val previousTool = previousAnswer?.tools?.singleOrNull()
        val previousScreen = previousTool?.let(knowledge::resolveTool)
        val retrievalRequest = if (
            request.context.toolId == null && previousScreen != null &&
            followUpQuestion.containsMatchIn(request.query)
        ) {
            request.copy(
                context = request.context.copy(
                    toolId = previousScreen.id,
                    parameters = request.context.parameters + mapOf(
                        "toolKey" to previousScreen.simpleName,
                        "mode" to previousTool.title
                    )
                )
            )
        } else request
        val retrieved = knowledge.retrieve(
            retrievalRequest,
            sources = previousAnswer?.references?.map { it.tipId }.orEmpty()
        )
        if (images.isEmpty() && retrieved.references.isEmpty() && retrieved.tools.isEmpty() && retrieved.settings.isEmpty()) {
            return AssistantAnswer(
                resources.getStringLocalized(
                    R.string.assistant_scope,
                    request.language
                        ?: if (request.query.any { it in 'А'..'я' || it == 'ё' || it == 'Ё' }) "ru" else "en"
                )
            )
        }
        val promptStarted = System.nanoTime()
        val prompt = buildPrompt(request, retrieved, variant, images.isNotEmpty())
        val promptFinished = System.nanoTime()
        makeLog(
            "metadataMs=${(retrievalStarted - metadataStarted) / 1_000_000} " +
                    "retrievalMs=${(promptStarted - retrievalStarted) / 1_000_000} " +
                    "promptMs=${(promptFinished - promptStarted) / 1_000_000}",
            tag = "AssistantInference"
        )
        val output = StringBuilder()
        var lastUpdate = 0L
        updateMessage(id) { it.copy(answer = retrieved) }
        try {
            val completed = models.generate(
                variant = variant,
                prompt = prompt,
                images = images,
                maxOutputTokens = when (variant) {
                    AssistantModel.Lite -> 160
                    AssistantModel.Advanced -> 224
                    else -> 288
                },
                repetitionPenalty = 1.1
            ) { token ->
                output.append(token)
                val now = System.nanoTime()
                if (now - lastUpdate >= STREAM_UPDATE_INTERVAL_NS) {
                    lastUpdate = now
                    val text = output.toString()
                    withContext(uiDispatcher) {
                        updateMessage(id) { it.copy(answer = retrieved.copy(text = text)) }
                    }
                }
            }
            output.clear().append(completed)
            check(output.isNotBlank()) { "The model returned an empty answer" }
            return retrieved.copy(text = output.toString().trim())
        } finally {
            withContext(NonCancellable + uiDispatcher) {
                updateMessage(id) {
                    it.copy(
                        answer = retrieved.copy(
                            text = output.toString().trim()
                        )
                    )
                }
            }
        }
    }

    private suspend fun withFileInfo(context: AssistantScreenContext): AssistantScreenContext =
        withContext(ioDispatcher) {
            val info = context.selectedUris.take(3).mapIndexedNotNull { index, selected ->
                val uri = selected.toUri()
                val mime = runCatching { uri.mimeType() }.getOrNull()
                val size = runCatching { uri.fileSize() }.getOrNull()?.takeIf { it > 0 }
                val dimensions = if (mime?.startsWith("image/") == true) uri.imageSize() else null
                val details = listOfNotNull(
                    mime,
                    dimensions?.let { "${it.width}x${it.height}px" },
                    size?.let { "$it bytes" }
                )
                details.takeIf { it.isNotEmpty() }?.joinToString(prefix = "${index + 1}: ")
            }.joinToString("; ")
            context.copy(
                parameters = (context.parameters - "selectedImageInfo") +
                        if (info.isNotEmpty()) mapOf("selectedImageInfo" to info) else emptyMap()
            )
        }

    private suspend fun loadImages(
        variant: AssistantModel,
        context: AssistantScreenContext
    ): List<Bitmap> {
        if (!variant.supportsVision) return emptyList()
        val uris = withContext(ioDispatcher) {
            context.selectedUris.filter { uri ->
                val mime = runCatching { uri.toUri().mimeType() }.getOrNull()
                mime == null || mime.startsWith("image/")
            }
        }
        return uris.map { uri ->
            try {
                imageGetter.getImage(data = uri, size = AssistantModels.MAX_IMAGE_SIZE)
                    ?: throw ImageLoadException()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                throw ImageLoadException(error)
            }
        }
    }

    private fun buildPrompt(
        request: AssistantRequest,
        retrieved: AssistantAnswer,
        variant: AssistantModel,
        hasImage: Boolean
    ): String = assistantPrompt(
        grounding = knowledge.grounding(request, retrieved, variant, hasImage),
        history = store.messages.value.dropLast(1).filter {
            it.answer.references.isNotEmpty() || it.answer.tools.isNotEmpty() || it.answer.settings.isNotEmpty() ||
                    it.context.selectedUris.isNotEmpty() || it.context.toolId != null || it.context.settingKey != null
        },
        question = knowledge.userPrompt(request, retrieved),
        hasImage = hasImage
    )

    private class ImageLoadException(cause: Exception? = null) : IllegalArgumentException(cause)

    override fun regenerate() {
        if (!appScope.isActive || !enabled || isBusy) return
        val last = messages.value.lastOrNull() ?: return
        val result = last.answer.gallery
        if (result != null && canContinueGallery(last)) {
            send(AssistantRequest(last.question, last.context), last.id) {
                gallerySearch.retry(result.sessionId)
            }
        } else {
            send(AssistantRequest(last.question, last.context), last.id)
        }
    }

    override fun newConversation() {
        if (!appScope.isActive || isBusy) return
        gallerySearch.clear()
        store.update { emptyList() }
        _error.value = null
        appScope.launch(uiDispatcher) { persistConversation() }
    }

    override fun stop() {
        models.cancel()
        generation?.cancel()
    }

    override fun releaseWhenIdle() {
        releaseRequested = true
        if (isGenerating.value) return
        val previous = releaseJob
        releaseJob = appScope.launch(uiDispatcher) {
            previous?.join()
            models.release()
        }
    }

    override fun canContinueGallery(message: AssistantMessage): Boolean =
        message.answer.gallery?.let { gallerySearch.isCurrent(it.sessionId) } == true &&
                messages.value.lastOrNull { it.answer.gallery != null }?.id == message.id

    override fun chooseGalleryPerson(message: AssistantMessage, personId: Long) {
        val result = message.answer.gallery ?: return
        if (!canContinueGallery(message)) return
        send(AssistantRequest(message.question, message.context), message.id) {
            gallerySearch.choosePerson(result.sessionId, personId)
        }
    }

    override suspend fun prepareModel(model: AssistantModel) {
        releaseJob?.join()
        if (!enabled || selectedModel != model) return
        releaseRequested = false
        try {
            models.prepare(model)
        } finally {
            if (!isGenerating.value && (releaseRequested || !enabled || selectedModel != model)) {
                models.release()
            }
        }
    }

    override suspend fun downloadModel(model: AssistantModel) {
        models.download(model) {}
    }

    override fun cancelDownload(model: AssistantModel) {
        models.cancelDownload(model)
    }

    override fun deleteModel(model: AssistantModel) {
        if (isGenerating.value || isDownloading(model)) return
        appScope.launch(uiDispatcher) {
            try {
                models.delete(model)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                failure.makeLog()
                _error.value = resources.getString(R.string.something_went_wrong)
            }
            refreshModels()
        }
    }

    private fun isDownloading(model: AssistantModel): Boolean = installations.value.any {
        it.variant == model && it.phase in setOf(
            AssistantModels.Phase.Downloading,
            AssistantModels.Phase.Verifying
        )
    }

    private suspend fun refreshModels() {
        _isCheckingModels.value = true
        try {
            models.installations(verifyFiles = false)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            failure.makeLog()
            _error.value = failure.message
        } finally {
            _isCheckingModels.value = false
        }
    }

    private suspend fun persistConversation() {
        try {
            store.save()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            failure.makeLog()
            _error.value = resources.getString(R.string.assistant_history_save_error)
        }
    }

    private fun updateMessage(id: Long, update: (AssistantMessage) -> AssistantMessage) {
        store.update { messages -> messages.map { if (it.id == id) update(it) else it } }
    }

    private companion object {
        const val REPLY_TIMEOUT_MS = 120_000L
        const val STREAM_UPDATE_INTERVAL_NS = 40_000_000L
        val followUpQuestion = Regex(
            """(?<!\p{L})(?:это|этим|этого|эти|ними|его|неё|ней|них|так|this|that|these|those|it|them|same)(?!\p{L})""",
            RegexOption.IGNORE_CASE
        )
    }
}