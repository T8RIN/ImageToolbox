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
        ExifInterface.TAG_DATETIME,
        ExifInterface.TAG_EXPOSURE_TIME,
        ExifInterface.TAG_FOCAL_LENGTH,
        ExifInterface.TAG_GPS_ALTITUDE,
        ExifInterface.TAG_GPS_LATITUDE,
        ExifInterface.TAG_GPS_LONGITUDE,
        ExifInterface.TAG_GPS_PROCESSING_METHOD,
        ExifInterface.TAG_MAKE,
        ExifInterface.TAG_MODEL,
        ExifInterface.TAG_EXPOSURE_TIME,
        ExifInterface.TAG_DATETIME_DIGITIZED
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