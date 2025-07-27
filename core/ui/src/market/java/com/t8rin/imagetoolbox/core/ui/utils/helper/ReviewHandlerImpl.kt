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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory

internal object ReviewHandlerImpl : ReviewHandler {

    override val showNotShowAgainButton: Boolean = false

    override fun showReview(
        context: Context,
        onComplete: () -> Unit
    ) {
        runCatching {
            val reviewManager = ReviewManagerFactory.create(context)

            reviewManager
                .requestReviewFlow()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        reviewManager
                            .launchReviewFlow(context as Activity, it.result)
                            .addOnCompleteListener {
                                onComplete()
                            }
                    }
                }
        }
    }

    override fun notShowReviewAgain(context: Context) = Unit

}