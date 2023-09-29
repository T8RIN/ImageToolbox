@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.data.migrations

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.data.keys.Keys

class BorderWidthMigration : DataMigration<Preferences> {
    override suspend fun cleanUp() = Unit

    override suspend fun shouldMigrate(
        currentData: Preferences
    ): Boolean = BuildConfig.VERSION_CODE < 89

    override suspend fun migrate(
        currentData: Preferences
    ): Preferences = currentData
        .toMutablePreferences()
        .apply {
            this[Keys.BORDER_WIDTH] = 0f
        }

}