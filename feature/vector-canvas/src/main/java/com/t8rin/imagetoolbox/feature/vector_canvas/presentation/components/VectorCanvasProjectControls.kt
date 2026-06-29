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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FileExport
import com.t8rin.imagetoolbox.core.resources.icons.FileImport
import com.t8rin.imagetoolbox.core.resources.icons.VectorPolyline
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
internal fun VectorCanvasProjectControls(
    actions: VectorCanvasProjectActions,
    canReplay: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        PreferenceItem(
            title = stringResource(R.string.vector_canvas_export_svg),
            subtitle = stringResource(R.string.vector_canvas_export_svg_sub),
            startIcon = Icons.Outlined.VectorPolyline,
            onClick = actions.onExportSvg,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.top
        )
        PreferenceItem(
            title = stringResource(R.string.vector_canvas_export_json),
            subtitle = stringResource(R.string.vector_canvas_export_json_sub),
            startIcon = Icons.Rounded.FileExport,
            onClick = actions.onExportJson,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.center
        )
        PreferenceItem(
            title = stringResource(R.string.vector_canvas_import_json),
            subtitle = stringResource(R.string.vector_canvas_import_json_sub),
            startIcon = Icons.Rounded.FileImport,
            onClick = actions.onImportJson,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.center
        )
    }
}

internal data class VectorCanvasProjectActions(
    val onExportSvg: () -> Unit,
    val onExportJson: () -> Unit,
    val onImportJson: () -> Unit
)
