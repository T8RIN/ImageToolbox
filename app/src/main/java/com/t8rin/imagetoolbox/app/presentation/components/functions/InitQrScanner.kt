/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.app.presentation.components.functions

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.applyPadding
import com.t8rin.opencv_tools.qr_prepare.QrPrepareHelper
import io.github.g00fy2.quickie.extensions.QrProcessor

internal fun initQrScanner() = QrProcessor.setProcessor(::prepareBitmap)

private fun prepareBitmap(bitmap: Bitmap): Bitmap =
    QrPrepareHelper.prepareQrForDecode(bitmap.applyPadding(100))