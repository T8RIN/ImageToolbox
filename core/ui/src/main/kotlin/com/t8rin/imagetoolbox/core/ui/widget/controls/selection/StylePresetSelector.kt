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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Loyalty
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
fun <T> StylePresetSelector(
    currentStyle: T,
    styles: List<Pair<String, T>>,
    listStateKey: String,
    summary: @Composable (T) -> String,
    onApply: (T) -> Unit,
    onSave: (String) -> Unit,
    onDelete: (String) -> Unit,
    shape: Shape = ShapeDefaults.large,
    showSelection: Boolean = true,
    state: StylePresetSelectorState = rememberStylePresetSelectorState()
) {
    val selectedName = remember(currentStyle, styles, showSelection) {
        styles.firstOrNull { showSelection && it.second == currentStyle }?.first
    }

    PreferenceItem(
        title = stringResource(R.string.style_presets),
        subtitle = selectedName ?: stringResource(R.string.style_presets_sub),
        onClick = { state.showSheet = true },
        shape = shape,
        startIcon = Icons.Outlined.Loyalty,
        endIcon = Icons.Rounded.MiniEdit,
        modifier = Modifier.fillMaxWidth()
    )

    StylePresetsSheet(
        visible = state.showSheet,
        currentStyle = currentStyle,
        styles = styles,
        selectedName = selectedName,
        showSelection = showSelection,
        listStateKey = listStateKey,
        summary = summary,
        onDismiss = { state.showSheet = false },
        onApply = onApply,
        onSave = onSave,
        onWantDelete = {
            state.styleToDelete = it
            state.showDeleteDialog = true
        }
    )

    DeleteStylePresetDialog(
        name = state.styleToDelete,
        visible = state.showDeleteDialog,
        onDismiss = { state.showDeleteDialog = false },
        onConfirm = onDelete
    )
}