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

package ru.tech.imageresizershrinker.feature.load_net_image.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.components.LoadNetImageActionButtons
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.components.LoadNetImageAdaptiveActions
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.components.LoadNetImageTopAppBarActions
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.components.LoadNetImageUrlTextField
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.components.ParsedImagePreview
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.components.ParsedImagesSelection
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent

@Composable
fun LoadNetImageContent(
    component: LoadNetImageComponent
) {
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
            ParsedImagePreview(component)
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
        canShowScreenData = true
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving
    )
}