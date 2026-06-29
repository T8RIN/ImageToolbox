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

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Redo
import com.t8rin.imagetoolbox.core.resources.icons.Undo
import com.t8rin.imagetoolbox.core.ui.widget.buttons.EraseModeButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.PanModeButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import io.ak1.drawbox.domain.model.Mode
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

@Composable
internal fun VectorCanvasQuickActions(
    mode: Mode,
    controller: DrawBoxController
) {
    val canUndo by controller.canUndo.collectAsState()
    val canRedo by controller.canRedo.collectAsState()
    var restoreMode by remember { mutableStateOf<Mode>(Mode.PEN) }

    LaunchedEffect(mode) {
        if (mode != Mode.PAN && mode != Mode.ERASER) restoreMode = mode
    }

    PanModeButton(
        selected = mode == Mode.PAN,
        onClick = {
            controller.setMode(if (mode == Mode.PAN) restoreMode else Mode.PAN)
        }
    )
    EnhancedIconButton(
        onClick = controller::undo,
        enabled = canUndo
    ) {
        Icon(
            imageVector = Icons.Rounded.Undo,
            contentDescription = stringResource(R.string.vector_canvas_undo)
        )
    }
    EnhancedIconButton(
        onClick = controller::redo,
        enabled = canRedo
    ) {
        Icon(
            imageVector = Icons.Rounded.Redo,
            contentDescription = stringResource(R.string.vector_canvas_redo)
        )
    }
    EraseModeButton(
        selected = mode == Mode.ERASER,
        enabled = mode != Mode.PAN,
        onClick = {
            controller.setMode(if (mode == Mode.ERASER) restoreMode else Mode.ERASER)
        }
    )
}
