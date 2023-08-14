package ru.tech.imageresizershrinker.presentation.root.widget.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import ru.tech.imageresizershrinker.presentation.crash_screen.CrashActivity
import ru.tech.imageresizershrinker.presentation.root.utils.exception.GlobalExceptionHandler

@AndroidEntryPoint
open class M3Activity : AppCompatActivity() {

//    private lateinit var settingsState: SettingsState
//
//    override fun attachBaseContext(newBase: Context) {
//        settingsState = runBlocking {
//            EntryPointAccessors
//                .fromApplication(newBase, SettingsStateEntryPoint::class.java)
//                .getSettingsStateUseCase()
//        }
//        super.attachBaseContext(newBase.adjustFontSize(settingsState.fontScale))
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
//        adjustFontSize(settingsState.fontScale)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
    }

}

//@EntryPoint
//@InstallIn(dagger.hilt.components.SingletonComponent::class)
//private interface SettingsStateEntryPoint {
//    val getSettingsStateUseCase: GetSettingsStateUseCase
//}