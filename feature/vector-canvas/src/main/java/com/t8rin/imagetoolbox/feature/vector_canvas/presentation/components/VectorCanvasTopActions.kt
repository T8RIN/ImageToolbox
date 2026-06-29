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
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Layers
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton

@Composable
internal fun VectorCanvasTopActions(
    hasElements: Boolean,
    hasSelection: Boolean,
    onShowLayers: () -> Unit,
    onDelete: () -> Unit
) {
    EnhancedIconButton(
        onClick = onShowLayers,
        enabled = hasElements
    ) {
        Icon(
            imageVector = Icons.Rounded.Layers,
            contentDescription = stringResource(R.string.vector_canvas_layers)
        )
    }
    EnhancedIconButton(
        onClick = onDelete,
        enabled = hasElements
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = stringResource(
                if (hasSelection) R.string.delete else R.string.vector_canvas_clear
            )
        )
    }
}
