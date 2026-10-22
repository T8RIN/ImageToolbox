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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Person
import com.t8rin.imagetoolbox.core.resources.icons.Restore
import com.t8rin.imagetoolbox.core.resources.icons.Visibility
import com.t8rin.imagetoolbox.core.resources.icons.VisibilityOff
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCheckbox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent
import kotlin.math.roundToInt

@Composable
internal fun MediaPeopleOverview(
    component: MediaPickerComponent,
    gridState: LazyGridState,
    onEditPerson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by component.semanticSearchCategories.collectAsStateWithLifecycle()
    val state by component.semanticSearchState.collectAsStateWithLifecycle()
    val loading by component.isSemanticSearchCategoriesLoading.collectAsStateWithLifecycle()
    val people = remember(categories) {
        categories.filter { it.category is SemanticSearchCategory.Person }
            .sortedWith(
                compareByDescending<SemanticSearchCategoryResult> { it.media.size }
                    .thenBy { (it.category as SemanticSearchCategory.Person).personId }
            )
    }
    val visiblePeople = remember(people) {
        people.filterNot { (it.category as SemanticSearchCategory.Person).hidden }
    }
    val hiddenPeople = remember(people) {
        people.filter { (it.category as SemanticSearchCategory.Person).hidden }
    }
    val resolvedPairs = component.resolvedPeoplePairs
    val suggestions = remember(visiblePeople, resolvedPairs) {
        buildList {
            val available = visiblePeople.associateBy {
                (it.category as SemanticSearchCategory.Person).personId
            }
            visiblePeople.forEach { first ->
                val person = first.category as SemanticSearchCategory.Person
                person.similarPeople.filterKeys {
                    it > person.personId && (person.personId to it) !in resolvedPairs
                }.forEach { (id, score) ->
                    available[id]?.let { second ->
                        val other = second.category as SemanticSearchCategory.Person
                        val keepFirst = when {
                            person.name.isBlank() != other.name.isBlank() -> person.name.isNotBlank()
                            else -> first.media.size >= second.media.size
                        }
                        add(
                            if (keepFirst) PeopleGroupSuggestion(first, second, score)
                            else PeopleGroupSuggestion(second, first, score)
                        )
                    }
                }
            }
        }.sortedByDescending(PeopleGroupSuggestion::similarity)
    }
    var showSuggestions by rememberSaveable { mutableStateOf(false) }
    val hiddenListState = rememberLazyListState()
    val layoutDirection = LocalLayoutDirection.current
    val cutout = WindowInsets.displayCutout.asPaddingValues()
    val navBar = WindowInsets.navigationBars.asPaddingValues()
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = cutout.calculateStartPadding(layoutDirection),
                end = cutout.calculateEndPadding(layoutDirection)
            ),
        state = gridState,
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp,
            bottom = navBar.calculateBottomPadding() + 12.dp
        ),
        flingBehavior = enhancedFlingBehavior(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (hiddenPeople.isNotEmpty()) {
            item(key = "hidden", span = { GridItemSpan(maxLineSpan) }) {
                ExpandableItem(
                    modifier = Modifier.fillMaxWidth(),
                    visibleContent = {
                        TitleItem(
                            text = stringResource(R.string.gallery_people_hidden_groups),
                            icon = Icons.Rounded.VisibilityOff,
                            subtitle = null,
                            modifier = Modifier.padding(8.dp),
                            endContent = {
                                Text(
                                    text = hiddenPeople.size.toString(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    },
                    expandableContent = {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fadingEdges(hiddenListState),
                            state = hiddenListState,
                            contentPadding = PaddingValues(
                                start = 12.dp,
                                end = 12.dp,
                                bottom = 4.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            flingBehavior = enhancedFlingBehavior()
                        ) {
                            items(hiddenPeople, key = { it.category.id }) { result ->
                                PeopleGroupCard(
                                    result = result,
                                    onClick = { component.selectSemanticCategory(result.category.id) },
                                    onLongClick = { onEditPerson(result.category.id) },
                                    modifier = Modifier.width(120.dp),
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    },
                    shape = ShapeDefaults.extraLarge
                )
            }
        }
        if (suggestions.isNotEmpty()) {
            item(key = "similar", span = { GridItemSpan(maxLineSpan) }) {
                PreferenceItem(
                    title = stringResource(R.string.gallery_people_similar),
                    subtitle = stringResource(
                        R.string.gallery_people_similar_count,
                        suggestions.size
                    ),
                    startIcon = Icons.Rounded.Link,
                    endIcon = Icons.Rounded.MiniEdit,
                    onClick = { showSuggestions = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (visiblePeople.isEmpty()) {
            item(key = "empty", span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (loading || (people.isEmpty() && state.isFacesIndexing)) {
                        EnhancedLoadingIndicator()
                    } else {
                        InfoContainer(
                            text = stringResource(
                                if (hiddenPeople.isNotEmpty()) R.string.gallery_people_all_hidden
                                else R.string.gallery_people_empty
                            ),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.85f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        items(visiblePeople, key = { it.category.id }) { result ->
            PeopleGroupCard(
                result = result,
                onClick = { component.selectSemanticCategory(result.category.id) },
                onLongClick = { onEditPerson(result.category.id) }
            )
        }
    }
    MediaPeopleSuggestionsSheet(
        visible = showSuggestions,
        suggestions = suggestions,
        resolvingPair = component.resolvingPeoplePair,
        onResolve = component::resolvePeoplePair,
        onDismiss = { showSuggestions = false }
    )
}

@Composable
private fun PeopleGroupCard(
    result: SemanticSearchCategoryResult,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified
) {
    val person = result.category as SemanticSearchCategory.Person
    Column(
        modifier = modifier
            .fillMaxWidth()
            .container(shape = ShapeDefaults.default, resultPadding = 0.dp, color = containerColor)
            .hapticsCombinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Picture(
            model = person.thumbnail,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = ShapeDefaults.top,
            showTransparencyChecker = false,
            size = 512
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .hapticsCombinedClickable(onClick = onLongClick, onLongClick = onLongClick)
                .padding(8.dp)
        ) {
            Text(
                text = person.name.ifBlank {
                    stringResource(R.string.gallery_people_add_name)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (person.name.isBlank()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.gallery_people_photo_count, result.media.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun PeopleGroupSettings(
    result: SemanticSearchCategoryResult,
    people: List<SemanticSearchCategoryResult>,
    component: MediaPickerComponent,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val person = result.category as SemanticSearchCategory.Person
    var name by rememberSaveable(person.id) { mutableStateOf(person.name) }
    var threshold by rememberSaveable(person.id) { mutableFloatStateOf(person.threshold) }
    var coverFaceId by rememberSaveable(person.id) {
        mutableStateOf(person.coverFaceId ?: person.faces.firstOrNull()?.id)
    }
    var excludedFaces by rememberSaveable(person.id) { mutableStateOf(emptyList<String>()) }
    var restoreExcluded by rememberSaveable(person.id) { mutableStateOf(false) }
    var merging by rememberSaveable(person.id) { mutableStateOf(false) }
    var showHideConfirmation by rememberSaveable(person.id) { mutableStateOf(false) }
    val saving = component.isSavingPersonSettings
    val focusManager = LocalFocusManager.current
    val photoCount = remember(person.faces, excludedFaces) {
        person.faces.filterNot { it.id in excludedFaces }.distinctBy { it.uri }.size
    }
    val mergeCandidates = remember(people, person.id, person.similarPeople) {
        val relevance = person.similarPeople
        people.filter {
            it.category.id != person.id && !(it.category as SemanticSearchCategory.Person).hidden
        }.sortedByDescending {
            relevance[(it.category as SemanticSearchCategory.Person).personId] ?: -1f
        }
    }
    var mergingPeople by remember(person.id) { mutableStateOf(mergeCandidates) }
    var wasVisible by rememberSaveable(person.id) { mutableStateOf(false) }
    LaunchedEffect(visible, person.id) {
        if (visible && !wasVisible) {
            name = person.name
            threshold = person.threshold
            coverFaceId = person.coverFaceId ?: person.faces.firstOrNull()?.id
            excludedFaces = emptyList()
            restoreExcluded = false
            merging = false
            showHideConfirmation = false
        }
        wasVisible = visible
    }

    suspend fun saveChanges(): Boolean = component.savePerson(
        id = person.personId,
        name = name,
        threshold = threshold,
        cover = person.faces.firstOrNull { it.id == coverFaceId }?.uri,
        restoreExcluded = restoreExcluded,
        excludedFaces = excludedFaces.toSet(),
        coverFaceId = coverFaceId
    )

    fun save(action: suspend () -> Boolean) {
        focusManager.clearFocus()
        component.savePersonSettings(action)
    }
    EnhancedModalBottomSheet(
        visible = visible,
        cancelable = !saving,
        onDismiss = {
            if (!it && !saving) {
                focusManager.clearFocus()
                onDismiss()
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.gallery_people_group),
                subtitle = stringResource(R.string.gallery_people_photo_count, photoCount),
                icon = Icons.Outlined.Person
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = !saving,
                onClick = {
                    save { saveChanges() }
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.save),
                        modifier = Modifier.alpha(if (saving) 0f else 1f)
                    )
                    if (saving) {
                        EnhancedCircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .enhancedVerticalScroll(rememberScrollState())
                .clearFocusOnTap()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RoundedTextField(
                value = name,
                onValueChange = { name = it },
                enabled = !saving,
                label = stringResource(R.string.gallery_people_name),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                shape = ShapeDefaults.top,
                modifier = Modifier
                    .fillMaxWidth()
                    .container(shape = ShapeDefaults.top, resultPadding = 8.dp)
            )
            EnhancedSliderItem(
                value = (threshold * 100).roundToInt(),
                valueRange = 50f..85f,
                title = stringResource(R.string.gallery_people_threshold),
                onValueChange = { threshold = it.roundToInt() / 100f },
                internalStateTransformation = Float::roundToInt,
                valueSuffix = "%",
                enabled = !saving,
                shape = ShapeDefaults.bottom,
                modifier = Modifier.fillMaxWidth()
            )
            InfoContainer(
                text = stringResource(R.string.gallery_people_threshold_description),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.85f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            if (person.faces.isNotEmpty()) {
                PeopleGroupFaces(
                    faces = person.faces,
                    coverFaceId = coverFaceId,
                    excludedFaces = excludedFaces,
                    enabled = !saving,
                    onSelectCover = { coverFaceId = it },
                    onExclude = { faceId ->
                        excludedFaces = if (faceId in excludedFaces) {
                            excludedFaces - faceId
                        } else excludedFaces + faceId
                        if (coverFaceId == null || coverFaceId in excludedFaces) {
                            coverFaceId = person.faces.firstOrNull { it.id !in excludedFaces }?.id
                        }
                    }
                )
            }
            if (person.excludedPhotos > 0) {
                PreferenceRowSwitch(
                    title = stringResource(R.string.gallery_people_restore_faces),
                    startIcon = Icons.Rounded.Restore,
                    checked = restoreExcluded,
                    enabled = !saving,
                    resultModifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = { restoreExcluded = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (mergeCandidates.isNotEmpty()) {
                PreferenceItem(
                    title = stringResource(R.string.gallery_people_merge),
                    subtitle = stringResource(R.string.gallery_people_merge_description),
                    startIcon = Icons.Rounded.Link,
                    enabled = !saving,
                    onClick = {
                        focusManager.clearFocus()
                        mergingPeople = mergeCandidates
                        merging = true
                    },
                    endIcon = Icons.Rounded.MiniEdit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            PreferenceItem(
                title = stringResource(
                    if (person.hidden) R.string.gallery_people_restore
                    else R.string.gallery_people_hide
                ),
                subtitle = stringResource(R.string.gallery_people_hide_description).takeUnless { person.hidden },
                startIcon = if (person.hidden) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                enabled = !saving,
                onClick = {
                    focusManager.clearFocus()
                    if (person.hidden) {
                        save {
                            saveChanges() && component.setPersonHidden(person.personId, false)
                        }
                    } else showHideConfirmation = true
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    PeopleMergeSheet(
        visible = visible && merging,
        people = mergingPeople,
        enabled = !saving,
        onDismiss = { merging = false },
        onMerge = { sources ->
            save {
                saveChanges() && component.mergePeople(sources, person.personId)
            }
        }
    )
    EnhancedAlertDialog(
        visible = visible && showHideConfirmation,
        onDismissRequest = { showHideConfirmation = false },
        icon = { Icon(Icons.Rounded.VisibilityOff, contentDescription = null) },
        title = { Text(stringResource(R.string.gallery_people_hide)) },
        text = {
            Text(
                stringResource(
                    R.string.gallery_people_hide_confirmation,
                    name.ifBlank { person.title() }
                )
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showHideConfirmation = false
                    save {
                        saveChanges() && component.setPersonHidden(person.personId, true)
                    }
                }
            ) {
                Text(stringResource(R.string.gallery_people_hide))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showHideConfirmation = false }
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        placeAboveAll = true
    )
}

@Composable
private fun PeopleGroupFaces(
    faces: List<SemanticSearchCategory.PersonFace>,
    coverFaceId: String?,
    excludedFaces: List<String>,
    enabled: Boolean,
    onSelectCover: (String) -> Unit,
    onExclude: (String) -> Unit
) {
    PreferenceItem(
        title = stringResource(R.string.gallery_people_cover),
        subtitle = stringResource(R.string.gallery_people_cover_description),
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        bottomContent = {
            LazyRow(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(faces, key = { it.id }) { face ->
                    val excluded = face.id in excludedFaces
                    val selected = !excluded && coverFaceId == face.id
                    Box {
                        EnhancedChip(
                            selected = selected,
                            selectedColor = MaterialTheme.colorScheme.primaryContainer,
                            onClick = if (enabled && !excluded) ({ onSelectCover(face.id) }) else null,
                            contentPadding = PaddingValues(
                                animateDpAsState(if (selected) 6.dp else 0.dp).value
                            ),
                            modifier = Modifier.size(72.dp),
                            shape = ShapeDefaults.small,
                            label = {
                                Picture(
                                    model = face.thumbnail,
                                    shape = ShapeDefaults.extraSmall,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(if (excluded) 0.4f else 1f),
                                    size = 256
                                )
                            }
                        )
                        EnhancedIconButton(
                            enabled = enabled,
                            onClick = { onExclude(face.id) },
                            containerColor = if (excluded) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            },
                            contentColor = if (excluded) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (excluded) Icons.Rounded.Restore else Icons.Outlined.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun PeopleMergeSheet(
    visible: Boolean,
    people: List<SemanticSearchCategoryResult>,
    enabled: Boolean,
    onDismiss: () -> Unit,
    onMerge: (Set<Long>) -> Unit
) {
    var selectedIds by rememberSaveable { mutableStateOf(emptyList<Long>()) }
    var wasVisible by rememberSaveable { mutableStateOf(false) }
    val selected = remember(people, selectedIds) {
        people.mapNotNull {
            (it.category as SemanticSearchCategory.Person).personId.takeIf { id -> id in selectedIds }
        }.toSet()
    }
    LaunchedEffect(visible) {
        if (visible && !wasVisible) selectedIds = emptyList()
        wasVisible = visible
    }
    EnhancedModalBottomSheet(
        visible = visible,
        cancelable = enabled,
        onDismiss = { if (!it && enabled) onDismiss() },
        title = {
            TitleItem(
                text = stringResource(R.string.gallery_people_merge),
                icon = Icons.Rounded.Link
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = enabled && selected.isNotEmpty(),
                onClick = { onMerge(selected) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.apply),
                        modifier = Modifier.alpha(if (enabled) 1f else 0f)
                    )
                    if (!enabled) {
                        EnhancedCircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                InfoContainer(
                    text = stringResource(R.string.gallery_people_merge_hint),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.85f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
            }
            itemsIndexed(people, key = { _, result -> result.category.id }) { index, result ->
                val person = result.category as SemanticSearchCategory.Person
                val isSelected = person.personId in selected
                val toggle = {
                    selectedIds = if (isSelected) selectedIds - person.personId
                    else selectedIds + person.personId
                }
                PreferenceItemOverload(
                    title = person.title(),
                    subtitle = stringResource(
                        R.string.gallery_people_photo_count,
                        result.media.size
                    ),
                    animateTitle = false,
                    enabled = enabled,
                    onClick = toggle,
                    shape = ShapeDefaults.byIndex(index, people.size),
                    modifier = Modifier.fillMaxWidth(),
                    drawStartIconContainer = false,
                    startIcon = {
                        Picture(
                            model = person.thumbnail,
                            modifier = Modifier.size(56.dp),
                            shape = ShapeDefaults.small,
                            showTransparencyChecker = false,
                            size = 256
                        )
                    },
                    endIcon = {
                        EnhancedCheckbox(
                            checked = isSelected,
                            enabled = enabled,
                            onCheckedChange = { toggle() }
                        )
                    }
                )
            }
        }
    }
}