/* handle frontlight within the activity, without affecting other activities */

package org.koreader.launcher.driver.light

import android.util.Log
import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.driver.DriverContext

class BoyueS62RootController(ctx: DriverContext) : BacklightDriver {
    override val platform: String = "boyue-s62-root"

    companion object {
        private const val TAG = "BacklightDriver"
        private const val BRIGHTNESS_SYSFS = "/sys/class/backlight/rk28_bl_warm/brightness"
    }

    private val resolver = ctx.contentResolver

    override val hasFallback: Boolean = true
    override val maxBrightness: Int = 254

    override fun getBrightness(): Int {
        return try {
            android.provider.Settings.System.getInt(resolver, "boyue_warm_light")
        } catch (e: Exception) {
            Log.w(TAG, "$e")
            0
        }
    }

    override fun setBrightness(brightness: Int) {
        if (brightness !in minBrightness..maxBrightness) {
            Log.w(TAG, "brightness value out of range: $brightness")
            return
        }
        Log.v(TAG, "setBrightness: $brightness")
        try {
            Runtime.getRuntime().exec("su -c echo $brightness > $BRIGHTNESS_SYSFS")
        } catch (e: Exception) {
            Log.w(TAG, "$e")
        }
    }
}
