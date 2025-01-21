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

package ru.tech.imageresizershrinker.feature.pick_color.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.helper.toHex
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee

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
                modifier = Modifier.animateContentSize(),
            ) {
                Column {
                    Column(Modifier.navBarsPaddingOnlyIfTheyAtTheEnd()) {
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
                                                .clip(RoundedCornerShape(8.dp))
                                                .hapticsClickable {
                                                    context.copyToClipboard(color.toHex())
                                                    essentials.showToast(
                                                        icon = Icons.Rounded.ContentPaste,
                                                        message = context.getString(R.string.color_copied)
                                                    )
                                                }
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .border(
                                                    settingsState.borderWidth,
                                                    MaterialTheme.colorScheme.outlineVariant(
                                                        onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                    ),
                                                    RoundedCornerShape(8.dp)
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
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .height(40.dp)
                                                .width(72.dp)
                                                .border(
                                                    width = settingsState.borderWidth,
                                                    color = MaterialTheme.colorScheme.outlineVariant(
                                                        onTopOf = animateColorAsState(color).value
                                                    ),
                                                    shape = RoundedCornerShape(11.dp)
                                                )
                                                .clip(RoundedCornerShape(12.dp))
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
                                            .clip(RoundedCornerShape(8.dp))
                                            .hapticsClickable {
                                                context.copyToClipboard(color.toHex())
                                                essentials.showToast(
                                                    icon = Icons.Rounded.ContentPaste,
                                                    message = context.getString(R.string.color_copied)
                                                )
                                            }
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .border(
                                                settingsState.borderWidth,
                                                MaterialTheme.colorScheme.outlineVariant(
                                                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                ),
                                                RoundedCornerShape(8.dp)
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
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .height(40.dp)
                                            .width(72.dp)
                                            .border(
                                                width = settingsState.borderWidth,
                                                color = MaterialTheme.colorScheme.outlineVariant(
                                                    onTopOf = animateColorAsState(color).value
                                                ),
                                                shape = RoundedCornerShape(11.dp)
                                            )
                                            .clip(RoundedCornerShape(12.dp))
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