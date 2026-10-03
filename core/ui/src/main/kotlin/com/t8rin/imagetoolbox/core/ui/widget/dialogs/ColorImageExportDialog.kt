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

package com.t8rin.imagetoolbox.core.ui.widget.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.ColorImageExporter
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.restrict
import com.t8rin.imagetoolbox.core.ui.utils.helper.SaveResultHandler
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalKeepAliveService
import com.t8rin.imagetoolbox.core.ui.utils.rememberColorImageExporter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun ColorImageExportDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    color: Color
) {
    val exporter = rememberColorImageExporter()
    val scope = rememberCoroutineScope()
    val keepAliveService = LocalKeepAliveService.current

    var width by rememberSaveable { mutableIntStateOf(1000) }
    var height by rememberSaveable { mutableIntStateOf(1000) }
    var isSaving by remember { mutableStateOf(false) }
    var savingJob by remember { mutableStateOf<Job?>(null) }
    var showSaveLocationDialog by rememberSaveable { mutableStateOf(false) }

    val canSave = width in 1..ColorImageExporter.MAX_SIZE &&
            height in 1..ColorImageExporter.MAX_SIZE && !isSaving
    val save: (String?) -> Unit = { location ->
        if (canSave) {
            val imageInfo = ImageInfo(
                width = width,
                height = height,
                imageFormat = ImageFormat.Png.Lossless
            )
            isSaving = true
            savingJob = scope.launch {
                try {
                    keepAliveService.track(
                        onFailure = AppToastHost::showFailureToast
                    ) {
                        val result = exporter.save(
                            color = color,
                            imageInfo = imageInfo,
                            oneTimeSaveLocationUri = location
                        )
                        SaveResultHandler.parseFileSaveResult(result)
                        if (result is SaveResult.Success) onDismiss()
                    }
                } finally {
                    isSaving = false
                }
            }
        }
    }

    EnhancedAlertDialog(
        placeAboveAll = true,
        visible = visible,
        onDismissRequest = { if (!isSaving) onDismiss() },
        icon = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .container(
                        shape = ShapeDefaults.circle,
                        resultPadding = 0.dp
                    )
                    .transparencyChecker()
                    .background(color)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.save_color_as_image),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = canSave,
                onClick = { save(null) },
                onLongClick = { showSaveLocationDialog = true }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            EnhancedButton(
                enabled = !isSaving,
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .enhancedVerticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProvideTextStyle(LocalTextStyle.current.copy(textAlign = TextAlign.Start)) {
                    Row(
                        modifier = Modifier.container(shape = ShapeDefaults.extraLarge)
                    ) {
                        RoundedTextField(
                            value = width.takeIf { it != 0 }?.toString() ?: "",
                            onValueChange = {
                                width = it.restrict(ColorImageExporter.MAX_SIZE).toIntOrNull() ?: 0
                            },
                            shape = ShapeDefaults.smallStart,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.width, " ")) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 2.dp)
                        )
                        RoundedTextField(
                            value = height.takeIf { it != 0 }?.toString() ?: "",
                            onValueChange = {
                                height = it.restrict(ColorImageExporter.MAX_SIZE).toIntOrNull() ?: 0
                            },
                            shape = ShapeDefaults.smallEnd,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.height, " ")) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 2.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        )
                    }
                }
            }
        }
    )

    OneTimeSaveLocationSelectionDialog(
        visible = showSaveLocationDialog,
        onDismiss = { showSaveLocationDialog = false },
        onSaveRequest = save,
        formatForFilenameSelection = ImageFormat.Png.Lossless,
        hasOriginalUri = false
    )

    LoadingDialog(
        visible = isSaving,
        onCancelLoading = { savingJob?.cancel() }
    )
}
