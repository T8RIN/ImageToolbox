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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.filters.presentation.model.FilterPreviewKey
import com.t8rin.imagetoolbox.feature.draw.domain.PathPaint

internal data class PreviewRequest(
    val bitmapId: Int,
    val imageInfo: ImageInfo,
    val filterType: String?,
    val basicState: BasicPreviewState?,
    val maskingState: MaskingPreviewState?
)

internal data class BasicPreviewState(
    val uris: List<Uri>?,
    val selectedUri: Uri?,
    val filters: List<FilterPreviewKey>
)

internal data class MaskingPreviewState(
    val uri: Uri?,
    val masks: List<MaskPreviewState>
)

internal data class MaskPreviewState(
    val filters: List<FilterPreviewKey>,
    val maskPaints: List<PathPaint<Path, Color>>,
    val isInverseFillType: Boolean
)