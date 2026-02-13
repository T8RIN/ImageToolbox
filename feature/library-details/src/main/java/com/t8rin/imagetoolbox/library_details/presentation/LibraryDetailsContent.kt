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

package com.t8rin.imagetoolbox.library_details.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.HtmlText
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.library_details.presentation.screenLogic.LibraryDetailsComponent

@Composable
fun LibraryDetailsContent(
    component: LibraryDetailsComponent
) {
    val childScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val linkHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(childScrollBehavior.nestedScrollConnection),
        topBar = {
            EnhancedTopAppBar(
                title = {
                    Text(
                        text = component.libraryName,
                        modifier = Modifier.marquee()
                    )
                },
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = component.onGoBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (component.libraryLink.isNullOrBlank()) {
                        TopAppBarEmoji()
                    } else {
                        EnhancedIconButton(
                            onClick = { linkHandler.openUri(component.libraryLink) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                contentDescription = component.libraryLink
                            )
                        }
                    }
                },
                type = EnhancedTopAppBarType.Large,
                scrollBehavior = childScrollBehavior
            )
        }
    ) { contentPadding ->
        SelectionContainer {
            HtmlText(
                modifier = Modifier
                    .fillMaxSize()
                    .enhancedVerticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(12.dp)
                    .container(
                        resultPadding = 12.dp
                    ),
                html = component.libraryDescription
            )
        }
    }
}