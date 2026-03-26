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

package com.t8rin.cropper.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import com.t8rin.cropper.settings.CropProperties
import com.t8rin.cropper.settings.CropType


/**
 * Create and [remember] the [CropState] based on the currently appropriate transform
 * configuration to allow changing pan, zoom, and rotation.
 * @param imageSize size of the **Bitmap**
 * @param containerSize size of the Composable that draws **Bitmap**
 * @param cropProperties wrapper class that contains crop state properties such as
 * crop type,
 * @param keys are used to reset remember block to initial calculations. This can be used
 * when image, contentScale or any property changes which requires values to be reset to initial
 * values
 */
@Composable
fun rememberCropState(
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    cropProperties: CropProperties,
    vararg keys: Any?
): CropState {

    // Properties of crop state
    val handleSize = cropProperties.handleSize
    val cropType = cropProperties.cropType
    val aspectRatio = cropProperties.aspectRatio
    val overlayRatio = cropProperties.overlayRatio
    val maxZoom = cropProperties.maxZoom
    val fling = cropProperties.fling
    val zoomable = cropProperties.zoomable
    val pannable = cropProperties.pannable
    val rotatable = cropProperties.rotatable
    val minDimension = cropProperties.minDimension
    val fixedAspectRatio = cropProperties.fixedAspectRatio

    return remember(*keys) {
        when (cropType) {
            CropType.Static -> {
                StaticCropState(
                    imageSize = imageSize,
                    containerSize = containerSize,
                    drawAreaSize = drawAreaSize,
                    aspectRatio = aspectRatio,
                    overlayRatio = overlayRatio,
                    maxZoom = maxZoom,
                    fling = fling,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = false
                )
            }

            else -> {
                DynamicCropState(
                    imageSize = imageSize,
                    containerSize = containerSize,
                    drawAreaSize = drawAreaSize,
                    aspectRatio = aspectRatio,
                    overlayRatio = overlayRatio,
                    maxZoom = maxZoom,
                    handleSize = handleSize,
                    fling = fling,
                    zoomable = zoomable,
                    pannable = pannable,
                    rotatable = rotatable,
                    limitPan = true,
                    minDimension = minDimension,
                    fixedAspectRatio = fixedAspectRatio,
                )
            }
        }
    }
}
