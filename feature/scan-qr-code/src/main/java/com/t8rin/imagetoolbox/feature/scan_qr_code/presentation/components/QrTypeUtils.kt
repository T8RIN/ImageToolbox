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

import android.content.ContentValues
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
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.parameter.EmailType
import ezvcard.parameter.TelephoneType
import ezvcard.property.FormattedName
import ezvcard.property.Organization
import ezvcard.property.StructuredName
import ezvcard.property.Telephone
import ezvcard.property.Title
import io.github.g00fy2.quickie.extensions.DataType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val QrType.name: Int
    get() = when (this) {
        is QrType.Calendar -> R.string.qr_type_calendar_event
        is QrType.Contact -> R.string.qr_type_contact_info
        is QrType.Email -> R.string.qr_type_email
        is QrType.Geo -> R.string.qr_type_geo_point
        is QrType.Phone -> R.string.qr_type_phone
        is QrType.Plain -> R.string.qr_type_plain
        is QrType.Sms -> R.string.qr_type_sms
        is QrType.Url -> R.string.qr_type_url
        is QrType.Wifi -> R.string.qr_type_wifi
    }

val QrType.icon: ImageVector
    get() = when (this) {
        is QrType.Calendar -> Icons.Rounded.CalendarMonth
        is QrType.Contact -> Icons.Rounded.Contacts
        is QrType.Email -> Icons.Rounded.Email
        is QrType.Geo -> Icons.Rounded.LocationOn
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
        is QrType.Sms -> {
            val cleanNumber = phoneNumber.removePrefix("smsto:").removePrefix("sms:")

            return Intent(Intent.ACTION_SENDTO, "smsto:$cleanNumber".toUri()).apply {
                if (message.isNotBlank()) {
                    putExtra("sms_body", message)
                }
            }
        }

        is QrType.Geo -> Intent(Intent.ACTION_VIEW, raw.toUri())

        is QrType.Wifi -> null

        is QrType.Contact -> {
            Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
                if (name.formattedName.isNotBlank()) {
                    putExtra(ContactsContract.Intents.Insert.NAME, name.formattedName)
                }
                if (organization.isNotBlank()) {
                    putExtra(ContactsContract.Intents.Insert.COMPANY, organization)
                }
                if (title.isNotBlank()) {
                    putExtra(ContactsContract.Intents.Insert.JOB_TITLE, title)
                }

                val data = arrayListOf<ContentValues>()

                phones.forEach { phone ->
                    val cv = ContentValues()
                    cv.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    cv.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.number)
                    cv.put(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        when (phone.type) {
                            DataType.TYPE_HOME -> ContactsContract.CommonDataKinds.Phone.TYPE_HOME
                            DataType.TYPE_WORK -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                            DataType.TYPE_FAX -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK
                            DataType.TYPE_MOBILE -> ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                            else -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
                        }
                    )
                    data.add(cv)
                }

                emails.forEach { email ->
                    val cv = ContentValues()
                    cv.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    )
                    cv.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.address)
                    cv.put(
                        ContactsContract.CommonDataKinds.Email.TYPE,
                        when (email.type) {
                            DataType.TYPE_HOME -> ContactsContract.CommonDataKinds.Email.TYPE_HOME
                            DataType.TYPE_WORK -> ContactsContract.CommonDataKinds.Email.TYPE_WORK
                            else -> ContactsContract.CommonDataKinds.Email.TYPE_OTHER
                        }
                    )
                    data.add(cv)
                }

                addresses.forEach { addr ->
                    val cv = ContentValues().apply {
                        put(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                        )
                        put(
                            ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                            addr.addressLines.joinToString(" ")
                        )
                        put(
                            ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                            when (addr.type) {
                                DataType.TYPE_HOME -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME
                                DataType.TYPE_WORK -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
                                else -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER
                            }
                        )
                    }
                    data.add(cv)
                }

                urls.forEach { url ->
                    val cv = ContentValues().apply {
                        put(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                        )
                        put(ContactsContract.CommonDataKinds.Website.URL, url)
                        put(
                            ContactsContract.CommonDataKinds.Website.TYPE,
                            ContactsContract.CommonDataKinds.Website.TYPE_OTHER
                        )
                    }
                    data.add(cv)
                }

                if (name.pronunciation.isNotBlank()) {
                    val cv = ContentValues().apply {
                        put(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                        )
                        put(
                            ContactsContract.CommonDataKinds.StructuredName.PHONETIC_NAME,
                            name.pronunciation
                        )
                    }
                    data.add(cv)
                }

                putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)
            }
        }

        is QrType.Calendar -> {
            Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, summary)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start?.time)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end?.time)
            }
        }
    }?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

