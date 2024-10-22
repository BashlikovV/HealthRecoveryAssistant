@file:Suppress("MissingPermission")

package by.bashlikovvv.bluetooth.discovery

import android.os.ParcelUuid
import by.bashlikovvv.bluetooth.model.GBDeviceCandidate
import by.bashlikovvv.bluetooth.util.DeviceHelper
import java.util.LinkedList
import java.util.concurrent.LinkedBlockingQueue
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GBScanEventProcessor(
    private val callback: Callback,
) {
    private val candidatesByAddress = mutableMapOf<String, GBDeviceCandidate>()

    private val eventsToProcessQueue = LinkedBlockingQueue<String>()

    private val eventsToProcessMap = mutableMapOf<String, LinkedList<GBScanEvent>>()

    private var discoverUnsupported = false

    private var running = false

    suspend fun run() = suspendCoroutine<Unit> { continuation ->
        while (running) {
            try {
                val candidateAddress = eventsToProcessQueue.take()
                if (processAllScanEvents(candidateAddress)) {
                    callback.onDeviceChanged()
                }
            } catch (e: InterruptedException) {
                continuation.resumeWithException(e)
                break
            }
        }
    }

    fun scheduleProcessing(event: GBScanEvent) {
        val address = event.device.address
        synchronized(eventsToProcessMap) {
            if (!eventsToProcessMap.containsKey(address)) {
                eventsToProcessMap[address] = LinkedList()
            }
            eventsToProcessMap[address]?.add(event)
        }

        try {
            eventsToProcessQueue.put(address)
        } catch (_: InterruptedException) { }
    }

    private fun processCandidate(candidate: GBDeviceCandidate): Boolean {
        val deviceType = DeviceHelper.resolveDeviceType(candidate, false)

        if (deviceType.isSupported || discoverUnsupported) {
            synchronized(candidatesByAddress) {
                candidatesByAddress[candidate.getMacAddress()] = candidate
            }
        }

        return deviceType.isSupported
    }

    private fun processAllScanEvents(address: String): Boolean {
        val events: MutableList<GBScanEvent>?
        synchronized(eventsToProcessMap) {
            events = eventsToProcessMap.remove(address)?.toMutableList()
        }
        if (events.isNullOrEmpty()) {
            return false
        }

        var candidate: GBDeviceCandidate? = candidatesByAddress[address]

        var previousName: String? = null
        var previousUuids: Array<ParcelUuid>? = null
        var firstTime = false

        if (candidate == null) {
            // First time we see this device
            firstTime = true
            val firstEvent = events[0]
            events.removeAt(0)
            candidate = GBDeviceCandidate(firstEvent.device, firstEvent.rssi, firstEvent.serviceUuids)
        } else {
            previousName = candidate.deviceName
            previousUuids = candidate.serviceUuids
        }

        // Update the device with the remaining events
        for (event in events) {
            candidate.setRssi(event.rssi)
            candidate.addUuids(event.serviceUuids)
        }

        candidate.refreshNameIfUnknown()
        try {
            candidate.addUuids(candidate.device.uuids)
        } catch (_: SecurityException) { }

        if (!firstTime) {
            if (candidate.deviceName == previousName && candidate.serviceUuids contentEquals previousUuids) {
                return false
            }
        }

        if (processCandidate(candidate)) {
            return true
        }

        if (candidate.serviceUuids.isEmpty() || (candidate.serviceUuids.size == 1 && candidate.serviceUuids[0] == ZERO_UUID)) {
            try {
                candidate.device.fetchUuidsWithSdp()
            } catch (_: SecurityException) { }
        }

        return true
    }

    companion object {
        val ZERO_UUID = ParcelUuid.fromString("00000000-0000-0000-0000-000000000000")
    }

    interface Callback {
        fun onDeviceChanged()
    }
}