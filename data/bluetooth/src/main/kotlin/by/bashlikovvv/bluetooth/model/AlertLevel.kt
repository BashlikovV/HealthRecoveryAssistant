package by.bashlikovvv.bluetooth.model

/**
 * https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.alert_level.xml
 */
enum class AlertLevel(val id: Int) {
    NoAlert(0),
    MildAlert(1),
    HighAlert(2);
    // 3-255 reserved
}