package by.bashlikovvv.bluetooth.impl

import by.bashlikovvv.bluetooth.model.BtLEAction
import by.bashlikovvv.bluetooth.model.VibrationProfile
import by.bashlikovvv.bluetooth.service.NotificationStrategy
import by.bashlikovvv.bluetooth.service.TransactionBuilder

class V1NotificationStrategy : NotificationStrategy {
    override fun sendCustomNotification(
        vibrationProfile: VibrationProfile,
        extraAction: BtLEAction,
        builder: TransactionBuilder
    ) {

    }
}