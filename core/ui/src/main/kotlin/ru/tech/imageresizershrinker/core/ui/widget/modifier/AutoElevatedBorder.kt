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
import androidx.compose.foundation.border
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.theme.suggestContainerColorBy

fun Modifier.autoElevatedBorder(
    height: Dp = Dp.Unspecified,
    shape: Shape? = null,
    color: Color = Color.Unspecified,
    autoElevation: Dp = 6.dp
) = this.composed {
    val h = if (height.isUnspecified) {
        LocalSettingsState.current.borderWidth.takeIf { it > 0.dp }
    } else null

    val shape1 = shape ?: FloatingActionButtonDefaults.shape

    if (h == null) {
        Modifier
    } else {
        Modifier.border(
            width = h,
            color = if (color.isSpecified) color
            else {
                MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.3f,
                    onTopOf = MaterialTheme.colorScheme.suggestContainerColorBy(LocalContentColor.current)
                )
            },
            shape = shape1
        )
    }.materialShadow(
        elevation = animateDpAsState(if (h == null) autoElevation else 0.dp).value,
        shape = shape1
    )
}

fun Modifier.containerFabBorder(
    autoElevation: Dp = 1.5.dp,
    shape: Shape? = null
) = autoElevatedBorder(
    autoElevation = autoElevation,
    shape = shape
)