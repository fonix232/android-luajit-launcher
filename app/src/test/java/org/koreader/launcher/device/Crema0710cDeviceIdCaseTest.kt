package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koreader.launcher.device.DeviceCharacterizationTestSupport
import org.koreader.launcher.device.DeviceInfo
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Crema0710cDeviceIdCaseTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "other",
            brand = "crema",
            model = "crema-0710c",
            device = "other",
            product = "other",
            hardware = "",
        )
    }

    @Test
    fun detects_expected_id() {
        assertThat(DeviceInfo.ID).isEqualTo(DeviceInfo.Id.CREMA_0710C)
    }
}
