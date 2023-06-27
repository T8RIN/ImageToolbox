package ru.tech.imageresizershrinker.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.data.repository.CipherRepositoryImpl
import ru.tech.imageresizershrinker.domain.repository.CipherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideCipherRepository(): CipherRepository = CipherRepositoryImpl()

}