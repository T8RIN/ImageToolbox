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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.widget.buttons.MediaCheckBox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction

@Composable
fun MediaStickyHeader(
    modifier: Modifier = Modifier,
    date: String,
    isChecked: Boolean,
    onChecked: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .padding(
                start = 8.dp,
                end = 12.dp,
                top = 14.dp,
                bottom = 14.dp
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val shape = shapeByInteraction(
            shape = ShapeDefaults.circle,
            pressedShape = ShapeDefaults.pressed,
            interactionSource = interactionSource
        )

        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .container(
                    shape = shape,
                    resultPadding = 0.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer
                        .blend(MaterialTheme.colorScheme.primary, 0.1f)
                        .copy(0.5f)
                )
                .hapticsCombinedClickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onLongClick = onChecked,
                    onClick = {
                        onChecked?.invoke()
                    }
                )
                .padding(
                    vertical = 6.dp,
                    horizontal = 12.dp
                )
        )
        AnimatedVisibility(
            visible = onChecked != null,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150))
        ) {
            MediaCheckBox(
                isChecked = isChecked,
                onCheck = { onChecked?.invoke() },
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true) {
    MediaStickyHeader(
        date = "Today",
        isChecked = true,
        onChecked = {}
    )
}