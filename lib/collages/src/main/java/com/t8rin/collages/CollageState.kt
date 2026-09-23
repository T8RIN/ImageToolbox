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

package com.t8rin.collages

import androidx.compose.runtime.Immutable

@Immutable
data class CollageState(
    val layoutId: String,
    val images: List<CollageImageState>,
    val layoutParams: List<Float>
)

@Immutable
data class CollageImageState(
    val index: Int,
    val uri: String?,
    val matrixValues: List<Float>,
    val viewWidth: Float,
    val viewHeight: Float,
    val userAllowedEmptySpace: Boolean
)
