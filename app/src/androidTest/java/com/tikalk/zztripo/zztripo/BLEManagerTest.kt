package com.tikalk.zztripo.zztripo

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.tikalk.zztripo.zztripo.BLE.BLEManager
import com.tikalk.zztripo.zztripo.BLE.IScanCallback
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

/**
 * Created by shaulr on 02/10/2017.
 */
@RunWith(AndroidJUnit4::class)
class BLEManagerTest {
    lateinit var ctx: Context
    lateinit var manager: BLEManager

    @Rule
    @JvmField
    val  mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.BLUETOOTH,
                                                                                 android.Manifest.permission.BLUETOOTH_ADMIN)


    @Before
    fun beforeRun() {
        ctx = InstrumentationRegistry.getTargetContext()
        manager = BLEManager()
    }

    @Test
    fun testBasicManager() {
        manager.init(ctx)
        assert(manager.isBluetoothEnabled())
        val latch = CountDownLatch(1)
        var success = false
        manager.startScan(5000, object: IScanCallback {
            override fun onDone() {
                latch.countDown()
                success = true
            }

            override fun onError() {
                latch.countDown()
            }
        })

        latch.await()
        assert(success)
    }

}