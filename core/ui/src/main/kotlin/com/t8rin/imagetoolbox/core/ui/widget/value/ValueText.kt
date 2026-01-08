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

package com.t8rin.imagetoolbox.core.ui.widget.value

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerColor
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText

@Composable
fun ValueText(
    modifier: Modifier = Modifier.padding(
        top = 8.dp,
        end = 8.dp
    ),
    value: Number,
    enabled: Boolean = true,
    valueSuffix: String = "",
    customText: String? = null,
    onClick: (() -> Unit)?,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer.copy(
        0.25f
    )
) {
    val text by remember(customText, value, valueSuffix) {
        derivedStateOf {
            customText ?: "${value.toString().trimTrailingZero()}$valueSuffix"
        }
    }
    val interactionSource = remember { MutableInteractionSource() }

    val shape = shapeByInteraction(
        shape = ButtonDefaults.shape,
        pressedShape = ButtonDefaults.pressedShape,
        interactionSource = interactionSource
    )
    ProvideContainerDefaults(
        color = LocalContainerColor.current
    ) {
        AnimatedContent(
            targetState = text,
            transitionSpec = { fadeIn(tween(100)) togetherWith fadeOut(tween(100)) },
            modifier = modifier
                .container(
                    shape = shape,
                    color = backgroundColor,
                    resultPadding = 0.dp,
                    autoShadowElevation = if (enabled) 0.7.dp else 0.dp
                )
        ) {
            AutoSizeText(
                text = it,
                color = LocalContentColor.current.copy(0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .then(
                        if (onClick != null) {
                            Modifier.hapticsClickable(
                                enabled = enabled,
                                onClick = onClick,
                                interactionSource = interactionSource,
                                indication = LocalIndication.current
                            )
                        } else Modifier
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                lineHeight = 18.sp
            )
        }
    }
}