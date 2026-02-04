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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.MultipleStop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DownloadFile
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.negativePadding
import com.t8rin.imagetoolbox.core.ui.widget.other.GradientEdge
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.recognize.text.domain.OCRLanguage
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionType

@Composable
internal fun OCRLanguagesColumn(
    listState: LazyListState,
    allowMultipleLanguagesSelection: Boolean,
    value: List<OCRLanguage>,
    currentRecognitionType: RecognitionType,
    onValueChange: (List<OCRLanguage>, RecognitionType) -> Unit,
    onImportLanguages: () -> Unit,
    onExportLanguages: () -> Unit,
    downloadedLanguages: List<OCRLanguage>,
    notDownloadedLanguages: List<OCRLanguage>,
    onDeleteLanguage: (OCRLanguage, List<RecognitionType>) -> Unit,
    onToggleAllowMultipleLanguagesSelection: () -> Unit
) {
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

    DeleteLanguageDialog(
        languageToDelete = deleteDialogData,
        onDismiss = { deleteDialogData = null },
        onDeleteLanguage = onDeleteLanguage,
        currentRecognitionType = currentRecognitionType
    )

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            start = 16.dp,
            bottom = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        flingBehavior = enhancedFlingBehavior()
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .negativePadding(horizontal = 16.dp)
                    .background(EnhancedBottomSheetDefaults.containerColor)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                PreferenceRowSwitch(
                    title = stringResource(R.string.allow_multiple_languages),
                    containerColor = animateColorAsState(
                        if (allowMultipleLanguagesSelection) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainer
                    ).value,
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.extremeLarge,
                    checked = allowMultipleLanguagesSelection,
                    startIcon = Icons.Rounded.MultipleStop,
                    onClick = {
                        if (!it) {
                            onValueChange(
                                value.take(1),
                                currentRecognitionType
                            )
                        }
                        onToggleAllowMultipleLanguagesSelection()
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
                        shape = ShapeDefaults.large,
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
        itemsIndexed(
            items = downloadedLanguages,
            key = { _, l -> l.code }
        ) { index, lang ->
            DownloadedLanguageItem(
                index = index,
                value = value,
                lang = lang,
                downloadedLanguages = downloadedLanguages,
                onWantDelete = { deleteDialogData = it },
                onValueChange = { selected, language ->
                    onValueChangeImpl(
                        selected = selected,
                        type = currentRecognitionType,
                        lang = language
                    )
                },
                onValueChangeForced = onValueChange,
                currentRecognitionType = currentRecognitionType
            )
        }
        if (notDownloadedLanguages.isNotEmpty()) {
            item {
                TitleItem(
                    icon = Icons.Rounded.Download,
                    text = stringResource(id = R.string.available_languages)
                )
            }
        }
        itemsIndexed(
            items = notDownloadedLanguages,
            key = { _, l -> l.code }
        ) { index, lang ->
            val selected by remember(value, lang) {
                derivedStateOf {
                    lang in value
                }
            }
            PreferenceItem(
                title = lang.name,
                subtitle = lang.localizedName.takeIf { it != lang.name },
                onClick = {
                    onValueChangeImpl(
                        selected = selected,
                        type = currentRecognitionType,
                        lang = lang
                    )
                },
                containerColor = animateColorAsState(
                    if (selected) {
                        MaterialTheme.colorScheme.surfaceColorAtElevation(20.dp)
                    } else EnhancedBottomSheetDefaults.contentContainerColor
                ).value,
                shape = ShapeDefaults.byIndex(
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