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

package com.t8rin.imagetoolbox.feature.wallpapers_export.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberCurrentLifecycleEvent
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.other.FeatureNotAvailableContent
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.WallpapersResult
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.components.WallpapersActionButtons
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.components.WallpapersControls
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.components.WallpapersPreview
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.screenLogic.WallpapersExportComponent

@Composable
fun WallpapersExportContent(
    component: WallpapersExportComponent
) {
    val essentials = rememberLocalEssentials()

    if (essentials.isInstalledFromPlayStore()) {
        FeatureNotAvailableContent(
            title = {
                Text(
                    text = stringResource(R.string.wallpapers_export),
                    modifier = Modifier.marquee()
                )
            },
            onGoBack = component.onGoBack
        )
        return
    }

    val isPortrait by isPortraitOrientationAsState()
    val lifecycleEvent = rememberCurrentLifecycleEvent()

    LaunchedEffect(lifecycleEvent) {
        component.loadWallpapers()
    }

    AutoContentBasedColors(component.wallpapers.firstOrNull()?.imageUri)

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.wallpapers_export),
                input = component.wallpapers.takeIf { it.isNotEmpty() },
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = component.onGoBack,
        actions = {
            ShareButton(
                enabled = component.selectedImages.isNotEmpty(),
                onShare = {
                    component.performSharing(essentials::showConfetti)
                },
                onCopy = if (component.wallpapers.size == 1) {
                    { component.cacheImages { it.firstOrNull()?.let(essentials::copyToClipboard) } }
                } else null
            )
        },
        topAppBarPersistentActions = {
            if (isPortrait) {
                TopAppBarEmoji()
            }
        },
        imagePreview = {
            WallpapersPreview(component)
        },
        controls = {
            WallpapersControls(component)
        },
        buttons = { actions ->
            WallpapersActionButtons(
                component = component,
                actions = actions
            )
        },
        placeImagePreview = component.wallpapersState is WallpapersResult.Success,
        showImagePreviewAsStickyHeader = false,
        canShowScreenData = true
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving
    )
}