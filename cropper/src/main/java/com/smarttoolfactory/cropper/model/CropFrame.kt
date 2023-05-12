package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable

/**
 * Model for drawing title with shape for crop selection menu.
 */
@Immutable
data class CropFrame(
    val outlineType: OutlineType,
    val editable: Boolean = false,
    val cropOutlineContainer: CropOutlineContainer<out CropOutline>
) {
    var selectedIndex: Int
        get() = cropOutlineContainer.selectedIndex
        set(value) {
            cropOutlineContainer.selectedIndex = value
        }

    val outlines: List<CropOutline>
        get() = cropOutlineContainer.outlines

    val outlineCount: Int
        get() = cropOutlineContainer.size

    fun addOutline(outline: CropOutline): CropFrame {
        outlines.toMutableList().add(outline)
       return this
    }
}

@Suppress("UNCHECKED_CAST")
fun getOutlineContainer(
    outlineType: OutlineType,
    index: Int,
    outlines: List<CropOutline>
): CropOutlineContainer<out CropOutline> {
    return when (outlineType) {
        OutlineType.RoundedRect -> {
            RoundedRectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<RoundedCornerCropShape>
            )
        }
        OutlineType.CutCorner -> {
            CutCornerRectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<CutCornerCropShape>
            )
        }

        OutlineType.Oval -> {
            OvalOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<OvalCropShape>
            )
        }

        OutlineType.Polygon -> {
            PolygonOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<PolygonCropShape>
            )
        }

        OutlineType.Custom -> {
            CustomOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<CustomPathOutline>
            )
        }

        OutlineType.ImageMask -> {
            ImageMaskOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<ImageMaskOutline>
            )
        }
        else -> {
            RectOutlineContainer(
                selectedIndex = index,
                outlines = outlines as List<RectCropShape>
            )
        }
    }
}


enum class OutlineType {
    Rect, RoundedRect, CutCorner, Oval, Polygon, Custom, ImageMask
}
