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

package ru.tech.imageresizershrinker.core.ui.widget.controls.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.image.model.BlendingMode
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.entries
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText

@Composable
fun BlendingModeSelector(
    value: BlendingMode,
    onValueChange: (BlendingMode) -> Unit,
    entries: List<BlendingMode> = remember {
        mutableListOf<BlendingMode>().apply {
            add(BlendingMode.SrcOver)
            addAll(
                BlendingMode
                    .entries
                    .toList() - listOf(
                    BlendingMode.SrcOver,
                    BlendingMode.Clear,
                    BlendingMode.Src,
                    BlendingMode.Dst
                ).toSet()
            )
        }
    },
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    color: Color = MaterialTheme.colorScheme.surface
) {
    Column(
        modifier = modifier.container(
            shape = shape,
            color = color
        )
    ) {
        Text(
            fontWeight = FontWeight.Medium,
            text = stringResource(R.string.overlay_mode),
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, bottom = 8.dp)
        )
        val listState = rememberLazyListState()
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .fadingEdges(listState),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(entries) {
                val selected by remember(it, value) {
                    derivedStateOf {
                        value == it
                    }
                }
                EnhancedChip(
                    selected = selected,
                    onClick = {
                        onValueChange(it)
                    },
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    AutoSizeText(
                        text = it.toString(),
                        maxLines = 1
                    )
                }
            }
        }
    }
}