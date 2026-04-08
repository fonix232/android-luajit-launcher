package org.koreader.launcher.driver.epd.common

import android.util.Log
import org.koreader.launcher.driver.EpdDriver
import java.util.Locale

/**
 * Abstract EPD driver base for Rockchip RK33xx SoC devices.
 *
 * Covers: Boyue RK33xx clones (Likebook Mimas, Mars, Muses…).
 *
 * Refresh uses a vendor-injected static `View.setByEinkUpdateMode(Int)` reached
 * via reflection. No [View] instance is needed — the call is a static method
 * invocation on the `android.view.View` class itself.
 *
 * Only full-screen mode has been verified in the field; [requestEpdMode] always
 * maps to [EINK_MODE_FULL] regardless of the [epdMode] string passed in.
 *
 * Constants mirror those in [deviceOld.epd.rockchip.RK33xxEPDController].
 * Based on https://github.com/koreader/koreader/issues/4595 — thanks to @carlinux.
 */
abstract class Rk33xxEpdDriver : EpdDriver {

    override val platform: String = "rockchip"

    companion object {
        private const val TAG = "EpdDriver"

        const val EINK_MODE_FULL = 1
        const val EINK_MODE_PARTIAL = 2
        const val EINK_MODE_FULL_UI = 4
        const val EINK_MODE_PARTIAL_UI = 4
        const val EINK_MODE_FAST = 3
        const val EINK_WAVEFORM_DELAY = 0

        fun requestEpdMode(epdMode: String): Boolean = try {
            Class.forName("android.view.View").getMethod(
                "setByEinkUpdateMode", Integer.TYPE,
            ).invoke(null, getEpdMode(epdMode))
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }

        private fun getEpdMode(epdMode: String): Int {
            val mode = EINK_MODE_FULL
            Log.v(TAG, String.format(Locale.US, "Requesting %s: %d", epdMode, mode))
            return mode
        }
    }
}
