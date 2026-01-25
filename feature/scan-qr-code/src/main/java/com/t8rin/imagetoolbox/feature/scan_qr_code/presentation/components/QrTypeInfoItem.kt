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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun QrTypeInfoItem(
    qrType: QrType,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = qrType,
        transitionSpec = {
            if (initialState::class.isInstance(targetState)) {
                fadeIn(tween(150)) togetherWith fadeOut(tween(150))
            } else if (targetState !is QrType.Complex) {
                slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
            } else {
                slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) { type ->
        when (type) {
            is QrType.Complex -> {
                QrInfoItem(
                    qrInfo = rememberQrInfo(type),
                    modifier = modifier
                )
            }

            else -> Spacer(Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun QrInfoItem(
    qrInfo: QrInfo,
    modifier: Modifier,
) {
    if (qrInfo.data.isEmpty()) return

    val essentials = rememberLocalEssentials()

    Column(
        modifier = Modifier
            .then(modifier)
            .container(
                shape = ShapeDefaults.large,
                resultPadding = 0.dp
            )
            .padding(8.dp)
    ) {
        TitleItem(
            text = qrInfo.title,
            icon = qrInfo.icon,
            modifier = Modifier.padding(8.dp),
            endContent = {
                qrInfo.intent?.let {
                    EnhancedIconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            essentials.startActivity(qrInfo.intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = "open",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            qrInfo.data.forEachIndexed { index, (icon, text, canCopy) ->
                val interactionSource = remember(index) { MutableInteractionSource() }

                val shape = shapeByInteraction(
                    shape = ShapeDefaults.byIndex(
                        index = index,
                        size = qrInfo.data.size
                    ),
                    pressedShape = ShapeDefaults.pressed,
                    interactionSource = interactionSource
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = shape,
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 0.dp
                        )
                        .hapticsClickable(
                            enabled = canCopy,
                            onClick = { essentials.copyToClipboard(text) },
                            interactionSource = interactionSource,
                            indication = LocalIndication.current
                        )
                        .padding(
                            vertical = 10.dp,
                            horizontal = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (canCopy) {
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}