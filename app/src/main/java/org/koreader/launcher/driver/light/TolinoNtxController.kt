/* Special controller for Tolino Vision5
 * see https://github.com/koreader/android-luajit-launcher/pull/382
 *
 * Original Controller by @zwim, see "./TolinoRootController.kt"
 */

package org.koreader.launcher.driver.light

import android.util.Log
import org.koreader.launcher.driver.DriverContext
import org.koreader.launcher.driver.Ioctl
import org.koreader.launcher.extensions.read
import java.io.File

class TolinoNtxController(ctx: DriverContext) : org.koreader.launcher.driver.light.common.SystemSettingsBacklightDriver(ctx) {

    override val platform: String = "tolino"
    companion object {
        private const val TAG = "BacklightDriver"
        private const val NTX_IO_FILE = "/dev/ntx_io"
        private const val NTX_WARMTH_ID = 248
        // can also be read by "/sys/class/backlight/lm3630a_led/max_color" on Vision5
        private const val WARMTH_MAX_VAL = 10
        private const val COLOR_FILE = "/sys/class/backlight/lm3630a_led/color"
    }

    private val ioctl = Ioctl()

    // store the current warmth value, because in some models (Vision5) it cannot be fetched
    private var currentWarmth: Int? = null

    override val brightnessKey: String = "screen_brightness"
    override val maxBrightness: Int = 255
    override val hasWarmth: Boolean = true
    override val maxWarmth: Int = WARMTH_MAX_VAL

    override fun getWarmth(): Int {
        if (currentWarmth == null) {
            currentWarmth = WARMTH_MAX_VAL - File(COLOR_FILE).read()
        }
        return currentWarmth ?: WARMTH_MAX_VAL
    }

    override fun setWarmth(warmth: Int) {
        if (warmth !in minWarmth..maxWarmth) {
            Log.w(TAG, "warmth value out of range: $warmth")
            return
        }
        Log.v(TAG, "setWarmth: $warmth of $WARMTH_MAX_VAL")
        if (ioctl.io(NTX_IO_FILE, NTX_WARMTH_ID, (warmth - WARMTH_MAX_VAL) * -1)) {
            currentWarmth = warmth
        }
    }
}
