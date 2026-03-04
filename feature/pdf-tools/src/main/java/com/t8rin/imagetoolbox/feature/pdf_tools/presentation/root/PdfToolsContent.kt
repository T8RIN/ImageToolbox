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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.screenLogic.PdfToolsComponent

@Composable
fun PdfToolsContent(
    component: PdfToolsComponent
) {
    var tempSelectionUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showSelectionPdfPicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(showSelectionPdfPicker) {
        if (!showSelectionPdfPicker) tempSelectionUri = null
    }
    val selectionPdfPicker = rememberFilePicker(
        mimeType = MimeType.Pdf,
        onSuccess = { uri: Uri ->
            tempSelectionUri = uri
            showSelectionPdfPicker = true
        }
    )

    val essentials = rememberLocalEssentials()

    AdaptiveLayoutScreen(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.marquee()
            ) {
                Text(
                    text = stringResource(R.string.pdf_tools),
                    textAlign = TextAlign.Center
                )
                EnhancedBadge(
                    content = {
                        Text(
                            text = Screen.PdfTools.options.size.toString()
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .padding(bottom = 12.dp)
                        .scaleOnTap {
                            essentials.showConfetti()
                        }
                )
            }
        },
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        onGoBack = component.onGoBack,
        shouldDisableBackHandler = true,
        buttons = {},
        controls = {
            Scaffold(
                bottomBar = {
                    BottomButtonsBlock(
                        isNoData = true,
                        showNullDataButtonAsContainer = true,
                        isPrimaryButtonVisible = false,
                        onSecondaryButtonClick = selectionPdfPicker::pickFile,
                        onPrimaryButtonClick = { },
                        actions = { },
                        secondaryButtonIcon = Icons.Rounded.FileOpen,
                        secondaryButtonText = stringResource(R.string.pick_file)
                    )
                },
                contentWindowInsets = WindowInsets.systemBars
                    .union(WindowInsets.displayCutout)
                    .only(WindowInsetsSides.Start)
            ) { contentPadding ->
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxHeight(),
                    columns = StaggeredGridCells.Adaptive(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 12.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalItemSpacing = 12.dp,
                    contentPadding = contentPadding + PaddingValues(16.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    items(Screen.PdfTools.options) { screen ->
                        PreferenceItem(
                            title = stringResource(screen.title),
                            subtitle = stringResource(screen.subtitle),
                            startIcon = screen.icon,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { component.onNavigate(screen) }
                        )
                    }
                }
            }
        },
        imagePreview = {},
        placeImagePreview = false,
        showImagePreviewAsStickyHeader = false,
        placeControlsSeparately = true,
        canShowScreenData = true,
        noDataControls = {},
        actions = {}
    )

    EnhancedModalBottomSheet(
        visible = showSelectionPdfPicker,
        onDismiss = {
            showSelectionPdfPicker = it
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showSelectionPdfPicker = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        sheetContent = {
            if (tempSelectionUri == null) showSelectionPdfPicker = false

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
                items(Screen.PdfTools.options) { screen ->
                    PreferenceItem(
                        title = stringResource(screen.title),
                        subtitle = stringResource(screen.subtitle),
                        startIcon = screen.icon,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showSelectionPdfPicker = false
                            component.navigate(
                                screen = screen,
                                tempSelectionUri = tempSelectionUri
                            )
                        }
                    )
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