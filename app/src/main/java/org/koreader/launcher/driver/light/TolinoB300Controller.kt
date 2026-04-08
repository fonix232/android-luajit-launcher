// Light and warmth controller for B300 Tolino devices (Epos 3, Vision 6, Shine 4)

package org.koreader.launcher.driver.light

import android.content.Context
import android.provider.Settings
import android.util.Log
import org.koreader.launcher.driver.DriverContext

open class TolinoB300Controller(ctx: DriverContext) : org.koreader.launcher.driver.light.common.SystemSettingsBacklightDriver(ctx) {

    override val platform: String = "tolino"
    companion object {
        private const val TAG = "BacklightDriver"
        private const val SCREEN_BRIGHTNESS_COLOR = "screen_brightness_color"
    }

    private val context: Context = ctx.context

    override val brightnessKey: String = "screen_brightness"
    override val maxBrightness: Int = 100
    override val needsPermission: Boolean = true
    override val hasWarmth: Boolean = true
    override val maxWarmth: Int = 10

    /** Override to true in subclasses where warmth scale is inverted (Vision 6, Shine 4). */
    protected open val invertedWarmth: Boolean = false

    override fun getWarmth(): Int {
        return try {
            val raw = Settings.System.getInt(resolver, SCREEN_BRIGHTNESS_COLOR)
            if (invertedWarmth) maxWarmth - raw else raw
        } catch (e: Exception) { Log.w(TAG, e.toString()); 0 }
    }

    override fun setBrightness(brightness: Int) {
        if (!checkPermission()) return
        super.setBrightness(brightness)
    }

    override fun setWarmth(warmth: Int) {
        if (!checkPermission()) return
        if (warmth !in minWarmth..maxWarmth) {
            Log.w(TAG, "warmth value out of range: $warmth")
            return
        }
        val warmthToSet = if (invertedWarmth) maxWarmth - warmth else warmth
        Log.v(TAG, "setWarmth: $warmth (actual: $warmthToSet)")
        try {
            Settings.System.putInt(resolver, SCREEN_BRIGHTNESS_COLOR, warmthToSet)
            // Workaround: toggle brightness to force warmth refresh
            val current = getBrightness()
            Settings.System.putInt(resolver, brightnessKey, current + 1)
            Settings.System.putInt(resolver, brightnessKey, current)
        } catch (e: Exception) { Log.w(TAG, "$e") }
    }

    private fun checkPermission(): Boolean =
        Settings.System.canWrite(context).also { canWrite ->
            if (!canWrite) Log.w(TAG, "WRITE_SETTINGS permission not granted.")
        }
}

