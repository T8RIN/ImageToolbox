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

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasBottomBar
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasControlActions
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasControls
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasEditor
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasEffectDependencies
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasEffects
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasOverlays
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasPersistentActions
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.VectorCanvasQuickActions
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.rememberVectorCanvasPickerActions
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasInitialColors
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasScreenContext
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.rememberVectorCanvasUiState
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.screenLogic.VectorCanvasComponent
import io.ak1.drawbox.domain.model.State
import io.ak1.drawbox.presentation.viewmodel.rememberDrawBoxController

@Composable
fun VectorCanvasContent(
    component: VectorCanvasComponent
) {
    val canvasColor = MaterialTheme.colorScheme.surface
    val strokeColor = MaterialTheme.colorScheme.primary
    val controller = rememberDrawBoxController(
        initialState = remember {
            State(
                bgColor = canvasColor,
                strokeColor = strokeColor,
                strokeWidth = 8f
            )
        }
    )
    val state by controller.state.collectAsState()
    val uiState = rememberVectorCanvasUiState()
    val patternTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
    val patternPainter = uiState.pattern.drawable?.let { painterResource(it) }

    val context = VectorCanvasScreenContext(
        component = component,
        state = state,
        controller = controller,
        uiState = uiState
    )
    val pickerActions = rememberVectorCanvasPickerActions(context)

    VectorCanvasEffects(
        state = state,
        dependencies = VectorCanvasEffectDependencies(
            patternPainter = patternPainter,
            patternTint = patternTint,
            controller = controller,
            component = component,
            uiState = uiState
        )
    )

    val controls = remember(context.controller, pickerActions) {
        VectorCanvasControlActions(
            onInsertImage = pickerActions.insertImage,
            onExportSvg = context.controller::exportSvg,
            onExportJson = context.controller::exportJson,
            onImportJson = pickerActions.importJson,
            onEditText = context.uiState::beginTextEdit
        )
    }
    val component = context.component

    AdaptiveBottomScaffoldLayoutScreen(
        title = {
            Text(
                text = stringResource(R.string.vector_canvas),
                modifier = Modifier.marquee()
            )
        },
        onGoBack = context::handleBack,
        shouldDisableBackHandler = !component.haveChanges,
        actions = {
            VectorCanvasQuickActions(
                mode = state.mode,
                controller = controller
            )
        },
        topAppBarPersistentActions = {
            VectorCanvasPersistentActions(context)
        },
        mainContent = {
            VectorCanvasEditor(
                state = state,
                controller = controller,
                uiState = uiState,
                modifier = Modifier.fillMaxSize()
            )
        },
        controls = {
            VectorCanvasControls(
                state = state,
                controller = controller,
                uiState = uiState,
                actions = controls
            )
        },
        buttons = { actions ->
            VectorCanvasBottomBar(
                onSave = {
                    uiState.pendingPngLocation = null
                    controller.saveBitmap()
                },
                onSaveLongClick = { uiState.showSaveLocationDialog = true },
                actions = actions
            )
        },
        canShowScreenData = true,
        showActionsInTopAppBar = false,
        autoClearFocus = false,
        enableNoDataScroll = false,
        mainContentWeight = 0.7f
    )

    VectorCanvasOverlays(
        context = context,
        initialColors = VectorCanvasInitialColors(canvasColor, strokeColor)
    )
}
