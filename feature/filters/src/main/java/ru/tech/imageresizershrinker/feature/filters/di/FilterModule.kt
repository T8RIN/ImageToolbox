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
import androidx.exifinterface.media.ExifInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.feature.filters.data.FavoriteFiltersInteractorImpl
import ru.tech.imageresizershrinker.feature.filters.data.applier.AndroidFilterMaskApplier
import ru.tech.imageresizershrinker.feature.filters.data.provider.AndroidFilterProvider
import ru.tech.imageresizershrinker.feature.filters.domain.FilterMaskApplier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object FilterModule {

    @Singleton
    @Provides
    fun provideFilterProvider(
        @ApplicationContext context: Context,
    ): FilterProvider<Bitmap> = AndroidFilterProvider(context)

    @Singleton
    @Provides
    fun provideFilterMaskApplier(
        imageGetter: ImageGetter<Bitmap, ExifInterface>,
        imageTransformer: ImageTransformer<Bitmap>,
        filterProvider: FilterProvider<Bitmap>
    ): FilterMaskApplier<Bitmap, Path, Color> = AndroidFilterMaskApplier(
        imageGetter = imageGetter,
        imageTransformer = imageTransformer,
        filterProvider = filterProvider
    )

    @FilterInteractorDataStore
    @Singleton
    @Provides
    fun provideFilterInteractorDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("favorite_filters") },
    )

    @Singleton
    @Provides
    fun provideFavoriteFiltersInteractor(
        @FilterInteractorDataStore dataStore: DataStore<Preferences>
    ): FavoriteFiltersInteractor<Bitmap> = FavoriteFiltersInteractorImpl(dataStore)


}