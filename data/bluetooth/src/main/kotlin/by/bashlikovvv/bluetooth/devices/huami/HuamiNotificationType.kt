package by.bashlikovvv.bluetooth.devices.huami

enum class HuamiNotificationType(val code: Byte) {
    APP_ALERTS(0x00),
    INCOMING_CALL(0x01),
    INCOMING_SMS(0x02),
    GOAL_NOTIFICATION(0x04),
    ALARM(0x05),
    IDLE_ALERTS(0x06),
    EVENT_REMINDER(0x08),
    FIND_BAND(0x09),
    TODO_LIST(0x0a),
    SCHEDULE(0x0c);
}