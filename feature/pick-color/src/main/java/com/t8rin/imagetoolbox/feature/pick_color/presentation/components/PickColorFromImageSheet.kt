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

package com.t8rin.imagetoolbox.feature.pick_color.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.ImageColorDetector
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.copyToClipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.widget.buttons.PanModeButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState
import kotlinx.coroutines.launch

@Composable
fun PickColorFromImageSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    bitmap: Bitmap?,
    onColorChange: (Color) -> Unit,
    color: Color
) {
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current

    var panEnabled by rememberSaveable { mutableStateOf(false) }

    EnhancedModalBottomSheet(
        sheetContent = {
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {
                remember(bitmap) { bitmap?.asImageBitmap() }?.let {
                    ImageColorDetector(
                        panEnabled = panEnabled,
                        color = color,
                        imageBitmap = it,
                        onColorChange = onColorChange,
                        isMagnifierEnabled = settingsState.magnifierEnabled,
                        boxModifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .container(
                                shape = ShapeDefaults.extraSmall,
                                color = MaterialTheme.colorScheme
                                    .outlineVariant()
                                    .copy(alpha = 0.1f),
                                resultPadding = 0.dp
                            )
                            .transparencyChecker()
                    )
                } ?: Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .container(
                            shape = ShapeDefaults.extraSmall,
                            color = MaterialTheme.colorScheme
                                .outlineVariant()
                                .copy(alpha = 0.1f),
                            resultPadding = 0.dp
                        )
                        .shimmer(true)
                )

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .container(
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .background(
                                        color = animateColorAsState(color).value,
                                        shape = ShapeDefaults.small
                                    )
                                    .size(40.dp)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(
                                            onTopOf = animateColorAsState(color).value
                                        ),
                                        shape = ShapeDefaults.small
                                    )
                                    .clip(ShapeDefaults.small)
                                    .hapticsClickable {
                                        context.copyToClipboard(color.toHex())
                                        scope.launch {
                                            toastHostState.showToast(
                                                icon = Icons.Rounded.ContentPaste,
                                                message = context.getString(R.string.color_copied)
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = stringResource(R.string.copy),
                                    tint = animateColorAsState(
                                        color.inverse(
                                            fraction = { cond ->
                                                if (cond) 0.8f
                                                else 0.5f
                                            },
                                            darkMode = color.luminance() < 0.3f
                                        )
                                    ).value,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                modifier = Modifier
                                    .clip(ShapeDefaults.mini)
                                    .hapticsClickable {
                                        context.copyToClipboard(color.toHex())
                                        scope.launch {
                                            toastHostState.showToast(
                                                icon = Icons.Rounded.ContentPaste,
                                                message = context.getString(R.string.color_copied)
                                            )
                                        }
                                    }
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(
                                            onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                        ),
                                        shape = ShapeDefaults.mini
                                    )
                                    .padding(horizontal = 8.dp),
                                text = color.toHex(),
                                style = LocalTextStyle.current.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    PanModeButton(
                        selected = panEnabled,
                        onClick = { panEnabled = !panEnabled }
                    )
                }
            }
        },
        dragHandle = {
            EnhancedModalSheetDragHandle(
                color = Color.Transparent,
                drawStroke = false,
                heightWhenDisabled = 20.dp
            )
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        }
    )
}