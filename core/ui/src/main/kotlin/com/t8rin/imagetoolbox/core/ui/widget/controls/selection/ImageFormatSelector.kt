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

@file:Suppress("UnnecessaryVariable")

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.rounded.Architecture
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormatGroup
import com.t8rin.imagetoolbox.core.domain.image.model.alphaContainedEntries
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState
import kotlinx.coroutines.launch


@Composable
fun ImageFormatSelector(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    entries: List<ImageFormatGroup> = ImageFormatGroup.entries,
    forceEnabled: Boolean = false,
    value: ImageFormat,
    onValueChange: (ImageFormat) -> Unit
) {
    val enabled = !LocalSettingsState.current.overwriteFiles || forceEnabled
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    val cannotChangeFormat: () -> Unit = {
        scope.launch {
            toastHostState.showToast(
                context.getString(R.string.cannot_change_image_format),
                Icons.Rounded.Architecture
            )
        }
    }

    val allFormats by remember(entries) {
        derivedStateOf {
            entries.flatMap { it.formats }
        }
    }

    LaunchedEffect(value, allFormats) {
        if (value !in allFormats) {
            onValueChange(
                if (ImageFormat.Png.Lossless in allFormats) {
                    ImageFormat.Png.Lossless
                } else {
                    allFormats.first()
                }
            )
        }
    }

    Box {
        Column(
            modifier = modifier
                .container(
                    shape = ShapeDefaults.extraLarge,
                    color = backgroundColor
                )
                .animateContentSizeNoClip()
                .alpha(if (enabled) 1f else 0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.image_format),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            val formats by remember(value) {
                derivedStateOf {
                    entries.firstOrNull {
                        value in it.formats
                    }?.formats ?: emptyList()
                }
            }
            val filteredFormats = formats.filteredFormats()
            val showBackgroundSelector = value !in ImageFormat.alphaContainedEntries

            val entriesSize = (if (filteredFormats.size > 1) 1 else 0)
                .plus(if (showBackgroundSelector) 1 else 0)
                .plus(1)

            AnimatedContent(
                targetState = entries.filtered(),
                modifier = Modifier.fillMaxWidth()
            ) { items ->
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .container(
                            shape = ShapeDefaults.byIndex(0, entriesSize),
                            color = MaterialTheme.colorScheme.surface
                        )
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    items.forEach {
                        EnhancedChip(
                            onClick = {
                                if (enabled) {
                                    onValueChange(it.formats[0])
                                } else cannotChangeFormat()
                            },
                            selected = value in it.formats,
                            label = {
                                Text(text = it.title)
                            },
                            selectedColor = MaterialTheme.colorScheme.tertiary,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = filteredFormats.size > 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .container(
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 0.dp,
                            shape = ShapeDefaults.byIndex(1, entriesSize),
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.compression_type),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedContent(
                        targetState = filteredFormats,
                        modifier = Modifier.fillMaxWidth()
                    ) { items ->
                        FlowRow(
                            verticalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items.forEach {
                                EnhancedChip(
                                    onClick = {
                                        if (enabled) {
                                            onValueChange(it)
                                        } else cannotChangeFormat()
                                    },
                                    selected = value == it,
                                    label = {
                                        Text(text = it.title)
                                    },
                                    selectedColor = MaterialTheme.colorScheme.tertiary,
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            val simpleSettingsInteractor = LocalSimpleSettingsInteractor.current
            val settingsState = LocalSettingsState.current
            AnimatedVisibility(
                visible = showBackgroundSelector,
                modifier = Modifier.fillMaxWidth()
            ) {
                val index = if (filteredFormats.size > 1) 2 else 1
                ColorRowSelector(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .container(
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 0.dp,
                            shape = ShapeDefaults.byIndex(index, entriesSize),
                        )
                        .fillMaxWidth(),
                    value = settingsState.backgroundForNoAlphaImageFormats,
                    icon = Icons.Outlined.FormatPaint,
                    onValueChange = {
                        scope.launch {
                            simpleSettingsInteractor.setBackgroundColorForNoAlphaFormats(
                                color = it.toModel()
                            )
                            val previous = value
                            onValueChange(
                                ImageFormat.entries.run {
                                    rightFrom(indexOf(value))
                                }
                            )
                            onValueChange(previous)
                        }
                    },
                    allowAlpha = false
                )
            }
            Spacer(Modifier.height(4.dp))
        }
        if (!enabled) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .matchParentSize()
                    .tappable {
                        cannotChangeFormat()
                    }
            ) {}
        }
    }
}

@Composable
private fun List<ImageFormatGroup>.filtered(): List<ImageFormatGroup> = remember(this) {
    if (Build.VERSION.SDK_INT <= 24) this - ImageFormatGroup.highLevelFormats.toSet()
    else this
}

@Composable
private fun List<ImageFormat>.filteredFormats(): List<ImageFormat> = remember(this) {
    if (Build.VERSION.SDK_INT <= 24) this - ImageFormat.highLevelFormats.toSet()
    else this
}