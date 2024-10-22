package by.bashlikovvv.bluetooth.model

/**
 * This interface is implemented at least once for every supported gadget device.
 * It allows Gadgetbridge to generically deal with different kinds of devices
 * without actually knowing the details of any device.
 * <p/>
 * Instances will be created as needed and asked whether they support a given
 * device. If a coordinator answers true, it will be used to assist in handling
 * the given device.
 */
interface DeviceCoordinator {
    val orderPriority: Int

    /**
     * Checks whether this coordinator handles the given candidate.
     *
     * @param candidate
     * @return true if this coordinator handles the given candidate.
     */
    fun supports(candidate: GBDeviceCandidate): Boolean
}