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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EditRoad
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.FilterHdr
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.LensBlur
import androidx.compose.material.icons.rounded.Light
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.filterIsNotInstance
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.collectAsUiState
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterPreviewSheet
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterSelectionItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Cube
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getStringLocalized
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberForeverLazyListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AddFiltersSheet(
    component: AddFiltersSheetComponent,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    previewBitmap: Bitmap?,
    onFilterPicked: (UiFilter<*>) -> Unit,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit,
    canAddTemplates: Boolean = true
) {
    val context = LocalContext.current
    val groupedFilters by remember(context, canAddTemplates) {
        derivedStateOf {
            UiFilter.groupedEntries(context).let { lists ->
                if (canAddTemplates) lists
                else lists.map {
                    it.filterIsNotInstance(
                        Filter.PaletteTransfer::class,
                        Filter.LUT512x512::class,
                        Filter.PaletteTransferVariant::class,
                        Filter.CubeLut::class,
                        Filter.LensCorrection::class
                    )
                }
            }
        }
    }
    val haptics = LocalHapticFeedback.current
    val pagerState = rememberPagerState(
        pageCount = { groupedFilters.size + if (canAddTemplates) 2 else 1 },
        initialPage = 2
    )

    val onRequestFilterMapping = component::filterToTransformation

    val essentials = rememberLocalEssentials()
    val scope = essentials.coroutineScope

    val favoriteFilters by component.favoritesFlow.collectAsUiState()

    val tabs: List<Pair<ImageVector, String>> by remember(canAddTemplates) {
        derivedStateOf {
            listOf(
                Icons.Rounded.Bookmark to context.getString(R.string.favorite),
                Icons.Rounded.Speed to context.getString(R.string.simple_effects),
                Icons.Rounded.FormatColorFill to context.getString(R.string.color),
                Icons.Rounded.TableChart to context.getString(R.string.lut),
                Icons.Rounded.Light to context.getString(R.string.light_aka_illumination),
                Icons.Rounded.FilterHdr to context.getString(R.string.effect),
                Icons.Rounded.LensBlur to context.getString(R.string.blur),
                Icons.Rounded.Cube to context.getString(R.string.pixelation),
                Icons.Rounded.Animation to context.getString(R.string.distortion),
                Icons.Rounded.EditRoad to context.getString(R.string.dithering)
            ).let {
                if (canAddTemplates) listOf(
                    Icons.Rounded.Extension to context.getString(R.string.template)
                ) + it
                else it
            }
        }
    }

    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }
    var searchKeyword by rememberSaveable(isSearching) {
        mutableStateOf("")
    }
    var filtersForSearch by remember {
        mutableStateOf(
            groupedFilters.flatten().sortedBy { context.getString(it.title) }
        )
    }
    LaunchedEffect(searchKeyword) {
        delay(400L) // Debounce calculations
        if (searchKeyword.isEmpty()) {
            filtersForSearch = groupedFilters.flatten().sortedBy { context.getString(it.title) }
            return@LaunchedEffect
        }

        filtersForSearch = groupedFilters.flatten().filter {
            context.getString(it.title).contains(
                other = searchKeyword,
                ignoreCase = true
            ).or(
                context.getStringLocalized(
                    it.title, Locale.ENGLISH
                ).contains(
                    other = searchKeyword,
                    ignoreCase = true
                )
            )
        }.sortedBy { context.getString(it.title) }
    }

    EnhancedModalBottomSheet(
        dragHandle = {
            EnhancedModalSheetDragHandle {
                AnimatedVisibility(visible = !isSearching) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PrimaryScrollableTabRow(
                            divider = {},
                            edgePadding = 16.dp,
                            containerColor = EnhancedBottomSheetDefaults.barContainerColor,
                            selectedTabIndex = pagerState.currentPage,
                            indicator = {
                                TabRowDefaults.PrimaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(
                                        selectedTabIndex = pagerState.currentPage,
                                        matchContentSize = true
                                    ),
                                    width = Dp.Unspecified,
                                    height = 4.dp,
                                    shape = RoundedCornerShape(
                                        topStart = 100f,
                                        topEnd = 100f
                                    )
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, (icon, title) ->
                                val selected = pagerState.currentPage == index
                                val color by animateColorAsState(
                                    if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else MaterialTheme.colorScheme.onSurface
                                )
                                val interactionSource = remember { MutableInteractionSource() }
                                val shape = shapeByInteraction(
                                    shape = RoundedCornerShape(42.dp),
                                    pressedShape = ShapeDefaults.default,
                                    interactionSource = interactionSource
                                )

                                Tab(
                                    interactionSource = interactionSource,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clip(shape),
                                    selected = selected,
                                    onClick = {
                                        haptics.longPress()
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = color
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = title,
                                            color = color
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        sheetContent = {
            component.AttachLifecycle()

            AnimatedContent(
                modifier = Modifier.weight(1f, false),
                targetState = isSearching
            ) { isSearching ->
                if (isSearching) {
                    AnimatedContent(
                        targetState = filtersForSearch.isNotEmpty()
                    ) { isNotEmpty ->
                        if (isNotEmpty) {
                            LazyColumn(
                                state = rememberForeverLazyListState("sheet"),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.animateContentSizeNoClip(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                itemsIndexed(
                                    items = filtersForSearch,
                                    key = { _, f -> f.hashCode() }
                                ) { index, filter ->
                                    FilterSelectionItem(
                                        filter = filter,
                                        isFavoritePage = false,
                                        canOpenPreview = previewBitmap != null,
                                        favoriteFilters = favoriteFilters,
                                        onLongClick = {
                                            component.setPreviewData(filter)
                                        },
                                        onOpenPreview = {
                                            component.setPreviewData(filter)
                                        },
                                        onClick = {
                                            onVisibleChange(false)
                                            onFilterPicked(filter)
                                        },
                                        onRequestFilterMapping = onRequestFilterMapping,
                                        shape = ShapeDefaults.byIndex(
                                            index = index,
                                            size = filtersForSearch.size
                                        ),
                                        onToggleFavorite = {
                                            component.toggleFavorite(filter)
                                        },
                                        modifier = Modifier.animateItem()
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = stringResource(R.string.nothing_found_by_search),
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        end = 24.dp,
                                        top = 8.dp,
                                        bottom = 8.dp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Rounded.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .weight(2f)
                                        .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                                        .fillMaxSize()
                                )
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 2
                    ) { page ->
                        val templatesContent = @Composable {
                            TemplatesContent(
                                component = component,
                                filterTemplateCreationSheetComponent = filterTemplateCreationSheetComponent,
                                onVisibleChange = onVisibleChange,
                                onFilterPickedWithParams = onFilterPickedWithParams
                            )
                        }
                        val favoritesContent = @Composable {
                            FavoritesContent(
                                component = component,
                                onVisibleChange = onVisibleChange,
                                onFilterPickedWithParams = onFilterPickedWithParams,
                                onFilterPicked = onFilterPicked,
                                previewBitmap = previewBitmap
                            )
                        }
                        val otherContent = @Composable {
                            val filters by remember(page) {
                                derivedStateOf {
                                    groupedFilters[page - if (canAddTemplates) 2 else 1]
                                }
                            }
                            OtherContent(
                                component = component,
                                tabs = tabs,
                                page = page,
                                filters = filters,
                                onVisibleChange = onVisibleChange,
                                onFilterPickedWithParams = onFilterPickedWithParams,
                                onFilterPicked = onFilterPicked,
                                previewBitmap = previewBitmap
                            )
                        }

                        if (canAddTemplates) {
                            when (page) {
                                0 -> templatesContent()

                                1 -> favoritesContent()

                                else -> otherContent()
                            }
                        } else {
                            when (page) {
                                0 -> favoritesContent()

                                else -> otherContent()
                            }
                        }
                    }
                }
            }
        },
        title = {
            AnimatedContent(
                targetState = isSearching
            ) { searching ->
                if (searching) {
                    BackHandler {
                        searchKeyword = ""
                        isSearching = false
                    }
                    ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                        RoundedTextField(
                            maxLines = 1,
                            hint = { Text(stringResource(id = R.string.search_here)) },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search,
                                autoCorrectEnabled = null
                            ),
                            value = searchKeyword,
                            onValueChange = {
                                searchKeyword = it
                            },
                            startIcon = {
                                EnhancedIconButton(
                                    onClick = {
                                        searchKeyword = ""
                                        isSearching = false
                                    },
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = stringResource(R.string.exit),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            endIcon = {
                                AnimatedVisibility(
                                    visible = searchKeyword.isNotEmpty(),
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut() + scaleOut()
                                ) {
                                    EnhancedIconButton(
                                        onClick = {
                                            searchKeyword = ""
                                        },
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = stringResource(R.string.close),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            shape = CircleShape
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TitleItem(
                            text = stringResource(R.string.filter),
                            icon = Icons.Rounded.AutoFixHigh
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        EnhancedIconButton(
                            onClick = { isSearching = true },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(R.string.search_here)
                            )
                        }
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = { onVisibleChange(false) }
                        ) {
                            AutoSizeText(stringResource(R.string.close))
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        },
        confirmButton = {},
        enableBottomContentWeight = false,
        visible = visible,
        onDismiss = onVisibleChange
    )

    FilterPreviewSheet(
        component = component,
        onFilterPickedWithParams = onFilterPickedWithParams,
        onVisibleChange = onVisibleChange,
        previewBitmap = previewBitmap
    )
}

@Composable
fun AddFiltersSheet(
    component: AddFiltersSheetComponent,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent,
    visible: Boolean,
    onDismiss: () -> Unit,
    previewBitmap: Bitmap?,
    onFilterPicked: (UiFilter<*>) -> Unit,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit,
    canAddTemplates: Boolean = true
) {
    AddFiltersSheet(
        component = component,
        filterTemplateCreationSheetComponent = filterTemplateCreationSheetComponent,
        visible = visible,
        onVisibleChange = { if (!it) onDismiss() },
        previewBitmap = previewBitmap,
        onFilterPicked = onFilterPicked,
        onFilterPickedWithParams = onFilterPickedWithParams,
        canAddTemplates = canAddTemplates
    )
}