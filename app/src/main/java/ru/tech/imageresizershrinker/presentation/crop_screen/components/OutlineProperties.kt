package ru.tech.imageresizershrinker.presentation.crop_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import com.smarttoolfactory.cropper.model.CornerRadiusProperties
import com.smarttoolfactory.cropper.model.CropShape
import com.smarttoolfactory.cropper.model.CustomPathOutline
import com.smarttoolfactory.cropper.model.CutCornerCropShape
import com.smarttoolfactory.cropper.model.ImageMaskOutline
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.model.RoundedCornerCropShape
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.settings.Paths
import ru.tech.imageresizershrinker.coreui.shapes.CloverShape
import ru.tech.imageresizershrinker.coreui.shapes.KotlinShape
import ru.tech.imageresizershrinker.coreui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.coreui.shapes.OctagonShape
import ru.tech.imageresizershrinker.coreui.shapes.OvalShape
import ru.tech.imageresizershrinker.coreui.shapes.PentagonShape
import ru.tech.imageresizershrinker.coreui.shapes.SquircleShape

@Composable
fun outlineProperties(): List<CropOutlineProperty> = remember {
    listOf(
        CropOutlineProperty(
            OutlineType.Rect,
            RectCropShape(
                id = 0,
                title = OutlineType.Rect.name
            )
        ),
        CropOutlineProperty(
            OutlineType.RoundedRect,
            RoundedCornerCropShape(
                id = 1,
                title = OutlineType.RoundedRect.name,
                cornerRadius = CornerRadiusProperties()
            )
        ),
        CropOutlineProperty(
            OutlineType.CutCorner,
            CutCornerCropShape(
                id = 2,
                title = OutlineType.CutCorner.name,
                cornerRadius = CornerRadiusProperties()
            )
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = OvalShape
                override val id: Int
                    get() = 5
                override val title: String
                    get() = "Oval"

            }
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = SquircleShape
                override val id: Int
                    get() = 3
                override val title: String
                    get() = "Squircle"

            }
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = OctagonShape
                override val id: Int
                    get() = 6
                override val title: String
                    get() = "Octagon"

            },
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = PentagonShape
                override val id: Int
                    get() = 4
                override val title: String
                    get() = "Pentagon"

            }
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = CloverShape
                override val id: Int
                    get() = 7
                override val title: String
                    get() = "Clover"

            }
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = MaterialStarShape
                override val id: Int
                    get() = 12
                override val title: String
                    get() = "DavidStar"

            }
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = KotlinShape
                override val id: Int
                    get() = 13
                override val title: String
                    get() = "Kotlin"

            }
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            CustomPathOutline(
                id = 8,
                title = "Heart",
                path = Paths.Favorite
            )
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            CustomPathOutline(
                id = 9,
                title = "Star",
                path = Paths.Star
            )
        ),
        CropOutlineProperty(
            OutlineType.ImageMask,
            ImageMaskOutline(
                id = 10,
                title = OutlineType.ImageMask.name,
                image = ImageBitmap(1, 1)
            )
        )
    )
}