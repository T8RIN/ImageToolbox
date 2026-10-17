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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult

@Composable
internal fun MediaSemanticSearchCategories(
    categories: List<SemanticSearchCategoryResult>,
    selectedCategory: SemanticSearchCategory?,
    onCategorySelected: (SemanticSearchCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val layoutDirection = LocalLayoutDirection.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = WindowInsets.displayCutout.asPaddingValues()
                    .calculateStartPadding(layoutDirection) + 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Neurology,
            contentDescription = stringResource(R.string.gallery_search_categories),
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(24.dp)
        )
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
                key = { it.category.name }
            ) { result ->
                val isSelected = selectedCategory == result.category
                CategoryChip(
                    title = stringResource(result.category.titleRes),
                    count = result.media.size,
                    selected = isSelected,
                    onClick = {
                        onCategorySelected(result.category.takeUnless { isSelected })
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
    onClick: () -> Unit,
    count: Int
) {
    EnhancedChip(
        selected = selected,
        onClick = onClick,
        selectedColor = MaterialTheme.colorScheme.tertiary,
        unselectedColor = MaterialTheme.colorScheme.tertiaryContainer,
        unselectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        contentPadding = PaddingValues(horizontal = 12.dp),
        defaultMinSize = 32.dp,
        shape = ShapeDefaults.circle,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title)
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalContentColor.current.copy(alpha = 0.65f)
                )
            }
        }
    )
}

@get:StringRes
private val SemanticSearchCategory.titleRes: Int
    get() = when (this) {
        SemanticSearchCategory.People -> R.string.gallery_category_people
        SemanticSearchCategory.Animals -> R.string.gallery_category_animals
        SemanticSearchCategory.Nature -> R.string.gallery_category_nature
        SemanticSearchCategory.Food -> R.string.gallery_category_food
        SemanticSearchCategory.Documents -> R.string.gallery_category_documents
        SemanticSearchCategory.Screenshots -> R.string.gallery_category_screenshots
        SemanticSearchCategory.Vehicles -> R.string.gallery_category_vehicles
        SemanticSearchCategory.Travel -> R.string.gallery_category_travel
        SemanticSearchCategory.Architecture -> R.string.gallery_category_architecture
        SemanticSearchCategory.Art -> R.string.gallery_category_art
        SemanticSearchCategory.Sports -> R.string.gallery_category_sports
        SemanticSearchCategory.Events -> R.string.gallery_category_events
        SemanticSearchCategory.Night -> R.string.gallery_category_night
        SemanticSearchCategory.Beach -> R.string.gallery_category_beach
        SemanticSearchCategory.Mountains -> R.string.gallery_category_mountains
        SemanticSearchCategory.Sunset -> R.string.gallery_category_sunset
    }