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

package com.t8rin.cropper.util

import androidx.compose.ui.graphics.GraphicsLayerScope
import com.t8rin.cropper.state.TransformState

/**
 * Calculate zoom level and zoom value when user double taps
 */
internal fun calculateZoom(
    zoomLevel: ZoomLevel,
    initial: Float,
    min: Float,
    max: Float
): Pair<ZoomLevel, Float> {

    val newZoomLevel: ZoomLevel
    val newZoom: Float

    when (zoomLevel) {
        ZoomLevel.Mid -> {
            newZoomLevel = ZoomLevel.Max
            newZoom = max.coerceAtMost(3f)
        }

        ZoomLevel.Max -> {
            newZoomLevel = ZoomLevel.Min
            newZoom = if (min == initial) initial else min
        }

        else -> {
            newZoomLevel = ZoomLevel.Mid
            newZoom = if (min == initial) (min + max.coerceAtMost(3f)) / 2 else initial
        }
    }
    return Pair(newZoomLevel, newZoom)
}

internal fun getNextZoomLevel(zoomLevel: ZoomLevel): ZoomLevel = when (zoomLevel) {
    ZoomLevel.Mid -> {
        ZoomLevel.Max
    }

    ZoomLevel.Max -> {
        ZoomLevel.Min
    }

    else -> {
        ZoomLevel.Mid
    }
}

/**
 * Update graphic layer with [transformState]
 */
internal fun GraphicsLayerScope.update(transformState: TransformState) {

    // Set zoom
    val zoom = transformState.zoom
    this.scaleX = zoom
    this.scaleY = zoom

    // Set pan
    val pan = transformState.pan
    val translationX = pan.x
    val translationY = pan.y
    this.translationX = translationX
    this.translationY = translationY

    // Set rotation
    this.rotationZ = transformState.rotation
}
