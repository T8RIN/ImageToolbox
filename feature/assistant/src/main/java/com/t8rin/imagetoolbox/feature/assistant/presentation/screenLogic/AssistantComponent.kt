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

package com.t8rin.imagetoolbox.feature.assistant.presentation.screenLogic

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnStop
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.takePersistablePermission
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.utils.withSelectedUris
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.core.utils.mimeType
import com.t8rin.imagetoolbox.feature.assistant.data.AssistantConversationStore
import com.t8rin.imagetoolbox.feature.assistant.data.AssistantKnowledge
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantReference
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantSetting
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantTool
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantPrompt
import com.t8rin.neural_tools.assistant.AssistantModels
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch as launchImmediate

class AssistantComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val route: Screen.Assistant,
    @Assisted val onGoBack: () -> Unit,
    @Assisted private val onNavigate: (Screen) -> Unit,
    private val knowledge: AssistantKnowledge,
    private val store: AssistantConversationStore,
    private val settings: SettingsManager,
    private val resources: ResourceManager,
    private val imageGetter: ImageGetter<Bitmap>,
    private val appScope: AppScope,
    dispatchers: DispatchersHolder
) : BaseComponent(dispatchers, componentContext) {
    private val models = AssistantModels()
    private val cleanupScope = CoroutineScope(componentScope.coroutineContext + SupervisorJob())
    private var releaseJob: Job? = null
    private var generation: Job? = null

    private val _messages = mutableStateOf(store.messages.value)
    val messages by _messages
    private val _isLoading = mutableStateOf(true)
    val isLoading by _isLoading
    private val _isCheckingModels = mutableStateOf(true)
    val isCheckingModels by _isCheckingModels
    private val _isGenerating = mutableStateOf(false)
    val isGenerating by _isGenerating
    private val _enabled = mutableStateOf(settings.settingsState.value.isAiAssistantEnabled)
    val enabled by _enabled
    private val _selectedModel = mutableStateOf(settings.settingsState.value.aiAssistantModel)
    val selectedModel by _selectedModel
    private val _installations = mutableStateOf<List<AssistantModels.ModelState>>(emptyList())
    val installations by _installations
    private val _error = mutableStateOf<String?>(null)
    val error by _error
    private val _selectedUris =
        mutableStateOf(route.selectedUris.map { it.toString() }.distinct().take(maxSelectedImages))
    val selectedUris by _selectedUris

    private val maxSelectedImages: Int
        get() = if (selectedModel.supportsVision) AssistantModels.MAX_IMAGES else MAX_SELECTED_IMAGES

    private val isBusy: Boolean
        get() = isLoading || isGenerating

    val context: AssistantScreenContext
        get() = AssistantScreenContext(
            toolId = route.source?.id?.takeIf { it >= 0 },
            settingKey = route.settingKey ?: (route.source as? Screen.Settings)
                ?.targetSetting?.key(),
            selectedUris = selectedUris,
            parameters = route.parameters
        )

    init {
        lifecycle.doOnStop { release() }
        lifecycle.doOnDestroy {
            release()
            cleanupScope.launchImmediate {
                try {
                    releaseJob?.join()
                } finally {
                    cleanupScope.cancel()
                }
            }
        }
        componentScope.launchImmediate {
            store.messages.collect { _messages.value = it }
        }
        componentScope.launchImmediate {
            try {
                store.load()
            } finally {
                _isLoading.value = false
            }
            refreshModels()
        }
        componentScope.launchImmediate {
            models.states.collect {
                _installations.value = it
                if (it.any { state -> state.installed } &&
                    !settings.settingsState.value.isAiAssistantSetupComplete
                ) {
                    settings.setAiAssistantSetupComplete()
                }
            }
        }
        componentScope.launchImmediate {
            settings.settingsState.map { it.isAiAssistantEnabled to it.aiAssistantModel }
                .distinctUntilChanged().collectLatest { (isEnabled, model) ->
                    val changedModel = selectedModel != model
                    _enabled.value = isEnabled
                    _selectedModel.value = model
                    if (changedModel) updateSelectedUris(selectedUris)
                    if (!isEnabled || changedModel) release()
                    if (!isEnabled) {
                        AssistantModel.entries.forEach(models::cancelDownload)
                    }
                }
        }
    }

    fun updateSelectedUris(uris: List<String>) {
        _selectedUris.value = uris.distinct().take(maxSelectedImages)
    }

    fun send(question: String, requestContext: AssistantScreenContext = context): Boolean {
        val selected = requestContext.selectedUris.distinct().take(maxSelectedImages)
        val accepted = send(
            AssistantRequest(
                question.trim(),
                requestContext.copy(selectedUris = selected)
            )
        )
        if (accepted) {
            selected.forEach {
                it.toUri().takePersistablePermission(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (!selectedModel.supportsVision) _selectedUris.value = emptyList()
        }
        return accepted
    }

    private fun send(request: AssistantRequest, replaceMessageId: Long? = null): Boolean {
        if (!componentScope.isActive || isBusy) return false
        if (request.query.isBlank() && request.context.selectedUris.isEmpty()) return false
        if (request.query.length > MAX_QUERY_LENGTH) return false
        if (!enabled) {
            _error.value = resources.getString(R.string.assistant_enabled_required)
            return false
        }
        val variant = selectedModel
        val context = request.context.copy(
            selectedUris = request.context.selectedUris.distinct().take(maxSelectedImages)
        )
        val model = variant.id
        val installed = installations.any { it.variant == variant && it.installed }
        if (!AssistantModels.isSupported() || !installed) {
            _error.value = resources.getString(R.string.assistant_model_not_installed)
            return false
        }
        _isGenerating.value = true
        _error.value = null
        generation = componentScope.launchImmediate {
            val id = store.nextMessageId()
            try {
                store.update {
                    it.filterNot { message -> message.id == replaceMessageId } + AssistantMessage(
                        id,
                        request.query,
                        AssistantAnswer(""),
                        context,
                        model
                    )
                }
                persistConversation()
                releaseJob?.join()
                val answer = generateAnswer(variant, id, request.copy(context = context))
                updateMessage(id) { it.copy(answer = answer, error = null) }
            } catch (cancelled: CancellationException) {
                models.release()
                updateMessage(id) { it.copy(error = resources.getString(R.string.assistant_cancelled)) }
                throw cancelled
            } catch (failure: Exception) {
                failure.makeLog()
                val error = when (failure) {
                    is AssistantModels.InputTooLongException -> R.string.assistant_input_too_long
                    is ImageLoadException -> R.string.assistant_image_load_error
                    else -> {
                        models.release()
                        R.string.assistant_generation_error
                    }
                }
                updateMessage(id) { it.copy(error = resources.getString(error)) }
            } finally {
                try {
                    withContext(NonCancellable) { persistConversation() }
                } finally {
                    _isGenerating.value = false
                }
            }
        }
        return true
    }

    private suspend fun generateAnswer(
        variant: AssistantModel,
        id: Long,
        originalRequest: AssistantRequest
    ): AssistantAnswer {
        val metadataStarted = System.nanoTime()
        val images = loadImages(variant, originalRequest.context)
        if (variant.supportsVision && selectedUris == originalRequest.context.selectedUris) {
            _selectedUris.value = emptyList()
        }
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
        val retrieved = knowledge.retrieve(request)
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
            models.generate(variant, prompt, images) { token ->
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
        history = store.messages.value.dropLast(1),
        question = knowledge.userPrompt(request, retrieved),
        hasImage = hasImage
    )

    private class ImageLoadException(cause: Exception? = null) : IllegalArgumentException(cause)

    fun regenerate() {
        if (!componentScope.isActive || !enabled || isBusy) return
        val last = store.messages.value.lastOrNull() ?: return
        send(
            request = AssistantRequest(last.question, last.context),
            replaceMessageId = last.id
        )
    }

    fun newConversation() {
        if (!componentScope.isActive || isBusy) return
        store.update { emptyList() }
        _error.value = null
        componentScope.launchImmediate { persistConversation() }
    }

    fun stop() {
        models.cancel()
        generation?.cancel()
    }

    private fun release() {
        stop()
        val previous = releaseJob
        releaseJob = cleanupScope.launchImmediate {
            previous?.join()
            models.release()
        }
    }

    fun selectModel(model: AssistantModel) {
        componentScope.launchImmediate { settings.setAiAssistantModel(model) }
    }

    fun disable() {
        componentScope.launchImmediate {
            settings.setAiAssistantEnabled(false)
            AssistantModel.entries.forEach(models::cancelDownload)
            onGoBack()
        }
    }

    fun downloadModel(variant: AssistantModel) {
        if (!componentScope.isActive || !enabled || isDownloading(variant)) return
        val models = models
        val settings = settings
        appScope.launchImmediate {
            try {
                if (!settings.settingsState.value.isAiAssistantSetupComplete) {
                    settings.setAiAssistantModel(variant)
                }
                models.download(variant) {}
                settings.setAiAssistantSetupComplete()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                failure.makeLog()
                AppToastHost.showFailureToast(failure)
            }
        }
    }

    fun cancelDownload(variant: AssistantModel) {
        models.cancelDownload(variant)
    }

    fun deleteModel(variant: AssistantModel) {
        if (isGenerating || isDownloading(variant)) return
        componentScope.launchImmediate {
            try {
                models.delete(variant)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                failure.makeLog()
                _error.value = resources.getString(R.string.something_went_wrong)
            }
            refreshModels()
        }
    }

    private fun isDownloading(variant: AssistantModel): Boolean = installations.any {
        it.variant == variant && it.phase in setOf(
            AssistantModels.Phase.Downloading,
            AssistantModels.Phase.Verifying
        )
    }

    private suspend fun refreshModels() {
        _isCheckingModels.value = true
        try {
            models.installations()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            failure.makeLog()
            _error.value = failure.message
        } finally {
            _isCheckingModels.value = false
        }
    }

    fun openTool(tool: AssistantTool, messageContext: AssistantScreenContext) {
        if (!enabled) return
        val screen = knowledge.resolveTool(tool) ?: return
        componentScope.launchImmediate {
            val destination = withContext(ioDispatcher) {
                runCatching {
                    val uris = messageContext.selectedUris.map { it.toUri() }
                    val mime = uris.firstOrNull()?.mimeType()
                    val type = when {
                        mime == "application/pdf" -> ExtraDataType.Pdf
                        mime == "image/gif" -> ExtraDataType.Gif
                        mime?.startsWith("audio/") == true -> ExtraDataType.Audio
                        mime?.startsWith("image/") == true -> null
                        else -> ExtraDataType.File
                    }
                    screen.withSelectedUris(uris, type)
                }.getOrDefault(screen)
            }
            release()
            onNavigate(destination)
        }
    }

    fun openSetting(setting: AssistantSetting) {
        if (!enabled) return
        val target = SettingsGroup.entries.flatMap { it.settingsList }
            .firstOrNull { it.key() == setting.key } ?: return
        release()
        onNavigate(Screen.Settings(targetSetting = target))
    }

    fun openReference(reference: AssistantReference) {
        if (!enabled) return
        release()
        onNavigate(Screen.Help(categoryName = reference.categoryKey, tipId = reference.tipId))
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

    companion object {
        const val MAX_QUERY_LENGTH = 4_000
        private const val MAX_SELECTED_IMAGES = 500
        private const val STREAM_UPDATE_INTERVAL_NS = 40_000_000L
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            route: Screen.Assistant,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): AssistantComponent
    }
}
