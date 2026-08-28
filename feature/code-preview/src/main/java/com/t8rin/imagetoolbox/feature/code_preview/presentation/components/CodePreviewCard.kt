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

package com.t8rin.imagetoolbox.feature.code_preview.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodePreviewParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

@Composable
internal fun CodePreviewCard(
    params: CodePreviewParams,
    highlightedCode: AnnotatedString,
    modifier: Modifier = Modifier
) {
    val canvasShape = RoundedCornerShape(params.canvasCornerRadius.dp)
    val bitmapHolder = remember { PreviewBitmapHolder() }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(params, highlightedCode) {
        delay(100)
        var renderedBitmap: Bitmap? = null
        try {
            renderedBitmap = withContext(Dispatchers.Default) {
                renderCodePreviewBitmap(
                    params = params,
                    highlightedCode = highlightedCode,
                    maxBitmapPixels = 2_000_000f
                )
            }
            ensureActive()
            val previousBitmap = bitmapHolder.bitmap
            bitmapHolder.bitmap = renderedBitmap
            previewBitmap = renderedBitmap
            renderedBitmap = null
            withFrameNanos { }
            previousBitmap?.recycle()
        } finally {
            renderedBitmap?.recycle()
        }
    }

    DisposableEffect(bitmapHolder) {
        onDispose {
            bitmapHolder.bitmap?.recycle()
            bitmapHolder.bitmap = null
        }
    }

    Box(
        modifier = modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (!params.showCanvasBackground) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(canvasShape)
                    .transparencyChecker()
            )
        }
        previewBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit,
                filterQuality = FilterQuality.High
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
    }
}

private class PreviewBitmapHolder {
    var bitmap: Bitmap? = null
}
