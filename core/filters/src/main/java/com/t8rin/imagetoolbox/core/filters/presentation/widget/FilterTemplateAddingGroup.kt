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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberBarcodeScanner
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalResourceManager
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState

@SuppressLint("StringFormatInvalid")
@Composable
internal fun FilterTemplateAddingGroup(
    component: FilterTemplateCreationSheetComponent,
    onAddTemplateFilterFromString: (
        string: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit
    ) -> Unit,
    onAddTemplateFilterFromUri: (
        uri: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit
    ) -> Unit
) {
    val toastHostState = LocalToastHostState.current
    val context = LocalResourceManager.current

    fun addTemplateFilterFromString(
        string: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit
    ) = onAddTemplateFilterFromString(string, onSuccess, onFailure)

    fun addTemplateFilterFromUri(
        uri: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit
    ) = onAddTemplateFilterFromUri(uri, onSuccess, onFailure)

    val scanner = rememberBarcodeScanner {
        addTemplateFilterFromString(
            string = it.raw,
            onSuccess = { filterName, filtersCount ->
                toastHostState.showToast(
                    message = context.getString(
                        R.string.added_filter_template,
                        filterName,
                        filtersCount
                    ),
                    icon = Icons.Outlined.AutoFixHigh
                )
            },
            onFailure = {
                toastHostState.showToast(
                    message = context.getString(R.string.scanned_qr_code_isnt_filter_template),
                    icon = Icons.Rounded.QrCodeScanner
                )
            }
        )
    }

    val importFromFileLauncher = rememberFilePicker { uri: Uri ->
        addTemplateFilterFromUri(
            uri = uri.toString(),
            onSuccess = { filterName, filtersCount ->
                toastHostState.showToast(
                    message = context.getString(
                        R.string.added_filter_template,
                        filterName,
                        filtersCount
                    ),
                    icon = Icons.Outlined.AutoFixHigh
                )
            },
            onFailure = {
                toastHostState.showToast(
                    message = context.getString(R.string.opened_file_have_no_filter_template),
                    icon = Icons.Outlined.AutoFixHigh
                )
            }
        )
    }

    var showFilterTemplateCreationSheet by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
    ) {
        EnhancedIconButton(
            onClick = scanner::scan,
            containerColor = MaterialTheme.colorScheme.tertiary
        ) {
            Icon(
                imageVector = Icons.Rounded.QrCodeScanner,
                contentDescription = "Scan QR"
            )
        }
        EnhancedIconButton(
            onClick = importFromFileLauncher::pickFile,
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                imageVector = Icons.Outlined.FileOpen,
                contentDescription = "Import From File"
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        EnhancedButton(
            onClick = { showFilterTemplateCreationSheet = true },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Text(stringResource(R.string.create_new))
        }
    }

    FilterTemplateCreationSheet(
        visible = showFilterTemplateCreationSheet,
        onDismiss = { showFilterTemplateCreationSheet = false },
        component = component
    )
}