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
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.core.android.IntentUtils.parcelable
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.presentation.main_screen.MainActivity
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import javax.inject.Inject

@AndroidEntryPoint
class ScreenshotService : Service() {

    @Inject
    lateinit var imageManager: ImageManager<Bitmap, ExifInterface>

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val resultCode = intent?.getIntExtra("resultCode", RESULT_CANCELED) ?: RESULT_CANCELED
        val data = intent?.parcelable<Intent>("data")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "1",
                    "screenshot",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
            startForeground(
                1,
                Notification.Builder(applicationContext, "1")
                    .setSmallIcon(R.drawable.outline_colorize_24)
                    .build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        }
        startCapture(mediaProjectionManager.getMediaProjection(resultCode, data!!))

        return START_STICKY
    }

    private fun startCapture(mediaProjection: MediaProjection) {
        val displayMetrics = applicationContext.resources.displayMetrics
        val imageReader = ImageReader.newInstance(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            PixelFormat.RGBA_8888, 2
        )

        val virtualDisplay = mediaProjection.createVirtualDisplay(
            "screenshot",
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null, null
        )


        Handler(
            Looper.getMainLooper()
        ).postDelayed(
            {
                val image: Image? = imageReader.acquireLatestImage()

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


                    val uri: Uri? = runBlocking {
                        imageManager.cacheImage(
                            image = bitmap,
                            imageInfo = ImageInfo(
                                width = bitmap.width,
                                height = bitmap.height,
                                mimeType = MimeType.Png
                            ),
                            name = "screenshot"
                        )?.toUri()
                    }

                    applicationContext.startActivity(
                        Intent(applicationContext, MainActivity::class.java).apply {
                            putExtra("screen", Screen.PickColorFromImage::class.simpleName)
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            action = Intent.ACTION_SEND
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )

                    image.close()
                    imageReader.close()
                    virtualDisplay?.release()
                    mediaProjection.stop()
                }
            }, 1000
        )
    }

    override fun onBind(intent: Intent): IBinder = Binder()

}