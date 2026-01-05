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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.PARTNER_FREE_SOFTWARE
import com.t8rin.imagetoolbox.core.domain.utils.Flavor
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.HandshakeAlt
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRow
import java.util.Locale

@Composable
fun FreeSoftwarePartnerSettingItem(
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    if (Flavor.isFoss() || Locale.getDefault().language != "ru") return

    val linkHandler = LocalUriHandler.current
    PreferenceRow(
        shape = shape,
        onClick = {
            linkHandler.openUri(PARTNER_FREE_SOFTWARE)
        },
        startIcon = Icons.Outlined.HandshakeAlt,
        title = stringResource(R.string.free_software_partner),
        subtitle = stringResource(R.string.free_software_partner_sub),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
            .compositeOver(MaterialTheme.colorScheme.surface),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.9f),
        modifier = modifier
    )
}