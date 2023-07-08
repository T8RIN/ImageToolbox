package ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen.components

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class ColorPickerTile : TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(applicationContext, TransparentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }

}