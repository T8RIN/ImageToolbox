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

@file:Suppress("KotlinConstantConditions", "LocalVariableName")

package com.t8rin.imagetoolbox.core.ui.widget.other

import android.graphics.Bitmap
import android.os.Build
import android.view.Choreographer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.createBitmap
import com.dotlottie.dlplayer.Config
import com.dotlottie.dlplayer.DotLottiePlayer
import com.dotlottie.dlplayer.DotLottiePlayerEvent
import com.dotlottie.dlplayer.Layout
import com.dotlottie.dlplayer.Mode
import com.dotlottie.dlplayer.createDefaultLayout
import com.lottiefiles.dotlottie.core.compose.runtime.DotLottieController
import com.lottiefiles.dotlottie.core.compose.runtime.DotLottiePlayerState
import com.lottiefiles.dotlottie.core.util.DotLottieContent
import com.lottiefiles.dotlottie.core.util.DotLottieEventListener
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.lottiefiles.dotlottie.core.util.DotLottieUtils
import com.lottiefiles.dotlottie.core.util.InternalDotLottieApi
import com.lottiefiles.dotlottie.core.util.pollAndDispatchAllEvents
import com.t8rin.imagetoolbox.core.domain.utils.throttleLatest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import com.lottiefiles.dotlottie.core.jni.DotLottiePlayer as DotLottieJNI

