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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import ru.tech.imageresizershrinker.core.resources.BuildConfig

val AppActivityClass: Class<*> by lazy {
    Class.forName(
        "ru.tech.imageresizershrinker.app.presentation.AppActivity"
    )
}

val MediaPickerActivityClass: Class<*> by lazy {
    Class.forName(
        "ru.tech.imageresizershrinker.feature.media_picker.presentation.MediaPickerActivity"
    )
}

val AppVersionPreRelease: String by lazy {
    BuildConfig.VERSION_NAME
        .replace(BuildConfig.FLAVOR, "")
        .split("-")
        .takeIf { it.size > 1 }
        ?.drop(1)?.first()
        ?.takeWhile { it.isLetter() } ?: ""
}

@Suppress("KotlinConstantConditions")
val AppVersionPreReleaseFlavored: String by lazy {
    if (BuildConfig.FLAVOR == "market") {
        AppVersionPreRelease
    } else {
        "${BuildConfig.FLAVOR} $AppVersionPreRelease"
    }.uppercase()
}

@Suppress("KotlinConstantConditions")
val AppVersion: String by lazy {
    BuildConfig.VERSION_NAME + if (BuildConfig.FLAVOR == "foss") "-foss" else ""
}

const val ColorSchemeName = "scheme"