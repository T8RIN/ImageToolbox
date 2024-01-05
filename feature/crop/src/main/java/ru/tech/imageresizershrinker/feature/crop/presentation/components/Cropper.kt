package ru.tech.imageresizershrinker.feature.crop.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropProperties
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker

@Composable
fun Cropper(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    crop: Boolean,
    imageCropStarted: () -> Unit,
    imageCropFinished: (Bitmap) -> Unit,
    cropProperties: CropProperties,
) {
    Column {
        AnimatedContent(
            targetState = (cropProperties.aspectRatio != AspectRatio.Original) to bitmap,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { (fixedAspectRatio, bitmap) ->
            val bmp = remember(bitmap) { bitmap.asImageBitmap() }
            ImageCropper(
                backgroundModifier = Modifier.transparencyChecker(),
                imageBitmap = bmp,
                contentDescription = null,
                cropProperties = cropProperties.copy(fixedAspectRatio = fixedAspectRatio),
                onCropStart = {
                    imageCropStarted()
                },
                crop = crop,
                cropStyle = CropDefaults.style(
                    overlayColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onCropSuccess = { image ->
                    imageCropFinished(image.asAndroidBitmap())
                },
            )
        }
    }
}