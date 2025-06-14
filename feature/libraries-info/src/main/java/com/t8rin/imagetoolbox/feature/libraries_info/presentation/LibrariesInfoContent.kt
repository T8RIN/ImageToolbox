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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.util.withContext
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.libraries_info.presentation.screenLogic.LibrariesInfoComponent
import kotlinx.collections.immutable.toPersistentList


@Composable
fun LibrariesInfoContent(
    component: LibrariesInfoComponent
) {
    val essentials = rememberLocalEssentials()

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
            LibrariesContainer(
                libraries = remember {
                    Libs.Builder()
                        .withContext(essentials.context)
                        .build().let { libs ->
                            libs.copy(
                                libraries = libs.libraries.distinctBy {
                                    it.name
                                }.sortedWith(
                                    compareBy(
                                        { !it.name.contains("T8RIN", true) },
                                        { it.name }
                                    ),
                                ).toPersistentList()
                            )
                        }
                },
                modifier = Modifier.weight(1f),
                contentPadding = WindowInsets
                    .navigationBars
                    .only(WindowInsetsSides.Bottom)
                    .union(WindowInsets.ime)
                    .union(
                        WindowInsets.displayCutout
                            .only(
                                WindowInsetsSides.Horizontal
                            )
                    )
                    .asPaddingValues(),
                onLibraryClick = { library ->
                    val license = library.licenses.firstOrNull()
                    if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                        component.selectLibrary(library)
                    } else if (!license?.url.isNullOrBlank()) {
                        license.url?.also {
                            runCatching {
                                linkHandler.openUri(it)
                            }.onFailure(essentials::showFailureToast)
                        }
                    }
                }
            )
        }
    }
}