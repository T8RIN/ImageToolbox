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

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiAutumnFilter : UiFilter<Unit>(
    title = R.string.autumn,
    value = Unit
), Filter.Autumn

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiBoneFilter : UiFilter<Unit>(
    title = R.string.bone,
    value = Unit
), Filter.Bone

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiJetFilter : UiFilter<Unit>(
    title = R.string.jet,
    value = Unit
), Filter.Jet

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiWinterFilter : UiFilter<Unit>(
    title = R.string.winter,
    value = Unit
), Filter.Winter

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiRainbowFilter : UiFilter<Unit>(
    title = R.string.rainbow,
    value = Unit
), Filter.Rainbow

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiOceanFilter : UiFilter<Unit>(
    title = R.string.ocean,
    value = Unit
), Filter.Ocean

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiSummerFilter : UiFilter<Unit>(
    title = R.string.summer,
    value = Unit
), Filter.Summer

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiSpringFilter : UiFilter<Unit>(
    title = R.string.spring,
    value = Unit
), Filter.Spring

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiCoolVariantFilter : UiFilter<Unit>(
    title = R.string.cool_variant,
    value = Unit
), Filter.CoolVariant

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiHsvFilter : UiFilter<Unit>(
    title = R.string.hsv,
    value = Unit
), Filter.Hsv

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiPinkFilter : UiFilter<Unit>(
    title = R.string.pink,
    value = Unit
), Filter.Pink

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiHotFilter : UiFilter<Unit>(
    title = R.string.hot,
    value = Unit
), Filter.Hot

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiParulaFilter : UiFilter<Unit>(
    title = R.string.parula,
    value = Unit
), Filter.Parula

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiMagmaFilter : UiFilter<Unit>(
    title = R.string.magma,
    value = Unit
), Filter.Magma

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiInfernoFilter : UiFilter<Unit>(
    title = R.string.inferno,
    value = Unit
), Filter.Inferno

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiPlasmaFilter : UiFilter<Unit>(
    title = R.string.plasma,
    value = Unit
), Filter.Plasma

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiViridisFilter : UiFilter<Unit>(
    title = R.string.viridis,
    value = Unit
), Filter.Viridis

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiCividisFilter : UiFilter<Unit>(
    title = R.string.cividis,
    value = Unit
), Filter.Cividis

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiTwilightFilter : UiFilter<Unit>(
    title = R.string.twilight,
    value = Unit
), Filter.Twilight

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiTwilightShiftedFilter : UiFilter<Unit>(
    title = R.string.twilight_shifted,
    value = Unit
), Filter.TwilightShifted

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiTurboFilter : UiFilter<Unit>(
    title = R.string.turbo,
    value = Unit
), Filter.Turbo

@UiFilterInject(group = UiFilterInject.Groups.SIMPLE)
class UiDeepGreenFilter : UiFilter<Unit>(
    title = R.string.deep_green,
    value = Unit
), Filter.DeepGreen