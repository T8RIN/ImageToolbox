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

package com.t8rin.imagetoolbox.feature.single_edit.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.curves.ImageCurvesEditor
import com.t8rin.curves.ImageCurvesEditorState
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageReset
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShowOriginalButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee

@Composable
fun ToneCurvesEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap) -> Unit,
    editorState: ImageCurvesEditorState,
    onResetState: () -> Unit
) {
    bitmap?.let {
        val scaffoldState = rememberBottomSheetScaffoldState()

        var showOriginal by remember {
            mutableStateOf(false)
        }
        var imageObtainingTrigger by remember {
            mutableStateOf(false)
        }

        var showResetDialog by rememberSaveable { mutableStateOf(false) }

        val actions = @Composable {
            EnhancedIconButton(
                onClick = { showResetDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ImageReset,
                    contentDescription = stringResource(R.string.reset_image)
                )
            }
            ShowOriginalButton(
                onStateChange = {
                    showOriginal = it
                }
            )
        }

        var isDefault by remember(editorState) {
            mutableStateOf(editorState.isDefault())
        }

        FullscreenEditOption(
            showControls = false,
            canGoBack = isDefault,
            visible = visible,
            modifier = Modifier.heightIn(max = LocalScreenSize.current.height / 1.5f),
            onDismiss = {
                onDismiss()
                onResetState()
            },
            useScaffold = useScaffold,
            controls = { },
            fabButtons = if (useScaffold) {
                {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .offset(y = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TopAppBarEmoji()
                    }
                }
            } else {
                {
                    actions()
                }
            },
            scaffoldState = scaffoldState,
            actions = {
                actions()
            },
            topAppBar = { closeButton ->
                EnhancedTopAppBar(
                    type = EnhancedTopAppBarType.Center,
                    navigationIcon = closeButton,
                    actions = {
                        EnhancedIconButton(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            onClick = {
                                imageObtainingTrigger = true
                            },
                            enabled = !showOriginal
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Done,
                                contentDescription = "Done"
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.tone_curves),
                            modifier = Modifier.marquee()
                        )
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ImageCurvesEditor(
                    bitmap = bitmap,
                    state = editorState,
                    curvesSelectionText = {
                        Text(
                            text = when (it) {
                                0 -> stringResource(R.string.all)
                                1 -> stringResource(R.string.color_red)
                                2 -> stringResource(R.string.color_green)
                                3 -> stringResource(R.string.color_blue)
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    placeControlsAtTheEnd = !useScaffold,
                    imageObtainingTrigger = imageObtainingTrigger,
                    onImageObtained = {
                        imageObtainingTrigger = false
                        onGetBitmap(it)
                        onResetState()
                        onDismiss()
                    },
                    contentPadding = WindowInsets.systemBars.union(WindowInsets.displayCutout)
                        .let {
                            if (!useScaffold) it.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                            else it.only(WindowInsetsSides.Horizontal)
                        }
                        .union(
                            WindowInsets(
                                left = 16.dp,
                                top = 16.dp,
                                right = 16.dp,
                                bottom = 16.dp
                            )
                        )
                        .asPaddingValues(),
                    containerModifier = Modifier.align(Alignment.Center),
                    showOriginal = showOriginal,
                    onStateChange = {
                        isDefault = it.isDefault()
                    }
                )
            }
        }

        ResetDialog(
            visible = showResetDialog,
            onDismiss = { showResetDialog = false },
            title = stringResource(R.string.reset_curves),
            text = stringResource(R.string.reset_curves_sub),
            onReset = onResetState
        )
    }
}
