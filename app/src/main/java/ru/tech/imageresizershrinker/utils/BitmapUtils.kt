package ru.tech.imageresizershrinker.utils

import android.content.*
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import ru.tech.imageresizershrinker.resize_screen.components.BitmapInfo
import java.io.*
import java.util.*
import kotlin.math.abs
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

    fun Bitmap.resizeBitmap(width_: Int, height_: Int, resize: Int): Bitmap {
        val max = max(width_, height_)
        return when (resize) {
            0 -> {
                Bitmap.createScaledBitmap(
                    this,
                    width_,
                    height_,
                    false
                )
            }
            1 -> {
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
            else -> {
                resizeWithAspectRatio(width_, height_) ?: this
            }
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
        val fd = contentResolver.openFileDescriptor(uri, "r")
        val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
        onGetExif(exif)
        val mime = contentResolver.getMimeType(uri) ?: ""
        val mimeInt = if ("png" in mime) 2 else if ("webp" in mime) 1 else 0
        onGetMimeType(mimeInt)
        fd?.close()

        val bmp = kotlin.runCatching {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
            BitmapFactory.decodeFileDescriptor(fileDescriptor, outPadding, options).also {
                parcelFileDescriptor?.close()
            }.rotate(exif?.rotationDegrees?.toFloat() ?: 0f)
        }.getOrNull()
        bmp?.let { onGetBitmap(it) }
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
        val tWidth = widthValue ?: width
        val tHeight = heightValue ?: height

        rotate(rotation)
            .resizeBitmap(tWidth, tHeight, resize)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun Context.getUriByName(fileName: String?): Uri? {
        applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media._ID),
            null, null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val index =
                    cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME).takeIf { it != -1 }
                        ?: 0
                val name = cursor.getString(index)
                if (name == fileName) {
                    val pIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        .takeIf { it != -1 } ?: 0
                    return ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(pIndex)
                    )
                }
            }
        }
        return null
    }

    fun Int.with(bitmap: Bitmap?, currentInfo: BitmapInfo): BitmapInfo {
        if (bitmap == null) return currentInfo


        val rotated = abs(currentInfo.rotation) % 180 != 0f
        fun Bitmap.width() = if (rotated) height else width
        fun Bitmap.height() = if (rotated) width else height
        fun Int.calc(cnt: Int): String = (this * (cnt / 100f)).toInt().toString()

        return when (val percent = this) {
            100 -> {
                currentInfo.copy(
                    quality = percent.toFloat(),
                    width = bitmap.width().toString(),
                    height = bitmap.height().toString()
                )
            }
            in 90 downTo 70 -> currentInfo.copy(
                width = bitmap.width().calc(percent),
                height = bitmap.height().calc(percent),
                quality = percent.toFloat()
            )
            in 60 downTo 30 -> currentInfo.run {
                copy(
                    width = bitmap.width().calc(percent + 15),
                    height = bitmap.height().calc(percent + 15),
                    quality = percent.toFloat()
                )
            }
            else -> currentInfo
        }
    }

    private var lastModification: Pair<Int, Int> = 0 to 0

    private fun Bitmap.resizeWithAspectRatio(w: Int, h: Int): Bitmap? {
        return if (w > 0 && h > 0) {
            val (originalWidth, originalHeight) = width to height
            var (newWidth, newHeight) = w to h

            fun updateByHeight() {
                val ratio = originalWidth.toFloat() / originalHeight.toFloat()
                newWidth = (newHeight * ratio).toInt()
            }

            fun updateByWidth() {
                val ratio = originalHeight.toFloat() / originalWidth.toFloat()
                newHeight = (newWidth * ratio).toInt()
            }

            if (originalHeight > originalWidth) {
                if (h != lastModification.second) updateByHeight()
                else updateByWidth()
            } else if (originalWidth > originalHeight) {
                if (w != lastModification.first) updateByWidth()
                else updateByHeight()
            } else {
                if (w != lastModification.first) newHeight = w
                else newWidth = h
            }

            lastModification = newWidth to newHeight

            Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
        } else this
    }

    fun Bitmap.size(): Int {
        return width * height * (if (config == Bitmap.Config.RGB_565) 2 else 4)
    }

    fun Bitmap.checkBitmapFitsInMemory(): Boolean {
        return size() < 4096 * 4096 * 5
    }

    private fun Context.saveImage(image: Bitmap, bitmapInfo: BitmapInfo): Uri? {
        val imagesFolder = File(cacheDir, "images")
        return kotlin.runCatching {
            imagesFolder.mkdirs()
            val mime = bitmapInfo.mime
            val ext = if (mime == 2) "png" else if (mime == 1) "webp" else "jpg"
            val file = File(imagesFolder, "shared_image.$ext")
            val stream = FileOutputStream(file)
            image.compress(
                if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 2) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                bitmapInfo.quality.toInt(), stream
            )
            stream.flush()
            stream.close()
            FileProvider.getUriForFile(this, "ru.tech.imageresizershrinker.fileprovider", file)
        }.getOrNull()
    }

    fun Context.shareImageUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/*"
        }
        startActivity(intent)
    }

    fun Context.shareBitmap(
        bitmap: Bitmap?,
        bitmapInfo: BitmapInfo
    ) = bitmap?.let {
        saveImage(it, bitmapInfo)?.let { uri -> shareImageUri(uri) }
    }

}