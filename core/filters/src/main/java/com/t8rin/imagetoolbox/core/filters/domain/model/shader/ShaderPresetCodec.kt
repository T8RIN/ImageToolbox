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

fun ShaderPreset.toItShaderJson(): String = buildString {
    appendLine("{")
    appendLine("  \"version\": $version,")
    appendLine("  \"name\": \"${name.escapeJson()}\",")
    appendLine("  \"params\": [")
    params.forEachIndexed { index, param ->
        appendLine("    {")
        appendLine("      \"name\": \"${param.name.escapeJson()}\",")
        appendLine("      \"type\": \"${param.type.serialName}\",")
        appendLine("      \"default\": ${param.defaultValue.toJsonValue()}${if (param.minValue != null || param.maxValue != null) "," else ""}")
        param.minValue?.let {
            appendLine("      \"min\": ${it.toJsonValue()}${if (param.maxValue != null) "," else ""}")
        }
        param.maxValue?.let {
            appendLine("      \"max\": ${it.toJsonValue()}")
        }
        append("    }")
        if (index != params.lastIndex) append(",")
        appendLine()
    }
    appendLine("  ],")
    appendLine("  \"shader\": \"${shader.escapeJson()}\"")
    append("}")
}

private fun ShaderValue.toJsonValue(): String = when (this) {
    is ShaderValue.FloatValue -> value.toString()
    is ShaderValue.IntValue -> value.toString()
    is ShaderValue.BoolValue -> value.toString()
    is ShaderValue.ColorValue -> "\"#${red.hex()}${green.hex()}${blue.hex()}${alpha.hex()}\""
    is ShaderValue.Vec2Value -> "[$x, $y]"
}

private fun String.escapeJson(): String = buildString {
    this@escapeJson.forEach { char ->
        when (char) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\b' -> append("\\b")
            '\u000C' -> append("\\f")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> {
                if (char.code < CONTROL_CHAR_LIMIT) {
                    append("\\u")
                    append(char.code.toString(HEX_RADIX).padStart(UNICODE_ESCAPE_LENGTH, '0'))
                } else {
                    append(char)
                }
            }
        }
    }
}

private fun Int.hex(): String = coerceIn(COLOR_MIN, COLOR_MAX)
    .toString(HEX_RADIX)
    .padStart(HEX_CHANNEL_LENGTH, '0')

private const val COLOR_MIN = 0
private const val COLOR_MAX = 255
private const val HEX_RADIX = 16
private const val HEX_CHANNEL_LENGTH = 2
private const val CONTROL_CHAR_LIMIT = 0x20
private const val UNICODE_ESCAPE_LENGTH = 4
