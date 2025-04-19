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

package ru.tech.imageresizershrinker.feature.libraries_info.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.util.withContext
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.PredictiveBackObserver
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.modifier.toShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withLayoutCorners
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.HtmlText
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.libraries_info.presentation.screenLogic.LibrariesInfoComponent


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
                librariesBlock = { context ->
                    Libs.Builder().withContext(context).build().let { libs ->
                        libs.copy(
                            libraries = libs.libraries.distinctBy {
                                it.name
                            }.toPersistentList()
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
                        license?.url?.also {
                            runCatching {
                                linkHandler.openUri(it)
                            }.onFailure(essentials::showFailureToast)
                        }
                    }
                }
            )

            FullscreenPopup {
                var predictiveBackProgress by remember {
                    mutableFloatStateOf(0f)
                }
                val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)
                val scale = (1f - animatedPredictiveBackProgress * 1.5f).coerceAtLeast(0.75f)

                LaunchedEffect(predictiveBackProgress, component.selectedLibrary) {
                    if (component.selectedLibrary == null && predictiveBackProgress != 0f) {
                        delay(600)
                        predictiveBackProgress = 0f
                    }
                }

                AnimatedContent(
                    targetState = component.selectedLibrary,
                    transitionSpec = {
                        fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                    }
                ) { library ->
                    if (library != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.scrim.copy(0.32f))
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .withLayoutCorners { corners ->
                                    graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        shape = corners.toShape(animatedPredictiveBackProgress)
                                        clip = true
                                    }
                                }
                        ) {
                            val childScrollBehavior =
                                TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .nestedScroll(childScrollBehavior.nestedScrollConnection)
                            ) {
                                EnhancedTopAppBar(
                                    title = {
                                        Text(
                                            text = library.name,
                                            modifier = Modifier.marquee()
                                        )
                                    },
                                    navigationIcon = {
                                        EnhancedIconButton(
                                            onClick = {
                                                component.selectLibrary(null)
                                            }
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
                                    scrollBehavior = childScrollBehavior
                                )
                                val html = remember(library) {
                                    library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty()
                                        .trimIndent()
                                }
                                SelectionContainer {
                                    HtmlText(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())
                                            .navigationBarsPadding()
                                            .padding(
                                                WindowInsets.displayCutout
                                                    .only(
                                                        WindowInsetsSides.Horizontal
                                                    )
                                                    .asPaddingValues()
                                            )
                                            .padding(16.dp),
                                        html = html,
                                        onHyperlinkClick = linkHandler::openUri
                                    )
                                }
                            }
                        }
                        PredictiveBackObserver(
                            onProgress = {
                                predictiveBackProgress = it
                            },
                            onClean = { isCompleted ->
                                if (isCompleted) {
                                    component.selectLibrary(null)
                                    delay(400)
                                }
                                predictiveBackProgress = 0f
                            }
                        )
                    } else {
                        Spacer(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}