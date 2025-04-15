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

package ru.tech.imageresizershrinker.feature.quick_tiles.screenshot

import android.app.Activity.RESULT_CANCELED
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.FileSaveTarget
import ru.tech.imageresizershrinker.core.domain.utils.timestamp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.AppActivityClass
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.postToast
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.startActivity
import ru.tech.imageresizershrinker.core.ui.utils.helper.DataExtra
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelable
import ru.tech.imageresizershrinker.core.ui.utils.helper.ResultCode
import ru.tech.imageresizershrinker.core.ui.utils.helper.ScreenshotAction
import ru.tech.imageresizershrinker.core.ui.utils.helper.getTileScreenAction
import ru.tech.imageresizershrinker.core.ui.utils.helper.putTileScreenAction
import javax.inject.Inject

@AndroidEntryPoint
class ScreenshotService : Service() {

    @Inject
    lateinit var fileController: FileController

    @Inject
    lateinit var shareProvider: ImageShareProvider<Bitmap>

    @Inject
    lateinit var imageCompressor: ImageCompressor<Bitmap>

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        runCatching {
            val mediaProjectionManager = getSystemService<MediaProjectionManager>()

            val resultCode = intent?.getIntExtra(ResultCode, RESULT_CANCELED) ?: RESULT_CANCELED
            val data = intent?.parcelable<Intent>(DataExtra)
            val channelId = 1

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getSystemService<NotificationManager>()
                    ?.createNotificationChannel(
                        NotificationChannel(
                            channelId.toString(),
                            "screenshot",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                    )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        channelId,
                        Notification.Builder(applicationContext, channelId.toString())
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .build(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                } else {
                    startForeground(
                        channelId,
                        Notification.Builder(applicationContext, channelId.toString())
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .build()
                    )
                }
            }
            val callback = object : MediaProjection.Callback() {}

            mediaProjectionManager?.getMediaProjection(resultCode, data!!)?.apply {
                registerCallback(
                    callback,
                    Handler(Looper.getMainLooper())
                )
                val screenshotMaker = buildScreenshotMaker(
                    mediaProjection = this,
                    intent = intent
                )

                screenshotMaker.takeScreenshot(1000)
            }
        }

        return START_REDELIVER_INTENT
    }

    private fun buildScreenshotMaker(
        mediaProjection: MediaProjection,
        intent: Intent?
    ) = ScreenshotMaker(
        mediaProjection = mediaProjection,
        displayMetrics = resources.displayMetrics,
        onSuccess = { bitmap ->
            val uri: Uri? = runBlocking {
                shareProvider.cacheImage(
                    image = bitmap,
                    imageInfo = ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        imageFormat = ImageFormat.Png.Lossless
                    )
                )?.toUri()
            }

            if (intent?.getTileScreenAction() != ScreenshotAction) {
                startActivity(AppActivityClass) {
                    putTileScreenAction(intent?.getTileScreenAction())
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                runBlocking {
                    fileController.save(
                        saveTarget = FileSaveTarget(
                            filename = "screenshot-${timestamp()}.png",
                            originalUri = "screenshot",
                            imageFormat = ImageFormat.Png.Lossless,
                            data = imageCompressor.compress(
                                image = bitmap,
                                imageFormat = ImageFormat.Png.Lossless,
                                quality = Quality.Base()
                            )
                        ),
                        keepOriginalMetadata = true
                    )
                    val clipboardManager = getSystemService<ClipboardManager>()

                    uri?.let { uri ->
                        clipboardManager?.setPrimaryClip(
                            ClipData.newUri(
                                contentResolver,
                                "IMAGE",
                                uri
                            )
                        )
                    }
                    postToast(
                        textRes = R.string.saved_to_without_filename,
                        fileController.defaultSavingPath
                    )
                }
            }
        }
    )

    override fun onBind(intent: Intent): IBinder? = null

}