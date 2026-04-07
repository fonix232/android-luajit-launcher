package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class T62PrecedenceCharacterizationTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "other",
            brand = "other",
            model = "rk30sdk",
            device = "t62e",
            product = "other",
            hardware = "rk30board",
        )
    }

    @Test
    fun characterizes_current_t62_precedence_behavior() {
        val facade = Device(DeviceCharacterizationTestSupport.buildActivity())

        assertThat(DeviceInfo.ID.name).isEqualTo("BOYUE_T62")
        assertThat(facade.epd.javaClass.simpleName).isEqualTo("RK3026EPDController")
        assertThat(facade.lights.javaClass.simpleName).isEqualTo("GenericController")
    }
}