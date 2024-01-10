@file:Suppress("ObjectPropertyName")

package ru.tech.imageresizershrinker.core.ui.widget.image

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageScope
import coil.request.ImageRequest
import coil.transform.Transformation
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.findActivity
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalImageLoader

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
    shimmerEnabled: Boolean = true,
    crossfadeEnabled: Boolean = true,
    allowHardware: Boolean = true,
    showTransparencyChecker: Boolean = true
) {
    val activity = LocalContext.current.findActivity()
    val context = LocalContext.current

    var errorOccurred by rememberSaveable { mutableStateOf(false) }

    var shimmerVisible by rememberSaveable { mutableStateOf(true) }

    val imageLoader = manualImageLoader ?: LocalImageLoader.current

    val request = manualImageRequest ?: ImageRequest.Builder(context)
        .data(model)
        .crossfade(crossfadeEnabled)
        .allowHardware(allowHardware)
        .transformations(transformations)
        .build()

    SubcomposeAsyncImage(
        model = request,
        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(shape)
            .then(if (showTransparencyChecker) Modifier.transparencyChecker() else Modifier)
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

    //Needed for triggering recomposition
    LaunchedEffect(errorOccurred) {
        if (errorOccurred && error == null) {
            shimmerVisible = false
            shimmerVisible = true
            errorOccurred = false
        }
    }

}