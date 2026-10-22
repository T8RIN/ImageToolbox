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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult

internal data class PeopleGroupSuggestion(
    val first: SemanticSearchCategoryResult,
    val second: SemanticSearchCategoryResult,
    val similarity: Float
) {
    val firstPerson get() = first.category as SemanticSearchCategory.Person
    val secondPerson get() = second.category as SemanticSearchCategory.Person
    val key get() = "${firstPerson.personId}:${secondPerson.personId}"
}

@Composable
internal fun MediaPeopleSuggestionsSheet(
    visible: Boolean,
    suggestions: List<PeopleGroupSuggestion>,
    resolvingPair: Pair<Long, Long>?,
    onResolve: (Long, Long, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    EnhancedModalBottomSheet(
        visible = visible,
        cancelable = resolvingPair == null,
        onDismiss = { if (!it && resolvingPair == null) onDismiss() },
        title = {
            TitleItem(
                text = stringResource(R.string.gallery_people_similar),
                subtitle = stringResource(R.string.gallery_people_similar_count, suggestions.size),
                icon = Icons.Rounded.Link
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = resolvingPair == null,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .enhancedVerticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val suggestion = suggestions.firstOrNull()
            if (suggestion == null) {
                InfoContainer(
                    text = stringResource(R.string.gallery_people_no_similar),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                val first = suggestion.firstPerson.personId
                val second = suggestion.secondPerson.personId
                val isResolving = resolvingPair == (first to second)
                var mergeRequested by rememberSaveable(suggestion.key) { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(shape = ShapeDefaults.top, resultPadding = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PeopleSuggestionPreview(
                        result = suggestion.first,
                        modifier = Modifier.weight(1f)
                    )
                    PeopleSuggestionPreview(
                        result = suggestion.second,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(shape = ShapeDefaults.bottom, resultPadding = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(Modifier.weight(1f)) {
                        EnhancedButton(
                            enabled = resolvingPair == null,
                            onClick = {
                                mergeRequested = false
                                onResolve(first, second, false)
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val loading = isResolving && !mergeRequested
                                Text(
                                    text = stringResource(R.string.gallery_people_different_people),
                                    modifier = Modifier.alpha(if (loading) 0f else 1f)
                                )
                                if (loading) {
                                    EnhancedCircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        EnhancedButton(
                            enabled = resolvingPair == null,
                            onClick = {
                                mergeRequested = true
                                onResolve(first, second, true)
                            },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val loading = isResolving && mergeRequested
                                Text(
                                    text = stringResource(R.string.gallery_people_same_person),
                                    modifier = Modifier.alpha(if (loading) 0f else 1f)
                                )
                                if (loading) {
                                    EnhancedCircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeopleSuggestionPreview(
    result: SemanticSearchCategoryResult,
    modifier: Modifier
) {
    val person = result.category as SemanticSearchCategory.Person
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Picture(
            model = person.thumbnail,
            modifier = Modifier.size(104.dp),
            shape = ShapeDefaults.small,
            showTransparencyChecker = false,
            size = 256
        )
        Text(
            text = person.title(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(R.string.gallery_people_photo_count, result.media.size),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}
