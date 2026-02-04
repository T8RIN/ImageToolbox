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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExtensionOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.collectAsUiState
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateAddingGroup
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateInfoSheet
import com.t8rin.imagetoolbox.core.filters.presentation.widget.TemplateFilterSelectionItem
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberRetainedLazyListState

@Composable
internal fun TemplatesContent(
    component: AddFiltersSheetComponent,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent,
    onVisibleChange: (Boolean) -> Unit,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit,
) {
    val templateFilters by component.templatesFlow.collectAsUiState()
    val onRequestFilterMapping = component::filterToTransformation
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    AnimatedContent(
        targetState = templateFilters.isEmpty()
    ) { noTemplates ->
        if (noTemplates) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .enhancedVerticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.no_template_filters),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Outlined.ExtensionOff,
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )
                FilterTemplateAddingGroup(
                    onAddTemplateFilterFromString = component::addTemplateFilterFromString,
                    onAddTemplateFilterFromUri = component::addTemplateFilterFromUri,
                    component = filterTemplateCreationSheetComponent
                )
                Spacer(Modifier.weight(1f))
            }
        } else {
            LazyColumn(
                state = rememberRetainedLazyListState("templates"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(16.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                itemsIndexed(
                    items = templateFilters,
                    key = { _, f -> f.hashCode() }
                ) { index, templateFilter ->
                    var showFilterTemplateInfoSheet by rememberSaveable {
                        mutableStateOf(false)
                    }
                    TemplateFilterSelectionItem(
                        templateFilter = templateFilter,
                        onClick = {
                            onVisibleChange(false)
                            templateFilter.filters.forEach {
                                onFilterPickedWithParams(it.toUiFilter())
                            }
                        },
                        onLongClick = {
                            component.setPreviewData(templateFilter.filters)
                        },
                        onInfoClick = {
                            showFilterTemplateInfoSheet = true
                        },
                        onRequestFilterMapping = onRequestFilterMapping,
                        shape = ShapeDefaults.byIndex(
                            index = index,
                            size = templateFilters.size
                        ),
                        modifier = Modifier.animateItem()
                    )
                    FilterTemplateInfoSheet(
                        visible = showFilterTemplateInfoSheet,
                        onDismiss = {
                            showFilterTemplateInfoSheet = it
                        },
                        templateFilter = templateFilter,
                        onRequestFilterMapping = onRequestFilterMapping,
                        onShareImage = {
                            component.shareImage(it, showConfetti)
                        },
                        onSaveImage = {
                            component.saveImage(
                                bitmap = it,
                                onComplete = essentials::parseSaveResult
                            )
                        },
                        onSaveFile = { fileUri, content ->
                            component.saveContentTo(
                                content = content,
                                fileUri = fileUri,
                                onResult = essentials::parseFileSaveResult
                            )
                        },
                        onConvertTemplateFilterToString = component::convertTemplateFilterToString,
                        onRemoveTemplateFilter = component::removeTemplateFilter,
                        onRequestTemplateFilename = {
                            component.createTemplateFilename(
                                templateFilter
                            )
                        },
                        onShareFile = { content ->
                            component.shareContent(
                                content = content,
                                filename = component.createTemplateFilename(
                                    templateFilter
                                ),
                                onComplete = showConfetti
                            )
                        },
                        component = filterTemplateCreationSheetComponent
                    )
                }
                item {
                    FilterTemplateAddingGroup(
                        onAddTemplateFilterFromString = component::addTemplateFilterFromString,
                        onAddTemplateFilterFromUri = component::addTemplateFilterFromUri,
                        component = filterTemplateCreationSheetComponent
                    )
                }
            }
        }
    }
}