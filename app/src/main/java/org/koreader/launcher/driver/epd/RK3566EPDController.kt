package org.koreader.launcher.driver.epd

import android.view.View

class RK3566EPDController : org.koreader.launcher.driver.epd.common.Rk35xxEpdDriver() {

    override val mode: String = "full-only"

    override val waveformFull: Int = EPD_AUTO
    override val waveformPartial: Int = EPD_AUTO
    override val waveformFullUi: Int = EPD_AUTO
    override val waveformPartialUi: Int = EPD_AUTO
    override val waveformFast: Int = EPD_AUTO

    override fun setEpdMode(view: View, mode: Int, delay: Long, x: Int, y: Int, width: Int, height: Int, epdMode: String?) {
        requestEpdMode(view)
    }
}
