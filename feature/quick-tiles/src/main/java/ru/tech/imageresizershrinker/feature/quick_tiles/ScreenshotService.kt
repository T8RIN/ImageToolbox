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

package ru.tech.imageresizershrinker.feature.quick_tiles

import android.app.Activity.RESULT_CANCELED
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.FileSaveTarget
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.AppActivityClass
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelable
import ru.tech.imageresizershrinker.core.ui.utils.helper.mainLooperDelayedAction
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ScreenshotService : Service() {

    @Inject
    lateinit var fileController: FileController

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
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

            mediaProjectionManager.getMediaProjection(resultCode, data!!).apply {
                registerCallback(
                    callback,
                    Handler(
                        Looper.getMainLooper()
                    )
                )
                startCapture(
                    mediaProjection = this,
                    intent = intent
                )
            }
        }

        return START_REDELIVER_INTENT
    }

    private fun startCapture(
        mediaProjection: MediaProjection,
        intent: Intent?
    ) {
        mainLooperDelayedAction(1000) {
            ScreenshotMaker(
                mMediaProjection = mediaProjection,
                displayMetrics = resources.displayMetrics
            ).takeScreenshot { bitmap ->
                val uri: Uri? = runBlocking {
                    File(filesDir, "screenshots").let { dir ->
                        dir.deleteRecursively()
                        dir.mkdirs()
                        val file = File(dir, "screenshot.png")
                        file.createNewFile()

                        FileOutputStream(file).use {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                        Uri.fromFile(file)
                    }
                }

                if (intent?.getStringExtra("screen") != "shot") {
                    applicationContext.startActivity(
                        Intent(
                            applicationContext,
                            AppActivityClass
                        ).apply {
                            putExtra("screen", intent?.getStringExtra("screen"))
                            type = "image/png"
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
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        bitmap.recycle()

                        fileController.save(
                            FileSaveTarget(
                                filename = "screenshot-$timeStamp.png",
                                originalUri = "screenshot",
                                imageFormat = ImageFormat.Png.Lossless,
                                data = stream.toByteArray()
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
    }

    override fun onBind(intent: Intent): IBinder = Binder()

}