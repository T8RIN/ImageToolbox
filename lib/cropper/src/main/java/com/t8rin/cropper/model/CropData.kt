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
import androidx.compose.ui.geometry.Rect


/**
 * Class that contains information about
 * current zoom, pan and rotation, and rectangle of zoomed and panned area for cropping [cropRect],
 * and area of overlay as[overlayRect]
 *
 */
@Immutable
data class CropData(
    val zoom: Float = 1f,
    val pan: Offset = Offset.Zero,
    val rotation: Float = 0f,
    val overlayRect: Rect,
    val cropRect: Rect
)