@OptIn(InternalDotLottieApi::class)
@Composable
fun CustomDotLottieAnimation(
    modifier: Modifier = Modifier,
    source: DotLottieSource,
    autoplay: Boolean = true,
    loop: Boolean = true,
    useFrameInterpolation: Boolean = true,
    themeId: String? = null,
    stateMachineId: String? = null,
    marker: String? = null,
    speed: Float = 1f,
    segment: Pair<Float, Float>? = null,
    playMode: Mode = Mode.FORWARD,
    controller: DotLottieController? = null,
    layout: Layout = createDefaultLayout(),
    onState: (DotLottiePlayerState) -> Unit = {},
    eventListeners: List<DotLottieEventListener> = emptyList(),
    threads: UInt? = null,
    loopCount: UInt = 0u,
) {
    val context = LocalContext.current

    val rController = remember { controller ?: DotLottieController() }

    val currentOnState by rememberUpdatedState(onState)

    LaunchedEffect(rController) {
        withContext(Dispatchers.Default) {
            rController.currentState.throttleLatest(100).collect {
                currentOnState(it)
            }
        }
    }

    val dlConfig = remember {
        buildDLConfig(
            autoplay,
            loop,
            playMode,
            speed,
            useFrameInterpolation,
            segment,
            marker,
            layout,
            themeId,
            loopCount
        )
    }

    val initialStateMachineId = remember { stateMachineId }
    val dlPlayer = remember(threads) {
        if (threads != null) {
            DotLottiePlayer.withThreads(dlConfig, threads)
        } else {
            DotLottiePlayer(dlConfig)
        }
    }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var drawVersion by remember { mutableIntStateOf(0) }
    val drawDstRect = remember { android.graphics.RectF() }
    val choreographer = remember { Choreographer.getInstance() }
    val currentState by rController.currentState.collectAsState()
    val _width by rController.height.collectAsState()
    val _height by rController.width.collectAsState()
    val frameUpdateRequested by rController.frameUpdateRequested.collectAsState()
    var layoutSize by remember { mutableStateOf<Size?>(null) }
    var animationData by remember { mutableStateOf<Result<DotLottieContent>?>(null) }
    var forceUpdateBitmap by remember { mutableStateOf(false) }

    val renderScope = rememberCoroutineScope()
    val renderMutex = remember { Mutex() }
    val singleThreadDispatcher = remember { Dispatchers.Default.limitedParallelism(1) }

    val frameCallback = remember {
        object : Choreographer.FrameCallback {
            var isActive = true

            override fun doFrame(frameTimeNanos: Long) {
                if (bitmap == null || !isActive) return

                // Skip if previous render is still in progress (non-blocking check)
                if (!renderMutex.tryLock()) {
                    if (dlPlayer.isPlaying() || rController.stateMachineIsActive) {
                        choreographer.postFrameCallback(this)
                    }
                    return
                }

                val ticked = if (rController.stateMachineIsActive) {
                    dlPlayer.stateMachineTick()
                } else {
                    dlPlayer.tick()
                }

                // Poll and dispatch events
                pollAndDispatchEvents(dlPlayer, rController)

                var lockHandedToCoroutine = false

                if (ticked || forceUpdateBitmap) {
                    val shouldResetFlag = !ticked && forceUpdateBitmap
                    val bmp = bitmap

                    lockHandedToCoroutine = true
                    renderScope.launch(singleThreadDispatcher) {
                        try {
                            ensureActive()

                            if (bmp != null && !bmp.isRecycled) {
                                DotLottieJNI.nativeFlushBitmapPixels(bmp)

                                withContext(Dispatchers.Main) {
                                    if (isActive && !bmp.isRecycled) {
                                        drawVersion++
                                        if (shouldResetFlag) {
                                            forceUpdateBitmap = false
                                        }
                                    }
                                }
                            }
                        } finally {
                            renderMutex.unlock()
                        }
                    }
                }

                if (dlPlayer.isPlaying() || rController.stateMachineIsActive) {
                    choreographer.postFrameCallback(this)
                }

                if (!lockHandedToCoroutine) {
                    renderMutex.unlock()
                }
            }
        }
    }

    LaunchedEffect(source) {
        animationData = kotlin.runCatching {
            DotLottieUtils.getContent(context, source)
        }
    }

    suspend fun init(animationData: DotLottieContent, layoutSize: Size) {
        runCatching {
            val height = layoutSize.height.toUInt()
            val width = layoutSize.width.toUInt()
            val isLoaded = dlPlayer.isLoaded()
            rController.resize(height, width)

            // Move expensive native load off the main thread.
            withContext(Dispatchers.Default) {
                renderMutex.withLock {
                    bitmap?.let { DotLottieJNI.nativeUnlockBitmapPixels(it) }

                    // Create bitmap and lock its pixels as the render target
                    val newBitmap = createBitmap(width.toInt(), height.toInt())
                    val pixelPtr = DotLottieJNI.nativeLockBitmapPixels(newBitmap)
                    if (pixelPtr == 0L) return@withLock
                    dlPlayer.setSwTarget(pixelPtr, width, height)

                    when (animationData) {
                        is DotLottieContent.Json -> {
                            dlPlayer.loadAnimationData(animationData.jsonString, width, height)
                        }

                        is DotLottieContent.Binary -> {
                            dlPlayer.loadDotlottieData(animationData.data, width, height)
                        }
                    }

                    bitmap = newBitmap
                    drawDstRect.set(0f, 0f, layoutSize.width, layoutSize.height)
                    drawVersion++
                }
            }

            if (!isLoaded) {
                rController.init()
            }
            choreographer.postFrameCallback(frameCallback)
        }.onFailure { e ->
            rController.eventListeners.forEach {
                it.onLoadError(e)
            }
        }
    }

    LaunchedEffect(animationData, layoutSize) {
        animationData?.let { result ->
            result.onSuccess { data ->
                if (layoutSize != null) {
                    init(data, layoutSize!!)
                }
                if (initialStateMachineId != null) {
                    if (initialStateMachineId.isNotEmpty()) {
                        rController.stateMachineLoad(initialStateMachineId)
                        rController.stateMachineStart()
                    }
                }
            }.onFailure { t ->
                rController.eventListeners.forEach {
                    it.onLoadError(t)
                }
            }
        }
    }

    LaunchedEffect(dlPlayer.isPlaying(), currentState) {
        choreographer.postFrameCallback(frameCallback)
    }

    LaunchedEffect(frameUpdateRequested) {
        if (frameUpdateRequested > 0 && !dlPlayer.isPlaying()) {
            forceUpdateBitmap = true
            choreographer.postFrameCallback(frameCallback)
        }
    }

    LaunchedEffect(
        loop,
        autoplay,
        playMode,
        useFrameInterpolation,
        speed,
        segment,
        themeId,
        marker,
        layout,
    ) {
        val conf = dlPlayer.config()
        conf.loopAnimation = loop
        conf.autoplay = autoplay
        conf.mode = playMode
        conf.useFrameInterpolation = useFrameInterpolation
        conf.speed = speed
        conf.marker = marker ?: ""
        conf.layout = layout
        conf.themeId = themeId ?: ""

        if (segment != null) {
            conf.segment = listOf(segment.first, segment.second)
        } else {
            conf.segment = emptyList()
        }

        dlPlayer.setConfig(conf)

        // Start playing if player isCompleted
        if (autoplay && loop && dlPlayer.isComplete()) {
            dlPlayer.play()
        }

        choreographer.postFrameCallback(frameCallback)
    }

    LaunchedEffect(_width, _height) {
        if (dlPlayer.isLoaded() && (_height != 0u || _width != 0u)) {
            // Skip if already at the correct size (avoids destroying the buffer init() just populated)
            val currentBmp = bitmap
            if (currentBmp != null && !currentBmp.isRecycled
                && currentBmp.width == _width.toInt() && currentBmp.height == _height.toInt()
            ) {
                return@LaunchedEffect
            }
            // Wait for any pending render to complete
            renderMutex.withLock {
                val oldBitmap = bitmap
                val newBitmap = createBitmap(_width.toInt(), _height.toInt())
                val pixelPtr = DotLottieJNI.nativeLockBitmapPixels(newBitmap)
                if (pixelPtr == 0L) return@withLock
                dlPlayer.resize(_width, _height)
                dlPlayer.setSwTarget(pixelPtr, _width, _height)
                bitmap = newBitmap
                drawVersion++
                oldBitmap?.let {
                    DotLottieJNI.nativeUnlockBitmapPixels(it)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        it.recycle()
                    }
                }
            }
            if (!dlPlayer.isPlaying()) {
                forceUpdateBitmap = true
                choreographer.postFrameCallback(frameCallback)
            }
        }
    }

    DisposableEffect(UInt) {
        rController.setPlayerInstance(dlPlayer, dlConfig)
        eventListeners.forEach { rController.addEventListener(it) }

        onDispose {
            frameCallback.isActive = false
            choreographer.removeFrameCallback(frameCallback)
            renderScope.cancel()

            bitmap?.let { DotLottieJNI.nativeUnlockBitmapPixels(it) }
            // Detach the controller from the native player before destroying it so any
            // pending callbacks or recompositions that touch the controller see a
            // safe-default state instead of calling into an already-destroyed player.
            rController.destroy()
            runCatching { dlPlayer.destroy() }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                bitmap?.recycle()
            }
        }
    }


    Box(
        modifier = modifier
            .defaultMinSize(200.dp, 200.dp)
            .onGloballyPositioned { layoutCoordinates ->
                val newSize = layoutCoordinates.size.toSize()
                if (layoutSize?.width != newSize.width || layoutSize?.height != newSize.height) {
                    layoutSize = newSize
                }
            }
            .drawBehind {
                @Suppress("UNUSED_EXPRESSION")
                drawVersion
                val bmp = bitmap
                if (bmp != null && !bmp.isRecycled) {
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawBitmap(bmp, null, drawDstRect, null)
                    }
                }
            }
    ) {
        @Suppress("UNUSED_EXPRESSION")
        drawVersion
    }
}

