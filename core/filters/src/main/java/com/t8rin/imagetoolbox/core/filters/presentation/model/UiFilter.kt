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
import androidx.compose.material.icons.Icons
import com.t8rin.imagetoolbox.core.resources.icons.FilterHdr
import com.t8rin.imagetoolbox.core.resources.icons.Lightbulb
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.filterIsNotInstance
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.blurGroupFilters
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.colorGroupFilters
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.copyUiFilterInstance
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.distortionGroupFilters
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.ditheringGroupFilters
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.effectsGroupFilters
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.lightGroupFilters
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.lutGroupFilters
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.mapFilterToUiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.newUiFilterInstance
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.pixelationGroupFilters
import com.t8rin.imagetoolbox.core.filters.presentation.model.generated.simpleGroupFilters
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Animation
import com.t8rin.imagetoolbox.core.resources.icons.BlurCircular
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.Cube
import com.t8rin.imagetoolbox.core.resources.icons.Extension
import com.t8rin.imagetoolbox.core.resources.icons.FloodFill
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.resources.icons.Speed
import com.t8rin.imagetoolbox.core.resources.icons.TableEye
import com.t8rin.imagetoolbox.core.utils.appContext

sealed class UiFilter<T : Any>(
    @StringRes val title: Int,
    val paramsInfo: List<FilterParam> = listOf(),
    override val value: T,
) : Filter<T> {

    override var isVisible: Boolean by mutableStateOf(true)

    constructor(
        @StringRes title: Int,
        valueRange: ClosedFloatingPointRange<Float>,
        value: T,
    ) : this(
        title = title,
        paramsInfo = listOf(
            FilterParam(valueRange = valueRange)
        ),
        value = value
    )

    fun <T : Any> copy(
        value: T
    ): UiFilter<*> = copyUiFilterInstance(
        filter = this,
        newValue = value
    )

    fun newInstance(): UiFilter<*> = newUiFilterInstance(this)

    sealed class Group(
        val icon: ImageVector,
        val title: Int,
        data: List<UiFilter<*>>
    ) {
        operator fun component1() = icon
        operator fun component2() = title

        internal val filters: List<UiFilter<*>> by lazy {
            data.sortedBy { appContext.getString(it.title) }
        }

        private val filtersForTemplateCreation: List<UiFilter<*>> by lazy {
            filters.filterIsNotInstance(
                Filter.PaletteTransfer::class,
                Filter.LUT512x512::class,
                Filter.PaletteTransferVariant::class,
                Filter.CubeLut::class,
                Filter.LensCorrection::class
            )
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

        data object Simple : Group(
            icon = Icons.Rounded.Speed,
            title = R.string.simple_effects,
            data = simpleGroupFilters()
        )

        data object Color : Group(
            icon = Icons.Rounded.FloodFill,
            title = R.string.color,
            data = colorGroupFilters()
        )

        data object LUT : Group(
            icon = Icons.Rounded.TableEye,
            title = R.string.lut,
            data = lutGroupFilters()
        )

        data object Light : Group(
            icon = Icons.Rounded.Lightbulb,
            title = R.string.light_aka_illumination,
            data = lightGroupFilters()
        )

        data object Effects : Group(
            icon = Icons.Rounded.FilterHdr,
            title = R.string.effect,
            data = effectsGroupFilters()
        )

        data object Blur : Group(
            icon = Icons.Rounded.BlurCircular,
            title = R.string.blur,
            data = blurGroupFilters()
        )

        data object Pixelation : Group(
            icon = Icons.Rounded.Cube,
            title = R.string.pixelation,
            data = pixelationGroupFilters()
        )

        data object Distortion : Group(
            icon = Icons.Rounded.Animation,
            title = R.string.distortion,
            data = distortionGroupFilters()
        )

        data object Dithering : Group(
            icon = Icons.Rounded.Gradient,
            title = R.string.dithering,
            data = ditheringGroupFilters()
        )
    }

    companion object {
        val groups: List<Group> by lazy {
            listOf(
                Group.Simple,
                Group.Color,
                Group.LUT,
                Group.Light,
                Group.Effects,
                Group.Blur,
                Group.Pixelation,
                Group.Distortion,
                Group.Dithering
            )
        }

        val count: Int by lazy {
            groups.sumOf { it.filters.size }
        }
    }

}

fun Filter<*>.toUiFilter(
    preserveVisibility: Boolean = true
): UiFilter<*> = mapFilterToUiFilter(
    filter = this,
    preserveVisibility = preserveVisibility
)


infix fun Int.paramTo(valueRange: ClosedFloatingPointRange<Float>) = FilterParam(
    title = this,
    valueRange = valueRange
)