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

package com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import coil3.transform.Transformation
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.SHADER_EXT
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.filters.domain.FilterParamsInteractor
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.ShaderPresetRepository
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ShaderParams
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParam
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParamType
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParseException
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValidationError
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValidator
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiShaderFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.localizedMessage
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.toCoil
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.toImageModel
import dagger.Lazy
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ShaderStudioComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    private val shaderPresetRepository: ShaderPresetRepository,
    private val filterProvider: Lazy<FilterProvider<Bitmap>>,
    private val filterParamsInteractor: Lazy<FilterParamsInteractor>,
    private val fileController: FileController,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    val presets: StateFlow<List<ShaderPreset>> = shaderPresetRepository.getPresets()
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val _name = mutableStateOf("")
    val name: String by _name

    private val _shaderSource = mutableStateOf("")
    val shaderSource: String by _shaderSource

    private val _helperSource = mutableStateOf("")
    val helperSource: String by _helperSource

    private val _params = mutableStateOf(emptyList<ShaderParam>())
    val params: List<ShaderParam> by _params

    private val _validationErrors = mutableStateOf<List<ShaderValidationError>>(
        listOf(ShaderValidationError.BlankSource)
    )
    val validationErrors: List<ShaderValidationError> by _validationErrors

    private val _previewModel: MutableState<ImageModel> = mutableStateOf(
        R.drawable.filter_preview_source.toImageModel()
    )
    val previewModel: ImageModel by _previewModel

    val canSave: Boolean
        get() = validationErrors.isEmpty()

    fun createPreset() {
        _name.update { "" }
        _shaderSource.update { "" }
        _helperSource.update { "" }
        _params.update { emptyList() }
        registerChanges()
        updateBasicValidationErrors()
        cancelImageLoading()
    }

    fun editPreset(preset: ShaderPreset) {
        val draft = preset.toDraftSnapshot()
        _name.update { preset.name }
        _shaderSource.update { draft.shaderSource }
        _helperSource.update { draft.helperSource }
        _params.update { preset.params }
        registerChangesCleared()
        updateBasicValidationErrors()
    }

    fun updateName(value: String) {
        _name.update { value }
        registerChanges()
        updateBasicValidationErrors()
    }

    fun updateShaderSource(value: String) {
        _shaderSource.update { value }
        registerChanges()
        updateBasicValidationErrors()
    }

    fun updateHelperSource(value: String) {
        _helperSource.update { value }
        registerChanges()
        updateBasicValidationErrors()
    }

    fun addParam(type: ShaderParamType = ShaderParamType.Float) {
        _params.update {
            it + ShaderParam(
                name = it.nextParamName(),
                type = type,
                defaultValue = type.defaultValue()
            )
        }
        registerChanges()
        updateBasicValidationErrors()
    }

    fun removeParam(index: Int) {
        _params.update { it.filterIndexed { paramIndex, _ -> paramIndex != index } }
        registerChanges()
        updateBasicValidationErrors()
    }

    fun updateParam(
        index: Int,
        param: ShaderParam
    ) {
        _params.update {
            it.toMutableList().apply {
                if (index in indices) this[index] = param
            }
        }
        registerChanges()
        updateBasicValidationErrors()
    }

    fun savePreset() {
        componentScope.launch {
            val snapshot = buildDraftSnapshot()
            val errors = validateSnapshot(snapshot)

            _validationErrors.update { errors }
            if (errors.isNotEmpty()) return@launch

            val preset = snapshot.toPreset().withUniqueName()

            shaderPresetRepository.savePreset(
                preset = preset,
                replacingName = null
            ).onSuccess {
                _name.update { preset.name }
                registerChangesCleared()
                AppToastHost.showConfetti()
            }.onFailure(::showError)
        }
    }

    fun setFilterPreviewModel(value: String) {
        _previewModel.update { value.toPreviewImageModel() }

        componentScope.launch {
            val interactor = filterParamsInteractor.get()
            interactor.setFilterPreviewModel(value)
            interactor.setCanSetDynamicFilterPreview(false)
        }
    }

    fun importPreset(uri: Uri) {
        componentScope.launch {
            val result = runCatching {
                val json = fileController.readBytes(uri.toString()).decodeToString()
                shaderPresetRepository.importPreset(json).getOrThrow()
            }

            result.onSuccess { preset ->
                editPreset(preset)
                AppToastHost.showConfetti()
            }.onFailure(::showError)
        }
    }

    fun exportPreset(
        preset: ShaderPreset,
        uri: Uri
    ) {
        componentScope.launch {
            fileController.writeBytes(uri.toString()) { writeable ->
                writeable.writeBytes(
                    shaderPresetRepository.exportPreset(preset).encodeToByteArray()
                )
            }.also(::parseFileSaveResult).onSuccess(::registerSave)
        }
    }

    fun sharePreset(preset: ShaderPreset) {
        componentScope.launch {
            shareProvider.shareData(
                writeData = { writeable: Writeable ->
                    writeable.writeBytes(
                        shaderPresetRepository.exportPreset(preset).encodeToByteArray()
                    )
                },
                filename = "${preset.name.sanitizedFileName()}.$SHADER_EXT",
                onComplete = AppToastHost::showConfetti
            )
        }
    }

    fun deletePreset(preset: ShaderPreset) {
        componentScope.launch {
            shaderPresetRepository.deletePreset(preset)
        }
    }

    fun getPreviewTransformations(): List<Transformation> {
        val snapshot = buildDraftSnapshot()
        val previewPreset = snapshot.toPreset().copy(
            name = snapshot.name
        )

        val haveErrors = validationErrors.any {
            it !is ShaderValidationError.BlankName
        }

        if (haveErrors || snapshot.shaderSource.isBlank()) return emptyList()

        return listOf(
            UiShaderFilter(
                ShaderParams(
                    preset = previewPreset,
                    values = previewPreset.defaultValues
                )
            )
        ).map {
            filterProvider.get().filterToTransformation(it).toCoil()
        }
    }

    private fun buildDraftSnapshot(): ShaderDraftSnapshot =
        ShaderDraftSnapshot(
            name = name,
            shaderSource = shaderSource,
            helperSource = helperSource,
            params = params
        )

    private fun updateBasicValidationErrors() {
        _validationErrors.update {
            validateSnapshot(buildDraftSnapshot())
        }
    }

    private fun validateSnapshot(
        snapshot: ShaderDraftSnapshot
    ): List<ShaderValidationError> {
        val preset = snapshot.toPreset()

        return buildList {
            if (snapshot.shaderSource.isBlank()) {
                add(ShaderValidationError.BlankSource)
            }
            addAll(ShaderValidator.validate(preset))
        }.distinct()
    }

    private fun ShaderPreset.withUniqueName(): ShaderPreset {
        val existingNames = presets.value.mapTo(mutableSetOf()) { it.name }
        if (name !in existingNames) return this

        var index = 1
        var candidate = "$name Copy"
        while (candidate in existingNames) {
            index += 1
            candidate = "$name Copy $index"
        }

        return copy(name = candidate)
    }

    private fun showError(throwable: Throwable) {
        _isImageLoading.update { false }
        if (throwable is ShaderParseException) {
            AppToastHost.showFailureToast(throwable.localizedMessage())
        } else {
            AppToastHost.showFailureToast(throwable)
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit
        ): ShaderStudioComponent
    }

}
