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

package com.t8rin.imagetoolbox.feature.filters.data.model

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.OffsetModel
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.settings.data.model.toDomainFontFamily
import com.t8rin.imagetoolbox.core.settings.data.model.toFontType
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import kotlin.io.encoding.Base64

internal sealed class FilterField<T>(
    private val value: () -> T,
    val info: FilterParam
) {
    constructor(default: T, info: FilterParam) : this({ default }, info)

    val default: T get() = value()

    open val controls: List<FilterParam> get() = listOf(info)

    open val isSerializable: Boolean get() = true

    open val hasEditor: Boolean get() = true

    open fun cacheValue(value: T): Any? = encodeValue(value) ?: value

    open fun encodeValue(value: T): String? = value.toString()

    abstract fun decodeValue(value: String): T

    open class Value<T>(
        default: () -> T,
        info: FilterParam,
        override val controls: List<FilterParam> = listOf(info),
        private val encode: ((T) -> String?)? = null,
        private val decode: ((String) -> T)? = null,
        private val cache: ((T) -> Any?)? = null
    ) : FilterField<T>(default, info) {
        override val isSerializable: Boolean = encode != null && decode != null

        override val hasEditor: Boolean get() = false

        override fun cacheValue(value: T): Any? =
            if (cache != null) cache.invoke(value) else super.cacheValue(value)

        override fun encodeValue(value: T): String? = encode?.invoke(value)

        override fun decodeValue(value: String): T = requireNotNull(decode) {
            "This parameter cannot be restored from a template"
        }(value)
    }

    class Array(
        default: () -> FloatArray,
        val columns: Int
    ) : Value<FloatArray>(
        default,
        control(valueRange = 0f..0f),
        encode = { it.joinToString("*") },
        decode = { value ->
            if (value.isEmpty()) floatArrayOf() else value.split('*').map(String::toFiniteFloat)
                .toFloatArray()
        }
    ) {
        override val hasEditor: Boolean get() = true

        init {
            require(columns > 0)
        }
    }

    class Number(default: Float, info: FilterParam) : FilterField<Float>(default, info) {
        init {
            require(info.valueRange.start.isFinite() && info.valueRange.endInclusive.isFinite())
            require(default.isFinite() && default in info.valueRange)
        }

        override fun decodeValue(value: String): Float = value.toFiniteFloat()

        fun constrain(value: Float): Float =
            if (value.isFinite()) value.coerceIn(info.valueRange) else default
    }

    class Integer(default: Int, info: FilterParam) : FilterField<Int>(default, info) {
        init {
            require(default.toFloat() in info.valueRange)
        }

        override fun decodeValue(value: String): Int = value.toInt()

        fun constrain(value: Int): Int =
            value.coerceIn(info.valueRange.start.toInt(), info.valueRange.endInclusive.toInt())
    }

    class Toggle(default: Boolean, info: FilterParam) : FilterField<Boolean>(default, info) {
        override fun decodeValue(value: String): Boolean = value.toBooleanStrict()
    }

    class Color(
        default: ColorModel,
        info: FilterParam,
        val allowAlpha: Boolean
    ) : FilterField<ColorModel>(default, info) {
        override fun encodeValue(value: ColorModel): String = value.colorInt.toString()

        override fun decodeValue(value: String): ColorModel = ColorModel(value.toInt())
    }

    class Text(default: () -> String, info: FilterParam) : Value<String>(
        default, info,
        encode = { Base64.encode(it.encodeToByteArray()) },
        decode = { Base64.decode(it).decodeToString() }
    ) {
        override val hasEditor: Boolean get() = true
    }

    class Choice<T : Enum<T>>(
        default: () -> T,
        info: FilterParam,
        val entries: List<T>,
        val label: (T) -> Int
    ) : Value<T>(
        default, info,
        encode = { it.name },
        decode = { name -> entries.single { it.name == name } }
    ) {
        override val hasEditor: Boolean get() = true
    }

    class Palette(default: () -> GradientPalette) : Value<GradientPalette>(
        default, control(valueRange = 0f..0f), emptyList(),
        encode = { Base64.encode(it.toSerializedString().encodeToByteArray()) },
        decode = {
            requireNotNull(GradientPalette.fromSerializedString(Base64.decode(it).decodeToString()))
        }
    ) {
        override val hasEditor: Boolean get() = true
    }

    class Image(default: () -> ImageModel, info: FilterParam) : Value<ImageModel>(default, info) {
        override val hasEditor: Boolean get() = true
    }

    class File(default: () -> FileModel, info: FilterParam) : Value<FileModel>(default, info) {
        override val hasEditor: Boolean get() = true
    }
}

internal fun floatParam(
    @StringRes title: Int? = null,
    default: Float,
    range: ClosedFloatingPointRange<Float>,
    roundTo: Int = 2
): FilterField.Number = FilterField.Number(default, FilterParam(title, range, roundTo))

internal fun intParam(
    @StringRes title: Int,
    default: Int,
    range: IntRange,
    roundTo: Int = 0
): FilterField.Integer = FilterField.Integer(
    default,
    FilterParam(title, range.first.toFloat()..range.last.toFloat(), roundTo)
)

