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

package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.containerFabBorder

@Composable
fun FabPreview(
    modifier: Modifier = Modifier,
    alignment: Alignment,
) {
    val shadowEnabled = LocalSettingsState.current.drawContainerShadows
    val elevation by animateDpAsState(if (shadowEnabled) 2.dp else 0.dp)
    Column(
        modifier = modifier
            .padding(4.dp)
            .autoElevatedBorder(shape = RoundedCornerShape(12.dp), autoElevation = elevation)
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .autoElevatedBorder(shape = shapes.small, autoElevation = elevation)
                .clip(shapes.small)
                .background(colorScheme.surfaceContainer)
                .fillMaxWidth(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(4.dp))
            EnhancedIconButton(
                onClick = {},
                modifier = Modifier
                    .size(25.dp)
                    .aspectRatio(1f),
                shape = CloverShape,
                containerColor = colorScheme.surfaceColorAtElevation(6.dp),
                contentColor = colorScheme.onSurfaceVariant
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = stringResource(R.string.pick_image),
                modifier = Modifier.padding(4.dp),
                fontSize = 3.sp,
                lineHeight = 4.sp,
                textAlign = TextAlign.Center,
                color = colorScheme.onSurfaceVariant
            )
        }
        val weight by animateFloatAsState(
            targetValue = when (alignment) {
                Alignment.BottomStart -> 0f
                Alignment.BottomCenter -> 0.5f
                else -> 1f
            }
        )

        val settingsState = LocalSettingsState.current
        CompositionLocalProvider(LocalContentColor provides colorScheme.onPrimaryContainer) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.weight(0.01f + weight))
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(22.dp)
                        .aspectRatio(1f)
                        .containerFabBorder(
                            shape = RoundedCornerShape(7.dp),
                            autoElevation = animateDpAsState(
                                if (settingsState.drawFabShadows) 3.dp
                                else 0.dp
                            ).value
                        )
                        .background(
                            color = colorScheme.primaryContainer,
                            shape = RoundedCornerShape(7.dp),
                        )
                        .clip(RoundedCornerShape(7.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null, Modifier.size(10.dp))
                }
                Spacer(modifier = Modifier.weight(1.01f - weight))
            }
        }
    }
}