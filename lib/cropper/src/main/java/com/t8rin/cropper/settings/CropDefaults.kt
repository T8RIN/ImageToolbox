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

package com.t8rin.cropper.settings

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.t8rin.cropper.ImageCropper
import com.t8rin.cropper.crop
import com.t8rin.cropper.model.AspectRatio
import com.t8rin.cropper.model.CropOutline
import com.t8rin.cropper.model.OutlineType
import com.t8rin.cropper.model.aspectRatios
import com.t8rin.cropper.state.CropState

/**
 * Contains the default values used by [ImageCropper]
 */
object CropDefaults {

    /**
     * Properties effect crop behavior that should be passed to [CropState]
     */
    fun properties(
        cropType: CropType = CropType.Dynamic,
        handleSize: Float = 60f,
        middleHandleSize: Float = handleSize * 1.5f,
        maxZoom: Float = 10f,
        contentScale: ContentScale = ContentScale.Fit,
        cropOutlineProperty: CropOutlineProperty,
        aspectRatio: AspectRatio = aspectRatios[2].aspectRatio,
        overlayRatio: Float = .9f,
        pannable: Boolean = true,
        fling: Boolean = false,
        zoomable: Boolean = true,
        rotatable: Boolean = false,
        minDimension: IntSize? = null,
        fixedAspectRatio: Boolean = false,
    ): CropProperties {
        return CropProperties(
            cropType = cropType,
            handleSize = handleSize,
            middleHandleSize = middleHandleSize,
            contentScale = contentScale,
            cropOutlineProperty = cropOutlineProperty,
            maxZoom = maxZoom,
            aspectRatio = aspectRatio,
            overlayRatio = overlayRatio,
            pannable = pannable,
            fling = fling,
            zoomable = zoomable,
            rotatable = rotatable,
            minDimension = minDimension,
            fixedAspectRatio = fixedAspectRatio,
        )
    }

    /**
     * Style is cosmetic changes that don't effect how [CropState] behaves because of that
     * none of these properties are passed to [CropState]
     */
    fun style(
        drawOverlay: Boolean = true,
        drawGrid: Boolean = true,
        strokeWidth: Dp = 1.dp,
        overlayColor: Color = DefaultOverlayColor,
        handleColor: Color = DefaultHandleColor,
        backgroundColor: Color = DefaultBackgroundColor
    ): CropStyle {
        return CropStyle(
            drawOverlay = drawOverlay,
            drawGrid = drawGrid,
            strokeWidth = strokeWidth,
            overlayColor = overlayColor,
            handleColor = handleColor,
            backgroundColor = backgroundColor
        )
    }
}

/**
 * Data class for selecting cropper properties. Fields of this class control inner work
 * of [CropState] while some such as [cropType], [aspectRatio], [handleSize]
 * is shared between ui and state.
 */
@Immutable
data class CropProperties(
    val cropType: CropType,
    val handleSize: Float,
    val middleHandleSize: Float,
    val contentScale: ContentScale,
    val cropOutlineProperty: CropOutlineProperty,
    val aspectRatio: AspectRatio,
    val overlayRatio: Float,
    val pannable: Boolean,
    val fling: Boolean,
    val rotatable: Boolean,
    val zoomable: Boolean,
    val maxZoom: Float,
    val minDimension: IntSize? = null,
    val fixedAspectRatio: Boolean = false,
)

/**
 * Data class for cropper styling only. None of the properties of this class is used
 * by [CropState] or [Modifier.crop]
 */
@Immutable
data class CropStyle(
    val drawOverlay: Boolean,
    val drawGrid: Boolean,
    val strokeWidth: Dp,
    val overlayColor: Color,
    val handleColor: Color,
    val backgroundColor: Color,
    val cropTheme: CropTheme = CropTheme.Dark
)

/**
 * Property for passing [CropOutline] between settings UI to [ImageCropper]
 */
@Immutable
data class CropOutlineProperty(
    val outlineType: OutlineType,
    val cropOutline: CropOutline
)

/**
 * Light, Dark or system controlled theme
 */
enum class CropTheme {
    Light,
    Dark,
    System
}

private val DefaultBackgroundColor = Color(0x99000000)
private val DefaultOverlayColor = Color.Gray
private val DefaultHandleColor = Color.White