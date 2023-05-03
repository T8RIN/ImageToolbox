package ru.tech.imageresizershrinker.widget

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build.VERSION.SDK_INT
import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageScope
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.Transformation
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.widget.StatusBarUtils.hideSystemBars
import ru.tech.imageresizershrinker.widget.StatusBarUtils.isSystemBarsHidden
import ru.tech.imageresizershrinker.widget.StatusBarUtils.showSystemBars

@Composable
fun Picture(
    modifier: Modifier = Modifier,
    model: Any?,
    transformations: List<Transformation> = emptyList(),
    manualImageRequest: ImageRequest? = null,
    manualImageLoader: ImageLoader? = null,
    contentDescription: String? = null,
    shape: Shape = CircleShape,
    contentScale: ContentScale = ContentScale.Crop,
    loading: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Error) -> Unit)? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    zoomParams: ZoomParams = ZoomParams(),
    shimmerEnabled: Boolean = true,
    crossfadeEnabled: Boolean = true,
    allowHardware: Boolean = true,
) {
    val activity = LocalContext.current.findActivity()
    val context = LocalContext.current

    var errorOccurred by rememberSaveable { mutableStateOf(false) }

    var shimmerVisible by rememberSaveable { mutableStateOf(true) }

    val imageLoader =
        manualImageLoader ?: context.imageLoader.newBuilder().components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
            add(SvgDecoder.Factory())
        }.build()

    val request = manualImageRequest ?: ImageRequest.Builder(context)
        .data(model)
        .crossfade(crossfadeEnabled)
        .allowHardware(allowHardware)
        .transformations(transformations)
        .build()

    val image: @Composable () -> Unit = {
        SubcomposeAsyncImage(
            model = request,
            imageLoader = imageLoader,
            contentDescription = contentDescription,
            modifier = modifier
                .clip(shape)
                .then(if (shimmerEnabled) Modifier.shimmer(shimmerVisible) else Modifier),
            contentScale = contentScale,
            loading = {
                if (loading != null) loading(it)
                shimmerVisible = true
            },
            success = success,
            error = error,
            onSuccess = {
                shimmerVisible = false
                onSuccess?.invoke(it)
                onState?.invoke(it)
            },
            onLoading = {
                onLoading?.invoke(it)
                onState?.invoke(it)
            },
            onError = {
                if (error != null) shimmerVisible = false
                onError?.invoke(it)
                onState?.invoke(it)
                errorOccurred = true
            },
            alignment = alignment,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality
        )
    }

    if (zoomParams.zoomEnabled) {
        Zoomable(
            state = rememberZoomableState(
                minScale = zoomParams.minZoomScale,
                maxScale = zoomParams.maxZoomScale
            ),
            onTap = {
                if (zoomParams.hideBarsOnTap) {
                    activity?.apply { if (isSystemBarsHidden) showSystemBars() else hideSystemBars() }
                }
                zoomParams.onTap(it)
            },
            content = { image() }
        )
    } else image()

    //Needed for triggering recomposition
    LaunchedEffect(errorOccurred) {
        if (errorOccurred && error == null) {
            shimmerVisible = false
            shimmerVisible = true
            errorOccurred = false
        }
    }

}

object StatusBarUtils {

    val Activity.isSystemBarsHidden: Boolean
        get() {
            return _isSystemBarsHidden
        }

    private var _isSystemBarsHidden = false

    fun Activity.hideSystemBars() = WindowInsetsControllerCompat(
        window,
        window.decorView
    ).let { controller ->
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        _isSystemBarsHidden = true
    }

    fun Activity.showSystemBars() = WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars()).also {
        _isSystemBarsHidden = false
    }
}

data class ZoomParams(
    val zoomEnabled: Boolean = false,
    val hideBarsOnTap: Boolean = false,
    val minZoomScale: Float = 1f,
    val maxZoomScale: Float = 4f,
    val onTap: (Offset) -> Unit = {}
)

