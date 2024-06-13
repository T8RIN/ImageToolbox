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

package ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeAnchor
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.SupportingButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.components.BlurRadiusSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.components.UseBlurredBackgroundToggle
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.BackgroundColorSelector
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem


@Composable
fun ResizeTypeSelector(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    value: ResizeType,
    onValueChange: (ResizeType) -> Unit
) {
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }
    var canvasColor by rememberSaveable {
        mutableStateOf(Color.Transparent)
    }
    var useBlurredBgInsteadOfColor by rememberSaveable {
        mutableStateOf(true)
    }
    var blurRadius by remember {
        mutableIntStateOf(35)
    }

    val modifiedResizeType by remember(canvasColor, useBlurredBgInsteadOfColor, blurRadius) {
        derivedStateOf {
            ResizeType.CenterCrop(
                canvasColor = canvasColor.toArgb()
                    .takeIf { !useBlurredBgInsteadOfColor },
                blurRadius = blurRadius
            )
        }
    }
    val updateResizeType = {
        onValueChange(modifiedResizeType)
    }
    Column(
        modifier = modifier
            .container(shape = RoundedCornerShape(24.dp))
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ToggleGroupButton(
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
            items = listOf(
                stringResource(R.string.explicit),
                stringResource(R.string.flexible),
                stringResource(R.string.crop)
            ),
            selectedIndex = when (value) {
                is ResizeType.Explicit -> 0
                is ResizeType.Flexible -> 1
                is ResizeType.CenterCrop -> 2
            },
            indexChanged = {
                onValueChange(
                    when (it) {
                        0 -> ResizeType.Explicit
                        1 -> ResizeType.Flexible
                        2 -> modifiedResizeType
                        else -> throw IllegalStateException()
                    }
                )
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
            ToggleGroupButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .container(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface
                    ),
                itemCount = entries.size,
                selectedIndex = selectedIndex,
                itemContent = {
                    Text(entries[it].title)
                },
                indexChanged = {
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
            UseBlurredBackgroundToggle(
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                checked = useBlurredBgInsteadOfColor,
                onCheckedChange = {
                    useBlurredBgInsteadOfColor = it
                    updateResizeType()
                }
            )
        }
        AnimatedVisibility(
            visible = value is ResizeType.CenterCrop,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AnimatedContent(targetState = useBlurredBgInsteadOfColor) { showBlurRadius ->
                if (!showBlurRadius) {
                    BackgroundColorSelector(
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 8.dp, start = 8.dp)
                            .container(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface
                            ),
                        value = canvasColor,
                        onColorChange = {
                            canvasColor = it
                            updateResizeType()
                        }
                    )
                } else {
                    BlurRadiusSelector(
                        modifier = Modifier.padding(bottom = 8.dp, end = 8.dp, start = 8.dp),
                        value = blurRadius,
                        color = MaterialTheme.colorScheme.surface,
                        onValueChange = {
                            blurRadius = it
                            updateResizeType()
                        }
                    )
                }
            }
        }
    }

    SimpleSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ResizeType.entries.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ContainerShapeDefaults.shapeForIndex(
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
}

private fun ResizeType.getSubtitle(): Int = when (this) {
    is ResizeType.CenterCrop -> R.string.crop_description
    is ResizeType.Explicit -> R.string.explicit_description
    is ResizeType.Flexible -> R.string.flexible_description
}