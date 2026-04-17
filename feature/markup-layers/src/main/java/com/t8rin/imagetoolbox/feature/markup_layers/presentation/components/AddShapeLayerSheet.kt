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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.StarSticky
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ShapeMode
import com.t8rin.imagetoolbox.feature.markup_layers.domain.defaultMode
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.icon

@Composable
internal fun AddShapeLayerSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onShapePicked: (ShapeMode) -> Unit
) {
    val shapeKinds = remember {
        ShapeMode.Kind.entries
    }

    EnhancedModalBottomSheet(
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.shape),
                icon = Icons.Outlined.StarSticky
            )
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        }
    ) {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            shapeKinds.forEach { kind ->
                EnhancedChip(
                    selected = false,
                    selectedColor = MaterialTheme.colorScheme.surface,
                    unselectedContentColor = MaterialTheme.colorScheme.surface,
                    label = {
                        Icon(
                            imageVector = kind.icon,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onShapePicked(kind.defaultMode())
                    }
                )
            }
        }
    }
}