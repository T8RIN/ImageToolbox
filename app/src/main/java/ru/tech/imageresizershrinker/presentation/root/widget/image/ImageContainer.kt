package ru.tech.imageresizershrinker.presentation.root.widget.image

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
import ru.tech.imageresizershrinker.presentation.root.widget.other.Loading

@Composable
fun ImageContainer(
    modifier: Modifier = Modifier,
    imageInside: Boolean,
    showOriginal: Boolean,
    previewBitmap: Bitmap?,
    originalBitmap: Bitmap?,
    isLoading: Boolean,
    shouldShowPreview: Boolean,
    animatePreviewChange: Boolean = true
) {
    if (animatePreviewChange) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
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
                        if (!shouldShowPreview && !loading && originalBitmap != null && bmp == null) BadImageWidget()
                    }
                    if (loading) Loading()
                }
            }
        }
    } else {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
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
                        }
                        if (previewBitmap == null && loading) {
                            Loading()
                        }
                    }
                }
            }
        }
    }
}