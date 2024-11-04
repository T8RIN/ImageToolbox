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

package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.MiniEdit
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem

@Composable
fun ShareButton(
    enabled: Boolean = true,
    onShare: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onCopy: ((ClipboardManager) -> Unit)? = null
) {
    var showSelectionDialog by rememberSaveable {
        mutableStateOf(false)
    }

    EnhancedIconButton(
        containerColor = Color.Transparent,
        contentColor = LocalContentColor.current,
        enableAutoShadowAndBorder = false,
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
            Text(stringResource(R.string.image))
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = null
            )
        },
        text = {
            val clipboardManager = LocalClipboardManager.current

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PreferenceItem(
                    title = stringResource(R.string.share),
                    shape = ContainerShapeDefaults.topShape,
                    startIcon = Icons.Rounded.Share,
                    onClick = {
                        showSelectionDialog = false
                        onShare()
                    },
                    titleFontStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                )
                if (onCopy != null) {
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        title = stringResource(R.string.copy),
                        shape = if (onEdit == null) ContainerShapeDefaults.bottomShape
                        else ContainerShapeDefaults.centerShape,
                        startIcon = Icons.Rounded.ContentCopy,
                        onClick = {
                            showSelectionDialog = false
                            onCopy(clipboardManager)
                        },
                        titleFontStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                if (onEdit != null) {
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        title = stringResource(R.string.edit),
                        shape = ContainerShapeDefaults.bottomShape,
                        startIcon = Icons.Rounded.MiniEdit,
                        onClick = {
                            showSelectionDialog = false
                            onEdit()
                        },
                        titleFontStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    )
}