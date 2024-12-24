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

package ru.tech.imageresizershrinker.feature.base64_tools.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.utils.isBase64
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Base64
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.haptics.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.feature.base64_tools.presentation.screenLogic.Base64ToolsComponent

@Composable
internal fun Base64ToolsTiles(component: Base64ToolsComponent) {
    val essentials = rememberLocalEssentials()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val pasteTile: @Composable RowScope.(shape: Shape) -> Unit = { shape ->
        Tile(
            onClick = {
                val text = clipboardManager.getText()?.text?.substringAfter(",") ?: ""
                if (isBase64(text)) {
                    component.setBase64(text)
                } else {
                    essentials.showToast(
                        message = context.getString(R.string.not_a_valid_base_64),
                        icon = Icons.Rounded.Base64
                    )
                }
            },
            shape = shape,
            icon = Icons.Rounded.ContentPaste,
            textRes = R.string.paste_base_64
        )
    }

    AnimatedContent(component.uri == null) { isNoUri ->
        if (isNoUri) {
            Row {
                pasteTile(CircleShape)
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.container(
                    shape = RoundedCornerShape(20.dp),
                    resultPadding = 8.dp
                )
            ) {
                Text(
                    text = stringResource(R.string.options),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        pasteTile(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 4.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 4.dp
                            )
                        )

                        Tile(
                            onClick = {
                                val text = buildAnnotatedString {
                                    append(component.base64String)
                                }
                                if (isBase64(component.base64String)) {
                                    clipboardManager.setText(text)
                                } else {
                                    essentials.showToast(
                                        message = context.getString(R.string.copy_not_a_valid_base_64),
                                        icon = Icons.Rounded.Base64
                                    )
                                }
                            },
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 16.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 4.dp
                            ),
                            icon = Icons.Rounded.CopyAll,
                            textRes = R.string.copy_base_64
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Tile(
                            onClick = {

                            },
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 4.dp
                            ),
                            icon = Icons.Outlined.Share,
                            textRes = R.string.share_base_64
                        )

                        Tile(
                            onClick = {

                            },
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 16.dp
                            ),
                            icon = Icons.Outlined.Save,
                            textRes = R.string.save_base_64
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.Tile(
    onClick: () -> Unit,
    shape: Shape,
    icon: ImageVector,
    textRes: Int
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .container(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                shape = shape,
                resultPadding = 0.dp
            )
            .hapticsClickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
        AutoSizeText(
            text = stringResource(id = textRes),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center
        )
    }
}