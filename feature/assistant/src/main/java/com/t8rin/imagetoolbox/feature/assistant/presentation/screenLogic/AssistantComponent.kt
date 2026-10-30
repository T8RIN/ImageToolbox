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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnStop
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.takePersistablePermission
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.utils.withSelectedUris
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.core.utils.mimeType
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryPhoto
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantKnowledge
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantReference
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRepository
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantSetting
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantTool
import com.t8rin.neural_tools.assistant.AssistantModels
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class AssistantComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val route: Screen.Assistant,
    @Assisted val onGoBack: () -> Unit,
    @Assisted private val onNavigate: (Screen) -> Unit,
    private val knowledge: AssistantKnowledge,
    private val repository: AssistantRepository,
    private val settings: SettingsManager,
    private val appScope: AppScope,
    dispatchers: DispatchersHolder
) : BaseComponent(dispatchers, componentContext) {
    private val _messages = mutableStateOf(repository.messages.value)
    val messages by _messages
    private val _isLoading = mutableStateOf(repository.isLoading.value)
    val isLoading by _isLoading
    private val _isCheckingModels = mutableStateOf(repository.isCheckingModels.value)
    val isCheckingModels by _isCheckingModels
    private val _isGenerating = mutableStateOf(repository.isGenerating.value)
    val isGenerating by _isGenerating
    private val _enabled = mutableStateOf(settings.settingsState.value.isAiAssistantEnabled)
    val enabled by _enabled
    private val _selectedModel = mutableStateOf(settings.settingsState.value.aiAssistantModel)
    val selectedModel by _selectedModel
    private val _installations = mutableStateOf(repository.installations.value)
    val installations by _installations
    private val _error = mutableStateOf(repository.error.value)
    val error by _error
    private val _selectedUris =
        mutableStateOf(route.selectedUris.map { it.toString() }.distinct().take(maxSelectedImages))
    val selectedUris by _selectedUris

    private val _galleryPreviewUris = mutableStateOf<List<String>>(emptyList())
    val galleryPreviewUris by _galleryPreviewUris
    private val _galleryPreviewUri = mutableStateOf<String?>(null)
    val galleryPreviewUri by _galleryPreviewUri

    private val maxSelectedImages: Int
        get() = if (selectedModel.supportsVision) AssistantModels.MAX_IMAGES
        else AssistantRepository.MAX_SELECTED_IMAGES

    private val isBusy: Boolean
        get() = repository.isLoading.value || repository.isGenerating.value

    val context: AssistantScreenContext
        get() = AssistantScreenContext(
            toolId = route.source?.id?.takeIf { it >= 0 },
            settingKey = route.settingKey ?: (route.source as? Screen.Settings)
                ?.targetSetting?.key(),
            selectedUris = selectedUris,
            parameters = route.parameters
        )

    init {
        lifecycle.doOnStop { repository.releaseWhenIdle() }
        lifecycle.doOnDestroy { repository.releaseWhenIdle() }
        componentScope.launch {
            repository.messages.collect { _messages.value = it }
        }
        componentScope.launch {
            repository.isLoading.collect { _isLoading.value = it }
        }
        componentScope.launch {
            repository.isCheckingModels.collect { _isCheckingModels.value = it }
        }
        componentScope.launch {
            repository.isGenerating.collect { _isGenerating.value = it }
        }
        componentScope.launch {
            repository.error.collect { _error.value = it }
        }
        componentScope.launch {
            repository.installations.collect { _installations.value = it }
        }
        componentScope.launch {
            settings.settingsState.map { it.isAiAssistantEnabled to it.aiAssistantModel }
                .distinctUntilChanged().collect { (isEnabled, model) ->
                    val changedModel = selectedModel != model
                    _enabled.value = isEnabled
                    _selectedModel.value = model
                    if (changedModel) updateSelectedUris(selectedUris)
                }
        }
    }

    fun updateSelectedUris(uris: List<String>) {
        _selectedUris.value = uris.distinct().take(maxSelectedImages)
    }

    fun send(question: String, requestContext: AssistantScreenContext = context): Boolean {
        val selected = requestContext.selectedUris.distinct().take(maxSelectedImages)
        if (!componentScope.isActive) return false
        val accepted = repository.send(
            AssistantRequest(
                question.trim(),
                requestContext.copy(selectedUris = selected)
            )
        )
        if (accepted) {
            selected.forEach {
                it.toUri().takePersistablePermission(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            _selectedUris.value = emptyList()
        }
        return accepted
    }

    fun regenerate() {
        if (!componentScope.isActive) return
        repository.regenerate()
    }

    fun newConversation() {
        if (!componentScope.isActive || isBusy) return
        repository.newConversation()
        _galleryPreviewUri.value = null
        _galleryPreviewUris.value = emptyList()
    }

    fun stop() {
        repository.stop()
    }

    fun selectModel(model: AssistantModel) {
        if (!componentScope.isActive || !enabled || isBusy) return
        componentScope.launch {
            settings.setAiAssistantModel(model)
            try {
                repository.prepareModel(model)
            } catch (failure: Exception) {
                ensureActive()
                failure.makeLog()
                AppToastHost.showFailureToast(failure)
            }
        }
    }

    fun disable() {
        componentScope.launch {
            settings.setAiAssistantEnabled(false)
            AssistantModel.entries.forEach(repository::cancelDownload)
            onGoBack()
        }
    }

    fun downloadModel(variant: AssistantModel) {
        if (!componentScope.isActive || !enabled || isDownloading(variant)) return
        val repository = repository
        val settings = settings
        appScope.launch {
            try {
                if (!settings.settingsState.value.isAiAssistantSetupComplete) {
                    settings.setAiAssistantModel(variant)
                }
                repository.downloadModel(variant)
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
        repository.cancelDownload(variant)
    }

    fun deleteModel(variant: AssistantModel) {
        repository.deleteModel(variant)
    }

    private fun isDownloading(variant: AssistantModel): Boolean = installations.any {
        it.variant == variant && it.phase in setOf(
            AssistantModels.Phase.Downloading,
            AssistantModels.Phase.Verifying
        )
    }

    fun canContinueGallery(message: AssistantMessage): Boolean =
        repository.canContinueGallery(message)

    fun chooseGalleryPerson(message: AssistantMessage, personId: Long) {
        if (!componentScope.isActive) return
        repository.chooseGalleryPerson(message, personId)
    }

    fun openGalleryPhoto(uri: String, photos: List<AssistantGalleryPhoto>) {
        if (!enabled || photos.none { it.uri == uri }) return
        _galleryPreviewUris.value = photos.map { it.uri }
        _galleryPreviewUri.value = uri
    }

    fun updateGalleryPreviewUri(uri: String?) {
        _galleryPreviewUri.value = uri
    }

    fun openGalleryTool(screen: Screen) {
        if (!enabled) return
        _galleryPreviewUri.value = null
        onNavigate(screen)
    }

    fun openTool(tool: AssistantTool, messageContext: AssistantScreenContext) {
        if (!enabled) return
        val screen = knowledge.resolveTool(tool) ?: return
        componentScope.launch {
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
            onNavigate(destination)
        }
    }

    fun openSetting(setting: AssistantSetting) {
        if (!enabled) return
        val target = SettingsGroup.entries.flatMap { it.settingsList }
            .firstOrNull { it.key() == setting.key } ?: return
        onNavigate(Screen.Settings(targetSetting = target))
    }

    fun openReference(reference: AssistantReference) {
        if (!enabled) return
        onNavigate(Screen.Help(categoryName = reference.categoryKey, tipId = reference.tipId))
    }

    companion object {
        const val MAX_QUERY_LENGTH = AssistantRepository.MAX_QUERY_LENGTH
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
