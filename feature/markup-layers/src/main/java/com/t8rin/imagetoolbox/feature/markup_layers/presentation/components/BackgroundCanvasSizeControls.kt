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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.ResizeImageField
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.BackgroundBehavior

@Composable
internal fun BackgroundCanvasSizeControls(
    behavior: BackgroundBehavior.Color,
    imageFormat: ImageFormat,
    onApply: (width: Int, height: Int) -> Unit
) {
    var width by rememberSaveable(behavior.width, behavior.height) {
        mutableIntStateOf(behavior.width)
    }
    var height by rememberSaveable(behavior.width, behavior.height) {
        mutableIntStateOf(behavior.height)
    }

    val canApply by remember(width, height, behavior.width, behavior.height) {
        derivedStateOf {
            width > 0 &&
                    height > 0 &&
                    (width != behavior.width || height != behavior.height)
        }
    }
    val bottomCorner by animateDpAsState(
        if (canApply) 4.dp else 24.dp
    )

    Column {
        ResizeImageField(
            imageInfo = ImageInfo(
                width = width,
                height = height,
                imageFormat = imageFormat
            ),
            originalSize = IntegerSize(
                width = behavior.width,
                height = behavior.height
            ),
            onWidthChange = { width = it.coerceIn(0, 8192) },
            onHeightChange = { height = it.coerceIn(0, 8192) },
            modifier = Modifier.fillMaxWidth(),
            shape = AutoCornersShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = bottomCorner,
                bottomEnd = bottomCorner
            )
        )
        AnimatedVisibility(
            visible = canApply,
            modifier = Modifier.fillMaxWidth()
        ) {
            EnhancedButton(
                onClick = { onApply(width, height) },
                enabled = canApply,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = AutoCornersShape(
                    topStart = 4.dp,
                    topEnd = 4.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    }
}
