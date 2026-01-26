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

package com.t8rin.imagetoolbox.feature.cipher.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shuffle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.model.CipherType
import com.t8rin.imagetoolbox.core.domain.utils.toInt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.Green
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.cipher.presentation.screenLogic.CipherComponent
import kotlin.random.Random

@Composable
internal fun CipherControls(component: CipherComponent) {
    val settingsState = LocalSettingsState.current
    val isPortrait by isPortraitOrientationAsState()
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val saveLauncher = rememberFileCreator(
        onSuccess = { uri ->
            component.saveCryptographyTo(
                uri = uri,
                onResult = essentials::parseFileSaveResult
            )
        }
    )

    val filename = component.uri?.let {
        rememberFilename(it)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isPortrait) Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .container(ShapeDefaults.circle)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                stringResource(R.string.encryption),
                stringResource(R.string.decryption)
            )
            EnhancedButtonGroup(
                enabled = true,
                itemCount = items.size,
                selectedIndex = (!component.isEncrypt).toInt(),
                onIndexChange = {
                    component.setIsEncrypt(it == 0)
                },
                itemContent = {
                    Text(
                        text = items[it],
                        fontSize = 12.sp
                    )
                },
                isScrollable = false,
                modifier = Modifier.weight(1f)
            )
            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = component::showTip
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                    contentDescription = "Info"
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        PreferenceItem(
            modifier = Modifier,
            title = filename ?: stringResource(R.string.something_went_wrong),
            onClick = null,
            titleFontStyle = LocalTextStyle.current.copy(
                lineHeight = 16.sp,
                fontSize = 15.sp
            ),
            subtitle = component.uri?.let {
                stringResource(
                    id = R.string.size,
                    rememberHumanFileSize(it)
                )
            },
            startIcon = Icons.AutoMirrored.Rounded.InsertDriveFile
        )
        Spacer(Modifier.height(16.dp))
        RoundedTextField(
            modifier = Modifier
                .container(
                    shape = MaterialTheme.shapes.large,
                    resultPadding = 8.dp
                ),
            value = component.key,
            startIcon = {
                EnhancedIconButton(
                    onClick = {
                        component.updateKey(component.generateRandomPassword())
                    },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = stringResource(R.string.shuffle),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            },
            endIcon = {
                EnhancedIconButton(
                    onClick = { component.updateKey("") },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = stringResource(R.string.cancel),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = false,
            onValueChange = component::updateKey,
            label = {
                Text(stringResource(R.string.key))
            }
        )
        AnimatedVisibility(visible = component.byteArray != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .container(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme
                            .colorScheme
                            .surfaceContainerHighest,
                        resultPadding = 0.dp
                    )
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = Green,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = ShapeDefaults.circle
                            )
                            .border(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(),
                                shape = ShapeDefaults.circle
                            )
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        stringResource(R.string.file_proceed),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = stringResource(R.string.store_file_desc),
                    fontSize = 13.sp,
                    color = LocalContentColor.current.copy(alpha = 0.7f),
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                var name by rememberSaveable(component.byteArray, filename) {
                    mutableStateOf(
                        if (component.isEncrypt) {
                            "enc-"
                        } else {
                            "dec-"
                        } + (filename ?: Random.nextInt())
                    )
                }
                RoundedTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .container(
                            shape = MaterialTheme.shapes.large,
                            resultPadding = 8.dp
                        ),
                    value = name,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = false,
                    onValueChange = { name = it },
                    label = {
                        Text(stringResource(R.string.filename))
                    }
                )

                Row(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    EnhancedButton(
                        onClick = {
                            saveLauncher.make(name)
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .fillMaxWidth(0.5f)
                            .height(50.dp),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FileDownload,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AutoSizeText(
                                text = stringResource(id = R.string.save),
                                maxLines = 1
                            )
                        }
                    }
                    EnhancedButton(
                        onClick = {
                            component.byteArray?.let {
                                component.shareFile(
                                    it = it,
                                    filename = name,
                                    onComplete = showConfetti
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = stringResource(
                                    R.string.share
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AutoSizeText(
                                text = stringResource(id = R.string.share),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        DataSelector(
            modifier = Modifier,
            value = component.cipherType,
            containerColor = Color.Unspecified,
            spanCount = 5,
            selectedItemColor = MaterialTheme.colorScheme.secondary,
            onValueChange = component::updateCipherType,
            entries = CipherType.entries,
            title = stringResource(R.string.algorithms),
            titleIcon = Icons.Rounded.Key,
            itemContentText = {
                it.name
            },
            initialExpanded = true
        )
    }
}