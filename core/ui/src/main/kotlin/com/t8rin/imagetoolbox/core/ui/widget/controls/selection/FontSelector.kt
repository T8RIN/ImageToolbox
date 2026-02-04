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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.UiFontFamily
import com.t8rin.imagetoolbox.core.ui.theme.ProvideTypography
import com.t8rin.imagetoolbox.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch

@Composable
fun FontSelector(
    value: UiFontFamily,
    onValueChange: (UiFontFamily) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.font),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = ShapeDefaults.large,
    behaveAsContainer: Boolean = true
) {
    Column(
        modifier = modifier.then(
            if (behaveAsContainer) {
                Modifier.container(
                    shape = shape,
                    color = containerColor
                )
            } else Modifier
        )
    ) {
        val fonts = UiFontFamily.entries

        var expanded by rememberSaveable { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            val scope = rememberCoroutineScope()
            val confettiHostState = LocalConfettiHostState.current
            val showConfetti: () -> Unit = {
                scope.launch {
                    confettiHostState.showConfetti()
                }
            }
            val rotation by animateFloatAsState(if (expanded) 180f else 0f)
            TitleItem(
                text = title,
                icon = if (behaveAsContainer) Icons.Outlined.TextFields else null,
                modifier = Modifier.padding(top = 12.dp, start = 12.dp, bottom = 8.dp)
            )
            EnhancedBadge(
                content = {
                    Text(fonts.size.toString())
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .padding(bottom = 12.dp)
                    .scaleOnTap {
                        showConfetti()
                    }
            )
            Spacer(modifier = Modifier.weight(1f))
            EnhancedIconButton(
                containerColor = Color.Transparent,
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Expand",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
        val state = rememberLazyStaggeredGridState()
        LazyHorizontalStaggeredGrid(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            state = state,
            horizontalItemSpacing = 8.dp,
            rows = StaggeredGridCells.Adaptive(30.dp),
            modifier = Modifier
                .heightIn(max = animateDpAsState(if (expanded) 140.dp else 52.dp).value)
                .fadingEdges(
                    scrollableState = state,
                    isVertical = false,
                    spanCount = 3
                ),
            contentPadding = PaddingValues(8.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            items(fonts) { font ->
                ProvideTypography(font) {
                    EnhancedChip(
                        selected = font == value,
                        onClick = {
                            onValueChange(font)
                        },
                        selectedColor = MaterialTheme.colorScheme.secondary,
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        AutoSizeText(
                            text = font.name
                                ?: stringResource(id = R.string.system),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}