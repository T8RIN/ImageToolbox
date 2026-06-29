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

@file:Suppress("FunctionNaming", "PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasUiState
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.screenLogic.VectorCanvasComponent

@Composable
internal fun VectorCanvasDialogs(
    component: VectorCanvasComponent,
    uiState: VectorCanvasUiState,
    onClear: () -> Unit,
    onSaveTo: (String?) -> Unit
) {
    LoadingDialog(
        visible = component.isBusy,
        onCancelLoading = component::cancel,
        canCancel = true
    )
    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { uiState.showExitDialog = false },
        visible = uiState.showExitDialog
    )
    ResetDialog(
        visible = uiState.showClearDialog,
        onDismiss = { uiState.showClearDialog = false },
        onReset = onClear,
        title = stringResource(R.string.vector_canvas_clear),
        text = stringResource(R.string.vector_canvas_clear_sub),
        icon = Icons.Outlined.Delete
    )
    OneTimeSaveLocationSelectionDialog(
        visible = uiState.showSaveLocationDialog,
        onDismiss = { uiState.showSaveLocationDialog = false },
        onSaveRequest = onSaveTo,
        formatForFilenameSelection = component.pngFormat
    )
}
