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

package ru.tech.imageresizershrinker.core.ui.widget.palette_selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.PaletteStyle
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem

@Composable
fun PaletteStyleSelectionItem(
    style: PaletteStyle,
    onClick: () -> Unit
) {
    val settingsState = LocalSettingsState.current
    val selected = settingsState.themeStyle == style
    val context = LocalContext.current

    PreferenceItem(
        onClick = onClick,
        title = style.getTitle(context),
        subtitle = style.getSubtitle(context),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(
            alpha = animateFloatAsState(
                if (selected) 0.7f
                else 0.2f
            ).value
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = settingsState.borderWidth,
                color = animateColorAsState(
                    if (selected) MaterialTheme.colorScheme
                        .onSecondaryContainer
                        .copy(alpha = 0.5f)
                    else Color.Transparent
                ).value,
                shape = RoundedCornerShape(16.dp)
            ),
        endIcon = if (selected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked
    )
}