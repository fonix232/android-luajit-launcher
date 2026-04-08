/* Special controller for Tolino Epos/Epos2.
 * see https://github.com/koreader/koreader/pull/6332
 *
 * Thanks to @zwim
 */

package org.koreader.launcher.driver.light

import android.util.Log
import org.koreader.launcher.driver.DriverContext
import org.koreader.launcher.extensions.read
import org.koreader.launcher.extensions.write
import java.io.File

class TolinoRootController(ctx: DriverContext) : org.koreader.launcher.driver.light.common.SystemSettingsBacklightDriver(ctx) {

    override val platform: String = "tolino"
    companion object {
        private const val TAG = "BacklightDriver"
        private const val ACTUAL_BRIGHTNESS_FILE = "/sys/class/backlight/mxc_msp430_fl.0/actual_brightness"
        private const val COLOR_FILE_EPOS2 = "/sys/class/backlight/tlc5947_bl/color"
        private const val COLOR_FILE_VISION4HD = "/sys/class/backlight/lm3630a_led/color"
        private val COLOR_FILE = if (File(COLOR_FILE_VISION4HD).exists())
            COLOR_FILE_VISION4HD else COLOR_FILE_EPOS2
    }

    override val brightnessKey: String = "screen_brightness"
    override val maxBrightness: Int = 255
    override val hasWarmth: Boolean = true
    override val maxWarmth: Int = 10

    override fun enableFrontlight(): Int {
        val startBrightness = getBrightness()
        val actualBrightnessFile = File(ACTUAL_BRIGHTNESS_FILE)
        val startBrightnessFromFile = try { actualBrightnessFile.readText().trim().toInt() }
            catch (e: Exception) { Log.w(TAG, "$e"); -1 }

        // Toggle brightness slightly so the system driver detects a change
        if (startBrightness > maxBrightness / 2)
            setBrightness(startBrightness - (maxBrightness / 100 + 1))
        else
            setBrightness(startBrightness + (maxBrightness / 100 + 1))

        Thread.sleep(80)

        val actualBrightnessFromFile = try { actualBrightnessFile.readText().trim().toInt() }
            catch (e: Exception) { Log.w(TAG, "$e"); -1 }

        setBrightness(startBrightness)

        return if (startBrightnessFromFile == actualBrightnessFromFile) {
            try {
                Runtime.getRuntime().exec("su -c input keyevent KEYCODE_BUTTON_A && echo OK")
                1
            } catch (e: Exception) { e.printStackTrace(); 0 }
        } else { 1 }
    }

    override fun getWarmth(): Int = maxWarmth - File(COLOR_FILE).read()

    override fun setWarmth(warmth: Int) {
        if (warmth !in minWarmth..maxWarmth) {
            Log.w(TAG, "warmth value out of range: $warmth")
            return
        }
        val colorFile = File(COLOR_FILE)
        Log.v(TAG, "setWarmth: $warmth")
        try {
            if (!colorFile.canWrite()) {
                Runtime.getRuntime().exec("su -c chmod 666 $COLOR_FILE")
            }
            colorFile.write(maxWarmth - warmth)
        } catch (e: Exception) {
            Log.w(TAG, "$e")
        }
    }
}
