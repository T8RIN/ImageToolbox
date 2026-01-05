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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.domain.utils.Flavor
import com.t8rin.imagetoolbox.core.resources.BuildConfig

val AppActivityClass: Class<*> by lazy {
    Class.forName(
        "com.t8rin.imagetoolbox.app.presentation.AppActivity"
    )
}

val MediaPickerActivityClass: Class<*> by lazy {
    Class.forName(
        "com.t8rin.imagetoolbox.feature.media_picker.presentation.MediaPickerActivity"
    )
}

fun createMediaPickerIntent(
    context: Context,
    allowMultiple: Boolean,
    currentAccent: Color,
    imageExtension: String
): Intent = Intent(
    Intent.ACTION_PICK,
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    context,
    MediaPickerActivityClass
).apply {
    setDataAndType(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        "image/$imageExtension"
    )
    if (allowMultiple) {
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }
    putExtra(ColorSchemeName, currentAccent.toArgb())
}

val AppVersionPreRelease: String by lazy {
    BuildConfig.VERSION_NAME
        .replace(BuildConfig.FLAVOR, "")
        .split("-")
        .takeIf { it.size > 1 }
        ?.drop(1)?.first()
        ?.takeWhile { it.isLetter() } ?: ""
}

val AppVersionPreReleaseFlavored: String by lazy {
    if (!Flavor.isFoss()) {
        AppVersionPreRelease
    } else {
        "${BuildConfig.FLAVOR} $AppVersionPreRelease"
    }.uppercase()
}

val AppVersion: String by lazy {
    BuildConfig.VERSION_NAME + if (Flavor.isFoss()) "-foss" else ""
}

const val ColorSchemeName = "scheme"