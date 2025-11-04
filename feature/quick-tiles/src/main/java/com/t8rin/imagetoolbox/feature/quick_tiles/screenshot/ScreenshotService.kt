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

package com.t8rin.imagetoolbox.feature.quick_tiles.screenshot

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
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.getSystemService
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.FileSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getScreenExtra
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.postToast
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.putScreenExtra
import com.t8rin.imagetoolbox.core.ui.utils.helper.IntentUtils.parcelable
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemover
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@RequiresApi(Build.VERSION_CODES.N)
@AndroidEntryPoint
class ScreenshotService : Service() {

    @Inject
    lateinit var fileController: FileController

    @Inject
    lateinit var shareProvider: ImageShareProvider<Bitmap>

    @Inject
    lateinit var imageCompressor: ImageCompressor<Bitmap>

    @Inject
    lateinit var autoBackgroundRemover: AutoBackgroundRemover<Bitmap>

    @Inject
    lateinit var dispatchersHolder: DispatchersHolder

    private val coroutineScope by lazy {
        CoroutineScope(dispatchersHolder.defaultDispatcher)
    }

    private val clipboardManager get() = getSystemService<ClipboardManager>()
    private val mediaProjectionManager get() = getSystemService<MediaProjectionManager>()

    private val screenshotChannel = Channel<Screenshot>(Channel.BUFFERED)
    private var screenshotJob by smartJob()

    private var timeoutJob by smartJob()

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int = runCatching {
        startListening()

        val resultCode = intent?.getIntExtra(RESULT_CODE_EXTRA, RESULT_CANCELED) ?: RESULT_CANCELED

        val data = intent?.parcelable<Intent>(DATA_EXTRA)
        val channelId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService<NotificationManager>()
                ?.createNotificationChannel(
                    NotificationChannel(
                        channelId.toString(),
                        "screenshot",
                        NotificationManager.IMPORTANCE_MIN
                    )
                )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    channelId,
                    Notification.Builder(applicationContext, channelId.toString())
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(getString(R.string.processing_screenshot))
                        .build(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(
                    channelId,
                    Notification.Builder(applicationContext, channelId.toString())
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(getString(R.string.processing_screenshot))
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

        START_REDELIVER_INTENT
    }.getOrNull() ?: START_REDELIVER_INTENT

    override fun onDestroy() {
        screenshotJob?.cancel()
        timeoutJob?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    private fun startListening() {
        screenshotJob = coroutineScope.launch {
            screenshotChannel
                .receiveAsFlow()
                .debounce(500.milliseconds)
                .collectLatest { (intent, output) ->
                    val bitmap = autoBackgroundRemover.trimEmptyParts(output)

                    val resultBitmap = createBitmap(
                        width = bitmap.width,
                        height = bitmap.height,
                        config = bitmap.safeConfig
                    ).applyCanvas {
                        drawColor(Color.Black.toArgb())
                        drawBitmap(bitmap)
                    }

                    val uri: Uri? = shareProvider.cacheImage(
                        image = resultBitmap,
                        imageInfo = ImageInfo(
                            width = resultBitmap.width,
                            height = resultBitmap.height,
                            imageFormat = ImageFormat.Png.Lossless
                        )
                    )?.toUri()

                    if (intent?.action != SCREENSHOT_ACTION) {
                        startActivity(
                            Intent(Intent.ACTION_SEND).apply {
                                setPackage(applicationContext.packageName)
                                type = "image/png"
                                putScreenExtra(intent.getScreenExtra())
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                        )
                    } else {
                        fileController.save(
                            saveTarget = FileSaveTarget(
                                filename = "screenshot-${timestamp()}.png",
                                originalUri = "screenshot",
                                imageFormat = ImageFormat.Png.Lossless,
                                data = imageCompressor.compress(
                                    image = resultBitmap,
                                    imageFormat = ImageFormat.Png.Lossless,
                                    quality = Quality.Base()
                                )
                            ),
                            keepOriginalMetadata = true
                        )

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

                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
        }

        timeoutJob = coroutineScope.launch {
            delay(5.seconds)
            if (screenshotChannel.isEmpty) {
                postToast(
                    textRes = R.string.screenshot_not_captured_try_again,
                    isLong = true
                )
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun buildScreenshotMaker(
        mediaProjection: MediaProjection,
        intent: Intent?
    ) = ScreenshotMaker(
        mediaProjection = mediaProjection,
        context = this,
        onSuccess = {
            screenshotChannel.trySend(
                Screenshot(
                    intent = intent,
                    output = it
                )
            )
        }
    )

    private data class Screenshot(
        val intent: Intent?,
        val output: Bitmap
    )

}