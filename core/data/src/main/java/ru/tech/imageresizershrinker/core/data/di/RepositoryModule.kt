package ru.tech.imageresizershrinker.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.data.repository.CipherRepositoryImpl
import ru.tech.imageresizershrinker.core.data.repository.SettingsRepositoryImpl
import ru.tech.imageresizershrinker.coredomain.repository.CipherRepository
import ru.tech.imageresizershrinker.coredomain.repository.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideCipherRepository(): CipherRepository = CipherRepositoryImpl()

    @Singleton
    @Provides
    fun provideSettingsRepository(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>
    ): SettingsRepository = SettingsRepositoryImpl(context, dataStore)

}