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

package ru.tech.imageresizershrinker.core.ui.shapes

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.graphics.shapes.Cubic
import androidx.graphics.shapes.RoundedPolygon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import ru.tech.imageresizershrinker.core.settings.presentation.model.IconShape
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.inverse
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalContainerColor
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalContainerShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import kotlin.math.PI
import kotlin.math.atan2

object IconShapeDefaults {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    val shapes: ImmutableList<IconShape> by lazy {
        listOf(
            IconShape(SquircleShape),
            IconShape(RoundedCornerShape(15)),
            IconShape(RoundedCornerShape(25)),
            IconShape(RoundedCornerShape(35)),
            IconShape(RoundedCornerShape(45)),
            IconShape(CutCornerShape(25)),
            IconShape(CutCornerShape(35), 8.dp, 22.dp),
            IconShape(CutCornerShape(50), 10.dp, 18.dp),
            IconShape(CloverShape),
            IconShape(MaterialStarShape, 6.dp, 22.dp),
            IconShape(SmallMaterialStarShape, 6.dp, 22.dp),
            IconShape(BookmarkShape, 8.dp, 22.dp),
            IconShape(PillShape, 10.dp, 22.dp),
            IconShape(BurgerShape, 6.dp, 22.dp),
            IconShape(OvalShape, 6.dp),
            IconShape(ShieldShape, 8.dp, 20.dp),
            IconShape(EggShape, 8.dp, 20.dp),
            IconShape(DropletShape, 6.dp, 22.dp),
            IconShape(ArrowShape, 10.dp, 20.dp),
            IconShape(PentagonShape, 6.dp, 22.dp),
            IconShape(OctagonShape, 6.dp, 22.dp),
            IconShape(ShurikenShape, 8.dp, 22.dp),
            IconShape(ExplosionShape, 6.dp),
            IconShape(MapShape, 10.dp, 22.dp),
            IconShape(HeartShape, 10.dp, 18.dp),
            IconShape(SimpleHeartShape, 12.dp, 16.dp),
        ).toMutableList().apply {
            val shapes = listOf(
                MaterialShapes.Slanted,
                MaterialShapes.Arch,
                MaterialShapes.SemiCircle,
                MaterialShapes.Oval,
                MaterialShapes.Diamond,
                MaterialShapes.ClamShell,
                MaterialShapes.Gem,
                MaterialShapes.Sunny,
                MaterialShapes.VerySunny,
                MaterialShapes.Cookie6Sided,
                MaterialShapes.Cookie9Sided,
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
                MaterialShapes.Bun
            ).map {
                IconShape(it.toShape(), 10.dp, 20.dp)
            }
            addAll(shapes)
            add(IconShape.Random)
        }.toPersistentList()
    }

    val contentColor: Color
        @Composable
        get() = takeColorFromScheme {
            val settingsState = LocalSettingsState.current

            onPrimaryContainer.inverse(
                fraction = {
                    if (it && settingsState.isAmoledMode) 0.35f
                    else 0.65f
                }
            )
        }

    val containerColor: Color
        @Composable
        get() = takeColorFromScheme {
            if (it) primary.blend(primaryContainer).copy(0.2f)
            else primaryContainer.blend(primary).copy(0.35f)
        }

}

@Composable
fun IconShapeContainer(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    iconShape: IconShape? = LocalSettingsState.current.iconShape,
    contentColor: Color = IconShapeDefaults.contentColor,
    containerColor: Color = IconShapeDefaults.containerColor,
    content: @Composable (Boolean) -> Unit = {}
) {
    CompositionLocalProvider(
        values = arrayOf(
            LocalContainerShape provides null,
            LocalContainerColor provides null,
            LocalContentColor provides if (enabled && contentColor.isSpecified && iconShape != null) {
                contentColor
            } else LocalContentColor.current
        )
    ) {
        AnimatedContent(
            targetState = remember(iconShape) {
                derivedStateOf {
                    iconShape?.takeOrElseFrom(IconShapeDefaults.shapes)
                }
            }.value,
            modifier = modifier
        ) { iconShapeAnimated ->
            Box(
                modifier = if (enabled && iconShapeAnimated != null) {
                    Modifier.container(
                        shape = iconShapeAnimated.shape,
                        color = containerColor,
                        autoShadowElevation = 0.65.dp,
                        resultPadding = iconShapeAnimated.padding,
                        composeColorOnTopOfBackground = false,
                        isShadowClip = true
                    )
                } else Modifier,
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = if (enabled && iconShapeAnimated != null) {
                        Modifier
                            .size(iconShapeAnimated.iconSize)
                            .offset(
                                y = when (iconShapeAnimated.shape) {
                                    PentagonShape -> 2.dp
                                    BookmarkShape -> (-1).dp
                                    SimpleHeartShape -> (-1.5).dp
                                    ArrowShape -> 2.dp
                                    else -> 0.dp
                                }
                            )
                    } else Modifier
                ) {
                    content(iconShapeAnimated == null)
                }
            }
        }
    }
}

@ExperimentalMaterial3ExpressiveApi
fun RoundedPolygon.toShape(startAngle: Int = 0): Shape {
    return GenericShape { size, direction ->
        rewind()
        addPath(toPath(startAngle = startAngle))
        val scaleMatrix = Matrix().apply { scale(x = size.width, y = size.height) }

        transform(scaleMatrix)
    }
}

fun RoundedPolygon.toPath(
    path: Path = Path(),
    startAngle: Int = 0,
    repeatPath: Boolean = false,
    closePath: Boolean = true,
): Path {
    pathFromCubics(
        path = path,
        startAngle = startAngle,
        repeatPath = repeatPath,
        closePath = closePath,
        cubics = cubics,
        rotationPivotX = centerX,
        rotationPivotY = centerY
    )
    return path
}

private fun pathFromCubics(
    path: Path,
    startAngle: Int,
    repeatPath: Boolean,
    closePath: Boolean,
    cubics: List<Cubic>,
    rotationPivotX: Float,
    rotationPivotY: Float
) {
    var first = true
    var firstCubic: Cubic? = null
    path.rewind()
    cubics.fastForEach {
        if (first) {
            path.moveTo(it.anchor0X, it.anchor0Y)
            if (startAngle != 0) {
                firstCubic = it
            }
            first = false
        }
        path.cubicTo(
            it.control0X,
            it.control0Y,
            it.control1X,
            it.control1Y,
            it.anchor1X,
            it.anchor1Y
        )
    }
    if (repeatPath) {
        var firstInRepeat = true
        cubics.fastForEach {
            if (firstInRepeat) {
                path.lineTo(it.anchor0X, it.anchor0Y)
                firstInRepeat = false
            }
            path.cubicTo(
                it.control0X,
                it.control0Y,
                it.control1X,
                it.control1Y,
                it.anchor1X,
                it.anchor1Y
            )
        }
    }

    if (closePath) path.close()

    if (startAngle != 0 && firstCubic != null) {
        val angleToFirstCubic =
            radiansToDegrees(
                atan2(
                    y = cubics[0].anchor0Y - rotationPivotY,
                    x = cubics[0].anchor0X - rotationPivotX
                )
            )
        // Rotate the Path to to start from the given angle.
        path.transform(Matrix().apply { rotateZ(-angleToFirstCubic + startAngle) })
    }
}

private fun radiansToDegrees(radians: Float): Float {
    return (radians * 180.0 / PI).toFloat()
}