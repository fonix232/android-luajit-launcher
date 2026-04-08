package org.koreader.launcher.driver.light.common

import android.content.ContentResolver
import android.provider.Settings
import android.util.Log
import android.view.Window
import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.driver.DriverContext

/**
 * Abstract base for drivers that control brightness through the Android
 * [Window] screen-brightness attribute.
 *
 * A [Window] and [ContentResolver] must be supplied to the subclass constructor.
 * The [Window] is used for reading/writing the brightness float attribute;
 * the [ContentResolver] is used as a fallback when the window attribute
 * is -1 (system-controlled).
 *
 * The window attribute accepts a float in `[-1.0, 1.0]`, where -1 means
 * "follow system brightness". This base maps between that float and the
 * integer range `minBrightness..maxBrightness` used by the rest of the
 * driver API.
 *
 * [hasFallback] is true — the window-brightness path is always available
 * on any Android device regardless of hardware.
 *
 * Only [GenericController] currently uses this base.
 */
abstract class WindowBacklightDriver(
    ctx: DriverContext,
) : BacklightDriver {

    protected val window: Window = ctx.window
    protected val resolver: ContentResolver = ctx.contentResolver

    /**
     * Lower bound defaults to 1, not 0: mapping 0 to the window attribute
     * float produces 0.0 which means "follow system setting" — a different
     * semantic from "minimum brightness".
     */
    override val minBrightness: Int get() = 1
    override val maxBrightness: Int get() = 255

    override val hasFallback: Boolean get() = true

    override fun getBrightness(): Int {
        val windowBrightness = window.attributes.screenBrightness
        val mapped = (windowBrightness * (maxBrightness - minBrightness)).toInt() + minBrightness
        return if (mapped < minBrightness) {
            // Window attribute is -1 (system-controlled) — fall back to Settings
            try {
                Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
            } catch (e: Exception) {
                Log.w(TAG, e.toString())
                0
            }
        } else {
            mapped
        }
    }

    override fun setBrightness(brightness: Int) {
        Log.v(TAG, "setBrightness: $brightness")
        val level: Float? = when (brightness) {
            0 -> 0.0f   // sentinel: reset to system-controlled
            !in minBrightness..maxBrightness -> {
                Log.w(TAG, "setBrightness out of range: $brightness (allowed $minBrightness..$maxBrightness)")
                null
            }
            else -> (brightness - minBrightness).toFloat() / (maxBrightness - minBrightness)
        }
        level?.let { value ->
            try {
                val params = window.attributes
                params.screenBrightness = value
                window.attributes = params
            } catch (e: Exception) {
                Log.w(TAG, e.toString())
            }
        }
    }

    companion object {
        private const val TAG = "BacklightDriver"
    }
}
