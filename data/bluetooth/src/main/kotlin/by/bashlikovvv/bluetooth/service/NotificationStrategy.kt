package by.bashlikovvv.bluetooth.service

import by.bashlikovvv.bluetooth.model.BtLEAction
import by.bashlikovvv.bluetooth.model.VibrationProfile

interface NotificationStrategy {
    /**
     * @param vibrationProfile specifies how and how often the Band shall vibrate.
     * @param extraAction an extra action to be executed after every vibration. Allows to abort the repetition, for example.
     * */
    fun sendCustomNotification(
        vibrationProfile: VibrationProfile,
        extraAction: BtLEAction,
        builder: TransactionBuilder,
    )
}