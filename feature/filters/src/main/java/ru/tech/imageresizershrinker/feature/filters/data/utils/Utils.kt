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

package ru.tech.imageresizershrinker.feature.filters.data.utils

import com.awxkee.aire.EdgeMode
import com.awxkee.aire.TransferFunction
import ru.tech.imageresizershrinker.core.filters.domain.model.BlurEdgeMode
import ru.tech.imageresizershrinker.core.filters.domain.model.TransferFunc

fun BlurEdgeMode.toEdgeMode(): EdgeMode = when (this) {
    BlurEdgeMode.Clamp -> EdgeMode.CLAMP
    BlurEdgeMode.Clip -> EdgeMode.KERNEL_CLIP
    BlurEdgeMode.Wrap -> EdgeMode.WRAP
    BlurEdgeMode.Reflect -> EdgeMode.REFLECT
}


fun TransferFunc.toFunc(): TransferFunction = when (this) {
    TransferFunc.SRGB -> TransferFunction.SRGB
    TransferFunc.REC709 -> TransferFunction.REC709
    TransferFunc.GAMMA2P2 -> TransferFunction.GAMMA2P2
    TransferFunc.GAMMA2P8 -> TransferFunction.GAMMA2P8
}
