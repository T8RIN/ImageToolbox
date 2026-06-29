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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SettingsBackupRestore
import com.t8rin.imagetoolbox.core.resources.icons.ZoomIn
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import io.ak1.drawbox.domain.model.Viewport
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

@Composable
internal fun VectorCanvasViewportControls(
    viewport: Viewport,
    focalPoint: Offset,
    controller: DrawBoxController,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        EnhancedSliderItem(
            value = viewport.scale,
            title = stringResource(R.string.vector_canvas_zoom),
            icon = Icons.Outlined.ZoomIn,
            valueRange = 0.1f..4f,
            valueSuffix = "×",
            onValueChange = { controller.zoomTo(it, focalPoint) },
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.top
        )
        PreferenceItem(
            title = stringResource(R.string.vector_canvas_reset_view),
            startIcon = Icons.Rounded.SettingsBackupRestore,
            onClick = controller::resetCamera,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.bottom
        )
    }
}
