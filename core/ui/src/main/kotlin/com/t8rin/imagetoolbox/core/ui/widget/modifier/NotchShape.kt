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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.annotation.IntRange
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp

@Immutable
class NotchShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize
) : CornerBasedShape(
    topStart = topStart,
    topEnd = topEnd,
    bottomEnd = bottomEnd,
    bottomStart = bottomStart
) {

    constructor(size: CornerSize) : this(
        topStart = size,
        topEnd = size,
        bottomEnd = size,
        bottomStart = size
    )

    constructor(size: Dp) : this(size = CornerSize(size))

    constructor(@IntRange(from = 0, to = 100) percent: Int) : this(size = CornerSize(percent))

    constructor(
        topStart: Dp = 0.dp,
        topEnd: Dp = 0.dp,
        bottomEnd: Dp = 0.dp,
        bottomStart: Dp = 0.dp
    ) : this(
        topStart = CornerSize(topStart),
        topEnd = CornerSize(topEnd),
        bottomEnd = CornerSize(bottomEnd),
        bottomStart = CornerSize(bottomStart)
    )

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline {
        if (topStart + topEnd + bottomStart + bottomEnd == 0f) {
            return Outline.Rectangle(size.toRect())
        }

        val topLeft = if (layoutDirection == Ltr) topStart else topEnd
        val topRight = if (layoutDirection == Ltr) topEnd else topStart
        val bottomRight = if (layoutDirection == Ltr) bottomEnd else bottomStart
        val bottomLeft = if (layoutDirection == Ltr) bottomStart else bottomEnd

        return Outline.Generic(
            Path().apply {
                moveTo(topLeft, 0f)
                lineTo(size.width - topRight, 0f)
                lineTo(size.width - topRight, topRight)
                lineTo(size.width, topRight)
                lineTo(size.width, size.height - bottomRight)
                lineTo(size.width - bottomRight, size.height - bottomRight)
                lineTo(size.width - bottomRight, size.height)
                lineTo(bottomLeft, size.height)
                lineTo(bottomLeft, size.height - bottomLeft)
                lineTo(0f, size.height - bottomLeft)
                lineTo(0f, topLeft)
                lineTo(topLeft, topLeft)
                close()
            }
        )
    }

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ): NotchShape {
        return NotchShape(
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NotchShape) return false

        if (topStart != other.topStart) return false
        if (topEnd != other.topEnd) return false
        if (bottomEnd != other.bottomEnd) return false
        if (bottomStart != other.bottomStart) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topStart.hashCode()
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        return result
    }

    override fun toString(): String {
        return "NotchShape(topStart=$topStart, topEnd=$topEnd, bottomEnd=$bottomEnd, " +
                "bottomStart=$bottomStart)"
    }
}