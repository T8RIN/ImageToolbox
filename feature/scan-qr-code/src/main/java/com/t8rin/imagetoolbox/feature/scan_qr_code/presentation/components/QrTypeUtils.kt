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

import android.content.Intent
import android.provider.CalendarContract
import android.provider.ContactsContract
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.domain.model.ifNotEmpty
import com.t8rin.imagetoolbox.core.resources.R


val QrType.name: Int
    get() = when (this) {
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

val QrType.icon: ImageVector
    get() = when (this) {
        is QrType.CalendarEvent -> Icons.Rounded.CalendarMonth
        is QrType.ContactInfo -> Icons.Rounded.Contacts
        is QrType.Email -> Icons.Rounded.Email
        is QrType.GeoPoint -> Icons.Rounded.LocationOn
        is QrType.Phone -> Icons.Rounded.Phone
        is QrType.Sms -> Icons.Rounded.Sms
        is QrType.Wifi -> Icons.Rounded.Wifi
        is QrType.Plain -> Icons.Rounded.TextFields
        is QrType.Url -> Icons.Rounded.Link
    }

fun QrType.toIntent(): Intent? = ifNotEmpty {
    when (this) {
        is QrType.Plain -> Intent(Intent.ACTION_SEND).setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, raw)

        is QrType.Url -> Intent(Intent.ACTION_VIEW, url.toUri())

        is QrType.Email -> Intent(Intent.ACTION_SENDTO, raw.toUri())
        is QrType.Phone -> Intent(Intent.ACTION_DIAL, raw.toUri())
        is QrType.Sms -> Intent(Intent.ACTION_SENDTO, raw.toUri())

        is QrType.GeoPoint -> Intent(Intent.ACTION_VIEW, raw.toUri())

        is QrType.Wifi -> null

        is QrType.ContactInfo -> {
            Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.NAME, name.formattedName)
                putExtra(ContactsContract.Intents.Insert.COMPANY, organization)
                putExtra(ContactsContract.Intents.Insert.JOB_TITLE, title)
                if (phones.isNotEmpty()) {
                    putExtra(ContactsContract.Intents.Insert.PHONE, phones[0].number)
                }
                if (emails.isNotEmpty()) {
                    putExtra(ContactsContract.Intents.Insert.EMAIL, emails[0].address)
                }
            }
        }

        is QrType.CalendarEvent -> {
            Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, summary)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.time)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.time)
            }
        }
    }
}