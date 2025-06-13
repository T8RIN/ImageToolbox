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

package com.t8rin.imagetoolbox.feature.pick_color.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.parser.rememberColorParser
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke

@Composable
internal fun PickColorFromImageBottomAppBar(
    bitmap: Bitmap?,
    isPortrait: Boolean,
    switch: @Composable () -> Unit,
    onOneTimePickImage: () -> Unit,
    onPickImage: () -> Unit,
    color: Color,
) {
    AnimatedVisibility(
        visible = bitmap != null && isPortrait,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val parser = rememberColorParser()

        BottomAppBar(
            modifier = Modifier
                .drawHorizontalStroke(true),
            actions = {
                switch()
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .offset(x = (-10).dp),
                    text = remember(color) {
                        derivedStateOf {
                            parser.parseColorName(color)
                        }
                    }.value,
                    textAlign = TextAlign.Center
                )
            },
            floatingActionButton = {
                EnhancedFloatingActionButton(
                    onClick = onPickImage,
                    onLongClick = onOneTimePickImage
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlt,
                        contentDescription = stringResource(R.string.pick_image_alt)
                    )
                }
            }
        )
    }
}