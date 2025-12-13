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

package com.t8rin.imagetoolbox.feature.filters.data.utils.pixelation

internal object PixelationCommands {

    fun enhancedDiamond(value: Float): Array<PixelationLayer> = arrayOf(
        PixelationLayer.Builder(PixelationLayer.Shape.Square)
            .setResolution(value)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Diamond)
            .setResolution(value)
            .setOffset(value / 4)
            .setAlpha(0.5f)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Diamond)
            .setResolution(value)
            .setOffset(value)
            .setAlpha(0.5f)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value / 3)
            .setSize(value / 6)
            .setOffset(value / 12)
            .build()
    )

    fun circle(value: Float): Array<PixelationLayer> = arrayOf(
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setSize(value / 3f)
            .setOffset(value / 2)
            .build()
    )

    fun diamond(value: Float): Array<PixelationLayer> = arrayOf(
        PixelationLayer.Builder(PixelationLayer.Shape.Diamond)
            .setResolution(value)
            .setSize(value + 1)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Diamond)
            .setResolution(value)
            .setOffset(value / 2)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Square)
            .setResolution(value)
            .setAlpha(0.6f)
            .build()
    )

    fun enhancedCircle(value: Float): Array<PixelationLayer> = arrayOf(
        PixelationLayer.Builder(PixelationLayer.Shape.Square)
            .setResolution(value)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setOffset(value / 2)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setSize(value / 1.2f)
            .setOffset(value / 2.5f)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setSize(value / 1.8f)
            .setOffset(value / 3)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setSize(value / 2.7f)
            .setOffset(value / 4)
            .build()
    )

    fun enhancedSquare(value: Float): Array<PixelationLayer> = arrayOf(
        PixelationLayer.Builder(PixelationLayer.Shape.Square)
            .setResolution(value)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Diamond)
            .setResolution(value / 4)
            .setSize(value / 6)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Diamond)
            .setResolution(value / 4)
            .setSize(value / 6)
            .setOffset(value / 8)
            .build()
    )

    fun square(value: Float): Array<PixelationLayer> = arrayOf(
        PixelationLayer.Builder(PixelationLayer.Shape.Square)
            .setResolution(value - 4f)
            .setSize(value)
            .build()
    )

    fun stroke(value: Float): Array<PixelationLayer> = arrayOf(
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setSize(value / 5)
            .setOffset(value / 4)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setSize(value / 4)
            .setOffset(value / 2)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setSize(value / 3)
            .setOffset(value / 1.3f)
            .build(),
        PixelationLayer.Builder(PixelationLayer.Shape.Circle)
            .setResolution(value)
            .setSize(value / 4)
            .setOffset(0f)
            .build()
    )

}