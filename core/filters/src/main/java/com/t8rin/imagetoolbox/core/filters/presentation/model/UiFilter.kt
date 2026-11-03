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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.StructuredFilterParameters
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Animation
import com.t8rin.imagetoolbox.core.resources.icons.BlurCircular
import com.t8rin.imagetoolbox.core.resources.icons.Bolt
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.resources.icons.Cube
import com.t8rin.imagetoolbox.core.resources.icons.DrawAbstract
import com.t8rin.imagetoolbox.core.resources.icons.Extension
import com.t8rin.imagetoolbox.core.resources.icons.FilterFrames
import com.t8rin.imagetoolbox.core.resources.icons.FilterHdr
import com.t8rin.imagetoolbox.core.resources.icons.FilterVintage
import com.t8rin.imagetoolbox.core.resources.icons.FloodFill
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.resources.icons.Lightbulb
import com.t8rin.imagetoolbox.core.resources.icons.Schedule
import com.t8rin.imagetoolbox.core.resources.icons.StarShine
import com.t8rin.imagetoolbox.core.resources.icons.TableEye
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlin.reflect.KProperty1

abstract class UiFilter<T : Any> protected constructor(
    val schema: Filter.Schema<T>,
    val serializationKey: String,
    override val value: T
) : Filter<T> {

    val info = requireNotNull(schema.info)

    @get:StringRes
    val title: Int get() = info.title

    @get:StringRes
    val helpDescription: Int get() = info.help

    override var isVisible: Boolean by mutableStateOf(true)

    override var error: String by mutableStateOf("")

    override fun pushError(error: String) {
        this.error = error
    }

    open val singleNumber: Float? get() = null

    open fun withSingleNumber(value: Float): T = this.value

    val canUseTemplate: Boolean get() = schema.parameters?.isSerializable != false

    protected abstract fun recreate(value: Any?): UiFilter<T>

    fun copy(value: Any): UiFilter<T> = recreate(value).also { copied ->
        copied.isVisible = isVisible
        if (this is Filter.RawGmic && copied is Filter.RawGmic) {
            copied.auxiliaryImages = auxiliaryImages
        }
    }

    fun newInstance(): UiFilter<T> = recreate(null).also {
        if (value is Filter.EmptyValue) it.isVisible = isVisible
    }

    sealed class Group(
        val icon: ImageVector,
        val title: Int,
        data: List<UiFilter<*>> = emptyList(),
        group: String? = null
    ) {
        operator fun component1() = icon
        operator fun component2() = title

        internal val filters: List<UiFilter<*>> by lazy {
            val generated = group?.let(filterCatalog::filters).orEmpty()
            (data + generated).sortedBy { appContext.getString(it.title) }
        }

        private val filtersForTemplateCreation: List<UiFilter<*>> by lazy {
            filters.filter { it.canUseTemplate }
        }

        fun filters(canAddTemplates: Boolean) =
            if (canAddTemplates) filters else filtersForTemplateCreation

        data object Template : Group(
            icon = Icons.Rounded.Extension,
            title = R.string.template,
            data = emptyList()
        )

        class Favorite(
            data: List<UiFilter<*>>
        ) : Group(
            icon = Icons.Rounded.Bookmark,
            title = R.string.favorite,
            data = data
        ) {
            override fun toString(): String = "Favorite"
        }

        class Recent(
            data: List<UiFilter<*>>
        ) : Group(
            icon = Icons.Rounded.Schedule,
            title = R.string.recent,
            data = data
        ) {
            override fun toString(): String = "Recent"
        }

        data object Simple : Group(
            icon = Icons.Rounded.Bolt,
            title = R.string.simple_effects,
            group = FilterGroups.SIMPLE
        )

        data object Color : Group(
            icon = Icons.Rounded.FloodFill,
            title = R.string.color,
            group = FilterGroups.COLOR
        )

        data object LUT : Group(
            icon = Icons.Rounded.TableEye,
            title = R.string.lut,
            group = FilterGroups.LUT
        )

        data object Light : Group(
            icon = Icons.Rounded.Lightbulb,
            title = R.string.light_aka_illumination,
            group = FilterGroups.LIGHT
        )

        data object Enhancement : Group(
            icon = Icons.Rounded.StarShine,
            title = R.string.enhancement,
            group = FilterGroups.ENHANCEMENT
        )

        data object Artistic : Group(
            icon = Icons.Rounded.FilterVintage,
            title = R.string.artistic,
            group = FilterGroups.ARTISTIC
        )

        data object Frames : Group(
            icon = Icons.Rounded.FilterFrames,
            title = R.string.frames,
            group = FilterGroups.FRAMES
        )

        data object Effects : Group(
            icon = Icons.Rounded.FilterHdr,
            title = R.string.effect,
            group = FilterGroups.EFFECTS
        )

        data object Blur : Group(
            icon = Icons.Rounded.BlurCircular,
            title = R.string.blur,
            group = FilterGroups.BLUR
        )

        data object Pixelation : Group(
            icon = Icons.Rounded.Cube,
            title = R.string.pixelation,
            group = FilterGroups.PIXELATION
        )

        data object Distortion : Group(
            icon = Icons.Rounded.Animation,
            title = R.string.distortion,
            group = FilterGroups.DISTORTION
        )

        data object Glitch : Group(
            icon = Icons.Rounded.BrokenImageAlt,
            title = R.string.glitch,
            group = FilterGroups.GLITCH
        )

        data object Geometry : Group(
            icon = Icons.Rounded.DrawAbstract,
            title = R.string.gmic_param_geometry,
            group = FilterGroups.GEOMETRY
        )

        data object Dithering : Group(
            icon = Icons.Rounded.Gradient,
            title = R.string.dithering,
            group = FilterGroups.DITHERING
        )
    }

    companion object {
        fun fromSerializationKey(
            key: String,
            value: Any? = null
        ): UiFilter<*>? = restoreCatalogFilter(key, value)

        val groups: List<Group> by lazy {
            listOf(
                Group.Simple,
                Group.Color,
                Group.LUT,
                Group.Light,
                Group.Enhancement,
                Group.Artistic,
                Group.Frames,
                Group.Effects,
                Group.Blur,
                Group.Pixelation,
                Group.Distortion,
                Group.Glitch,
                Group.Geometry,
                Group.Dithering
            )
        }

        val count: Int by lazy {
            groups.sumOf { it.filters.size }
        }
    }

}

@Suppress("UNCHECKED_CAST")
fun <T : Filter.ParameterValue> Filter<T>.parameterInfo(
    property: KProperty1<in T, *>
): FilterParam {
    require(this is UiFilter<*>) { "Parameter controls are only available on UiFilter" }
    val parameters = requireNotNull(schema.parameters as? StructuredFilterParameters<T>) {
        "The filter has no structured parameters"
    }
    return parameters[property]
}

fun Filter<*>.toUiFilter(
    preserveVisibility: Boolean = true
): UiFilter<*> = when (this) {
    is UiFilter<*> -> copy(value)
    else -> requireNotNull(filterCatalog.toUiFilter(this)) {
        "No UiFilter mapping for ${this::class.qualifiedName}"
    }
}.also { uiFilter ->
    uiFilter.isVisible = !preserveVisibility || isVisible
    uiFilter.pushError(error)
    if (this is Filter.RawGmic && uiFilter is Filter.RawGmic) {
        uiFilter.auxiliaryImages = auxiliaryImages
    }
}
