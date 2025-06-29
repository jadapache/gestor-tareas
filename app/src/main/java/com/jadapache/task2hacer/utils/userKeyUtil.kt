package com.jadapache.task2hacer.utils

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import androidx.core.content.edit

object userKeyUtil {
    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_ALIAS = "cloud_user_key"

    suspend fun getOrCreateUserKey(context: Context): ByteArray {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("No user logged in")
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val db = FirebaseFirestore.getInstance().collection("usuarios")
        val doc = db.document(userId).get().await()
        val keyBase64 = prefs.getString(KEY_ALIAS + userId, null)
        if (doc != null) {
            // Buscar en el documento del usuario
            val cloudKeyBase64 = doc.getString("token")
            if (!cloudKeyBase64.isNullOrEmpty()) {
                prefs.edit { putString(KEY_ALIAS + userId, cloudKeyBase64) }
                return Base64.decode(cloudKeyBase64, Base64.DEFAULT)
            }
        }
            // Si no existe, generar y guardar nueva clave
            val key = ByteArray(32)
            SecureRandom().nextBytes(key)
            val newKeyBase64 = Base64.encodeToString(key, Base64.DEFAULT)
            db.document(userId).update("token", newKeyBase64).await()
            prefs.edit { putString(KEY_ALIAS + userId, newKeyBase64) }
            return key
    }
} 