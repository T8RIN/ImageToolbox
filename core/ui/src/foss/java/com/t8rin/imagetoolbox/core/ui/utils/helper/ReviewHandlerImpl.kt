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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.findActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object ReviewHandlerImpl : ReviewHandler {
    private val Context.dataStore by preferencesDataStore("saves_count")
    private val SAVES_COUNT = intPreferencesKey("SAVES_COUNT")
    private val NOT_SHOW_AGAIN = booleanPreferencesKey("NOT_SHOW_AGAIN")

    private val _showNotShowAgainButton = mutableStateOf(false)
    override val showNotShowAgainButton: Boolean by _showNotShowAgainButton

    override fun showReview(
        context: Context,
        onComplete: () -> Unit
    ) {
        val activity = context.findActivity() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                if (it[NOT_SHOW_AGAIN] != true) {
                    val saves = it[SAVES_COUNT] ?: 0
                    it[SAVES_COUNT] = saves + 1

                    _showNotShowAgainButton.value = saves >= 30

                    if (saves % 10 == 0) {
                        activity.startActivity(
                            Intent(activity, activity::class.java).apply {
                                action = Intent.ACTION_BUG_REPORT
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                        )
                    }
                } else {
                    _showNotShowAgainButton.value = false
                }
            }
            onComplete()
        }
    }

    override fun notShowReviewAgain(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[NOT_SHOW_AGAIN] = true
            }
        }
    }
}