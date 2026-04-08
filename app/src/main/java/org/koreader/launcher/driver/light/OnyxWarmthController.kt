package org.koreader.launcher.driver.light

import android.util.Log
import org.koreader.launcher.driver.DriverContext
import org.koreader.launcher.extensions.read
import org.koreader.launcher.extensions.write
import java.io.File

class OnyxWarmthController(@Suppress("UNUSED_PARAMETER") ctx: DriverContext) : org.koreader.launcher.driver.light.common.SysfsBacklightDriver() {

    override val platform: String = "onyx-warmth"
    companion object {
        private const val TAG = "BacklightDriver"
        private const val WHITE_FILE = "/sys/class/backlight/white/brightness"
        private const val WARMTH_FILE = "/sys/class/backlight/warm/brightness"
    }

    override val brightnessWritePath: String = WHITE_FILE
    override val maxBrightness: Int = 255
    override val hasWarmth: Boolean = true
    override val hasStandaloneWarmth: Boolean = true
    override val maxWarmth: Int = 255

    override fun getWarmth(): Int = File(WARMTH_FILE).read()

    override fun setWarmth(warmth: Int) {
        if (warmth !in minWarmth..maxWarmth) {
            Log.w(TAG, "setWarmth out of range: $warmth")
            return
        }
        Log.v(TAG, "setWarmth: $warmth")
        File(WARMTH_FILE).write(warmth)
    }
}
