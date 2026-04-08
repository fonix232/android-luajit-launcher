/* Controller for Onyx Boox Palma 2 Pro with brightness and warmth support.
 * Uses onyx_bl_br for brightness and onyx_bl_ct for warmth (color temperature).
 * Dynamically reads max values from sysfs.
 */

package org.koreader.launcher.driver.light

import android.util.Log
import org.koreader.launcher.driver.DriverContext
import org.koreader.launcher.extensions.read
import org.koreader.launcher.extensions.write
import java.io.File

class OnyxPalma2ProController(@Suppress("UNUSED_PARAMETER") ctx: DriverContext) : org.koreader.launcher.driver.light.common.SysfsBacklightDriver() {

    override val platform: String = "onyx-palma2pro"
    companion object {
        private const val TAG = "BacklightDriver"
        private const val DEFAULT_BRIGHTNESS_MAX = 255
        private const val DEFAULT_WARMTH_MAX = 32
        private const val BRIGHTNESS_FILE = "/sys/class/backlight/onyx_bl_br/brightness"
        private const val BRIGHTNESS_MAX_FILE = "/sys/class/backlight/onyx_bl_br/max_brightness"
        private const val WARMTH_FILE = "/sys/class/backlight/onyx_bl_ct/brightness"
        private const val WARMTH_MAX_FILE = "/sys/class/backlight/onyx_bl_ct/max_brightness"
    }

    override val brightnessWritePath: String = BRIGHTNESS_FILE
    override val hasWarmth: Boolean = true
    override val hasStandaloneWarmth: Boolean = true

    override val maxBrightness: Int by lazy {
        try { File(BRIGHTNESS_MAX_FILE).read().takeIf { it > 0 } ?: DEFAULT_BRIGHTNESS_MAX }
        catch (e: Exception) { Log.w(TAG, "Failed to read max brightness: $e"); DEFAULT_BRIGHTNESS_MAX }
    }

    override val maxWarmth: Int by lazy {
        try { File(WARMTH_MAX_FILE).read().takeIf { it > 0 } ?: DEFAULT_WARMTH_MAX }
        catch (e: Exception) { Log.w(TAG, "Failed to read max warmth: $e"); DEFAULT_WARMTH_MAX }
    }

    override fun getWarmth(): Int = try { File(WARMTH_FILE).read() }
        catch (e: Exception) { Log.w(TAG, "Failed to read warmth: $e"); 0 }

    override fun setWarmth(warmth: Int) {
        if (warmth !in minWarmth..maxWarmth) {
            Log.w(TAG, "setWarmth out of range: $warmth (max: $maxWarmth)")
            return
        }
        Log.v(TAG, "setWarmth: $warmth")
        try { File(WARMTH_FILE).write(warmth) }
        catch (e: Exception) { Log.e(TAG, "Failed to set warmth: $e") }
    }
}
