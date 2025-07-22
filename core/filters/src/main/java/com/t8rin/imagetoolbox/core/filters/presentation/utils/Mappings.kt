/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.filters.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PaletteTransferSpace
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.resources.R

internal val PopArtBlendingMode.translatedName: String
    @Composable
    get() = when (this) {
        PopArtBlendingMode.MULTIPLY -> "Multiply"
        PopArtBlendingMode.COLOR_BURN -> "Color Burn"
        PopArtBlendingMode.SOFT_LIGHT -> "Soft Light"
        PopArtBlendingMode.HSL_COLOR -> "HSL Color"
        PopArtBlendingMode.HSL_HUE -> "HSL Hue"
        PopArtBlendingMode.DIFFERENCE -> "Difference"
    }

internal val PaletteTransferSpace.translatedName: String
    @Composable
    get() = when (this) {
        PaletteTransferSpace.LALPHABETA -> "Lαβ"
        PaletteTransferSpace.LAB -> "LAB"
        PaletteTransferSpace.OKLAB -> "OKLAB"
        PaletteTransferSpace.LUV -> "LUV"
    }

internal val TransferFunc.translatedName: String
    @Composable
    get() = when (this) {
        TransferFunc.SRGB -> "sRGB"
        TransferFunc.REC709 -> "Rec.709"
        TransferFunc.GAMMA2P2 -> "${stringResource(R.string.gamma)} 2.2"
        TransferFunc.GAMMA2P8 -> "${stringResource(R.string.gamma)} 2.8"
    }

internal val BlurEdgeMode.translatedName: String
    @Composable
    get() = when (this) {
        BlurEdgeMode.Clamp -> stringResource(R.string.tile_mode_clamp)
        BlurEdgeMode.Reflect101 -> stringResource(R.string.mirror_101)
        BlurEdgeMode.Wrap -> stringResource(R.string.wrap)
        BlurEdgeMode.Reflect -> stringResource(R.string.tile_mode_mirror)
    }

internal val FadeSide.translatedName: String
    @Composable
    get() = when (this) {
        FadeSide.Start -> stringResource(R.string.start)
        FadeSide.End -> stringResource(R.string.end)
        FadeSide.Top -> stringResource(R.string.top)
        FadeSide.Bottom -> stringResource(R.string.bottom)
    }

internal val MirrorSide.translatedName: String
    @Composable
    get() = when (this) {
        MirrorSide.LeftToRight -> stringResource(R.string.left_to_right)
        MirrorSide.RightToLeft -> stringResource(R.string.right_to_left)
        MirrorSide.TopToBottom -> stringResource(R.string.top_to_bottom)
        MirrorSide.BottomToTop -> stringResource(R.string.bottom_to_top)
    }

internal val PolarCoordinatesType.translatedName: String
    @Composable
    get() = when (this) {
        PolarCoordinatesType.RECT_TO_POLAR -> stringResource(R.string.rect_to_polar)
        PolarCoordinatesType.POLAR_TO_RECT -> stringResource(R.string.polar_to_rect)
        PolarCoordinatesType.INVERT_IN_CIRCLE -> stringResource(R.string.invert_in_circle)
    }