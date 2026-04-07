package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BoyueT61CharacterizationTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "boyue",
            brand = "boyue",
            model = "t61d",
            device = "t61d",
            product = "t61d",
            hardware = "rk30board",
        )
    }

    @Test
    fun characterizes_boyue_t61_selection() {
        val facade = Device(DeviceCharacterizationTestSupport.buildActivity())

        assertThat(DeviceInfo.ID.name).isEqualTo("BOYUE_T61")
        assertThat(facade.epd.javaClass.simpleName).isEqualTo("RK3026EPDController")
        assertThat(facade.lights.javaClass.simpleName).isEqualTo("GenericController")
        assertThat(facade.hasEinkSupport).isTrue()
        assertThat(facade.hasLights).isTrue()
        assertThat(facade.needsWakelocks).isFalse()
        assertThat(facade.hasColorScreen).isFalse()
    }
}