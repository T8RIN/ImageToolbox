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

package com.t8rin.imagetoolbox.core.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.StringRes

class AppContext private constructor(
    application: Context
) : ContextWrapper(application) {

    companion object {
        internal var appContext: AppContext? = null

        internal fun init(application: Context) {
            appContext = AppContext(application)
        }
    }

}

fun Context.initAppContext() = AppContext.init(this)

val appContext: AppContext
    get() = checkNotNull(AppContext.appContext) {
        "AppContext not initialized"
    }

fun getString(@StringRes resId: Int): String = appContext.getString(resId)