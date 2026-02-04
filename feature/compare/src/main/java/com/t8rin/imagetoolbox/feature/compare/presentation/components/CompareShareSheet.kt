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

package com.t8rin.imagetoolbox.feature.compare.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults.bottom
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults.center
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults.top
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem


@Composable
internal fun CompareShareSheet(
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    onSaveBitmap: (ImageFormat, String?) -> Unit,
    onShare: (ImageFormat) -> Unit,
    onCopy: (ImageFormat) -> Unit,
    previewData: Any?,
    transformations: List<Transformation>
) {
    EnhancedModalBottomSheet(
        sheetContent = {
            var imageFormat by remember { mutableStateOf<ImageFormat>(ImageFormat.Png.Lossless) }
            Box {
                Column(
                    modifier = Modifier.enhancedVerticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .padding(
                                bottom = 8.dp,
                                start = 4.dp,
                                end = 4.dp,
                                top = 16.dp
                            )
                            .height(100.dp)
                            .width(120.dp)
                            .container(
                                shape = MaterialTheme.shapes.extraLarge,
                                resultPadding = 0.dp
                            )
                    ) {
                        Picture(
                            model = previewData,
                            transformations = transformations,
                            shape = RectangleShape,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    ImageFormatSelector(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        value = imageFormat,
                        forceEnabled = true,
                        onValueChange = { imageFormat = it }
                    )
                    Spacer(Modifier.height(8.dp))
                    var showFolderSelectionDialog by rememberSaveable(visible) {
                        mutableStateOf(false)
                    }
                    PreferenceItem(
                        title = stringResource(id = R.string.save),
                        onClick = {
                            onSaveBitmap(imageFormat, null)
                        },
                        onLongClick = {
                            showFolderSelectionDialog = true
                        },
                        shape = top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        endIcon = Icons.Rounded.Save
                    )
                    OneTimeSaveLocationSelectionDialog(
                        visible = showFolderSelectionDialog,
                        onDismiss = { showFolderSelectionDialog = false },
                        onSaveRequest = {
                            onSaveBitmap(imageFormat, it)
                        },
                        formatForFilenameSelection = imageFormat
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        title = stringResource(id = R.string.copy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = center,
                        onClick = {
                            onCopy(imageFormat)
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        endIcon = Icons.Rounded.ContentCopy
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        title = stringResource(id = R.string.share),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = bottom,
                        onClick = {
                            onShare(imageFormat)
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        endIcon = Icons.Rounded.Share
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    onVisibleChange(false)
                }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.share),
                icon = Icons.Rounded.IosShare
            )
        },
        onDismiss = onVisibleChange,
        visible = visible
    )
}