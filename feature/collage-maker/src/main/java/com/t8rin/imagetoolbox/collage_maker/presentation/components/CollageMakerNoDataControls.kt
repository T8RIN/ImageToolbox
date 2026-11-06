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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImagesMode
import com.t8rin.imagetoolbox.core.resources.icons.Unarchive
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
internal fun CollageMakerNoDataControls(
    onPickImages: () -> Unit,
    onOpenProject: () -> Unit
) {
    val cutout = WindowInsets.displayCutout.asPaddingValues()
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxHeight(),
        columns = StaggeredGridCells.Adaptive(300.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalItemSpacing = 12.dp,
        contentPadding = PaddingValues(
            bottom = 12.dp + WindowInsets
                .navigationBars
                .asPaddingValues()
                .calculateBottomPadding(),
            top = 12.dp,
            end = 12.dp + cutout.calculateEndPadding(
                LocalLayoutDirection.current
            ),
            start = 12.dp + cutout.calculateStartPadding(
                LocalLayoutDirection.current
            )
        ),
        flingBehavior = enhancedFlingBehavior()
    ) {
        item {
            PreferenceItem(
                onClick = onPickImages,
                startIcon = Icons.Outlined.ImagesMode,
                title = stringResource(R.string.pick_images),
                subtitle = stringResource(R.string.collage_maker_sub),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            PreferenceItem(
                onClick = onOpenProject,
                startIcon = Icons.Outlined.Unarchive,
                title = stringResource(R.string.open_markup_project),
                subtitle = stringResource(R.string.open_markup_project_sub),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
