package org.koreader.launcher.driver.epd.common

import android.util.Log
import android.view.View
import org.koreader.launcher.driver.EpdDriver
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Abstract EPD driver base for Rockchip RK30xx (RK3026) SoC devices.
 *
 * Covers: Boyue T61/T62 clones.
 *
 * Refresh uses a vendor-injected `View.EINK_MODE` enum and
 * `View.requestEpdMode(EINK_MODE, Boolean)` method, both accessed via reflection.
 * The method is invoked on a live [View] instance. Despite this, [needsView] is
 * `false` — unlike NTX drivers, the caller is not responsible for triggering the
 * exact view that needs refreshing; any accessible View will do.
 *
 * Constants mirror those in [deviceOld.epd.rockchip.RK30xxEPDController].
 * Based on https://github.com/unwmun/refreshU — thanks to @unwmun.
 */
abstract class Rk30xxEpdDriver : EpdDriver {

    override val platform: String = "rockchip"
    override val needsView: Boolean = false

    companion object {
        private const val TAG = "EpdDriver"

        const val EINK_MODE_FULL = 1
        const val EINK_MODE_PARTIAL = 2
        const val EINK_MODE_FULL_UI = 4
        const val EINK_MODE_PARTIAL_UI = 4
        const val EINK_MODE_FAST = 3
        const val EINK_WAVEFORM_DELAY = 0

        private var eInkEnum: Class<Enum<*>>? = null
        private var updateEpdMethod: Method? = null

        init {
            try {
                @Suppress("UNCHECKED_CAST")
                eInkEnum = Class.forName("android.view.View\$EINK_MODE") as Class<Enum<*>>
                updateEpdMethod = View::class.java.getMethod(
                    "requestEpdMode",
                    eInkEnum, Boolean::class.javaPrimitiveType,
                )
            } catch (e: ClassNotFoundException) {
                Log.e(TAG, e.toString())
            } catch (e: NoSuchMethodException) {
                Log.e(TAG, e.toString())
            }
        }

        fun requestEpdMode(view: View, mode: String, flag: Boolean): Boolean = try {
            updateEpdMethod!!.invoke(view, stringToEnum(mode), flag)
            true
        } catch (e: IllegalAccessException) {
            Log.e(TAG, e.toString())
            false
        } catch (e: InvocationTargetException) {
            Log.e(TAG, e.toString())
            false
        }

        private fun stringToEnum(str: String): Any {
            val values = eInkEnum!!.enumConstants as Array<Enum<*>>
            return values.first { it.name == str }
        }
    }
}
