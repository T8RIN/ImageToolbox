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

package ru.tech.imageresizershrinker.feature.compare.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.rememberFilename

@Composable
internal fun BoxScope.CompareLabel(
    uri: Uri?,
    alignment: Alignment,
    enabled: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = enabled && uri != null,
        modifier = modifier.align(alignment)
    ) {
        Text(
            text = uri?.let { rememberFilename(it) }
                ?: stringResource(R.string.filename),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.scrim.copy(0.4f),
                    shape = shape
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
            color = Color.White,
            fontSize = 13.sp
        )
    }
}