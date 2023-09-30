package ru.tech.imageresizershrinker.data.migrations

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import ru.tech.imageresizershrinker.data.keys.Keys

class BorderWidthMigration : DataMigration<Preferences> {
    override suspend fun cleanUp() = Unit

    override suspend fun shouldMigrate(
        currentData: Preferences
    ): Boolean = (currentData[Keys.APP_OPEN_COUNT] ?: 0) == 0

    override suspend fun migrate(
        currentData: Preferences
    ): Preferences = currentData
        .toMutablePreferences()
        .apply {
            this[Keys.BORDER_WIDTH] = 0f
        }

}