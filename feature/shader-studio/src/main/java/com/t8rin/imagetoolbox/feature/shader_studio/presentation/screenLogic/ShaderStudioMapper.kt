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
    ShaderPreset(
        version = SUPPORTED_SHADER_VERSION,
        name = name.trim(),
        params = params,
        shader = shaderSource.toFragmentShader(params)
    )

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

internal fun String.toFragmentShader(params: List<ShaderParam>): String = buildString {
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

internal fun String.mainBody(): String {
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

private const val SUPPORTED_SHADER_VERSION = 1
private val MAIN_PATTERN = Regex("""void\s+main\s*\(\s*\)""")
