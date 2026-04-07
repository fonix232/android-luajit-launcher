package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SonyRp1CharacterizationTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "sony",
            brand = "sony",
            model = "dpt-rp1",
            device = "dpt-rp1",
            product = "dpt-rp1",
            hardware = "msm8996",
        )
    }

    @Test
    fun characterizes_sony_rp1_selection_and_quirks() {
        val facade = Device(DeviceCharacterizationTestSupport.buildActivity())

        assertThat(DeviceInfo.ID.name).isEqualTo("SONY_RP1")
        assertThat(facade.epd.javaClass.simpleName).isEqualTo("NookEPDController")
        assertThat(facade.lights.javaClass.simpleName).isEqualTo("GenericController")
        assertThat(facade.needsWakelocks).isTrue()
        assertThat(facade.hasLights).isFalse()
    }
}