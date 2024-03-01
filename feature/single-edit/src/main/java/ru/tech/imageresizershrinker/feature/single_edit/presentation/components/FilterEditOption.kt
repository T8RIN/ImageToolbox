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

package ru.tech.imageresizershrinker.feature.single_edit.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.data.utils.toCoil
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.filters.presentation.components.AddFiltersSheet
import ru.tech.imageresizershrinker.feature.filters.presentation.components.FilterItem
import ru.tech.imageresizershrinker.feature.filters.presentation.components.FilterReorderSheet
import ru.tech.imageresizershrinker.feature.pick_color.presentation.components.PickColorFromImageSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap) -> Unit,
    onRequestFiltering: suspend (Bitmap, List<UiFilter<*>>) -> Bitmap?,
    onRequestMappingFilters: (List<UiFilter<*>>) -> List<Transformation<Bitmap>>,
    filterList: List<UiFilter<*>>,
    updateFilter: (Any, Int, (Throwable) -> Unit) -> Unit,
    removeAt: (Int) -> Unit,
    addFilter: (UiFilter<*>) -> Unit,
    updateOrder: (List<UiFilter<*>>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current
    bitmap?.let {
        val scaffoldState = rememberBottomSheetScaffoldState()

        val showFilterSheet = rememberSaveable { mutableStateOf(false) }
        val showReorderSheet = rememberSaveable { mutableStateOf(false) }

        var stateBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }

        val showColorPicker = remember { mutableStateOf(false) }
        var tempColor by remember { mutableStateOf(Color.Black) }

        FullscreenEditOption(
            showControls = filterList.isNotEmpty(),
            canGoBack = stateBitmap == bitmap,
            visible = visible,
            modifier = Modifier.heightIn(max = LocalConfiguration.current.screenHeightDp.dp / 1.5f),
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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
                                    onFilterChange = {
                                        updateFilter(
                                            it,
                                            index
                                        ) {
                                            scope.launch {
                                                toastHostState.showError(
                                                    context,
                                                    it
                                                )
                                            }
                                        }
                                    },
                                    onLongPress = {
                                        showReorderSheet.value = true
                                    },
                                    showDragHandle = false,
                                    onRemove = {
                                        removeAt(index)
                                    }
                                )
                            }
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.mixedContainer,
                                onClick = { showFilterSheet.value = true },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(Icons.Rounded.AutoFixHigh, null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(id = R.string.add_filter))
                            }
                        }
                    }
                }
            },
            fabButtons = {
                EnhancedFloatingActionButton(
                    onClick = {
                        showFilterSheet.value = true
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(Icons.Rounded.AutoFixHigh, null)
                }
            },
            scaffoldState = scaffoldState,
            actions = {
                if (filterList.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.add_filter),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    showFilterSheet.value = true
                                }
                            }
                    )
                } else {
                    EnhancedIconButton(
                        containerColor = Color.Transparent,
                        contentColor = LocalContentColor.current,
                        enableAutoShadowAndBorder = false,
                        onClick = {
                            showColorPicker.value = true
                        },
                    ) {
                        Icon(Icons.Outlined.Colorize, null)
                    }
                }
            },
            topAppBar = { closeButton ->
                CenterAlignedTopAppBar(
                    navigationIcon = closeButton,
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    modifier = Modifier.drawHorizontalStroke(),
                    actions = {
                        AnimatedVisibility(
                            visible = stateBitmap != bitmap,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                onClick = {
                                    onGetBitmap(stateBitmap)
                                    onDismiss()
                                }
                            ) {
                                Icon(Icons.Rounded.Done, null)
                            }
                        }
                    },
                    title = {
                        Marquee(edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
                            Text(
                                text = stringResource(R.string.filter),
                            )
                        }
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
                        stateBitmap = it.result.drawable.toBitmap()
                    },
                    showTransparencyChecker = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                        .transparencyChecker()
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
            previewBitmap = stateBitmap,
            onFilterPicked = {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
                addFilter(it.newInstance())
            },
            onFilterPickedWithParams = {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
                addFilter(it)
            },
            onRequestFilterMapping = {
                onRequestMappingFilters(listOf(it)).first().toCoil()
            },
            onRequestPreview = { bitmap, filters, _ ->
                onRequestFiltering(bitmap, filters)
            }
        )

        FilterReorderSheet(
            filterList = filterList,
            visible = showReorderSheet,
            updateOrder = updateOrder
        )

        PickColorFromImageSheet(
            visible = showColorPicker,
            bitmap = stateBitmap,
            onColorChange = { tempColor = it },
            color = tempColor
        )
    }
}