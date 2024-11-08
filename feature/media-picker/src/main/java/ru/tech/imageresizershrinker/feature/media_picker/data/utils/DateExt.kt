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

package ru.tech.imageresizershrinker.feature.media_picker.data.utils

import android.text.format.DateFormat
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.DEFAULT_DATE_FORMAT
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.EXTENDED_DATE_FORMAT
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.WEEKLY_DATE_FORMAT
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

data class DateExt(
    val month: String,
    val day: Int,
    val year: Int
)

fun Long.getDateExt(): DateExt {
    val mediaDate = Calendar.getInstance(Locale.US)
    mediaDate.timeInMillis = this * 1000L
    return DateExt(
        month = mediaDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)!!,
        day = mediaDate.get(Calendar.DAY_OF_MONTH),
        year = mediaDate.get(Calendar.YEAR)
    )
}

fun Long.getDate(
    format: CharSequence = DEFAULT_DATE_FORMAT,
): String {
    val mediaDate = Calendar.getInstance(Locale.US)
    mediaDate.timeInMillis = this * 1000L
    return DateFormat.format(format, mediaDate).toString()
}

fun Long.getDate(
    format: CharSequence = DEFAULT_DATE_FORMAT,
    weeklyFormat: CharSequence = WEEKLY_DATE_FORMAT,
    extendedFormat: CharSequence = EXTENDED_DATE_FORMAT,
    stringToday: String,
    stringYesterday: String
): String {
    val currentDate = Calendar.getInstance(Locale.US)
    currentDate.timeInMillis = System.currentTimeMillis()
    val mediaDate = Calendar.getInstance(Locale.US)
    mediaDate.timeInMillis = this * 1000L
    val different: Long = System.currentTimeMillis() - mediaDate.timeInMillis
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24

    val daysDifference = different / daysInMilli

    return when (daysDifference.toInt()) {
        0 -> {
            if (currentDate.get(Calendar.DATE) != mediaDate.get(Calendar.DATE)) {
                stringYesterday
            } else {
                stringToday
            }
        }

        1 -> {
            stringYesterday
        }

        else -> {
            if (daysDifference.toInt() in 2..5) {
                DateFormat.format(weeklyFormat, mediaDate).toString()
            } else {
                if (currentDate.get(Calendar.YEAR) > mediaDate.get(Calendar.YEAR)) {
                    DateFormat.format(extendedFormat, mediaDate).toString()
                } else DateFormat.format(format, mediaDate).toString()
            }
        }
    }
}

fun Long.getMonth(): String {
    val currentDate =
        Calendar.getInstance(Locale.US).apply { timeInMillis = System.currentTimeMillis() }
    val mediaDate = Calendar.getInstance(Locale.US).apply { timeInMillis = this@getMonth * 1000L }
    val month = mediaDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)!!
    val year = mediaDate.get(Calendar.YEAR)
    return if (currentDate.get(Calendar.YEAR) != mediaDate.get(Calendar.YEAR))
        "$month $year"
    else month
}

fun getMonth(date: String): String {
    return try {
        val dateFormatExtended =
            SimpleDateFormat(EXTENDED_DATE_FORMAT, Locale.US).parse(date)
        val cal = Calendar.getInstance(Locale.US).apply { timeInMillis = dateFormatExtended!!.time }
        val month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)!!
        val year = cal.get(Calendar.YEAR)
        "$month $year"
    } catch (e: ParseException) {
        try {
            val dateFormat = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.US).parse(date)
            val cal = Calendar.getInstance(Locale.US).apply { timeInMillis = dateFormat!!.time }
            cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)!!
        } catch (e: ParseException) {
            ""
        }
    }
}

fun getDateHeader(
    startDate: DateExt,
    endDate: DateExt
): String {
    return if (startDate.year == endDate.year) {
        if (startDate.month == endDate.month) {
            if (startDate.day == endDate.day) {
                "${startDate.month} ${startDate.day}, ${startDate.year}"
            } else "${startDate.month} ${startDate.day} - ${endDate.day}, ${startDate.year}"
        } else
            "${startDate.month} ${startDate.day} - ${endDate.month} ${endDate.day}, ${startDate.year}"
    } else {
        "${startDate.month} ${startDate.day}, ${startDate.year} - ${endDate.month} ${endDate.day}, ${endDate.year}"
    }
}

fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "00:00"
    } else {
        String.format(
            locale = Locale.getDefault(),
            format = "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}

fun String?.formatMinSec(): String {
    return when (val value = this?.toLong()) {
        null -> ""
        else -> value.formatMinSec()
    }
}