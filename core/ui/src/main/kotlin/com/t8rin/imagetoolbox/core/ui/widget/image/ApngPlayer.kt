/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import java.io.BufferedOutputStream
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Download
import com.t8rin.imagetoolbox.core.resources.icons.Pause
import com.t8rin.imagetoolbox.core.resources.icons.Play
import com.t8rin.imagetoolbox.core.resources.icons.SelectAll
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFolderPicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.utils.isApng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import oupson.apng.decoder.ApngDecoder
import oupson.apng.drawable.ApngDrawable

private const val STREAM_BUFFER_BYTES = 1 shl 16

class ApngFrame(
    val bitmap: Bitmap,
    val durationMs: Long
)

class ApngAnimation(
    val frames: List<ApngFrame>
) {
    val totalDurationMs: Long = frames.sumOf { it.durationMs }.coerceAtLeast(1L)

    fun frameIndexAt(positionMs: Long): Int {
        if (frames.isEmpty()) return 0
        var accumulator = 0L
        frames.forEachIndexed { index, frame ->
            accumulator += frame.durationMs
            if (positionMs < accumulator) return index
        }
        return frames.lastIndex
    }
}

private suspend fun decodeApngAnimation(
    context: Context,
    uri: Uri
): ApngAnimation? = withContext(Dispatchers.IO) {
    runCatching {
        if (!uri.isApng()) return@runCatching null

        val drawable = ApngDecoder(context, uri, ApngDecoder.Config())
            .decodeApng(context).getOrNull() as? ApngDrawable ?: return@runCatching null

        (0 until drawable.numberOfFrames).mapNotNull { index ->
            (drawable.getFrame(index) as? BitmapDrawable)?.let {
                ApngFrame(
                    bitmap = it.bitmap,
                    durationMs = drawable.getDuration(index).toLong().coerceAtLeast(1L)
                )
            }
        }.takeIf { it.isNotEmpty() }?.let(::ApngAnimation)
    }.getOrNull()
}

@Stable
class ApngPlayerState internal constructor(
    private val animation: ApngAnimation
) {
    var currentFrameIndex by mutableIntStateOf(0)
        private set

    var progress by mutableFloatStateOf(0f)
        private set

    var isPlaying by mutableStateOf(true)
        private set

    var isScrubbing by mutableStateOf(false)
        internal set

    val frameCount: Int get() = animation.frames.size

    val currentFrame: Bitmap
        get() = animation.frames[currentFrameIndex.coerceIn(0, frameCount - 1)].bitmap

    fun togglePlay() {
        isPlaying = !isPlaying
    }

    fun scrubTo(fraction: Float) {
        isScrubbing = true
        val position = (fraction.coerceIn(0f, 1f) * animation.totalDurationMs).toLong()
            .coerceIn(0L, animation.totalDurationMs - 1L)
        playedMs = position
        anchorNanos = 0L
        currentFrameIndex = animation.frameIndexAt(position)
        progress = position.toFloat() / animation.totalDurationMs
    }

    private var playedMs = 0L
    private var anchorNanos = 0L

    internal fun reanchor(nowNanos: Long) {
        anchorNanos = nowNanos
    }

    internal fun tick(nowNanos: Long) {
        if (anchorNanos != 0L) {
            playedMs += (nowNanos - anchorNanos) / 1_000_000L
        }
        anchorNanos = nowNanos
        val position = playedMs % animation.totalDurationMs
        currentFrameIndex = animation.frameIndexAt(position)
        progress = position.toFloat() / animation.totalDurationMs
    }
}

/**
 * Everything the image viewer needs to play an APNG: playback state,
 * the frame-driving effect and frame saving. Hosts that only show
 * still images simply never touch this class.
 */
