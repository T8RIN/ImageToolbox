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

package ru.tech.imageresizershrinker.core.ui.widget.image

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator

@Composable
fun ImageContainer(
    modifier: Modifier = Modifier,
    imageInside: Boolean,
    showOriginal: Boolean,
    previewBitmap: Bitmap?,
    originalBitmap: Bitmap?,
    isLoading: Boolean,
    shouldShowPreview: Boolean,
    animatePreviewChange: Boolean = true,
    containerModifier: Modifier = Modifier.fillMaxSize()
) {
    val generatePreviews = LocalSettingsState.current.generatePreviews
    if (animatePreviewChange) {
        AnimatedContent(
            modifier = containerModifier,
            targetState = remember(previewBitmap, isLoading, showOriginal) {
                derivedStateOf {
                    Triple(previewBitmap, isLoading, showOriginal)
                }
            }.value,
            transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(false) }
        ) { (bmp, loading, showOrig) ->
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.then(
                        if (!imageInside) {
                            Modifier.padding(
                                bottom = WindowInsets
                                    .navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding()
                            )
                        } else Modifier
                    )
                ) {
                    if (showOrig) {
                        SimplePicture(
                            bitmap = originalBitmap,
                            loading = loading
                        )
                    } else {
                        SimplePicture(
                            loading = loading,
                            bitmap = bmp,
                            visible = shouldShowPreview
                        )
                        if (!loading && (bmp == null || !shouldShowPreview) || !generatePreviews) {
                            BadImageWidget()
                        }
                    }
                    if (loading) LoadingIndicator()
                }
            }
        }
    } else {
        AnimatedContent(
            modifier = containerModifier,
            targetState = remember(isLoading, showOriginal) {
                derivedStateOf {
                    isLoading to showOriginal
                }
            }.value,
            transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(false) }
        ) { (loading, showOrig) ->
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.then(
                            if (!imageInside) {
                                Modifier.padding(
                                    bottom = WindowInsets
                                        .navigationBars
                                        .asPaddingValues()
                                        .calculateBottomPadding()
                                )
                            } else Modifier
                        )
                    ) {
                        previewBitmap?.let {
                            if (!showOrig) {
                                SimplePicture(
                                    bitmap = it,
                                    loading = loading
                                )
                            } else {
                                SimplePicture(
                                    loading = loading,
                                    bitmap = originalBitmap,
                                    visible = true
                                )
                            }
                        } ?: if (!generatePreviews) {
                            BadImageWidget()
                        } else Unit

                        if (previewBitmap == null && loading) {
                            LoadingIndicator()
                        }
                    }
                }
            }
        }
    }
}