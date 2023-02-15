package ru.tech.imageresizershrinker.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.util.*
import kotlin.math.max


object BitmapUtils {

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun Bitmap.flip(value: Boolean): Bitmap {
        return if (value) {
            val matrix = Matrix().apply { postScale(-1f, 1f, width / 2f, width / 2f) }
            Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        } else this
    }

    fun Bitmap.resizeBitmap(width_: Int, height_: Int, adaptiveResize: Boolean): Bitmap {
        val max = max(width_, height_)
        return if (!adaptiveResize) {
            Bitmap.createScaledBitmap(
                this,
                width_,
                height_,
                false
            )
        } else {
            kotlin.runCatching {
                if (height >= width) {
                    val aspectRatio = width.toDouble() / height.toDouble()
                    val targetWidth = (max * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(this, targetWidth, max, false)
                } else {
                    val aspectRatio = height.toDouble() / width.toDouble()
                    val targetHeight = (max * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(this, max, targetHeight, false)
                }
            }.getOrNull() ?: this
        }
    }

    fun Context.decodeBitmapFromUri(
        uri: Uri,
        outPadding: Rect? = null,
        options: BitmapFactory.Options = BitmapFactory.Options(),
        onGetBitmap: (Bitmap) -> Unit,
        onGetExif: (ExifInterface?) -> Unit,
        onGetMimeType: (Int) -> Unit,
    ) {
        val bmp = kotlin.runCatching {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
            BitmapFactory.decodeFileDescriptor(fileDescriptor, outPadding, options).also {
                parcelFileDescriptor?.close()
            }
        }.getOrNull()
        bmp?.let { onGetBitmap(it) }

        val fd = contentResolver.openFileDescriptor(uri, "r")
        onGetExif(fd?.fileDescriptor?.let { ExifInterface(it) })
        val mime = contentResolver.getMimeType(uri) ?: ""
        val mimeInt = if ("png" in mime) 2 else if ("webp" in mime) 1 else 0
        onGetMimeType(mimeInt)
        fd?.close()
    }

    private fun ContentResolver.getMimeType(uri: Uri): String? {
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme) getType(uri)
        else {
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(
                        uri.toString()
                    ).lowercase(Locale.getDefault())
                )
        }
    }

