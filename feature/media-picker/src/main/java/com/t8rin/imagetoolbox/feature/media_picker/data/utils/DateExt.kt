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

package com.t8rin.imagetoolbox.feature.media_picker.data.utils

import android.icu.text.SimpleDateFormat
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.DEFAULT_DATE_FORMAT
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.EXTENDED_DATE_FORMAT
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.WEEKLY_DATE_FORMAT
import java.util.Calendar
import java.util.Locale

fun Long.getDate(
    format: String = DEFAULT_DATE_FORMAT,
    weeklyFormat: String = WEEKLY_DATE_FORMAT,
    extendedFormat: String = EXTENDED_DATE_FORMAT,
    stringToday: String = getString(R.string.header_today),
    stringYesterday: String = getString(R.string.header_yesterday)
): String {
    val currentDate = Calendar.getInstance(Locale.getDefault()).apply {
        timeInMillis = System.currentTimeMillis()
    }

    val mediaDate = Calendar.getInstance(Locale.getDefault()).apply {
        timeInMillis = this@getDate * 1000L
    }

    val daysDifference =
        (System.currentTimeMillis() - mediaDate.timeInMillis) / (1000 * 60 * 60 * 24)

    return when (daysDifference.toInt()) {
        0 -> if (currentDate.get(Calendar.DATE) != mediaDate.get(Calendar.DATE)) {
            stringYesterday
        } else {
            stringToday
        }

        1 -> stringYesterday

        else -> {
            if (daysDifference.toInt() in 2..5) {
                SimpleDateFormat(weeklyFormat, Locale.getDefault())
            } else {
                if (currentDate.get(Calendar.YEAR) > mediaDate.get(Calendar.YEAR)) {
                    SimpleDateFormat(extendedFormat, Locale.getDefault())
                } else {
                    SimpleDateFormat(format, Locale.getDefault())
                }
            }.format(mediaDate.time)
        }
    }.replaceFirstChar { it.titlecase(Locale.getDefault()) }
}