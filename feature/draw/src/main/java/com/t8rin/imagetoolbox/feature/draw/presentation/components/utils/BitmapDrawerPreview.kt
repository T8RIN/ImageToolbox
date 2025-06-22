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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHelperGrid
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker

@Composable
fun BoxScope.BitmapDrawerPreview(
    preview: ImageBitmap,
    globalTouchPointersCount: MutableIntState,
    onReceiveMotionEvent: (MotionEvent) -> Unit,
    onInvalidate: () -> Unit,
    onUpdateCurrentDrawPosition: (Offset) -> Unit,
    onUpdateDrawDownPosition: (Offset) -> Unit,
    drawEnabled: Boolean,
    helperGridParams: HelperGridParams,
    beforeHelperGridModifier: Modifier = Modifier,
) {
    Picture(
        model = preview,
        modifier = Modifier
            .matchParentSize()
            .pointerDrawHandler(
                globalTouchPointersCount = globalTouchPointersCount,
                onReceiveMotionEvent = onReceiveMotionEvent,
                onInvalidate = onInvalidate,
                onUpdateCurrentDrawPosition = onUpdateCurrentDrawPosition,
                onUpdateDrawDownPosition = onUpdateDrawDownPosition,
                enabled = drawEnabled
            )
            .clip(ShapeDefaults.extremeSmall)
            .transparencyChecker()
            .then(beforeHelperGridModifier)
            .drawHelperGrid(helperGridParams)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant(),
                shape = ShapeDefaults.extremeSmall
            ),
        contentDescription = null,
        contentScale = ContentScale.FillBounds
    )
}