@Composable
fun Zoomable(
    state: ZoomableState,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    onTap: (Offset) -> Unit = { },
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val conf = LocalConfiguration.current
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier,
    ) {
        var childWidth by remember { mutableStateOf(0) }
        var childHeight by remember { mutableStateOf(0) }
        LaunchedEffect(
            childHeight,
            childWidth,
            state.scale,
        ) {
            val maxX = (childWidth * state.scale - constraints.maxWidth)
                .coerceAtLeast(0F) / 2F
            val maxY = (childHeight * state.scale - constraints.maxHeight)
                .coerceAtLeast(0F) / 2F
            state.updateBounds(maxX, maxY)
        }
        val transformableState = rememberTransformableState { zoomChange, _, _ ->
            if (enable) scope.launch { state.onZoomChange(zoomChange) }
        }
        val doubleTapModifier = if (enable) {
            Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scope.launch {
                            if (state.scale == state.maxScale) state.animateScaleTo(state.minScale)
                            else {
                                state.animateScaleTo(state.scale + (state.maxScale - state.minScale) / 2)
                                state.setTapOffset(it, conf, density)
                            }
                        }
                    },
                    onTap = onTap
                )
            }
        } else Modifier

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDrag(
                        onDrag = { change, dragAmount ->
                            if (state.zooming && enable) {
                                if (change.positionChange() != Offset.Zero) change.consume()
                                scope.launch {
                                    state.drag(dragAmount)
                                    state.addPosition(
                                        change.uptimeMillis,
                                        change.position
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            if (enable) state.resetTracking()
                        },
                        onDragEnd = {
                            if (state.zooming && enable) {
                                scope.launch { state.dragEnd() }
                            }
                        },
                    )
                }
                .then(doubleTapModifier)
                .transformable(state = transformableState)
                .layout { measurable, constraints ->
                    val placeable =
                        measurable.measure(constraints = constraints)
                    childHeight = placeable.height
                    childWidth = placeable.width
                    layout(
                        width = constraints.maxWidth,
                        height = constraints.maxHeight
                    ) {
                        placeable.placeRelativeWithLayer(
                            (constraints.maxWidth - placeable.width) / 2,
                            (constraints.maxHeight - placeable.height) / 2
                        ) {
                            scaleX = state.scale
                            scaleY = state.scale
                            translationX = state.translateX
                            translationY = state.translateY
                        }
                    }
                }
        ) {
            content.invoke(this)
        }
    }
}


private suspend fun PointerInputScope.detectDrag(
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var drag: PointerInputChange?
        do {
            drag = awaitTouchSlopOrCancellation(down.id, onDrag)
        } while (drag != null && !drag.isConsumed)
        if (drag != null) {
            onDragStart.invoke(drag.position)
            if (!drag(drag.id) { onDrag(it, it.positionChange()) }) onDragCancel()
            else onDragEnd()
        }
    }
}

@Composable
fun rememberZoomableState(
    @FloatRange(from = 0.0) minScale: Float = 1f,
    @FloatRange(from = 0.0) maxScale: Float = Float.MAX_VALUE,
): ZoomableState = rememberSaveable(
    saver = ZoomableState.Saver
) {
    ZoomableState(
        minScale = minScale,
        maxScale = maxScale,
    )
}

/**
 * A state object that can be hoisted to observe scale and translate for [Zoomable].
 *
 * In most cases, this will be created via [rememberZoomableState].
 *
 * @param minScale the minimum scale value for [ZoomableState.minScale]
 * @param maxScale the maximum scale value for [ZoomableState.maxScale]
 * @param initialTranslateX the initial translateX value for [ZoomableState.translateX]
 * @param initialTranslateY the initial translateY value for [ZoomableState.translateY]
 * @param initialScale the initial scale value for [ZoomableState.scale]
 */
