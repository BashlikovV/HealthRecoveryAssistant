package by.bashlikovvv.database.contract

object HRADRoomContract {
    const val DATABASE_NAME = "hrad_database.room"

    object WearableEventsTable {
        const val TABLE_NAME = "wearable_events"
        const val COLUMN_ID = "wearable_events_id"
        const val COLUMN_SCHEDULED_TIME = "wearable_events_scheduled_time"
        const val COLUMN_TASK_DESCRIPTION_KEY = "wearable_events_task_description"
    }

    object WearableTasksTable {
        const val TABLE_NAME = "wearable_tasks"
        const val COLUMN_ID = "wearable_tasks_id"
        const val COLUMN_VIBRATION_EVENTS = "wearable_tasks_vibration_events"
        const val COLUMN_NOTIFICATION_TEXT = "wearable_tasks_notification_text"
    }

    object ConnectedDevices {
        const val TABLE_NAME = "connected_devices"
        const val COLUMN_ID = "connected_devices_id"
        const val COLUMN_DEVICE_NAME = "connected_devices_device_name"
        const val COLUMN_DEVICE_ADDRESS = "connected_devices_device_address"
        const val COLUMN_DEVICE_TYPE = "connected_devices_device_type"
    }
}