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

package ru.tech.imageresizershrinker.core.ui.widget.image

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.FileOpen
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.shapes.MorphShape
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun ImageNotPickedWidget(
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.pick_image),
) {
    Column(
        modifier = modifier.container(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        ClickableActionIcon(
            icon = Icons.TwoTone.Image,
            onClick = onPickImage
        )
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FileNotPickedWidget(
    onPickFile: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.pick_file_to_start),
    icon: ImageVector = Icons.TwoTone.FileOpen
) {
    Column(
        modifier = modifier.container(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        ClickableActionIcon(
            icon = icon,
            onClick = onPickFile
        )
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ClickableActionIcon(
    icon: ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val percentage = animateFloatAsState(
        if (pressed) 1f
        else 0f
    )
    val scale by animateFloatAsState(
        if (pressed) 1f
        else 1.1f
    )
    val morph = remember {
        Morph(
            start = MaterialShapes.Cookie4Sided,
            end = MaterialShapes.Square
        )
    }
    val shape = MorphShape(
        morph = morph,
        percentage = percentage.value
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .scale(scale)
            .container(
                shape = shape,
                resultPadding = 0.dp,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            .hapticsClickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
            .scale(1f / scale)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}