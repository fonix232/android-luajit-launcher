package org.koreader.launcher.driver.epd.common

import android.view.View
import org.koreader.launcher.driver.EpdDriver

/**
 * No-op EPD driver used for [DeviceRegistry.unknown] — i.e., devices that are
 * not recognised. Reports platform "none" so callers know e-ink is unavailable.
 */
class FakeEPDController : EpdDriver {

    override val platform: String = "none"
    override val mode: String = "full-only"

    override val waveformFull: Int = 0
    override val waveformPartial: Int = 0
    override val waveformFullUi: Int = 0
    override val waveformPartialUi: Int = 0
    override val waveformFast: Int = 0

    override fun setEpdMode(view: View, mode: Int, delay: Long, x: Int, y: Int, width: Int, height: Int, epdMode: String?) {}
}
