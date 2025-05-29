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

package com.t8rin.imagetoolbox.feature.gradient_maker.presentation

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShowOriginalButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.GradientMakerAppColorSchemeHandler
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.GradientMakerBottomButtons
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.GradientMakerCompareButton
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.GradientMakerControls
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.GradientMakerImagePreview
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.GradientMakerNoDataControls
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.model.GradientMakerType
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent

@Composable
fun GradientMakerContent(
    component: GradientMakerComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val screenType = component.screenType

    GradientMakerAppColorSchemeHandler(component)

    LaunchedEffect(screenType) {
        if (screenType == null) {
            component.resetState()
        }
    }
    val goBack = {
        if (screenType != null) {
            component.resetState()
        } else {
            component.onGoBack()
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        component.setUris(uris)
        component.updateGradientAlpha(0.5f)
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = screenType == null,
        canShowScreenData = screenType != null,
        title = {
            TopAppBarTitle(
                title = when (screenType) {
                    null, GradientMakerType.Default -> stringResource(R.string.gradient_maker)
                    GradientMakerType.Overlay -> stringResource(R.string.gradient_maker_type_image)
                    GradientMakerType.Mesh -> stringResource(R.string.mesh_gradients)
                    GradientMakerType.MeshOverlay -> stringResource(R.string.gradient_maker_type_image_mesh)
                },
                input = Unit,
                isLoading = false,
                size = null
            )
        },
        onGoBack = {
            if (component.haveChanges) showExitDialog = true
            else goBack()
        },
        actions = {
            if (component.uris.isNotEmpty()) {
                ShowOriginalButton(
                    onStateChange = component::setShowOriginal
                )
            }
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = component.brush != null,
                onShare = {
                    component.shareBitmaps(showConfetti)
                },
                onCopy = {
                    component.cacheCurrentImage(essentials::copyToClipboard)
                },
                onEdit = {
                    component.cacheImages {
                        editSheetData = it
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
        },
        topAppBarPersistentActions = {
            if (screenType == null) {
                TopAppBarEmoji()
            }

            GradientMakerCompareButton(component)
        },
        imagePreview = {
            GradientMakerImagePreview(component)
        },
        controls = {
            GradientMakerControls(component)
        },
        insetsForNoData = WindowInsets(0),
        noDataControls = {
            GradientMakerNoDataControls(component)
        },
        buttons = { actions ->
            GradientMakerBottomButtons(
                component = component,
                actions = actions,
                imagePicker = imagePicker
            )
        },
        forceImagePreviewToMax = component.showOriginal,
        contentPadding = animateDpAsState(
            if (screenType == null) 12.dp
            else 20.dp
        ).value
    )

    LoadingDialog(
        visible = component.isSaving || component.isImageLoading,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving,
    )

    ExitWithoutSavingDialog(
        onExit = goBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}