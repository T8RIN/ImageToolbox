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

package ru.tech.imageresizershrinker.feature.watermarking.presentation.components

import android.graphics.PorterDuff
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.UiFontFam
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.theme.Typography
import ru.tech.imageresizershrinker.core.ui.theme.toColor
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRow
import ru.tech.imageresizershrinker.core.ui.widget.controls.AlphaSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.other.ExpandableItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkParams
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkingType
import kotlin.math.roundToInt

@Composable
fun WatermarkParamsSelectionGroup(
    value: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val pressed by interactionSource.collectIsPressedAsState()
    val cornerSize by animateDpAsState(
        if (pressed) 8.dp
        else 24.dp
    )
    ExpandableItem(
        modifier = modifier,
        interactionSource = interactionSource,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        visibleContent = {
            TitleItem(
                text = stringResource(id = R.string.properties),
                icon = Icons.Outlined.Tune
            )
        },
        expandableContent = {
            Column(
                modifier = Modifier.padding(end = 8.dp, start = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(visible = !value.isRepeated) {
                    EnhancedSliderItem(
                        value = value.positionX,
                        title = stringResource(id = R.string.offset_x),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onValueChange(value.copy(positionX = it))
                        },
                        valueRange = 0f..1f,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    EnhancedSliderItem(
                        value = value.positionY,
                        title = stringResource(id = R.string.offset_y),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onValueChange(value.copy(positionY = it))
                        },
                        valueRange = 0f..1f,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                EnhancedSliderItem(
                    value = value.rotation,
                    title = stringResource(id = R.string.angle),
                    valueRange = 0f..360f,
                    internalStateTransformation = { it.roundToInt() },
                    onValueChange = {
                        onValueChange(value.copy(rotation = it.roundToInt()))
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                )
                Spacer(modifier = Modifier.height(4.dp))
                AlphaSelector(
                    value = value.alpha,
                    onValueChange = {
                        onValueChange(value.copy(alpha = it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                )
                Spacer(modifier = Modifier.height(4.dp))
                PreferenceRowSwitch(
                    title = stringResource(id = R.string.repeat_watermark),
                    subtitle = stringResource(id = R.string.repeat_watermark_sub),
                    checked = value.isRepeated,
                    startIcon = Icons.Rounded.Repeat,
                    onClick = {
                        onValueChange(value.copy(isRepeated = it))
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier.container(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Text(
                        fontWeight = FontWeight.Medium,
                        text = stringResource(R.string.overlay_mode),
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                    )
                    val listState = rememberLazyListState()
                    val modes: List<PorterDuff.Mode> = remember {
                        mutableListOf<PorterDuff.Mode>().apply {
                            add(PorterDuff.Mode.SRC_OVER)
                            addAll(
                                PorterDuff.Mode
                                    .values()
                                    .toList() - listOf(
                                    PorterDuff.Mode.SRC_OVER,
                                    PorterDuff.Mode.CLEAR,
                                    PorterDuff.Mode.SRC,
                                    PorterDuff.Mode.DST
                                )
                            )
                        }
                    }
                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fadingEdges(listState),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(modes) {
                            val selected by remember(it, value) {
                                derivedStateOf {
                                    value.overlayMode == it.ordinal
                                }
                            }
                            EnhancedChip(
                                selected = selected,
                                onClick = {
                                    onValueChange(
                                        value.copy(overlayMode = it.ordinal)
                                    )
                                },
                                selectedColor = MaterialTheme.colorScheme.tertiary,
                                contentPadding = PaddingValues(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                AutoSizeText(
                                    text = it.name.replace("_", " "),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                val selectedIndex by remember(value.watermarkingType) {
                    derivedStateOf {
                        WatermarkingType
                            .entries
                            .indexOfFirst {
                                value.watermarkingType::class.java.isInstance(it)
                            }
                    }
                }
                ToggleGroupButton(
                    modifier = Modifier
                        .container(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                    enabled = true,
                    items = WatermarkingType.entries.map { it.translatedName },
                    selectedIndex = selectedIndex,
                    title = stringResource(id = R.string.watermark_type),
                    fadingEdgesColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    indexChanged = {
                        onValueChange(value.copy(watermarkingType = WatermarkingType.entries[it]))
                    },
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
                Spacer(modifier = Modifier.height(4.dp))
                AnimatedVisibility(visible = value.watermarkingType is WatermarkingType.Text) {
                    val type = value.watermarkingType as? WatermarkingType.Text
                        ?: return@AnimatedVisibility
                    Column {
                        RoundedTextField(
                            modifier = Modifier
                                .container(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                                .padding(8.dp),
                            value = type.text,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            singleLine = false,
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(text = it)
                                    )
                                )
                            },
                            label = {
                                Text(stringResource(R.string.text))
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Text(
                                fontWeight = FontWeight.Medium,
                                text = stringResource(R.string.font),
                                modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                            )
                            val listState = rememberLazyListState()
                            val fonts = remember {
                                UiFontFam.entries.reversed()
                            }
                            LazyRow(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fadingEdges(listState),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(fonts) {
                                    val selected by remember(it, type) {
                                        derivedStateOf {
                                            if (it == UiFontFam.System) type.font == 0
                                            else type.font == it.fontRes
                                        }
                                    }
                                    MaterialTheme(
                                        typography = Typography(it)
                                    ) {
                                        EnhancedChip(
                                            selected = selected,
                                            onClick = {
                                                onValueChange(
                                                    value.copy(
                                                        watermarkingType = type.copy(
                                                            font = it.fontRes ?: 0
                                                        )
                                                    )
                                                )
                                            },
                                            selectedColor = MaterialTheme.colorScheme.tertiary,
                                            contentPadding = PaddingValues(
                                                horizontal = 12.dp,
                                                vertical = 8.dp
                                            ),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            AutoSizeText(
                                                text = it.name
                                                    ?: stringResource(id = R.string.system),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        EnhancedSliderItem(
                            value = type.size,
                            title = stringResource(id = R.string.size, ""),
                            internalStateTransformation = {
                                it.roundToInt()
                            },
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(size = it)
                                    )
                                )
                            },
                            valueRange = 1f..100f,
                            steps = 99,
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Text(
                                fontWeight = FontWeight.Medium,
                                text = stringResource(R.string.text_color),
                                modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                            )
                            ColorSelectionRow(
                                allowAlpha = true,
                                contentPadding = PaddingValues(16.dp),
                                value = type.color.toColor(),
                                onValueChange = {
                                    onValueChange(
                                        value.copy(
                                            watermarkingType = type.copy(
                                                color = it.toArgb()
                                            )
                                        )
                                    )
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Text(
                                fontWeight = FontWeight.Medium,
                                text = stringResource(R.string.background_color),
                                modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                            )
                            ColorSelectionRow(
                                allowAlpha = true,
                                contentPadding = PaddingValues(16.dp),
                                value = type.backgroundColor.toColor(),
                                onValueChange = {
                                    onValueChange(
                                        value.copy(
                                            watermarkingType = type.copy(
                                                backgroundColor = it.toArgb()
                                            )
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                AnimatedVisibility(visible = value.watermarkingType is WatermarkingType.Image) {
                    val type = value.watermarkingType as? WatermarkingType.Image
                        ?: return@AnimatedVisibility
                    val pickImageLauncher = rememberImagePicker(
                        mode = localImagePickerMode(Picker.Single)
                    ) { list ->
                        list.firstOrNull()?.let {
                            onValueChange(
                                value.copy(
                                    watermarkingType = type.copy(imageData = it)
                                )
                            )
                        }
                    }
                    Column {
                        PreferenceItemOverload(
                            title = stringResource(id = R.string.image),
                            subtitle = stringResource(id = R.string.watermarking_image_sub),
                            onClick = {
                                pickImageLauncher.pickImage()
                            },
                            icon = {
                                Picture(
                                    contentScale = ContentScale.Inside,
                                    model = type.imageData,
                                    shape = CloverShape,
                                    modifier = Modifier.size(48.dp)
                                )
                            },
                            endIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.CreateAlt,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        EnhancedSliderItem(
                            value = type.size,
                            title = stringResource(id = R.string.size, ""),
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(size = it)
                                    )
                                )
                            },
                            valueRange = 0.01f..1f,
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(cornerSize)
    )
}

private val WatermarkingType.translatedName: String
    @Composable
    get() = when (this) {
        is WatermarkingType.Text -> stringResource(R.string.text)
        is WatermarkingType.Image -> stringResource(R.string.image)
    }