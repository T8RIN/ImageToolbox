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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.UiFontFamily
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ProvideTypography
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
fun FontSelectionItem(
    font: UiFontFamily,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = ShapeDefaults.default
) {
    val settingsState = LocalSettingsState.current
    val (_, name, isVariable) = font
    val selected = font == settingsState.font
    ProvideTypography(font) {
        PreferenceItem(
            onClick = onClick,
            onLongClick = onLongClick,
            title = (name ?: stringResource(id = R.string.system)) + isVariable.toVariable(),
            subtitle = stringResource(R.string.alphabet_and_numbers),
            containerColor = takeColorFromScheme {
                if (selected) secondaryContainer
                else SafeLocalContainerColor
            },
            modifier = modifier
                .border(
                    width = settingsState.borderWidth,
                    color = animateColorAsState(
                        if (selected) MaterialTheme
                            .colorScheme
                            .onSecondaryContainer
                            .copy(alpha = 0.5f)
                        else Color.Transparent
                    ).value,
                    shape = ShapeDefaults.default
                ),
            shape = shape,
            endIcon = if (selected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked
        )
    }
}

@Composable
private fun Boolean?.toVariable(): String {
    if (this == null || this) return ""
    return " (${stringResource(R.string.regular)})"
}
