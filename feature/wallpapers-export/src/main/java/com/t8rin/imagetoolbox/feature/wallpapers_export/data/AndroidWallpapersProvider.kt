/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.wallpapers_export.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.app.WallpaperManager.FLAG_LOCK
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.hasPermissionAllowed
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.WallpapersProvider
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.Permission
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.Wallpaper
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.WallpapersResult
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidWallpapersProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : WallpapersProvider, DispatchersHolder by dispatchersHolder {

    private val wallpaperManager = WallpaperManager.getInstance(context)

    override suspend fun getWallpapers(): WallpapersResult = withContext(defaultDispatcher) {
        val missingPermissions = mutableListOf<Permission>()

        if (!context.hasPermissionAllowed(Manifest.permission.READ_MEDIA_IMAGES) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            missingPermissions.add(Permission.ReadMediaImages)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            missingPermissions.add(Permission.ManageExternalStorage)
        }

        if (missingPermissions.isNotEmpty()) {
            return@withContext WallpapersResult.Failed.NoPermissions(missingPermissions)
        }

        val wallpapers = loadWallpapers()

        return@withContext WallpapersResult.Success(
            wallpapers.mapIndexed { index, drawable ->
                val imageUri = drawable?.let {
                    shareProvider.cacheImage(
                        image = drawable.toBitmap(),
                        imageInfo = ImageInfo(
                            width = drawable.intrinsicWidth,
                            height = drawable.intrinsicHeight,
                            imageFormat = ImageFormat.Png.Lossless
                        )
                    )
                }

                Wallpaper(
                    imageUri = imageUri,
                    nameRes = nameByIndex(index),
                    resolution = drawable?.let {
                        IntegerSize(
                            width = it.intrinsicWidth,
                            height = it.intrinsicHeight
                        )
                    } ?: IntegerSize.Zero
                )
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun loadWallpapers(): List<Drawable?> {
        val home = safe { wallpaperManager.drawable }
        val builtIn = safe { wallpaperManager.getBuiltInDrawable(30000, 30000, false, 0.5f, 0.5f) }
        val lock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            safe {
                wallpaperManager.getWallpaperFile(FLAG_LOCK)?.use {
                    BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
                        .toDrawable(context.resources)
                }
            } ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                safe {
                    wallpaperManager.getDrawable(FLAG_LOCK)
                } ?: safe {
                    wallpaperManager.getBuiltInDrawable(FLAG_LOCK)
                }
            } else {
                safe { wallpaperManager.getBuiltInDrawable(FLAG_LOCK) }
            }
        } else {
            null
        }

        return listOf(home, lock, builtIn)
    }

    private inline fun <T> safe(action: () -> T): T? = runCatching { action() }
        .onFailure { it.makeLog("AndroidWallpapersProvider") }.getOrNull()

    private fun nameByIndex(index: Int): Int = when (index) {
        0 -> R.string.home_screen
        1 -> R.string.lock_screen
        else -> R.string.built_in
    }

}