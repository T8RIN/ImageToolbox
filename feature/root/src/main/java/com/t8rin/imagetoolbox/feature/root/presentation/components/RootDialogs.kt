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

package com.t8rin.imagetoolbox.feature.root.presentation.components

import androidx.compose.runtime.Composable
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalEditPresetsController
import com.t8rin.imagetoolbox.core.ui.utils.helper.ReviewHandler
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.UpdateSheet
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.root.presentation.components.dialogs.AppExitDialog
import com.t8rin.imagetoolbox.feature.root.presentation.components.dialogs.EditPresetsSheet
import com.t8rin.imagetoolbox.feature.root.presentation.components.dialogs.FirstLaunchSetupDialog
import com.t8rin.imagetoolbox.feature.root.presentation.components.dialogs.GithubReviewDialog
import com.t8rin.imagetoolbox.feature.root.presentation.components.dialogs.PermissionDialog
import com.t8rin.imagetoolbox.feature.root.presentation.components.dialogs.TelegramGroupDialog
import com.t8rin.imagetoolbox.feature.root.presentation.components.utils.HandleLookForUpdates
import com.t8rin.imagetoolbox.feature.root.presentation.components.utils.SuccessRestoreBackupToastHandler
import com.t8rin.imagetoolbox.feature.root.presentation.screenLogic.RootComponent
import com.t8rin.imagetoolbox.feature.settings.presentation.components.additional.DonateDialog

@Composable
internal fun RootDialogs(component: RootComponent) {
    val editPresetsController = LocalEditPresetsController.current

    AppExitDialog(component)

    EditPresetsSheet(
        visible = editPresetsController.isVisible,
        onDismiss = editPresetsController::close,
        onUpdatePresets = component::setPresets
    )

    val essentials = rememberLocalEssentials()
    ProcessImagesPreferenceSheet(
        uris = component.uris ?: emptyList(),
        extraDataType = component.extraDataType,
        visible = component.showSelectDialog,
        onDismiss = component::hideSelectDialog,
        onNavigate = { screen ->
            component.navigateTo(screen)
            essentials.clearClipboard()
        }
    )

    UpdateSheet(
        tag = component.tag,
        changelog = component.changelog,
        visible = component.showUpdateDialog,
        onDismiss = component::cancelledUpdate
    )

    FirstLaunchSetupDialog(
        toggleShowUpdateDialog = component::toggleShowUpdateDialog,
        toggleAllowBetas = component::toggleAllowBetas,
        adjustPerformance = component::adjustPerformance
    )

    DonateDialog(
        onRegisterDonateDialogOpen = component::registerDonateDialogOpen,
        onNotShowDonateDialogAgain = component::notShowDonateDialogAgain
    )

    PermissionDialog()

    GithubReviewDialog(
        visible = component.showGithubReviewDialog,
        onDismiss = component::hideReviewDialog,
        onNotShowAgain = {
            ReviewHandler.notShowReviewAgain(appContext)
        },
        isNotShowAgainButtonVisible = ReviewHandler.showNotShowAgainButton
    )

    TelegramGroupDialog(
        visible = component.showTelegramGroupDialog,
        onDismiss = component::hideTelegramGroupDialog,
        onRedirected = component::registerTelegramGroupOpen
    )

    SuccessRestoreBackupToastHandler(component)

    HandleLookForUpdates(component)
}