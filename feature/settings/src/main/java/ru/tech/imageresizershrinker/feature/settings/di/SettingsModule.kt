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

package ru.tech.imageresizershrinker.feature.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.settings.domain.SettingsInteractor
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.settings.domain.SettingsProvider
import ru.tech.imageresizershrinker.feature.settings.data.AndroidSettingsManager
import ru.tech.imageresizershrinker.feature.settings.data.BorderWidthMigration
import ru.tech.imageresizershrinker.feature.settings.domain.SettingsDatastoreName
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal interface SettingsModule {

    @Singleton
    @Binds
    fun provideSettingsManager(
        repository: AndroidSettingsManager
    ): SettingsManager

    @Singleton
    @Binds
    fun provideSettingsProvider(
        repository: SettingsManager
    ): SettingsProvider

    @Singleton
    @Binds
    fun provideSettingsInteractor(
        repository: SettingsManager
    ): SettingsInteractor

    companion object {

        @Singleton
        @Provides
        fun provideDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(SettingsDatastoreName) },
            migrations = listOf(BorderWidthMigration())
        )

    }

}