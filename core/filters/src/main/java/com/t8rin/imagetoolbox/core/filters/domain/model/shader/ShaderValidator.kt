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

package com.t8rin.imagetoolbox.core.filters.domain.model.shader

import javax.inject.Inject

class ShaderValidator @Inject constructor() {
    fun validate(preset: ShaderPreset): Result<ShaderPreset> {
        val errors = validateErrors(preset)

        return if (errors.isEmpty()) {
            Result.success(preset)
        } else {
            Result.failure(ShaderValidationException(errors))
        }
    }

    fun validateErrors(preset: ShaderPreset): List<String> = buildList {
        if (preset.version != SUPPORTED_VERSION) {
            add("Unsupported shader version ${preset.version}. Supported version: $SUPPORTED_VERSION.")
        }
        if (preset.name.isBlank()) {
            add("Shader name must not be blank.")
        }
        if (preset.shader.isBlank()) {
            add("Shader source must not be blank.")
        }

        validateParams(preset.params)

        val shaderSource = preset.shader.withoutComments()
        if (preset.shader.isNotBlank()) {
            val shaderSignature = shaderSource.shaderSignature()
            validateShaderContract(shaderSignature)
            validateSingleInputTexture(shaderSignature.samplerUniforms)
            validateUnsupportedShaderFeatures(shaderSource)
            validateUniforms(shaderSignature.uniformTypes, preset.params)
        }
    }

