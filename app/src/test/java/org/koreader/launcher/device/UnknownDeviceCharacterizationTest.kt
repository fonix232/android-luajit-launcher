package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UnknownDeviceCharacterizationTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "google",
            brand = "google",
            model = "pixel",
            device = "redfin",
            product = "redfin",
            hardware = "redfin",
        )
    }

    @Test
    fun characterizes_unknown_device_fallbacks() {
        val facade = Device(DeviceCharacterizationTestSupport.buildActivity())

        assertThat(DeviceInfo.ID.name).isEqualTo("NONE")
        assertThat(facade.epd.javaClass.simpleName).isEqualTo("FakeEPDController")
        assertThat(facade.lights.javaClass.simpleName).isEqualTo("GenericController")
        assertThat(facade.hasEinkSupport).isFalse()
        assertThat(facade.hasColorScreen).isTrue()
        assertThat(facade.properties).isEqualTo("google;google;pixel;redfin;redfin;redfin")
    }
}