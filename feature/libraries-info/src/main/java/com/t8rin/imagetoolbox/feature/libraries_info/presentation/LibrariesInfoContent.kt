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

package com.t8rin.imagetoolbox.feature.libraries_info.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.union
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.chipColors
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.util.withContext
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.libraries_info.presentation.components.LibrariesContainer
import com.t8rin.imagetoolbox.feature.libraries_info.presentation.components.link
import com.t8rin.imagetoolbox.feature.libraries_info.presentation.screenLogic.LibrariesInfoComponent
import kotlinx.collections.immutable.toPersistentList


@Composable
fun LibrariesInfoContent(
    component: LibrariesInfoComponent
) {
    val context = LocalContext.current

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize()
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            EnhancedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.open_source_licenses),
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
                    TopAppBarEmoji()
                },
                type = EnhancedTopAppBarType.Large,
                scrollBehavior = scrollBehavior
            )

            val linkHandler = LocalUriHandler.current

            val libraries = remember(context) {
                Libs.Builder()
                    .withContext(context)
                    .build().let { libs ->
                        libs.copy(
                            libraries = libs.libraries.distinctBy {
                                it.name
                            }.filter { it.licenses.isNotEmpty() }.sortedWith(
                                compareBy(
                                    { !it.name.contains("T8RIN", true) },
                                    { it.name }
                                ),
                            ).toPersistentList()
                        )
                    }
            }
            val contentPadding = WindowInsets
                .navigationBars
                .only(WindowInsetsSides.Bottom)
                .union(WindowInsets.ime)
                .union(
                    WindowInsets.displayCutout
                        .only(
                            WindowInsetsSides.Horizontal
                        )
                )
                .union(
                    WindowInsets(
                        left = 12.dp,
                        top = 12.dp,
                        right = 12.dp,
                        bottom = 12.dp
                    )
                )
                .asPaddingValues()

            LibrariesContainer(
                libraries = libraries,
                modifier = Modifier.weight(1f),
                contentPadding = contentPadding,
                dimensions = LibraryDefaults.libraryDimensions(
                    itemSpacing = 4.dp
                ),
                colors = LibraryDefaults.libraryColors(
                    versionChipColors = LibraryDefaults.chipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ),
                onLibraryClick = { library ->
                    val license = library.licenses.firstOrNull()
                    val url = library.link()

                    if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                        component.selectLibrary(library)
                    } else if (!url.isNullOrBlank()) {
                        linkHandler.openUri(url)
                    }
                }
            )
        }
    }
}