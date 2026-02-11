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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.animation.CombinedMutableInteractionSource
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
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
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
    val favoriteInteractionSource = remember { MutableInteractionSource() }
    val copyInteractionSource = remember { MutableInteractionSource() }
    val interactionSource = remember {
        CombinedMutableInteractionSource(
            favoriteInteractionSource,
            copyInteractionSource
        )
    }

    val shape = shapeByInteraction(
        shape = ShapeDefaults.default,
        pressedShape = ShapeDefaults.pressed,
        interactionSource = interactionSource
    )
    Column(
        modifier = modifier
            .heightIn(min = 100.dp)
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (color.alpha < 1f) Modifier.transparencyChecker()
                else Modifier
            )
            .background(boxColor),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = color.toHex(),
                color = contentColor,
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        color = boxColor.copy(alpha = 1f),
                        shape = ShapeDefaults.mini
                    )
                    .padding(horizontal = 4.dp),
                fontSize = 12.sp
            )

            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = stringResource(R.string.copy),
                tint = contentColor.copy(0.8f),
                modifier = Modifier
                    .padding(4.dp)
                    .size(28.dp)
                    .clip(ShapeDefaults.mini)
                    .background(boxColor.copy(alpha = 1f))
                    .hapticsClickable(
                        indication = LocalIndication.current,
                        interactionSource = copyInteractionSource,
                        onClick = onCopy
                    )
                    .padding(4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = if (isFavorite) {
                    Icons.Rounded.BookmarkRemove
                } else {
                    Icons.Outlined.BookmarkBorder
                },
                contentDescription = stringResource(R.string.favorite),
                tint = contentColor.copy(0.8f),
                modifier = Modifier
                    .padding(4.dp)
                    .size(28.dp)
                    .clip(ShapeDefaults.mini)
                    .background(boxColor.copy(alpha = 1f))
                    .hapticsClickable(
                        indication = LocalIndication.current,
                        interactionSource = favoriteInteractionSource,
                        onClick = onToggleFavorite
                    )
                    .padding(4.dp)
            )

            Text(
                text = name,
                color = contentColor,
                modifier = Modifier
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

    var fav by remember {
        mutableStateOf(setOf<String>())
    }

    LazyVerticalGrid(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        flingBehavior = enhancedFlingBehavior(),
        columns = GridCells.Adaptive(150.dp)
    ) {
        items(colors) { colorWithName ->
            ColorWithNameItem(
                colorWithName = colorWithName,
                isFavorite = colorWithName.name in fav,
                onToggleFavorite = { fav = fav.toggle(colorWithName.name) },
                onCopy = {},
                modifier = Modifier.animateItem()
            )
        }
    }
}