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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult

@Composable
internal fun MediaSemanticSearchCategories(
    categories: List<SemanticSearchCategoryResult>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onEditCategory: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var showCategoryThumbnail by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val layoutDirection = LocalLayoutDirection.current
    val customCategoryIds = categories.mapNotNull {
        (it.category as? SemanticSearchCategory.Custom)?.id
    }
    var knownCategoryIds by remember { mutableStateOf(customCategoryIds) }
    LaunchedEffect(customCategoryIds) {
        val addedCategory = customCategoryIds.lastOrNull { it !in knownCategoryIds }
        knownCategoryIds = customCategoryIds
        if (addedCategory != null) {
            listState.animateScrollToItem(categories.indexOfFirst { it.category.id == addedCategory })
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = WindowInsets.displayCutout.asPaddingValues()
                    .calculateStartPadding(layoutDirection) + 8.dp
            ),
        verticalAlignment = Alignment.Top
    ) {
        EnhancedIconButton(
            onClick = { showCategoryThumbnail = !showCategoryThumbnail },
            modifier = Modifier.size(32.dp),
            contentColor = MaterialTheme.colorScheme.tertiary
        ) {
            Icon(
                imageVector = Icons.Outlined.Neurology,
                contentDescription = stringResource(R.string.gallery_search_categories)
            )
        }
        LazyRow(
            modifier = Modifier
                .weight(1f)
                .fadingEdges(listState),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(
                start = 8.dp,
                end = WindowInsets.displayCutout.asPaddingValues()
                    .calculateEndPadding(layoutDirection) + 8.dp
            ),
            flingBehavior = enhancedFlingBehavior()
        ) {
            items(
                items = categories,
                key = { it.category.id }
            ) { result ->
                val isSelected = selectedCategory == result.category.id
                CategoryChip(
                    title = result.category.title(),
                    count = result.media.size,
                    previewUri = result.media.firstOrNull()?.uri,
                    showThumbnail = showCategoryThumbnail,
                    selected = isSelected,
                    isLoading = isLoading,
                    onLongClick = { onEditCategory(result.category.id) }.takeUnless { isLoading },
                    onClick = if (isLoading) null else {
                        { onCategorySelected(result.category.id.takeUnless { isSelected }) }
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    count: Int,
    previewUri: String?,
    showThumbnail: Boolean,
    isLoading: Boolean
) {
    val isImageVisible = showThumbnail && !previewUri.isNullOrEmpty()
    EnhancedChip(
        selected = selected,
        onClick = onClick,
        onLongClick = onLongClick,
        selectedColor = MaterialTheme.colorScheme.tertiary,
        unselectedColor = MaterialTheme.colorScheme.tertiaryContainer,
        unselectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        contentPadding = PaddingValues(
            horizontal = animateDpAsState(if (isImageVisible) 8.dp else 12.dp).value,
            vertical = animateDpAsState(if (isImageVisible) 8.dp else 0.dp).value
        ),
        defaultMinSize = 32.dp,
        shape = ShapeDefaults.default,
        label = {
            MediaPickerChipContent(
                title = title,
                previewUri = previewUri,
                count = count.toLong(),
                showThumbnail = showThumbnail,
                showInlineCount = true,
                isLoading = isLoading
            )
        }
    )
}

@Composable
internal fun SemanticSearchCategory.title(): String = when (this) {
    is SemanticSearchCategory.BuiltIn -> stringResource(titleRes)
    is SemanticSearchCategory.Custom -> name
}

@get:StringRes
private val SemanticSearchCategory.BuiltIn.titleRes: Int
    get() = when (this) {
        SemanticSearchCategory.BuiltIn.People -> R.string.gallery_category_people
        SemanticSearchCategory.BuiltIn.Animals -> R.string.gallery_category_animals
        SemanticSearchCategory.BuiltIn.Nature -> R.string.gallery_category_nature
        SemanticSearchCategory.BuiltIn.Food -> R.string.gallery_category_food
        SemanticSearchCategory.BuiltIn.Documents -> R.string.gallery_category_documents
        SemanticSearchCategory.BuiltIn.Screenshots -> R.string.gallery_category_screenshots
        SemanticSearchCategory.BuiltIn.Vehicles -> R.string.gallery_category_vehicles
        SemanticSearchCategory.BuiltIn.Travel -> R.string.gallery_category_travel
        SemanticSearchCategory.BuiltIn.Architecture -> R.string.gallery_category_architecture
        SemanticSearchCategory.BuiltIn.Art -> R.string.gallery_category_art
        SemanticSearchCategory.BuiltIn.Sports -> R.string.gallery_category_sports
        SemanticSearchCategory.BuiltIn.Events -> R.string.gallery_category_events
        SemanticSearchCategory.BuiltIn.Night -> R.string.gallery_category_night
        SemanticSearchCategory.BuiltIn.Beach -> R.string.gallery_category_beach
        SemanticSearchCategory.BuiltIn.Mountains -> R.string.gallery_category_mountains
        SemanticSearchCategory.BuiltIn.Sunset -> R.string.gallery_category_sunset
    }