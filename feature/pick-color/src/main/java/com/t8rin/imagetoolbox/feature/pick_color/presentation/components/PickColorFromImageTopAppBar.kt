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

package com.t8rin.imagetoolbox.feature.pick_color.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.parser.rememberColorParser
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.copyToClipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee

@Composable
internal fun PickColorFromImageTopAppBar(
    bitmap: Bitmap?,
    scrollBehavior: TopAppBarScrollBehavior,
    onGoBack: () -> Unit,
    isPortrait: Boolean,
    magnifierButton: @Composable () -> Unit,
    color: Color,
) {
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current

    val parser = rememberColorParser()

    val essentials = rememberLocalEssentials()

    AnimatedContent(
        modifier = Modifier.drawHorizontalStroke(),
        targetState = bitmap == null,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { noBmp ->
        if (noBmp) {
            EnhancedTopAppBar(
                type = EnhancedTopAppBarType.Large,
                scrollBehavior = scrollBehavior,
                drawHorizontalStroke = false,
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = onGoBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.pick_color),
                        modifier = Modifier.marquee()
                    )
                },
                actions = {
                    if (bitmap == null) {
                        TopAppBarEmoji()
                    }
                }
            )
        } else {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.animateContentSizeNoClip(),
            ) {
                Column {
                    Column(
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets.navigationBars
                                .only(WindowInsetsSides.End)
                                .union(WindowInsets.displayCutout)
                        )
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                EnhancedIconButton(
                                    onClick = onGoBack,
                                    modifier = Modifier.statusBarsPadding()
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = stringResource(R.string.exit)
                                    )
                                }
                                if (isPortrait) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    magnifierButton()
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                            if (!isPortrait) {
                                ProvideTextStyle(
                                    value = LocalTextStyle.current.merge(
                                        MaterialTheme.typography.headlineSmall
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(
                                                start = 16.dp,
                                                end = 16.dp
                                            )
                                            .statusBarsPadding()
                                    ) {
                                        Text(stringResource(R.string.color))

                                        Text(
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp)
                                                .clip(ShapeDefaults.mini)
                                                .hapticsClickable {
                                                    context.copyToClipboard(color.toHex())
                                                    essentials.showToast(
                                                        icon = Icons.Rounded.ContentPaste,
                                                        message = context.getString(R.string.color_copied)
                                                    )
                                                }
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .border(
                                                    width = settingsState.borderWidth,
                                                    color = MaterialTheme.colorScheme.outlineVariant(
                                                        onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                    ),
                                                    shape = ShapeDefaults.mini
                                                )
                                                .padding(horizontal = 6.dp),
                                            text = color.toHex(),
                                            style = LocalTextStyle.current.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        )

                                        Text(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(2.dp),
                                            text = remember(color) {
                                                derivedStateOf {
                                                    parser.parseColorName(color)
                                                }
                                            }.value
                                        )

                                        Box(
                                            Modifier
                                                .padding(
                                                    vertical = 4.dp,
                                                    horizontal = 16.dp
                                                )
                                                .background(
                                                    color = animateColorAsState(color).value,
                                                    shape = ShapeDefaults.small
                                                )
                                                .height(40.dp)
                                                .width(72.dp)
                                                .border(
                                                    width = settingsState.borderWidth,
                                                    color = MaterialTheme.colorScheme.outlineVariant(
                                                        onTopOf = animateColorAsState(color).value
                                                    ),
                                                    shape = ShapeDefaults.small
                                                )
                                                .clip(ShapeDefaults.small)
                                                .hapticsClickable {
                                                    context.copyToClipboard(color.toHex())
                                                    essentials.showToast(
                                                        icon = Icons.Rounded.ContentPaste,
                                                        message = context.getString(R.string.color_copied)
                                                    )
                                                }
                                        )
                                    }
                                }
                            }
                            Spacer(
                                Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                        }
                        if (isPortrait) {
                            Spacer(modifier = Modifier.height(8.dp))
                            ProvideTextStyle(
                                value = LocalTextStyle.current.merge(
                                    MaterialTheme.typography.headlineSmall
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                ) {
                                    Text(stringResource(R.string.color))

                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .clip(ShapeDefaults.mini)
                                            .hapticsClickable {
                                                context.copyToClipboard(color.toHex())
                                                essentials.showToast(
                                                    icon = Icons.Rounded.ContentPaste,
                                                    message = context.getString(R.string.color_copied)
                                                )
                                            }
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .border(
                                                width = settingsState.borderWidth,
                                                color = MaterialTheme.colorScheme.outlineVariant(
                                                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                ),
                                                shape = ShapeDefaults.mini
                                            )
                                            .padding(horizontal = 6.dp),
                                        text = color.toHex(),
                                        style = LocalTextStyle.current.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp)
                                    )

                                    Box(
                                        Modifier
                                            .padding(vertical = 4.dp)
                                            .background(
                                                color = animateColorAsState(color).value,
                                                shape = ShapeDefaults.small
                                            )
                                            .height(40.dp)
                                            .width(72.dp)
                                            .border(
                                                width = settingsState.borderWidth,
                                                color = MaterialTheme.colorScheme.outlineVariant(
                                                    onTopOf = animateColorAsState(color).value
                                                ),
                                                shape = ShapeDefaults.small
                                            )
                                            .clip(ShapeDefaults.small)
                                            .hapticsClickable {
                                                context.copyToClipboard(color.toHex())
                                                essentials.showToast(
                                                    icon = Icons.Rounded.ContentPaste,
                                                    message = context.getString(R.string.color_copied)
                                                )
                                            }
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}