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

package com.t8rin.imagetoolbox.core.ui.widget.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults

@Composable
fun ShareButton(
    enabled: Boolean = true,
    onShare: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    dialogTitle: String = stringResource(R.string.image),
    dialogIcon: ImageVector = Icons.Outlined.Image
) {
    var showSelectionDialog by rememberSaveable {
        mutableStateOf(false)
    }

    EnhancedIconButton(
        onClick = {
            if (onCopy != null || onEdit != null) {
                showSelectionDialog = true
            } else {
                onShare()
            }
        },
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Rounded.Share,
            contentDescription = stringResource(R.string.share)
        )
    }

    EnhancedAlertDialog(
        visible = showSelectionDialog && (onEdit != null || onCopy != null),
        onDismissRequest = { showSelectionDialog = false },
        confirmButton = {
            EnhancedButton(
                onClick = { showSelectionDialog = false },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = {
            Text(dialogTitle)
        },
        icon = {
            Icon(
                imageVector = dialogIcon,
                contentDescription = null
            )
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fadingEdges(
                        scrollableState = scrollState,
                        isVertical = true
                    )
                    .enhancedVerticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PreferenceItem(
                    title = stringResource(R.string.share),
                    shape = ShapeDefaults.top,
                    startIcon = Icons.Rounded.Share,
                    onClick = {
                        showSelectionDialog = false
                        onShare()
                    },
                    titleFontStyle = PreferenceItemDefaults.TitleFontStyleCentered
                )
                if (onCopy != null) {
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        title = stringResource(R.string.copy),
                        shape = if (onEdit == null) ShapeDefaults.bottom
                        else ShapeDefaults.center,
                        startIcon = Icons.Rounded.ContentCopy,
                        onClick = {
                            showSelectionDialog = false
                            onCopy()
                        },
                        titleFontStyle = PreferenceItemDefaults.TitleFontStyleCentered
                    )
                }
                if (onEdit != null) {
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        title = stringResource(R.string.edit),
                        shape = ShapeDefaults.bottom,
                        startIcon = Icons.Rounded.MiniEdit,
                        onClick = {
                            showSelectionDialog = false
                            onEdit()
                        },
                        titleFontStyle = PreferenceItemDefaults.TitleFontStyleCentered
                    )
                }
            }
        }
    )
}