private fun pollAndDispatchEvents(player: DotLottiePlayer, controller: DotLottieController) {
    pollAndDispatchAllEvents(
        player = player,
        eventListeners = controller.eventListeners,
        stateMachineListeners = controller.stateMachineListeners,
        onStateChange = controllerStateChange(controller),
        onOpenUrl = controller.onOpenUrlCallback,
    )
}

@OptIn(InternalDotLottieApi::class)
private fun controllerStateChange(controller: DotLottieController): (DotLottiePlayerEvent) -> Unit =
    { event ->
        when (event) {
            is DotLottiePlayerEvent.Load -> controller.updateState(DotLottiePlayerState.LOADED)
            is DotLottiePlayerEvent.LoadError -> controller.updateState(DotLottiePlayerState.ERROR)
            is DotLottiePlayerEvent.Play -> controller.updateState(DotLottiePlayerState.PLAYING)
            is DotLottiePlayerEvent.Pause -> controller.updateState(DotLottiePlayerState.PAUSED)
            is DotLottiePlayerEvent.Stop -> controller.updateState(DotLottiePlayerState.STOPPED)
            is DotLottiePlayerEvent.Complete -> controller.updateState(DotLottiePlayerState.COMPLETED)
            else -> {}
        }
    }

private fun buildDLConfig(
    autoplay: Boolean,
    loop: Boolean,
    playMode: Mode,
    speed: Float,
    useFrameInterpolation: Boolean,
    segment: Pair<Float, Float>?,
    marker: String?,
    layout: Layout,
    themeId: String?,
    loopCount: UInt,
): Config = Config(
    autoplay = autoplay,
    loopAnimation = loop,
    mode = playMode,
    speed = speed,
    useFrameInterpolation = useFrameInterpolation,
    segment = if (segment != null) listOf(segment.first, segment.second) else emptyList(),
    backgroundColor = 0u,
    marker = marker ?: "",
    layout = layout,
    themeId = themeId ?: "",
    stateMachineId = "",
    animationId = "",
    loopCount = loopCount
)
