package ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen.components

import android.app.Activity.RESULT_CANCELED
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Parcelable
import android.widget.Toast
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.ImageManager
import ru.tech.imageresizershrinker.coredomain.model.ImageData
import ru.tech.imageresizershrinker.coredomain.model.ImageFormat
import ru.tech.imageresizershrinker.coredomain.model.ImageInfo
import ru.tech.imageresizershrinker.coredomain.saving.FileController
import ru.tech.imageresizershrinker.coredomain.saving.model.FileSaveTarget
import ru.tech.imageresizershrinker.presentation.main_screen.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ScreenshotService : Service() {

    @Inject
    lateinit var imageManager: ImageManager<Bitmap, ExifInterface>

    @Inject
    lateinit var fileController: FileController

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runCatching {
            val mediaProjectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

            val resultCode = intent?.getIntExtra("resultCode", RESULT_CANCELED) ?: RESULT_CANCELED
            val data = intent?.parcelable<Intent>("data")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        "1",
                        "screenshot",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        1,
                        Notification.Builder(applicationContext, "1")
                            .setSmallIcon(R.drawable.ic_launcher_monochrome)
                            .build(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                } else {
                    startForeground(
                        1,
                        Notification.Builder(applicationContext, "1")
                            .setSmallIcon(R.drawable.ic_launcher_monochrome)
                            .build()
                    )
                }
            }
            val callback = object : MediaProjection.Callback() {}

            val mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
            mediaProjection.registerCallback(callback, null)
            startCapture(
                mediaProjection = mediaProjection,
                intent = intent
            )
        }

        return START_REDELIVER_INTENT
    }

    private var imageReader: ImageReader? = null

    private fun startCapture(mediaProjection: MediaProjection, intent: Intent?) {
        Handler(
            Looper.getMainLooper()
        ).postDelayed(
            {
                val displayMetrics = applicationContext.resources.displayMetrics
                imageReader = ImageReader.newInstance(
                    displayMetrics.widthPixels,
                    displayMetrics.heightPixels,
                    PixelFormat.RGBA_8888, 2
                )
                imageReader?.use {
                    val virtualDisplay = mediaProjection.createVirtualDisplay(
                        "screenshot",
                        displayMetrics.widthPixels,
                        displayMetrics.heightPixels,
                        displayMetrics.densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        it.surface,
                        null, null
                    )
                    val image: Image? = it.acquireLatestImage()

                    image?.planes?.let { planes ->
                        val buffer = planes[0].buffer
                        val pixelStride = planes[0].pixelStride
                        val rowStride = planes[0].rowStride
                        val rowPadding = rowStride - pixelStride * displayMetrics.widthPixels

                        val bitmap = Bitmap.createBitmap(
                            displayMetrics.widthPixels + rowPadding / pixelStride,
                            displayMetrics.heightPixels, Bitmap.Config.ARGB_8888
                        )
                        bitmap.copyPixelsFromBuffer(buffer)
                        image.close()
                        virtualDisplay?.release()
                        mediaProjection.stop()

                        val uri: Uri? = runBlocking {
                            imageManager.cacheImage(
                                image = bitmap,
                                imageInfo = ImageInfo(
                                    width = bitmap.width,
                                    height = bitmap.height,
                                    imageFormat = ImageFormat.Jpg
                                ),
                                name = "screenshot"
                            )?.toUri()
                        }

                        if (intent?.getStringExtra("screen") != "shot") {
                            applicationContext.startActivity(
                                Intent(applicationContext, MainActivity::class.java).apply {
                                    putExtra("screen", intent?.getStringExtra("screen"))
                                    type = "image/jpg"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    action = Intent.ACTION_SEND
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            )
                        } else {
                            runBlocking {
                                val timeStamp = SimpleDateFormat(
                                    "yyyy-MM-dd_HH-mm-ss",
                                    Locale.getDefault()
                                ).format(Date())
                                fileController.save(
                                    FileSaveTarget(
                                        filename = "screenshot-$timeStamp.jpg",
                                        originalUri = "screenshot",
                                        imageFormat = ImageFormat["jpg"],
                                        data = imageManager.compress(
                                            ImageData(
                                                bitmap, ImageInfo(
                                                    width = bitmap.width,
                                                    height = bitmap.height,
                                                    imageFormat = ImageFormat.Jpg
                                                )
                                            )
                                        )
                                    ),
                                    true
                                )
                                Toast.makeText(
                                    this@ScreenshotService,
                                    this@ScreenshotService.getString(
                                        R.string.saved_to_without_filename,
                                        fileController.savingPath
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }, 1000
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        imageReader?.close()
        imageReader = null
    }

    override fun onBind(intent: Intent): IBinder = Binder()

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

}