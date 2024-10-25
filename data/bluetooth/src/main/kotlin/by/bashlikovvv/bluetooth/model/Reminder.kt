package by.bashlikovvv.bluetooth.model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Calendar
import java.util.Date

data class Reminder(
    val message: String,
    val date: Date,
) {
    class Builder(messageLength: Int) {
        private val buffer = ByteBuffer
            .allocate(14 + messageLength)
            .order(ByteOrder.LITTLE_ENDIAN)

        fun put(byte: Byte): Builder {
            buffer.put(byte)
            return this
        }
        
        fun position(position: Int): Builder {
            return put((position and 0xFF).toByte())
        }
        
        fun put(value: Int): Builder {
            buffer.put(value.toByte())
            return this
        }

        fun putInt(value: Int): Builder {
            buffer.putInt(value)
            return this
        }
        
        fun date(date: Date): Builder {
            val calendar = Calendar.getInstance()
            calendar.time = date
            buffer.put(shortCalendarToRawBytes(calendar))
            return this
        }

        fun message(message: ByteArray): Builder {
            buffer.put(message)
            return this
        }

        fun build(): ByteArray = buffer.array()

        private fun shortCalendarToRawBytes(timestamp: Calendar): ByteArray {
            val year = fromUint16(timestamp.get(Calendar.YEAR))
            return byteArrayOf(
                year[0],
                year[1],
                fromUint8(timestamp.get(Calendar.MONTH) + 1),
                fromUint8(timestamp.get(Calendar.DATE)),
                fromUint8(timestamp.get(Calendar.HOUR_OF_DAY)),
                fromUint8(timestamp.get(Calendar.MINUTE)),
            )
        }

        private fun fromUint8(value: Int): Byte {
            return (value and 0xFF).toByte()
        }
        
        private fun fromUint16(value: Int): ByteArray {
            return byteArrayOf(
                (value and 0xFF).toByte(),
                ((value shr 8) and 0xFF).toByte()
            )
        }

        companion object {
            const val MAX_REMINDER_MESSAGE_LENGTH = 16
        }
    }

    companion object {
        fun truncate(s: String, maxLength: Int): String {
            val length = s.length.coerceAtMost(maxLength)
            if (length < 0) return ""

            return s.substring(0, length)
        }
    }
}