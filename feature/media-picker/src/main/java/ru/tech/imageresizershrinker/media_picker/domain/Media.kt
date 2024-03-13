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

package ru.tech.imageresizershrinker.media_picker.domain

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Parcelable
import android.text.format.DateFormat
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Immutable
@Parcelize
data class Media(
    val id: Long = 0,
    val label: String,
    val uri: Uri,
    val path: String,
    val relativePath: String,
    val albumID: Long,
    val albumLabel: String,
    val timestamp: Long,
    val expiryTimestamp: Long? = null,
    val takenTimestamp: Long? = null,
    val fullDate: String,
    val mimeType: String,
    val favorite: Int,
    val trashed: Int,
    val duration: String? = null,
) : Parcelable {

    @IgnoredOnParcel
    @Stable
    val isVideo: Boolean = mimeType.startsWith("video/") && duration != null

    @IgnoredOnParcel
    @Stable
    val isImage: Boolean = mimeType.startsWith("image/")

    @IgnoredOnParcel
    @Stable
    val isTrashed: Boolean = trashed == 1

    @IgnoredOnParcel
    @Stable
    val isFavorite: Boolean = favorite == 1

    @Stable
    override fun toString(): String {
        return "$id, $path, $fullDate, $mimeType, favorite=$favorite"
    }

    /**
     * Used to determine if the Media object is not accessible
     * via MediaStore.
     * This happens when the user tries to open media from an app
     * using external sources (in our case, Gallery Media Viewer), but
     * the specific media is only available internally in that app
     * (Android/data(OR media)/com.package.name/)
     *
     * If it's readUriOnly then we know that we should expect a barebone
     * Media object with limited functionality (no favorites, trash, timestamp etc)
     */
    @IgnoredOnParcel
    @Stable
    val readUriOnly: Boolean = albumID == -99L && albumLabel == ""

    /**
     * Determine if the current media is a raw format
     *
     * Checks if [mimeType] starts with "image/x-" or "image/vnd."
     *
     * Most used formats:
     * - ARW: image/x-sony-arw
     * - CR2: image/x-canon-cr2
     * - CRW: image/x-canon-crw
     * - DCR: image/x-kodak-dcr
     * - DNG: image/x-adobe-dng
     * - ERF: image/x-epson-erf
     * - K25: image/x-kodak-k25
     * - KDC: image/x-kodak-kdc
     * - MRW: image/x-minolta-mrw
     * - NEF: image/x-nikon-nef
     * - ORF: image/x-olympus-orf
     * - PEF: image/x-pentax-pef
     * - RAF: image/x-fuji-raf
     * - RAW: image/x-panasonic-raw
     * - SR2: image/x-sony-sr2
     * - SRF: image/x-sony-srf
     * - X3F: image/x-sigma-x3f
     *
     * Other proprietary image types in the standard:
     * image/vnd.manufacturer.filename_extension for instance for NEF by Nikon and .mrv for Minolta:
     * - NEF: image/vnd.nikon.nef
     * - Minolta: image/vnd.minolta.mrw
     */
    @IgnoredOnParcel
    @Stable
    val isRaw: Boolean =
        mimeType.isNotBlank() && (mimeType.startsWith("image/x-") || mimeType.startsWith("image/vnd."))

    @IgnoredOnParcel
    @Stable
    val fileExtension: String = label.substringAfterLast(".").removePrefix(".")

    @IgnoredOnParcel
    @Stable
    val volume: String = path.substringBeforeLast("/").removeSuffix(relativePath.removeSuffix("/"))

    companion object {
        fun createFromUri(
            context: Context,
            uri: Uri
        ): Media? {
            if (uri.path == null) return null
            val extension = uri.toString().substringAfterLast(".")
            var mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()
            var duration: String? = null
            try {
                val retriever = MediaMetadataRetriever().apply {
                    setDataSource(context, uri)
                }
                val hasVideo =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)
                val isVideo = "yes" == hasVideo
                if (isVideo) {
                    duration =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                }
                if (mimeType.isEmpty()) {
                    mimeType = if (isVideo) "video/*" else "image/*"
                }
            } catch (_: Exception) {
            }
            var timestamp = 0L
            uri.path?.let { File(it) }?.let {
                timestamp = try {
                    it.lastModified()
                } catch (_: Exception) {
                    0L
                }
            }
            var formattedDate = ""
            if (timestamp != 0L) {
                formattedDate = timestamp.getDate(EXTENDED_DATE_FORMAT)
            }
            return Media(
                id = Random(System.currentTimeMillis()).nextLong(-1000, 25600000),
                label = uri.toString().substringAfterLast("/"),
                uri = uri,
                path = uri.path.toString(),
                relativePath = uri.path.toString().substringBeforeLast("/"),
                albumID = -99L,
                albumLabel = "",
                timestamp = timestamp,
                fullDate = formattedDate,
                mimeType = mimeType,
                duration = duration,
                favorite = 0,
                trashed = 0
            )
        }
    }
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
            "%02d:%02d",
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

const val WEEKLY_DATE_FORMAT = "EEEE"
const val DEFAULT_DATE_FORMAT = "EEE, MMMM d"
const val EXTENDED_DATE_FORMAT = "EEE, MMM d, yyyy"
const val FULL_DATE_FORMAT = "EEEE, MMMM d, yyyy, hh:mm a"
const val HEADER_DATE_FORMAT = "MMMM d, yyyy\n" + "h:mm a"
const val EXIF_DATE_FORMAT = "MMMM d, yyyy • h:mm a"