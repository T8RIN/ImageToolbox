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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.GridOn
import com.t8rin.imagetoolbox.core.resources.icons.Image
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasPattern

@Composable
internal fun VectorCanvasBackgroundControls(
    backgroundColor: Color,
    pattern: VectorCanvasPattern,
    showGrid: Boolean,
    actions: VectorCanvasBackgroundActions,
    modifier: Modifier = Modifier
) {
    ColorRowSelector(
        value = backgroundColor,
        onValueChange = actions.onBackgroundColorChange,
        title = stringResource(R.string.background_color),
        icon = Icons.Outlined.BackgroundColor,
        allowAlpha = false,
        modifier = modifier.container(ShapeDefaults.extraLarge)
    )
    PatternSelector(
        value = pattern,
        onValueChange = actions.onPatternChange,
        modifier = modifier
    )
    PreferenceRowSwitch(
        title = stringResource(R.string.vector_canvas_show_grid),
        checked = showGrid,
        startIcon = Icons.Outlined.GridOn,
        onClick = actions.onShowGridChange,
        modifier = modifier
    )
    PreferenceItem(
        title = stringResource(R.string.vector_canvas_insert_image),
        subtitle = stringResource(R.string.vector_canvas_insert_image_sub),
        startIcon = Icons.Outlined.Image,
        onClick = actions.onInsertImage,
        modifier = modifier,
        shape = ShapeDefaults.extraLarge
    )
}

@Composable
private fun PatternSelector(
    value: VectorCanvasPattern,
    onValueChange: (VectorCanvasPattern) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.container(ShapeDefaults.extraLarge),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleItem(
            text = stringResource(R.string.vector_canvas_background_pattern),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VectorCanvasPattern.entries.forEach { pattern ->
                PatternItem(
                    pattern = pattern,
                    selected = pattern == value,
                    onClick = { onValueChange(pattern) }
                )
            }
        }
    }
}

@Composable
private fun PatternItem(
    pattern: VectorCanvasPattern,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = ShapeDefaults.small,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = BorderStroke(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            )
        ) {
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                val drawable = pattern.drawable
                if (drawable == null) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Image(
                        painter = painterResource(drawable),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                        ),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
        Text(
            text = stringResource(pattern.title),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.height(32.dp)
        )
    }
}

internal data class VectorCanvasBackgroundActions(
    val onBackgroundColorChange: (Color) -> Unit,
    val onPatternChange: (VectorCanvasPattern) -> Unit,
    val onShowGridChange: (Boolean) -> Unit,
    val onInsertImage: () -> Unit
)
