package ru.tech.imageresizershrinker.coredomain.image

import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGBA_F16
import coil.size.Size

interface Transformation<T> {

    /**
     * The unique cache key for this transformation.
     *
     * The key is added to the image request's memory cache key and should contain any params that
     * are part of this transformation (e.g. size, scale, color, radius, etc.).
     */
    val cacheKey: String

    /**
     * Apply the transformation to [input] and return the transformed [T].
     *
     * @param input The input [T] to transform.
     *  Its config will always be [ARGB_8888] or [RGBA_F16].
     * @param size The size of the image request.
     * @return The transformed [T].
     */
    suspend fun transform(input: T, size: Size): T
}
