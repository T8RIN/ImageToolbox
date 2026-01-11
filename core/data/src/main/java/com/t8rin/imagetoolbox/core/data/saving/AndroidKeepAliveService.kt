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

package com.t8rin.imagetoolbox.core.data.saving

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.t8rin.imagetoolbox.core.data.saving.KeepAliveForegroundService.Companion.ACTION_STOP
import com.t8rin.imagetoolbox.core.data.saving.KeepAliveForegroundService.Companion.ACTION_UPDATE
import com.t8rin.imagetoolbox.core.data.saving.KeepAliveForegroundService.Companion.EXTRA_DESC
import com.t8rin.imagetoolbox.core.data.saving.KeepAliveForegroundService.Companion.EXTRA_PROGRESS
import com.t8rin.imagetoolbox.core.data.saving.KeepAliveForegroundService.Companion.EXTRA_REMOVE_NOTIFICATION
import com.t8rin.imagetoolbox.core.data.saving.KeepAliveForegroundService.Companion.EXTRA_TITLE
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.utils.tryAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AndroidKeepAliveService @Inject constructor(
    @ApplicationContext private val context: Context
) : KeepAliveService {

    private val baseIntent: Intent
        get() = Intent(context, KeepAliveForegroundService::class.java)

    override fun updateOrStart(
        title: String,
        description: String,
        progress: Float
    ) {
        val intent = baseIntent
            .setAction(ACTION_UPDATE)
            .putExtra(EXTRA_TITLE, title)
            .putExtra(EXTRA_DESC, description)
            .putExtra(EXTRA_PROGRESS, progress)

        tryAll(
            { ContextCompat.startForegroundService(context, intent) },
            { context.startService(intent) }
        )
    }

    override fun stop(removeNotification: Boolean) {
        val intent = baseIntent
            .setAction(ACTION_STOP)
            .putExtra(EXTRA_REMOVE_NOTIFICATION, removeNotification)

        tryAll(
            { context.startService(intent) },
            { context.stopService(intent) }
        )
    }

}