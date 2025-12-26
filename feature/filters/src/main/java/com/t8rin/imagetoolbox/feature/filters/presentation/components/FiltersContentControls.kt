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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.AddFilterButton
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheet
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.controls.SaveExifWidget
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.HistogramChart
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.filters.presentation.screenLogic.FiltersComponent

@Composable
internal fun FiltersContentControls(
    component: FiltersComponent
) {
    val essentials = rememberLocalEssentials()

    val filterType = component.filterType

    var showTemplateCreationSheet by rememberSaveable(filterType) {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val histogramItem = @Composable {
        PreferenceItemOverload(
            title = stringResource(R.string.histogram),
            subtitle = stringResource(R.string.histogram_sub),
            endIcon = {
                AnimatedContent(component.previewBitmap != null) {
                    if (it) {
                        HistogramChart(
                            model = component.previewBitmap,
                            modifier = Modifier
                                .width(100.dp)
                                .height(65.dp)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    } else {
                        Box(modifier = Modifier.size(56.dp)) {
                            EnhancedLoadingIndicator()
                        }
                    }
                }
            },
            shape = ShapeDefaults.extraLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(8.dp))
    }

    when (filterType) {
        is Screen.Filter.Type.Basic -> {
            val filterList = component.basicFilterState.filters
            if (component.bitmap != null) {
                ImageCounter(
                    imageCount = component.basicFilterState.uris?.size?.takeIf { it > 1 },
                    onRepick = component::showPickImageFromUrisSheet
                )
                if (filterList.isNotEmpty()) histogramItem()
                AnimatedContent(
                    targetState = filterList.isNotEmpty(),
                    transitionSpec = {
                        fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                    }
                ) { notEmpty ->
                    if (notEmpty) {
                        Column(Modifier.container(MaterialTheme.shapes.extraLarge)) {
                            TitleItem(text = stringResource(R.string.filters))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    8.dp
                                ),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                filterList.forEachIndexed { index, filter ->
                                    FilterItem(
                                        backgroundColor = MaterialTheme.colorScheme.surface,
                                        filter = filter,
                                        onFilterChange = { newValue ->
                                            component.updateFilter(
                                                value = newValue,
                                                index = index,
                                                onFailure = essentials::showFailureToast
                                            )
                                        },
                                        onLongPress = component::showReorderSheet,
                                        showDragHandle = false,
                                        onRemove = {
                                            component.removeFilterAtIndex(index)
                                        },
                                        onCreateTemplate = {
                                            showTemplateCreationSheet = true
                                            component.filterTemplateCreationSheetComponent.setInitialTemplateFilter(
                                                TemplateFilter(
                                                    name = context.getString(filter.title),
                                                    filters = listOf(filter)
                                                )
                                            )
                                        }
                                    )
                                }
                                AddFilterButton(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = component::showAddFiltersSheet,
                                    onCreateTemplate = {
                                        showTemplateCreationSheet = true
                                        component.filterTemplateCreationSheetComponent.setInitialTemplateFilter(
                                            TemplateFilter(
                                                name = context.getString(
                                                    filterList.firstOrNull()?.title
                                                        ?: R.string.template_filter
                                                ),
                                                filters = filterList
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        AddFilterButton(
                            onClick = component::showAddFiltersSheet,
                            modifier = Modifier.padding(
                                horizontal = 16.dp
                            )
                        )
                    }
                }
                Spacer(Modifier.size(8.dp))
                if (filterList.isEmpty()) histogramItem()
                SaveExifWidget(
                    imageFormat = component.imageInfo.imageFormat,
                    checked = component.keepExif,
                    onCheckedChange = component::setKeepExif
                )
                if (component.imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                    Modifier.size(8.dp)
                )
                QualitySelector(
                    imageFormat = component.imageInfo.imageFormat,
                    quality = component.imageInfo.quality,
                    onQualityChange = component::setQuality
                )
                Spacer(Modifier.size(8.dp))
                ImageFormatSelector(
                    value = component.imageInfo.imageFormat,
                    onValueChange = {
                        component.setImageFormat(it)
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        is Screen.Filter.Type.Masking -> {
            val maskList = component.maskingFilterState.masks
            if (component.bitmap != null) {
                if (maskList.isNotEmpty()) histogramItem()
                AnimatedContent(
                    targetState = maskList.isNotEmpty(),
                    transitionSpec = {
                        fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                    }
                ) { notEmpty ->
                    if (notEmpty) {
                        Column(Modifier.container(MaterialTheme.shapes.extraLarge)) {
                            TitleItem(text = stringResource(R.string.masks))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    4.dp
                                ),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                maskList.forEachIndexed { index, mask ->
                                    MaskItem(
                                        backgroundColor = MaterialTheme.colorScheme.surface,
                                        imageUri = component.maskingFilterState.uri,
                                        previousMasks = maskList.take(index),
                                        mask = mask,
                                        titleText = stringResource(
                                            R.string.mask_indexed,
                                            index + 1
                                        ),
                                        onMaskChange = { filterMask ->
                                            component.updateMask(
                                                value = filterMask,
                                                index = index,
                                                showError = essentials::showFailureToast
                                            )
                                        },
                                        onLongPress = component::showReorderSheet,
                                        showDragHandle = false,
                                        onRemove = {
                                            component.removeMaskAtIndex(index)
                                        },
                                        addMaskSheetComponent = component.addMaskSheetComponent,
                                        onCreateTemplate = {
                                            showTemplateCreationSheet = true
                                            component.filterTemplateCreationSheetComponent.setInitialTemplateFilter(
                                                TemplateFilter(
                                                    name = context.getString(
                                                        mask.filters.firstOrNull()
                                                            ?.toUiFilter()?.title
                                                            ?: R.string.template_filter
                                                    ),
                                                    filters = mask.filters
                                                )
                                            )
                                        }
                                    )
                                }
                                EnhancedButton(
                                    containerColor = MaterialTheme.colorScheme.mixedContainer,
                                    onClick = component::showAddFiltersSheet,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 4.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Texture,
                                        contentDescription = stringResource(R.string.add_mask)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(id = R.string.add_mask))
                                }
                            }
                        }
                    } else {
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.mixedContainer,
                            onClick = component::showAddFiltersSheet,
                            modifier = Modifier.padding(
                                horizontal = 16.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Texture,
                                contentDescription = stringResource(R.string.add_mask)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(id = R.string.add_mask))
                        }
                    }
                }
                Spacer(Modifier.size(8.dp))
                if (maskList.isEmpty()) histogramItem()
                SaveExifWidget(
                    imageFormat = component.imageInfo.imageFormat,
                    checked = component.keepExif,
                    onCheckedChange = component::setKeepExif
                )
                if (component.imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                    Modifier.size(8.dp)
                )
                QualitySelector(
                    imageFormat = component.imageInfo.imageFormat,
                    quality = component.imageInfo.quality,
                    onQualityChange = component::setQuality
                )
                Spacer(Modifier.size(8.dp))
                ImageFormatSelector(
                    value = component.imageInfo.imageFormat,
                    onValueChange = {
                        component.setImageFormat(it)
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        else -> Unit
    }

    FilterTemplateCreationSheet(
        component = component.filterTemplateCreationSheetComponent,
        visible = showTemplateCreationSheet,
        onDismiss = { showTemplateCreationSheet = false }
    )
}