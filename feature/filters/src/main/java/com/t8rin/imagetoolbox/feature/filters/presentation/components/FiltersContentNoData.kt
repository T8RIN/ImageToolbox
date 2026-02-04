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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withModifier
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.filters.presentation.screenLogic.FiltersComponent

@Composable
internal fun FiltersContentNoData(
    component: FiltersComponent,
    imagePicker: ImagePicker,
    pickSingleImagePicker: ImagePicker,
    tempSelectionUris: List<Uri>?
) {
    val isPortrait by isPortraitOrientationAsState()

    val preference1 = @Composable {
        BasicFilterPreference(
            onClick = imagePicker::pickImage,
            modifier = Modifier.fillMaxWidth()
        )
    }
    val preference2 = @Composable {
        MaskFilterPreference(
            onClick = pickSingleImagePicker::pickImage,
            modifier = Modifier.fillMaxWidth()
        )
    }
    if (isPortrait) {
        Column {
            preference1()
            Spacer(modifier = Modifier.height(8.dp))
            preference2()
        }
    } else {
        val direction = LocalLayoutDirection.current
        Row(
            modifier = Modifier.padding(
                WindowInsets.displayCutout.asPaddingValues()
                    .let {
                        PaddingValues(
                            start = it.calculateStartPadding(direction),
                            end = it.calculateEndPadding(direction)
                        )
                    }
            )
        ) {
            preference1.withModifier(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            preference2.withModifier(modifier = Modifier.weight(1f))
        }
    }

    EnhancedModalBottomSheet(
        visible = component.isSelectionFilterPickerVisible,
        onDismiss = {
            if (!it) component.hideSelectionFilterPicker()
        },
        confirmButton = {
            EnhancedButton(
                onClick = component::hideSelectionFilterPicker,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        sheetContent = {
            SideEffect {
                if (tempSelectionUris == null) {
                    component.hideSelectionFilterPicker()
                }
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(250.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(12.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                item {
                    BasicFilterPreference(
                        onClick = {
                            component.setBasicFilter(tempSelectionUris)
                            component.hideSelectionFilterPicker()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    MaskFilterPreference(
                        onClick = {
                            component.setMaskFilter(tempSelectionUris?.firstOrNull())
                            component.hideSelectionFilterPicker()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.pick_file),
                icon = Icons.Rounded.FileOpen
            )
        }
    )
}