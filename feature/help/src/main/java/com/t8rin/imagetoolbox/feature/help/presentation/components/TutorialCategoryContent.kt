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

package com.t8rin.imagetoolbox.feature.help.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpCategory
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpTip

@Composable
internal fun TutorialCategoryContent(
    category: HelpCategory,
    filteredTips: List<HelpTip>,
    isSearching: Boolean,
    searchKeyword: String,
    onOpenTip: (HelpTip) -> Unit,
    onSearchingChange: (Boolean) -> Unit,
    onSearchKeywordChange: (String) -> Unit,
    onGoBack: () -> Unit
) {
    HelpScaffold(
        title = stringResource(category.title),
        onGoBack = onGoBack,
        isSearching = isSearching,
        searchKeyword = searchKeyword,
        onSearchingChange = onSearchingChange,
        onSearchKeywordChange = onSearchKeywordChange
    ) { contentPadding ->
        AnimatedContent(
            targetState = filteredTips.isNotEmpty(),
            modifier = Modifier.fillMaxSize()
        ) { haveData ->
            if (haveData) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(
                        items = filteredTips,
                        key = { _, tip -> tip.id }
                    ) { index, tip ->
                        PreferenceItem(
                            title = stringResource(tip.title),
                            subtitle = stringResource(tip.subtitle),
                            startIcon = tip.icon as? ImageVector,
                            shape = ShapeDefaults.byIndex(index, filteredTips.size),
                            onClick = { onOpenTip(tip) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            } else {
                HelpSearchEmptyContent(contentPadding)
            }
        }
    }
}