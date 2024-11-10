package com.dar_hav_projects.messenger.encryption

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.google.crypto.tink.Aead
import com.google.crypto.tink.HybridDecrypt
import com.google.crypto.tink.HybridEncrypt
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.subtle.Base64
import com.google.crypto.tink.subtle.Ed25519Sign
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.inject.Inject

class Encryption @Inject constructor(
    private val context: Context
) : I_Encryprion {

   override fun generateAndSaveKeyPair(alias: String): KeyPair? {
        return try {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
            )

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setKeySize(2048)
                setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                setUserAuthenticationRequired(false)
            }.build()

            keyPairGenerator.initialize(keyGenParameterSpec)
            val keyPair = keyPairGenerator.generateKeyPair()
            keyPair
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getKeyPair(alias: String): KeyPair? {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val privateKey = keyStore.getKey(alias, null) as? PrivateKey
            val publicKey = keyStore.getCertificate(alias)?.publicKey

            if (privateKey != null && publicKey != null) {
                KeyPair(publicKey, privateKey)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    override fun convertStringToPublicKey(publicKeyString: String): PublicKey? {
        return try {
            val keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT)

            val keySpec = X509EncodedKeySpec(keyBytes)

            val keyFactory = KeyFactory.getInstance("RSA")

            keyFactory.generatePublic(keySpec)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    override fun encryptMessage(message: String, publicKey: PublicKey): String {
        Log.d("MyLogPUBLIC", "encryptMessage $$message $publicKey ")
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(message.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }


    override fun decryptMessage(encryptedMessage: ByteArray, privateKey: PrivateKey): String {
        Log.d("MyLogPUBLIC", "decryptMessage $$encryptedMessage $privateKey ")
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(cipher.doFinal(encryptedMessage), Charsets.UTF_8)
    }


}
