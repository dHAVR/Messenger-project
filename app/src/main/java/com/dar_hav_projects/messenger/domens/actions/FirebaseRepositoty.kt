package com.dar_hav_projects.messenger.domens.actions

import android.content.Context
import android.net.Uri
import android.util.Log
import com.dar_hav_projects.messenger.di.AppComponent
import com.dar_hav_projects.messenger.domens.models.IsSignedEnum
import com.dar_hav_projects.messenger.domens.models.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FirebaseRepository@Inject constructor(
    private val context: Context
): I_NetworkActions {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore


    override suspend fun signUp(email: String, pass: String): Result<Boolean> =
        suspendCoroutine { res ->
            CoroutineScope(Dispatchers.Default).launch {
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            res.resume(Result.success(true))
                        } else {
                            res.resume(
                                Result.failure(
                                    task.exception ?: Throwable("Sign-up failed")
                                )
                            )
                        }
                    }
            }
        }

    override suspend fun verifyEmail(): Result<Boolean> =
        suspendCoroutine { res ->
            CoroutineScope(Dispatchers.Default).launch {
                val user = auth.currentUser
                user?.let {
                    it.sendEmailVerification()
                        .addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                CoroutineScope(Dispatchers.Default).launch {
                                    it.reload().addOnCompleteListener { reloadTask ->
                                        if (reloadTask.isSuccessful && it.isEmailVerified) {
                                            res.resume(Result.success(true))
                                        } else {
                                            res.resume(Result.failure(Throwable("Email not verified yet")))
                                        }
                                    }
                                }
                            } else {
                                res.resume(
                                    Result.failure(
                                        verifyTask.exception
                                            ?: Throwable("Error sending verification email")
                                    )
                                )
                            }
                        }
                }
            }
        }


    override suspend fun signIn(email: String, pass: String): Result<IsSignedEnum> =
        suspendCoroutine { res ->
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            if (user.isEmailVerified) {
                                if (user.displayName.isNullOrEmpty()) {
                                    res.resume(Result.success(IsSignedEnum.DoNotHaveNickname))
                                } else {
                                    res.resume(Result.success(IsSignedEnum.IsSigned))
                                }
                            } else {
                                res.resume(Result.success(IsSignedEnum.DoNotVerifyEmail))
                            }
                        } else {
                            res.resume(Result.success(IsSignedEnum.IsNotSigned))
                        }
                    } else {
                        res.resume(
                            Result.failure(
                                task.exception ?: Throwable("An error occurred, please try again")
                            )
                        )
                    }
                }
        }


    override suspend fun isSignedAsync(): IsSignedEnum = withContext(Dispatchers.IO) {
        val user = auth.currentUser
        var isSigned: IsSignedEnum
        try {
            if (user != null) {
                user.reload().await()
                if (user.isEmailVerified) {
                    isSigned = if (!user.displayName.isNullOrEmpty()) {
                        IsSignedEnum.IsSigned
                    } else {
                        IsSignedEnum.DoNotHaveNickname
                    }
                } else {
                    isSigned = IsSignedEnum.DoNotVerifyEmail
                }
            } else {
                isSigned = IsSignedEnum.IsNotSigned
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e("MyLog", "User not found or deleted", e)
            auth.signOut()
            isSigned = IsSignedEnum.IsNotSigned
        } catch (e: Exception) {
            Log.e("MyLog", "An unexpected error occurred", e)
            isSigned = IsSignedEnum.IsNotSigned
        }

        isSigned
    }

    override suspend fun saveUserData(
        nickname: String,
        name: String,
        surname: String,
        imageUri: Uri?
    ): Result<Any> = suspendCoroutine { continuation ->
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Log.e("Authentication", "User not authenticated")
            continuation.resume(Result.failure(Throwable("User not authenticated")))
            return@suspendCoroutine
        }

        Log.d("UserData", "User ID: $userId")
        Log.d("UserData", "Image URI: $imageUri")

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val profileImageRef = storageRef.child("profileImages/$userId.jpg")

        val saveUserDataToFirestore: (String) -> Unit = { downloadUrl ->
            firestore.collection(COLLECTION_USERDATA)
                .document(userId)
                .set(
                    hashMapOf(
                        COLLECTION_USERDATA_NICKNAME to nickname,
                        COLLECTION_USERDATA_NAME to name,
                        COLLECTION_USERDATA_SURNAME to surname,
                        COLLECTION_USERDATA_URL to downloadUrl
                    )
                )
                .addOnSuccessListener {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = nickname
                    }
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnSuccessListener {
                            continuation.resume(Result.success(true))
                        }
                        ?.addOnFailureListener { ex ->
                            Log.e("FirebaseAuth", "Error updating display name", ex)
                            continuation.resume(Result.failure(ex))
                        }
                }
                .addOnFailureListener { ex ->
                    Log.e("Firestore", "Error saving user data", ex)
                    continuation.resume(Result.failure(ex))
                }
        }

        if (imageUri != null) {
            val uploadTask = profileImageRef.putFile(imageUri)
            uploadTask.addOnSuccessListener {
                profileImageRef.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        saveUserDataToFirestore(downloadUrl.toString())
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseStorage", "Error getting download URL", e)
                        continuation.resume(Result.failure(e))
                    }
            }.addOnFailureListener { e ->
                Log.e("Upload", "File upload failed", e)
                continuation.resume(Result.failure(e))
            }
        } else {
            saveUserDataToFirestore("")
        }
    }

    override suspend fun fetchUserData(): Result<UserData?> {
        val userId = auth.currentUser?.uid
        return try {
            val document = userId?.let {
                firestore.collection(COLLECTION_USERDATA)
                    .document(it)
                    .get()
                    .await()
            }

            val userData = document?.toObject(UserData::class.java)
            Result.success(userData)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    companion object{

        private const val COLLECTION_USERDATA = "UserData"
        private const val COLLECTION_USERDATA_NICKNAME = "nickname"
        private const val COLLECTION_USERDATA_NAME = "name"
        private const val COLLECTION_USERDATA_SURNAME = "surname"
        private const val COLLECTION_USERDATA_URL = "url"
    }


}