fun QrType.Complex.asRaw(): String = raw.ifEmpty {
    runCatching {
        when (this) {
            is QrType.Wifi -> buildString {
                append("WIFI:")
                append("S:").append(ssid).append(";")
                if (encryptionType != QrType.Wifi.EncryptionType.OPEN) {
                    append("T:").append(encryptionType.name).append(";")
                    append("P:").append(password).append(";")
                }
                append(";")
            }

            is QrType.Sms -> buildString {
                append("SMSTO:").append(phoneNumber).append(":").append(message)
            }

            is QrType.Geo -> buildString {
                if (latitude != null && longitude != null) {
                    append("geo:").append(latitude).append(",").append(longitude)
                }
            }

            is QrType.Email -> buildString {
                append("MATMSG:TO:").append(address).append(";")
                append("SUB:").append(subject).append(";")
                append("BODY:").append(body).append(";;")
            }

            is QrType.Phone -> "tel:$number"

            is QrType.Contact -> {
                val vcard = VCard()

                if (!name.isEmpty()) {
                    val structuredName = StructuredName()
                    structuredName.given = name.first
                    structuredName.family = name.last
                    structuredName.additionalNames.add(name.middle)
                    structuredName.prefixes.add(name.prefix)
                    structuredName.suffixes.add(name.suffix)
                    vcard.structuredName = structuredName
                    if (name.formattedName.isNotBlank()) {
                        vcard.formattedName = FormattedName(name.formattedName)
                    }
                }

                if (organization.isNotBlank()) {
                    vcard.organization = Organization().apply { values.add(organization) }
                }
                if (title.isNotBlank()) {
                    vcard.titles.add(Title(title))
                }

                phones.forEach {
                    vcard.telephoneNumbers.add(
                        Telephone(it.number).apply {
                            types.add(mapPhoneType(it.type))
                        }
                    )
                }

                emails.forEach {
                    vcard.emails.add(
                        ezvcard.property.Email(it.address).apply {
                            types.add(mapEmailType(it.type))
                        }
                    )
                }

                addresses.forEach {
                    vcard.addresses.add(
                        ezvcard.property.Address().apply {
                            streetAddress = it.addressLines.joinToString(", ")
                            types.add(mapAddressType(it.type))
                        }
                    )
                }

                urls.forEach {
                    vcard.urls.add(ezvcard.property.Url(it))
                }

                Ezvcard.write(vcard).go()
            }

            is QrType.Calendar -> buildString {
                val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }

                append("BEGIN:VEVENT\n")
                if (summary.isNotBlank()) append("SUMMARY:").append(summary).append("\n")
                if (description.isNotBlank()) append("DESCRIPTION:").append(description)
                    .append("\n")
                if (location.isNotBlank()) append("LOCATION:").append(location).append("\n")
                if (organizer.isNotBlank()) append("ORGANIZER:").append(organizer).append("\n")
                if (status.isNotBlank()) append("STATUS:").append(status).append("\n")

                append("DTSTART:").append(dateFormat.format(start ?: Date())).append("\n")
                append("DTEND:").append(dateFormat.format(end ?: Date())).append("\n")
                append("END:VEVENT")
            }
        }
    }.getOrDefault("")
}

private fun mapPhoneType(type: Int): TelephoneType = when (type) {
    DataType.TYPE_WORK -> TelephoneType.WORK
    DataType.TYPE_HOME -> TelephoneType.HOME
    DataType.TYPE_FAX -> TelephoneType.FAX
    DataType.TYPE_MOBILE -> TelephoneType.CELL
    else -> TelephoneType.VOICE
}

private fun mapEmailType(type: Int): EmailType = when (type) {
    DataType.TYPE_WORK -> EmailType.WORK
    DataType.TYPE_HOME -> EmailType.HOME
    else -> EmailType.INTERNET
}

private fun mapAddressType(type: Int): ezvcard.parameter.AddressType = when (type) {
    DataType.TYPE_WORK -> ezvcard.parameter.AddressType.WORK
    DataType.TYPE_HOME -> ezvcard.parameter.AddressType.HOME
    else -> ezvcard.parameter.AddressType.HOME
}