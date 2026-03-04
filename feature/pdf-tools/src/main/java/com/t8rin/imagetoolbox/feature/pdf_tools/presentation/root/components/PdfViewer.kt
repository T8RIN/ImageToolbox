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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.canUseNewPdf
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.viewer.LegacyPdfViewer
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.viewer.ModernPdfViewer

@Composable
fun PdfViewer(
    uri: Uri?,
    modifier: Modifier,
    spacing: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(start = 20.dp, end = 20.dp),
    isSearching: Boolean = false
) {
    AnimatedContent(
        targetState = uri
    ) { uri ->
        if (uri != null) {
            if (canUseNewPdf()) {
                ModernPdfViewer(
                    uri = uri,
                    isSearching = isSearching,
                    modifier = modifier
                )
            } else {
                LegacyPdfViewer(
                    uri = uri,
                    spacing = spacing,
                    contentPadding = contentPadding,
                    modifier = modifier
                )
            }
        } else {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .animateContentSizeNoClip(),
                contentAlignment = Alignment.Center
            ) {
                EnhancedLoadingIndicator()
            }
        }
    }
}