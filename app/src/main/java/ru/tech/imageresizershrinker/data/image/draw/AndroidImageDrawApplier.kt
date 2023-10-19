package ru.tech.imageresizershrinker.data.image.draw

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.exifinterface.media.ExifInterface
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.draw.DrawBehavior
import ru.tech.imageresizershrinker.domain.image.draw.ImageDrawApplier
import ru.tech.imageresizershrinker.domain.image.draw.PathPaint
import javax.inject.Inject

class AndroidImageDrawApplier @Inject constructor(
    private val context: Context,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ImageDrawApplier<Bitmap, Path, Color> {

    override suspend fun applyDrawToImage(
        drawBehavior: DrawBehavior,
        pathPaints: List<PathPaint<Path, Color>>,
        imageUri: String
    ): Bitmap {
        TODO("Not yet implemented")
    }

    override suspend fun applyEraseToImage(
        pathPaints: List<PathPaint<Path, Color>>,
        imageUri: String
    ): Bitmap {
        TODO("Not yet implemented")
    }


}