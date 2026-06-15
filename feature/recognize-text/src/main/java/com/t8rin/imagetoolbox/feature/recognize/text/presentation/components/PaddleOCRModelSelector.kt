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

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.Language
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.recognize.text.domain.PaddleOCRModel

@Composable
fun PaddleOCRModelSelector(
    value: PaddleOCRModel,
    updateKey: Int,
    isDownloaded: (PaddleOCRModel) -> Boolean,
    onValueChange: (PaddleOCRModel) -> Unit,
    onDeleteModel: (PaddleOCRModel) -> Unit
) {
    var showSelectionSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.language),
        subtitle = stringResource(id = value.localizedName).replaceFirstChar { it.titlecase() },
        onClick = {
            showSelectionSheet = true
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = ShapeDefaults.extraLarge,
        startIcon = Icons.Rounded.Language,
        endIcon = Icons.Rounded.MiniEdit
    )

    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }
    var searchKeyword by rememberSaveable {
        mutableStateOf("")
    }

    EnhancedModalBottomSheet(
        visible = showSelectionSheet,
        onDismiss = {
            showSelectionSheet = it
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
                                        imageVector = Icons.Rounded.ArrowBack,
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
                            text = stringResource(id = R.string.language),
                            icon = Icons.Rounded.Language
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        EnhancedIconButton(
                            onClick = { isSearching = true },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(R.string.search_here)
                            )
                        }
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                showSelectionSheet = false
                            }
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    }
                }
            }
        },
        sheetContent = {
            PaddleOCRModelSelectorSheetContent(
                value = value,
                updateKey = updateKey,
                isDownloaded = isDownloaded,
                searchKeyword = searchKeyword,
                isSearching = isSearching,
                onValueChange = onValueChange,
                onDeleteModel = onDeleteModel
            )
        }
    )
}

internal val PaddleOCRModel.localizedName: Int
    get() = when (this) {
        PaddleOCRModel.CJK -> R.string.paddle_ocr_model_universal
        PaddleOCRModel.Korean -> R.string.paddle_ocr_model_korean
        PaddleOCRModel.Latin -> R.string.paddle_ocr_model_latin
        PaddleOCRModel.EastSlavic -> R.string.paddle_ocr_model_east_slavic
        PaddleOCRModel.Thai -> R.string.paddle_ocr_model_thai
        PaddleOCRModel.Greek -> R.string.paddle_ocr_model_greek
        PaddleOCRModel.English -> R.string.paddle_ocr_model_english
        PaddleOCRModel.Cyrillic -> R.string.paddle_ocr_model_cyrillic
        PaddleOCRModel.Arabic -> R.string.paddle_ocr_model_arabic
        PaddleOCRModel.Devanagari -> R.string.paddle_ocr_model_devanagari
        PaddleOCRModel.Tamil -> R.string.paddle_ocr_model_tamil
        PaddleOCRModel.Telugu -> R.string.paddle_ocr_model_telugu
        PaddleOCRModel.UniversalV6 -> R.string.paddle_ocr_v6
    }