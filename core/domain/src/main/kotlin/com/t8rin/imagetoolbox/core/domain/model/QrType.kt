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

    fun isEmpty(): Boolean

    data class Plain(
        override val raw: String
    ) : QrType {
        override fun isEmpty(): Boolean = raw.isBlank()
    }

    data class Url(
        override val raw: String,
        val title: String,
        val url: String
    ) : QrType {
        override fun isEmpty(): Boolean = title.isBlank() && url.isBlank()
    }

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

        override fun isEmpty(): Boolean = ssid.isBlank() && password.isBlank()
    }

    data class Sms(
        override val raw: String,
        val message: String,
        val phoneNumber: String
    ) : Complex {
        override fun isEmpty(): Boolean = message.isBlank() && phoneNumber.isBlank()
    }

    data class Geo(
        override val raw: String,
        val latitude: Double,
        val longitude: Double
    ) : Complex {
        override fun isEmpty(): Boolean = false
    }

    data class Email(
        override val raw: String,
        val address: String,
        val body: String,
        val subject: String,
        val type: Int
    ) : Complex {
        override fun isEmpty(): Boolean = address.isBlank() && body.isBlank() && subject.isBlank()
    }

    data class Phone(
        override val raw: String,
        val number: String,
        val type: Int
    ) : Complex {
        override fun isEmpty(): Boolean = number.isBlank()
    }

    data class Contact(
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
        ) {
            fun isEmpty() = first.isBlank() &&
                    formattedName.isBlank() &&
                    last.isBlank() &&
                    middle.isBlank() &&
                    prefix.isBlank() &&
                    pronunciation.isBlank() &&
                    suffix.isBlank()
        }

        override fun isEmpty(): Boolean =
            addresses.isEmpty() && emails.isEmpty() && name.isEmpty() && organization.isBlank() && phones.isEmpty() && title.isBlank() && urls.isEmpty()
    }

    data class Calendar(
        override val raw: String,
        val description: String,
        val end: Date,
        val location: String,
        val organizer: String,
        val start: Date,
        val status: String,
        val summary: String
    ) : Complex {
        override fun isEmpty(): Boolean =
            description.isBlank() && location.isBlank() && organizer.isBlank() && status.isBlank() && summary.isBlank()
    }

    companion object {
        val Empty = Plain("")
    }
}

inline fun <reified T : QrType> T.copy(raw: String): T = when (this) {
    is QrType.Plain -> this.copy(raw = raw)
    is QrType.Wifi -> this.copy(raw = raw)
    is QrType.Url -> this.copy(raw = raw)
    is QrType.Sms -> this.copy(raw = raw)
    is QrType.Geo -> this.copy(raw = raw)
    is QrType.Email -> this.copy(raw = raw)
    is QrType.Phone -> this.copy(raw = raw)
    is QrType.Contact -> this.copy(raw = raw)
    is QrType.Calendar -> this.copy(raw = raw)
}.cast()


inline fun <T> QrType.ifNotEmpty(action: () -> T): T? = if (!isEmpty()) action() else null