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

package com.t8rin.imagetoolbox.core.ui.widget.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.DashboardCustomize
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropAspectRatio
import com.smarttoolfactory.cropper.util.createRectShape
import com.smarttoolfactory.cropper.widget.AspectRatioSelectionCard
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.utils.ifCasts
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import kotlin.math.abs

@Composable
fun AspectRatioSelector(
    modifier: Modifier = Modifier,
    selectedAspectRatio: DomainAspectRatio? = DomainAspectRatio.Free,
    unselectedCardColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    contentPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        top = 4.dp,
        bottom = 16.dp,
        end = 16.dp + WindowInsets
            .navigationBars
            .asPaddingValues()
            .calculateEndPadding(LocalLayoutDirection.current)
    ),
    title: @Composable ColumnScope.() -> Unit = {
        Text(
            text = stringResource(id = R.string.aspect_ratio),
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 16.dp,
                    bottom = 8.dp
                ),
            fontWeight = FontWeight.Medium
        )
    },
    enableFadingEdges: Boolean = true,
    onAspectRatioChange: (DomainAspectRatio, AspectRatio) -> Unit,
    color: Color = Color.Unspecified,
    shape: Shape = ShapeDefaults.extraLarge,
    aspectRatios: List<DomainAspectRatio> = aspectRatios()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .container(
                color = color,
                shape = shape,
                resultPadding = 0.dp
            )
            .clearFocusOnTap()
    ) {
        title()
        val listState = rememberLazyListState()
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            contentPadding = contentPadding,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fadingEdges(
                scrollableState = listState,
                enabled = enableFadingEdges
            )
        ) {
            itemsIndexed(
                items = aspectRatios,
                key = { _, a ->
                    a.toCropAspectRatio(
                        original = "0",
                        free = "1",
                        custom = "2"
                    ).title
                }
            ) { index, item ->
                val selected = (item == selectedAspectRatio)
                    .or(
                        item is DomainAspectRatio.Custom && selectedAspectRatio is DomainAspectRatio.Custom
                    )
                val cropAspectRatio = item.toCropAspectRatio(
                    original = stringResource(R.string.original),
                    free = stringResource(R.string.free),
                    custom = stringResource(R.string.custom)
                )
                val isNumeric by remember(item) {
                    derivedStateOf {
                        item != DomainAspectRatio.Original && item != DomainAspectRatio.Free && item !is DomainAspectRatio.Custom
                    }
                }
                if (isNumeric) {
                    AspectRatioSelectionCard(
                        modifier = Modifier
                            .width(90.dp)
                            .container(
                                resultPadding = 0.dp,
                                color = animateColorAsState(
                                    targetValue = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else unselectedCardColor,
                                ).value,
                                borderColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    0.7f
                                )
                                else MaterialTheme.colorScheme.outlineVariant()
                            )
                            .hapticsClickable {
                                onAspectRatioChange(
                                    aspectRatios[index],
                                    cropAspectRatio.aspectRatio
                                )
                            }
                            .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 2.dp),
                        contentColor = Color.Transparent,
                        color = MaterialTheme.colorScheme.onSurface,
                        cropAspectRatio = cropAspectRatio
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .height(106.dp)
                            .container(
                                resultPadding = 0.dp,
                                color = animateColorAsState(
                                    targetValue = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else unselectedCardColor,
                                ).value,
                                borderColor = takeColorFromScheme {
                                    if (selected) onPrimaryContainer.copy(0.7f)
                                    else outlineVariant()
                                }
                            )
                            .hapticsClickable {
                                if (!item::class.isInstance(selectedAspectRatio)) {
                                    onAspectRatioChange(
                                        aspectRatios[index],
                                        cropAspectRatio.aspectRatio
                                    )
                                }
                            }
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (item) {
                                is DomainAspectRatio.Original -> {
                                    Icon(
                                        imageVector = Icons.Outlined.Image,
                                        contentDescription = null
                                    )
                                }

                                is DomainAspectRatio.Free -> {
                                    Icon(
                                        imageVector = Icons.Outlined.CropFree,
                                        contentDescription = null
                                    )
                                }

                                else -> {
                                    Icon(
                                        imageVector = Icons.Outlined.DashboardCustomize,
                                        contentDescription = null
                                    )
                                }
                            }
                            Text(
                                text = cropAspectRatio.title,
                                fontSize = 14.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = selectedAspectRatio is DomainAspectRatio.Custom) {
            Row(
                Modifier
                    .padding(8.dp)
                    .container(
                        shape = ShapeDefaults.extraLarge,
                        color = unselectedCardColor
                    )
            ) {
                var tempWidth by remember {
                    mutableStateOf(selectedAspectRatio?.widthProportion?.toString() ?: "1")
                }
                var tempHeight by remember {
                    mutableStateOf(selectedAspectRatio?.heightProportion?.toString() ?: "1")
                }
                RoundedTextField(
                    value = tempWidth,
                    onValueChange = { value ->
                        tempWidth = value
                        val width = abs(value.toFloatOrNull() ?: 0f).coerceAtLeast(1f)

                        selectedAspectRatio.ifCasts<DomainAspectRatio.Custom> { aspect ->
                            onAspectRatioChange(
                                aspect.copy(
                                    widthProportion = width
                                ),
                                AspectRatio(
                                    (width / aspect.heightProportion).takeIf { !it.isNaN() }
                                        ?: 1f
                                )
                            )
                        }
                    },
                    shape = ShapeDefaults.smallStart,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = {
                        Text(stringResource(R.string.width, " "))
                    },
                    supportingText = abs(tempWidth.toFloatOrNull() ?: 0f).takeIf { it < 1f }?.let {
                        {
                            Text(stringResource(R.string.minimum_value_is, 1))
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = 8.dp,
                            top = 8.dp,
                            bottom = 8.dp,
                            end = 2.dp
                        )
                )
                RoundedTextField(
                    value = tempHeight,
                    onValueChange = { value ->
                        tempHeight = value
                        val height = abs(value.toFloatOrNull() ?: 1f).coerceAtLeast(1f)

                        selectedAspectRatio.ifCasts<DomainAspectRatio.Custom> { aspect ->
                            onAspectRatioChange(
                                aspect.copy(
                                    heightProportion = height
                                ),
                                AspectRatio(
                                    (aspect.widthProportion / height).takeIf { !it.isNaN() }
                                        ?: 1f
                                )
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = ShapeDefaults.smallEnd,
                    label = {
                        Text(stringResource(R.string.height, " "))
                    },
                    supportingText = abs(tempHeight.toFloatOrNull() ?: 0f).takeIf { it < 1f }?.let {
                        {
                            Text(stringResource(R.string.minimum_value_is, 1))
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = 2.dp,
                            top = 8.dp,
                            bottom = 8.dp,
                            end = 8.dp
                        ),
                )
            }
        }
    }
}

fun DomainAspectRatio.toCropAspectRatio(
    original: String,
    free: String,
    custom: String
): CropAspectRatio = when (this) {
    is DomainAspectRatio.Original -> {
        CropAspectRatio(
            title = original,
            shape = createRectShape(AspectRatio.Original),
            aspectRatio = AspectRatio.Original
        )
    }

    is DomainAspectRatio.Free -> {
        CropAspectRatio(
            title = free,
            shape = createRectShape(AspectRatio.Original),
            aspectRatio = AspectRatio.Original
        )
    }

    is DomainAspectRatio.Custom -> {
        CropAspectRatio(
            title = custom,
            shape = createRectShape(AspectRatio(value)),
            aspectRatio = AspectRatio(value)
        )
    }

    else -> {
        val width = widthProportion.toString()
            .trimTrailingZero()
        val height = heightProportion.toString()
            .trimTrailingZero()
        CropAspectRatio(
            title = "$width:$height",
            shape = createRectShape(AspectRatio(value)),
            aspectRatio = AspectRatio(value)
        )
    }
}