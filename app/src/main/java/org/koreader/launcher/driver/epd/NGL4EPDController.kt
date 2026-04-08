/* Tested on Nook Glowlight 4e */

package org.koreader.launcher.driver.epd

import android.util.Log
import android.view.View
import java.util.Locale

class NGL4EPDController : org.koreader.launcher.driver.epd.common.NtxEpdDriver() {

    override val mode: String = "full-only"

    @Suppress("unused")
    companion object {
        private const val NGL4TAG = "NGL4"

        // constants taken as is from sunxi-kobo.h (NGL4 prefix inserted for names)
        const val NGL4_EINK_INIT_MODE = 0x01
        const val NGL4_EINK_DU_MODE = 0x02
        const val NGL4_EINK_GC16_MODE = 0x04
        const val NGL4_EINK_GC4_MODE = 0x08
        const val NGL4_EINK_A2_MODE = 0x10
        const val NGL4_EINK_GL16_MODE = 0x20
        const val NGL4_EINK_GLR16_MODE = 0x40
        const val NGL4_EINK_GLD16_MODE = 0x80
        const val NGL4_EINK_GU16_MODE = 0x84
        const val NGL4_EINK_GCK16_MODE = 0x90
        const val NGL4_EINK_GLK16_MODE = 0x94
        const val NGL4_EINK_CLEAR_MODE = 0x88
        const val NGL4_EINK_GC4L_MODE = 0x8C
        const val NGL4_EINK_GCC16_MODE = 0xA0
        const val NGL4_EINK_PARTIAL_MODE = 0x400
        const val NGL4_EINK_AUTO_MODE = 0x8000
        const val NGL4_EINK_NEGATIVE_MODE = 0x10000
        const val NGL4_EINK_REGAL_MODE = 0x80000
        const val NGL4_EINK_GAMMA_CORRECT = 0x200000
        const val NGL4_EINK_MONOCHROME = 0x400000
        const val NGL4_EINK_DITHERING_Y1 = 0x01800000
        const val NGL4_EINK_DITHERING_Y4 = 0x02800000
        const val NGL4_EINK_DITHERING_SIMPLE = 0x04800000
        const val NGL4_EINK_DITHERING_NTX_Y1 = 0x08800000
        const val NGL4_EINK_NO_MERGE = Integer.MIN_VALUE // 0x80000000
    }

    // the mode constants below are effectively not used because mode returns something other than "all"
    override val waveformFull: Int = NGL4_EINK_NO_MERGE + NGL4_EINK_GC16_MODE
    override val waveformPartial: Int = NGL4_EINK_GU16_MODE
    override val waveformFullUi: Int = NGL4_EINK_NO_MERGE + org.koreader.launcher.driver.epd.common.NtxEpdDriver.EINK_WAVEFORM_MODE_GLR16
    override val waveformPartialUi: Int = NGL4_EINK_GLR16_MODE
    override val waveformFast: Int = NGL4_EINK_GU16_MODE

    // waveformDelay is the only effectively used delay because mode is not "all"
    override val waveformDelay: Int = 0
    override val waveformDelayUi: Int = 0
    override val waveformDelayFast: Int = 0

    override fun setEpdMode(view: View, mode: Int, delay: Long, x: Int, y: Int, width: Int, height: Int, epdMode: String?) {
        Log.i(NGL4TAG, String.format(Locale.US,
            "calling requestEpdMode: type:%d delay: %d x:%d y:%d w:%d h:%d",
            mode, delay, x, y, width, height))
        requestEpdMode(view, mode, delay, x, y, width, height)
    }
}
