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

package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.material.icons.rounded.MultipleStop
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.DownloadFile
import ru.tech.imageresizershrinker.core.resources.icons.MiniEdit
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.GradientEdge
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.recognize.text.domain.OCRLanguage
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecognizeLanguageSelector(
    currentRecognitionType: RecognitionType,
    value: List<OCRLanguage>,
    availableLanguages: List<OCRLanguage>,
    onValueChange: (List<OCRLanguage>, RecognitionType) -> Unit,
    onDeleteLanguage: (OCRLanguage, List<RecognitionType>) -> Unit,
    onImportLanguages: () -> Unit,
    onExportLanguages: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    val downloadedLanguages by remember(availableLanguages) {
        derivedStateOf {
            availableLanguages.filter {
                it.downloaded.isNotEmpty()
            }.sortedByDescending {
                it.downloaded.size
            }
        }
    }
    val notDownloadedLanguages by remember(availableLanguages, value) {
        derivedStateOf {
            availableLanguages.filter {
                it.downloaded.isEmpty()
            }.sortedByDescending {
                it in value
            }
        }
    }

    var showDetailedLanguageSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.language),
        subtitle = value.joinToString(separator = ", ") { it.localizedName },
        onClick = {
            showDetailedLanguageSheet = true
        },
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(24.dp),
        startIcon = Icons.Outlined.Language,
        endIcon = Icons.Rounded.MiniEdit
    )

    var allowMultipleLanguagesSelection by rememberSaveable {
        mutableStateOf(value.isNotEmpty())
    }

    fun onValueChangeImpl(
        selected: Boolean,
        type: RecognitionType,
        lang: OCRLanguage
    ) {
        if (allowMultipleLanguagesSelection) {
            if (selected) {
                onValueChange(
                    (value - lang).distinct(),
                    type
                )
            } else onValueChange(
                (value + lang).distinct(),
                type
            )
        } else onValueChange(listOf(lang), type)
    }

    var deleteDialogData by remember {
        mutableStateOf<OCRLanguage?>(null)
    }

    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }
    var searchKeyword by rememberSaveable {
        mutableStateOf("")
    }
    var languagesForSearch by remember {
        mutableStateOf(
            downloadedLanguages + notDownloadedLanguages
        )
    }

    LaunchedEffect(searchKeyword) {
        delay(400L) // Debounce calculations
        if (searchKeyword.isEmpty()) {
            languagesForSearch = downloadedLanguages + notDownloadedLanguages
            return@LaunchedEffect
        }

        languagesForSearch = (downloadedLanguages + notDownloadedLanguages).filter {
            it.name.contains(
                other = searchKeyword,
                ignoreCase = true
            ).or(
                it.localizedName.contains(
                    other = searchKeyword,
                    ignoreCase = true
                )
            )
        }.sortedBy { it.name }
    }

    EnhancedModalBottomSheet(
        visible = showDetailedLanguageSheet,
        onDismiss = {
            showDetailedLanguageSheet = it
        },
        enableBottomContentWeight = false,
        confirmButton = {},
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
                            text = stringResource(id = R.string.language),
                            icon = Icons.Outlined.Language
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
                            onClick = {
                                showDetailedLanguageSheet = false
                            }
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    }
                }
            }
        },
        sheetContent = {
            AnimatedContent(targetState = value.isEmpty()) { loading ->
                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                } else {
                    val listState = rememberLazyListState()
                    LaunchedEffect(downloadedLanguages) {
                        downloadedLanguages.indexOf(value.firstOrNull()).takeIf {
                            it != -1
                        }.let {
                            listState.scrollToItem(it ?: 0)
                        }
                    }

                    AnimatedContent(
                        targetState = isSearching to languagesForSearch.isNotEmpty()
                    ) { (searching, haveData) ->
                        if (searching) {
                            if (haveData) {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    itemsIndexed(languagesForSearch) { index, lang ->
                                        val selected by remember(value, lang) {
                                            derivedStateOf {
                                                lang in value
                                            }
                                        }
                                        PreferenceItem(
                                            title = lang.name,
                                            subtitle = lang.localizedName.takeIf { it != lang.name },
                                            onClick = {
                                                haptics.performHapticFeedback(
                                                    HapticFeedbackType.LongPress
                                                )
                                                onValueChangeImpl(
                                                    selected = selected,
                                                    type = currentRecognitionType,
                                                    lang = lang
                                                )
                                            },
                                            color = animateColorAsState(
                                                if (selected) {
                                                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                        20.dp
                                                    )
                                                } else EnhancedBottomSheetDefaults.contentContainerColor
                                            ).value,
                                            shape = ContainerShapeDefaults.shapeForIndex(
                                                index = index,
                                                size = languagesForSearch.size
                                            ),
                                            modifier = Modifier
                                                .animateItem()
                                                .fillMaxWidth()
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
                        } else {
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    bottom = 16.dp,
                                    end = 16.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                stickyHeader {
                                    Column(
                                        modifier = Modifier
                                            .layout { measurable, constraints ->
                                                val result = measurable.measure(
                                                    constraints.copy(
                                                        maxWidth = constraints.maxWidth + 32.dp.roundToPx()
                                                    )
                                                )
                                                layout(
                                                    result.measuredWidth,
                                                    result.measuredHeight
                                                ) {
                                                    result.place(0, 0)
                                                }
                                            }
                                            .background(EnhancedBottomSheetDefaults.containerColor)
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Spacer(modifier = Modifier.height(20.dp))
                                        PreferenceRowSwitch(
                                            title = stringResource(R.string.allow_multiple_languages),
                                            color = animateColorAsState(
                                                if (allowMultipleLanguagesSelection) MaterialTheme.colorScheme.primaryContainer
                                                else MaterialTheme.colorScheme.surfaceContainer
                                            ).value,
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(28.dp),
                                            checked = allowMultipleLanguagesSelection,
                                            startIcon = Icons.Rounded.MultipleStop,
                                            onClick = {
                                                if (!it) onValueChange(
                                                    value.take(1),
                                                    currentRecognitionType
                                                )
                                                allowMultipleLanguagesSelection = it
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    GradientEdge(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(16.dp),
                                        startColor = EnhancedBottomSheetDefaults.containerColor,
                                        endColor = Color.Transparent
                                    )
                                }
                                item {
                                    Column(
                                        modifier = Modifier
                                            .container(
                                                shape = RoundedCornerShape(20.dp),
                                                color = EnhancedBottomSheetDefaults.contentContainerColor,
                                                resultPadding = 0.dp
                                            )
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.backup_ocr_models),
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            FillableButton(
                                                onClick = onImportLanguages,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.DownloadFile,
                                                    contentDescription = null
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = stringResource(R.string.import_word))
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            FillableButton(
                                                onClick = onExportLanguages,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.UploadFile,
                                                    contentDescription = null
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = stringResource(R.string.export))
                                            }
                                        }
                                    }
                                }
                                if (downloadedLanguages.isNotEmpty()) {
                                    item {
                                        TitleItem(
                                            icon = Icons.Rounded.DownloadDone,
                                            text = stringResource(id = R.string.downloaded_languages)
                                        )
                                    }
                                }
                                itemsIndexed(downloadedLanguages) { index, lang ->
                                    DownloadedLanguageItem(
                                        index = index,
                                        value = value,
                                        lang = lang,
                                        downloadedLanguages = downloadedLanguages,
                                        onWantDelete = {
                                            deleteDialogData = it
                                        },
                                        onValueChange = { selected, lang ->
                                            onValueChangeImpl(
                                                selected = selected,
                                                type = currentRecognitionType,
                                                lang = lang
                                            )
                                        },
                                        onValueChangeForced = onValueChange,
                                        currentRecognitionType = currentRecognitionType
                                    )
                                }
                                if (notDownloadedLanguages.isNotEmpty()) {
                                    item {
                                        TitleItem(
                                            icon = Icons.Rounded.DownloadForOffline,
                                            text = stringResource(id = R.string.available_languages)
                                        )
                                    }
                                }
                                itemsIndexed(notDownloadedLanguages) { index, lang ->
                                    val selected by remember(value, lang) {
                                        derivedStateOf {
                                            lang in value
                                        }
                                    }
                                    PreferenceItem(
                                        title = lang.name,
                                        subtitle = lang.localizedName.takeIf { it != lang.name },
                                        onClick = {
                                            haptics.performHapticFeedback(
                                                HapticFeedbackType.LongPress
                                            )
                                            onValueChangeImpl(
                                                selected = selected,
                                                type = currentRecognitionType,
                                                lang = lang
                                            )
                                        },
                                        color = animateColorAsState(
                                            if (selected) {
                                                MaterialTheme.colorScheme.surfaceColorAtElevation(20.dp)
                                            } else EnhancedBottomSheetDefaults.contentContainerColor
                                        ).value,
                                        shape = ContainerShapeDefaults.shapeForIndex(
                                            index = index,
                                            size = notDownloadedLanguages.size
                                        ),
                                        modifier = Modifier
                                            .animateItem()
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    DeleteLanguageDialog(
        languageToDelete = deleteDialogData,
        onDismiss = { deleteDialogData = null },
        onDeleteLanguage = onDeleteLanguage,
        currentRecognitionType = currentRecognitionType
    )
}