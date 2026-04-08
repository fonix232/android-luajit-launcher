/* generic EPD Controller for Android devices,
 * based on https://github.com/unwmun/refreshU */

package org.koreader.launcher.driver.epd

import android.view.View

class RK3026EPDController : org.koreader.launcher.driver.epd.common.Rk30xxEpdDriver() {

    override val mode: String = "full-only"

    override val waveformFull: Int = EINK_MODE_FULL
    override val waveformPartial: Int = EINK_MODE_PARTIAL
    override val waveformFullUi: Int = EINK_MODE_FULL_UI
    override val waveformPartialUi: Int = EINK_MODE_PARTIAL_UI
    override val waveformFast: Int = EINK_MODE_FAST

    override fun setEpdMode(view: View, mode: Int, delay: Long, x: Int, y: Int, width: Int, height: Int, epdMode: String?) {
        requestEpdMode(view, epdMode!!, true)
    }
}
