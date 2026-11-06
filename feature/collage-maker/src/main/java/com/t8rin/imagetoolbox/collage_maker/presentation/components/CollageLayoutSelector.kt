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

package com.t8rin.imagetoolbox.collage_maker.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.collages.CollageTypeSelection
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges

@Composable
internal fun CollageLayoutSelector(
    component: CollageMakerComponent
) {
    val canChangeCollage = (component.uris?.size ?: 0) > 1

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(
                if (canChangeCollage) {
                    Modifier.container(
                        resultPadding = 0.dp,
                        shape = ShapeDefaults.extraLarge
                    )
                } else Modifier
            )
    ) {
        if (canChangeCollage) {
            Text(
                fontWeight = FontWeight.Medium,
                text = stringResource(R.string.collage_type),
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp
            )
        }
        val state = rememberLazyListState()
        CollageTypeSelection(
            state = state,
            imagesCount = component.uris?.size ?: 0,
            value = component.collageType,
            favorites = component.library.favorites,
            itemSize = 76.dp,
            itemBottomContent = { type ->
                val isFavorite = type.layoutId in component.library.favorites
                EnhancedIconButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    shape = ShapeDefaults.bottom,
                    pressedShape = ShapeDefaults.bottom,
                    forceMinimumInteractiveComponentSize = false,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    onClick = { type.layoutId?.let(component::toggleFavorite) }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.BookmarkRemove else Icons.Outlined.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            onValueChange = component::setCollageType,
            modifier = Modifier
                .fillMaxWidth()
                .height(142.dp)
                .fadingEdges(state),
            contentPadding = PaddingValues(16.dp),
            shape = ShapeDefaults.top,
            itemModifierFactory = { isSelected ->
                Modifier
                    .container(
                        resultPadding = 0.dp,
                        color = animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else MaterialTheme.colorScheme.surfaceContainerLowest,
                        ).value,
                        shape = ShapeDefaults.top
                    )
                    .padding(8.dp)
                    .clip(ShapeDefaults.extremeSmall)
            }
        )
    }
}