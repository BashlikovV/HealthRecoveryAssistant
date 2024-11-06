package by.bashlikovvv.devicesettings.domain.model

import android.os.Parcelable
import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

@Parcelize
open class VibrationProfile(
    val onOffSequence: IntArray,
    val repeat: Short,
) : VibrationProfileCompose, Parcelable {
    override val name: String @Composable get() = ""

    companion object {
        private val profileByName: Map<String, VibrationProfile> = mapOf(
            "staccato" to staccato(1),
            "shortProfile" to shortProfile(1),
            "medium" to medium(1),
            "long" to long(1),
            "waterDrop" to waterDrop(1),
            "ring" to ring(1),
            "alarmClock" to alarmClock(1),
        )

        val entries = profileByName.keys.toList()

        fun staccato(repeat: Short) = object : VibrationProfile(
            onOffSequence = intArrayOf(100, 0),
            repeat = repeat
        ) {
            override val name: String @Composable get() = "staccato"
        }

        fun shortProfile(repeat: Short) = object : VibrationProfile(
            onOffSequence = intArrayOf(200, 200),
            repeat = repeat
        ) {
            override val name: String @Composable get() = "shortProfile"
        }

        fun medium(repeat: Short) = object : VibrationProfile(
            onOffSequence = intArrayOf(300, 600),
            repeat = repeat
        ) {
            override val name: String @Composable get() = "medium"
        }

        fun long(repeat: Short) = object : VibrationProfile(
            onOffSequence = intArrayOf(500, 1000),
            repeat = repeat
        ) {
            override val name: String @Composable get() = "long"
        }

        fun waterDrop(repeat: Short) = object : VibrationProfile(
            onOffSequence = intArrayOf(100, 1500),
            repeat = repeat
        ) {
            override val name: String @Composable get() = "waterDrop"
        }

        fun ring(repeat: Short) = object : VibrationProfile(
            onOffSequence = intArrayOf(300, 200, 600, 2000),
            repeat = repeat
        ) {
            override val name: String @Composable get() = "ring"
        }

        fun alarmClock(repeat: Short) = object : VibrationProfile(
            onOffSequence = intArrayOf(30, 35, 30, 35, 30, 35, 30, 800),
            repeat = repeat
        ) {
            override val name: String @Composable get() = "alarmClock"
        }

        fun byName(name: String): VibrationProfile = profileByName.getOrElse(name) { null } ?: shortProfile(1)
    }
}