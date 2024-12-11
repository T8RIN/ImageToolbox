/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.markup_layers.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.resources.icons.Stacks
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.feature.markup_layers.domain.LayerType
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.model.UiMarkupLayer
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent

@Composable
internal fun MarkupLayersActions(
    component: MarkupLayersComponent,
    showLayersSelection: Boolean,
    onToggleLayersSection: () -> Unit
) {
    val layerImagePicker = rememberImagePicker { uri: Uri ->
        component.deactivateAllLayers()
        component.addLayer(
            UiMarkupLayer(
                type = LayerType.Image(uri)
            )
        )
    }
    var showTextEnteringDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Box {
        EnhancedIconButton(
            containerColor = takeColorFromScheme {
                if (showLayersSelection) tertiary
                else Color.Transparent
            },
            onClick = onToggleLayersSection,
            enabled = component.layers.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.Rounded.Stacks,
                contentDescription = null
            )
        }
    }
    EnhancedIconButton(
        onClick = {
            showTextEnteringDialog = true
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.TextFields,
            contentDescription = null
        )
    }
    EnhancedIconButton(
        onClick = layerImagePicker::pickImage
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = null
        )
    }

    AddTextLayerDialog(
        visible = showTextEnteringDialog,
        onDismiss = { showTextEnteringDialog = false },
        onAddLayer = {
            component.deactivateAllLayers()
            component.addLayer(it)
        }
    )
}