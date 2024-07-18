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

package ru.tech.imageresizershrinker.core.domain.image.model

sealed interface ImageFrames {

    data object All : ImageFrames {
        override fun getFramePositions(
            frameCount: Int
        ): List<Int> = List(frameCount) { it + 1 }
    }

    data class ManualSelection(
        val framePositions: List<Int>
    ) : ImageFrames {
        override fun getFramePositions(
            frameCount: Int
        ): List<Int> = framePositions.filter { it - 1 < frameCount }
    }

    fun isEmpty(): Boolean = when (this) {
        All -> false
        is ManualSelection -> framePositions.isEmpty()
    }

    fun isNotEmpty(): Boolean = !isEmpty()

    fun getFramePositions(
        frameCount: Int
    ): List<Int>
}