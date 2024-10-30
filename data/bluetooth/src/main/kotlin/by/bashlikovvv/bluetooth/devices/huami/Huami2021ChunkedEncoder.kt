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
import android.bluetooth.BluetoothGattCharacteristic
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.zip.CRC32
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import kotlin.experimental.or
import kotlin.experimental.xor

class Huami2021ChunkedEncoder(
    private val characteristicChunked2021Write: BluetoothGattCharacteristic,
    private val force2021Protocol: Boolean,
    private var mMTU: Int = 23
) {
    private var writeHandle: Byte = 0

    @Volatile
    private var encryptedSequenceNr: Int = 0

    @Volatile
    private var sharedSessionKey: ByteArray? = null

    @Synchronized
    fun setEncryptionParameters(encryptedSequenceNr: Int, sharedSessionKey: ByteArray) {
        this.encryptedSequenceNr = encryptedSequenceNr
        this.sharedSessionKey = sharedSessionKey
    }

    @Synchronized
    fun setMTU(mMTU: Int) {
        this.mMTU = mMTU
    }

    @Synchronized
    fun write(
        builder: TransactionBuilder,
        type: Short,
        data: ByteArray,
        extendedFlags: Boolean,
        encrypt: Boolean
    ) {
        if (encrypt && sharedSessionKey == null) {
            return
        }

        writeHandle++

        var remaining = data.size
        val length = data.size
        var count: Byte = 0
        var headerSize = 10

        if (extendedFlags) {
            headerSize++
        }

        var encryptedData = data
        if (extendedFlags && encrypt) {
            val messageKey = ByteArray(16) { i -> (sharedSessionKey!![i] xor writeHandle).toByte() }
            var encryptedLength = length + 8
            val overflow = encryptedLength % 16
            if (overflow > 0) {
                encryptedLength += (16 - overflow)
            }

            val encryptablePayload = ByteArray(encryptedLength)
            System.arraycopy(data, 0, encryptablePayload, 0, length)
            encryptablePayload[length] = (encryptedSequenceNr and 0xff).toByte()
            encryptablePayload[length + 1] = ((encryptedSequenceNr shr 8) and 0xff).toByte()
            encryptablePayload[length + 2] = ((encryptedSequenceNr shr 16) and 0xff).toByte()
            encryptablePayload[length + 3] = ((encryptedSequenceNr shr 24) and 0xff).toByte()
            encryptedSequenceNr++
            val checksum = getCRC32(encryptablePayload, 0, length + 4)
            encryptablePayload[length + 4] = (checksum and 0xff).toByte()
            encryptablePayload[length + 5] = ((checksum shr 8) and 0xff).toByte()
            encryptablePayload[length + 6] = ((checksum shr 16) and 0xff).toByte()
            encryptablePayload[length + 7] = ((checksum shr 24) and 0xff).toByte()
            remaining = encryptedLength
            try {
                encryptedData = encryptAES(encryptablePayload, messageKey)
            } catch (e: Exception) {
                return
            }
        }

        while (remaining > 0) {
            val maxChunkLength = mMTU - 3 - headerSize
            val copyBytes = remaining.coerceAtMost(maxChunkLength)
            val chunk = ByteArray(copyBytes + headerSize)

            var flags: Byte = 0
            if (encrypt) {
                flags = flags or 0x08
            }
            if (count.toInt() == 0) {
                flags = flags or 0x01
                var i = 4
                if (extendedFlags) {
                    i++
                }
                chunk[i++] = (length and 0xff).toByte()
                chunk[i++] = ((length shr 8) and 0xff).toByte()
                chunk[i++] = ((length shr 16) and 0xff).toByte()
                chunk[i++] = ((length shr 24) and 0xff).toByte()
                chunk[i++] = (type.toInt() and 0xff).toByte()
                chunk[i] = ((type.toInt() shr 8) and 0xff).toByte()
            }
            if (remaining <= maxChunkLength) {
                flags = flags or 0x06 // last chunk?
            }
            chunk[0] = 0x03
            chunk[1] = flags
            if (extendedFlags) {
                chunk[2] = 0
                chunk[3] = writeHandle
                chunk[4] = count
            } else {
                chunk[2] = writeHandle
                chunk[3] = count
            }

            System.arraycopy(encryptedData, encryptedData.size - remaining, chunk, headerSize, copyBytes)
            builder.write(characteristicChunked2021Write, chunk)
            remaining -= copyBytes
            headerSize = 4

            if (extendedFlags) {
                headerSize++
            }

            count++
        }
    }

    companion object {
        fun getCRC32(seq: ByteArray, offset: Int, length: Int): Int {
            val crc = CRC32()
            crc.update(seq, offset, length)
            return crc.value.toInt()
        }

        @SuppressLint("GetInstance")
        @Throws(InvalidKeyException::class, NoSuchPaddingException::class, NoSuchAlgorithmException::class, BadPaddingException::class, IllegalBlockSizeException::class)
        fun encryptAES(value: ByteArray, secretKey: ByteArray): ByteArray {
            val ecipher = Cipher.getInstance("AES/ECB/NoPadding")
            val newKey = SecretKeySpec(secretKey, "AES")
            ecipher.init(Cipher.ENCRYPT_MODE, newKey)
            return ecipher.doFinal(value)
        }
    }
}