@Stable
class ApngPlayback internal constructor(
    private val animation: ApngAnimation,
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val exportSlots = Semaphore(permits = 4)
    val player: ApngPlayerState = ApngPlayerState(animation)

    /**
     * Writes the frame currently on screen into the document
     * picked through the system save dialog.
     */
    internal fun saveCurrentFrame(target: Uri) {
        scope.launch(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openOutputStream(target)?.use { out ->
                    player.currentFrame.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }.onFailure(AppToastHost::handleFileSystemFailure)
        }
    }

    /**
     * Writes every frame of the animation as a PNG into the
     * picked directory: {name}_frame_{1..n}.png. Frames are
     * encoded in parallel, PNG compression being CPU bound.
     */
    internal fun exportAllFrames(tree: Uri, filename: String?) {
        val base = filename?.substringBeforeLast('.') ?: "apng"
        val directory = DocumentFile.fromTreeUri(context, tree)
        scope.launch(Dispatchers.IO) {
            runCatching {
                coroutineScope {
                    animation.frames.mapIndexed { index, frame ->
                        async {
                            exportSlots.withPermit {
                                writeFrame(directory, frame, index, base)
                            }
                        }
                    }.awaitAll()
                }
            }.onFailure(AppToastHost::handleFileSystemFailure)
        }
    }

    private suspend fun writeFrame(
        directory: DocumentFile?,
        frame: ApngFrame,
        index: Int,
        base: String
    ) {
        directory?.createFile("image/png", "${base}_frame_${index + 1}.png")?.let { file ->
            context.contentResolver.openOutputStream(file.uri)?.let { raw ->
                BufferedOutputStream(raw, STREAM_BUFFER_BYTES).use { out ->
                    frame.bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
        }
    }
}

/**
 * Prepares APNG playback for the given uri in a single call.
 * Returns null for still images, keeping the host code linear.
 */
@Composable
fun rememberApngPlayback(uri: Uri?): ApngPlayback? {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var animation by remember { mutableStateOf<ApngAnimation?>(null) }
    LaunchedEffect(uri) {
        animation = uri?.let { decodeApngAnimation(context, it) }
    }

    val playback = remember(animation, context, scope) {
        animation?.let { ApngPlayback(it, context, scope) }
    }

    val player = playback?.player
    LaunchedEffect(playback, player?.isPlaying, player?.isScrubbing) {
        val activePlayer = player ?: return@LaunchedEffect
        if (!activePlayer.isPlaying || activePlayer.isScrubbing) return@LaunchedEffect

        activePlayer.reanchor(withFrameNanos { it })
        while (true) {
            withFrameNanos(activePlayer::tick)
        }
    }

    return playback
}

@Composable
fun ApngPlayerControlBar(
    playback: ApngPlayback,
    filename: String?,
    modifier: Modifier = Modifier
) {
    val player = playback.player
    val frameSaver = rememberFileCreator(
        mimeType = MimeType.StaticPng,
        onSuccess = playback::saveCurrentFrame
    )
    val framesExporter = rememberFolderPicker(
        onSuccess = { playback.exportAllFrames(it, filename) }
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        EnhancedIconButton(
            onClick = player::togglePlay
        ) {
            Icon(
                imageVector = if (player.isPlaying) Icons.Rounded.Pause else Icons.Rounded.Play,
                contentDescription = stringResource(
                    if (player.isPlaying) R.string.pause else R.string.play
                ),
                tint = White
            )
        }
        Slider(
            value = player.progress,
            onValueChange = player::scrubTo,
            onValueChangeFinished = { player.isScrubbing = false },
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${player.currentFrameIndex + 1}/${player.frameCount}",
            color = White,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(52.dp)
        )
        Spacer(Modifier.width(4.dp))
        EnhancedIconButton(
            onClick = {
                frameSaver.make(
                    "${filename?.substringBeforeLast('.') ?: "apng"}_frame_${player.currentFrameIndex + 1}.png"
                )
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = stringResource(R.string.save_current_frame),
                tint = White
            )
        }
        EnhancedIconButton(
            onClick = framesExporter::pickFolder
        ) {
            Icon(
                imageVector = Icons.Outlined.SelectAll,
                contentDescription = stringResource(R.string.export_all_frames),
                tint = White
            )
        }
    }
}
