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

@file:Suppress("KotlinConstantConditions")

package com.t8rin.imagetoolbox.feature.erase_background.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
fun AutoEraseBackgroundCard(
    modifier: Modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
    onClick: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        Modifier
            .then(modifier)
            .container(
                resultPadding = 8.dp,
                shape = ShapeDefaults.extraLarge
            )
    ) {
        val notFoss = BuildConfig.FLAVOR != "foss"
        if (notFoss) {
            Row(
                modifier = Modifier
                    .container(
                        resultPadding = 0.dp,
                        color = MaterialTheme.colorScheme.mixedContainer.copy(0.7f)
                    )
                    .hapticsClickable(onClick = onClick)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(id = R.string.auto_erase_background),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onMixedContainer
                )
                Icon(
                    imageVector = Icons.Rounded.AutoFixHigh,
                    contentDescription = stringResource(R.string.auto_erase_background),
                    tint = MaterialTheme.colorScheme.onMixedContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        EnhancedButton(
            containerColor = MaterialTheme.colorScheme.mixedContainer.copy(0.4f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = onReset,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.default,
            isShadowClip = true
        ) {
            Icon(
                imageVector = Icons.Rounded.SettingsBackupRestore,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(id = R.string.restore_image))
        }
    }
}