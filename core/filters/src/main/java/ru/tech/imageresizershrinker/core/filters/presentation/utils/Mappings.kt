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

package ru.tech.imageresizershrinker.core.filters.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.filters.domain.model.BlurEdgeMode
import ru.tech.imageresizershrinker.core.filters.domain.model.FadeSide
import ru.tech.imageresizershrinker.core.filters.domain.model.PaletteTransferSpace
import ru.tech.imageresizershrinker.core.filters.domain.model.PopArtBlendingMode
import ru.tech.imageresizershrinker.core.filters.domain.model.TransferFunc
import ru.tech.imageresizershrinker.core.resources.R

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