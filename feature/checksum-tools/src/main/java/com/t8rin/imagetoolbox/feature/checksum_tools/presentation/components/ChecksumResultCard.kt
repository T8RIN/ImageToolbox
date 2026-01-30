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

package com.t8rin.imagetoolbox.feature.checksum_tools.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.Green
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.Red
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContentColor
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
internal fun ChecksumResultCard(
    isCorrect: Boolean
) {
    val containerColor = animateColorAsState(
        when {
            isCorrect -> Green
            else -> Red
        }
    ).value.blend(
        color = Color.Black,
        fraction = 0.25f
    )

    val contentColor = containerColor.inverse(
        fraction = { 1f },
        darkMode = true
    )

    CompositionLocalProvider(
        LocalIconShapeContentColor provides contentColor,
        LocalIconShapeContainerColor provides containerColor.blend(
            color = Color.Black,
            fraction = 0.15f
        )
    ) {
        PreferenceItem(
            title = if (isCorrect) {
                stringResource(R.string.match)
            } else {
                stringResource(R.string.difference)
            },
            subtitle = if (isCorrect) {
                stringResource(R.string.match_sub)
            } else {
                stringResource(R.string.difference_sub)
            },
            startIcon = if (isCorrect) {
                Icons.Outlined.CheckCircle
            } else {
                Icons.Outlined.WarningAmber
            },
            contentColor = contentColor,
            containerColor = containerColor,
            overrideIconShapeContentColor = true,
            modifier = Modifier.fillMaxWidth(),
            onClick = null
        )
    }
}

@Composable
@Preview
private fun Preview() = ImageToolboxThemeForPreview(false) {
    CompositionLocalProvider(
        LocalSettingsState provides LocalSettingsState.current.copy(
            drawContainerShadows = false
        )
    ) {
        ChecksumResultCard(false)
    }
}