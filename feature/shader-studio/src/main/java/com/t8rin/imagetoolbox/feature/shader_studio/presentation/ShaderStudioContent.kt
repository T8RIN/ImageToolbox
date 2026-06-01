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

package com.t8rin.imagetoolbox.feature.shader_studio.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageReset
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.components.SavedShadersButton
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.components.ShaderLibrarySheet
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.components.ShaderParamsEditor
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.components.ShaderPresetEditor
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.components.ShaderPreview
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.components.ShaderPreviewSourceSelector
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.components.ShaderStudioButtons
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic.ShaderStudioComponent
import dev.hossain.highlight.ui.HighlightThemeProvider
import dev.hossain.highlight.ui.rememberTomorrowNightTheme
import dev.hossain.highlight.ui.rememberTomorrowTheme

@Composable
fun ShaderStudioContent(
    component: ShaderStudioComponent
) {
    var showShaderLibrary by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val importPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = component::importPreset
    )

    HighlightThemeProvider(
        lightHighlightTheme = rememberTomorrowTheme(),
        darkHighlightTheme = rememberTomorrowNightTheme()
    ) {
        AdaptiveLayoutScreen(
            shouldDisableBackHandler = !component.haveChanges,
            title = {
                Text(
                    text = stringResource(R.string.shader_studio),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.marquee()
                )
            },
            onGoBack = {
                if (component.haveChanges) {
                    showExitDialog = true
                } else {
                    component.onGoBack()
                }
            },
            actions = {
                EnhancedIconButton(
                    onClick = { showResetDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ImageReset,
                        contentDescription = stringResource(R.string.reset)
                    )
                }
            },
            topAppBarPersistentActions = {
                TopAppBarEmoji()
            },
            imagePreview = {
                ShaderPreview(component)
            },
            controls = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShaderPreviewSourceSelector(component)
                    ShaderPresetEditor(component)
                    ShaderParamsEditor(component)
                    SavedShadersButton(
                        component = component,
                        onClick = { showShaderLibrary = true }
                    )
                }
            },
            buttons = { actions ->
                ShaderStudioButtons(
                    actions = actions,
                    onImport = importPicker::pickFile,
                    onSave = component::savePreset,
                    canSave = component.canSave
                )
            },
            canShowScreenData = true
        )
    }

    ShaderLibrarySheet(
        visible = showShaderLibrary,
        component = component,
        onDismiss = { showShaderLibrary = false }
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        onReset = component::createPreset,
        title = stringResource(R.string.reset_shader),
        text = stringResource(R.string.reset_shader_sub)
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}
