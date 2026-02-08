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

package com.t8rin.imagetoolbox.image_cutting.domain

data class CutParams(
    val vertical: PivotPair?,
    val horizontal: PivotPair?,
    val inverseVertical: Boolean,
    val inverseHorizontal: Boolean
) {
    companion object {
        val Default by lazy {
            CutParams(
                vertical = null,
                horizontal = null,
                inverseVertical = false,
                inverseHorizontal = false
            )
        }
    }
}

class PivotPair(
    val start: Float,
    val end: Float,
    val isRtl: Boolean = false
) {
    val startRtlAdjusted = if (isRtl) 1f - end else start
    val endRtlAdjusted = if (isRtl) 1f - start else end

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PivotPair

        if (start != other.start) return false
        if (end != other.end) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }
}