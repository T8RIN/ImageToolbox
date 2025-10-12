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

package com.t8rin.imagetoolbox.core.ui.utils.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter

private class CenterCropPainter(
    private val wrappedPainter: Painter
) : Painter() {
    override val intrinsicSize: Size
        get() = wrappedPainter.intrinsicSize

    override fun DrawScope.onDraw() {
        val srcW = intrinsicSize.width
        val srcH = intrinsicSize.height
        val dstW = size.width
        val dstH = size.height

        val srcAspect = srcW / srcH
        val dstAspect = dstW / dstH

        val scale = if (srcAspect > dstAspect) {
            dstH / srcH
        } else {
            dstW / srcW
        }

        val scaledW = (srcW * scale)
        val scaledH = (srcH * scale)

        val offsetX = (dstW - scaledW) / 2f
        val offsetY = (dstH - scaledH) / 2f

        clipRect {
            translate(left = offsetX, top = offsetY) {
                with(wrappedPainter) {
                    draw(
                        Size(scaledW, scaledH)
                    )
                }
            }
        }
    }
}

fun Painter.centerCrop(): Painter = CenterCropPainter(this)