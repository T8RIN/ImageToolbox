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

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker

@Composable
fun SimplePicture(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier,
    scale: ContentScale = ContentScale.Fit,
    boxModifier: Modifier = Modifier,
    enableContainer: Boolean = true,
    loading: Boolean = false,
    visible: Boolean = true
) {
    bitmap?.asImageBitmap()
        ?.takeIf { visible }
        ?.let {
            Box(
                modifier = boxModifier
                    .then(
                        if (enableContainer) {
                            Modifier
                                .container()
                                .padding(4.dp)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Picture(
                    model = it,
                    contentScale = scale,
                    contentDescription = null,
                    modifier = modifier
                        .aspectRatio(
                            it.safeAspectRatio
                        )
                        .clip(MaterialTheme.shapes.medium)
                        .transparencyChecker()
                        .shimmer(loading)
                )
            }
        }
}

//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//    val activity = LocalComponentActivity.current
//    DisposableEffect(it) {
//        activity.window.colorMode = if (bitmap.hasGainmap()) {
//            ActivityInfo.COLOR_MODE_HDR
//        } else ActivityInfo.COLOR_MODE_DEFAULT
//        onDispose {
//            activity.window.colorMode = ActivityInfo.COLOR_MODE_DEFAULT
//        }
//    }
//}