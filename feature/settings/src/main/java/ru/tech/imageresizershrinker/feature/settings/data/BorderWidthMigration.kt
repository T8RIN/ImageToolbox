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

package ru.tech.imageresizershrinker.feature.settings.data

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

internal class BorderWidthMigration : DataMigration<Preferences> {

    private val migrated = booleanPreferencesKey("migratedBorders")
    override suspend fun cleanUp() = Unit

    override suspend fun shouldMigrate(
        currentData: Preferences
    ): Boolean = !(currentData[migrated] ?: false)

    override suspend fun migrate(
        currentData: Preferences
    ): Preferences = currentData
        .toMutablePreferences()
        .apply {
            this[SettingKeys.BORDER_WIDTH] = -1f
            this[migrated] = true
        }

}