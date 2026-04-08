package org.koreader.launcher.driver.epd.common

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import org.koreader.launcher.driver.EpdDriver

/**
 * Abstract EPD driver base for Rockchip RK35xx SoC devices.
 *
 * Covers: Xiaomi 7 Reader, Xiaomi Duokan Pro II, Xiaomi Ereader Pro II.
 *
 * Refresh is triggered via the `android.os.EinkManager` system service
 * (`sendOneFullFrame`), obtained through `view.context.getSystemService("eink")`.
 * A live [View] is required to access the Context — see [needsView].
 *
 * All waveform codes are uniformly [EPD_AUTO] (0): the platform chooses the
 * appropriate refresh mode based on the System UI setting (Clear / Balanced / Quick).
 */
abstract class Rk35xxEpdDriver : EpdDriver {

    override val platform: String = "rockchip"
    override val needsView: Boolean = true

    companion object {
        private const val TAG = "EpdDriver"

        const val EPD_AUTO = 0

        @SuppressLint("WrongConstant")
        fun requestEpdMode(view: View): Boolean = try {
            val einkManagerClass = Class.forName("android.os.EinkManager")
            val einkManager: Any? = view.context.getSystemService("eink")
            val sendOneFullFrame = einkManagerClass.getDeclaredMethod("sendOneFullFrame")
            sendOneFullFrame.invoke(einkManager)
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            Log.e(TAG, e.stackTraceToString())
            false
        }
    }
}
