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

package com.t8rin.imagetoolbox.core.ui.widget.image

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.resources.icons.MoreVert
import com.t8rin.imagetoolbox.core.resources.icons.Share
import com.t8rin.imagetoolbox.core.ui.theme.onSecondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.secondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun ImageViewerActions(
    uri: Uri?,
    onShare: (Uri) -> Unit,
    onEdit: (Uri) -> Unit
) {
    var expanded by remember(uri) { mutableStateOf(false) }

    Box {
        EnhancedIconButton(
            onClick = { expanded = true },
            enabled = uri != null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(24.dp),
            forceMinimumInteractiveComponentSize = false,
            containerColor = MaterialTheme.colorScheme.secondaryContainerFixed,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainerFixed,
            enableAutoShadowAndBorder = false
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = stringResource(R.string.actions),
                modifier = Modifier.size(18.dp)
            )
        }
        EnhancedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = ShapeDefaults.large
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(horizontal = 8.dp)
            ) {
                EnhancedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.top,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        expanded = false
                        uri?.let(onShare)
                    }
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.share))
                }
                EnhancedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.bottom,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        expanded = false
                        uri?.let(onEdit)
                    }
                ) {
                    Icon(Icons.Rounded.EditAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.edit))
                }
            }
        }
    }
}