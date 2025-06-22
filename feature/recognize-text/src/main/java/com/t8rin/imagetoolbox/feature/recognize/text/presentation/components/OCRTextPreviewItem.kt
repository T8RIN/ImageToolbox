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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun OCRTextPreviewItem(
    text: String?,
    onTextEdit: (String) -> Unit,
    isLoading: Boolean,
    loadingProgress: Int,
    accuracy: Int
) {
    var expanded by rememberSaveable {
        mutableStateOf(true)
    }

    AnimatedContent(targetState = isLoading) { loading ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .container(shape = ShapeDefaults.extraLarge)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = ShapeDefaults.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                            .size(72.dp)
                    ) {
                        EnhancedLoadingIndicator(
                            progress = loadingProgress / 100f,
                            loaderSize = 36.dp
                        )
                    }
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = ShapeDefaults.small
                        )
                        .padding(16.dp)
                        .animateContentSizeNoClip()
                ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = stringResource(R.string.accuracy, accuracy),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.weight(1f)
                        )
                        if ((text?.length ?: 0) >= 100) {
                            val rotation by animateFloatAsState(
                                if (expanded) 180f
                                else 0f
                            )
                            EnhancedIconButton(
                                forceMinimumInteractiveComponentSize = false,
                                containerColor = Color.Transparent,
                                onClick = { expanded = !expanded },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = "Expand",
                                    modifier = Modifier.rotate(rotation)
                                )
                            }
                        }
                    }
                    AnimatedContent(
                        targetState = expanded,
                        transitionSpec = {
                            fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                        }
                    ) { showFull ->
                        SelectionContainer {
                            if (showFull) {
                                BasicTextField(
                                    value = text
                                        ?: stringResource(R.string.picture_has_no_text),
                                    onValueChange = onTextEdit,
                                    enabled = text != null,
                                    textStyle = LocalTextStyle.current.copy(
                                        color = LocalContentColor.current
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                                )
                            } else {
                                Text(
                                    text = text ?: stringResource(R.string.picture_has_no_text),
                                    maxLines = if (text == null) Int.MAX_VALUE else 3,
                                    overflow = TextOverflow.Ellipsis,
                                    style = LocalTextStyle.current
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}