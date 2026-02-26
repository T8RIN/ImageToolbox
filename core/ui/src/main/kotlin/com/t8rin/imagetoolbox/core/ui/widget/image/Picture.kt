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

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.SingletonImageLoader
import coil3.compose.AsyncImageModelEqualityDelegate
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalAsyncImageModelEqualityDelegate
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageScope
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.domain.transformation.GenericTransformation
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.findActivity
import com.t8rin.imagetoolbox.core.ui.utils.helper.toCoil
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun Picture(
    model: Any?,
    modifier: Modifier = Modifier,
    transformations: List<Transformation>? = null,
    contentDescription: String? = null,
    shape: Shape = RectangleShape,
    contentScale: ContentScale = if (model.isCompose()) ContentScale.Fit else ContentScale.Crop,
    loading: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Error) -> Unit)? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = if (model is ImageVector) {
        ColorFilter.tint(LocalContentColor.current)
    } else null,
    filterQuality: FilterQuality = FilterQuality.None,
    shimmerEnabled: Boolean = true,
    crossfadeEnabled: Boolean = true,
    allowHardware: Boolean = true,
    showTransparencyChecker: Boolean = true,
    isLoadingFromDifferentPlace: Boolean = false,
    enableUltraHDRSupport: Boolean = false,
    size: Int? = null,
    contentPadding: PaddingValues = PaddingValues()
) {
    CompositionLocalProvider(
        LocalAsyncImageModelEqualityDelegate provides AsyncImageModelEqualityDelegate.AllProperties
    ) {
        when (model) {
            is ImageBitmap -> {
                Image(
                    bitmap = model,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    alignment = alignment,
                    contentScale = contentScale,
                    alpha = alpha,
                    colorFilter = colorFilter,
                    filterQuality = filterQuality
                )
            }

            is Painter -> {
                Image(
                    painter = model,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    alignment = alignment,
                    contentScale = contentScale,
                    alpha = alpha,
                    colorFilter = colorFilter
                )
            }

            is ImageVector -> {
                Image(
                    imageVector = model,
                    contentDescription = contentDescription ?: model.name,
                    modifier = modifier,
                    alignment = alignment,
                    contentScale = contentScale,
                    alpha = alpha,
                    colorFilter = colorFilter
                )
            }

            else -> {
                CoilPicture(
                    model = model,
                    modifier = modifier,
                    transformations = transformations,
                    contentDescription = contentDescription,
                    shape = shape,
                    contentScale = contentScale,
                    loading = loading,
                    success = success,
                    error = error,
                    onLoading = onLoading,
                    onSuccess = onSuccess,
                    onError = onError,
                    onState = onState,
                    alignment = alignment,
                    alpha = alpha,
                    colorFilter = colorFilter,
                    filterQuality = filterQuality,
                    shimmerEnabled = shimmerEnabled,
                    crossfadeEnabled = crossfadeEnabled,
                    allowHardware = allowHardware,
                    showTransparencyChecker = showTransparencyChecker,
                    isLoadingFromDifferentPlace = isLoadingFromDifferentPlace,
                    enableUltraHDRSupport = enableUltraHDRSupport,
                    size = size,
                    contentPadding = contentPadding
                )
            }
        }
    }
}

@Composable
private fun CoilPicture(
    model: Any?,
    modifier: Modifier,
    transformations: List<Transformation>?,
    contentDescription: String?,
    shape: Shape,
    contentScale: ContentScale,
    loading: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Loading) -> Unit)?,
    success: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Success) -> Unit)?,
    error: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Error) -> Unit)?,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)?,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)?,
    onError: ((AsyncImagePainter.State.Error) -> Unit)?,
    onState: ((AsyncImagePainter.State) -> Unit)?,
    alignment: Alignment,
    alpha: Float,
    colorFilter: ColorFilter?,
    filterQuality: FilterQuality,
    shimmerEnabled: Boolean,
    crossfadeEnabled: Boolean,
    allowHardware: Boolean,
    showTransparencyChecker: Boolean,
    isLoadingFromDifferentPlace: Boolean,
    enableUltraHDRSupport: Boolean,
    size: Int?,
    contentPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current

    var errorOccurred by rememberSaveable { mutableStateOf(false) }

    var shimmerVisible by rememberSaveable { mutableStateOf(true) }

    val imageLoader = context.imageLoader

    val hdrTransformation = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && enableUltraHDRSupport) {
            listOf(
                GenericTransformation<Bitmap> { bitmap ->
                    withContext(Dispatchers.Main.immediate) {
                        delay(1000)
                        context.findActivity()?.window?.colorMode = if (bitmap.hasGainmap()) {
                            ActivityInfo.COLOR_MODE_HDR
                        } else ActivityInfo.COLOR_MODE_DEFAULT
                    }
                    bitmap
                }.toCoil()
            )
        } else emptyList()
    }

    val request = model as? ImageRequest
        ?: remember(
            context,
            model,
            crossfadeEnabled,
            allowHardware,
            transformations,
            hdrTransformation,
            size
        ) {
            ImageRequest.Builder(context)
                .data(model)
                .crossfade(crossfadeEnabled)
                .allowHardware(allowHardware)
                .transformations(
                    (transformations ?: emptyList()) + hdrTransformation
                )
                .apply {
                    size?.let { size(it) }
                }
                .build()
        }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && enableUltraHDRSupport) {
        DisposableEffect(model) {
            onDispose {
                context.findActivity()?.window?.colorMode = ActivityInfo.COLOR_MODE_DEFAULT
            }
        }
    }

    SubcomposeAsyncImage(
        model = request,
        imageLoader = if (LocalInspectionMode.current) {
            SingletonImageLoader.get(LocalPlatformContext.current)
        } else imageLoader,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(shape)
            .then(
                if (!LocalInspectionMode.current) {
                    Modifier
                        .then(if (showTransparencyChecker) Modifier.transparencyChecker() else Modifier)
                        .then(if (shimmerEnabled) Modifier.shimmer(shimmerVisible || isLoadingFromDifferentPlace) else Modifier)
                } else {
                    Modifier
                }
            )
            .padding(contentPadding),
        contentScale = contentScale,
        loading = {
            if (loading != null) loading(it)
            shimmerVisible = true
        },
        success = success,
        error = error,
        onSuccess = {
            if (model is ImageRequest && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && enableUltraHDRSupport) {
                context.findActivity()?.window?.colorMode =
                    if (it.result.image.toBitmap(400, 400).hasGainmap()) {
                        ActivityInfo.COLOR_MODE_HDR
                    } else ActivityInfo.COLOR_MODE_DEFAULT
            }
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

private fun Any?.isCompose(): Boolean =
    this is Painter || this is ImageVector || this is ImageBitmap