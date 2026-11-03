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

import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PaletteTransferSpace
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.resources.R

internal val PopArtBlendingMode.label: Int
    get() = when (this) {
        PopArtBlendingMode.MULTIPLY -> R.string.blend_multiply
        PopArtBlendingMode.COLOR_BURN -> R.string.blend_color_burn
        PopArtBlendingMode.SOFT_LIGHT -> R.string.blend_soft_light
        PopArtBlendingMode.HSL_COLOR -> R.string.blend_hsl_color
        PopArtBlendingMode.HSL_HUE -> R.string.blend_hsl_hue
        PopArtBlendingMode.DIFFERENCE -> R.string.difference
    }

internal val PaletteTransferSpace.label: Int
    get() = when (this) {
        PaletteTransferSpace.LALPHABETA -> R.string.color_space_lalphabeta
        PaletteTransferSpace.LAB -> R.string.color_space_lab
        PaletteTransferSpace.OKLAB -> R.string.color_space_oklab
        PaletteTransferSpace.LUV -> R.string.color_space_luv
    }

internal val TransferFunc.label: Int
    get() = when (this) {
        TransferFunc.SRGB -> R.string.transfer_srgb
        TransferFunc.REC709 -> R.string.transfer_rec709
        TransferFunc.GAMMA2P2 -> R.string.transfer_gamma_2_2
        TransferFunc.GAMMA2P8 -> R.string.transfer_gamma_2_8
    }

internal val BlurEdgeMode.label: Int
    get() = when (this) {
        BlurEdgeMode.Clamp -> R.string.tile_mode_clamp
        BlurEdgeMode.Reflect101 -> R.string.mirror_101
        BlurEdgeMode.Wrap -> R.string.wrap
        BlurEdgeMode.Reflect -> R.string.tile_mode_mirror
    }

internal val FadeSide.label: Int
    get() = when (this) {
        FadeSide.Start -> R.string.start
        FadeSide.End -> R.string.end
        FadeSide.Top -> R.string.top
        FadeSide.Bottom -> R.string.bottom
    }

internal val MirrorSide.label: Int
    get() = when (this) {
        MirrorSide.LeftToRight -> R.string.left_to_right
        MirrorSide.RightToLeft -> R.string.right_to_left
        MirrorSide.TopToBottom -> R.string.top_to_bottom
        MirrorSide.BottomToTop -> R.string.bottom_to_top
    }

internal val PolarCoordinatesType.label: Int
    get() = when (this) {
        PolarCoordinatesType.RECT_TO_POLAR -> R.string.rect_to_polar
        PolarCoordinatesType.POLAR_TO_RECT -> R.string.polar_to_rect
        PolarCoordinatesType.INVERT_IN_CIRCLE -> R.string.invert_in_circle
    }

internal val SpotHealMode.label: Int
    get() = when (this) {
        SpotHealMode.OpenCV -> R.string.opencv
        SpotHealMode.LaMa -> R.string.lama
    }