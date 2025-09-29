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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import io.github.g00fy2.quickie.content.QRContent
import io.github.g00fy2.quickie.content.QRContent.CalendarEvent.CalendarDateTime
import io.github.g00fy2.quickie.content.QRContent.ContactInfo.Address.AddressType
import io.github.g00fy2.quickie.content.QRContent.Email.EmailType
import io.github.g00fy2.quickie.content.QRContent.Phone.PhoneType
import io.github.g00fy2.quickie.extensions.DataType
import java.util.Calendar
import java.util.Date

sealed interface QrType {
    val raw: String

    data class Plain(
        override val raw: String
    ) : QrType

    data class Wifi(
        override val raw: String,
        val ssid: String,
        val password: String,
        val encryptionType: Int
    ) : QrType

    data class Url(
        override val raw: String,
        val title: String,
        val url: String
    ) : QrType

    data class Sms(
        override val raw: String,
        val message: String,
        val phoneNumber: String
    ) : QrType

    data class GeoPoint(
        override val raw: String,
        val lat: Double,
        val lng: Double
    ) : QrType

    data class Email(
        override val raw: String,
        val address: String,
        val body: String,
        val subject: String,
        val type: Int
    ) : QrType

    data class Phone(
        override val raw: String,
        val number: String,
        val type: Int
    ) : QrType

    data class ContactInfo(
        override val raw: String,
        val addresses: List<Address>,
        val emails: List<Email>,
        val name: PersonName,
        val organization: String,
        val phones: List<Phone>,
        val title: String,
        val urls: List<String>
    ) : QrType {
        data class Address(
            val addressLines: List<String>,
            val type: Int
        )

        data class PersonName(
            val first: String,
            val formattedName: String,
            val last: String,
            val middle: String,
            val prefix: String,
            val pronunciation: String,
            val suffix: String
        )
    }

    data class CalendarEvent(
        override val raw: String,
        val description: String,
        val end: Date,
        val location: String,
        val organizer: String,
        val start: Date,
        val status: String,
        val summary: String
    ) : QrType
}

fun QRContent.toQrType(): QrType {
    val raw = rawValue ?: rawBytes?.toString(Charsets.UTF_8).orEmpty()

    return when (this) {
        is QRContent.Plain -> QrType.Plain(raw)

        is QRContent.Wifi -> QrType.Wifi(
            raw = raw,
            ssid = ssid,
            password = password,
            encryptionType = encryptionType
        )

        is QRContent.Url -> QrType.Url(
            raw = raw,
            title = title,
            url = url
        )

        is QRContent.Sms -> QrType.Sms(
            raw = raw,
            message = message,
            phoneNumber = phoneNumber
        )

        is QRContent.GeoPoint -> QrType.GeoPoint(
            raw = raw,
            lat = lat,
            lng = lng
        )

        is QRContent.Email -> QrType.Email(
            raw = raw,
            address = address,
            body = body,
            subject = subject,
            type = type.toData()
        )

        is QRContent.Phone -> QrType.Phone(
            raw = raw,
            number = number,
            type = type.toData()
        )

        is QRContent.ContactInfo -> QrType.ContactInfo(
            raw = raw,
            addresses = addresses.map {
                QrType.ContactInfo.Address(
                    addressLines = it.addressLines,
                    type = it.type.toData()
                )
            },
            emails = emails.map {
                QrType.Email(
                    raw = raw,
                    address = it.address,
                    body = it.body,
                    subject = it.subject,
                    type = it.type.toData()
                )
            },
            name = QrType.ContactInfo.PersonName(
                first = name.first,
                formattedName = name.formattedName,
                last = name.last,
                middle = name.middle,
                prefix = name.prefix,
                pronunciation = name.pronunciation,
                suffix = name.suffix
            ),
            organization = organization,
            phones = phones.map {
                QrType.Phone(
                    raw = raw,
                    number = it.number,
                    type = it.type.toData()
                )
            },
            title = title,
            urls = urls
        )

        is QRContent.CalendarEvent -> QrType.CalendarEvent(
            raw = raw,
            description = description,
            end = end.toDate(),
            location = location,
            organizer = organizer,
            start = start.toDate(),
            status = status,
            summary = summary
        )
    }
}

private fun PhoneType.toData(): Int = when (this) {
    PhoneType.WORK -> DataType.TYPE_WORK
    PhoneType.HOME -> DataType.TYPE_HOME
    PhoneType.FAX -> DataType.TYPE_FAX
    PhoneType.MOBILE -> DataType.TYPE_MOBILE
    else -> DataType.TYPE_UNKNOWN
}

private fun AddressType.toData(): Int = when (this) {
    AddressType.WORK -> DataType.TYPE_WORK
    AddressType.HOME -> DataType.TYPE_HOME
    else -> DataType.TYPE_UNKNOWN
}

private fun EmailType.toData(): Int = when (this) {
    EmailType.WORK -> DataType.TYPE_WORK
    EmailType.HOME -> DataType.TYPE_HOME
    else -> DataType.TYPE_UNKNOWN
}

private fun CalendarDateTime.toDate(): Date {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hours)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, seconds)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.time
}