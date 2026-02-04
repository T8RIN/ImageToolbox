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

package com.t8rin.imagetoolbox.core.ui.widget.controls.resize_group

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeAnchor
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.state.derivedValueOf
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.resize_group.components.BlurRadiusSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.resize_group.components.UseBlurredBackgroundToggle
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.PositionSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem


@Composable
fun ResizeTypeSelector(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    value: ResizeType,
    onValueChange: (ResizeType) -> Unit,
) {
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }
    var canvasColor by rememberSaveable(stateSaver = ColorSaver) {
        mutableStateOf(Color.Transparent)
    }
    var useBlurredBgInsteadOfColor by rememberSaveable {
        mutableStateOf(true)
    }
    var blurRadius by rememberSaveable {
        mutableIntStateOf(35)
    }
    var position by rememberSaveable {
        mutableStateOf(Position.Center)
    }

    val centerCropResizeType by remember(
        canvasColor,
        useBlurredBgInsteadOfColor,
        blurRadius,
        position
    ) {
        derivedStateOf {
            ResizeType.CenterCrop(
                canvasColor = canvasColor.toArgb()
                    .takeIf { !useBlurredBgInsteadOfColor },
                blurRadius = blurRadius,
                position = position
            )
        }
    }
    val updateCropResizeType = {
        onValueChange(centerCropResizeType)
    }

    val fitResizeType by remember(
        canvasColor,
        useBlurredBgInsteadOfColor,
        blurRadius,
        position
    ) {
        derivedStateOf {
            ResizeType.Fit(
                canvasColor = canvasColor.toArgb()
                    .takeIf { !useBlurredBgInsteadOfColor },
                blurRadius = blurRadius,
                position = position
            )
        }
    }
    val updateFitResizeType = {
        onValueChange(fitResizeType)
    }

    Column(
        modifier = modifier
            .container(shape = ShapeDefaults.extraLarge)
            .animateContentSizeNoClip(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EnhancedButtonGroup(
            modifier = Modifier.padding(start = 3.dp, end = 2.dp),
            enabled = enabled,
            title = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.resize_type),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SupportingButton(
                            onClick = {
                                isSheetVisible = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            itemCount = ResizeType.entries.size,
            selectedIndex = derivedValueOf(value) {
                ResizeType.entries.indexOfFirst { it::class.isInstance(value) }
            },
            onIndexChange = {
                onValueChange(
                    when (it) {
                        0 -> ResizeType.Explicit
                        1 -> ResizeType.Flexible
                        2 -> centerCropResizeType
                        3 -> fitResizeType
                        else -> ResizeType.Explicit
                    }
                )
            },
            itemContent = {
                Text(stringResource(ResizeType.entries[it].getTitle()))
            }
        )
        AnimatedVisibility(
            visible = value is ResizeType.Flexible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val entries = remember {
                ResizeAnchor.entries
            }
            val selectedIndex by remember(entries, value) {
                derivedStateOf {
                    entries.indexOfFirst {
                        it == (value as? ResizeType.Flexible)?.resizeAnchor
                    }
                }
            }
            EnhancedButtonGroup(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .container(
                        shape = ShapeDefaults.default,
                        color = MaterialTheme.colorScheme.surface
                    ),
                itemCount = entries.size,
                selectedIndex = selectedIndex,
                itemContent = {
                    Text(entries[it].title)
                },
                onIndexChange = {
                    onValueChange(
                        ResizeType.Flexible(entries[it])
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.resize_anchor),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    )
                },
                inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
        AnimatedVisibility(
            visible = value is ResizeType.CenterCrop,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                PositionSelector(
                    value = position,
                    onValueChange = {
                        position = it
                        updateCropResizeType()
                    },
                    shape = ShapeDefaults.top,
                    color = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.height(4.dp))
                UseBlurredBackgroundToggle(
                    checked = useBlurredBgInsteadOfColor,
                    onCheckedChange = {
                        useBlurredBgInsteadOfColor = it
                        updateCropResizeType()
                    },
                    shape = ShapeDefaults.center
                )
                Spacer(modifier = Modifier.height(4.dp))
                AnimatedContent(targetState = useBlurredBgInsteadOfColor) { showBlurRadius ->
                    if (showBlurRadius) {
                        BlurRadiusSelector(
                            modifier = Modifier,
                            value = blurRadius,
                            color = MaterialTheme.colorScheme.surface,
                            onValueChange = {
                                blurRadius = it
                                updateCropResizeType()
                            },
                            shape = ShapeDefaults.bottom
                        )
                    } else {
                        ColorRowSelector(
                            modifier = Modifier
                                .container(
                                    shape = ShapeDefaults.bottom,
                                    color = MaterialTheme.colorScheme.surface
                                ),
                            value = canvasColor,
                            onValueChange = {
                                canvasColor = it
                                updateCropResizeType()
                            }
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = value is ResizeType.Fit,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                PositionSelector(
                    value = position,
                    onValueChange = {
                        position = it
                        updateFitResizeType()
                    },
                    shape = ShapeDefaults.top,
                    color = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.height(4.dp))
                UseBlurredBackgroundToggle(
                    checked = useBlurredBgInsteadOfColor,
                    onCheckedChange = {
                        useBlurredBgInsteadOfColor = it
                        updateFitResizeType()
                    },
                    shape = ShapeDefaults.center
                )
                Spacer(modifier = Modifier.height(4.dp))
                AnimatedContent(targetState = useBlurredBgInsteadOfColor) { showBlurRadius ->
                    if (showBlurRadius) {
                        BlurRadiusSelector(
                            modifier = Modifier,
                            value = blurRadius,
                            color = MaterialTheme.colorScheme.surface,
                            onValueChange = {
                                blurRadius = it
                                updateFitResizeType()
                            },
                            shape = ShapeDefaults.bottom
                        )
                    } else {
                        ColorRowSelector(
                            modifier = Modifier
                                .container(
                                    shape = ShapeDefaults.bottom,
                                    color = MaterialTheme.colorScheme.surface
                                ),
                            value = canvasColor,
                            onValueChange = {
                                canvasColor = it
                                updateFitResizeType()
                            }
                        )
                    }
                }
            }
        }
    }

    EnhancedModalBottomSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .enhancedVerticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ResizeType.entries.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ShapeDefaults.byIndex(
                                    index = index,
                                    size = ResizeType.entries.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(text = stringResource(item.getTitle()))
                        Text(
                            text = stringResource(item.getSubtitle()),
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        visible = isSheetVisible,
        onDismiss = {
            isSheetVisible = it
        },
        title = {
            TitleItem(text = stringResource(R.string.resize_type))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { isSheetVisible = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

private val ResizeAnchor.title: String
    @Composable
    get() = when (this) {
        ResizeAnchor.Min -> stringResource(R.string.min)
        ResizeAnchor.Max -> stringResource(R.string.max)
        ResizeAnchor.Width -> stringResource(R.string.width, "")
        ResizeAnchor.Height -> stringResource(R.string.height, "")
        ResizeAnchor.Default -> stringResource(R.string.basic, "")
    }

private fun ResizeType.getTitle(): Int = when (this) {
    is ResizeType.CenterCrop -> R.string.crop
    is ResizeType.Explicit -> R.string.explicit
    is ResizeType.Flexible -> R.string.flexible
    is ResizeType.Fit -> R.string.fit
}

private fun ResizeType.getSubtitle(): Int = when (this) {
    is ResizeType.CenterCrop -> R.string.crop_description
    is ResizeType.Explicit -> R.string.explicit_description
    is ResizeType.Flexible -> R.string.flexible_description
    is ResizeType.Fit -> R.string.fit_description
}