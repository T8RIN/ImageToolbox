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

package com.t8rin.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class CornerRadiusProperties(
    val topStartPercent: Int = 20,
    val topEndPercent: Int = 20,
    val bottomStartPercent: Int = 20,
    val bottomEndPercent: Int = 20
)

@Immutable
data class PolygonProperties(
    val sides: Int = 6,
    val angle: Float = 0f,
    val offset: Offset = Offset.Zero
)

@Immutable
data class OvalProperties(
    val startAngle: Float = 0f,
    val sweepAngle: Float = 360f,
    val offset: Offset = Offset.Zero
)
