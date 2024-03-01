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

package ru.tech.imageresizershrinker.feature.filters.di

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.feature.filters.data.AndroidFilterMaskApplier
import ru.tech.imageresizershrinker.feature.filters.data.AndroidFilterProvider
import ru.tech.imageresizershrinker.feature.filters.data.FavoriteFiltersInteractorImpl
import ru.tech.imageresizershrinker.feature.filters.domain.FilterMaskApplier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal interface FilterModule {

    @Singleton
    @Binds
    fun filterProvider(
        provider: AndroidFilterProvider
    ): FilterProvider<Bitmap>

    @Singleton
    @Binds
    fun filterMaskApplier(
        applier: AndroidFilterMaskApplier
    ): FilterMaskApplier<Bitmap, Path, Color>

    companion object {

        @FilterInteractorDataStore
        @Singleton
        @Provides
        fun filterInteractorDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("favorite_filters") },
        )

    }

    @Singleton
    @Binds
    fun favoriteFiltersInteractor(
        interactor: FavoriteFiltersInteractorImpl
    ): FavoriteFiltersInteractor<Bitmap>

}