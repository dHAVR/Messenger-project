package com.dar_hav_projects.messenger.encryption

import com.google.crypto.tink.Aead
import com.google.crypto.tink.HybridDecrypt
import com.google.crypto.tink.HybridEncrypt
import com.google.crypto.tink.KeysetHandle
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

interface I_Encryprion {

    fun generateAndSaveKeyPair(alias: String): KeyPair?
    fun getKeyPair(alias: String): KeyPair?
    fun convertStringToPublicKey(publicKeyString: String): PublicKey?
    fun encryptMessage(message: String, publicKey: PublicKey): String
    fun decryptMessage(encryptedMessage: ByteArray, privateKey: PrivateKey): String
}