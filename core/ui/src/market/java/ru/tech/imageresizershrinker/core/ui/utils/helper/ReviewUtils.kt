package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory

fun showReview(context: Context, onComplete: () -> Unit = {}) {
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