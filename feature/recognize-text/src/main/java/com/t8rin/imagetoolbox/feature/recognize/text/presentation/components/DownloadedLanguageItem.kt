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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.Green
import com.t8rin.imagetoolbox.core.ui.theme.Red
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCheckbox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.feature.recognize.text.domain.OCRLanguage
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionType
import kotlinx.coroutines.launch

@Composable
internal fun LazyItemScope.DownloadedLanguageItem(
    index: Int,
    value: List<OCRLanguage>,
    lang: OCRLanguage,
    downloadedLanguages: List<OCRLanguage>,
    onWantDelete: (OCRLanguage) -> Unit,
    onValueChange: (Boolean, OCRLanguage) -> Unit,
    onValueChangeForced: (List<OCRLanguage>, RecognitionType) -> Unit,
    currentRecognitionType: RecognitionType
) {
    val settingsState = LocalSettingsState.current
    val selected by remember(value, lang) {
        derivedStateOf {
            lang in value
        }
    }
    val scope = rememberCoroutineScope()
    val state = rememberRevealState()
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isDragged by interactionSource.collectIsDraggedAsState()
    val shape = ShapeDefaults.byIndex(
        index = index,
        size = downloadedLanguages.size,
        forceDefault = isDragged
    )
    SwipeToReveal(
        state = state,
        modifier = Modifier.animateItem(),
        revealedContentEnd = {
            Box(
                Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = shape,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable {
                        scope.launch {
                            state.animateTo(RevealValue.Default)
                        }
                        onWantDelete(lang)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(end = 8.dp)
                        .align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        directions = setOf(RevealDirection.EndToStart),
        swipeableContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .container(
                        shape = shape,
                        color = animateColorAsState(
                            if (selected) {
                                MaterialTheme
                                    .colorScheme
                                    .mixedContainer
                                    .copy(0.8f)
                            } else EnhancedBottomSheetDefaults.contentContainerColor
                        ).value,
                        resultPadding = 0.dp
                    )
                    .hapticsCombinedClickable(
                        onLongClick = {
                            scope.launch {
                                state.animateTo(RevealValue.FullyRevealedStart)
                            }
                        },
                        onClick = {
                            onValueChange(selected, lang)
                        }
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = value.size > 1) {
                    LocalMinimumInteractiveComponentSize.ProvidesValue(
                        Dp.Unspecified
                    ) {
                        EnhancedCheckbox(
                            checked = selected,
                            onCheckedChange = {
                                onValueChange(selected, lang)
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = lang.name,
                        style = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    )
                    if (lang.name != lang.localizedName) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = lang.localizedName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 14.sp,
                            color = LocalContentColor.current.copy(alpha = 0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.container()
                ) {
                    RecognitionType.entries.forEach { type ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(ShapeDefaults.circle)
                                .hapticsClickable {
                                    onValueChangeForced(value, type)
                                }
                        ) {
                            val notDownloaded by remember(
                                type,
                                lang.downloaded
                            ) {
                                derivedStateOf {
                                    type !in lang.downloaded
                                }
                            }
                            val displayName by remember(type) {
                                derivedStateOf {
                                    type.displayName.first().uppercase()
                                }
                            }
                            val green = Green
                            val red = Red
                            val color by remember(
                                currentRecognitionType,
                                red,
                                green,
                                lang.downloaded
                            ) {
                                derivedStateOf {
                                    when (type) {
                                        currentRecognitionType -> if (type in lang.downloaded) {
                                            green
                                        } else red

                                        !in lang.downloaded -> red.copy(
                                            0.3f
                                        )

                                        else -> green.copy(0.3f)
                                    }
                                }
                            }
                            Text(
                                text = displayName,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Icon(
                                imageVector = if (notDownloaded) {
                                    Icons.Rounded.Cancel
                                } else Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = animateColorAsState(color).value,
                                modifier = Modifier
                                    .size(28.dp)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(),
                                        shape = ShapeDefaults.circle
                                    )
                            )
                        }
                    }
                }
            }
        },
        interactionSource = interactionSource
    )
}