package com.jadapache.task2hacer.utils

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlinx.coroutines.runBlocking

object encriptationUtil {
    fun encrypt(context: Context, plainText: String): String {
        val key = runBlocking { userKeyUtil.getOrCreateUserKey(context) }
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(context: Context, encryptedData: String): String {
        return try {
            val key = runBlocking { userKeyUtil.getOrCreateUserKey(context) }
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)
            val iv = combined.copyOfRange(0, 16)
            val encrypted = combined.copyOfRange(16, combined.size)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
            val decrypted = cipher.doFinal(encrypted)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            "" // Si falla, retorna cadena vacía
        }
    }
} 