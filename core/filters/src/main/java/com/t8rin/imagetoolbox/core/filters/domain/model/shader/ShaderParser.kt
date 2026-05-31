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

import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import javax.inject.Inject

class ShaderParser @Inject constructor(
    private val jsonParser: JsonParser
) {
    fun parse(json: String): Result<ShaderPreset> = runCatching {
        val trimmedJson = json.trim()

        if (trimmedJson.isBlank()) {
            throw ShaderParseException("Shader file is empty.")
        }
        if (!trimmedJson.isLikelyShaderPresetJson()) {
            throw ShaderParseException(
                "Shader file must be a .itshader JSON object with version, name, and shader fields."
            )
        }

        val rawPreset = jsonParser.fromJson<ShaderPresetJson>(
            json = trimmedJson,
            type = ShaderPresetJson::class.java
        ) ?: throw ShaderParseException(
            "Shader file is not valid JSON or does not match the .itshader format."
        )

        rawPreset.toShaderPreset()
    }

    private fun ShaderPresetJson.toShaderPreset(): ShaderPreset {
        val errors = mutableListOf<String>()
        val parsedParams = params.orEmpty().mapIndexedNotNull { index, param ->
            param.toShaderParam(index, errors)
        }

        val preset = ShaderPreset(
            version = version ?: 0,
            name = name?.trim().orEmpty(),
            params = parsedParams,
            shader = shader.orEmpty()
        )

        if (version == null) {
            errors += "Field 'version' is required."
        }
        if (name == null) {
            errors += "Field 'name' is required."
        }
        if (shader == null) {
            errors += "Field 'shader' is required."
        }

        if (errors.isNotEmpty()) {
            throw ShaderParseException(errors.joinToString(separator = "\n"))
        }

        return preset
    }

    private fun ShaderParamJson.toShaderParam(
        index: Int,
        errors: MutableList<String>
    ): ShaderParam? {
        val startErrorCount = errors.size
        val path = "params[$index]"
        val parsedName = name?.trim().orEmpty()
        val parsedType = type?.trim()
            ?.let(ShaderParamType::fromSerialName)

        if (name == null) {
            errors += "$path.name is required."
        }
        if (type == null) {
            errors += "$path.type is required."
        } else if (parsedType == null) {
            errors += "$path.type '$type' is not supported. Supported types: ${supportedTypes()}."
        }

        val defaultValue = parsedType?.let {
            parseValue(
                value = default,
                type = it,
                path = "$path.default",
                isRequired = true,
                errors = errors
            )
        }
        val minValue = parsedType?.let {
            parseValue(
                value = min,
                type = it,
                path = "$path.min",
                isRequired = false,
                errors = errors
            )
        }
        val maxValue = parsedType?.let {
            parseValue(
                value = max,
                type = it,
                path = "$path.max",
                isRequired = false,
                errors = errors
            )
        }

        return if (
            errors.size == startErrorCount &&
            parsedType != null &&
            defaultValue != null
        ) {
            ShaderParam(
                name = parsedName,
                type = parsedType,
                defaultValue = defaultValue,
                minValue = minValue,
                maxValue = maxValue
            )
        } else {
            null
        }
    }

    private fun parseValue(
        value: Any?,
        type: ShaderParamType,
        path: String,
        isRequired: Boolean,
        errors: MutableList<String>
    ): ShaderValue? {
        if (value == null) {
            if (isRequired) {
                errors += "$path is required for '${type.serialName}' parameters."
            }
            return null
        }

        return when (type) {
            ShaderParamType.Float -> parseFloat(value, path, errors)
                ?.let(ShaderValue::FloatValue)

            ShaderParamType.Int -> parseInt(value, path, errors)
                ?.let(ShaderValue::IntValue)

            ShaderParamType.Bool -> parseBool(value, path, errors)
                ?.let(ShaderValue::BoolValue)

            ShaderParamType.Color -> parseColor(value, path, errors)
            ShaderParamType.Vec2 -> parseVec2(value, path, errors)
        }
    }

    private fun parseFloat(
        value: Any?,
        path: String,
        errors: MutableList<String>
    ): Float? {
        val parsed = (value as? Number)?.toFloat()
        return if (parsed != null && parsed.isFinite()) {
            parsed
        } else {
            errors += "$path must be a finite number."
            null
        }
    }

    private fun parseInt(
        value: Any?,
        path: String,
        errors: MutableList<String>
    ): Int? {
        val number = value as? Number
        val parsed = number?.toDouble()

        return if (
            parsed != null &&
            parsed.isFinite() &&
            parsed % 1.0 == 0.0 &&
            parsed in Int.MIN_VALUE.toDouble()..Int.MAX_VALUE.toDouble()
        ) {
            parsed.toInt()
        } else {
            errors += "$path must be an integer."
            null
        }
    }

    private fun parseBool(
        value: Any?,
        path: String,
        errors: MutableList<String>
    ): Boolean? = (value as? Boolean) ?: run {
        errors += "$path must be true or false."
        null
    }

    private fun parseColor(
        value: Any,
        path: String,
        errors: MutableList<String>
    ): ShaderValue.ColorValue? = when (value) {
        is String -> parseColorString(value, path, errors)
        is List<*> -> parseColorList(value, path, errors)
        is Map<*, *> -> parseColorMap(value, path, errors)
        else -> {
            errors += "$path must be a color string, RGB/RGBA array, or color object."
            null
        }
    }

    private fun parseColorString(
        value: String,
        path: String,
        errors: MutableList<String>
    ): ShaderValue.ColorValue? {
        val hex = value.removePrefix("#")
        if (hex.length != RGB_HEX_LENGTH && hex.length != RGBA_HEX_LENGTH) {
            errors += "$path must use #RRGGBB or #RRGGBBAA format."
            return null
        }

        return runCatching {
            val red = hex.substring(0, 2).toInt(radix = HEX_RADIX)
            val green = hex.substring(2, 4).toInt(radix = HEX_RADIX)
            val blue = hex.substring(4, 6).toInt(radix = HEX_RADIX)
            val alpha = if (hex.length == RGBA_HEX_LENGTH) {
                hex.substring(6, 8).toInt(radix = HEX_RADIX)
            } else {
                OPAQUE_CHANNEL
            }
            ShaderValue.ColorValue(red, green, blue, alpha)
        }.getOrElse {
            errors += "$path must contain valid hexadecimal color channels."
            null
        }
    }

    private fun parseColorList(
        value: List<*>,
        path: String,
        errors: MutableList<String>
    ): ShaderValue.ColorValue? {
        if (value.size !in RGB_CHANNEL_COUNT..RGBA_CHANNEL_COUNT) {
            errors += "$path must contain 3 or 4 color channels."
            return null
        }

        val channels = value.mapIndexedNotNull { index, channel ->
            parseColorChannel(
                value = channel,
                path = "$path[$index]",
                errors = errors
            )
        }

        return if (channels.size == value.size) {
            ShaderValue.ColorValue(
                red = channels[0],
                green = channels[1],
                blue = channels[2],
                alpha = channels.getOrElse(3) { OPAQUE_CHANNEL }
            )
        } else {
            null
        }
    }

    private fun parseColorMap(
        value: Map<*, *>,
        path: String,
        errors: MutableList<String>
    ): ShaderValue.ColorValue? {
        val red = parseColorChannel(value["r"] ?: value["red"], "$path.r", errors)
        val green = parseColorChannel(value["g"] ?: value["green"], "$path.g", errors)
        val blue = parseColorChannel(value["b"] ?: value["blue"], "$path.b", errors)
        val alpha = (value["a"] ?: value["alpha"])?.let {
            parseColorChannel(it, "$path.a", errors)
        } ?: OPAQUE_CHANNEL

        return if (red != null && green != null && blue != null) {
            ShaderValue.ColorValue(red, green, blue, alpha)
        } else {
            null
        }
    }

    private fun parseColorChannel(
        value: Any?,
        path: String,
        errors: MutableList<String>
    ): Int? {
        val parsed = parseInt(value, path, errors)
        return if (parsed != null && parsed in CHANNEL_RANGE) {
            parsed
        } else {
            if (parsed != null) {
                errors += "$path must be between 0 and 255."
            }
            null
        }
    }

    private fun parseVec2(
        value: Any,
        path: String,
        errors: MutableList<String>
    ): ShaderValue.Vec2Value? = when (value) {
        is List<*> -> parseVec2List(value, path, errors)
        is Map<*, *> -> parseVec2Map(value, path, errors)
        else -> {
            errors += "$path must be a two-number array or an object with x and y."
            null
        }
    }

    private fun parseVec2List(
        value: List<*>,
        path: String,
        errors: MutableList<String>
    ): ShaderValue.Vec2Value? {
        if (value.size != VEC2_COMPONENT_COUNT) {
            errors += "$path must contain exactly 2 numbers."
            return null
        }

        val x = parseFloat(value[0], "$path[0]", errors)
        val y = parseFloat(value[1], "$path[1]", errors)

        return if (x != null && y != null) {
            ShaderValue.Vec2Value(x, y)
        } else {
            null
        }
    }

    private fun parseVec2Map(
        value: Map<*, *>,
        path: String,
        errors: MutableList<String>
    ): ShaderValue.Vec2Value? {
        val x = parseFloat(value["x"], "$path.x", errors)
        val y = parseFloat(value["y"], "$path.y", errors)

        return if (x != null && y != null) {
            ShaderValue.Vec2Value(x, y)
        } else {
            null
        }
    }

    private fun supportedTypes(): String =
        ShaderParamType.entries.joinToString { it.serialName }

    private fun String.isLikelyShaderPresetJson(): Boolean =
        startsWith("{") &&
                endsWith("}") &&
                REQUIRED_FIELDS.all { field -> contains("\"$field\"") }

    private companion object {
        const val HEX_RADIX = 16
        const val RGB_HEX_LENGTH = 6
        const val RGBA_HEX_LENGTH = 8
        const val RGB_CHANNEL_COUNT = 3
        const val RGBA_CHANNEL_COUNT = 4
        const val VEC2_COMPONENT_COUNT = 2
        const val OPAQUE_CHANNEL = 255

        val CHANNEL_RANGE = 0..255
        val REQUIRED_FIELDS = listOf("version", "name", "shader")
    }
}

class ShaderParseException(
    message: String
) : IllegalArgumentException(message)

private data class ShaderPresetJson(
    val version: Int? = null,
    val name: String? = null,
    val params: List<ShaderParamJson>? = null,
    val shader: String? = null
)

private data class ShaderParamJson(
    val name: String? = null,
    val type: String? = null,
    val default: Any? = null,
    val min: Any? = null,
    val max: Any? = null
)
