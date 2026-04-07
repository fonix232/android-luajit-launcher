package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Darwin5PrecedenceCharacterizationTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "other",
            brand = "other",
            model = "mc_c68pctm",
            device = "other",
            product = "other",
            hardware = "other",
        )
    }

    @Test
    fun characterizes_current_darwin5_precedence_behavior() {
        val facade = Device(DeviceCharacterizationTestSupport.buildActivity())

        assertThat(DeviceInfo.ID.name).isEqualTo("ONYX_DARWIN5")
        assertThat(facade.epd.javaClass.simpleName).isEqualTo("OnyxEPDController")
        assertThat(facade.lights.javaClass.simpleName).isEqualTo("OnyxSdkLightsController")
    }
}