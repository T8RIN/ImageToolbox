package ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen.components

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import ru.tech.imageresizershrinker.R

@RequiresApi(Build.VERSION_CODES.N)
class CaptureTile : TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(applicationContext, ScreenshotLauncher::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        if (Build.VERSION.SDK_INT >= 34) {
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    override fun onStartListening() {
        val tile = qsTile
        tile.label = getString(R.string.edit_screenshot)
        tile.icon = Icon.createWithResource(this, R.drawable.outline_app_registration_24)
        tile.updateTile()
    }

}