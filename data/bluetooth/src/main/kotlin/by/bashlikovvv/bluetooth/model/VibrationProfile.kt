package by.bashlikovvv.bluetooth.model

open class VibrationProfile(
    open val id: String,
    open val onOffSequence: Array<Int>,
    open val repeat: Short,
    open val alertLevel: Int = AlertLevel.MildAlert.id,
) {
    companion object {
        sealed class DefaultProfiles(
            override val id: String,
            override val onOffSequence: Array<Int>,
            override val repeat: Short,
            override val alertLevel: Int = AlertLevel.MildAlert.id,
        ) : VibrationProfile(id, onOffSequence, repeat, alertLevel) {
            class Staccato(
                override val id: String,
                override val repeat: Short,
            ) : DefaultProfiles(id, arrayOf(100, 0), repeat)

            class ProfileShort(
                override val id: String,
                override val repeat: Short,
            ) : DefaultProfiles(id, arrayOf(200, 200), repeat)

            class Long(
                override val id: String,
                override val repeat: Short,
            ) : DefaultProfiles(id, arrayOf(500, 1000), repeat)

            class WaterDrop(
                override val id: String,
                override val repeat: Short,
            ) : DefaultProfiles(id, arrayOf(100, 1500), repeat)

            class Ring(
                override val id: String,
                override val repeat: Short,
            ) : DefaultProfiles(id, arrayOf(300, 200, 600, 2000), repeat)

            class AlarmClock(
                override val id: String,
                override val repeat: Short,
            ) : DefaultProfiles(id, arrayOf(30, 35, 30, 35, 30, 35, 30, 800), repeat)

            class Medium(
                override val id: String,
                override val repeat: Short,
            ) : DefaultProfiles(id, arrayOf(300, 600), repeat)
        }
    }
}