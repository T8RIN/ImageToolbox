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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.resources.R

@Composable
fun QrType.name(): String = stringResource(
    when (this) {
        is QrType.CalendarEvent -> R.string.qr_type_calendar_event
        is QrType.ContactInfo -> R.string.qr_type_contact_info
        is QrType.Email -> R.string.qr_type_email
        is QrType.GeoPoint -> R.string.qr_type_geo_point
        is QrType.Phone -> R.string.qr_type_phone
        is QrType.Plain -> R.string.qr_type_plain
        is QrType.Sms -> R.string.qr_type_sms
        is QrType.Url -> R.string.qr_type_url
        is QrType.Wifi -> R.string.qr_type_wifi
    }
)