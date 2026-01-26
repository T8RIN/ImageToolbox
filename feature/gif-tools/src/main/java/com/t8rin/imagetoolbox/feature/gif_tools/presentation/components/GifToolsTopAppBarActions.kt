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

package com.t8rin.imagetoolbox.feature.gif_tools.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.screenLogic.GifToolsComponent

@Composable
internal fun RowScope.GifToolsTopAppBarActions(component: GifToolsComponent) {
    if (component.type == null) TopAppBarEmoji()
    val pagesSize by remember(component.gifFrames, component.convertedImageUris) {
        derivedStateOf {
            component.gifFrames.getFramePositions(component.convertedImageUris.size).size
        }
    }
    val isGifToImage = component.type is Screen.GifTools.Type.GifToImage
    AnimatedVisibility(
        visible = isGifToImage && pagesSize != component.convertedImageUris.size,
        enter = fadeIn() + scaleIn() + expandHorizontally(),
        exit = fadeOut() + scaleOut() + shrinkHorizontally()
    ) {
        EnhancedIconButton(
            onClick = component::selectAllConvertedImages
        ) {
            Icon(
                imageVector = Icons.Outlined.SelectAll,
                contentDescription = "Select All"
            )
        }
    }
    AnimatedVisibility(
        modifier = Modifier
            .padding(8.dp)
            .container(
                shape = ShapeDefaults.circle,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                resultPadding = 0.dp
            ),
        visible = isGifToImage && pagesSize != 0
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            pagesSize.takeIf { it != 0 }?.let {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = it.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            EnhancedIconButton(
                onClick = component::clearConvertedImagesSelection
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        }
    }
}