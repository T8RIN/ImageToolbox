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

package com.t8rin.imagetoolbox.core.ui.widget.icon_shape

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.shapes.ArrowShape
import com.t8rin.imagetoolbox.core.resources.shapes.BookmarkShape
import com.t8rin.imagetoolbox.core.resources.shapes.PentagonShape
import com.t8rin.imagetoolbox.core.resources.shapes.SimpleHeartShape
import com.t8rin.imagetoolbox.core.settings.presentation.model.IconShape
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerColor
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerShape
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

object IconShapeDefaults {

    val contentColor: Color
        @Composable
        get() {
            val colorScheme = MaterialTheme.colorScheme
            val localContainer = SafeLocalContainerColor
            val localContent = LocalContentColor.current
            val container = containerColor
            val settingsState = LocalSettingsState.current

            return remember(colorScheme, localContainer, localContent, container, settingsState) {
                derivedStateOf {
                    val containerLuma = container.compositeOver(localContainer).luminance()

                    val isLight = containerLuma > 0.2f

                    val baseColor = if (isLight) {
                        Color.Black.blend(
                            color = colorScheme.onPrimaryContainer,
                            fraction = 0.35f
                        )
                    } else {
                        Color.White.blend(
                            color = colorScheme.primary,
                            fraction = 0.35f
                        )
                    }

                    if (settingsState.isAmoledMode && settingsState.isNightMode) {
                        baseColor.blend(Color.Black)
                    } else {
                        baseColor
                    }
                }
            }.value
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
    contentColor: Color = LocalIconShapeContentColor.current
        ?: IconShapeDefaults.contentColor,
    containerColor: Color = LocalIconShapeContainerColor.current
        ?: IconShapeDefaults.containerColor,
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
                    iconShape?.takeOrElseFrom(IconShape.entries)
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

val LocalIconShapeContentColor = compositionLocalOf<Color?> { null }
val LocalIconShapeContainerColor = compositionLocalOf<Color?> { null }