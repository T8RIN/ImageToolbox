package ru.tech.imageresizershrinker.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.data.saving.FileControllerImpl
import ru.tech.imageresizershrinker.core.domain.repository.CipherRepository
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SavingModule {

    @Singleton
    @Provides
    fun provideFileController(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>,
        cipherRepository: CipherRepository
    ): FileController = FileControllerImpl(context, dataStore, cipherRepository)

}