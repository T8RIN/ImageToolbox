package ru.tech.imageresizershrinker.coredata.migrations

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import ru.tech.imageresizershrinker.coredata.keys.Keys

class BorderWidthMigration : DataMigration<Preferences> {

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
            this[Keys.BORDER_WIDTH] = -1f
            this[migrated] = true
        }

}