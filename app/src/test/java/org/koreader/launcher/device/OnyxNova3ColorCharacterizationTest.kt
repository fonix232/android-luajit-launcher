package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnyxNova3ColorCharacterizationTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "onyx",
            brand = "onyx",
            model = "nova3color",
            device = "nova3color",
            product = "nova3color",
            hardware = "qcom",
        )
    }

    @Test
    fun characterizes_nova3_color_selection() {
        val facade = Device(DeviceCharacterizationTestSupport.buildActivity())

        assertThat(DeviceInfo.ID.name).isEqualTo("ONYX_NOVA3_COLOR")
        assertThat(facade.epd.javaClass.simpleName).isEqualTo("OnyxEPDController")
        assertThat(facade.lights.javaClass.simpleName).isEqualTo("OnyxColorController")
        assertThat(facade.hasColorScreen).isTrue()
        assertThat(facade.hasFullEinkSupport).isFalse()
    }
}