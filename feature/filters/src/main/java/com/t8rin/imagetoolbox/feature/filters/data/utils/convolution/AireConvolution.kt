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

@file:Suppress("UnusedReceiverParameter")

package com.t8rin.imagetoolbox.feature.filters.data.utils.convolution

import android.graphics.Bitmap
import com.awxkee.aire.Aire
import com.awxkee.aire.EdgeMode
import com.awxkee.aire.KernelShape
import com.awxkee.aire.MorphOpMode
import com.awxkee.aire.Scalar

internal inline fun Aire.convolve2D(
    input: Bitmap,
    kernelProducer: (Int) -> FloatArray,
    size: Int
): Bitmap = Aire.convolve2D(
    bitmap = input,
    kernel = kernelProducer(size),
    kernelShape = KernelShape(size, size),
    edgeMode = EdgeMode.REFLECT_101,
    scalar = Scalar.ZEROS,
    mode = MorphOpMode.RGBA
)