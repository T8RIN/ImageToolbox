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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Cube
import com.t8rin.imagetoolbox.core.resources.icons.Highlighter
import com.t8rin.imagetoolbox.core.resources.icons.NeonBrush
import com.t8rin.imagetoolbox.core.resources.icons.Pen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.CustomPathEffectParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.ImageParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.PixelationParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.PrivacyBlurParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.SpotHealParamsSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.TextParamsSelector

@Composable
fun DrawModeSelector(
    addFiltersSheetComponent: AddFiltersSheetComponent,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent,
    modifier: Modifier,
    value: DrawMode,
    strokeWidth: Pt,
    onValueChange: (DrawMode) -> Unit,
    values: List<DrawMode> = DrawMode.entries
) {
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(values, value) {
        if (values.find { it::class.isInstance(value) } == null) {
            values.firstOrNull()?.let { onValueChange(it) }
        }
    }

    Column(
        modifier = modifier
            .container(ShapeDefaults.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EnhancedButtonGroup(
            enabled = true,
            itemCount = values.size,
            title = {
                Text(
                    text = stringResource(R.string.draw_mode),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                SupportingButton(
                    onClick = {
                        isSheetVisible = true
                    }
                )
            },
            selectedIndex = values.indexOfFirst {
                value::class.isInstance(it)
            },
            itemContent = {
                Icon(
                    imageVector = values[it].getIcon(),
                    contentDescription = null
                )
            },
            onIndexChange = {
                onValueChange(values[it])
            }
        )

        SpotHealParamsSelector(
            value = value,
            onValueChange = onValueChange
        )

        PrivacyBlurParamsSelector(
            value = value,
            onValueChange = onValueChange
        )

        PixelationParamsSelector(
            value = value,
            onValueChange = onValueChange
        )

        TextParamsSelector(
            value = value,
            onValueChange = onValueChange
        )

        ImageParamsSelector(
            value = value,
            onValueChange = onValueChange,
            strokeWidth = strokeWidth
        )

        CustomPathEffectParamsSelector(
            value = value,
            onValueChange = onValueChange,
            addFiltersSheetComponent = addFiltersSheetComponent,
            filterTemplateCreationSheetComponent = filterTemplateCreationSheetComponent
        )
    }
    EnhancedModalBottomSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                values.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ShapeDefaults.byIndex(
                                    index = index,
                                    size = values.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(
                            text = stringResource(item.getTitle()),
                            icon = item.getIcon()
                        )
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
            TitleItem(text = stringResource(R.string.draw_mode))
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

private fun DrawMode.getSubtitle(): Int = when (this) {
    is DrawMode.Highlighter -> R.string.highlighter_sub
    is DrawMode.Neon -> R.string.neon_sub
    is DrawMode.Pen -> R.string.pen_sub
    is DrawMode.PathEffect.PrivacyBlur -> R.string.privacy_blur_sub
    is DrawMode.PathEffect.Pixelation -> R.string.pixelation_sub
    is DrawMode.Text -> R.string.draw_text_sub
    is DrawMode.Image -> R.string.draw_mode_image_sub
    is DrawMode.PathEffect.Custom -> R.string.draw_filter_sub
    is DrawMode.SpotHeal -> R.string.spot_heal_sub
}

private fun DrawMode.getTitle(): Int = when (this) {
    is DrawMode.Highlighter -> R.string.highlighter
    is DrawMode.Neon -> R.string.neon
    is DrawMode.Pen -> R.string.pen
    is DrawMode.PathEffect.PrivacyBlur -> R.string.privacy_blur
    is DrawMode.PathEffect.Pixelation -> R.string.pixelation
    is DrawMode.Text -> R.string.text
    is DrawMode.Image -> R.string.image
    is DrawMode.PathEffect.Custom -> R.string.filter
    is DrawMode.SpotHeal -> R.string.spot_heal
}

private fun DrawMode.getIcon(): ImageVector = when (this) {
    is DrawMode.Highlighter -> Icons.Outlined.Highlighter
    is DrawMode.Neon -> Icons.Outlined.NeonBrush
    is DrawMode.Pen -> Icons.Outlined.Pen
    is DrawMode.PathEffect.PrivacyBlur -> Icons.Rounded.BlurCircular
    is DrawMode.PathEffect.Pixelation -> Icons.Outlined.Cube
    is DrawMode.Text -> Icons.Rounded.TextFormat
    is DrawMode.Image -> Icons.Outlined.Image
    is DrawMode.PathEffect.Custom -> Icons.Outlined.AutoFixHigh
    is DrawMode.SpotHeal -> Icons.Outlined.Healing
}