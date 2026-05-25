/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Line
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode

@Composable
fun DrawLineAngleSelector(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = ShapeDefaults.extraLarge,
    startIcon: ImageVector? = Icons.Rounded.Line
) {
    PreferenceRowSwitch(
        modifier = modifier,
        shape = shape,
        title = stringResource(R.string.show_line_angle),
        subtitle = stringResource(R.string.show_line_angle_sub),
        checked = checked,
        onClick = onCheckedChange,
        startIcon = startIcon
    )
}

fun DrawPathMode.canShowLineAngle(): Boolean {
    return this is DrawPathMode.Line ||
            this is DrawPathMode.LinePointingArrow ||
            this is DrawPathMode.DoubleLinePointingArrow
}