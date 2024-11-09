package com.dar_hav_projects.messenger.domens.actions

import android.content.Context
import android.net.Uri
import android.util.Log
import com.dar_hav_projects.messenger.di.AppComponent
import com.dar_hav_projects.messenger.domens.models.Chat
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.domens.models.IsSignedEnum
import com.dar_hav_projects.messenger.domens.models.Message
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
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
                        COLLECTION_USERDATA_USER_UID to userId,
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


    override suspend fun fetchUserDataByID(userId: String): Result<UserData?> {
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




    override suspend fun fetchChats(): Result<List<Chat>> = suspendCancellableCoroutine { res ->
        val userUid = auth.currentUser?.uid.toString()
        Log.d("MyLog", "User UID: $userUid")

        val chats = mutableListOf<Chat>()
        val firestoreCollection = firestore.collection(COLLECTION_CHATS)

        firestoreCollection
            .whereEqualTo(COLLECTION_CHATS_MEMBER1, userUid)
            .get()
            .addOnSuccessListener { result ->
                chats.addAll(result.toObjects(Chat::class.java))
                Log.d("MyLog", "Chats after member1UId query: $chats")

                firestoreCollection
                    .whereEqualTo(COLLECTION_CHATS_MEMBER2, userUid)
                    .get()
                    .addOnSuccessListener { result2 ->
                        chats.addAll(result2.toObjects(Chat::class.java))
                        Log.d("MyLog", "Final Chats: $chats")
                        res.resume(Result.success(chats))
                    }
                    .addOnFailureListener { ex ->
                        Log.d("MyLog", "Failure in member2UId query: ${ex.message}")
                        res.resume(Result.failure(ex))
                    }
            }
            .addOnFailureListener { ex ->
                Log.d("MyLog", "Failure in member1UId query: ${ex.message}")
                res.resume(Result.failure(ex))
            }
    }

    override suspend fun createChat(member2: String): Result<Boolean> = suspendCoroutine { res ->
        val userId = auth.currentUser?.uid.toString()

        val newChatRef = firestore.collection(COLLECTION_CHATS).document()
        val chatId = newChatRef.id

        val chatData = hashMapOf(
            COLLECTION_CHAT_ID to chatId,
            COLLECTION_CHATS_MEMBER1 to userId,
            COLLECTION_CHATS_MEMBER2 to member2,
            COLLECTION_CHATS_LAST_MESSAGE to "No messages",
            COLLECTION_CHATS_LAST_MESSAGE_TIMESTAMP to 0
        )

        firestore
            .collection(COLLECTION_CHATS)
            .add(chatData)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    res.resume(Result.success(true))
                } else {
                    res.resume(Result.failure(Throwable("Can't connect to server")))
                }
            }
            .addOnFailureListener { ex ->
                res.resume(Result.failure(ex))
            }
    }

    override suspend fun getChatName(item: Chat): Result<String> = suspendCoroutine { res ->
        val userId = auth.currentUser?.uid.toString()

        if (userId == item.member1UId) {
            CoroutineScope(Dispatchers.IO).launch {
               fetchUserDataByID(item.member2UId)
                   .onSuccess { user ->
                   if (user != null) {
                       res.resume(Result.success(user.name))
                   }
                }.onFailure {
                       res.resume(Result.success(""))
                   }
            }
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                fetchUserDataByID(item.member1UId)
                    .onSuccess { user ->
                        if (user != null) {
                            res.resume(Result.success(user.name))
                        }
                    }.onFailure {
                        res.resume(Result.success(""))
                    }
            }
        }

    }

    override suspend fun listenForMessages(chatID: String): Flow<List<Message>> = callbackFlow {
        val listenerRegistration = firestore.collection(COLLECTION_MESSAGES)
            .whereEqualTo(COLLECTION_MESSAGES_CHAT_ID, chatID)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreError", "Listening failed: ", error)
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val items = it.documents.mapNotNull { doc -> doc.toObject(Message::class.java) }
                    Log.d("MyLog", "listener $items")
                    trySend(items).isSuccess
                }
            }

        awaitClose { listenerRegistration.remove() }
    }


    override suspend fun createMessage(message: Message): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val userId = auth.currentUser?.uid.toString()

        val newMessageRef = firestore.collection(COLLECTION_MESSAGES).document()
        val messageId = newMessageRef.id

        val chatData = hashMapOf(
            COLLECTION_MESSAGE_ID to messageId,
            COLLECTION_MESSAGES_CHAT_ID to message.chatId,
            COLLECTION_MESSAGES_SENDER_ID to userId,
            COLLECTION_MESSAGES_TIMESTAMP to message.timestamp,
            COLLECTION_MESSAGES_CONTENT to message.content,
            COLLECTION_MESSAGES_IS_READ to message.isRead
        )

        firestore.collection(COLLECTION_MESSAGES)
            .add(chatData)
            .addOnCompleteListener { result ->
                if (continuation.isActive) {
                    if (result.isSuccessful) {
                        continuation.resume(Result.success(true), null)
                    } else {
                        continuation.resume(Result.failure(Throwable("Can't connect to server")), null)
                    }
                }
            }
            .addOnFailureListener { ex ->
                if (continuation.isActive) {
                    continuation.resume(Result.failure(ex), null)
                }
            }
    }


    override suspend fun deleteMessage(message: Message): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val userId = auth.currentUser?.uid.toString()

       if(message.senderId != userId){
           firestore.collection(COLLECTION_MESSAGES)
               .whereEqualTo(COLLECTION_MESSAGE_ID, message.messageId)
               .get()
               .addOnSuccessListener { querySnapshot ->
                   if (!querySnapshot.isEmpty) {
                       val batch = firestore.batch()
                       for (document in querySnapshot.documents) {
                           batch.delete(document.reference)
                       }

                       batch.commit()
                           .addOnSuccessListener {
                               continuation.resume(Result.success(true), null)
                           }
                           .addOnFailureListener { ex ->
                               continuation.resume(Result.failure(ex), null)
                           }
                   } else {
                       continuation.resume(Result.failure(NoSuchElementException("No message found with the given ID")), null)
                   }
               }
               .addOnFailureListener { ex ->
                   continuation.resume(Result.failure(ex), null)
               }
       }

    }



    override suspend fun fetchContacts(): Result<List<Contact>> = suspendCoroutine { res ->
        val userUid = auth.currentUser?.uid.toString()

        firestore.collection(COLLECTION_CONTACTS)
            .whereEqualTo(COLLECTION_CONTACTS_CONTACT_WITH, userUid)
            .get()
            .addOnSuccessListener { result ->
                val items = result.toObjects<Contact>()
                res.resume(Result.success(items))
                Log.d("MyLog", "contacts 1: ${items}")
            }
            .addOnFailureListener { ex ->
                Log.d("MyLog", "contacts 1: failure $ex")
                res.resume(Result.success(emptyList()))
            }
    }

    override suspend fun addContact(item: UserData): Result<Boolean> = suspendCoroutine { res ->
        val userId = auth.currentUser?.uid.toString()
        firestore
            .collection(COLLECTION_CONTACTS)
            .document()
            .set(
                hashMapOf(
                    COLLECTION_CONTACTS_CONTACT_WITH to userId,
                    COLLECTION_CONTACTS_NAME to item.name,
                    COLLECTION_CONTACTS_SURNAME to item.surname,
                    COLLECTION_CONTACTS_NICKNAME to item.nickname,
                    COLLECTION_CONTACTS_PROFILE_PHOTO_URL to item.url,
                    COLLECTION_CONTACTS_PROFILE_UID to item.userUID
                ),
                SetOptions.merge()
            )
            .addOnCompleteListener { result ->
                if(result.isSuccessful){
                    res.resume(Result.success(true))
                } else {
                    res.resume(Result.failure(Throwable("Can`t connect to server")))
                }
            }
            .addOnFailureListener { ex ->
                res.resume(Result.failure(ex))
            }

    }

    override suspend fun searchContact(nickname: String): Result<List<UserData>> = suspendCoroutine { res ->
        val userId = auth.currentUser?.uid.toString()
        Log.d("MyLog", "userId 1: ${userId}")
        firestore.collection(COLLECTION_USERDATA)
            .whereEqualTo(COLLECTION_USERDATA_NICKNAME, nickname)
            .get()
            .addOnSuccessListener { result ->
                val items = result.toObjects<UserData>().filter { it.userUID != userId }

                res.resume(Result.success(items))
                Log.d("MyLog", "contacts 1: ${items}")
            }
            .addOnFailureListener { ex ->
                Log.d("MyLog", "contacts 1: failure $ex")
                res.resume(Result.success(emptyList()))
            }
    }


    companion object{

        private const val COLLECTION_USERDATA = "UserData"
        private const val COLLECTION_USERDATA_USER_UID = "userUID"
        private const val COLLECTION_USERDATA_NICKNAME = "nickname"
        private const val COLLECTION_USERDATA_NAME = "name"
        private const val COLLECTION_USERDATA_SURNAME = "surname"
        private const val COLLECTION_USERDATA_URL = "url"

        private const val COLLECTION_CHATS = "Chats"
        private const val COLLECTION_CHAT_NAME = "chatName"
        private const val COLLECTION_CHAT_ID = "chatId"
        private const val COLLECTION_CHATS_LAST_MESSAGE = "lastMessage"
        private const val COLLECTION_CHATS_LAST_MESSAGE_TIMESTAMP = "lastMessageTimestamp"
        private const val COLLECTION_CHATS_MEMBER1 = "member1UId"
        private const val COLLECTION_CHATS_MEMBER2 = "member2UId"

        private const val COLLECTION_CONTACTS = "Contacts"
        private const val COLLECTION_CONTACTS_CONTACT_WITH = "contactWith"
        private const val COLLECTION_CONTACTS_NAME = "name"
        private const val COLLECTION_CONTACTS_SURNAME = "surname"
        private const val COLLECTION_CONTACTS_NICKNAME = "nickname"
        private const val COLLECTION_CONTACTS_PROFILE_PHOTO_URL = "profileImageUrl"
        private const val COLLECTION_CONTACTS_PROFILE_UID = "userId"

        private const val COLLECTION_MESSAGES = "Messages"
        private const val COLLECTION_MESSAGE_ID = "messageId"
        private const val COLLECTION_MESSAGES_CHAT_ID = "chatId"
        private const val COLLECTION_MESSAGES_SENDER_ID = "senderId"
        private const val COLLECTION_MESSAGES_TIMESTAMP = "timestamp"
        private const val COLLECTION_MESSAGES_CONTENT = "content"
        private const val COLLECTION_MESSAGES_IS_READ = "isRead"
    }


}