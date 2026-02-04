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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.APP_RELEASES
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.HtmlText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun DefaultUpdateSheet(
    changelog: String,
    tag: String,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        title = {},
        dragHandle = {
            EnhancedModalSheetDragHandle {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CompositionLocalProvider(
                        LocalContentColor.provides(MaterialTheme.colorScheme.onSurface),
                        LocalTextStyle.provides(MaterialTheme.typography.bodyLarge)
                    ) {
                        TitleItem(
                            text = stringResource(R.string.new_version, tag),
                            icon = Icons.Rounded.NewReleases
                        )
                    }
                }
            }
        },
        sheetContent = {
            ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                Box {
                    Column(
                        modifier = Modifier.align(Alignment.TopCenter),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HtmlText(
                            html = changelog.trimIndent(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .enhancedVerticalScroll(rememberScrollState())
                                .padding(
                                    start = 24.dp,
                                    end = 24.dp,
                                    top = 24.dp
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            val linkHandler = LocalUriHandler.current
            EnhancedButton(
                onClick = {
                    linkHandler.openUri("$APP_RELEASES/tag/${tag}")
                }
            ) {
                AutoSizeText(stringResource(id = R.string.update))
            }
        }
    )
}