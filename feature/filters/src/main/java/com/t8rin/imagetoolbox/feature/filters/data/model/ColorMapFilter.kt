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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.ColorMapType
import com.t8rin.imagetoolbox.feature.filters.data.transformation.ColorMapTransformation

internal class AutumnFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.AUTUMN), Filter.Autumn

internal class BoneFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.BONE), Filter.Bone

internal class JetFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.JET), Filter.Jet

internal class WinterFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.WINTER), Filter.Winter

internal class RainbowFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.RAINBOW), Filter.Rainbow

internal class OceanFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.OCEAN), Filter.Ocean

internal class SummerFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.SUMMER), Filter.Summer

internal class SpringFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.SPRING), Filter.Spring

internal class CoolVariantFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.COOL), Filter.CoolVariant

internal class HsvFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.HSV), Filter.Hsv

internal class PinkFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.PINK), Filter.Pink

internal class HotFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.HOT), Filter.Hot

internal class ParulaFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.PARULA), Filter.Parula

internal class MagmaFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.MAGMA), Filter.Magma

internal class InfernoFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.INFERNO), Filter.Inferno

internal class PlasmaFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.PLASMA), Filter.Plasma

internal class ViridisFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.VIRIDIS), Filter.Viridis

internal class CividisFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.CIVIDIS), Filter.Cividis

internal class TwilightFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.TWILIGHT), Filter.Twilight

internal class TwilightShiftedFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.TWILIGHT_SHIFTED), Filter.TwilightShifted

internal class TurboFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.TURBO), Filter.Turbo

internal class DeepGreenFilter(
    override val value: Unit = Unit
) : ColorMapTransformation(ColorMapType.DEEPGREEN), Filter.DeepGreen