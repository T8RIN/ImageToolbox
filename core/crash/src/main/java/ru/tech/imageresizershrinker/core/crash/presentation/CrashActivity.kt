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

package ru.tech.imageresizershrinker.core.crash.presentation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.AndroidEntryPoint
import ru.tech.imageresizershrinker.core.crash.presentation.components.CrashHandler
import ru.tech.imageresizershrinker.core.crash.presentation.components.CrashRootContent
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.ui.utils.M3Activity
import javax.inject.Inject

@AndroidEntryPoint
class CrashActivity : M3Activity(), CrashHandler {

    @Inject
    lateinit var imageGetter: ImageGetter<Bitmap, ExifInterface>

    @Composable
    override fun Content() = CrashRootContent()

}