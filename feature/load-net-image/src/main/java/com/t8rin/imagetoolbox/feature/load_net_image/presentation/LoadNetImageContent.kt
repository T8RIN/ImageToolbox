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

package com.t8rin.imagetoolbox.feature.load_net_image.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.takeUnless
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.components.LoadNetImageActionButtons
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.components.LoadNetImageAdaptiveActions
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.components.LoadNetImageTopAppBarActions
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.components.LoadNetImageUrlTextField
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.components.ParsedImagePreview
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.components.ParsedImagesSelection
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent

@Composable
fun LoadNetImageContent(
    component: LoadNetImageComponent
) {
    val isPortrait by isPortraitOrientationAsState()
    val essentials = rememberLocalEssentials()

    AutoContentBasedColors(component.bitmap)

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.load_image_from_net),
                input = component.bitmap,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = component.onGoBack,
        actions = {
            LoadNetImageAdaptiveActions(component)
        },
        topAppBarPersistentActions = {
            LoadNetImageTopAppBarActions(component)
        },
        imagePreview = {
            AnimatedContent(component.targetUrl.isEmpty()) { isEmpty ->
                if (isEmpty) {
                    ImageNotPickedWidget(
                        onPickImage = {
                            essentials.getTextFromClipboard {
                                component.updateTargetUrl(newUrl = it.toString())
                            }
                        },
                        modifier = Modifier.padding(20.dp),
                        text = stringResource(R.string.type_image_link),
                        containerColor = MaterialTheme
                            .colorScheme
                            .surfaceContainerLowest
                            .takeUnless(isPortrait)
                    )
                } else {
                    ParsedImagePreview(component)
                }
            }
        },
        controls = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LoadNetImageUrlTextField(component)
                ParsedImagesSelection(component)
            }
        },
        buttons = { actions ->
            LoadNetImageActionButtons(
                component = component,
                actions = actions
            )
        },
        showImagePreviewAsStickyHeader = component.targetUrl.isNotEmpty(),
        canShowScreenData = true
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving
    )
}