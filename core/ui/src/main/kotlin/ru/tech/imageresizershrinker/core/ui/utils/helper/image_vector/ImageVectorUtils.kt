/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.utils.helper.image_vector

import android.content.Context
import android.graphics.PorterDuff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RootGroupName
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.density

@Composable
fun imageVectorPainter(imageVector: ImageVector): Painter {
    val density = LocalDensity.current

    return remember(density, imageVector) {
        derivedStateOf {
            imageVector.toPainter(density)
        }
    }.value
}

fun ImageVector.toPainter(
    density: Density
): Painter = createVectorPainterFromImageVector(
    density = density,
    imageVector = this,
    root = GroupComponent().apply {
        createGroupComponent(root)
    }
)

fun ImageVector.toPainter(
    context: Context
): Painter = toPainter(context.density)

fun ImageVector.toImageBitmap(
    context: Context,
    width: Int,
    height: Int,
    tint: Color = Color.Unspecified,
    backgroundColor: Color = Color.Transparent,
    iconPadding: Int = 0
): ImageBitmap {
    val imageBitmap = ImageBitmap(width, height)
    val density = context.density

    val painter = toPainter(context)

    val canvas = Canvas(imageBitmap).apply {
        with(nativeCanvas) {
            drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
            drawColor(backgroundColor.toArgb())
        }
    }

    CanvasDrawScope().draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        translate(iconPadding.toFloat(), iconPadding.toFloat()) {
            with(painter) {
                draw(
                    size = Size(
                        width = (width - iconPadding * 2).toFloat(),
                        height = (height - iconPadding * 2).toFloat()
                    ),
                    colorFilter = ColorFilter.tint(tint)
                )
            }
        }
    }

    return imageBitmap
}

/**
 * Helper method to configure the properties of a VectorPainter that maybe re-used
 */
private fun VectorPainter.configureVectorPainter(
    defaultSize: Size,
    viewportSize: Size,
    name: String = RootGroupName,
    intrinsicColorFilter: ColorFilter?,
    autoMirror: Boolean = false,
): VectorPainter = apply {
    this.size = defaultSize
    this.autoMirror = autoMirror
    this.intrinsicColorFilter = intrinsicColorFilter
    this.viewportSize = viewportSize
    this.name = name
}

/**
 * Helper method to create a VectorPainter instance from an ImageVector
 */
private fun createVectorPainterFromImageVector(
    density: Density,
    imageVector: ImageVector,
    root: GroupComponent
): VectorPainter {
    val defaultSize = density.obtainSizePx(imageVector.defaultWidth, imageVector.defaultHeight)
    val viewport = obtainViewportSize(
        defaultSize,
        imageVector.viewportWidth,
        imageVector.viewportHeight
    )
    return VectorPainter(root).configureVectorPainter(
        defaultSize = defaultSize,
        viewportSize = viewport,
        name = imageVector.name,
        intrinsicColorFilter = createColorFilter(imageVector.tintColor, imageVector.tintBlendMode),
        autoMirror = imageVector.autoMirror
    )
}

/**
 * Helper method to conditionally create a ColorFilter to tint contents if [tintColor] is
 * specified, that is [Color.isSpecified] returns true
 */
private fun createColorFilter(
    tintColor: Color,
    tintBlendMode: BlendMode
): ColorFilter? = if (tintColor.isSpecified) {
    ColorFilter.tint(tintColor, tintBlendMode)
} else {
    null
}

/**
 * Helper method to calculate the viewport size. If the viewport width/height are not specified
 * this falls back on the default size provided
 */
private fun obtainViewportSize(
    defaultSize: Size,
    viewportWidth: Float,
    viewportHeight: Float
) = Size(
    if (viewportWidth.isNaN()) defaultSize.width else viewportWidth,
    if (viewportHeight.isNaN()) defaultSize.height else viewportHeight
)

private fun Density.obtainSizePx(
    defaultWidth: Dp,
    defaultHeight: Dp
) = Size(defaultWidth.toPx(), defaultHeight.toPx())