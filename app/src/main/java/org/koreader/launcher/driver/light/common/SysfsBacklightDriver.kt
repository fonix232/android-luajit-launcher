package org.koreader.launcher.driver.light.common

import android.util.Log
import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.extensions.read
import org.koreader.launcher.extensions.write
import java.io.File

/**
 * Abstract base for drivers that control brightness by reading and writing
 * Linux sysfs nodes under `/sys/class/backlight/`.
 *
 * No Android context is required at call time — all I/O goes directly to the
 * filesystem. Subclasses that also need a Context (e.g. for permissions) should
 * accept it in their own constructor.
 *
 * Subclasses must provide:
 * - [brightnessWritePath] — the sysfs path to write brightness values to.
 * - [maxBrightness]
 *
 * Subclasses may override:
 * - [brightnessReadPath] — if the node used for reading differs from the one
 *   used for writing (e.g. `actual_brightness` vs `brightness`). Defaults to
 *   [brightnessWritePath].
 *
 * Warmth support is not provided here. Subclasses that have a warmth sysfs
 * node override [hasWarmth], [hasStandaloneWarmth], [minWarmth], [maxWarmth],
 * [getWarmth], and [setWarmth] directly.
 *
 * Concrete implementations: OnyxC67, OnyxColor, OnyxWarmth, OnyxPalma2Pro.
 */
abstract class SysfsBacklightDriver : BacklightDriver {

    protected abstract val brightnessWritePath: String

    /** Override when the readable brightness node differs from the writable one. */
    protected open val brightnessReadPath: String get() = brightnessWritePath

    override fun getBrightness(): Int = File(brightnessReadPath).read()

    override fun setBrightness(brightness: Int) {
        if (brightness !in minBrightness..maxBrightness) {
            Log.w(TAG, "setBrightness out of range: $brightness (allowed $minBrightness..$maxBrightness)")
            return
        }
        Log.v(TAG, "setBrightness: $brightness")
        File(brightnessWritePath).write(brightness)
    }

    companion object {
        private const val TAG = "BacklightDriver"
    }
}
