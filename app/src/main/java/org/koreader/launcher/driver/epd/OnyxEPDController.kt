/* Tested on Onyx Boox Nova 2 */

package org.koreader.launcher.driver.epd

import android.view.View

class OnyxEPDController : org.koreader.launcher.driver.epd.common.QualcommEpdDriver() {

    override val mode: String = "full-only"

    override val waveformFull: Int = EINK_WAVEFORM_UPDATE_FULL + EINK_WAVEFORM_MODE_WAIT + EINK_WAVEFORM_MODE_GC16
    override val waveformPartial: Int = EINK_WAVEFORM_UPDATE_PARTIAL + EINK_WAVEFORM_MODE_GC16
    override val waveformFullUi: Int = EINK_WAVEFORM_UPDATE_FULL + EINK_WAVEFORM_MODE_REAGL
    override val waveformPartialUi: Int = EINK_WAVEFORM_UPDATE_PARTIAL + EINK_WAVEFORM_MODE_GC16
    override val waveformFast: Int = EINK_WAVEFORM_UPDATE_PARTIAL + EINK_WAVEFORM_MODE_DU

    override val waveformDelay: Int = EINK_WAVEFORM_DELAY
    override val waveformDelayUi: Int = EINK_WAVEFORM_DELAY_UI
    override val waveformDelayFast: Int = EINK_WAVEFORM_DELAY_FAST

    override fun setEpdMode(view: View, mode: Int, delay: Long, x: Int, y: Int, width: Int, height: Int, epdMode: String?) {
        requestEpdMode(view, mode, delay, x, y, width, height)
    }
}
