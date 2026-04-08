package org.koreader.launcher.driver.epd.common

import android.util.Log
import android.view.View
import org.koreader.launcher.driver.EpdDriver
import java.util.Locale

/**
 * Abstract EPD driver base for Qualcomm SoC devices.
 *
 * Covers: Onyx Boox Nova 2 and related Qualcomm-based Onyx devices.
 *
 * Refresh is triggered via `View.refreshScreen` + `View.setWaveformAndScheme`
 * reached by reflection. A [View] reference is passed to the refresh method, but
 * [needsView] is `false` — the driver does not require the specific view being
 * refreshed; any live View from the application will do.
 *
 * Constants mirror those in [deviceOld.epd.qualcomm.QualcommEPDController].
 * See https://github.com/koreader/android-luajit-launcher/pull/250#issuecomment-711443457
 */
abstract class QualcommEpdDriver : EpdDriver {

    override val platform: String = "qualcomm"
    override val needsView: Boolean = false

    companion object {
        private const val TAG = "EpdDriver"

        const val EINK_WAVEFORM_UPDATE_FULL = 32
        const val EINK_WAVEFORM_UPDATE_PARTIAL = 0
        const val EINK_WAVEFORM_MODE_WAIT = 64
        const val EINK_WAVEFORM_MODE_DU = 1
        const val EINK_WAVEFORM_MODE_GC16 = 2
        const val EINK_WAVEFORM_MODE_REAGL = 6
        const val EINK_WAVEFORM_DELAY = 250
        const val EINK_WAVEFORM_DELAY_UI = 100
        const val EINK_WAVEFORM_DELAY_FAST = 0

        private fun preventSystemRefresh(): Boolean = try {
            // Sets UpdateMode and UpdateScheme to None
            // (EpdController.setSystemUpdateModeAndScheme in onyxsdk)
            Class.forName("android.view.View").getMethod(
                "setWaveformAndScheme",
                Integer.TYPE, Integer.TYPE, Integer.TYPE,
            ).invoke(null, 5, 1, 0)
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }

        fun requestEpdMode(
            targetView: View,
            mode: Int, delay: Long,
            x: Int, y: Int, width: Int, height: Int,
        ): Boolean = try {
            preventSystemRefresh()
            // EpdController.refreshScreenRegion in onyxsdk
            val refreshScreen = Class.forName("android.view.View").getMethod(
                "refreshScreen",
                Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE,
            )
            object : Thread() {
                override fun run() {
                    sleep(delay)
                    try {
                        refreshScreen.invoke(targetView, x, y, width, height, mode)
                        Log.i(TAG, String.format(Locale.US,
                            "requested eink refresh, type: %d x:%d y:%d w:%d h:%d",
                            mode, x, y, width, height))
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }.start()
            true
        } catch (e: Exception) {
            false
        }
    }
}
