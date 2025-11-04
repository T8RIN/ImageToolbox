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

package com.t8rin.imagetoolbox.core.data.di

import com.t8rin.imagetoolbox.core.data.coroutines.AndroidDispatchersHolder
import com.t8rin.imagetoolbox.core.data.coroutines.AppScopeImpl
import com.t8rin.imagetoolbox.core.data.utils.executorDispatcher
import com.t8rin.imagetoolbox.core.di.DecodingDispatcher
import com.t8rin.imagetoolbox.core.di.DefaultDispatcher
import com.t8rin.imagetoolbox.core.di.EncodingDispatcher
import com.t8rin.imagetoolbox.core.di.IoDispatcher
import com.t8rin.imagetoolbox.core.di.UiDispatcher
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.Executors
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


@Module
@InstallIn(SingletonComponent::class)
internal interface CoroutinesModule {

    @Binds
    @Singleton
    fun dispatchersHolder(
        dispatchers: AndroidDispatchersHolder
    ): DispatchersHolder

    @Binds
    @Singleton
    fun appScope(
        impl: AppScopeImpl
    ): AppScope

    companion object {

        @DefaultDispatcher
        @Singleton
        @Provides
        fun defaultDispatcher(): CoroutineContext = executorDispatcher {
            Executors.newCachedThreadPool()
        }

        @DecodingDispatcher
        @Singleton
        @Provides
        fun decodingDispatcher(): CoroutineContext = executorDispatcher {
            Executors.newFixedThreadPool(
                2 * Runtime.getRuntime().availableProcessors() + 1
            )
        }

        @EncodingDispatcher
        @Singleton
        @Provides
        fun encodingDispatcher(): CoroutineContext = executorDispatcher {
            Executors.newSingleThreadExecutor()
        }

        @IoDispatcher
        @Singleton
        @Provides
        fun ioDispatcher(): CoroutineContext = Dispatchers.IO

        @UiDispatcher
        @Singleton
        @Provides
        fun uiDispatcher(): CoroutineContext = Dispatchers.Main.immediate

    }

}