    private fun MutableList<String>.validateParams(params: List<ShaderParam>) {
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
            validateValueType(param, param.defaultValue, "default")
            param.minValue?.let { validateValueType(param, it, "min") }
            param.maxValue?.let { validateValueType(param, it, "max") }
            validateBounds(param)
        }
    }

    private fun MutableList<String>.validateValueType(
        param: ShaderParam,
        value: ShaderValue,
        fieldName: String
    ) {
        if (value.type != param.type) {
            add(
                "Parameter '${param.name}' has $fieldName value type " +
                        "'${value.type.serialName}', expected '${param.type.serialName}'."
            )
        }
    }

    private fun MutableList<String>.validateBounds(param: ShaderParam) {
        val minValue = param.minValue
        val maxValue = param.maxValue
        if (minValue == null && maxValue == null) return
        if (minValue != null && minValue.type != param.type) return
        if (maxValue != null && maxValue.type != param.type) return

        when (param.type) {
            ShaderParamType.Float -> validateFloatBounds(
                name = param.name,
                defaultValue = (param.defaultValue as? ShaderValue.FloatValue)?.value,
                minValue = (minValue as? ShaderValue.FloatValue)?.value,
                maxValue = (maxValue as? ShaderValue.FloatValue)?.value
            )

            ShaderParamType.Int -> validateIntBounds(
                name = param.name,
                defaultValue = (param.defaultValue as? ShaderValue.IntValue)?.value,
                minValue = (minValue as? ShaderValue.IntValue)?.value,
                maxValue = (maxValue as? ShaderValue.IntValue)?.value
            )

            ShaderParamType.Vec2 -> validateVec2Bounds(
                name = param.name,
                defaultValue = param.defaultValue as? ShaderValue.Vec2Value,
                minValue = minValue as? ShaderValue.Vec2Value,
                maxValue = maxValue as? ShaderValue.Vec2Value
            )

            ShaderParamType.Color -> validateColorBounds(
                name = param.name,
                defaultValue = param.defaultValue as? ShaderValue.ColorValue,
                minValue = minValue as? ShaderValue.ColorValue,
                maxValue = maxValue as? ShaderValue.ColorValue
            )

            ShaderParamType.Bool -> {
                add("Parameter '${param.name}' cannot define min or max for bool values.")
            }
        }
    }

    private fun MutableList<String>.validateFloatBounds(
        name: String,
        defaultValue: Float?,
        minValue: Float?,
        maxValue: Float?
    ) {
        if (minValue != null && maxValue != null && minValue > maxValue) {
            add("Parameter '$name' has min greater than max.")
        }
        if (defaultValue != null && minValue != null && defaultValue < minValue) {
            add("Parameter '$name' default is lower than min.")
        }
        if (defaultValue != null && maxValue != null && defaultValue > maxValue) {
            add("Parameter '$name' default is greater than max.")
        }
    }

    private fun MutableList<String>.validateIntBounds(
        name: String,
        defaultValue: Int?,
        minValue: Int?,
        maxValue: Int?
    ) {
        if (minValue != null && maxValue != null && minValue > maxValue) {
            add("Parameter '$name' has min greater than max.")
        }
        if (defaultValue != null && minValue != null && defaultValue < minValue) {
            add("Parameter '$name' default is lower than min.")
        }
        if (defaultValue != null && maxValue != null && defaultValue > maxValue) {
            add("Parameter '$name' default is greater than max.")
        }
    }

    private fun MutableList<String>.validateVec2Bounds(
        name: String,
        defaultValue: ShaderValue.Vec2Value?,
        minValue: ShaderValue.Vec2Value?,
        maxValue: ShaderValue.Vec2Value?
    ) {
        validateFloatBounds(
            name = "$name.x",
            defaultValue = defaultValue?.x,
            minValue = minValue?.x,
            maxValue = maxValue?.x
        )
        validateFloatBounds(
            name = "$name.y",
            defaultValue = defaultValue?.y,
            minValue = minValue?.y,
            maxValue = maxValue?.y
        )
    }

    private fun MutableList<String>.validateColorBounds(
        name: String,
        defaultValue: ShaderValue.ColorValue?,
        minValue: ShaderValue.ColorValue?,
        maxValue: ShaderValue.ColorValue?
    ) {
        validateIntBounds(
            name = "$name.red",
            defaultValue = defaultValue?.red,
            minValue = minValue?.red,
            maxValue = maxValue?.red
        )
        validateIntBounds(
            name = "$name.green",
            defaultValue = defaultValue?.green,
            minValue = minValue?.green,
            maxValue = maxValue?.green
        )
        validateIntBounds(
            name = "$name.blue",
            defaultValue = defaultValue?.blue,
            minValue = minValue?.blue,
            maxValue = maxValue?.blue
        )
        validateIntBounds(
            name = "$name.alpha",
            defaultValue = defaultValue?.alpha,
            minValue = minValue?.alpha,
            maxValue = maxValue?.alpha
        )
    }

    private fun MutableList<String>.validateShaderContract(shaderSignature: ShaderSignature) {
        if (shaderSignature.uniformTypes[INPUT_TEXTURE_NAME] != "sampler2D") {
            add("Shader must declare 'uniform sampler2D $INPUT_TEXTURE_NAME;'.")
        }
        if (!shaderSignature.hasTextureCoordinateVarying) {
            add("Shader must declare 'varying vec2 $TEXTURE_COORDINATE_NAME;'.")
        }
        if (!shaderSignature.hasMainFunction) {
            add("Shader must define 'void main()'.")
        }
        if (!shaderSignature.hasFragmentColorOutput) {
            add("Shader must write a color to gl_FragColor.")
        }
    }

    private fun MutableList<String>.validateUnsupportedShaderFeatures(shaderSource: String) {
        if (shaderSource.hasIdentifier("mainImage")) {
            add("ShaderToy mainImage shaders are not supported. Use GPUImage's void main() contract.")
        }
        if (shaderSource.hasIdentifier("iTime")) {
            add("Animated ShaderToy uniform 'iTime' is not supported.")
        }
        if (shaderSource.hasIdentifier("iFrame")) {
            add("Animated ShaderToy uniform 'iFrame' is not supported.")
        }
        if (shaderSource.hasIdentifier("iResolution")) {
            add("ShaderToy uniform 'iResolution' is not supported.")
        }
        if (shaderSource.hasIdentifierWithPrefix("iChannel")) {
            add("ShaderToy iChannel textures are not supported.")
        }
        if (shaderSource.hasIdentifier("samplerExternalOES")) {
            add("External textures are not supported.")
        }
        if (shaderSource.hasIdentifier("GL_OES_EGL_image_external")) {
            add("External texture extensions are not supported.")
        }
        if (shaderSource.lineSequence().any { it.trimStart().startsWith("#pragma parameter") }) {
            add("Libretro shader parameters are not supported.")
        }
    }

    private fun MutableList<String>.validateSingleInputTexture(
        samplerUniforms: List<Pair<String, String>>
    ) {
        samplerUniforms
            .filterNot { (_, samplerName) -> samplerName == INPUT_TEXTURE_NAME }
            .forEach { (samplerType, samplerName) ->
                add(
                    "Only one input texture is supported. Remove " +
                            "'uniform $samplerType $samplerName;'."
                )
            }
    }

    private fun MutableList<String>.validateUniforms(
        uniformTypes: Map<String, String>,
        params: List<ShaderParam>
    ) {
        params.forEach { param ->
            val uniformType = uniformTypes[param.name]
            when {
                uniformType == null -> add(
                    "Parameter '${param.name}' must be declared as " +
                            "'uniform ${param.type.uniformType} ${param.name};'."
                )

                uniformType != param.type.uniformType -> add(
                    "Parameter '${param.name}' is declared as 'uniform $uniformType ${param.name};', " +
                            "expected 'uniform ${param.type.uniformType} ${param.name};'."
                )
            }
        }
    }

    private fun String.withoutComments(): String = buildString(length) {
        var index = 0
        while (index < this@withoutComments.length) {
            val current = this@withoutComments[index]
            val next = this@withoutComments.getOrNull(index + 1)
            when (current) {
                '/' if next == '/' -> {
                    index += 2
                    while (index < this@withoutComments.length && this@withoutComments[index] != '\n') {
                        index += 1
                    }
                }

                '/' if next == '*' -> {
                    index += 2
                    while (
                        index + 1 < this@withoutComments.length &&
                        !(this@withoutComments[index] == '*' && this@withoutComments[index + 1] == '/')
                    ) {
                        index += 1
                    }
                    index = (index + 2).coerceAtMost(this@withoutComments.length)
                }

                else -> {
                    append(current)
                    index += 1
                }
            }
        }
    }

    private fun String.shaderSignature(): ShaderSignature {
        val uniformTypes = mutableMapOf<String, String>()
        val samplerUniforms = mutableListOf<Pair<String, String>>()
        var hasTextureCoordinateVarying = false

        splitToSequence(';').forEach { statement ->
            val trimmed = statement.trim()
            val uniform = trimmed.readDeclaration("uniform")
            if (uniform != null) {
                val (type, names) = uniform
                names.forEach { name ->
                    uniformTypes[name] = type
                    if (type in SAMPLER_TYPES) {
                        samplerUniforms += type to name
                    }
                }
            }

            val varying = trimmed.readDeclaration("varying")
            if (varying != null) {
                val (type, names) = varying
                if (type == "vec2" && TEXTURE_COORDINATE_NAME in names) {
                    hasTextureCoordinateVarying = true
                }
            }
        }

        return ShaderSignature(
            uniformTypes = uniformTypes,
            samplerUniforms = samplerUniforms,
            hasTextureCoordinateVarying = hasTextureCoordinateVarying,
            hasMainFunction = MAIN_FUNCTION.containsMatchIn(this),
            hasFragmentColorOutput = hasIdentifier("gl_FragColor")
        )
    }

    private fun String.readDeclaration(keyword: String): Pair<String, List<String>>? {
        val tokens = trim().split(WHITESPACE).filter(String::isNotBlank)
        if (tokens.firstOrNull() != keyword) return null

        var index = 1
        if (tokens.getOrNull(index) in PRECISION_QUALIFIERS) {
            index += 1
        }

        val type = tokens.getOrNull(index) ?: return null
        val names = tokens.drop(index + 1)
            .joinToString(" ")
            .split(",")
            .mapNotNull { IDENTIFIER_PREFIX.find(it.trim())?.value }

        return type to names
    }

    private fun String.hasIdentifier(identifier: String): Boolean {
        var index = indexOf(identifier)
        while (index >= 0) {
            val before = getOrNull(index - 1)
            val after = getOrNull(index + identifier.length)
            if (!before.isGlslIdentifierPart() && !after.isGlslIdentifierPart()) {
                return true
            }
            index = indexOf(identifier, startIndex = index + identifier.length)
        }
        return false
    }

    private fun String.hasIdentifierWithPrefix(prefix: String): Boolean {
        var index = indexOf(prefix)
        while (index >= 0) {
            val before = getOrNull(index - 1)
            if (!before.isGlslIdentifierPart()) return true
            index = indexOf(prefix, startIndex = index + prefix.length)
        }
        return false
    }

    private fun Char?.isGlslIdentifierPart(): Boolean =
        this != null && (isLetterOrDigit() || this == '_')

    private data class ShaderSignature(
        val uniformTypes: Map<String, String>,
        val samplerUniforms: List<Pair<String, String>>,
        val hasTextureCoordinateVarying: Boolean,
        val hasMainFunction: Boolean,
        val hasFragmentColorOutput: Boolean
    )

    companion object {
        const val SUPPORTED_VERSION = 1

        val RESERVED_NAMES: Set<String> = setOf(
            "inputImageTexture",
            "textureCoordinate",
            "inputImageSize",
            "outputImageSize"
        )

        private const val INPUT_TEXTURE_NAME = "inputImageTexture"
        private const val TEXTURE_COORDINATE_NAME = "textureCoordinate"

        private val GLSL_IDENTIFIER = Regex("""[A-Za-z_][A-Za-z0-9_]*""")
        private val PRECISION_QUALIFIERS = setOf("lowp", "mediump", "highp")
        private val SAMPLER_TYPES = setOf("sampler2D", "samplerCube", "samplerExternalOES")
        private val WHITESPACE = Regex("""\s+""")
        private val IDENTIFIER_PREFIX = Regex("""^[A-Za-z_][A-Za-z0-9_]*""")
        private val MAIN_FUNCTION = Regex(
            pattern = """\bvoid\s+main\s*\(\s*\)""",
            option = RegexOption.MULTILINE
        )
    }
}

class ShaderValidationException(
    val errors: List<String>
) : IllegalArgumentException(errors.joinToString(separator = "\n"))
