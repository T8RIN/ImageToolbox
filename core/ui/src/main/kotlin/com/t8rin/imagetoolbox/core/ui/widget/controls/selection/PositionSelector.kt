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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun PositionSelector(
    value: Position,
    onValueChange: (Position) -> Unit,
    entries: List<Position> = Position.entries,
    modifier: Modifier = Modifier,
    shape: Shape = ShapeDefaults.large,
    color: Color = MaterialTheme.colorScheme.surface,
    selectedItemColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    DataSelector(
        value = value,
        onValueChange = onValueChange,
        entries = entries,
        spanCount = 2,
        title = stringResource(R.string.position),
        titleIcon = Icons.Outlined.Place,
        itemContentText = { it.translatedName },
        modifier = modifier,
        shape = shape,
        color = color,
        selectedItemColor = selectedItemColor
    )
}

private val Position.translatedName: String
    @Composable
    get() = when (this) {
        Position.Center -> stringResource(id = R.string.center)
        Position.TopLeft -> stringResource(id = R.string.top_left)
        Position.TopRight -> stringResource(id = R.string.top_right)
        Position.BottomLeft -> stringResource(id = R.string.bottom_left)
        Position.BottomRight -> stringResource(id = R.string.bottom_right)
        Position.TopCenter -> stringResource(id = R.string.top_center)
        Position.CenterRight -> stringResource(id = R.string.center_right)
        Position.BottomCenter -> stringResource(id = R.string.bottom_center)
        Position.CenterLeft -> stringResource(id = R.string.center_left)
    }