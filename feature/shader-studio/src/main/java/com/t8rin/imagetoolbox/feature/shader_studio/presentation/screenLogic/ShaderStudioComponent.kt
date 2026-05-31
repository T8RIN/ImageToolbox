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
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParser
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiShaderFilter
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
    private val shaderParser: ShaderParser,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    val presets: StateFlow<List<ShaderPreset>> = shaderPresetRepository.getPresets()
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val _editingOriginalName = mutableStateOf<String?>(null)
    val editingOriginalName: String? by _editingOriginalName

    private val _name = mutableStateOf("")
    val name: String by _name

    private val _shaderSource = mutableStateOf("")
    val shaderSource: String by _shaderSource

    private val _params = mutableStateOf(emptyList<ShaderParam>())
    val params: List<ShaderParam> by _params

    private val _validationErrors = mutableStateOf(INITIAL_VALIDATION_ERRORS)
    val validationErrors: List<String> by _validationErrors

    private val _previewModel: MutableState<ImageModel> = mutableStateOf(
        R.drawable.filter_preview_source.toImageModel()
    )
    val previewModel: ImageModel by _previewModel

    val canSave: Boolean
        get() = validationErrors.isEmpty()

    fun createPreset() {
        _editingOriginalName.update { null }
        _name.update { "" }
        _shaderSource.update { "" }
        _params.update { emptyList() }
        registerChanges()
        updateBasicValidationErrors()
        cancelImageLoading()
    }

    fun editPreset(preset: ShaderPreset) {
        _editingOriginalName.update { preset.name }
        _name.update { preset.name }
        _shaderSource.update { "" }
        _params.update { preset.params }
        registerChangesCleared()
        _validationErrors.update { emptyList() }

        componentScope.launch {
            val shaderBody = preset.shader.mainBody()
            _shaderSource.update { shaderBody }
            updateBasicValidationErrors()
        }
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

    fun addParam(type: ShaderParamType = ShaderParamType.Float) {
        _params.update {
            it + ShaderParam(
                name = nextParamName(it),
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
            val errors = snapshot.validateDraftErrors()

            _validationErrors.update { errors }
            if (errors.isNotEmpty()) return@launch

            val preset = snapshot.toPreset()

            shaderPresetRepository.savePreset(
                preset = preset,
                replacingName = editingOriginalName
            ).onSuccess {
                _editingOriginalName.update { preset.name }
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
                filename = "${preset.name.sanitizedFileName()}.itshader",
                onComplete = AppToastHost::showConfetti
            )
        }
    }

    fun deletePreset(preset: ShaderPreset) {
        componentScope.launch {
            shaderPresetRepository.deletePreset(preset)
            if (editingOriginalName == preset.name) {
                createPreset()
                registerChangesCleared()
            }
        }
    }

    fun duplicatePreset(preset: ShaderPreset) {
        componentScope.launch {
            shaderPresetRepository.duplicatePreset(preset)
                .onSuccess(::editPreset)
                .onFailure(::showError)
        }
    }

    fun getPreviewTransformations(): List<Transformation> {
        val snapshot = buildDraftSnapshot()
        val previewErrors = snapshot
            .validateDraftErrors()

        if (previewErrors.isNotEmpty() || snapshot.shaderSource.isBlank()) return emptyList()

        val preset = snapshot.toPreset().copy(
            name = snapshot.name
        )

        return listOf(
            UiShaderFilter(
                ShaderParams(
                    preset = preset,
                    values = preset.defaultValues
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
            params = params
        )

    private fun updateBasicValidationErrors() {
        _validationErrors.update {
            buildDraftSnapshot().validateDraftErrors()
        }
    }

    private fun ShaderDraftSnapshot.validateDraftErrors(): List<String> =
        buildList {
            validateDraftParams(params)
            validateDraftBody(shaderSource)
        }

    private fun MutableList<String>.validateDraftParams(params: List<ShaderParam>) {
        params.groupBy { it.name }
            .filterValues { it.size > 1 }
            .keys
            .forEach { add("Parameter '$it' is declared more than once.") }

        params.forEach { param ->
            if (param.name.isBlank()) {
                add("Parameter names must not be blank.")
            }
            if (param.name in RESERVED_NAMES) {
                add("Parameter '${param.name}' uses a reserved GPUImage name.")
            }
            if (param.name.isNotBlank() && !GLSL_IDENTIFIER.matches(param.name)) {
                add("Parameter '${param.name}' must be a valid GLSL identifier.")
            }
        }
    }

    private fun MutableList<String>.validateDraftBody(shaderSource: String) {
        if (shaderSource.isBlank()) return
        if (!shaderSource.contains("gl_FragColor")) {
            add("Shader must write a color to gl_FragColor.")
        }
    }

    private fun ShaderDraftSnapshot.toPreset(): ShaderPreset =
        ShaderPreset(
            version = SUPPORTED_SHADER_VERSION,
            name = name.trim(),
            params = params,
            shader = shaderSource.toFragmentShader(params)
        )

    private fun showError(throwable: Throwable) {
        _isImageLoading.update { false }
        _validationErrors.update { throwable.message?.lines().orEmpty() }
        AppToastHost.showFailureToast(throwable)
    }

    private fun nextParamName(params: List<ShaderParam>): String {
        val names = params.mapTo(mutableSetOf()) { it.name }
        var index = params.size + 1
        var candidate = "uParam$index"
        while (candidate in names) {
            index += 1
            candidate = "uParam$index"
        }
        return candidate
    }

    private fun ShaderParamType.defaultValue(): ShaderValue = when (this) {
        ShaderParamType.Float -> ShaderValue.FloatValue(0f)
        ShaderParamType.Int -> ShaderValue.IntValue(0)
        ShaderParamType.Bool -> ShaderValue.BoolValue(false)
        ShaderParamType.Color -> ShaderValue.ColorValue(255, 255, 255, 255)
        ShaderParamType.Vec2 -> ShaderValue.Vec2Value(0f, 0f)
    }

    private fun String.toFragmentShader(params: List<ShaderParam>): String = buildString {
        appendLine("precision mediump float;")
        appendLine("varying highp vec2 textureCoordinate;")
        appendLine("uniform sampler2D inputImageTexture;")
        params.forEach { param ->
            appendLine("uniform ${param.type.glslType()} ${param.name};")
        }
        appendLine()
        appendLine("void main() {")
        this@toFragmentShader.lineSequence()
            .map { it.trimEnd() }
            .forEach { line ->
                append("    ")
                appendLine(line)
            }
        append("}")
    }

    private fun String.mainBody(): String {
        val mainStart = MAIN_PATTERN.find(this)?.range?.last?.plus(1) ?: return this
        val bodyStart = indexOf('{', startIndex = mainStart).takeIf { it >= 0 } ?: return this
        var depth = 0

        for (index in bodyStart until length) {
            when (this[index]) {
                '{' -> depth += 1
                '}' -> {
                    depth -= 1
                    if (depth == 0) {
                        return substring(bodyStart + 1, index)
                            .trim('\n', '\r')
                            .lines()
                            .joinToString("\n") { it.removePrefix("    ") }
                    }
                }
            }
        }

        return this
    }

    private fun ShaderParamType.glslType(): String = when (this) {
        ShaderParamType.Float -> "float"
        ShaderParamType.Int -> "int"
        ShaderParamType.Bool -> "bool"
        ShaderParamType.Color -> "vec4"
        ShaderParamType.Vec2 -> "vec2"
    }

    private fun String.sanitizedFileName(): String =
        replace(Regex("""[\\/:*?"<>|]"""), "_").ifBlank { "shader" }

    private fun String.toPreviewImageModel(): ImageModel = when (this) {
        "0" -> R.drawable.filter_preview_source
        "1" -> R.drawable.filter_preview_source_3
        else -> this
    }.toImageModel()

    private data class ShaderDraftSnapshot(
        val name: String,
        val shaderSource: String,
        val params: List<ShaderParam>
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit
        ): ShaderStudioComponent
    }

    private companion object {
        const val SUPPORTED_SHADER_VERSION = 1
        val MAIN_PATTERN = Regex("""void\s+main\s*\(\s*\)""")
        val GLSL_IDENTIFIER = Regex("""[A-Za-z_][A-Za-z0-9_]*""")
        val RESERVED_NAMES = setOf(
            "inputImageTexture",
            "textureCoordinate",
            "inputImageSize",
            "outputImageSize"
        )
        val INITIAL_VALIDATION_ERRORS = listOf(
            "Shader name must not be blank.",
            "Shader source must not be blank."
        )
    }
}
