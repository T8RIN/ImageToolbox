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

package com.t8rin.collages.model

import com.t8rin.collages.config.CollageConstants

data class CollageDesign(
    val slots: List<CollageSlot>
) {
    fun isValid(): Boolean =
        slots.size in 1..CollageConstants.MAX_IMAGE_COUNT && slots.all { slot ->
            slot.points.size in 3..256 && slot.points.all {
                it.x.isFinite() && it.y.isFinite() && it.x in 0f..1f && it.y in 0f..1f
            } && slot.area > 0.00001f && slot.right - slot.left > 0.001f && slot.bottom - slot.top > 0.001f
        }
}