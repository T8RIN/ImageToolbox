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

package ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.Typography
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberPrevious
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.QrCode
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText

@Composable
internal fun QrCodePreview(
    captureController: CaptureController,
    isLandscape: Boolean,
    params: QrPreviewParams
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
                        if ((params.imageUri != null || params.description.isNotEmpty()) && params.content.isNotEmpty()) {
                            Modifier
                                .background(
                                    color = takeColorFromScheme {
                                        if (isLandscape) {
                                            surfaceContainerLowest
                                        } else surfaceContainerLow
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        } else Modifier
                    )
            ) {
                val targetSize = min(min(this.maxWidth, this.maxHeight), 300.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val essentials = rememberLocalEssentials()
                    val previous = rememberPrevious(params)

                    AnimatedContent(
                        targetState = params.content.isEmpty(),
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
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .container(
                                        resultPadding = 0.dp,
                                        color = if (isLandscape) MaterialTheme.colorScheme.surfaceContainerLowest
                                        else Color.Unspecified
                                    )
                                    .padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.QrCode2,
                                    contentDescription = null,
                                    modifier = Modifier.size(targetSize / 3)
                                )
                                Spacer(Modifier.height(4.dp))
                                AutoSizeText(
                                    text = stringResource(R.string.generated_barcode_will_be_here),
                                    textAlign = TextAlign.Center,
                                    key = { it.length },
                                    maxLines = 2
                                )
                            }
                        } else {
                            QrCode(
                                content = params.content,
                                modifier = Modifier.width(targetSize),
                                heightRatio = params.heightRatio,
                                type = params.type,
                                enforceBlackAndWhite = params.enforceBlackAndWhite,
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

                    BoxAnimatedVisibility(visible = params.description.isNotEmpty() && params.content.isNotEmpty()) {
                        MaterialTheme(
                            typography = Typography(params.descriptionFont)
                        ) {
                            Text(
                                text = params.description,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(targetSize)
                            )
                        }
                    }
                }

                if (params.imageUri != null && params.content.isNotEmpty()) {
                    Picture(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-48).dp)
                            .size(64.dp),
                        model = params.imageUri,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
        }
    }
}