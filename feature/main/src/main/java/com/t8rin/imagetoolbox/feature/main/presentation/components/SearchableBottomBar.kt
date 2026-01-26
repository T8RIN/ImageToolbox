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

package com.t8rin.imagetoolbox.feature.main.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.APP_LINK
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Github
import com.t8rin.imagetoolbox.core.resources.icons.GooglePlay
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppVersion
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import com.t8rin.imagetoolbox.core.ui.utils.helper.asUnsafe
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.modifier.pulsate
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import kotlinx.coroutines.delay

@Composable
internal fun SearchableBottomBar(
    searching: Boolean,
    updateAvailable: Boolean,
    onTryGetUpdate: () -> Unit,
    screenSearchKeyword: String,
    onUpdateSearch: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    BottomAppBar(
        modifier = Modifier.drawHorizontalStroke(top = true),
        actions = {
            if (!searching) {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                        alpha = 0.5f
                    ),
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        onTopOf = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .pulsate(enabled = updateAvailable),
                    onClick = onTryGetUpdate
                ) {
                    Text(
                        stringResource(R.string.version) + " $AppVersion (${BuildConfig.VERSION_CODE})"
                    )
                }
            } else {
                val focus = remember {
                    FocusRequester()
                }
                LaunchedEffect(Unit) {
                    delay(100)
                    focus.requestFocus()
                }
                BackHandler {
                    onUpdateSearch("")
                    onCloseSearch()
                }
                ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                    RoundedTextField(
                        maxLines = 1,
                        hint = { Text(stringResource(id = R.string.search_here)) },
                        modifier = Modifier
                            .focusRequester(focus)
                            .padding(start = 6.dp)
                            .offset(2.dp, (-2).dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search,
                            autoCorrectEnabled = null
                        ),
                        value = screenSearchKeyword,
                        onValueChange = {
                            onUpdateSearch(it)
                        },
                        startIcon = {
                            EnhancedIconButton(
                                onClick = {
                                    onUpdateSearch("")
                                    onCloseSearch()
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
                                visible = screenSearchKeyword.isNotEmpty(),
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                EnhancedIconButton(
                                    onClick = {
                                        onUpdateSearch("")
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
            }
        },
        floatingActionButton = {
            val context = LocalContext.current
            val linkHandler = LocalUriHandler.current.asUnsafe()
            if (!searching) {
                EnhancedFloatingActionButton(
                    onClick = {
                        if (context.isInstalledFromPlayStore()) {
                            runCatching {
                                linkHandler.openUri("market://details?id=${context.packageName}")
                            }.onFailure {
                                linkHandler.openUri("https://play.google.com/store/apps/details?id=${context.packageName}")
                            }
                        } else {
                            linkHandler.openUri(APP_LINK)
                        }
                    },
                    modifier = Modifier.requiredSize(size = 56.dp),
                    content = {
                        if (context.isInstalledFromPlayStore()) {
                            Icon(
                                imageVector = Icons.Rounded.GooglePlay,
                                contentDescription = "Google Play",
                                modifier = Modifier.offset(1.5.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Github,
                                contentDescription = stringResource(R.string.github)
                            )
                        }
                    }
                )
            }
        }
    )
}