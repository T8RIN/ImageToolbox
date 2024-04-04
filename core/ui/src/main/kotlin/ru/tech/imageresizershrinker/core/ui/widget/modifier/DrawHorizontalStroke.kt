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

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.zIndex
import com.gigamole.composeshadowsplus.softlayer.softLayerShadow
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant

fun Modifier.drawHorizontalStroke(
    top: Boolean = false,
    height: Dp = Dp.Unspecified,
    autoElevation: Dp = 6.dp,
    enabled: Boolean = true
) = this.composed {
    if (!enabled) return@composed Modifier

    val settingsState = LocalSettingsState.current
    val borderWidth = settingsState.borderWidth
    val h = if (height.isUnspecified) {
        borderWidth.takeIf { it > 0.dp }
    } else height

    val color = MaterialTheme.colorScheme.outlineVariant(
        0.1f,
        onTopOf = MaterialTheme.colorScheme.surfaceContainer
    )


    val shadow = Modifier
        .softLayerShadow(
            spread = (-2).dp,
            shape = RectangleShape,
            radius = animateDpAsState(
                if (h == null && settingsState.drawAppBarShadows) {
                    autoElevation
                } else 0.dp
            ).value
        )
        .zIndex(100f)

    if (h == null) {
        shadow
    } else {
        val heightPx = with(LocalDensity.current) { h.toPx() }
        Modifier
            .zIndex(100f)
            .drawWithContent {
                drawContent()
                drawRect(
                    color = color,
                    topLeft = if (top) Offset(0f, 0f) else Offset(0f, this.size.height),
                    size = Size(this.size.width, heightPx)
                )
            }
            .then(shadow)
    }
}