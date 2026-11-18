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

package com.t8rin.imagetoolbox.core.data

import android.content.Context
import android.graphics.Bitmap
import coil3.ImageLoader
import com.t8rin.exif.ExifInterface
import com.t8rin.imagetoolbox.core.data.image.AndroidImageGetter
import com.t8rin.imagetoolbox.core.data.image.toMetadata
import com.t8rin.imagetoolbox.core.data.resource.AndroidResourceManager
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.MetadataProvider
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FailureNotifier
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow

fun ExifInterface.testMetadata(): Metadata = toMetadata()

fun testResourceManager(context: Context): ResourceManager = AndroidResourceManager(context)

fun testImageGetter(
    context: Context,
    imageLoader: ImageLoader,
    appScope: AppScope,
    dispatchers: DispatchersHolder
): ImageGetter<Bitmap> = AndroidImageGetter(
    context = context,
    imageLoader = imageLoader,
    appScope = appScope,
    failureNotifier = object : FailureNotifier {
        override fun send(error: Throwable) {
            throw AssertionError("Unexpected failure notification", error)
        }

        override fun sendNoConnection() = Unit
    },
    metadataProvider = { MetadataProvider { null } },
    settingsProvider = object : SettingsProvider {
        override val settingsState = MutableStateFlow(SettingsState.Default)
        override suspend fun getSettingsState() = settingsState.value
    },
    dispatchersHolder = dispatchers
)