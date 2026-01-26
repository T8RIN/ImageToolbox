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

package com.t8rin.imagetoolbox.core.ui.widget.buttons

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction

@Composable
fun ShowOriginalButton(
    canShow: Boolean = true,
    onStateChange: (Boolean) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val shape = shapeByInteraction(
        shape = ShapeDefaults.circle,
        pressedShape = ShapeDefaults.mini,
        interactionSource = interactionSource
    )

    Box(
        modifier = Modifier
            .clip(shape)
            .indication(
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        haptics.longPress()
                        val press = PressInteraction.Press(it)
                        interactionSource.emit(press)
                        if (canShow) onStateChange(true)

                        tryAwaitRelease()
                        onStateChange(false)
                        interactionSource.emit(
                            PressInteraction.Release(
                                press
                            )
                        )
                    }
                )
            }
    ) {
        Icon(
            imageVector = Icons.Rounded.History,
            contentDescription = stringResource(R.string.original),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
        )
    }
}