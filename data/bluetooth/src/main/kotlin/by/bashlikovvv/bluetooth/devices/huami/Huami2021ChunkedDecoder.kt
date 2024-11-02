/*  Copyright (C) 2022-2024 José Rebelo

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
package by.bashlikovvv.bluetooth.devices.huami

import android.annotation.SuppressLint
import java.nio.ByteBuffer
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.experimental.xor

class Huami2021ChunkedDecoder(
    private val force2021Protocol: Boolean,
    private var huami2021Handler: Huami2021Handler
) {
    private var currentHandle: Byte? = null
    private var currentType = 0
    private var currentLength = 0
    private lateinit var reassemblyBuffer: ByteBuffer

    // Keep track of last handle and count for acks
    var lastHandle: Byte = 0
        private set
    var lastCount: Byte = 0
        private set

    @Volatile
    private var sharedSessionKey: ByteArray? = null

    fun setEncryptionParameters(sharedSessionKey: ByteArray) {
        this.sharedSessionKey = sharedSessionKey
    }

    fun decode(data: ByteArray): Boolean {
        var i = 0
        if (data[i++] != 0x03.toByte()) {
            return false
        }
        val flags = data[i++]
        val encrypted = (flags and 0x08) == 0x08.toByte()
        val firstChunk = (flags and 0x01) == 0x01.toByte()
        val lastChunk = (flags and 0x02) == 0x02.toByte()
        val needsAck = (flags and 0x04) == 0x04.toByte()

        if (force2021Protocol) {
            i++ // skip extended header
        }
        val handle = data[i++]
        if (currentHandle != null && currentHandle != handle) {
            return false
        }
        lastHandle = handle
        lastCount = data[i++]
        if (firstChunk) { // beginning
            var fullLength = (data[i++].toInt() and 0xff) or
                    ((data[i++].toInt() and 0xff) shl 8) or
                    ((data[i++].toInt() and 0xff) shl 16) or
                    ((data[i++].toInt() and 0xff) shl 24)
            currentLength = fullLength
            if (encrypted) {
                var encryptedLength = fullLength + 8
                val overflow = encryptedLength % 16
                if (overflow > 0) {
                    encryptedLength += (16 - overflow)
                }
                fullLength = encryptedLength
            }
            reassemblyBuffer = ByteBuffer.allocate(fullLength)
            currentType = (data[i++].toInt() and 0xff) or ((data[i++].toInt() and 0xff) shl 8)
            currentHandle = handle
        }
        reassemblyBuffer.put(data, i, data.size - i)
        if (lastChunk) { // end
            var buf = reassemblyBuffer.array()
            if (encrypted) {
                sharedSessionKey?.let { key ->
                    val messageKey = ByteArray(16) { j -> (key[j] xor handle).toByte() }
                    try {
                        buf = decryptAES(buf, messageKey)
                        buf = buf.copyOfRange(0, currentLength)
                    } catch (e: Exception) {
                        currentHandle = null
                        currentType = 0
                        return false
                    }
                } ?: run {
                    // Should never happen
                    currentHandle = null
                    currentType = 0
                    return false
                }
            }

            try {
                huami2021Handler.handle2021Payload(currentType.toShort(), buf)
            } catch (ignored: Exception) {}
            currentHandle = null
            currentType = 0
        }

        return needsAck
    }

    fun setHuami2021Handler(huami2021Handler: Huami2021Handler) {
        this.huami2021Handler = huami2021Handler
    }

    companion object {
        @SuppressLint("GetInstance")
        @Throws(InvalidKeyException::class, NoSuchPaddingException::class, NoSuchAlgorithmException::class, BadPaddingException::class, IllegalBlockSizeException::class)
        fun decryptAES(value: ByteArray, secretKey: ByteArray): ByteArray {
            val ecipher = Cipher.getInstance("AES/ECB/NoPadding")
            val newKey = SecretKeySpec(secretKey, "AES")
            ecipher.init(Cipher.DECRYPT_MODE, newKey)
            return ecipher.doFinal(value)
        }
    }
}