@Stable
class ZoomableState(
    @FloatRange(from = 0.0) val minScale: Float = 1f,
    @FloatRange(from = 0.0) val maxScale: Float = Float.MAX_VALUE,
    @FloatRange(from = 0.0) initialTranslateX: Float = 0f,
    @FloatRange(from = 0.0) initialTranslateY: Float = 0f,
    @FloatRange(from = 0.0) initialScale: Float = minScale,
) {
    private val velocityTracker = VelocityTracker()
    private val _translateY = Animatable(initialTranslateY)
    private val _translateX = Animatable(initialTranslateX)
    private val _scale = Animatable(initialScale)

    init {
        require(minScale < maxScale) { "minScale must be < maxScale" }
    }

    /**
     * The current scale value for [Zoomable]
     */
    @get:FloatRange(from = 0.0)
    val scale: Float
        get() = _scale.value

    /**
     * The current translateY value for [Zoomable]
     */
    @get:FloatRange(from = 0.0)
    val translateY: Float
        get() = _translateY.value

    /**
     * The current translateX value for [Zoomable]
     */
    @get:FloatRange(from = 0.0)
    val translateX: Float
        get() = _translateX.value

    internal val zooming: Boolean
        get() = scale > minScale

    /**
     * Instantly sets scale of [Zoomable] to given [scale]
     */
    suspend fun snapScaleTo(scale: Float) = coroutineScope {
        _scale.snapTo(scale.coerceIn(minimumValue = minScale, maximumValue = maxScale))
    }

    /**
     * Animates scale of [Zoomable] to given [scale]
     */
    suspend fun animateScaleTo(
        scale: Float,
        animationSpec: AnimationSpec<Float> = spring(),
        initialVelocity: Float = 0f,
    ) = coroutineScope {
        _scale.animateTo(
            targetValue = scale.coerceIn(minimumValue = minScale, maximumValue = maxScale),
            animationSpec = animationSpec,
            initialVelocity = initialVelocity,
        )
    }

    private suspend fun fling(velocity: Offset) = coroutineScope {
        launch {
            _translateY.animateDecay(
                velocity.y / 2f,
                exponentialDecay()
            )
        }
        launch {
            _translateX.animateDecay(
                velocity.x / 2f,
                exponentialDecay()
            )
        }
    }

    internal suspend fun drag(dragDistance: Offset) = coroutineScope {
        launch {
            _translateY.snapTo(_translateY.value + dragDistance.y)
        }
        launch {
            _translateX.snapTo(_translateX.value + dragDistance.x)
        }
    }

    internal suspend fun dragEnd() {
        val velocity = velocityTracker.calculateVelocity()
        fling(Offset(velocity.x, velocity.y))
    }

    internal suspend fun updateBounds(maxX: Float, maxY: Float) = coroutineScope {
        _translateY.updateBounds(-maxY, maxY)
        _translateX.updateBounds(-maxX, maxX)
    }

    internal suspend fun onZoomChange(zoomChange: Float) = snapScaleTo(scale * zoomChange)

    internal fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(timeMillis = timeMillis, position = position)
    }

    internal fun resetTracking() = velocityTracker.resetTracking()

    override fun toString(): String = "ZoomableState(" +
            "minScale=$minScale, " +
            "maxScale=$maxScale, " +
            "translateY=$translateY" +
            "translateX=$translateX" +
            "scale=$scale" +
            ")"

    suspend fun setTapOffset(tapOffset: Offset, configuration: Configuration, density: Density) =
        coroutineScope {
            val targetOffset: Offset = calcTargetOffset(tapOffset, configuration, density)
            launch {
                _translateX.animateTo(_translateX.value + targetOffset.x)
            }
            launch {
                _translateY.animateTo(_translateY.value + targetOffset.y)
            }
        }

    private fun calcTargetOffset(
        tapOffset: Offset,
        configuration: Configuration,
        density: Density
    ): Offset {
        val width = configuration.screenWidthDp.dp.toPx(density)
        val height = configuration.screenHeightDp.dp.toPx(density)

        val halfWidth = width / 2
        val halfHeight = height / 2

        val x = halfWidth - tapOffset.x
        val y = halfHeight - tapOffset.y

        return Offset(x, y)
    }

    companion object {
        /**
         * The default [Saver] implementation for [ZoomableState].
         */
        val Saver: Saver<ZoomableState, *> = listSaver(
            save = {
                listOf(
                    it.translateX,
                    it.translateY,
                    it.scale,
                    it.minScale,
                    it.maxScale,
                )
            },
            restore = {
                ZoomableState(
                    initialTranslateX = it[0],
                    initialTranslateY = it[1],
                    initialScale = it[2],
                    minScale = it[3],
                    maxScale = it[4],
                )
            }
        )
    }
}

private fun Dp.toPx(density: Density): Float {
    return with(density) { toPx() }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun Modifier.shimmer(
    visible: Boolean,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) = then(
    Modifier.placeholder(
        visible = visible,
        color = color,
        highlight = PlaceholderHighlight.shimmer()
    )
)

