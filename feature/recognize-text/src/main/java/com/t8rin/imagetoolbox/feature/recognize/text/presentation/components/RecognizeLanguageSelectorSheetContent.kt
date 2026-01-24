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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.feature.recognize.text.domain.OCRLanguage
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionType
import kotlinx.coroutines.delay

@Composable
internal fun RecognizeLanguageSelectorSheetContent(
    value: List<OCRLanguage>,
    currentRecognitionType: RecognitionType,
    onValueChange: (List<OCRLanguage>, RecognitionType) -> Unit,
    onImportLanguages: () -> Unit,
    onExportLanguages: () -> Unit,
    isSearching: Boolean,
    searchKeyword: String,
    availableLanguages: List<OCRLanguage>,
    onDeleteLanguage: (OCRLanguage, List<RecognitionType>) -> Unit,
    allowMultipleLanguagesSelection: Boolean,
    onToggleAllowMultipleLanguagesSelection: () -> Unit
) {
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

    AnimatedContent(targetState = value.isEmpty()) { loading ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                EnhancedLoadingIndicator()
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
                        OCRLanguageColumnForSearch(
                            languagesForSearch = languagesForSearch,
                            value = value,
                            currentRecognitionType = currentRecognitionType,
                            onValueChange = onValueChange,
                            allowMultipleLanguagesSelection = allowMultipleLanguagesSelection
                        )
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
                    OCRLanguagesColumn(
                        listState = listState,
                        allowMultipleLanguagesSelection = allowMultipleLanguagesSelection,
                        value = value,
                        currentRecognitionType = currentRecognitionType,
                        onValueChange = onValueChange,
                        onImportLanguages = onImportLanguages,
                        onExportLanguages = onExportLanguages,
                        downloadedLanguages = downloadedLanguages,
                        notDownloadedLanguages = notDownloadedLanguages,
                        onDeleteLanguage = onDeleteLanguage,
                        onToggleAllowMultipleLanguagesSelection = onToggleAllowMultipleLanguagesSelection
                    )
                }
            }
        }
    }
}