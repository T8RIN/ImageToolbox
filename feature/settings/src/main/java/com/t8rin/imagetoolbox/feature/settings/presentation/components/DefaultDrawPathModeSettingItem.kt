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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FloodFill
import com.t8rin.imagetoolbox.core.resources.icons.FreeArrow
import com.t8rin.imagetoolbox.core.resources.icons.FreeDoubleArrow
import com.t8rin.imagetoolbox.core.resources.icons.FreeDraw
import com.t8rin.imagetoolbox.core.resources.icons.Lasso
import com.t8rin.imagetoolbox.core.resources.icons.Line
import com.t8rin.imagetoolbox.core.resources.icons.LineArrow
import com.t8rin.imagetoolbox.core.resources.icons.LineDoubleArrow
import com.t8rin.imagetoolbox.core.resources.icons.Polygon
import com.t8rin.imagetoolbox.core.resources.icons.Spray
import com.t8rin.imagetoolbox.core.resources.icons.Square
import com.t8rin.imagetoolbox.core.resources.icons.Triangle
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun DefaultDrawPathModeSettingItem(
    onValueChange: (Int) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current

    Column(modifier = modifier.container(shape = shape)) {
        TitleItem(
            text = stringResource(R.string.default_draw_path_mode),
            icon = Icons.Outlined.TouchApp,
            iconEndPadding = 14.dp,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp)
        )
        EnhancedButtonGroup(
            enabled = true,
            itemCount = ordinals.size,
            title = {},
            selectedIndex = ordinals.indexOf(settingsState.defaultDrawPathMode),
            activeButtonColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
            itemContent = {
                Icon(
                    imageVector = ordinals[it].getIcon(),
                    contentDescription = null
                )
            },
            onIndexChange = {
                onValueChange(ordinals[it])
            }
        )
    }
}

private val ordinals = listOf(
    0,
    17,
    18,
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13,
    14,
    15,
    16
)

private fun Int.getIcon(): ImageVector = when (this) {
    5 -> Icons.Rounded.LineDoubleArrow
    3 -> Icons.Rounded.FreeDoubleArrow
    0 -> Icons.Rounded.FreeDraw
    1 -> Icons.Rounded.Line
    4 -> Icons.Rounded.LineArrow
    2 -> Icons.Rounded.FreeArrow
    8 -> Icons.Rounded.RadioButtonUnchecked
    7 -> Icons.Rounded.CheckBoxOutlineBlank
    10 -> Icons.Rounded.Circle
    9 -> Icons.Rounded.Square
    6 -> Icons.Rounded.Lasso
    11 -> Icons.Rounded.Triangle
    12 -> Icons.Outlined.Triangle
    13 -> Icons.Rounded.Polygon
    14 -> Icons.Outlined.Polygon
    16 -> Icons.Rounded.StarOutline
    15 -> Icons.Rounded.Star
    17 -> Icons.Rounded.FloodFill
    18 -> Icons.Outlined.Spray
    else -> Icons.Rounded.HourglassEmpty
}