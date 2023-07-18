package ru.tech.imageresizershrinker.data.image

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size
import coil.util.DebugLogger
import com.github.awxkee.avifcoil.HeifDecoder
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.ResizeType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Suppress("PrivatePropertyName")
class AndroidImageManager @Inject constructor(
    private val context: Context
) : ImageManager<Bitmap, ExifInterface> {

    private val heifCoder = HeifCoder()

    private fun loader(): ImageLoader {
        return context.imageLoader.newBuilder().components {
            if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
            add(SvgDecoder.Factory())
            if (Build.VERSION.SDK_INT >= 24) add(HeifDecoder.Factory())
        }.allowHardware(false).logger(DebugLogger()).build()
    }

    override suspend fun transform(
        image: Bitmap,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): Bitmap? = withContext(Dispatchers.IO) {
        val request = ImageRequest
            .Builder(context)
            .data(image)
            .transformations(
                transformations.map { t ->
                    object : coil.transform.Transformation {
                        override val cacheKey: String
                            get() = t.cacheKey

                        override suspend fun transform(
                            input: Bitmap,
                            size: Size
                        ): Bitmap = t.transform(input, size)
                    }
                }
            )
            .apply {
                if (originalSize) size(Size.ORIGINAL)
            }
            .build()

        return@withContext loader().execute(request).drawable?.toBitmap()
    }

    override suspend fun getImage(
        uri: String,
        originalSize: Boolean
    ): ImageData<Bitmap, ExifInterface>? = withContext(Dispatchers.IO) {
        return@withContext kotlin.runCatching {
            loader().execute(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .apply {
                        if (originalSize) size(Size.ORIGINAL)
                    }
                    .build()
            ).drawable?.toBitmap()
        }.getOrNull()?.let { bitmap ->
            val fd = context.contentResolver.openFileDescriptor(uri.toUri(), "r")
            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
            fd?.close()
            ImageData.create(
                image = bitmap,
                imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    imageFormat = ImageFormat[getExtension(uri)]
                ),
                metadata = exif
            )
        }
    }

    override fun getImageAsync(
        uri: String,
        originalSize: Boolean,
        onGetImage: (ImageData<Bitmap, ExifInterface>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val bmp = kotlin.runCatching {
            loader().enqueue(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .apply {
                        if (originalSize) size(Size.ORIGINAL)
                    }
                    .target { drawable ->
                        drawable.toBitmap()?.let { it ->
                            val fd = context.contentResolver.openFileDescriptor(uri.toUri(), "r")
                            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
                            fd?.close()
                            ImageData.create(
                                image = it,
                                imageInfo = ImageInfo(
                                    width = it.width,
                                    height = it.height,
                                    imageFormat = ImageFormat[getExtension(uri)]
                                ),
                                metadata = exif
                            )
                        }?.let(onGetImage)
                    }.build()
            )
        }
        bmp.exceptionOrNull()?.let(onError)
    }

    override suspend fun resize(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: ResizeType
    ): Bitmap? = withContext(Dispatchers.IO) {

        val widthInternal = width.takeIf { it > 0 } ?: image.width
        val heightInternal = height.takeIf { it > 0 } ?: image.height

        val max = max(widthInternal, heightInternal)

        return@withContext when (resizeType) {
            ResizeType.Explicit -> {
                Bitmap.createScaledBitmap(
                    image,
                    widthInternal,
                    heightInternal,
                    false
                )
            }

            ResizeType.Flexible -> {
                kotlin.runCatching {
                    if (image.height >= image.width) {
                        val aspectRatio = image.width.toDouble() / image.height.toDouble()
                        val targetWidth = (max * aspectRatio).toInt()
                        Bitmap.createScaledBitmap(image, targetWidth, max, false)
                    } else {
                        val aspectRatio = image.height.toDouble() / image.width.toDouble()
                        val targetHeight = (max * aspectRatio).toInt()
                        Bitmap.createScaledBitmap(image, max, targetHeight, false)
                    }
                }.getOrNull() ?: image
            }

            ResizeType.Ratio -> {
                resizeWithAspectRatio(image, widthInternal, heightInternal)
            }

            is ResizeType.Limits -> {
                resizeType.resizeWithLimits(image, widthInternal, heightInternal)
            }
        }
    }

    private suspend fun ResizeType.Limits.resizeWithLimits(
        image: Bitmap,
        width: Int,
        height: Int
    ): Bitmap? {
        if (image.height > height || image.width > width) {
            if (image.aspectRatio > width / height.toFloat()) {
                return resize(
                    image = image,
                    width = width,
                    height = width,
                    resizeType = ResizeType.Flexible
                )
            } else if (image.aspectRatio < width / height.toFloat()) {
                return resize(
                    image = image,
                    width = height,
                    height = height,
                    resizeType = ResizeType.Flexible
                )
            } else {
                return resize(
                    image = image,
                    width = width,
                    height = height,
                    resizeType = ResizeType.Flexible
                )
            }
        } else {
            return when (this) {
                ResizeType.Limits.Copy -> image

                ResizeType.Limits.Force -> resize(
                    image = image,
                    width = width,
                    height = height,
                    resizeType = ResizeType.Flexible
                )

                ResizeType.Limits.Skip -> null
            }
        }
    }

    private val Bitmap.aspectRatio: Float get() = width / height.toFloat()


    override fun applyPresetBy(image: Bitmap?, preset: Int, currentInfo: ImageInfo): ImageInfo {
        if (image == null) return currentInfo


        val rotated = abs(currentInfo.rotationDegrees) % 180 != 0f
        fun Bitmap.width() = if (rotated) height else width
        fun Bitmap.height() = if (rotated) width else height
        fun Int.calc(cnt: Int): Int = (this * (cnt / 100f)).toInt()

        return when (preset) {
            in 500 downTo 70 -> {
                currentInfo.copy(
                    quality = preset.toFloat(),
                    width = image.width().calc(preset),
                    height = image.height().calc(preset),
                )
            }

            in 69 downTo 10 -> currentInfo.run {
                copy(
                    width = image.width().calc(preset + 15),
                    height = image.height().calc(preset + 15),
                    quality = preset.toFloat()
                )
            }

            else -> currentInfo
        }
    }

    override suspend fun getSampledImage(
        uri: String,
        reqWidth: Int,
        reqHeight: Int
    ): ImageData<Bitmap, ExifInterface>? = withContext(Dispatchers.IO) {
        return@withContext loader().execute(
            ImageRequest
                .Builder(context)
                .size(reqWidth, reqHeight)
                .data(uri)
                .build()
        ).drawable?.toBitmap()?.let { bitmap ->
            val fd = context.contentResolver.openFileDescriptor(uri.toUri(), "r")
            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
            fd?.close()
            ImageData.create(
                image = bitmap,
                imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    imageFormat = ImageFormat[getExtension(uri)]
                ),
                metadata = exif
            )
        }
    }

    override suspend fun shareFile(
        byteArray: ByteArray,
        filename: String,
        onComplete: () -> Unit
    ) = withContext(Dispatchers.IO) {
        val imagesFolder = File(context.cacheDir, "images")
        val uri = kotlin.runCatching {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, filename)
            FileOutputStream(file).use {
                it.write(byteArray)
            }
            FileProvider.getUriForFile(
                context,
                context.getString(R.string.file_provider),
                file
            )
        }.getOrNull()
        uri?.let {
            shareUri(
                uri = it,
                type = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(
                        getExtension(uri.toString())
                    ) ?: "*/*"
            )
        }
        onComplete()
    }

    override suspend fun shareImages(
        uris: List<String>,
        imageLoader: suspend (String) -> ImageData<Bitmap, ExifInterface>?,
        onProgressChange: (Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        var cnt = 0
        val uriList: MutableList<Uri> = mutableListOf()
        uris.forEachIndexed { index, uri ->
            imageLoader(uri)?.let { (image, imageInfo) ->
                cacheImage(
                    image = image,
                    imageInfo = imageInfo,
                    name = index.toString()
                )?.let { uri ->
                    cnt += 1
                    uriList.add(uri.toUri())
                }
            }
            onProgressChange(cnt)
        }
        onProgressChange(-1)
        shareImageUris(uriList)
    }

    override suspend fun cacheImage(
        image: Bitmap,
        imageInfo: ImageInfo,
        name: String
    ): String? = withContext(Dispatchers.IO) {
        val imagesFolder = File(context.cacheDir, "images")
        return@withContext kotlin.runCatching {
            imagesFolder.mkdirs()
            val ext = imageInfo.imageFormat.extension
            val file = File(imagesFolder, "$name.$ext")
            FileOutputStream(file).use {
                it.write(compress(ImageData.create(image, imageInfo)))
            }
            FileProvider.getUriForFile(context, context.getString(R.string.file_provider), file)
        }.getOrNull()?.toString()
    }

    override suspend fun shareImage(
        imageData: ImageData<Bitmap, ExifInterface>,
        onComplete: () -> Unit,
        name: String
    ) = withContext(Dispatchers.IO) {
        val imagesFolder = File(context.cacheDir, "images")
        val uri = kotlin.runCatching {
            imagesFolder.mkdirs()
            val ext = imageData.imageInfo.imageFormat.extension
            val file = File(imagesFolder, "$name.$ext")
            FileOutputStream(file).use {
                it.write(compress(imageData))
            }
            FileProvider.getUriForFile(
                context,
                context.getString(R.string.file_provider),
                file
            )
        }
        uri.getOrNull()?.let { shareUri(it) }
        onComplete()
    }

    override fun overlayImage(image: Bitmap, overlay: Bitmap): Bitmap {
        val finalBitmap = Bitmap.createBitmap(image.width, image.height, image.config)
        val canvas = Canvas(finalBitmap)
        canvas.drawBitmap(image, Matrix(), null)
        canvas.drawBitmap(overlay, 0f, 0f, null)
        return finalBitmap
    }

    override suspend fun getImageWithTransformations(
        uri: String,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): ImageData<Bitmap, ExifInterface>? = withContext(Dispatchers.IO) {
        val request = ImageRequest
            .Builder(context)
            .data(uri)
            .transformations(
                transformations.map { t ->
                    object : coil.transform.Transformation {
                        override val cacheKey: String
                            get() = t.cacheKey

                        override suspend fun transform(
                            input: Bitmap,
                            size: Size
                        ): Bitmap = t.transform(input, size)
                    }
                }
            )
            .apply {
                if (originalSize) size(Size.ORIGINAL)
            }
            .build()
        return@withContext loader().execute(request).drawable?.toBitmap()?.let { bitmap ->
            val fd = context.contentResolver.openFileDescriptor(uri.toUri(), "r")
            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
            fd?.close()
            ImageData.create(
                image = bitmap,
                imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    imageFormat = ImageFormat[getExtension(uri)]
                ),
                metadata = exif
            )
        }
    }

    override suspend fun scaleByMaxBytes(
        image: Bitmap,
        imageFormat: ImageFormat,
        maxBytes: Long
    ): ImageData<Bitmap, ExifInterface>? = withContext(Dispatchers.IO) {
        val maxBytes1 =
            maxBytes - maxBytes
                .times(0.04f)
                .roundToInt()
                .coerceIn(
                    minimumValue = 512,
                    maximumValue = 4096
                )
        return@withContext kotlin.runCatching {
            if (image.size() > maxBytes1) {
                var streamLength = maxBytes1
                var compressQuality = 100
                val bmpStream = ByteArrayOutputStream()
                var newSize = image.width to image.height

                while (streamLength >= maxBytes1) {
                    compressQuality -= 1

                    if (compressQuality < 20) break

                    bmpStream.use {
                        it.flush()
                        it.reset()
                    }
                    bmpStream.write(
                        compress(
                            ImageData.create(
                                image = image,
                                imageInfo = ImageInfo(
                                    quality = compressQuality.toFloat(),
                                    imageFormat = imageFormat
                                )
                            )
                        )
                    )
                    streamLength = (bmpStream.toByteArray().size).toLong()
                }
                if (compressQuality < 20) {
                    compressQuality = 20
                    while (streamLength >= maxBytes1) {

                        bmpStream.use {
                            it.flush()
                            it.reset()
                        }
                        val temp = resize(
                            image = image,
                            width = (newSize.first * 0.98).toInt(),
                            height = (newSize.second * 0.98).toInt(),
                            resizeType = ResizeType.Explicit
                        )
                        bmpStream.write(
                            temp?.let {
                                compress(
                                    ImageData.create(
                                        image = image,
                                        imageInfo = ImageInfo(
                                            quality = compressQuality.toFloat(),
                                            imageFormat = imageFormat
                                        )
                                    )
                                )
                            }
                        )
                        newSize = (newSize.first * 0.98).toInt() to (newSize.second * 0.98).toInt()
                        streamLength = (bmpStream.toByteArray().size).toLong()
                    }
                }
                BitmapFactory.decodeStream(ByteArrayInputStream(bmpStream.toByteArray())) to compressQuality
            } else null
        }.getOrNull()?.let {
            ImageData.create(
                it.first,
                imageInfo = ImageInfo(
                    width = it.first.width,
                    height = it.first.height,
                    quality = it.second.toFloat()
                )
            )
        }
    }

    override fun canShow(image: Bitmap): Boolean {
        return image.size() < 4096 * 4096 * 5
    }

    override suspend fun scaleUntilCanShow(image: Bitmap?): Bitmap? = withContext(Dispatchers.IO) {
        if (image == null) return@withContext null

        var bmp = if (!canShow(image)) {
            resize(
                image = image,
                height = (image.height * 0.95f).toInt(),
                width = (image.width * 0.95f).toInt(),
                resizeType = ResizeType.Flexible
            )
        } else image

        if (bmp == null) return@withContext null

        while (!canShow(bmp!!)) {
            bmp = resize(
                image = bmp,
                height = (bmp.height * 0.95f).toInt(),
                width = (bmp.width * 0.95f).toInt(),
                resizeType = ResizeType.Flexible
            )
        }
        return@withContext bmp
    }

    override suspend fun calculateImageSize(imageData: ImageData<Bitmap, ExifInterface>): Long {
        return compress(imageData).size.toLong()
    }

    @Suppress("DEPRECATION")
    override suspend fun compress(imageData: ImageData<Bitmap, ExifInterface>): ByteArray =
        withContext(Dispatchers.IO) {
            val currentImage = resize(
                image = rotate(
                    image = imageData.image,
                    degrees = imageData.imageInfo.rotationDegrees
                ),
                width = imageData.imageInfo.width,
                height = imageData.imageInfo.height,
                resizeType = imageData.imageInfo.resizeType
            )?.let {
                flip(
                    image = it,
                    isFlipped = imageData.imageInfo.isFlipped
                )
            } ?: return@withContext ByteArray(0)

            return@withContext when (imageData.imageInfo.imageFormat) {
                ImageFormat.Bmp -> compressToBMP(currentImage)
                ImageFormat.Jpeg -> {
                    val out = ByteArrayOutputStream()
                    currentImage.compress(
                        Bitmap.CompressFormat.JPEG,
                        imageData.imageInfo.quality.toInt().coerceIn(0, 100),
                        out
                    )
                    out.toByteArray()
                }

                ImageFormat.Jpg -> {
                    val out = ByteArrayOutputStream()
                    currentImage.compress(
                        Bitmap.CompressFormat.JPEG,
                        imageData.imageInfo.quality.toInt().coerceIn(0, 100),
                        out
                    )
                    out.toByteArray()
                }

                ImageFormat.Png -> {
                    val out = ByteArrayOutputStream()
                    currentImage.compress(
                        Bitmap.CompressFormat.PNG,
                        imageData.imageInfo.quality.toInt().coerceIn(0, 100),
                        out
                    )
                    out.toByteArray()
                }

                ImageFormat.Webp.Lossless -> {
                    val out = ByteArrayOutputStream()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        currentImage.compress(
                            Bitmap.CompressFormat.WEBP_LOSSLESS,
                            imageData.imageInfo.quality.toInt().coerceIn(0, 100),
                            out
                        )
                    } else currentImage.compress(
                        Bitmap.CompressFormat.WEBP,
                        imageData.imageInfo.quality.toInt().coerceIn(0, 100),
                        out
                    )
                    out.toByteArray()
                }

                ImageFormat.Webp.Lossy -> {
                    val out = ByteArrayOutputStream()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        currentImage.compress(
                            Bitmap.CompressFormat.WEBP_LOSSY,
                            imageData.imageInfo.quality.toInt().coerceIn(0, 100),
                            out
                        )
                    } else currentImage.compress(
                        Bitmap.CompressFormat.WEBP,
                        imageData.imageInfo.quality.toInt().coerceIn(0, 100),
                        out
                    )
                    out.toByteArray()
                }

                ImageFormat.Avif -> {
                    heifCoder.encodeAvif(
                        currentImage,
                        imageData.imageInfo.quality.toInt().coerceIn(0, 100)
                    )
                }

                ImageFormat.Heic -> {
                    heifCoder.encodeHeic(
                        currentImage,
                        imageData.imageInfo.quality.toInt().coerceIn(0, 100)
                    )
                }

                ImageFormat.Heif -> {
                    heifCoder.encodeHeic(
                        currentImage,
                        imageData.imageInfo.quality.toInt().coerceIn(0, 100)
                    )
                }
            }
    }

    override fun flip(image: Bitmap, isFlipped: Boolean): Bitmap {
        return if (isFlipped) {
            val matrix = Matrix().apply { postScale(-1f, 1f, image.width / 2f, image.width / 2f) }
            Bitmap.createBitmap(image, 0, 0, image.width, image.width, matrix, true)
        } else image
    }

    override fun rotate(image: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
    }

    override suspend fun createPreview(
        image: Bitmap,
        imageInfo: ImageInfo,
        onGetByteCount: (Int) -> Unit
    ): Bitmap = withContext(Dispatchers.IO) {
        if (imageInfo.height == 0 || imageInfo.width == 0) return@withContext image
        val out = ByteArrayOutputStream()
        var width = imageInfo.width
        var height = imageInfo.height

        while (height * width * 4L >= 4096 * 4096 * 5L) {
            height = (height * 0.9f).roundToInt()
            width = (width * 0.9f).roundToInt()
        }

        out.write(compress(ImageData.create(image, imageInfo.copy(width = width, height = height))))
        val b = out.toByteArray()
        onGetByteCount(b.size)

        val bitmap = loader().execute(
            ImageRequest.Builder(context).data(b).build()
        ).drawable?.toBitmap()

        out.flush()
        out.close()

        return@withContext scaleUntilCanShow(bitmap) ?: image
    }


    private val BMP_WIDTH_OF_TIMES = 4
    private val BYTE_PER_PIXEL = 3

    private fun compressToBMP(image: Bitmap): ByteArray {

        //image size
        val width = image.width
        val height = image.height

        //image dummy data size
        //reason : the amount of bytes per image row must be a multiple of 4 (requirements of bmp format)
        var dummyBytesPerRow: ByteArray? = null
        var hasDummy = false
        val rowWidthInBytes =
            BYTE_PER_PIXEL * width //source image width * number of bytes to encode one pixel.
        if (rowWidthInBytes % BMP_WIDTH_OF_TIMES > 0) {
            hasDummy = true
            //the number of dummy bytes we need to add on each row
            dummyBytesPerRow =
                ByteArray(BMP_WIDTH_OF_TIMES - rowWidthInBytes % BMP_WIDTH_OF_TIMES)
            //just fill an array with the dummy bytes we need to append at the end of each row
            for (i in dummyBytesPerRow.indices) {
                dummyBytesPerRow[i] = 0xFF.toByte()
            }
        }

        //an array to receive the pixels from the source image
        val pixels = IntArray(width * height)

        //the number of bytes used in the file to store raw image data (excluding file headers)
        val imageSize = (rowWidthInBytes + if (hasDummy) dummyBytesPerRow!!.size else 0) * height
        //file headers size
        val imageDataOffset = 0x36

        //final size of the file
        val fileSize = imageSize + imageDataOffset

        //Android Bitmap Image Data
        image.getPixels(pixels, 0, width, 0, 0, width, height)

        //ByteArrayOutputStream baos = new ByteArrayOutputStream(fileSize);
        val buffer = ByteBuffer.allocate(fileSize)
        /**
         * BITMAP FILE HEADER Write Start
         */
        buffer.put(0x42.toByte())
        buffer.put(0x4D.toByte())

        //size
        buffer.put(writeInt(fileSize))

        //reserved
        buffer.put(writeShort(0.toShort()))
        buffer.put(writeShort(0.toShort()))

        //image data start offset
        buffer.put(writeInt(imageDataOffset))
        /** BITMAP FILE HEADER Write End  */

        //*******************************************
        /** BITMAP INFO HEADER Write Start  */
        //size
        buffer.put(writeInt(0x28))

        //width, height
        //if we add 3 dummy bytes per row : it means we add a pixel (and the image width is modified.
        buffer.put(writeInt(width + if (hasDummy) (if (dummyBytesPerRow!!.size == 3) 1 else 0) else 0))
        buffer.put(writeInt(height))

        //planes
        buffer.put(writeShort(1.toShort()))

        //bit count
        buffer.put(writeShort(24.toShort()))

        //bit compression
        buffer.put(writeInt(0))

        //image data size
        buffer.put(writeInt(imageSize))

        //horizontal resolution in pixels per meter
        buffer.put(writeInt(0))

        //vertical resolution in pixels per meter (unreliable)
        buffer.put(writeInt(0))
        buffer.put(writeInt(0))
        buffer.put(writeInt(0))
        /** BITMAP INFO HEADER Write End  */
        var row = height
        var startPosition = (row - 1) * width
        var endPosition = row * width
        while (row > 0) {
            for (i in startPosition until endPosition) {
                buffer.put((pixels[i] and 0x000000FF).toByte())
                buffer.put((pixels[i] and 0x0000FF00 shr 8).toByte())
                buffer.put((pixels[i] and 0x00FF0000 shr 16).toByte())
            }
            if (hasDummy) {
                if (dummyBytesPerRow != null) {
                    buffer.put(dummyBytesPerRow)
                }
            }
            row--
            endPosition = startPosition
            startPosition -= width
        }
        return buffer.array()
    }

    private fun writeInt(value: Int): ByteArray {
        val b = ByteArray(4)
        b[0] = (value and 0x000000FF).toByte()
        b[1] = (value and 0x0000FF00 shr 8).toByte()
        b[2] = (value and 0x00FF0000 shr 16).toByte()
        b[3] = (value and -0x1000000 shr 24).toByte()
        return b
    }

    private fun writeShort(value: Short): ByteArray {
        val b = ByteArray(2)
        b[0] = (value.toInt() and 0x00FF).toByte()
        b[1] = (value.toInt() and 0xFF00 shr 8).toByte()
        return b
    }

    private fun Drawable.toBitmap(): Bitmap? {
        val drawable = this
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
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
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getExtension(uri: String): String? {
        return if (ContentResolver.SCHEME_CONTENT == uri.toUri().scheme) {
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(
                    context.contentResolver.getType(uri.toUri())
                )
        } else {
            MimeTypeMap.getFileExtensionFromUrl(uri).lowercase(Locale.getDefault())
        }
    }

    private var lastModification: Pair<Int, Int> = 0 to 0

    private fun resizeWithAspectRatio(image: Bitmap, w: Int, h: Int): Bitmap {
        return if (w > 0 && h > 0) {
            val (originalWidth, originalHeight) = image.width to image.height
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

            Bitmap.createScaledBitmap(image, newWidth, newHeight, false)
        } else image
    }

    private fun Bitmap.size(): Int {
        return width * height * (if (config == Bitmap.Config.RGB_565) 2 else 4)
    }

    private fun shareUri(uri: Uri, type: String = "image/*") {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.type = type
        }
        val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    private fun shareImageUris(uris: List<Uri>) {
        val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "image/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

}