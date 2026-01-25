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

package com.t8rin.imagetoolbox.feature.single_edit.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.utils.toCoil
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterReorderSheet
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheet
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheet
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Eyedropper
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvideFilterPreview
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.draw.presentation.components.OpenColorPickerCard
import com.t8rin.imagetoolbox.feature.pick_color.presentation.components.PickColorFromImageSheet
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun FilterEditOption(
    addFilterSheetComponent: AddFiltersSheetComponent,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent,
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap) -> Unit,
    onRequestMappingFilters: (List<UiFilter<*>>) -> List<Transformation<Bitmap>>,
    filterList: List<UiFilter<*>>,
    updateFilter: (Any, Int, (Throwable) -> Unit) -> Unit,
    removeAt: (Int) -> Unit,
    addFilter: (UiFilter<*>) -> Unit,
    updateOrder: (List<UiFilter<*>>) -> Unit
) {
    var stateBitmap by remember(bitmap, visible) { mutableStateOf(if (!visible) null else bitmap) }

    ProvideFilterPreview(stateBitmap)

    val essentials = rememberLocalEssentials()
    bitmap?.let {
        val scaffoldState = rememberBottomSheetScaffoldState()

        var showFilterSheet by rememberSaveable { mutableStateOf(false) }
        var showReorderSheet by rememberSaveable { mutableStateOf(false) }

        var showColorPicker by rememberSaveable { mutableStateOf(false) }
        var tempColor by rememberSaveable(
            showColorPicker,
            stateSaver = ColorSaver
        ) { mutableStateOf(Color.Black) }

        LaunchedEffect(visible) {
            if (visible && filterList.isEmpty()) {
                showFilterSheet = true
            }
        }

        var showTemplateCreationSheet by rememberSaveable {
            mutableStateOf(false)
        }

        FullscreenEditOption(
            showControls = filterList.isNotEmpty(),
            canGoBack = stateBitmap == bitmap,
            visible = visible,
            modifier = Modifier.heightIn(max = LocalScreenSize.current.height / 1.5f),
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!useScaffold) {
                        OpenColorPickerCard(
                            onOpen = {
                                showColorPicker = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }
                    Column(Modifier.container(MaterialTheme.shapes.extraLarge)) {
                        TitleItem(text = stringResource(R.string.filters))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            filterList.forEachIndexed { index, filter ->
                                FilterItem(
                                    filter = filter,
                                    onFilterChange = { filterChange ->
                                        updateFilter(
                                            filterChange,
                                            index,
                                            essentials::showFailureToast
                                        )
                                    },
                                    onLongPress = {
                                        showReorderSheet = true
                                    },
                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    showDragHandle = false,
                                    onRemove = {
                                        removeAt(index)
                                    },
                                    onCreateTemplate = {
                                        showTemplateCreationSheet = true
                                        filterTemplateCreationSheetComponent.setInitialTemplateFilter(
                                            TemplateFilter(
                                                name = essentials.getString(filter.title),
                                                filters = listOf(filter)
                                            )
                                        )
                                    }
                                )
                            }
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.mixedContainer,
                                onClick = { showFilterSheet = true },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoFixHigh,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.add_filter))
                            }
                        }

                        FilterTemplateCreationSheet(
                            component = filterTemplateCreationSheetComponent,
                            visible = showTemplateCreationSheet,
                            onDismiss = { showTemplateCreationSheet = false }
                        )
                    }
                }
            },
            fabButtons = {
                EnhancedFloatingActionButton(
                    onClick = {
                        showFilterSheet = true
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoFixHigh,
                        contentDescription = stringResource(R.string.add_filter)
                    )
                }
            },
            scaffoldState = scaffoldState,
            actions = {
                if (filterList.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.add_filter),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .tappable {
                                showFilterSheet = true
                            }
                    )
                } else {
                    EnhancedIconButton(
                        onClick = {
                            showColorPicker = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Eyedropper,
                            contentDescription = stringResource(R.string.pipette)
                        )
                    }
                }
            },
            topAppBar = { closeButton ->
                EnhancedTopAppBar(
                    type = EnhancedTopAppBarType.Center,
                    navigationIcon = closeButton,
                    actions = {
                        AnimatedVisibility(
                            visible = stateBitmap != bitmap && stateBitmap != null,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                onClick = {
                                    stateBitmap?.let(onGetBitmap)
                                    onDismiss()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Done,
                                    contentDescription = "Done"
                                )
                            }
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.filter),
                            modifier = Modifier.marquee()
                        )
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val direction = LocalLayoutDirection.current
                Picture(
                    model = bitmap,
                    shape = RectangleShape,
                    transformations = remember(filterList) {
                        derivedStateOf {
                            onRequestMappingFilters(filterList).map { it.toCoil() }
                        }
                    }.value,
                    onSuccess = {
                        stateBitmap = it.result.image.toBitmap()
                    },
                    showTransparencyChecker = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                        .transparencyChecker()
                        .clipToBounds()
                        .zoomable(rememberZoomState())
                        .padding(
                            start = WindowInsets
                                .displayCutout
                                .asPaddingValues()
                                .calculateStartPadding(direction)
                        ),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        AddFiltersSheet(
            visible = showFilterSheet,
            onVisibleChange = { showFilterSheet = it },
            previewBitmap = stateBitmap,
            onFilterPicked = {
                essentials.launch {
                    scaffoldState.bottomSheetState.expand()
                }
                addFilter(it.newInstance())
            },
            onFilterPickedWithParams = {
                essentials.launch {
                    scaffoldState.bottomSheetState.expand()
                }
                addFilter(it)
            },
            component = addFilterSheetComponent,
            filterTemplateCreationSheetComponent = filterTemplateCreationSheetComponent
        )

        FilterReorderSheet(
            filterList = filterList,
            visible = showReorderSheet,
            onDismiss = {
                showReorderSheet = false
            },
            onReorder = updateOrder
        )

        PickColorFromImageSheet(
            visible = showColorPicker,
            onDismiss = {
                showColorPicker = false
            },
            bitmap = stateBitmap,
            onColorChange = { tempColor = it },
            color = tempColor
        )
    }
}