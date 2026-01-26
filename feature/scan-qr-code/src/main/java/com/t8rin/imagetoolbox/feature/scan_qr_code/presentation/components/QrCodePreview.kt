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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil3.request.ImageRequest
import coil3.size.Precision
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.ProvideTypography
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.theme.takeIf
import com.t8rin.imagetoolbox.core.ui.utils.capturable.CaptureController
import com.t8rin.imagetoolbox.core.ui.utils.capturable.capturable
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberPrevious
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.other.QrCode
import com.t8rin.imagetoolbox.core.utils.appContext

@Composable
internal fun QrCodePreview(
    captureController: CaptureController,
    isLandscape: Boolean,
    params: QrPreviewParams,
    onStartScan: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.capturable(captureController)) {
            if (params.imageUri != null) {
                Spacer(modifier = Modifier.height(32.dp))
            }
            BoxWithConstraints(
                modifier = Modifier
                    .then(
                        if ((params.imageUri != null || params.description.isNotEmpty()) && params.content.raw.isNotEmpty()) {
                            Modifier
                                .background(
                                    color = takeColorFromScheme {
                                        if (isLandscape) {
                                            surfaceContainerLowest
                                        } else surfaceContainerLow
                                    },
                                    shape = ShapeDefaults.default
                                )
                                .padding(16.dp)
                        } else Modifier
                    )
            ) {
                val targetSize = min(min(this.maxWidth, this.maxHeight), 400.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val essentials = rememberLocalEssentials()
                    val previous = rememberPrevious(params)

                    AnimatedContent(
                        targetState = params.content.raw.isEmpty(),
                        modifier = Modifier
                            .padding(
                                top = if (params.imageUri != null) 36.dp else 0.dp,
                                bottom = if (params.description.isNotEmpty()) 16.dp else 0.dp
                            )
                            .then(
                                if (isLandscape) {
                                    Modifier
                                        .weight(1f, false)
                                        .aspectRatio(1f)
                                } else Modifier
                            )
                    ) { isEmpty ->
                        if (isEmpty) {
                            ImageNotPickedWidget(
                                onPickImage = onStartScan,
                                text = stringResource(R.string.generated_barcode_will_be_here),
                                containerColor = MaterialTheme
                                    .colorScheme
                                    .surfaceContainerLowest
                                    .takeIf(isLandscape)
                            )
                        } else {
                            QrCode(
                                content = params.content.raw,
                                modifier = Modifier.width(targetSize),
                                heightRatio = params.heightRatio,
                                type = params.type,
                                qrParams = params.qrParams,
                                cornerRadius = animateIntAsState(params.cornersSize).value.dp,
                                onSuccess = {
                                    essentials.dismissToasts()
                                },
                                onFailure = {
                                    essentials.dismissToasts()
                                    if (previous != params) essentials.showFailureToast(it)
                                }
                            )
                        }
                    }

                    BoxAnimatedVisibility(visible = params.description.isNotEmpty() && params.content.raw.isNotEmpty()) {
                        ProvideTypography(params.descriptionFont) {
                            Text(
                                text = params.description,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(targetSize)
                            )
                        }
                    }
                }

                if (params.imageUri != null && params.content.raw.isNotEmpty()) {
                    Picture(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-48).dp)
                            .size(64.dp),
                        model = remember(params.imageUri) {
                            ImageRequest.Builder(appContext)
                                .data(params.imageUri)
                                .size(1000, 1000)
                                .precision(Precision.INEXACT)
                                .build()
                        },
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.High,
                        contentDescription = null,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
        }
    }
}