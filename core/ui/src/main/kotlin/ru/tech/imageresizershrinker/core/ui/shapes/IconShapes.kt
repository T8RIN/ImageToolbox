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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import ru.tech.imageresizershrinker.core.settings.presentation.model.IconShape
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.inverse
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalContainerColor
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalContainerShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

object IconShapeDefaults {

    val shapes by lazy {
        persistentListOf(
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
            IconShape(PentagonShape, 6.dp, 22.dp),
            IconShape(OctagonShape, 6.dp, 22.dp),
            IconShape(HeartShape, 10.dp, 18.dp),
            IconShape(SimpleHeartShape, 12.dp, 16.dp),
            IconShape.Random
        )
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
                        autoShadowElevation = 0.5.dp,
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