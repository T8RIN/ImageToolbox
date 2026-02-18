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

package com.t8rin.imagetoolbox.feature.filters.presentation.components.addEditMaskSheet

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageHeaderState
import com.t8rin.imagetoolbox.core.ui.widget.image.imageStickyHeader
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.saver.PtSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberAvailableHeight
import com.t8rin.imagetoolbox.feature.filters.presentation.components.UiFilterMask

@Composable
fun AddEditMaskSheet(
    component: AddMaskSheetComponent,
    mask: UiFilterMask? = null,
    visible: Boolean,
    onDismiss: () -> Unit,
    targetBitmapUri: Uri? = null,
    masks: List<UiFilterMask> = emptyList(),
    onMaskPicked: (UiFilterMask) -> Unit,
) {
    var invalidations by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(mask, masks, targetBitmapUri, invalidations) {
        component.setMask(
            mask = mask,
            bitmapUri = targetBitmapUri,
            masks = masks
        )
    }

    val isPortrait by isPortraitOrientationAsState()

    var showExitDialog by remember { mutableStateOf(false) }

    val settingsState = LocalSettingsState.current
    var isEraserOn by rememberSaveable { mutableStateOf(false) }
    var strokeWidth by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(settingsState.defaultDrawLineWidth.pt) }
    var brushSoftness by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(20.pt) }
    var panEnabled by rememberSaveable { mutableStateOf(false) }

    val canSave = component.paths.isNotEmpty() && component.filterList.isNotEmpty()
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (component.paths.isEmpty() && component.filterList.isEmpty()) onDismiss()
            else showExitDialog = true
        },
        cancelable = false,
        title = {
            TitleItem(
                text = stringResource(id = R.string.add_mask),
                icon = Icons.Rounded.Texture
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = canSave,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    onMaskPicked(component.getUiMask())
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawHorizontalStroke(autoElevation = 3.dp)
                    .zIndex(Float.MAX_VALUE)
                    .background(EnhancedBottomSheetDefaults.barContainerColor)
                    .padding(8.dp)
            ) {
                EnhancedIconButton(
                    onClick = {
                        if (component.paths.isEmpty() && component.filterList.isEmpty()) onDismiss()
                        else showExitDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.exit)
                    )
                }
            }
        },
        enableBackHandler = component.paths.isEmpty() && component.filterList.isEmpty()
    ) {
        component.AttachLifecycle()

        LaunchedEffect(Unit) {
            invalidations++
        }
        var imageState by remember { mutableStateOf(ImageHeaderState(2)) }

        if (visible) {
            BackHandler(
                enabled = !(component.paths.isEmpty() && component.filterList.isEmpty())
            ) {
                showExitDialog = true
            }
        }
        val drawPreview: @Composable () -> Unit = {
            AddMaskSheetBitmapPreview(
                component = component,
                imageState = imageState,
                strokeWidth = strokeWidth,
                brushSoftness = brushSoftness,
                isEraserOn = isEraserOn,
                panEnabled = panEnabled
            )
        }
        Row {
            val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
            if (!isPortrait) {
                Box(modifier = Modifier.weight(1.3f)) {
                    drawPreview()
                }
            }
            val internalHeight = rememberAvailableHeight(imageState = imageState)
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                flingBehavior = enhancedFlingBehavior()
            ) {
                imageStickyHeader(
                    visible = isPortrait,
                    internalHeight = internalHeight,
                    imageState = imageState,
                    onStateChange = {
                        imageState = it
                    },
                    isControlsVisibleIndefinitely = true,
                    padding = 0.dp,
                    backgroundColor = backgroundColor,
                    imageBlock = drawPreview
                )
                item {
                    AddEditMaskSheetControls(
                        component = component,
                        imageState = imageState,
                        strokeWidth = strokeWidth,
                        onStrokeWidthChange = { strokeWidth = it },
                        brushSoftness = brushSoftness,
                        onBrushSoftnessChange = { brushSoftness = it },
                        panEnabled = panEnabled,
                        onTogglePanEnabled = { panEnabled = !panEnabled },
                        isEraserOn = isEraserOn,
                        onToggleIsEraserOn = { isEraserOn = !isEraserOn }
                    )
                }
            }
        }
    }

    ExitWithoutSavingDialog(
        onExit = onDismiss,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog,
        placeAboveAll = true
    )
}