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

package ru.tech.imageresizershrinker.feature.crop.presentation.components

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
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.shapes.KotlinShape
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.shapes.OctagonShape
import ru.tech.imageresizershrinker.core.ui.shapes.OvalShape
import ru.tech.imageresizershrinker.core.ui.shapes.PentagonShape
import ru.tech.imageresizershrinker.core.ui.shapes.SmallMaterialStarShape
import ru.tech.imageresizershrinker.core.ui.shapes.SquircleShape

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
                    get() = "MaterialStar"

            }
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = SmallMaterialStarShape
                override val id: Int
                    get() = 14
                override val title: String
                    get() = "SmallMaterialStar"

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