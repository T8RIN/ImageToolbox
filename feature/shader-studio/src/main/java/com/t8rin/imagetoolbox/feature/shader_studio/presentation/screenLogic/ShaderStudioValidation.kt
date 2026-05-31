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

import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParam
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValidator
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.glslUnsafeCharacters

internal fun ShaderDraftSnapshot.validateDraftErrors(): List<String> =
    buildList {
        if (shaderSource.isBlank()) {
            add("Shader source must not be blank.")
        }
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
        if (param.name in ShaderValidator.RESERVED_NAMES) {
            add("Parameter '${param.name}' uses a reserved GPUImage name.")
        }
        if (param.name.isNotBlank() && !GLSL_IDENTIFIER.matches(param.name)) {
            add("Parameter '${param.name}' must be a valid GLSL identifier.")
        }
    }
}

private fun MutableList<String>.validateDraftBody(shaderSource: String) {
    if (shaderSource.isBlank()) return

    val unsafeCharacters = shaderSource.glslUnsafeCharacters()
    if (unsafeCharacters.isNotEmpty()) {
        add(
            "Shader source contains unsupported characters: " +
                    unsafeCharacters.joinToString { it.readableName() } +
                    ". Use ASCII GLSL source only."
        )
    }

    if (!shaderSource.contains("gl_FragColor")) {
        add("Shader must write a color to gl_FragColor.")
    }
}

private fun Char.readableName(): String =
    if (isISOControl()) {
        "U+${code.toString(16).uppercase().padStart(4, '0')}"
    } else {
        "'$this'"
    }

private val GLSL_IDENTIFIER = Regex("""[A-Za-z_][A-Za-z0-9_]*""")
