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

package com.t8rin.imagetoolbox.color_library.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.parser.ColorNameParser
import com.smarttoolfactory.colordetector.parser.ColorWithName
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlinx.coroutines.runBlocking

@Composable
internal fun ColorWithNameItem(
    colorWithName: ColorWithName,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (color, name) = colorWithName

    val boxColor by animateColorAsState(color)
    val contentColor = boxColor.inverse(
        fraction = { cond ->
            if (cond) 0.8f
            else 0.5f
        },
        darkMode = boxColor.luminance() < 0.3f
    )
    val interactionSource = remember { MutableInteractionSource() }
    val shape = shapeByInteraction(
        shape = ShapeDefaults.default,
        pressedShape = ShapeDefaults.pressed,
        interactionSource = interactionSource
    )
    Box(
        modifier = modifier
            .heightIn(min = 96.dp)
            .fillMaxWidth()
            .clip(shape)
            .transparencyChecker()
            .background(boxColor)
            .hapticsClickable(
                indication = LocalIndication.current,
                interactionSource = interactionSource,
                onClick = onCopy
            )
    ) {
        Icon(
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = stringResource(R.string.copy),
            tint = contentColor,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(28.dp)
                .background(
                    color = boxColor.copy(alpha = 1f),
                    shape = ShapeDefaults.mini
                )
                .padding(4.dp)
        )

        Text(
            text = color.toHex(),
            color = contentColor,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .background(
                    color = boxColor.copy(alpha = 1f),
                    shape = ShapeDefaults.mini
                )
                .padding(horizontal = 4.dp),
            fontSize = 12.sp
        )

        Text(
            text = name,
            color = contentColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .background(
                    color = boxColor.copy(alpha = 1f),
                    shape = ShapeDefaults.mini
                )
                .padding(horizontal = 4.dp),
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true) {
    runBlocking {
        ColorNameParser.init(appContext)
    }

    val colors = remember {
        ColorNameParser.colorNames.values.sortedBy { it.name }.toList()
    }

    LazyVerticalGrid(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        flingBehavior = enhancedFlingBehavior(),
        columns = GridCells.Adaptive(150.dp)
    ) {
        items(colors) { colorWithName ->
            ColorWithNameItem(
                colorWithName = colorWithName,
                onCopy = {}
            )
        }
    }
}