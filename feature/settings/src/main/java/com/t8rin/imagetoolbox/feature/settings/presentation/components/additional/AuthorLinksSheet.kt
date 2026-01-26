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

package com.t8rin.imagetoolbox.feature.settings.presentation.components.additional

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.AUTHOR_LINK
import com.t8rin.imagetoolbox.core.domain.AUTHOR_TG
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Forum
import com.t8rin.imagetoolbox.core.resources.icons.Github
import com.t8rin.imagetoolbox.core.resources.icons.Telegram
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContentColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults.bottom
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults.center
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults.top
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun AuthorLinksSheet(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        title = {
            TitleItem(
                text = stringResource(R.string.app_developer_nick),
                icon = Icons.Rounded.Forum
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss,
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            val essentials = rememberLocalEssentials()
            val linkHandler = LocalUriHandler.current
            val settingsState = LocalSettingsState.current


            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(16.dp))
                    CompositionLocalProvider(
                        LocalIconShapeContentColor provides MaterialTheme.colorScheme.onTertiaryContainer,
                        LocalIconShapeContainerColor provides MaterialTheme.colorScheme.tertiaryContainer.blend(
                            color = MaterialTheme.colorScheme.tertiary,
                            fraction = if (settingsState.isNightMode) 0.2f else 0.1f
                        )
                    ) {
                        PreferenceItem(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            onClick = {
                                linkHandler.openUri(AUTHOR_TG)
                            },
                            endIcon = Icons.Rounded.Link,
                            shape = top,
                            title = stringResource(R.string.telegram),
                            startIcon = Icons.Rounded.Telegram,
                            subtitle = stringResource(R.string.app_developer_nick),
                            overrideIconShapeContentColor = true
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            val mail = essentials.getString(R.string.developer_email)
                            runCatching {
                                essentials.startActivity(
                                    Intent(Intent.ACTION_SENDTO).apply {
                                        data = "mailto:$mail".toUri()
                                    }
                                )
                            }.onFailure {
                                essentials.shareText(mail)
                            }
                        },
                        shape = center,
                        endIcon = Icons.Rounded.Link,
                        title = stringResource(R.string.email),
                        startIcon = Icons.Rounded.AlternateEmail,
                        subtitle = stringResource(R.string.developer_email)
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            linkHandler.openUri(AUTHOR_LINK)
                        },
                        endIcon = Icons.Rounded.Link,
                        shape = bottom,
                        title = stringResource(R.string.github),
                        startIcon = Icons.Rounded.Github,
                        subtitle = stringResource(R.string.app_developer_nick)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    )
}