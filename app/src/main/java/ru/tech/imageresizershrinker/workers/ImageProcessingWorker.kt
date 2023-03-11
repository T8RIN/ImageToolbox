package ru.tech.imageresizershrinker.workers

//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Build
//import android.os.ParcelFileDescriptor
//import androidx.core.app.NotificationCompat
//import androidx.core.content.ContextCompat.getSystemService
//import androidx.exifinterface.media.ExifInterface
//import androidx.work.CoroutineWorker
//import androidx.work.ForegroundInfo
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import ru.tech.imageresizershrinker.R
//import ru.tech.imageresizershrinker.resize_screen.components.BitmapInfo
//import ru.tech.imageresizershrinker.resize_screen.components.compressFormat
//import ru.tech.imageresizershrinker.resize_screen.components.extension
//import ru.tech.imageresizershrinker.utils.BitmapUtils.copyTo
//import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
//import ru.tech.imageresizershrinker.utils.BitmapUtils.flip
//import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
//import ru.tech.imageresizershrinker.utils.BitmapUtils.rotate
//import ru.tech.imageresizershrinker.utils.ContextUtils.isExternalStorageWritable
//import ru.tech.imageresizershrinker.utils.SavingFolder
//import ru.tech.imageresizershrinker.utils.getSavingFolder
//import ru.tech.imageresizershrinker.workers.WorkerUtils.getParcelable
//import ru.tech.imageresizershrinker.workers.WorkerUtils.getParcelableList
//import java.text.SimpleDateFormat
//import kotlin.random.Random

//class ImageProcessingWorker(
//    private val context: Context,
//    workerParameters: WorkerParameters
//) : CoroutineWorker(context, workerParameters) {
//
//    init {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "image_channel",
//                "image",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//
//            val notificationManager = context.getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    override suspend fun doWork(): Result {
//        setForeground(
//            ForegroundInfo(
//                Random.nextInt(),
//                NotificationCompat.Builder(context, "image_channel")
//                    .setSmallIcon(R.drawable.ic_launcher_monochrome)
//                    .setContentText()
//                    .setContentTitle(context.getString(R.string.processing))
//                    .build()
//            )
//        )
//
//        val uris = inputData.getParcelableList<Uri>("uris")
//        val bitmapInfo = inputData.getParcelable<BitmapInfo>("bitmapInfo")
//        val keepExif = inputData.getBoolean("keepExif", false)
//
//        val isExternalStorageWritable = context.isExternalStorageWritable()
//        val getFileDescriptor: (Uri?) -> ParcelFileDescriptor? = { uri ->
//            uri?.let { context.contentResolver.openFileDescriptor(uri, "rw", null) }
//        }
//        val getBitmap: (Uri) -> Pair<Bitmap?, ExifInterface?> = { uri ->
//            context.decodeBitmapFromUri(uri)
//        }
//
//        val getSavingFolder: (String, String) -> SavingFolder =
//            { name, ext ->
//                context.getSavingFolder(
//                    treeUri = inputData.getParcelable("treeUri"),
//                    filename = name,
//                    extension = ext
//                )
//            }
//
//        bitmapInfo?.apply {
//            if (!isExternalStorageWritable) {
//                return Result.failure()
//            } else {
//                _done.value = 0
//                uris.forEach { uri ->
//                    runCatching {
//                        getBitmap(uri)
//                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, exif) ->
//                        val ext = mime.extension
//
//                        val tWidth = width.toIntOrNull() ?: bitmap!!.width
//                        val tHeight = height.toIntOrNull() ?: bitmap!!.height
//
//                        val timeStamp: String =
//                            SimpleDateFormat(
//                                "yyyyMMdd_HHmmss",
//                                Locale.getDefault()
//                            ).format(Date())
//                        val name =
//                            "ResizedImage$timeStamp-${Date().hashCode()}.$ext"
//                        val localBitmap = bitmap!!.resizeBitmap(tWidth, tHeight, resizeType)
//                            .rotate(rotation)
//                            .flip(isFlipped)
//                        val savingFolder = getSavingFolder(name, ext)
//
//                        val fos = savingFolder.outputStream
//
//                        localBitmap.compress(
//                            mime.extension.compressFormat,
//                            quality.toInt(),
//                            fos
//                        )
//
//                        fos!!.flush()
//                        fos.close()
//
//                        if (keepExif) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                val fd = getFileDescriptor(savingFolder.fileUri)
//                                fd?.fileDescriptor?.let {
//                                    val ex = ExifInterface(it)
//                                    exif?.copyTo(ex)
//                                    ex.saveAttributes()
//                                }
//                                fd?.close()
//                            } else {
//                                val image = savingFolder.file!!
//                                val ex = ExifInterface(image)
//                                exif?.copyTo(ex)
//                                ex.saveAttributes()
//                            }
//                        }
//
//                    }
//                    _done.value += 1
//                }
//            }
//        }
//        return Result.success()
//    }

//    private fun createInputDataForUris(): Data {
//        val builder = Data.Builder()
//        _uris.value?.let { uris ->
//            builder.putParcelableList("uris", uris)
//            builder.putParcelable("bitmapInfo", bitmapInfo)
//            builder.putBoolean("keepExif", keepExif)
//            _saveFolderUri.value?.let { builder.putParcelable("treeUri", it) }
//        }
//        return builder.build()
//    }

//workManager.beginUniqueWork(
//            "image",
//            ExistingWorkPolicy.REPLACE,
//            OneTimeWorkRequestBuilder<ImageProcessingWorker>()
//                .setInputData(createInputDataForUris())
//                .build()
//        )


//}