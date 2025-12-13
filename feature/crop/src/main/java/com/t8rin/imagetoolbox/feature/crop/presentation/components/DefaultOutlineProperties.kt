/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.crop.presentation.components

import androidx.compose.material3.MaterialShapes
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
import com.t8rin.imagetoolbox.core.resources.shapes.ArrowShape
import com.t8rin.imagetoolbox.core.resources.shapes.BookmarkShape
import com.t8rin.imagetoolbox.core.resources.shapes.BurgerShape
import com.t8rin.imagetoolbox.core.resources.shapes.CloverShape
import com.t8rin.imagetoolbox.core.resources.shapes.DropletShape
import com.t8rin.imagetoolbox.core.resources.shapes.EggShape
import com.t8rin.imagetoolbox.core.resources.shapes.ExplosionShape
import com.t8rin.imagetoolbox.core.resources.shapes.KotlinShape
import com.t8rin.imagetoolbox.core.resources.shapes.MapShape
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.resources.shapes.OctagonShape
import com.t8rin.imagetoolbox.core.resources.shapes.OvalShape
import com.t8rin.imagetoolbox.core.resources.shapes.PentagonShape
import com.t8rin.imagetoolbox.core.resources.shapes.PillShape
import com.t8rin.imagetoolbox.core.resources.shapes.ShieldShape
import com.t8rin.imagetoolbox.core.resources.shapes.ShurikenShape
import com.t8rin.imagetoolbox.core.resources.shapes.SmallMaterialStarShape
import com.t8rin.imagetoolbox.core.resources.shapes.SquircleShape
import com.t8rin.imagetoolbox.core.settings.presentation.utils.toShape

val DefaultOutlineProperties = listOf(
    CropOutlineProperty(
        outlineType = OutlineType.Rect,
        cropOutline = RectCropShape(
            id = 0,
            title = OutlineType.Rect.name
        )
    ),
    CropOutlineProperty(
        outlineType = OutlineType.RoundedRect,
        cropOutline = RoundedCornerCropShape(
            id = 1,
            title = OutlineType.RoundedRect.name,
            cornerRadius = CornerRadiusProperties()
        )
    ),
    CropOutlineProperty(
        outlineType = OutlineType.CutCorner,
        cropOutline = CutCornerCropShape(
            id = 2,
            title = OutlineType.CutCorner.name,
            cornerRadius = CornerRadiusProperties()
        )
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = OvalShape
            override val id: Int = 3
            override val title: String = "Oval"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = SquircleShape
            override val id: Int = 4
            override val title: String = "Squircle"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = OctagonShape
            override val id: Int = 5
            override val title: String = "Octagon"
        },
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = PentagonShape
            override val id: Int = 6
            override val title: String = "Pentagon"
        }
    ),
    CropOutlineProperty(
        OutlineType.Custom,
        object : CropShape {
            override val shape: Shape = CloverShape
            override val id: Int = 7
            override val title: String = "Clover"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = PillShape
            override val id: Int = 8
            override val title: String = "Pill"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = BookmarkShape
            override val id: Int = 9
            override val title: String = "Bookmark"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = BurgerShape
            override val id: Int = 10
            override val title: String = "Burger"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = ShurikenShape
            override val id: Int = 11
            override val title: String = "Shuriken"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = ExplosionShape
            override val id: Int = 12
            override val title: String = "Explosion"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = MapShape
            override val id: Int = 13
            override val title: String = "Map"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = MaterialStarShape
            override val id: Int = 14
            override val title: String = "MaterialStar"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = SmallMaterialStarShape
            override val id: Int = 15
            override val title: String = "SmallMaterialStar"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = ShieldShape
            override val id: Int = 16
            override val title: String = "Shield"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = EggShape
            override val id: Int = 17
            override val title: String = "Egg"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = DropletShape
            override val id: Int = 18
            override val title: String = "Droplet"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = ArrowShape
            override val id: Int = 19
            override val title: String = "Arrow"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = object : CropShape {
            override val shape: Shape = KotlinShape
            override val id: Int = 20
            override val title: String = "Kotlin"
        }
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = CustomPathOutline(
            id = 21,
            title = "Heart",
            path = Paths.Favorite
        )
    ),
    CropOutlineProperty(
        outlineType = OutlineType.Custom,
        cropOutline = CustomPathOutline(
            id = 22,
            title = "Star",
            path = Paths.Star
        )
    ),
).toMutableList().apply {
    val shapes = listOf(
        MaterialShapes.Slanted,
        MaterialShapes.Arch,
        MaterialShapes.Fan,
        MaterialShapes.Arrow,
        MaterialShapes.SemiCircle,
        MaterialShapes.Oval,
        MaterialShapes.Triangle,
        MaterialShapes.Diamond,
        MaterialShapes.ClamShell,
        MaterialShapes.Gem,
        MaterialShapes.Sunny,
        MaterialShapes.VerySunny,
        MaterialShapes.Cookie4Sided,
        MaterialShapes.Cookie6Sided,
        MaterialShapes.Cookie9Sided,
        MaterialShapes.Cookie12Sided,
        MaterialShapes.Ghostish,
        MaterialShapes.Clover4Leaf,
        MaterialShapes.Clover8Leaf,
        MaterialShapes.Burst,
        MaterialShapes.SoftBurst,
        MaterialShapes.Boom,
        MaterialShapes.SoftBoom,
        MaterialShapes.Flower,
        MaterialShapes.Puffy,
        MaterialShapes.PuffyDiamond,
        MaterialShapes.PixelCircle,
        MaterialShapes.PixelTriangle,
        MaterialShapes.Bun,
        MaterialShapes.Heart
    ).mapIndexed { index, polygon ->
        CropOutlineProperty(
            outlineType = OutlineType.Custom,
            cropOutline = object : CropShape {
                override val shape: Shape = polygon.toShape()
                override val id: Int = index + 500
                override val title: String = (index + 500).toString()
            }
        )
    }
    addAll(shapes)
    add(
        CropOutlineProperty(
            outlineType = OutlineType.ImageMask,
            cropOutline = ImageMaskOutline(
                id = 23,
                title = OutlineType.ImageMask.name,
                image = ImageBitmap(
                    width = 1,
                    height = 1
                )
            )
        )
    )
}