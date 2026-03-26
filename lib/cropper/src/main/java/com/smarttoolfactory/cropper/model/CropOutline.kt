package com.smarttoolfactory.cropper.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.smarttoolfactory.cropper.util.createPolygonShape

/**
 * Common ancestor for list of shapes, paths or images to crop inside [CropOutlineContainer]
 */
interface CropOutline {
    val id: Int
    val title: String
}

/**
 * Crop outline that contains a [Shape] like [RectangleShape] to draw frame for cropping
 */
interface CropShape : CropOutline {
    val shape: Shape
}

/**
 * Crop outline that contains a [Path] to draw frame for cropping
 */
interface CropPath : CropOutline {
    val path: Path
}

/**
 * Crop outline that contains a [ImageBitmap]  to draw frame for cropping. And blend modes
 * to draw
 */
interface CropImageMask : CropOutline {
    val image: ImageBitmap
}

/**
 * Wrapper class that implements [CropOutline] and is a shape
 * wrapper that contains [RectangleShape]
 */
@Immutable
data class RectCropShape(
    override val id: Int,
    override val title: String,
) : CropShape {
    override val shape: Shape = RectangleShape
}

/**
 * Wrapper class that implements [CropOutline] and is a shape
 * wrapper that contains [RoundedCornerShape]
 */
@Immutable
data class RoundedCornerCropShape(
    override val id: Int,
    override val title: String,
    val cornerRadius: CornerRadiusProperties = CornerRadiusProperties(),
    override val shape: RoundedCornerShape = RoundedCornerShape(
        topStartPercent = cornerRadius.topStartPercent,
        topEndPercent = cornerRadius.topEndPercent,
        bottomEndPercent = cornerRadius.bottomEndPercent,
        bottomStartPercent = cornerRadius.bottomStartPercent
    )
) : CropShape

/**
 * Wrapper class that implements [CropOutline] and is a shape
 * wrapper that contains [CutCornerShape]
 */
@Immutable
data class CutCornerCropShape(
    override val id: Int,
    override val title: String,
    val cornerRadius: CornerRadiusProperties = CornerRadiusProperties(),
    override val shape: CutCornerShape = CutCornerShape(
        topStartPercent = cornerRadius.topStartPercent,
        topEndPercent = cornerRadius.topEndPercent,
        bottomEndPercent = cornerRadius.bottomEndPercent,
        bottomStartPercent = cornerRadius.bottomStartPercent
    )
) : CropShape

/**
 * Wrapper class that implements [CropOutline] and is a shape
 * wrapper that contains [CircleShape]
 */
@Immutable
data class OvalCropShape(
    override val id: Int,
    override val title: String,
    val ovalProperties: OvalProperties = OvalProperties(),
    override val shape: Shape = CircleShape
) : CropShape


/**
 * Wrapper class that implements [CropOutline] and is a shape
 * wrapper that contains [CircleShape]
 */
@Immutable
data class PolygonCropShape(
    override val id: Int,
    override val title: String,
    val polygonProperties: PolygonProperties = PolygonProperties(),
    override val shape: Shape = createPolygonShape(polygonProperties.sides, polygonProperties.angle)
) : CropShape

/**
 * Wrapper class that implements [CropOutline] and is a [Path] wrapper to crop using drawable
 * files converted fom svg or Vector Drawable to [Path]
 */
@Immutable
data class CustomPathOutline(
    override val id: Int,
    override val title: String,
    override val path: Path
) : CropPath

/**
 * Wrapper class that implements [CropOutline] and is a [ImageBitmap] wrapper to crop
 * using a reference png and blend modes to crop
 */
@Immutable
data class ImageMaskOutline(
    override val id: Int,
    override val title: String,
    override val image: ImageBitmap,
) : CropImageMask
