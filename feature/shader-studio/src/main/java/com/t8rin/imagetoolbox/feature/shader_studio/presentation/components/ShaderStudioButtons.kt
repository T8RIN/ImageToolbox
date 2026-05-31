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

package com.t8rin.imagetoolbox.feature.shader_studio.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock

@Composable
internal fun ShaderStudioButtons(
    actions: @Composable RowScope.() -> Unit,
    onImport: () -> Unit,
    onSave: () -> Unit,
    canSave: Boolean
) {
    val isPortrait by isPortraitOrientationAsState()

    BottomButtonsBlock(
        isNoData = false,
        onSecondaryButtonClick = onImport,
        secondaryButtonIcon = Icons.Rounded.FileOpen,
        secondaryButtonText = stringResource(R.string.import_word),
        onPrimaryButtonClick = onSave,
        primaryButtonIcon = Icons.Rounded.Save,
        isPrimaryButtonVisible = canSave,
        actions = {
            if (isPortrait) actions()
        }
    )
}
