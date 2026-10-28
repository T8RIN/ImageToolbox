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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.helpStringResource
import com.t8rin.imagetoolbox.core.resources.icons.Lightbulb
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpCategory
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpTip

@Composable
internal fun HelpListContent(
    categories: List<HelpCategory>,
    showButtonHint: Boolean,
    onDismissButtonHint: (Boolean) -> Unit,
    filteredTips: List<HelpTip>,
    isSearching: Boolean,
    searchKeyword: String,
    onOpenCategory: (HelpCategory) -> Unit,
    onOpenTip: (HelpTip) -> Unit,
    onSearchingChange: (Boolean) -> Unit,
    onSearchKeywordChange: (String) -> Unit,
    onGoBack: () -> Unit
) {
    val settingsState = LocalSettingsState.current
    val showHelpTipsButtonHint = showButtonHint && settingsState.showHelpTipsButton &&
            !settingsState.isHelpTipsButtonHintDismissed

    HelpScaffold(
        title = stringResource(R.string.help_tips),
        onGoBack = onGoBack,
        isSearching = isSearching,
        searchKeyword = searchKeyword,
        onSearchingChange = onSearchingChange,
        onSearchKeywordChange = onSearchKeywordChange
    ) { contentPadding ->
        AnimatedContent(
            targetState = searchKeyword.isBlank() to filteredTips.isNotEmpty(),
            modifier = Modifier.fillMaxSize()
        ) { (isSearchEmpty, haveData) ->
            if (isSearchEmpty) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    if (showHelpTipsButtonHint) {
                        item(key = "help_tips_button_hint") {
                            PreferenceItem(
                                title = stringResource(R.string.show_help_tips_button),
                                subtitle = stringResource(R.string.help_tips_button_hint),
                                startIcon = Icons.TwoTone.Lightbulb,
                                bottomContent = {
                                    BoxWithConstraints {
                                        val width = maxWidth

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(
                                                space = 8.dp,
                                                alignment = Alignment.End
                                            )
                                        ) {
                                            EnhancedButton(
                                                onClick = { onDismissButtonHint(true) },
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                modifier = Modifier.width(width / 2)
                                            ) {
                                                Text(stringResource(R.string.disable))
                                            }
                                            EnhancedButton(
                                                onClick = { onDismissButtonHint(false) },
                                                modifier = Modifier.width(width / 2)
                                            ) {
                                                Text(stringResource(R.string.understood))
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                                    .padding(bottom = 8.dp)
                            )
                        }
                    }
                    itemsIndexed(
                        items = categories,
                        key = { _, category -> category.key }
                    ) { index, category ->
                        PreferenceItem(
                            title = stringResource(category.title),
                            subtitle = stringResource(category.subtitle),
                            startIcon = category.icon as? ImageVector,
                            shape = ShapeDefaults.byIndex(index, categories.size),
                            onClick = { onOpenCategory(category) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            } else if (haveData) {
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
                            title = helpStringResource(tip.title),
                            subtitle = helpStringResource(tip.subtitle),
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