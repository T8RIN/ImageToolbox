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

package com.t8rin.imagetoolbox.feature.shader_studio.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Code
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.DownloadFile
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.resources.icons.IosShare
import com.t8rin.imagetoolbox.core.resources.icons.MoreVert
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic.ShaderStudioComponent

@Composable
internal fun SavedShadersButton(
    component: ShaderStudioComponent,
    onClick: () -> Unit
) {
    val presets by component.presets.collectAsStateWithLifecycle()

    if (presets.isNotEmpty()) {
        PreferenceItemOverload(
            title = stringResource(R.string.saved_shaders),
            subtitle = stringResource(R.string.shader_library_sub),
            startIcon = {
                Icon(
                    imageVector = Icons.Rounded.Code,
                    contentDescription = null
                )
            },
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun ShaderLibrarySheet(
    visible: Boolean,
    component: ShaderStudioComponent,
    onDismiss: () -> Unit,
    onExportPreset: (ShaderPreset) -> Unit
) {
    val presets by component.presets.collectAsStateWithLifecycle()

    LaunchedEffect(presets) {
        if (presets.isEmpty()) onDismiss()
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.saved_shaders),
                icon = Icons.Rounded.Code
            )
        }
    ) {
        ShaderLibrary(
            presets = presets,
            onEdit = {
                component.editPreset(it)
                onDismiss()
            },
            onDuplicate = component::duplicatePreset,
            onDelete = component::deletePreset,
            onShare = component::sharePreset,
            onExport = onExportPreset
        )
    }
}

@Composable
private fun ShaderLibrary(
    presets: List<ShaderPreset>,
    onEdit: (ShaderPreset) -> Unit,
    onDuplicate: (ShaderPreset) -> Unit,
    onDelete: (ShaderPreset) -> Unit,
    onShare: (ShaderPreset) -> Unit,
    onExport: (ShaderPreset) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .enhancedVerticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        presets.forEachIndexed { index, preset ->
            var showMenu by rememberSaveable(preset.name) { mutableStateOf(false) }

            PreferenceItemOverload(
                title = preset.name,
                subtitle = stringResource(R.string.shader_params_count, preset.params.size),
                endIcon = {
                    Box {
                        EnhancedIconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null
                            )
                        }
                        EnhancedDropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            ShaderMenuAction(R.string.edit, Icons.Rounded.EditAlt) {
                                showMenu = false
                                onEdit(preset)
                            }
                            ShaderMenuAction(R.string.duplicate, Icons.Rounded.ContentCopy) {
                                showMenu = false
                                onDuplicate(preset)
                            }
                            ShaderMenuAction(R.string.export, Icons.Outlined.DownloadFile) {
                                showMenu = false
                                onExport(preset)
                            }
                            ShaderMenuAction(R.string.share, Icons.Rounded.IosShare) {
                                showMenu = false
                                onShare(preset)
                            }
                            ShaderMenuAction(R.string.delete, Icons.Rounded.Delete) {
                                showMenu = false
                                onDelete(preset)
                            }
                        }
                    }
                },
                onClick = { onEdit(preset) },
                shape = ShapeDefaults.byIndex(index, presets.size),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ShaderMenuAction(
    title: Int,
    icon: ImageVector,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(stringResource(title)) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        onClick = onClick
    )
}
