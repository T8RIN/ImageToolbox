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

package ru.tech.imageresizershrinker.feature.draw.presentation.components.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EraseModeButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PanModeButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.feature.draw.presentation.screenLogic.DrawComponent

@Composable
internal fun DrawContentSecondaryControls(
    component: DrawComponent,
    panEnabled: Boolean,
    onTogglePanEnabled: () -> Unit,
    isEraserOn: Boolean,
    onToggleIsEraserOn: () -> Unit
) {
    PanModeButton(
        selected = panEnabled,
        onClick = onTogglePanEnabled
    )
    EnhancedIconButton(
        onClick = component::undo,
        enabled = component.lastPaths.isNotEmpty() || component.paths.isNotEmpty()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Undo,
            contentDescription = "Undo"
        )
    }
    EnhancedIconButton(
        onClick = component::redo,
        enabled = component.undonePaths.isNotEmpty()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Redo,
            contentDescription = "Redo"
        )
    }
    EraseModeButton(
        selected = isEraserOn,
        enabled = !panEnabled,
        onClick = onToggleIsEraserOn
    )
}