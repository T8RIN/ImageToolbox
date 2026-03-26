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

/**
 * Interface for containing multiple [CropOutline]s, currently selected item and index
 * for displaying on settings UI
 */
interface CropOutlineContainer<O : CropOutline> {
    var selectedIndex: Int
    val outlines: List<O>
    val selectedItem: O
        get() = outlines[selectedIndex]
    val size: Int
        get() = outlines.size
}

/**
 * Container for [RectCropShape]
 */
data class RectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<RectCropShape>
) : CropOutlineContainer<RectCropShape>

/**
 * Container for [RoundedCornerCropShape]s
 */
data class RoundedRectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<RoundedCornerCropShape>
) : CropOutlineContainer<RoundedCornerCropShape>

/**
 * Container for [CutCornerCropShape]s
 */
data class CutCornerRectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<CutCornerCropShape>
) : CropOutlineContainer<CutCornerCropShape>

/**
 * Container for [OvalCropShape]s
 */
data class OvalOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<OvalCropShape>
) : CropOutlineContainer<OvalCropShape>

/**
 * Container for [PolygonCropShape]s
 */
data class PolygonOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<PolygonCropShape>
) : CropOutlineContainer<PolygonCropShape>

/**
 * Container for [CustomPathOutline]s
 */
data class CustomOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<CustomPathOutline>
) : CropOutlineContainer<CustomPathOutline>

/**
 * Container for [ImageMaskOutline]s
 */
data class ImageMaskOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<ImageMaskOutline>
) : CropOutlineContainer<ImageMaskOutline>
