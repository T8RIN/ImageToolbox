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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import kotlin.reflect.KProperty1

internal class StructuredParametersBuilder<T : Filter.ParameterValue> {
    private val fields = mutableMapOf<KProperty1<in T, *>, FilterField<*>>()

    private fun <V> bind(property: KProperty1<in T, V>, field: FilterField<V>) {
        require(property !in fields) { "Duplicate filter parameter ${property.name}" }
        fields[property] = field
    }

    fun KProperty1<in T, Float>.float(
        @StringRes title: Int? = null,
        default: Float,
        range: ClosedFloatingPointRange<Float>,
        roundTo: Int = 2
    ) = bind(this, floatParam(title, default, range, roundTo))

    fun KProperty1<in T, Int>.int(
        @StringRes title: Int,
        default: Int,
        range: IntRange,
        roundTo: Int = 0
    ) = bind(this, intParam(title, default, range, roundTo))

    fun KProperty1<in T, Float>.seed(
        @StringRes title: Int,
        default: Float,
        range: ClosedFloatingPointRange<Float>,
        roundTo: Int = 2
    ) = bind(this, seedParam(title, default, range, roundTo))

    fun KProperty1<in T, Int>.seed(
        @StringRes title: Int,
        default: Int,
        range: IntRange
    ) = bind(this, seedParam(title, default, range))

    fun KProperty1<in T, Int>.choice(
        @StringRes title: Int,
        default: Int,
        @StringRes vararg options: Int
    ) = bind(this, choiceParam(title, default, *options))

    fun KProperty1<in T, Boolean>.boolean(@StringRes title: Int?, default: Boolean) =
        bind(this, booleanParam(title, default))

    fun KProperty1<in T, Boolean>.toggle(
        @StringRes title: Int,
        default: Boolean,
        @StringRes off: Int,
        @StringRes on: Int
    ) = bind(this, toggleParam(title, default, off, on))

    fun KProperty1<in T, ColorModel>.color(
        @StringRes title: Int,
        default: ColorModel,
        allowAlpha: Boolean = false
    ) = bind(this, colorParam(title, default, allowAlpha))

    fun KProperty1<in T, GmicFilterParams>.gmic(vararg fields: GmicField) =
        bind(this, gmicParam(*fields))

    fun KProperty1<in T, String>.text(@StringRes title: Int, default: () -> String) =
        bind(this, stringParam(title, default))

    fun KProperty1<in T, FloatArray>.array(columns: Int, default: () -> FloatArray) =
        bind(this, floatArrayParam(columns, default))

    inline fun <reified V : Enum<V>> KProperty1<in T, V>.enum(
        @StringRes title: Int? = null,
        noinline label: (V) -> Int,
        noinline default: () -> V
    ) = parameter(enumParam(title, label, default))

    fun KProperty1<in T, GradientPalette>.palette(default: () -> GradientPalette) =
        bind(this, paletteParam(default))

    fun KProperty1<in T, ImageModel>.image(@StringRes title: Int, default: () -> ImageModel) =
        bind(this, imageParam(title, default))

    fun KProperty1<in T, FileModel>.file(@StringRes title: Int, default: () -> FileModel) =
        bind(this, fileParam(title, default))

    fun KProperty1<in T, FontType?>.font(@StringRes title: Int, default: () -> FontType?) =
        bind(this, fontParam(title, default))

    fun KProperty1<in T, OffsetModel>.point(
        vararg controls: FilterParam,
        default: () -> OffsetModel
    ) = bind(this, pointParam(default, *controls))

    fun KProperty1<in T, List<List<Float>>>.curves(default: () -> List<List<Float>>) =
        bind(this, curvesParam(default))

    val <V> KProperty1<in T, V>.parameter: (FilterField<V>) -> Unit
        get() = { bind(this, it) }

    val <V> KProperty1<in T, V>.editor: FilterParameterEditor<V>
        get() = FilterParameterEditor(parameter)

    fun build(): StructuredParameters<T> = StructuredParameters(fields.toMap())
}
