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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.BackgroundBehavior
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import kotlinx.coroutines.launch

@Composable
internal fun MarkupLayersTopAppBarActions(
    component: MarkupLayersComponent,
    scaffoldState: BottomSheetScaffoldState
) {
    val isPortrait by isPortraitOrientationAsState()

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    if (component.backgroundBehavior == BackgroundBehavior.None) TopAppBarEmoji()
    else {
        if (isPortrait) {
            EnhancedIconButton(
                onClick = {
                    essentials.launch {
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                            scaffoldState.bottomSheetState.partialExpand()
                        } else {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = stringResource(R.string.properties)
                )
            }
        }
        var editSheetData by remember {
            mutableStateOf(listOf<Uri>())
        }
        ShareButton(
            enabled = component.backgroundBehavior !is BackgroundBehavior.None,
            onShare = {
                component.shareBitmap(showConfetti)
            },
            onCopy = {
                component.cacheCurrentImage(essentials::copyToClipboard)
            },
            onEdit = {
                component.cacheCurrentImage { uri ->
                    editSheetData = listOf(uri)
                }
            }
        )
        ProcessImagesPreferenceSheet(
            uris = editSheetData,
            visible = editSheetData.isNotEmpty(),
            onDismiss = {
                editSheetData = emptyList()
            },
            onNavigate = component.onNavigate
        )
    }
}