internal fun seedParam(
    @StringRes title: Int,
    default: Int,
    range: IntRange
): FilterField.Integer = FilterField.Integer(
    default,
    FilterParam(title, range.first.toFloat()..range.last.toFloat(), roundTo = 0, isSeed = true)
)

internal fun seedParam(
    @StringRes title: Int,
    default: Float,
    range: ClosedFloatingPointRange<Float>,
    roundTo: Int = 2
): FilterField.Number =
    FilterField.Number(default, FilterParam(title, range, roundTo, isSeed = true))

internal fun choiceParam(
    @StringRes title: Int,
    default: Int,
    @StringRes vararg options: Int
): FilterField.Integer {
    require(default in options.indices)
    return FilterField.Integer(
        default,
        FilterParam(title, 0f..options.lastIndex.toFloat(), roundTo = 0, options = options.toList())
    )
}

internal fun toggleParam(
    @StringRes title: Int,
    default: Boolean,
    @StringRes off: Int,
    @StringRes on: Int
): FilterField.Toggle = FilterField.Toggle(
    default,
    FilterParam(title, 0f..1f, roundTo = 0, options = listOf(off, on))
)

internal fun colorParam(
    @StringRes title: Int,
    default: ColorModel,
    allowAlpha: Boolean = false
): FilterField.Color = FilterField.Color(default, FilterParam(title, 0f..0f), allowAlpha)

internal fun booleanParam(@StringRes title: Int?, default: Boolean): FilterField.Toggle =
    FilterField.Toggle(default, FilterParam(title, 0f..1f, roundTo = 0))

internal fun stringParam(@StringRes title: Int, default: () -> String): FilterField.Text =
    FilterField.Text(default, control(title, 0f..0f))

internal fun floatArrayParam(columns: Int, default: () -> FloatArray): FilterField.Array =
    FilterField.Array(default, columns)

internal inline fun <reified T : Enum<T>> enumParam(
    @StringRes title: Int? = null,
    noinline label: (T) -> Int,
    noinline default: () -> T
): FilterField.Choice<T> = FilterField.Choice(
    default, control(title, 0f..0f), enumValues<T>().toList(), label
)

internal fun paletteParam(default: () -> GradientPalette): FilterField.Palette =
    FilterField.Palette(default)

internal fun pointParam(
    default: () -> OffsetModel,
    vararg controls: FilterParam
): FilterField.Value<OffsetModel> = editorParam(
    default,
    *controls,
    encode = { "${it.x}*${it.y}" },
    decode = {
        val components = it.split('*')
        require(components.size == 2) { "A point must contain exactly two coordinates" }
        OffsetModel(components[0].toFiniteFloat(), components[1].toFiniteFloat())
    }
)

internal fun fontParam(
    @StringRes title: Int,
    default: () -> FontType?
): FilterField.Value<FontType?> = editorParam(
    default,
    control(title, 0f..0f),
    encode = { font ->
        val value =
            if (font is FontType.File) "file:${font.path}" else font.toDomainFontFamily().asString()
        Base64.encode(value.encodeToByteArray())
    },
    decode = {
        val value = Base64.decode(it).decodeToString()
        if (value.startsWith("file:")) FontType.File(value.removePrefix("file:"))
        else DomainFontFamily.fromString(value).toFontType()
    }
)

internal fun curvesParam(
    default: () -> List<List<Float>>
): FilterField.Value<List<List<Float>>> = editorParam(
    default,
    encode = { rows -> rows.joinToString("|") { it.joinToString("*", "[", "]") } },
    decode = { value ->
        if (value.isEmpty()) emptyList() else value.split('|').map { row ->
            require(row.startsWith('[') && row.endsWith(']'))
            row.substring(1, row.lastIndex).let {
                if (it.isEmpty()) emptyList() else it.split('*').map(String::toFiniteFloat)
            }
        }
    }
)

internal fun imageParam(
    @StringRes title: Int,
    default: () -> ImageModel
): FilterField.Image = FilterField.Image(default, control(title, 0f..0f, 0))

internal fun fileParam(
    @StringRes title: Int,
    default: () -> FileModel
): FilterField.File = FilterField.File(default, control(title, 0f..0f, 0))

internal fun <T> editorParam(
    default: () -> T,
    vararg controls: FilterParam,
    encode: ((T) -> String?)? = null,
    decode: ((String) -> T)? = null,
    cache: ((T) -> Any?)? = null
): FilterField.Value<T> = FilterField.Value(
    default,
    controls.firstOrNull() ?: control(valueRange = 0f..0f),
    controls.toList(),
    encode,
    decode,
    cache
)

internal fun control(
    @StringRes title: Int? = null,
    valueRange: ClosedFloatingPointRange<Float>,
    roundTo: Int = 2,
    options: List<Int> = emptyList()
): FilterParam = FilterParam(title, valueRange, roundTo, options)


internal fun String.toFiniteFloat(): Float = toFloat().also {
    require(it.isFinite()) { "A filter parameter must be finite" }
}