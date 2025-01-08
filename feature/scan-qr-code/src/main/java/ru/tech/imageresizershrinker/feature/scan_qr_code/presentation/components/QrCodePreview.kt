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

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import ru.tech.imageresizershrinker.core.settings.presentation.model.UiFontFamily
import ru.tech.imageresizershrinker.core.ui.theme.Typography
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.QrCode

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun QrCodePreview(
    captureController: CaptureController,
    isLandscape: Boolean,
    qrImageUri: Uri?,
    qrDescription: String,
    qrContent: String,
    qrCornersSize: Int,
    qrDescriptionFont: UiFontFamily
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Companion.Center
    ) {
        Column(Modifier.capturable(captureController)) {
            if (qrImageUri != null) {
                Spacer(modifier = Modifier.height(32.dp))
            }
            BoxWithConstraints(
                modifier = Modifier
                    .then(
                        if ((qrImageUri != null || qrDescription.isNotEmpty()) && qrContent.isNotEmpty()) {
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
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
                ) {
                    QrCode(
                        content = qrContent,
                        modifier = Modifier
                            .padding(
                                top = if (qrImageUri != null) 36.dp else 0.dp,
                                bottom = if (qrDescription.isNotEmpty()) 16.dp else 0.dp
                            )
                            .then(
                                if (isLandscape) {
                                    Modifier
                                        .weight(1f, false)
                                        .aspectRatio(1f)
                                } else Modifier
                            )
                            .size(targetSize),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                            animateIntAsState(qrCornersSize).value.dp
                        )
                    )

                    BoxAnimatedVisibility(visible = qrDescription.isNotEmpty() && qrContent.isNotEmpty()) {
                        MaterialTheme(
                            typography = Typography(qrDescriptionFont)
                        ) {
                            Text(
                                text = qrDescription,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Companion.Center,
                                modifier = Modifier.width(targetSize)
                            )
                        }
                    }
                }

                if (qrImageUri != null && qrContent.isNotEmpty()) {
                    Picture(
                        modifier = Modifier
                            .align(Alignment.Companion.TopCenter)
                            .offset(y = (-48).dp)
                            .size(64.dp),
                        model = qrImageUri,
                        contentScale = ContentScale.Companion.Crop,
                        contentDescription = null,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
        }
    }
}