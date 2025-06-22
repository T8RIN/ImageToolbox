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

package com.t8rin.imagetoolbox.feature.erase_background.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Healing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun RecoverModeCard(
    modifier: Modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    PreferenceRowSwitch(
        modifier = modifier,
        shape = ShapeDefaults.extraLarge,
        enabled = enabled,
        title = stringResource(R.string.restore_background),
        subtitle = stringResource(R.string.restore_background_sub),
        startIcon = Icons.Rounded.Healing,
        checked = selected,
        onClick = {
            onClick()
        }
    )
}

@Composable
fun RecoverModeButton(
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EnhancedIconButton(
        modifier = modifier,
        enabled = enabled,
        containerColor = animateColorAsState(
            if (selected) MaterialTheme.colorScheme.mixedContainer
            else Color.Transparent
        ).value,
        contentColor = animateColorAsState(
            if (selected) MaterialTheme.colorScheme.onMixedContainer
            else MaterialTheme.colorScheme.onSurface
        ).value,
        borderColor = MaterialTheme.colorScheme.outlineVariant(
            luminance = 0.1f
        ),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Rounded.Healing,
            contentDescription = "Brush"
        )
    }
}