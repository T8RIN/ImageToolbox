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

package com.t8rin.imagetoolbox.core.domain.model

import com.t8rin.imagetoolbox.core.domain.utils.cast
import java.util.Date

sealed interface QrType {
    val raw: String

    data class Plain(
        override val raw: String
    ) : QrType

    data class Url(
        override val raw: String,
        val title: String,
        val url: String
    ) : QrType

    sealed interface Complex : QrType

    data class Wifi(
        override val raw: String,
        val ssid: String,
        val password: String,
        val encryptionType: EncryptionType
    ) : Complex {
        enum class EncryptionType {
            OPEN, WPA, WEP
        }
    }

    data class Sms(
        override val raw: String,
        val message: String,
        val phoneNumber: String
    ) : Complex

    data class GeoPoint(
        override val raw: String,
        val lat: Double,
        val lng: Double
    ) : Complex

    data class Email(
        override val raw: String,
        val address: String,
        val body: String,
        val subject: String,
        val type: Int
    ) : Complex

    data class Phone(
        override val raw: String,
        val number: String,
        val type: Int
    ) : Complex

    data class ContactInfo(
        override val raw: String,
        val addresses: List<Address>,
        val emails: List<Email>,
        val name: PersonName,
        val organization: String,
        val phones: List<Phone>,
        val title: String,
        val urls: List<String>
    ) : Complex {
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
    ) : Complex

    companion object {
        val Empty = Plain("")
    }
}

inline fun <reified T : QrType> T.copy(raw: String): T = when (this) {
    is QrType.Plain -> this.copy(raw = raw)
    is QrType.Wifi -> this.copy(raw = raw)
    is QrType.Url -> this.copy(raw = raw)
    is QrType.Sms -> this.copy(raw = raw)
    is QrType.GeoPoint -> this.copy(raw = raw)
    is QrType.Email -> this.copy(raw = raw)
    is QrType.Phone -> this.copy(raw = raw)
    is QrType.ContactInfo -> this.copy(raw = raw)
    is QrType.CalendarEvent -> this.copy(raw = raw)
}.cast()