package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnyxPoke2CharacterizationTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "onyx",
            brand = "onyx",
            model = "poke2",
            device = "poke2",
            product = "poke2",
            hardware = "qcom",
        )
    }

    @Test
    fun characterizes_onyx_poke2_selection_and_quirks() {
        val facade = Device(DeviceCharacterizationTestSupport.buildActivity())

        assertThat(DeviceInfo.ID.name).isEqualTo("ONYX_POKE2")
        assertThat(facade.epd.javaClass.simpleName).isEqualTo("OnyxEPDController")
        assertThat(facade.lights.javaClass.simpleName).isEqualTo("OnyxWarmthController")
        assertThat(facade.bugLifecycle).isTrue()
        assertThat(facade.needsWakelocks).isFalse()
        assertThat(facade.hasColorScreen).isFalse()
    }
}