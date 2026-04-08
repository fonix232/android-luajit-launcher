package org.koreader.launcher.driver.light.common

import android.content.ContentResolver
import android.provider.Settings
import android.util.Log
import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.driver.DriverContext

/**
 * Abstract base for drivers that control brightness through [Settings.System].
 *
 * A [ContentResolver] is required and must be supplied to the subclass
 * constructor — it is not passed per-call.
 *
 * [brightnessKey] defaults to [Settings.System.SCREEN_BRIGHTNESS]. Subclasses
 * using a custom key override it.
 *
 * [maxBrightness] defaults to 255; TolinoB300 overrides to 100.
 *
 * [needsPermission] defaults to false; TolinoB300 overrides to true.
 *
 * Warmth support is not provided here — subclasses with a warmth channel
 * (TolinoNtx via ioctl, TolinoRoot via sysfs+root, TolinoB300 via a second
 * Settings key) override the warmth properties and functions directly.
 *
 * Concrete implementations: TolinoNtx, TolinoNtxNoWarmth, TolinoRoot, TolinoB300.
 */
abstract class SystemSettingsBacklightDriver(
    ctx: DriverContext,
) : BacklightDriver {

    protected val resolver: ContentResolver = ctx.contentResolver

    protected open val brightnessKey: String = Settings.System.SCREEN_BRIGHTNESS

    override val maxBrightness: Int get() = 255

    override fun getBrightness(): Int = try {
        Settings.System.getInt(resolver, brightnessKey)
    } catch (e: Exception) {
        Log.w(TAG, e.toString())
        0
    }

    override fun setBrightness(brightness: Int) {
        if (brightness !in minBrightness..maxBrightness) {
            Log.w(TAG, "setBrightness out of range: $brightness (allowed $minBrightness..$maxBrightness)")
            return
        }
        Log.v(TAG, "setBrightness: $brightness")
        try {
            Settings.System.putInt(resolver, brightnessKey, brightness)
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
        }
    }

    companion object {
        private const val TAG = "BacklightDriver"
    }
}
