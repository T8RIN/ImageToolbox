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

@file:Suppress("AnimateAsStateLabel")

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalContainerShape
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

fun Modifier.container(
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = Color.Unspecified,
    resultPadding: Dp = 4.dp,
    borderColor: Color? = null,
    autoShadowElevation: Dp = 1.dp,
    clip: Boolean = true,
    composeColorOnTopOfBackground: Boolean = true,
    isShadowClip: Boolean = false,
    isStandaloneContainer: Boolean = true
) = composed {
    val localContainerShape = LocalContainerShape.current
    val resultShape = localContainerShape ?: shape
    val settingsState = LocalSettingsState.current
    val colorScheme = MaterialTheme.colorScheme
    val color1 = if (color.isUnspecified) {
        colorScheme.surfaceColorAtElevation(1.dp)
    } else {
        if (composeColorOnTopOfBackground) color.compositeOver(colorScheme.background)
        else color
    }

    val density = LocalDensity.current

    val genericModifier = Modifier.drawWithCache {
        val outline = resultShape.createOutline(
            size,
            layoutDirection,
            density
        )
        onDrawWithContent {
            drawOutline(
                outline = outline,
                color = color1
            )
            if (settingsState.borderWidth > 0.dp) {
                drawOutline(
                    outline = outline,
                    color = borderColor ?: colorScheme.outlineVariant(0.1f, color1),
                    style = Stroke(with(density) { settingsState.borderWidth.toPx() })
                )
            }
            drawContent()
        }
    }

    val cornerModifier = Modifier
        .background(
            color = color1,
            shape = resultShape
        )
        .border(
            width = LocalSettingsState.current.borderWidth,
            color = borderColor ?: colorScheme.outlineVariant(0.1f, color1),
            shape = resultShape
        )

    this
        .materialShadow(
            shape = resultShape,
            elevation = animateDpAsState(
                if (settingsState.borderWidth > 0.dp) {
                    0.dp
                } else autoShadowElevation.coerceAtLeast(0.dp)
            ).value,
            enabled = if (isStandaloneContainer) {
                LocalSettingsState.current.drawContainerShadows
            } else true,
            isClipped = isShadowClip
        )
        .then(
            if (resultShape is CornerBasedShape) cornerModifier
            else genericModifier
        )
        .then(if (clip) Modifier.clip(resultShape) else Modifier)
        .then(if (resultPadding > 0.dp) Modifier.padding(resultPadding) else Modifier)
}