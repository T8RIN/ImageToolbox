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

package ru.tech.imageresizershrinker.feature.checksum_tools.data

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.saving.io.ByteArrayReadable
import ru.tech.imageresizershrinker.core.data.saving.io.UriReadable
import ru.tech.imageresizershrinker.core.data.utils.computeFromReadable
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.model.HashingType
import ru.tech.imageresizershrinker.core.domain.saving.io.Readable
import ru.tech.imageresizershrinker.feature.checksum_tools.domain.ChecksumManager
import ru.tech.imageresizershrinker.feature.checksum_tools.domain.ChecksumSource
import javax.inject.Inject

internal class AndroidChecksumManager @Inject constructor(
    @ApplicationContext private val context: Context,
    dispatchersHolder: DispatchersHolder
) : ChecksumManager, DispatchersHolder by dispatchersHolder {

    override suspend fun calculateChecksum(
        type: HashingType,
        source: ChecksumSource
    ): String = withContext(defaultDispatcher) {
        runCatching {
            type.computeFromReadable(source.toReadable())
        }.getOrDefault("")
    }

    override suspend fun compareChecksum(
        checksum: String,
        type: HashingType,
        source: ChecksumSource
    ): Boolean = coroutineScope {
        calculateChecksum(type, source) == checksum
    }

    private fun ChecksumSource.toReadable(): Readable = when (this) {
        is ChecksumSource.Text -> ByteArrayReadable(data.encodeToByteArray())
        is ChecksumSource.Uri -> UriReadable(data.toUri(), context)
    }

}