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
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.core.utils.mimeType
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantCommand
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantConversationStore
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantFileSizePlan
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryCatalog
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantImageReply
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantKnowledge
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRepository
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantCommandPrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantImagePrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantInterpretationPrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantPrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantReplyPrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.documentsSubject
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
import java.time.LocalDate
import java.util.Locale
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

    private data class PreparedFiles(val context: AssistantScreenContext, val sizes: List<Long>)
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
    private val _progress = MutableStateFlow<AssistantRepository.Progress?>(null)
    override val progress = _progress.asStateFlow()
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
                gallerySearch.restore(store.messages.value.mapNotNull { it.answer.gallery })
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
        _progress.value = AssistantRepository.Progress.PreparingModel
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
                    _progress.value = AssistantRepository.Progress.FindingPhotos
                    galleryAction()
                } else {
                    withTimeout(if (variant == AssistantModel.Max || variant == AssistantModel.VisionMax) MAX_REPLY_TIMEOUT_MS else REPLY_TIMEOUT_MS) {
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
        generation?.invokeOnCompletion {
            _progress.value = null
            _isGenerating.value = false
        }
        generation?.start()
        return true
    }

    private suspend fun generateAnswer(
        variant: AssistantModel,
        id: Long,
        originalRequest: AssistantRequest
    ): AssistantAnswer {
        val started = System.nanoTime()
        val referenceDate = LocalDate.now()
        var passes = 0
        var firstTextMs: Long? = null
        suspend fun <T> stage(name: String, action: suspend () -> T): T {
            val start = System.nanoTime()
            return try {
                action()
            } finally {
                makeLog(
                    tag = "AssistantRequest",
                    data = "model=${variant.id} stage=$name ms=${(System.nanoTime() - start) / 1_000_000}"
                )
            }
        }

        stage("prepare") { models.prepare(variant) }
        _progress.value =
            if (originalRequest.context.selectedUris.isNotEmpty() && variant.supportsVision) {
                AssistantRepository.Progress.ReadingImage
            } else AssistantRepository.Progress.Interpreting
        val images = stage("images") { loadImages(variant, originalRequest.context) }
        val files = stage("fileInfo") { withFileInfo(originalRequest.context) }
        val request = originalRequest.copy(
            query = originalRequest.query.ifBlank {
                resources.getString(if (images.isNotEmpty()) R.string.assistant_vision_photo_prompt else R.string.assistant_photo_prompt)
            },
            context = files.context
        )
        updateMessage(id) { it.copy(context = request.context) }
        val history = messages.value.dropLast(1)
            .filter {
                it.error == null && !it.answer.isScopeNotice && !AssistantCommand.isInternalReply(it.answer.text)
            }.takeLast(2)
        val inputLanguage =
            stage("language") { knowledge.detectLanguage(request.copy(language = null)) }
        val language = request.language?.takeIf(String::isNotBlank) ?: inputLanguage
        ?: history.asReversed().firstNotNullOfOrNull {
            knowledge.detectLanguage(AssistantRequest(it.question))
                ?: knowledge.detectLanguage(AssistantRequest(it.answer.text))
        }
        val localized = request.copy(language = language)
        var retrieved = AssistantAnswer("")
        val output = StringBuilder()
        var lastUpdate = 0L
        val answerTokens = 320
        suspend fun generate(
            prompt: String,
            grammar: String? = null,
            tokens: Int = answerTokens,
            stream: Boolean = true,
            thinking: Boolean = false,
            sample: Boolean = false,
            includeImage: Boolean = true
        ): String {
            passes++
            output.clear()
            return stage("generation$passes") {
                models.generate(
                    variant = variant,
                    prompt = prompt,
                    images = if (includeImage) images else emptyList(),
                    maxOutputTokens = tokens,
                    repetitionPenalty = if (sample || variant == AssistantModel.Lite && grammar == null) 1.05 else 1.0,
                    stopOnRepetition = grammar == null,
                    responseGrammar = grammar,
                    thinking = thinking,
                    sample = sample
                ) { token ->
                    output.append(token)
                    if (stream) {
                        val text = AssistantCommand.visibleText(output.toString())
                        if (text.isNotBlank() && firstTextMs == null) {
                            firstTextMs = (System.nanoTime() - started) / 1_000_000
                            _progress.value = AssistantRepository.Progress.Answering
                        }
                        val now = System.nanoTime()
                        if (text.isNotBlank() && now - lastUpdate >= STREAM_UPDATE_INTERVAL_NS) {
                            lastUpdate = now
                            withContext(uiDispatcher) {
                                updateMessage(id) {
                                    it.copy(
                                        answer = retrieved.copy(
                                            text = text,
                                            references = retrieved.references.distinctBy { reference -> reference.tipId })
                                    )
                                }
                            }
                        }
                    }
                }
            }.trim()
        }

        suspend fun localizeFact(
            answer: AssistantAnswer,
            english: AssistantAnswer,
            targetLanguage: String? = language
        ): AssistantAnswer {
            val target = Locale.forLanguageTag(targetLanguage ?: "")
            if (target.language.isBlank() || target.language == Locale.ENGLISH.language || answer.text != english.text) return answer
            retrieved = answer
            _progress.value = AssistantRepository.Progress.Answering
            val text = AssistantCommand.visibleText(
                generate(
                    assistantPrompt(
                        grounding = "Translate the supplied factual text into ${
                            target.getDisplayLanguage(
                                Locale.ENGLISH
                            )
                        }. Preserve its meaning and limits. Do not add claims, tools or instructions. Return only the translation.",
                        history = emptyList(),
                        question = english.text,
                        hasImage = false,
                        model = variant
                    ),
                    tokens = 192,
                    includeImage = false
                )
            ).trim()
            check(text.isNotEmpty()) { "Assistant returned no translation" }
            return answer.copy(text = text)
        }

        suspend fun clarification(request: AssistantRequest): AssistantAnswer {
            val answer = localizeFact(
                knowledge.clarification(request),
                knowledge.clarification(request.copy(language = "en")), request.language
            )
            if (firstTextMs == null) firstTextMs = (System.nanoTime() - started) / 1_000_000
            return answer
        }

        suspend fun fileSizeAnswer(): AssistantAnswer? {
            if (localized.context.selectedUris.size != 1 || files.sizes.size != 1) return null
            val sizeTool =
                retrieved.tools.firstOrNull { knowledge.resolveTool(it) is Screen.WeightResize }
                    ?: return null
            val amounts = AssistantFileSizePlan.amounts(localized).takeIf { it.isNotEmpty() }
                ?: return null
            _progress.value = AssistantRepository.Progress.Interpreting
            val plan = try {
                AssistantFileSizePlan.parse(
                    generate(
                        AssistantFileSizePlan.prompt(localized, variant),
                        grammar = AssistantFileSizePlan.grammar(amounts),
                        tokens = 64,
                        stream = false,
                        includeImage = false
                    ), amounts
                )
            } catch (failure: IllegalArgumentException) {
                failure.makeLog()
                null
            } ?: return null
            val actualBytes = files.sizes.single()
            val limitBytes = plan.bytes
            if (actualBytes <= limitBytes) {
                val answer = localizeFact(
                    knowledge.fileSizeFits(localized, actualBytes, limitBytes, plan.label),
                    knowledge.fileSizeFits(
                        localized.copy(language = "en"),
                        actualBytes,
                        limitBytes,
                        plan.label
                    )
                )
                if (firstTextMs == null) firstTextMs = (System.nanoTime() - started) / 1_000_000
                return answer
            }
            val reference = retrieved.references.firstOrNull { it.tipId == sizeTool.helpTipId }
                ?: return null
            val source = localizeFact(
                knowledge.documentation(localized, reference.copy(pageIndex = 0), workflow = true),
                knowledge.documentation(
                    localized.copy(language = "en"),
                    reference.copy(pageIndex = 0),
                    workflow = true
                )
            )
            val maximum = limitBytes.toBigDecimal().divide(1024.toBigDecimal()).stripTrailingZeros()
                .toPlainString()
            val label = resources.getStringLocalized(R.string.max_bytes, language.orEmpty())
            if (firstTextMs == null) firstTextMs = (System.nanoTime() - started) / 1_000_000
            return source.copy(text = "$label: $maximum\n\n${source.text}")
        }
        try {
            if (images.isNotEmpty() || originalRequest.query.isBlank()) {
                _progress.value = AssistantRepository.Progress.FindingHelp
                retrieved =
                    stage("help") {
                        knowledge.retrieve(
                            localized.copy(query = originalRequest.query),
                            queries = listOf(originalRequest.query).filter(String::isNotBlank),
                            minimumCoverage = 0.75
                        )
                    }
                val hasImage = images.isNotEmpty()
                fileSizeAnswer()?.let { return it }
                val grounding =
                    knowledge.grounding(
                        localized,
                        retrieved,
                        variant,
                        hasImage = hasImage,
                        includeToolGuide = true
                    )
                val description =
                    hasImage && retrieved.references.isEmpty() && retrieved.tools.isEmpty() && retrieved.settings.isEmpty()
                val liteImage = description && variant == AssistantModel.VisionLite
                val prompt = if (liteImage) {
                    AssistantImageReply.prompt(localized, knowledge.context(localized), variant)
                } else if (description) {
                    assistantImagePrompt(localized, grounding, history, variant)
                } else assistantReplyPrompt(
                    localized,
                    grounding,
                    history,
                    hasImage = hasImage,
                    model = variant
                )
                _progress.value = if (hasImage) AssistantRepository.Progress.ReadingImage
                else AssistantRepository.Progress.Answering
                val completed = generate(
                    prompt,
                    tokens = if (liteImage) 192 else answerTokens,
                    stream = true,
                    sample = false
                )
                val text = if (liteImage) AssistantImageReply.parse(completed)
                else AssistantCommand.visibleText(completed).trim()
                if (firstTextMs == null && text.isNotBlank()) firstTextMs =
                    (System.nanoTime() - started) / 1_000_000
                check(text.isNotEmpty()) { "Assistant returned no user-facing answer" }
                return retrieved.copy(text = text)
            }
            val current = gallerySearch.currentQuery()
            val resolved = localized
            val previousDocumentation = history.lastOrNull()?.answer?.references.orEmpty()
            val documentationTopic =
                previousDocumentation.takeIf { it.isNotEmpty() }?.let { references ->
                stage("historyTopic") {
                    val source = knowledge.retrieve(
                        localized.copy(language = "en"),
                        sources = references.map { it.tipId })
                    (source.references.map { it.title } + source.tools.map { it.title } + source.settings.map { it.title })
                        .distinct().joinToString("; ")
                }
            }
            val interpreted = generate(
                assistantInterpretationPrompt(
                    resolved,
                    current,
                    variant,
                    documentationTopic = documentationTopic,
                    originalQuestion = request.query,
                    context = knowledge.context(localized),
                    history = history
                ),
                grammar = AssistantInterpretation.grammar(variant),
                tokens = 160,
                stream = false
            )
            val interpretation = try {
                AssistantInterpretation.parse(interpreted)
            } catch (failure: IllegalArgumentException) {
                failure.makeLog()
                return clarification(localized)
            }
            makeLog(
                tag = "AssistantRequest",
                data = "model=${variant.id} intent=${interpretation::class.simpleName}"
            )
            if (interpretation == AssistantInterpretation.NextPage) {
                _progress.value = AssistantRepository.Progress.FindingPhotos
                val answer = stage("morePhotos") { moreGalleryPhotos(localized.language) }
                if (answer.text.isNotBlank()) firstTextMs =
                    (System.nanoTime() - started) / 1_000_000
                return answer
            }
            if (interpretation is AssistantInterpretation.Formats) {
                firstTextMs = (System.nanoTime() - started) / 1_000_000
                return knowledge.formats(localized, interpretation.alpha)
            }
            if (interpretation == AssistantInterpretation.Capabilities) {
                val answer = localizeFact(
                    knowledge.capabilities(localized, variant),
                    knowledge.capabilities(localized.copy(language = "en"), variant)
                )
                if (firstTextMs == null) firstTextMs = (System.nanoTime() - started) / 1_000_000
                return answer
            }
            if (interpretation == AssistantInterpretation.Clarify) return clarification(localized)
            if (interpretation !is AssistantInterpretation.Search) {
                val replyRequest = if (interpretation is AssistantInterpretation.Translate) {
                    localized.copy(language = interpretation.language)
                } else localized
                _progress.value = AssistantRepository.Progress.FindingHelp
                retrieved = when {
                    interpretation is AssistantInterpretation.Help -> stage("help") {
                        if (interpretation.subject != null) {
                            knowledge.retrieve(
                                replyRequest.copy(query = interpretation.subject),
                                queries = listOf(interpretation.query),
                                definition = true
                            )
                        } else knowledge.retrieve(
                            replyRequest,
                            queries = listOf(interpretation.query)
                        )
                    }

                    interpretation is AssistantInterpretation.Translate && previousDocumentation.isNotEmpty() -> stage(
                        "translateHelp"
                    ) {
                        val translated = knowledge.retrieve(
                            replyRequest,
                            sources = previousDocumentation.map { it.tipId })
                        translated.copy(references = translated.references.map { reference ->
                            reference.copy(pageIndex = previousDocumentation.firstOrNull { it.tipId == reference.tipId }?.pageIndex)
                        })
                    }

                    else -> AssistantAnswer("")
                }
                if (interpretation is AssistantInterpretation.Help && interpretation.subject != null &&
                    !retrieved.documentsSubject(replyRequest, interpretation.subject)
                ) {
                    return clarification(replyRequest)
                }
                if (interpretation is AssistantInterpretation.Help && retrieved.references.isEmpty() &&
                    retrieved.tools.isEmpty() && retrieved.settings.isEmpty()
                ) {
                    return clarification(replyRequest)
                }
                if (interpretation is AssistantInterpretation.Help &&
                    interpretation.subject == null
                ) {
                    fileSizeAnswer()?.let { return it }
                    retrieved.references.firstOrNull()?.let { reference ->
                        val source = stage("helpSource") {
                            knowledge.documentation(
                                replyRequest,
                                reference.copy(pageIndex = 0),
                                workflow = interpretation.subject == null
                            )
                        }
                        if (variant == AssistantModel.Lite) {
                            val answer = localizeFact(
                                source, knowledge.documentation(
                                    replyRequest.copy(language = "en"),
                                    reference.copy(pageIndex = 0),
                                    workflow = interpretation.subject == null
                                ), replyRequest.language
                            )
                            if (firstTextMs == null) firstTextMs =
                                (System.nanoTime() - started) / 1_000_000
                            return answer
                        }
                        if (source.references.firstOrNull()?.tipId != reference.tipId) retrieved =
                            source.copy(text = "")
                        else retrieved = retrieved.copy(
                            settings = (retrieved.settings + source.settings).distinctBy { it.key }
                        )
                    }
                } else if (variant == AssistantModel.Lite && interpretation is AssistantInterpretation.Translate && retrieved.references.isNotEmpty()) {
                    suspend fun documentation(language: String?): AssistantAnswer {
                        val pages = retrieved.references.map {
                            knowledge.documentation(
                                replyRequest.copy(language = language), it
                            )
                        }
                        return retrieved.copy(text = pages.joinToString("\n\n") { it.text })
                    }

                    val source = stage("helpSource") { documentation(replyRequest.language) }
                    val answer = localizeFact(source, documentation("en"), replyRequest.language)
                    if (firstTextMs == null) firstTextMs = (System.nanoTime() - started) / 1_000_000
                    return answer
                }
                val grounding = if (interpretation == AssistantInterpretation.Answer) {
                    "Answer general knowledge questions, calculations and creative writing requests directly and briefly. Give the explanation already requested instead of asking whether the user wants it. Follow the requested format; for arithmetic use a short equation with its result. Ask for context if a named technical term or subject is unclear; never invent a definition. If asked about your abilities, you explain ImageToolbox tools/settings, answer general questions and search photos. Users apply edits in the tools. Never invent controls or claim files were edited. " +
                            "Reply in ${
                                replyRequest.language?.let {
                                    Locale.forLanguageTag(it).getDisplayLanguage(Locale.ENGLISH)
                                } ?: "the language of the latest question"
                            }." +
                            if (replyRequest.context.selectedUris.isEmpty()) "" else
                                "\nOnly the supplied file metadata is available; you cannot see image pixels. Use that metadata for file properties. Questions about depicted objects require a Vision model.\n" + knowledge.context(
                                    replyRequest
                                )
                } else knowledge.grounding(
                    replyRequest,
                    retrieved,
                    variant,
                    includeToolGuide = interpretation is AssistantInterpretation.Help && interpretation.subject == null
                )
                val replyHistory = history.map { message ->
                    var text = message.answer.text
                    message.answer.tools.forEach { old ->
                        retrieved.tools.firstOrNull {
                            it.key == old.key && it.filterKey == old.filterKey && it.helpTipId == old.helpTipId
                        }?.let { text = text.replace(old.title, it.title, ignoreCase = true) }
                    }
                    message.answer.settings.forEach { old ->
                        retrieved.settings.firstOrNull { it.key == old.key }
                            ?.let { text = text.replace(old.title, it.title, ignoreCase = true) }
                    }
                    message.copy(answer = message.answer.copy(text = text))
                }
                _progress.value = AssistantRepository.Progress.Answering
                val text = AssistantCommand.visibleText(
                    generate(
                        if (interpretation == AssistantInterpretation.Answer) assistantPrompt(
                            grounding = grounding,
                            history = replyHistory.takeLast(1).filter {
                                it.answer.references.isEmpty() && it.answer.tools.isEmpty() &&
                                        it.answer.settings.isEmpty() && it.answer.gallery == null
                            },
                            question = replyRequest.query,
                            hasImage = false,
                            model = variant
                        ) else assistantReplyPrompt(
                            replyRequest, grounding, replyHistory, model = variant
                        ),
                        sample = interpretation == AssistantInterpretation.Answer &&
                                variant != AssistantModel.Lite && variant != AssistantModel.VisionLite,
                        tokens = if (variant == AssistantModel.Lite) 192 else answerTokens
                    )
                ).trim()
                check(text.isNotEmpty()) { "Assistant returned no user-facing answer" }
                return retrieved.copy(text = text)
            }
            val photoRequest = localized
            _progress.value = AssistantRepository.Progress.FindingPhotos
            val catalog = stage("catalog") {
                try {
                    gallerySearch.catalog(request.query).also { catalog ->
                        makeLog(
                            tag = "AssistantRequest",
                            data = "searchActive=${current != null} catalogPeople=${catalog.people.size} catalogAlbums=${catalog.albums.size} catalogFiles=${catalog.files.size}"
                        )
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (failure: Exception) {
                    failure.makeLog()
                    AssistantGalleryCatalog()
                }
            }
            val completed = generate(
                assistantCommandPrompt(
                    photoRequest,
                    current,
                    catalog,
                    variant,
                    originalQuestion = request.query
                ),
                grammar = AssistantCommand.grammar(
                    current,
                    catalog,
                    request.query,
                    localized.language,
                    model = variant
                ),
                tokens = 256,
                stream = false
            )
            val command = try {
                AssistantCommand.parse(completed, current, request.query, referenceDate)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                failure.makeLog()
                firstTextMs = (System.nanoTime() - started) / 1_000_000
                return AssistantAnswer(resources.getString(R.string.assistant_gallery_parse_error))
            }
            val answer = when (command) {
                AssistantCommand.Action.MorePhotos -> stage("morePhotos") {
                    moreGalleryPhotos(
                        localized.language
                    )
                }

                is AssistantCommand.Action.SearchPhotos -> stage("photos") {
                    gallerySearch.search(command.query, command.mode, id, localized.language)
                }

                is AssistantCommand.Action.Answer -> AssistantAnswer(command.text)
            }
            if (firstTextMs == null && answer.text.isNotBlank()) firstTextMs =
                (System.nanoTime() - started) / 1_000_000
            return answer

        } finally {
            makeLog(
                tag = "AssistantRequest",
                data = "model=${variant.id} passes=$passes firstTextMs=$firstTextMs totalMs=${(System.nanoTime() - started) / 1_000_000}"
            )
        }
    }

    private suspend fun moreGalleryPhotos(language: String? = null): AssistantAnswer =
        messages.value.dropLast(1)
        .lastOrNull { it.answer.gallery != null }?.answer?.gallery
            ?.let { gallerySearch.more(it.sessionId, language) }
            ?: AssistantAnswer(
                if (language.isNullOrBlank()) resources.getString(R.string.assistant_gallery_no_search)
                else resources.getStringLocalized(R.string.assistant_gallery_no_search, language)
            )

    private suspend fun withFileInfo(context: AssistantScreenContext): PreparedFiles =
        withContext(ioDispatcher) {
            val sizes = mutableListOf<Long>()
            val info = context.selectedUris.take(3).mapIndexedNotNull { index, selected ->
                val uri = selected.toUri()
                val mime = runCatching { uri.mimeType() }.getOrNull()
                val size = runCatching { uri.fileSize() }.getOrNull()?.takeIf { it > 0 }
                size?.let(sizes::add)
                val dimensions = if (mime?.startsWith("image/") == true) uri.imageSize() else null
                val details = listOfNotNull(
                    mime,
                    dimensions?.let { "${it.width}x${it.height}px" },
                    size?.let {
                        "file size: ${
                            String.format(
                                java.util.Locale.ROOT,
                                "%.1f",
                                it / 1024.0
                            )
                        } KiB ($it bytes)"
                    }
                )
                details.takeIf { it.isNotEmpty() }?.joinToString(prefix = "${index + 1}: ")
            }.joinToString("; ")
            PreparedFiles(
                context.copy(
                parameters = (context.parameters - "selectedImageInfo") +
                        if (info.isNotEmpty()) mapOf("selectedImageInfo" to info) else emptyMap()
                ), sizes
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

    override fun moreGalleryPhotos(message: AssistantMessage) {
        val result = message.answer.gallery ?: return
        if (!canContinueGallery(message)) return
        send(
            AssistantRequest(
                resources.getString(R.string.assistant_gallery_more),
                message.context
            ), null
        ) {
            gallerySearch.more(result.sessionId)
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
        const val MAX_REPLY_TIMEOUT_MS = 180_000L
        const val STREAM_UPDATE_INTERVAL_NS = 40_000_000L

    }
}
