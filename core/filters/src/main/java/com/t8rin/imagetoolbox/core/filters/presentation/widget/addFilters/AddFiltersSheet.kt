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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.collectAsUiState
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterPreviewSheet
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterSelectionItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberRetainedLazyListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val favoriteFilters by component.favoritesFlow.collectAsUiState()

    val tabs: List<UiFilter.Group> by remember(canAddTemplates, favoriteFilters) {
        derivedStateOf {
            buildList {
                if (canAddTemplates) {
                    add(UiFilter.Group.Template)
                }
                add(UiFilter.Group.Favorite(favoriteFilters))
                addAll(UiFilter.groups)
            }
        }
    }

    val haptics = LocalHapticFeedback.current
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
        initialPage = 2
    )

    val onRequestFilterMapping = component::filterToTransformation

    val essentials = rememberLocalEssentials()

    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }
    var searchKeyword by rememberSaveable(isSearching) {
        mutableStateOf("")
    }
    val allFilters = remember {
        tabs.flatMap { group ->
            group.filters(canAddTemplates).sortedBy { essentials.getString(it.title) }
        }
    }
    var filtersForSearch by remember(allFilters) {
        mutableStateOf(allFilters)
    }
    LaunchedEffect(searchKeyword) {
        withContext(Dispatchers.Default) {
            delay(400L) // Debounce calculations
            if (searchKeyword.isEmpty()) {
                filtersForSearch = allFilters
                return@withContext
            }

            filtersForSearch = allFilters.filter {
                essentials.getString(it.title).contains(
                    other = searchKeyword,
                    ignoreCase = true
                ) || essentials.getStringLocalized(
                    resId = it.title,
                    language = Locale.ENGLISH.language
                ).contains(
                    other = searchKeyword,
                    ignoreCase = true
                )
            }.sortedBy { essentials.getString(it.title) }
        }
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
                                    shape = AutoCornersShape(
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
                                    shape = AutoCornersShape(42.dp),
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
                                        essentials.launch {
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
                                            text = stringResource(title),
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
                                state = rememberRetainedLazyListState("sheet"),
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
                        when (val group = tabs[page]) {
                            is UiFilter.Group.Template -> {
                                TemplatesContent(
                                    component = component,
                                    filterTemplateCreationSheetComponent = filterTemplateCreationSheetComponent,
                                    onVisibleChange = onVisibleChange,
                                    onFilterPickedWithParams = onFilterPickedWithParams
                                )
                            }

                            is UiFilter.Group.Favorite -> {
                                FavoritesContent(
                                    component = component,
                                    onVisibleChange = onVisibleChange,
                                    onFilterPickedWithParams = onFilterPickedWithParams,
                                    onFilterPicked = onFilterPicked,
                                    previewBitmap = previewBitmap
                                )
                            }

                            else -> {
                                val filters by remember(group, canAddTemplates) {
                                    derivedStateOf {
                                        group.filters(canAddTemplates)
                                    }
                                }
                                OtherContent(
                                    component = component,
                                    currentGroup = group,
                                    filters = filters,
                                    onVisibleChange = onVisibleChange,
                                    onFilterPickedWithParams = onFilterPickedWithParams,
                                    onFilterPicked = onFilterPicked,
                                    previewBitmap = previewBitmap
                                )
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
                            shape = ShapeDefaults.circle
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