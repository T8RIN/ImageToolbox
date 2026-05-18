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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.RemoveCircle
import com.t8rin.imagetoolbox.core.resources.icons.Texture
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun SeamCarvingMaskItem(
    maskUri: String,
    useMaskAsRemoval: Boolean,
    onUseMaskAsRemovalChange: (Boolean) -> Unit,
    onAddMask: () -> Unit,
    onRemoveMask: () -> Unit
) {
    if (maskUri.isEmpty()) {
        EnhancedButton(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            onClick = onAddMask,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Texture,
                contentDescription = stringResource(R.string.add_mask)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(id = R.string.add_mask))
        }
    } else {
        var showMaskRemoveDialog by rememberSaveable { mutableStateOf(false) }

        PreferenceRowSwitch(
            title = stringResource(R.string.seam_carving_mask_as_removal),
            subtitle = stringResource(R.string.seam_carving_mask_as_removal_sub),
            checked = useMaskAsRemoval,
            onClick = onUseMaskAsRemovalChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            applyHorizontalPadding = false,
            startContent = {},
            resultModifier = Modifier.padding(16.dp),
            containerColor = Color.Unspecified,
            shape = ShapeDefaults.extraLarge
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
                .container(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                    shape = ShapeDefaults.extraLarge
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(4.dp))
            SeamCarvingMaskPreview(
                maskUri = maskUri,
                modifier = Modifier.size(42.dp)
            )
            Text(
                text = stringResource(R.string.mask),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(Modifier.weight(1f))
            EnhancedIconButton(
                onClick = { showMaskRemoveDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Outlined.RemoveCircle,
                    contentDescription = stringResource(R.string.remove)
                )
            }
        }

        EnhancedAlertDialog(
            visible = showMaskRemoveDialog,
            onDismissRequest = { showMaskRemoveDialog = false },
            confirmButton = {
                EnhancedButton(
                    onClick = { showMaskRemoveDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            dismissButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        showMaskRemoveDialog = false
                        onRemoveMask()
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            title = {
                Text(stringResource(R.string.delete_mask))
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SeamCarvingMaskPreview(
                        maskUri = maskUri,
                        modifier = Modifier.sizeIn(
                            maxHeight = 80.dp,
                            maxWidth = 80.dp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.delete_mask_warn))
                }
            }
        )
    }
}

@Composable
private fun SeamCarvingMaskPreview(
    maskUri: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant(),
                shape = ShapeDefaults.extraSmall
            )
            .clip(ShapeDefaults.extraSmall)
    ) {
        AsyncImage(
            model = maskUri,
            contentDescription = stringResource(R.string.mask),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}