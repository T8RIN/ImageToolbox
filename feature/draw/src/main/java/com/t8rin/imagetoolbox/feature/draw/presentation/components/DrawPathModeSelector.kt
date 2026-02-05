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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.ArrowParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.DrawPathModeInfoSheet
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.FloodFillParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.OvalParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.PolygonParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.RectParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.SprayParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.StarParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.TriangleParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.getIcon
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.saveState

@Composable
fun DrawPathModeSelector(
    modifier: Modifier,
    values: List<DrawPathMode> = DrawPathMode.entries,
    value: DrawPathMode,
    onValueChange: (DrawPathMode) -> Unit,
    drawMode: DrawMode,
    containerColor: Color = Color.Unspecified
) {
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(value, values) {
        if (values.find { it::class.isInstance(value) } == null) {
            values.firstOrNull()?.let { onValueChange(it) }
        }
    }

    Column(
        modifier = modifier
            .container(
                shape = ShapeDefaults.extraLarge,
                color = containerColor
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EnhancedButtonGroup(
            enabled = true,
            itemCount = values.size,
            title = {
                Text(
                    text = stringResource(R.string.draw_path_mode),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                SupportingButton(
                    onClick = {
                        isSheetVisible = true
                    }
                )
            },
            selectedIndex = remember(values, value) {
                derivedStateOf {
                    values.indexOfFirst {
                        value::class.isInstance(it)
                    }
                }
            }.value,
            activeButtonColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            itemContent = {
                Icon(
                    imageVector = values[it].getIcon(),
                    contentDescription = null
                )
            },
            onIndexChange = {
                onValueChange(values[it].saveState(value))
            }
        )

        val canChangeFillColor =
            value is DrawPathMode.Outlined && (drawMode is DrawMode.Pen || drawMode is DrawMode.Highlighter || drawMode is DrawMode.Neon)

        PolygonParamsSelector(
            value = value,
            onValueChange = onValueChange,
            canChangeFillColor = canChangeFillColor
        )

        StarParamsSelector(
            value = value,
            onValueChange = onValueChange,
            canChangeFillColor = canChangeFillColor
        )

        RectParamsSelector(
            value = value,
            onValueChange = onValueChange,
            canChangeFillColor = canChangeFillColor
        )

        OvalParamsSelector(
            value = value,
            onValueChange = onValueChange,
            canChangeFillColor = canChangeFillColor
        )

        TriangleParamsSelector(
            value = value,
            onValueChange = onValueChange,
            canChangeFillColor = canChangeFillColor
        )

        ArrowParamsSelector(
            value = value,
            onValueChange = onValueChange
        )

        FloodFillParamsSelector(
            value = value,
            onValueChange = onValueChange
        )

        SprayParamsSelector(
            value = value,
            onValueChange = onValueChange
        )
    }

    DrawPathModeInfoSheet(
        visible = isSheetVisible,
        onDismiss = { isSheetVisible = false },
        values = values
    )
}