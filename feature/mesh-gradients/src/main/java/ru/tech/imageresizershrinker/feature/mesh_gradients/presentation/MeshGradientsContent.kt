/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.mesh_gradients.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import ru.tech.imageresizershrinker.core.ui.utils.helper.plus
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.image.ImagePreviewGrid
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.mesh_gradients.presentation.screenLogic.MeshGradientsComponent

@Composable
fun MeshGradientsContent(
    component: MeshGradientsComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    Surface(
        modifier = Modifier.fillMaxSize()
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
                        text = stringResource(R.string.collection_mesh_gradients),
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
                scrollBehavior = childScrollBehavior
            )
            Box(
                modifier = Modifier.weight(1f)
            ) {
                AnimatedContent(component.meshGradientUris) { uris ->
                    if (uris.isNotEmpty()) {
                        ImagePreviewGrid(
                            data = uris,
                            onAddImages = null,
                            onShareImage = {
                                component.shareImages(
                                    uriList = listOf(element = it),
                                    onComplete = showConfetti
                                )
                            },
                            onRemove = null,
                            onNavigate = component.onNavigate,
                            imageFrames = null,
                            onFrameSelectionChange = {},
                            contentPadding = WindowInsets.navigationBars.union(
                                WindowInsets.displayCutout.only(
                                    WindowInsetsSides.Horizontal
                                )
                            ).asPaddingValues() + PaddingValues(12.dp)
                        )
                    } else {
                        val meshGradientDownloadProgress =
                            component.meshGradientDownloadProgress
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val currentPercent =
                                meshGradientDownloadProgress?.currentPercent ?: 0f

                            if (currentPercent > 0f) {
                                LoadingIndicator(
                                    progress = currentPercent / 100,
                                    loaderSize = 72.dp
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = meshGradientDownloadProgress?.run { "$itemsDownloaded/$itemsCount" }
                                                ?: "",
                                            maxLines = 1,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            fontSize = 12.sp,
                                            lineHeight = 12.sp
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = rememberHumanFileSize(
                                                meshGradientDownloadProgress?.currentTotalSize ?: 0
                                            ),
                                            maxLines = 1,
                                            textAlign = TextAlign.Center,
                                            fontSize = 10.sp,
                                            lineHeight = 10.sp
                                        )
                                    }
                                }
                            } else {
                                LoadingIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}