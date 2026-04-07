package org.koreader.launcher

import android.app.DownloadManager
import android.content.Intent
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EventReceiverContractTest {

    @Test
    fun filter_contains_expected_actions() {
        val receiver = EventReceiver()
        val filter = receiver.filter

        assertThat(filter.hasAction(Intent.ACTION_POWER_CONNECTED)).isTrue()
        assertThat(filter.hasAction(Intent.ACTION_POWER_DISCONNECTED)).isTrue()
        assertThat(filter.hasAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)).isTrue()
        assertThat(filter.countActions()).isEqualTo(3)
    }
}