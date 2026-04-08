package org.koreader.launcher.driver.epd.common

import android.util.Log
import android.view.View
import org.koreader.launcher.driver.EpdDriver
import java.util.Locale

/**
 * Abstract EPD driver base for NTX (FreeScale/NXP iMX) chipset devices.
 *
 * Covers: Crema, Nook (GL3, GL4e), Tolino (Vision2/Shine3), OldTolino.
 *
 * The refresh entry-point is a vendor-patched [View.postInvalidateDelayed]
 * reached via reflection. A live [View] instance is required — see [needsView].
 *
 * Constants mirror those in [deviceOld.epd.freescale.NTXEPDController].
 */
abstract class NtxEpdDriver : EpdDriver {

    override val platform: String = "freescale"
    override val needsView: Boolean = true

    companion object {
        private const val TAG = "EpdDriver"

        const val EINK_WAVEFORM_UPDATE_FULL = 32
        const val EINK_WAVEFORM_UPDATE_PARTIAL = 0
        const val EINK_WAVEFORM_MODE_DU = 1
        const val EINK_WAVEFORM_MODE_GC16 = 2
        const val EINK_WAVEFORM_MODE_GL16 = 6
        const val EINK_WAVEFORM_MODE_GLR16 = 7
        const val EINK_WAVEFORM_DELAY = 0

        fun requestEpdMode(
            view: View,
            mode: Int, delay: Long,
            x: Int, y: Int, width: Int, height: Int,
        ): Boolean = try {
            Class.forName("android.view.View").getMethod(
                "postInvalidateDelayed",
                java.lang.Long.TYPE,
                Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE,
            ).invoke(view, delay, x, y, width, height, mode)
            Log.i(TAG, String.format(Locale.US,
                "requested eink refresh, type: %d x:%d y:%d w:%d h:%d",
                mode, x, y, width, height))
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }
    }
}
