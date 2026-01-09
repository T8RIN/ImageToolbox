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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MobileShare
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.utils.animation.springySpec
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import kotlinx.coroutines.delay

@Composable
fun SendLogsSettingItem(
    onClick: (onComplete: () -> Unit) -> Unit,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
    shape: Shape = ShapeDefaults.center,
    color: Color = MaterialTheme.colorScheme.mixedContainer.copy(0.9f),
    contentColor: Color = MaterialTheme.colorScheme.onMixedContainer
) {
    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    val progressAnimatable = remember { Animatable(if (isLoading) 1f else 0f) }
    val progress = progressAnimatable.value

    LaunchedEffect(isLoading) {
        delay(400)
        if (isLoading) {
            progressAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = springySpec()
            )
        } else {
            progressAnimatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(200)
            )
        }
    }

    PreferenceItemOverload(
        contentColor = contentColor,
        shape = shape,
        onClick = {
            isLoading = true
            onClick { isLoading = false }
        },
        startIcon = {
            Icon(
                imageVector = Icons.Outlined.MobileShare,
                contentDescription = null
            )
        },
        title = stringResource(R.string.send_logs),
        subtitle = stringResource(R.string.send_logs_sub),
        containerColor = color,
        modifier = modifier,
        endIcon = if (progress > 0f) {
            {
                EnhancedCircularProgressIndicator(
                    modifier = Modifier.size(24.dp * progress),
                    trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                    strokeWidth = 3.dp
                )
            }
        } else null
    )
}