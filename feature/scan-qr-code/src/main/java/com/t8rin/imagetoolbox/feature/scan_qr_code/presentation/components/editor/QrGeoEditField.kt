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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Latitude
import com.t8rin.imagetoolbox.core.resources.icons.Longitude
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.value.filterDecimal

@Composable
internal fun QrGeoEditField(
    value: QrType.Geo,
    onValueChange: (QrType.Geo) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var latitude by remember {
            mutableStateOf(value.latitude?.toString().orEmpty().trimTrailingZero())
        }
        var longitude by remember {
            mutableStateOf(value.longitude?.toString().orEmpty().trimTrailingZero())
        }

        RoundedTextField(
            value = latitude,
            onValueChange = {
                latitude = it.filterDecimal()

                latitude.toDoubleOrNull()?.coerceIn(LatitudeRange)?.let { new ->
                    onValueChange(value.copy(latitude = new))
                }
            },
            label = { Text(stringResource(R.string.latitude)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Latitude,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
        RoundedTextField(
            value = longitude,
            onValueChange = {
                longitude = it.filterDecimal()

                longitude.toDoubleOrNull()?.coerceIn(LongitudeRange)?.let { new ->
                    onValueChange(value.copy(longitude = new))
                }
            },
            label = { Text(stringResource(R.string.longitude)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Longitude,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }
}

private val LatitudeRange = -90.0..90.0
private val LongitudeRange = -180.0..180.0