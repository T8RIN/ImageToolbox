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

data class ShaderPreset(
    val version: Int,
    val name: String,
    val params: List<ShaderParam> = emptyList(),
    val shader: String
) {
    val defaultValues: Map<String, ShaderValue>
        get() = params.associate { it.name to it.defaultValue }
}

data class ShaderParam(
    val name: String,
    val type: ShaderParamType,
    val defaultValue: ShaderValue,
    val minValue: ShaderValue? = null,
    val maxValue: ShaderValue? = null
)

enum class ShaderParamType(
    val serialName: String,
    val uniformType: String
) {
    Float(serialName = "float", uniformType = "float"),
    Int(serialName = "int", uniformType = "int"),
    Bool(serialName = "bool", uniformType = "bool"),
    Color(serialName = "color", uniformType = "vec4"),
    Vec2(serialName = "vec2", uniformType = "vec2");

    companion object {
        fun fromSerialName(value: String): ShaderParamType? =
            entries.firstOrNull { it.serialName == value.lowercase() }
    }
}

sealed interface ShaderValue {
    val type: ShaderParamType

    data class FloatValue(
        val value: Float
    ) : ShaderValue {
        init {
            require(value.isFinite()) { "Float shader values must be finite." }
        }

        override val type: ShaderParamType = ShaderParamType.Float
    }

    data class IntValue(
        val value: Int
    ) : ShaderValue {
        override val type: ShaderParamType = ShaderParamType.Int
    }

    data class BoolValue(
        val value: Boolean
    ) : ShaderValue {
        override val type: ShaderParamType = ShaderParamType.Bool
    }

    data class ColorValue(
        val red: Int,
        val green: Int,
        val blue: Int,
        val alpha: Int = OPAQUE_CHANNEL
    ) : ShaderValue {
        init {
            require(red in CHANNEL_RANGE) { "Red channel must be between 0 and 255." }
            require(green in CHANNEL_RANGE) { "Green channel must be between 0 and 255." }
            require(blue in CHANNEL_RANGE) { "Blue channel must be between 0 and 255." }
            require(alpha in CHANNEL_RANGE) { "Alpha channel must be between 0 and 255." }
        }

        override val type: ShaderParamType = ShaderParamType.Color

        companion object {
            private const val OPAQUE_CHANNEL = 255
            private val CHANNEL_RANGE = 0..255
        }
    }

    data class Vec2Value(
        val x: Float,
        val y: Float
    ) : ShaderValue {
        init {
            require(x.isFinite()) { "Vec2 x value must be finite." }
            require(y.isFinite()) { "Vec2 y value must be finite." }
        }

        override val type: ShaderParamType = ShaderParamType.Vec2
    }
}
