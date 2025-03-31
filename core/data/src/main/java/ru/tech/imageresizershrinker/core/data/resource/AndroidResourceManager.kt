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

package ru.tech.imageresizershrinker.core.data.resource

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.tech.imageresizershrinker.core.domain.resource.ResourceManager
import java.util.Locale
import javax.inject.Inject

internal class AndroidResourceManager @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceManager {

    override fun getString(
        resId: Int
    ): String = context.getString(resId)

    override fun getString(
        resId: Int,
        vararg formatArgs: Any
    ): String = context.getString(resId, *formatArgs)

    override fun getStringLocalized(
        resId: Int,
        language: String
    ): String = context.getStringLocalized(
        resId = resId,
        locale = Locale.forLanguageTag(language)
    )

    override fun getStringLocalized(
        resId: Int,
        language: String,
        vararg formatArgs: Any
    ): String = context.getStringLocalized(
        resId = resId,
        locale = Locale.forLanguageTag(language),
        formatArgs = formatArgs
    )

    private fun Context.getStringLocalized(
        @StringRes
        resId: Int,
        locale: Locale,
    ): String = createConfigurationContext(
        Configuration(resources.configuration).apply { setLocale(locale) }
    ).getText(resId).toString()

    private fun Context.getStringLocalized(
        @StringRes
        resId: Int,
        locale: Locale,
        vararg formatArgs: Any
    ): String = createConfigurationContext(
        Configuration(resources.configuration).apply { setLocale(locale) }
    ).getString(resId, *formatArgs)
}