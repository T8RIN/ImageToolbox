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

package com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model

data class DuplicateItem(
    val uri: String,
    val name: String,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val format: String,
    val lastModified: Long?,
    val sourceIndex: Int,
    val sha256: String,
    val dHash: Long,
    val distance: Int? = null,
    val isCorrectSize: Boolean = true
) {
    val pixelCount: Long
        get() = width.toLong() * height.toLong()

    val aspectRatio: Double
        get() = if (width > 0 && height > 0) {
            maxOf(width, height).toDouble() / minOf(width, height)
        } else {
            0.0
        }
}
