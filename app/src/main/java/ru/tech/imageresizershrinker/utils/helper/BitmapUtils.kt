package ru.tech.imageresizershrinker.utils.helper

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import androidx.exifinterface.media.ExifInterface
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt


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
        onGetBitmap: (Bitmap) -> Unit,
        onGetExif: (ExifInterface?) -> Unit,
        onGetMimeType: (Int) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val bmp = kotlin.runCatching {
            val fd = contentResolver.openFileDescriptor(uri, "r")
            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
            onGetExif(exif)
            val mime = contentResolver.getMimeType(uri) ?: ""
            val mimeInt = mime.mimeTypeInt
            onGetMimeType(mimeInt)
            fd?.close()
            val loader = imageLoader.newBuilder().components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
            }.allowHardware(false).build()
            loader.enqueue(
                ImageRequest
                    .Builder(this@decodeBitmapFromUri)
                    .data(uri).target { drawable ->
                        drawable.toBitmap()?.let { onGetBitmap(it) }
                    }.build()
            )
        }
        bmp.exceptionOrNull()?.let(onError)
    }

    fun Drawable.toBitmap(): Bitmap? {
        val drawable = this
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap? = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        if (bitmap == null) return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    suspend fun Context.getBitmapByUri(uri: Uri): Bitmap? {
        val fd = contentResolver.openFileDescriptor(uri, "r")
        fd?.close()

        return kotlin.runCatching {
            val loader = imageLoader.newBuilder().components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
            }.allowHardware(false).build()
            loader.execute(
                ImageRequest
                    .Builder(this@getBitmapByUri)
                    .data(uri).build()
            ).drawable?.toBitmap()
        }.getOrNull()

    }

    suspend fun Context.decodeBitmapFromUri(
        uri: Uri
    ): Pair<Bitmap?, ExifInterface?> {
        val fd = contentResolver.openFileDescriptor(uri, "r")
        val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
        fd?.close()

        return kotlin.runCatching {
            val loader = imageLoader.newBuilder().components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
            }.allowHardware(false).build()
            loader.execute(
                ImageRequest
                    .Builder(this@decodeBitmapFromUri)
                    .data(uri).build()
            ).drawable?.toBitmap()
        }.getOrNull() to exif
    }

    suspend fun Context.decodeBitmapFromUriWithMime(
        uri: Uri
    ): Triple<Bitmap?, ExifInterface?, Int> {
        val fd = contentResolver.openFileDescriptor(uri, "r")
        val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
        fd?.close()

        val mime = contentResolver.getMimeType(uri) ?: ""

        return Triple(kotlin.runCatching {
            val loader = imageLoader.newBuilder().components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
            }.allowHardware(false).build()
            loader.execute(
                ImageRequest
                    .Builder(this@decodeBitmapFromUriWithMime)
                    .data(uri).build()
            ).drawable?.toBitmap()
        }.getOrNull(), exif, mime.mimeTypeInt)
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

    suspend fun Bitmap.previewBitmap(
        quality: Float,
        widthValue: Int?,
        heightValue: Int?,
        mimeTypeInt: Int,
        resizeType: Int,
        rotationDegrees: Float,
        isFlipped: Boolean,
        onByteCount: (Int) -> Unit
    ): Bitmap = withContext(Dispatchers.IO) {
        if (heightValue == 0 || widthValue == 0) return@withContext this@previewBitmap
        val out = ByteArrayOutputStream()
        val tWidth = widthValue ?: width
        val tHeight = heightValue ?: height

        rotate(rotationDegrees)
            .resizeBitmap(tWidth, tHeight, resizeType)
            .flip(isFlipped)
            .compress(mimeTypeInt.extension.compressFormat, quality.toInt().coerceIn(0, 100), out)
        val b = out.toByteArray()
        onByteCount(b.size)

        val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(b))

        out.flush()
        out.close()

        return@withContext bitmap.scaleUntilCanShow() ?: this@previewBitmap
    }

    suspend fun Bitmap.scaleUntilCanShow(
        context: CoroutineContext = Dispatchers.IO
    ): Bitmap? = withContext(context) {
        val bitmap = this@scaleUntilCanShow
        var bmp: Bitmap?
        bmp = if (!bitmap.canShow()) {
            bitmap.resizeBitmap(
                height_ = (bitmap.height * 0.95f).toInt(),
                width_ = (bitmap.width * 0.95f).toInt(),
                resize = 1
            )
        } else bitmap

        while (bmp?.canShow() == false) {
            bmp = bmp.resizeBitmap(
                height_ = (bmp.height * 0.95f).toInt(),
                width_ = (bmp.width * 0.95f).toInt(),
                resize = 1
            )
        }
        return@withContext bmp
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

    fun Int.applyPresetBy(bitmap: Bitmap?, currentInfo: BitmapInfo): BitmapInfo {
        if (bitmap == null) return currentInfo


        val rotated = abs(currentInfo.rotationDegrees) % 180 != 0f
        fun Bitmap.width() = if (rotated) height else width
        fun Bitmap.height() = if (rotated) width else height
        fun Int.calc(cnt: Int): Int = (this * (cnt / 100f)).toInt()

        return when (val percent = this) {
            in 500 downTo 70 -> {
                currentInfo.copy(
                    quality = percent.toFloat(),
                    width = bitmap.width().calc(percent),
                    height = bitmap.height().calc(percent),
                )
            }

            in 69 downTo 10 -> currentInfo.run {
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

    fun Bitmap.canShow(): Boolean {
        return size() < 4096 * 4096 * 5
    }

    private fun Context.shareImage(
        image: Bitmap,
        bitmapInfo: BitmapInfo,
        onComplete: () -> Unit,
        name: String = "shared_image",
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val imagesFolder = File(cacheDir, "images")
                val uri = kotlin.runCatching {
                    imagesFolder.mkdirs()
                    val mime = bitmapInfo.mimeTypeInt
                    val ext = mime.extension
                    val file = File(imagesFolder, "$name.$ext")
                    val stream = FileOutputStream(file)
                    image.compress(ext.compressFormat, bitmapInfo.quality.toInt(), stream)
                    stream.flush()
                    stream.close()
                    FileProvider.getUriForFile(
                        this@shareImage,
                        getString(R.string.file_provider),
                        file
                    )
                }.getOrNull()
                uri?.let { shareImageUri(it) }
                onComplete()
            }
        }
    }

    fun Context.cacheImage(
        image: Bitmap,
        bitmapInfo: BitmapInfo,
        name: String = "shared_image"
    ): Uri? {
        val imagesFolder = File(cacheDir, "images")
        return kotlin.runCatching {
            imagesFolder.mkdirs()
            val mime = bitmapInfo.mimeTypeInt
            val ext = mime.extension
            val file = File(imagesFolder, "$name.$ext")
            val stream = FileOutputStream(file)
            image.compress(ext.compressFormat, bitmapInfo.quality.toInt(), stream)
            stream.flush()
            stream.close()
            FileProvider.getUriForFile(this, getString(R.string.file_provider), file)
        }.getOrNull()
    }

    private fun Context.shareImage(
        image: Bitmap,
        compressFormat: CompressFormat,
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val imagesFolder = File(cacheDir, "images")
                val uri = kotlin.runCatching {
                    imagesFolder.mkdirs()
                    val ext = compressFormat.extension
                    val file = File(imagesFolder, "shared_image.$ext")
                    val stream = FileOutputStream(file)
                    image.compress(compressFormat, 100, stream)
                    stream.flush()
                    stream.close()
                    FileProvider.getUriForFile(
                        this@shareImage,
                        getString(R.string.file_provider),
                        file
                    )
                }.getOrNull()
                uri?.let { shareImageUri(it) }
                onComplete()
            }
        }
    }

    fun Context.shareImageUri(uri: Uri) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        startActivity(shareIntent)
    }

    fun Context.shareImageUris(uris: List<Uri>) {
        val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        startActivity(shareIntent)
    }

    fun Context.shareBitmaps(
        uris: List<Uri>,
        scope: CoroutineScope,
        bitmapLoader: suspend (Uri) -> Pair<Bitmap, BitmapInfo>?,
        onProgressChange: (Int) -> Unit
    ) {
        scope.launch {
            withContext(Dispatchers.IO) {
                var cnt = 0
                val uriList: MutableList<Uri> = mutableListOf()
                uris.forEachIndexed { index, uri ->
                    bitmapLoader(uri)?.let { (bitmap, bitmapInfo) ->
                        cacheImage(
                            image = bitmap,
                            bitmapInfo = bitmapInfo,
                            name = index.toString()
                        )?.let { uri ->
                            cnt += 1
                            uriList.add(uri)
                        }
                    }
                    onProgressChange(cnt)
                }
                onProgressChange(-1)
                shareImageUris(uriList)
            }
        }
    }

    fun Context.shareBitmap(
        bitmap: Bitmap?,
        bitmapInfo: BitmapInfo,
        onComplete: () -> Unit
    ) = bitmap?.let {
        shareImage(it, bitmapInfo, onComplete)
    }

    fun Context.shareBitmap(
        bitmap: Bitmap?,
        compressFormat: CompressFormat,
        onComplete: () -> Unit
    ) = bitmap?.let {
        shareImage(it, compressFormat, onComplete)
    }

    suspend fun Context.decodeSampledBitmapFromUri(
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        val loader = imageLoader.newBuilder().components {
            if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
            add(SvgDecoder.Factory())
        }.allowHardware(false).build()
        return loader.execute(
            ImageRequest
                .Builder(this@decodeSampledBitmapFromUri)
                .size(reqWidth, reqHeight)
                .data(uri).build()
        ).drawable?.toBitmap()
    }

    fun Bitmap.scaleByMaxBytes(
        compressFormat: CompressFormat,
        maxBytes: Long
    ): Pair<Bitmap, Int>? {
        val maxBytes1 =
            maxBytes - maxBytes
                .times(0.04f)
                .roundToInt()
                .coerceIn(
                    minimumValue = 512,
                    maximumValue = 4096
                )
        try {
            if (this.size() > maxBytes1) {
                var streamLength = maxBytes1
                var compressQuality = 100
                val bmpStream = ByteArrayOutputStream()
                var newSize = width to height

                while (streamLength >= maxBytes1) {
                    compressQuality -= 1

                    if (compressQuality < 20) break

                    bmpStream.use {
                        it.flush()
                        it.reset()
                    }
                    compress(compressFormat, compressQuality, bmpStream)
                    streamLength = (bmpStream.toByteArray().size).toLong()
                }
                if (compressQuality < 20) {
                    compressQuality = 20
                    while (streamLength >= maxBytes1) {

                        bmpStream.use {
                            it.flush()
                            it.reset()
                        }
                        resizeBitmap(
                            (newSize.first * 0.98).toInt(),
                            (newSize.second * 0.98).toInt(),
                            0
                        ).compress(
                            compressFormat,
                            compressQuality,
                            bmpStream
                        )
                        newSize = (newSize.first * 0.98).toInt() to (newSize.second * 0.98).toInt()
                        streamLength = (bmpStream.toByteArray().size).toLong()
                    }
                }
                return BitmapFactory.decodeStream(ByteArrayInputStream(bmpStream.toByteArray())) to compressQuality
            }
        } catch (t: Throwable) {
            return null
        }
        return this to 100
    }

    fun Uri.fileSize(context: Context): Long? {
        context.contentResolver
            .query(this, null, null, null, null, null)
            .use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (!cursor.isNull(sizeIndex)) {
                        return cursor.getLong(sizeIndex)
                    }
                }
            }
        return null
    }

    fun String.restrict(`by`: Int = 24000): String {
        if (isEmpty()) return this

        return if ((this.toIntOrNull() ?: 0) > `by`) `by`.toString()
        else if (this.isDigitsOnly() && (this.toIntOrNull() ?: 0) == 0) ""
        else this.trim().filter {
            !listOf('-', '.', ',', ' ', "\n").contains(it)
        }
    }

}
