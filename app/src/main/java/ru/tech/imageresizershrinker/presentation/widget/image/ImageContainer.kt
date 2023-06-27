package ru.tech.imageresizershrinker.presentation.widget.image

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.tech.imageresizershrinker.presentation.widget.other.Loading

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
            modifier = modifier,
            targetState = Triple(previewBitmap, isLoading, showOriginal),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { (bmp, loading, showOrig) ->
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
    } else {
        AnimatedContent(
            modifier = modifier,
            targetState = isLoading to showOriginal,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { (loading, showOrig) ->
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
                }
            }
        }
    }
}