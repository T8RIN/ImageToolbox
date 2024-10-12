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

package ru.tech.imageresizershrinker.core.ui.widget.value

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.utils.trimTrailingZero
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun ValueText(
    modifier: Modifier = Modifier.padding(
        top = 8.dp,
        end = 8.dp
    ),
    value: Number,
    enabled: Boolean = true,
    valueSuffix: String = "",
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer.copy(
        0.25f
    )
) {
    val haptics = LocalHapticFeedback.current
    AnimatedContent(
        targetState = value,
        transitionSpec = { fadeIn(tween(100)) togetherWith fadeOut(tween(100)) },
        modifier = modifier
            .container(
                shape = CircleShape,
                color = backgroundColor,
                resultPadding = 0.dp,
                autoShadowElevation = 0.7.dp
            )
    ) {
        Text(
            text = "${it.toString().trimTrailingZero()}$valueSuffix",
            color = LocalContentColor.current.copy(0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable(enabled = enabled) {
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            lineHeight = 18.sp
        )
    }
}