    fun Bitmap.previewBitmap(
        quality: Float,
        widthValue: Int?,
        heightValue: Int?,
        mime: Int,
        resize: Int,
        rotation: Float,
        isFlipped: Boolean,
        onByteCount: (Int) -> Unit
    ): Bitmap {
        val out = ByteArrayOutputStream()
        val explicit = resize == 0
        val tWidth = widthValue ?: width
        val tHeight = heightValue ?: height

        resizeBitmap(tWidth, tHeight, !explicit)
            .rotate(rotation)
            .flip(isFlipped)
            .compress(
                if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 2) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                quality.toInt(), out
            )
        val b = out.toByteArray()
        onByteCount(b.size)
        val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(b))

        out.flush()
        out.close()

        return decoded

    }

    val tags = listOf(
        ExifInterface.TAG_BITS_PER_SAMPLE,
        ExifInterface.TAG_COMPRESSION,
        ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION,
        ExifInterface.TAG_SAMPLES_PER_PIXEL,
        ExifInterface.TAG_PLANAR_CONFIGURATION,
        ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING,
        ExifInterface.TAG_Y_CB_CR_POSITIONING,
        ExifInterface.TAG_X_RESOLUTION,
        ExifInterface.TAG_Y_RESOLUTION,
        ExifInterface.TAG_RESOLUTION_UNIT,
        ExifInterface.TAG_STRIP_OFFSETS,
        ExifInterface.TAG_ROWS_PER_STRIP,
        ExifInterface.TAG_STRIP_BYTE_COUNTS,
        ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT,
        ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH,
        ExifInterface.TAG_TRANSFER_FUNCTION,
        ExifInterface.TAG_WHITE_POINT,
        ExifInterface.TAG_PRIMARY_CHROMATICITIES,
        ExifInterface.TAG_Y_CB_CR_COEFFICIENTS,
        ExifInterface.TAG_REFERENCE_BLACK_WHITE,
        ExifInterface.TAG_DATETIME,
        ExifInterface.TAG_IMAGE_DESCRIPTION,
        ExifInterface.TAG_MAKE,
        ExifInterface.TAG_MODEL,
        ExifInterface.TAG_SOFTWARE,
        ExifInterface.TAG_ARTIST,
        ExifInterface.TAG_COPYRIGHT,
        ExifInterface.TAG_EXIF_VERSION,
        ExifInterface.TAG_FLASHPIX_VERSION,
        ExifInterface.TAG_COLOR_SPACE,
        ExifInterface.TAG_GAMMA,
        ExifInterface.TAG_PIXEL_X_DIMENSION,
        ExifInterface.TAG_PIXEL_Y_DIMENSION,
        ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL,
        ExifInterface.TAG_MAKER_NOTE,
        ExifInterface.TAG_USER_COMMENT,
        ExifInterface.TAG_RELATED_SOUND_FILE,
        ExifInterface.TAG_DATETIME_ORIGINAL,
        ExifInterface.TAG_DATETIME_DIGITIZED,
        ExifInterface.TAG_OFFSET_TIME,
        ExifInterface.TAG_OFFSET_TIME_ORIGINAL,
        ExifInterface.TAG_OFFSET_TIME_DIGITIZED,
        ExifInterface.TAG_SUBSEC_TIME,
        ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
        ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
        ExifInterface.TAG_EXPOSURE_TIME,
        ExifInterface.TAG_F_NUMBER,
        ExifInterface.TAG_EXPOSURE_PROGRAM,
        ExifInterface.TAG_SPECTRAL_SENSITIVITY,
        ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
        ExifInterface.TAG_OECF,
        ExifInterface.TAG_SENSITIVITY_TYPE,
        ExifInterface.TAG_STANDARD_OUTPUT_SENSITIVITY,
        ExifInterface.TAG_RECOMMENDED_EXPOSURE_INDEX,
        ExifInterface.TAG_ISO_SPEED,
        ExifInterface.TAG_ISO_SPEED_LATITUDE_YYY,
        ExifInterface.TAG_ISO_SPEED_LATITUDE_ZZZ,
        ExifInterface.TAG_SHUTTER_SPEED_VALUE,
        ExifInterface.TAG_APERTURE_VALUE,
        ExifInterface.TAG_BRIGHTNESS_VALUE,
        ExifInterface.TAG_EXPOSURE_BIAS_VALUE,
        ExifInterface.TAG_MAX_APERTURE_VALUE,
        ExifInterface.TAG_SUBJECT_DISTANCE,
        ExifInterface.TAG_METERING_MODE,
        ExifInterface.TAG_FLASH,
        ExifInterface.TAG_SUBJECT_AREA,
        ExifInterface.TAG_FOCAL_LENGTH,
        ExifInterface.TAG_FLASH_ENERGY,
        ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE,
        ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION,
        ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION,
        ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT,
        ExifInterface.TAG_SUBJECT_LOCATION,
        ExifInterface.TAG_EXPOSURE_INDEX,
        ExifInterface.TAG_SENSING_METHOD,
        ExifInterface.TAG_FILE_SOURCE,
        ExifInterface.TAG_CFA_PATTERN,
        ExifInterface.TAG_CUSTOM_RENDERED,
        ExifInterface.TAG_EXPOSURE_MODE,
        ExifInterface.TAG_WHITE_BALANCE,
        ExifInterface.TAG_DIGITAL_ZOOM_RATIO,
        ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM,
        ExifInterface.TAG_SCENE_CAPTURE_TYPE,
        ExifInterface.TAG_GAIN_CONTROL,
        ExifInterface.TAG_CONTRAST,
        ExifInterface.TAG_SATURATION,
        ExifInterface.TAG_SHARPNESS,
        ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION,
        ExifInterface.TAG_SUBJECT_DISTANCE_RANGE,
        ExifInterface.TAG_IMAGE_UNIQUE_ID,
        ExifInterface.TAG_CAMERA_OWNER_NAME,
        ExifInterface.TAG_BODY_SERIAL_NUMBER,
        ExifInterface.TAG_LENS_SPECIFICATION,
        ExifInterface.TAG_LENS_MAKE,
        ExifInterface.TAG_LENS_MODEL,
        ExifInterface.TAG_LENS_SERIAL_NUMBER,
        ExifInterface.TAG_GPS_VERSION_ID,
        ExifInterface.TAG_GPS_LATITUDE_REF,
        ExifInterface.TAG_GPS_LATITUDE,
        ExifInterface.TAG_GPS_LONGITUDE_REF,
        ExifInterface.TAG_GPS_LONGITUDE,
        ExifInterface.TAG_GPS_ALTITUDE_REF,
        ExifInterface.TAG_GPS_ALTITUDE,
        ExifInterface.TAG_GPS_TIMESTAMP,
        ExifInterface.TAG_GPS_SATELLITES,
        ExifInterface.TAG_GPS_STATUS,
        ExifInterface.TAG_GPS_MEASURE_MODE,
        ExifInterface.TAG_GPS_DOP,
        ExifInterface.TAG_GPS_SPEED_REF,
        ExifInterface.TAG_GPS_SPEED,
        ExifInterface.TAG_GPS_TRACK_REF,
        ExifInterface.TAG_GPS_TRACK,
        ExifInterface.TAG_GPS_IMG_DIRECTION_REF,
        ExifInterface.TAG_GPS_IMG_DIRECTION,
        ExifInterface.TAG_GPS_MAP_DATUM,
        ExifInterface.TAG_GPS_DEST_LATITUDE_REF,
        ExifInterface.TAG_GPS_DEST_LATITUDE,
        ExifInterface.TAG_GPS_DEST_LONGITUDE_REF,
        ExifInterface.TAG_GPS_DEST_LONGITUDE,
        ExifInterface.TAG_GPS_DEST_BEARING_REF,
        ExifInterface.TAG_GPS_DEST_BEARING,
        ExifInterface.TAG_GPS_DEST_DISTANCE_REF,
        ExifInterface.TAG_GPS_DEST_DISTANCE,
        ExifInterface.TAG_GPS_PROCESSING_METHOD,
        ExifInterface.TAG_GPS_AREA_INFORMATION,
        ExifInterface.TAG_GPS_DATESTAMP,
        ExifInterface.TAG_GPS_DIFFERENTIAL,
        ExifInterface.TAG_GPS_H_POSITIONING_ERROR,
        ExifInterface.TAG_INTEROPERABILITY_INDEX,
        ExifInterface.TAG_DNG_VERSION,
        ExifInterface.TAG_DEFAULT_CROP_SIZE,
        ExifInterface.TAG_ORF_THUMBNAIL_IMAGE,
        ExifInterface.TAG_ORF_PREVIEW_IMAGE_START,
        ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH,
        ExifInterface.TAG_ORF_ASPECT_FRAME,
        ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER,
        ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER,
        ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER,
        ExifInterface.TAG_RW2_SENSOR_TOP_BORDER,
        ExifInterface.TAG_RW2_ISO
    )

    infix fun ExifInterface.copyTo(newExif: ExifInterface) {
        tags.forEach { attr ->
            getAttribute(attr)?.let { newExif.setAttribute(attr, it) }
        }
        newExif.saveAttributes()
    }

    fun ExifInterface.toMap(): Map<String, String> {
        val hashMap = HashMap<String, String>()
        tags.forEach { tag ->
            getAttribute(tag)?.let { hashMap[tag] = it }
        }
        return hashMap
    }

}