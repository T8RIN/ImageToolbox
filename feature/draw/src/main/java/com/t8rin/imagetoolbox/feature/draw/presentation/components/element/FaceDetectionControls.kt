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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.element

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AutoFixHigh
import com.t8rin.imagetoolbox.core.resources.icons.Face6
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload

@Composable
internal fun FaceDetectionControls(
    useYuNet: Boolean,
    onUseYuNetChange: (Boolean) -> Unit,
    isDetecting: Boolean,
    enabled: Boolean,
    onDetectFaces: () -> Unit
) {
    PreferenceItemOverload(
        title = stringResource(R.string.detect_faces),
        onClick = onDetectFaces.takeIf { enabled && !isDetecting },
        enabled = enabled || isDetecting,
        startIcon = {
            Icon(
                imageVector = Icons.Outlined.Face6,
                contentDescription = null
            )
        },
        endIcon = {
            if (isDetecting) {
                EnhancedCircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.AutoFixHigh,
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.bottom,
        containerColor = MaterialTheme.colorScheme.surface,
        bottomContent = {
            EnhancedButtonGroup(
                items = listOf("YuNet", "UltraFace"),
                selectedIndex = if (useYuNet) 0 else 1,
                onIndexChange = { onUseYuNetChange(it == 0) },
                enabled = !isDetecting,
                title = null,
                isScrollable = false,
                inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
        }
    )
}