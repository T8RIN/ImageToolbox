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

package ru.tech.imageresizershrinker.core.ui.widget.image

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageScope
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.Transformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.transformation.GenericTransformation
import ru.tech.imageresizershrinker.core.ui.utils.helper.toCoil
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker

@Composable
fun Picture(
    modifier: Modifier = Modifier,
    model: Any?,
    transformations: List<Transformation> = emptyList(),
    manualImageLoader: ImageLoader? = null,
    contentDescription: String? = null,
    shape: Shape = RectangleShape,
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
    showTransparencyChecker: Boolean = true,
    isLoadingFromDifferentPlace: Boolean = false,
    enableUltraHDRSupport: Boolean = false
) {
    val context = LocalContext.current

    var errorOccurred by rememberSaveable { mutableStateOf(false) }

    var shimmerVisible by rememberSaveable { mutableStateOf(true) }

    val imageLoader = manualImageLoader ?: LocalImageLoader.current

    val activity = LocalComponentActivity.current

    val hdrTransformation = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && enableUltraHDRSupport) {
            listOf(
                GenericTransformation<Bitmap> { bitmap ->
                    withContext(Dispatchers.Main.immediate) {
                        delay(1000)
                        activity.window.colorMode = if (bitmap.hasGainmap()) {
                            ActivityInfo.COLOR_MODE_HDR
                        } else ActivityInfo.COLOR_MODE_DEFAULT
                    }
                    bitmap
                }.toCoil()
            )
        } else emptyList()
    }

    val request = if (model !is ImageRequest) {
        remember(
            context,
            model,
            crossfadeEnabled,
            allowHardware,
            transformations,
            hdrTransformation
        ) {
            ImageRequest.Builder(context)
                .data(model)
                .crossfade(crossfadeEnabled)
                .allowHardware(allowHardware)
                .transformations(
                    transformations + hdrTransformation
                )
                .build()
        }
    } else model

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && enableUltraHDRSupport) {
        DisposableEffect(model) {
            onDispose {
                activity.window.colorMode = ActivityInfo.COLOR_MODE_DEFAULT
            }
        }
    }

    SubcomposeAsyncImage(
        model = request,
        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(shape)
            .then(if (showTransparencyChecker) Modifier.transparencyChecker() else Modifier)
            .then(if (shimmerEnabled) Modifier.shimmer(shimmerVisible || isLoadingFromDifferentPlace) else Modifier),
        contentScale = contentScale,
        loading = {
            if (loading != null) loading(it)
            shimmerVisible = true
        },
        success = success,
        error = error,
        onSuccess = {
            if (model is ImageRequest && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && enableUltraHDRSupport) {
                activity.window.colorMode =
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