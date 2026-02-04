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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.localImagePickerMode
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withModifier
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.recognize.text.presentation.screenLogic.RecognizeTextComponent

@Composable
internal fun RecognizeTextNoDataControls(component: RecognizeTextComponent) {
    val isPortrait by isPortraitOrientationAsState()

    val essentials = rememberLocalEssentials()

    val startRecognition = {
        component.startRecognition(
            onFailure = essentials::showFailureToast
        )
    }

    val imagePickerMode = localImagePickerMode(Picker.Single)

    val imagePicker = rememberImagePicker(imagePickerMode) { list ->
        component.updateType(
            type = Screen.RecognizeText.Type.Extraction(list.firstOrNull()),
            onImageSet = startRecognition
        )
    }

    val writeToFilePicker = rememberImagePicker { uris: List<Uri> ->
        component.updateType(
            type = Screen.RecognizeText.Type.WriteToFile(uris),
            onImageSet = startRecognition
        )
    }

    val writeToMetadataPicker = rememberImagePicker { uris: List<Uri> ->
        component.updateType(
            type = Screen.RecognizeText.Type.WriteToMetadata(uris),
            onImageSet = startRecognition
        )
    }

    val types = remember {
        Screen.RecognizeText.Type.entries
    }
    val preference1 = @Composable {
        PreferenceItem(
            title = stringResource(types[0].title),
            subtitle = stringResource(types[0].subtitle),
            startIcon = types[0].icon,
            modifier = Modifier.fillMaxWidth(),
            onClick = imagePicker::pickImage
        )
    }
    val preference2 = @Composable {
        PreferenceItem(
            title = stringResource(types[1].title),
            subtitle = stringResource(types[1].subtitle),
            startIcon = types[1].icon,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (component.selectionSheetData.isNotEmpty()) {
                    component.updateType(
                        type = Screen.RecognizeText.Type.WriteToFile(component.selectionSheetData),
                        onImageSet = startRecognition
                    )
                    component.hideSelectionTypeSheet()
                } else {
                    writeToFilePicker.pickImage()
                }
            }
        )
    }
    val preference3 = @Composable {
        PreferenceItem(
            title = stringResource(types[2].title),
            subtitle = stringResource(types[2].subtitle),
            startIcon = types[2].icon,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (component.selectionSheetData.isNotEmpty()) {
                    component.updateType(
                        type = Screen.RecognizeText.Type.WriteToMetadata(component.selectionSheetData),
                        onImageSet = startRecognition
                    )
                    component.hideSelectionTypeSheet()
                } else {
                    writeToMetadataPicker.pickImage()
                }
            }
        )
    }

    if (isPortrait) {
        Column {
            preference1()
            Spacer(modifier = Modifier.height(8.dp))
            preference2()
            Spacer(modifier = Modifier.height(8.dp))
            preference3()
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(
                    WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                        .asPaddingValues()
                )
            ) {
                preference1.withModifier(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                preference2.withModifier(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            preference3.withModifier(modifier = Modifier.fillMaxWidth(0.5f))
        }
    }

    EnhancedModalBottomSheet(
        visible = component.selectionSheetData.isNotEmpty(),
        onDismiss = {
            if (!it) component.hideSelectionTypeSheet()
        },
        confirmButton = {
            EnhancedButton(
                onClick = component::hideSelectionTypeSheet,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        sheetContent = {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(250.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(12.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                item {
                    preference2()
                }
                item {
                    preference3()
                }
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.pick_file),
                icon = Icons.Rounded.FileOpen
            )
        }
    )
}