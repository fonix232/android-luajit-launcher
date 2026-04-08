/* Tolino-like without REGAL */

package org.koreader.launcher.driver.epd

import android.view.View

class OldTolinoEPDController : org.koreader.launcher.driver.epd.common.NtxEpdDriver() {

    override val mode: String = "all"

    override val waveformFull: Int = EINK_WAVEFORM_UPDATE_FULL + EINK_WAVEFORM_MODE_GC16
    override val waveformPartial: Int = EINK_WAVEFORM_UPDATE_PARTIAL + EINK_WAVEFORM_MODE_GC16
    override val waveformFullUi: Int = EINK_WAVEFORM_UPDATE_FULL + EINK_WAVEFORM_MODE_GL16
    override val waveformPartialUi: Int = EINK_WAVEFORM_UPDATE_PARTIAL + EINK_WAVEFORM_MODE_GL16
    override val waveformFast: Int = EINK_WAVEFORM_UPDATE_PARTIAL + EINK_WAVEFORM_MODE_DU

    override fun setEpdMode(view: View, mode: Int, delay: Long, x: Int, y: Int, width: Int, height: Int, epdMode: String?) {
        requestEpdMode(view, mode, delay, x, y, width, height)
    }
}
