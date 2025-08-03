/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.resources.R

class UiAutumnFilter : UiFilter<Unit>(
    title = R.string.autumn,
    value = Unit
), Filter.Autumn

class UiBoneFilter : UiFilter<Unit>(
    title = R.string.bone,
    value = Unit
), Filter.Bone

class UiJetFilter : UiFilter<Unit>(
    title = R.string.jet,
    value = Unit
), Filter.Jet

class UiWinterFilter : UiFilter<Unit>(
    title = R.string.winter,
    value = Unit
), Filter.Winter

class UiRainbowFilter : UiFilter<Unit>(
    title = R.string.rainbow,
    value = Unit
), Filter.Rainbow

class UiOceanFilter : UiFilter<Unit>(
    title = R.string.ocean,
    value = Unit
), Filter.Ocean

class UiSummerFilter : UiFilter<Unit>(
    title = R.string.summer,
    value = Unit
), Filter.Summer

class UiSpringFilter : UiFilter<Unit>(
    title = R.string.spring,
    value = Unit
), Filter.Spring

class UiCoolVariantFilter : UiFilter<Unit>(
    title = R.string.cool_variant,
    value = Unit
), Filter.CoolVariant

class UiHsvFilter : UiFilter<Unit>(
    title = R.string.hsv,
    value = Unit
), Filter.Hsv

class UiPinkFilter : UiFilter<Unit>(
    title = R.string.pink,
    value = Unit
), Filter.Pink

class UiHotFilter : UiFilter<Unit>(
    title = R.string.hot,
    value = Unit
), Filter.Hot

class UiParulaFilter : UiFilter<Unit>(
    title = R.string.parula,
    value = Unit
), Filter.Parula

class UiMagmaFilter : UiFilter<Unit>(
    title = R.string.magma,
    value = Unit
), Filter.Magma

class UiInfernoFilter : UiFilter<Unit>(
    title = R.string.inferno,
    value = Unit
), Filter.Inferno

class UiPlasmaFilter : UiFilter<Unit>(
    title = R.string.plasma,
    value = Unit
), Filter.Plasma

class UiViridisFilter : UiFilter<Unit>(
    title = R.string.viridis,
    value = Unit
), Filter.Viridis

class UiCividisFilter : UiFilter<Unit>(
    title = R.string.cividis,
    value = Unit
), Filter.Cividis

class UiTwilightFilter : UiFilter<Unit>(
    title = R.string.twilight,
    value = Unit
), Filter.Twilight

class UiTwilightShiftedFilter : UiFilter<Unit>(
    title = R.string.twilight_shifted,
    value = Unit
), Filter.TwilightShifted

class UiTurboFilter : UiFilter<Unit>(
    title = R.string.turbo,
    value = Unit
), Filter.Turbo

class UiDeepGreenFilter : UiFilter<Unit>(
    title = R.string.deep_green,
    value = Unit
), Filter.DeepGreen