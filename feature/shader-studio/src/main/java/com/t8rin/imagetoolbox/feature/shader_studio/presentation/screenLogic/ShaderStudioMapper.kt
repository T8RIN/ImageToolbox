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

import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParam
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParamType
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.toImageModel

internal fun ShaderDraftSnapshot.toPreset(): ShaderPreset =
    params.sanitizeForShaderPreset().let { params ->
        ShaderPreset(
            version = SUPPORTED_SHADER_VERSION,
            name = name.trim(),
            params = params,
            shader = toFragmentShader(params)
        )
    }

internal fun ShaderPreset.toDraftSnapshot(): ShaderDraftSnapshot {
    val parts = shader.toShaderSourceParts(params)
    return ShaderDraftSnapshot(
        name = name,
        shaderSource = parts.mainBody,
        helperSource = parts.helperSource,
        params = params
    )
}

internal fun ShaderParamType.defaultValue(): ShaderValue = when (this) {
    ShaderParamType.Float -> ShaderValue.FloatValue(0f)
    ShaderParamType.Int -> ShaderValue.IntValue(0)
    ShaderParamType.Bool -> ShaderValue.BoolValue(false)
    ShaderParamType.Color -> ShaderValue.ColorValue(255, 255, 255, 255)
    ShaderParamType.Vec2 -> ShaderValue.Vec2Value(0f, 0f)
}

internal fun List<ShaderParam>.nextParamName(): String {
    val names = mapTo(mutableSetOf()) { it.name }
    var index = size + 1
    var candidate = "uParam$index"
    while (candidate in names) {
        index += 1
        candidate = "uParam$index"
    }
    return candidate
}

private fun List<ShaderParam>.sanitizeForShaderPreset(): List<ShaderParam> =
    map { param ->
        if (param.type == ShaderParamType.Bool || param.type == ShaderParamType.Color) {
            param.copy(minValue = null, maxValue = null)
        } else {
            param
        }
    }

private fun ShaderDraftSnapshot.toFragmentShader(params: List<ShaderParam>): String = buildString {
    appendLine("precision mediump float;")
    appendLine("varying highp vec2 textureCoordinate;")
    appendLine("uniform sampler2D inputImageTexture;")
    params.forEach { param ->
        appendLine("uniform ${param.type.glslType()} ${param.name};")
    }
    appendLine()
    helperSource.trim().takeIf(String::isNotEmpty)?.let { helpers ->
        appendLine(helpers)
        appendLine()
    }
    appendLine("void main() {")
    shaderSource.lineSequence()
        .map { it.trimEnd() }
        .forEach { line ->
            append("    ")
            appendLine(line)
        }
    append("}")
}

private fun String.toShaderSourceParts(params: List<ShaderParam>): ShaderSourceParts {
    val mainMatch = MAIN_PATTERN.find(this) ?: return ShaderSourceParts(
        mainBody = this,
        helperSource = ""
    )
    val bodyStart = indexOf('{', startIndex = mainMatch.range.last + 1)
        .takeIf { it >= 0 }
        ?: return ShaderSourceParts(mainBody = this, helperSource = "")
    var depth = 0

    for (index in bodyStart until length) {
        when (this[index]) {
            '{' -> depth += 1
            '}' -> {
                depth -= 1
                if (depth == 0) {
                    val prefix = substring(0, mainMatch.range.first)
                    val body = substring(bodyStart + 1, index)
                    val suffix = substring(index + 1)
                    return ShaderSourceParts(
                        mainBody = body.toEditableMainBody(),
                        helperSource = listOf(prefix, suffix)
                            .joinToString("\n")
                            .withoutStudioWrapper(params)
                    )
                }
            }
        }
    }

    return ShaderSourceParts(mainBody = this, helperSource = "")
}

private fun String.toEditableMainBody(): String =
    trim('\n', '\r')
        .lines()
        .joinToString("\n") { it.removePrefix("    ") }

private fun String.withoutStudioWrapper(params: List<ShaderParam>): String {
    val paramUniforms = params.associate { param ->
        param.name to param.type.glslType()
    }
    return lineSequence()
        .filterNot { line -> line.isStudioWrapperDeclaration(paramUniforms) }
        .joinToString("\n")
        .trim()
}

private fun String.isStudioWrapperDeclaration(paramUniforms: Map<String, String>): Boolean {
    val trimmed = trim()
    if (trimmed.isEmpty()) return false
    if (PRECISION_DECLARATION.matches(trimmed)) return true
    if (TEXTURE_COORDINATE_DECLARATION.matches(trimmed)) return true
    if (INPUT_TEXTURE_DECLARATION.matches(trimmed)) return true

    val uniform = UNIFORM_DECLARATION.matchEntire(trimmed) ?: return false
    val type = uniform.groupValues[1]
    val name = uniform.groupValues[2]
    return paramUniforms[name] == type
}

internal fun String.sanitizedFileName(): String =
    replace(Regex("""[\\/:*?"<>|]"""), "_").ifBlank { "shader" }

internal fun String.toPreviewImageModel(): ImageModel = when (this) {
    "0" -> R.drawable.filter_preview_source
    "1" -> R.drawable.filter_preview_source_3
    else -> this
}.toImageModel()

private fun ShaderParamType.glslType(): String = when (this) {
    ShaderParamType.Float -> "float"
    ShaderParamType.Int -> "int"
    ShaderParamType.Bool -> "bool"
    ShaderParamType.Color -> "vec4"
    ShaderParamType.Vec2 -> "vec2"
}

private data class ShaderSourceParts(
    val mainBody: String,
    val helperSource: String
)

private const val SUPPORTED_SHADER_VERSION = 1
private val MAIN_PATTERN = Regex("""void\s+main\s*\(\s*\)""")
private val PRECISION_DECLARATION = Regex("""precision\s+\w+\s+\w+\s*;""")
private val TEXTURE_COORDINATE_DECLARATION =
    Regex("""varying\s+(?:lowp|mediump|highp)?\s*vec2\s+textureCoordinate\s*;""")
private val INPUT_TEXTURE_DECLARATION =
    Regex("""uniform\s+(?:lowp|mediump|highp)?\s*sampler2D\s+inputImageTexture\s*;""")
private val UNIFORM_DECLARATION =
    Regex("""uniform\s+(?:lowp|mediump|highp)?\s*(\w+)\s+([A-Za-z_][A-Za-z0-9_]*)\s*;""")
