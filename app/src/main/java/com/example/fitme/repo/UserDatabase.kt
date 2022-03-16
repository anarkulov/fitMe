package com.example.fitme.repo

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.data.models.User
import com.example.fitme.utils.Constants.Collection.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import org.koin.dsl.module

val databaseModule = module {
    single { UserDatabase() }
}

class UserDatabase : AppDatabase() {

    private val firebaseUser = MutableLiveData<FirebaseUser?>()
    private val myTag = "UserDatabase"

    fun getProfile(): MutableLiveData<Resource<User>> {

        val liveData = MutableLiveData<Resource<User>>()

        if (currentUser?.uid == null) {
            liveData.value = Resource.error("UID is null", null, null)
            return liveData
        }

        liveData.value = Resource.loading(null)

        val docRef =
            firestoreInstance.collection(USERS).document(currentUser.uid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    liveData.value = Resource.success(document.toObject(User::class.java))
                } else {
                    liveData.value =
                        Resource.error("No document found on ${docRef.path}", null, 404)
                }
            } else {
                liveData.value = Resource.error(task.exception.toString(), null, null)
            }
        }
        return liveData
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        country: String,
        state: String,
        city: String,
    ): MutableLiveData<Resource<Boolean>> {

        val liveData = MutableLiveData<Resource<Boolean>>()

        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone,
            "country" to country,
            "state" to state,
            "city" to city
        )

        liveData.value = Resource.loading(null)
        currentUser?.uid?.let { uid ->

            firestoreInstance.collection(USERS).document(uid)
                .set(user)
                .addOnSuccessListener {
                    liveData.value = Resource.success(true)
                    Log.d("DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    liveData.value = Resource.error(e.toString(), null, null)
                }
        }

        return liveData
    }

    fun getCurrentUser(): MutableLiveData<FirebaseUser?> {
        return firebaseUser
    }

    private fun setCurrentUser() {
        firebaseUser.postValue(currentUser)
    }

    fun signIn(
        email: String,
        password: String): MutableLiveData<Resource<String>> {
        val liveData = MutableLiveData<Resource<String>>()
        liveData.value = Resource.loading(null)

        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    liveData.value = Resource.success(task.result.user?.uid)
                    setCurrentUser()
                } else {
                    Log.d("Failed to login")
                }
            }
            .addOnFailureListener {
                liveData.value = Resource.error(it.message, null, null)
                Log.d("Failed to login")
            }

        return liveData
    }

    fun signOut(): MutableLiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()

        firebaseAuth
            .signOut()

        liveData.postValue(true)

        return liveData
    }

    fun register(email: String, password: String): MutableLiveData<Resource<String>> {
        val liveData = MutableLiveData<Resource<String>>()
        liveData.value = Resource.loading(null)

        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    liveData.value = Resource.success(it.result.user?.uid)
                    setCurrentUser()
                } else {
                    Log.d("Failed to register")
                }
            }
            .addOnFailureListener {
                liveData.value = Resource.error(it.message, null, null)
                Log.d("Failed to register")
            }

        return liveData
    }

    fun createUser(
        email: String,
        password: String
    ): MutableLiveData<Resource<String>> {
        val liveData = MutableLiveData<Resource<String>>()

        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                liveData.value = Resource.success(it.user?.uid)
            }
            .addOnFailureListener {
                liveData.value = Resource.error(it.message, null, null)
                Log.d("Failed to createUser")
            }

        return liveData
    }

    fun createProfile(
        uid: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        country: String,
        state: String,
        city: String,
    ): MutableLiveData<Resource<Int>> {

        val liveData = MutableLiveData<Resource<Int>>()

        liveData.value = Resource.loading(null)

        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone,
            "country" to country,
            "state" to state,
            "city" to city
        )

        firestoreInstance.collection(USERS).document(uid)
            .set(user)
            .addOnCompleteListener {
                liveData.value = Resource.success(1)
                liveData.postValue(Resource(Status.SUCCESS, 1, "", 200))
                setCurrentUser()
                Log.d("Success to createProfile")
            }
            .addOnFailureListener { e ->
                liveData.value = Resource.error(e.toString(), null, null)
                liveData.postValue(Resource(Status.SUCCESS, null, e.message, 500))
                Log.d("Failed to createProfile")
            }

        return liveData
    }

    fun forgotPassword(email: String): MutableLiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()

        liveData.value = Resource.loading(null)

        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    liveData.value = Resource.success(true)
                }
            }
            .addOnFailureListener {
                liveData.value = Resource.error(it.message, null, null)
                Log.d("Failed to forgotPassword")
            }

        return liveData
    }

    fun getUser(uid: String): MutableLiveData<Resource<User>> {
        val user = MutableLiveData<Resource<User>>()

        user.value = Resource.loading(null)

        val docRef = firestoreInstance
            .collection("users")
            .document(uid)

        docRef.get()
            .addOnSuccessListener {
                val document = it
                if (document.exists()) {
                    user.value = Resource.success(document.toObject(User::class.java))
                } else {
                    user.value = Resource.error(null, null, null)
                }
            }
            .addOnFailureListener {
                user.value = Resource.error(it.message.toString(), null, null)
            }

        return user
    }

    fun fetchFileReference(
        timeStamp: String,
        imageUri: Uri,
        context: Context,
    ): LiveData<StorageReference> {
        val fetchFileReferenceImage = MutableLiveData<StorageReference>()

        val fileReference =
            storageReference.child(timeStamp + "." + getFileExtension(imageUri, context))
        fetchFileReferenceImage.value = fileReference

        return fetchFileReferenceImage
    }

    private fun getFileExtension(uri: Uri, context: Context): String? {
        val contentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri))
    }

    fun addImageUrlInDatabase(imageUrl: String, mUri: String): LiveData<Boolean> {
        val successAddUriImage = MutableLiveData<Boolean>()
        val reference =
            currentUser?.uid?.let {
                firestoreInstance.collection(USERS).document(it)
            }
        val map = HashMap<String, Any>()
        map[imageUrl] = mUri

        reference?.update(map)?.addOnCompleteListener { successAddUriImage.setValue(true) }
            ?.addOnFailureListener {
                successAddUriImage.setValue(
                    false
                )
            }

        return successAddUriImage
    }

    fun getAlarmList(): MutableLiveData<Resource<List<Alarm>>> {
        val liveData = MutableLiveData<Resource<List<Alarm>>>()
        liveData.value = Resource.loading(null)

        val alarmList = ArrayList<Alarm>()
        firebaseAuth.uid?.let {
            firestoreInstance.collection(ALARM_PATH)
                .orderBy(ALARM_ID_FIELD, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshots ->
                    if (snapshots != null) {
                        for (snapshot: DocumentSnapshot in snapshots) {
                            val alarm : Alarm? = snapshot.toObject(Alarm::class.java)
                            Log.d("snapshot: $alarm", myTag)
                            alarm?.let { item ->
                                item.docId = snapshot.id
                                item.id = snapshot["id"] as String
                                item.title = snapshot["title"] as String
                                item.timestamp = snapshot["timestamp"] as String
                                item.days = snapshot["days"] as ArrayList<Boolean>
                                item.timeInMs = snapshot["timeInMs"] as Long
                                item.isTurnedOn = snapshot["isTurnedOn"] as Boolean
                                item.isRepeatable = snapshot["isRepeatable"] as Boolean
                                item.userId = snapshot["userId"] as String
                                alarmList.add(item)
                            }
                        }
                    }
                    if (alarmList.isNotEmpty()) {
                        liveData.value = Resource.success(alarmList)
                    } else {
                        liveData.value = Resource.error("Alarm list is empty", null, -1)
                    }
                }
                .addOnFailureListener {
                    liveData.value = Resource.error("Failed to get alarm list", null, -1)
                }
        }

        return liveData
    }

    fun getUserId(): String? {
        return firebaseUser.value?.uid
    }

    fun saveAlarm(alarm: Alarm): MutableLiveData<Resource<String>> {
        val liveData = MutableLiveData<Resource<String>>()
        liveData.value = Resource.loading(null)

        val alarmMap = mapOf<String, Any>(
            ALARM_ID_FIELD to alarm.id,
            ALARM_TITLE_FIELD to alarm.title,
            ALARM_TIMESTAMP_FIELD to alarm.timestamp,
            ALARM_IS_TURN_ON_FIELD to alarm.isTurnedOn,
            ALARM_TIME_IN_MS_FIELD to alarm.timeInMs,
            ALARM_CHALLENGE_FIELD to alarm.challenge,
            ALARM_DAYS_FIELD to alarm.days,
            ALARM_USER_ID_FIELD to alarm.userId
        )

        try {
            firestoreInstance
                .collection(ALARM_PATH)
                .add(alarmMap)
                .addOnSuccessListener {
                    Log.d("Successfully added: ${it.id}", myTag)
                    liveData.postValue(Resource.success(it.id, 1))
                }.addOnFailureListener {
                    Log.d("Failure", myTag)
                    liveData.postValue(Resource.error(it.message, null, -1))
                }
        } catch (e: NullPointerException) {
            Log.d("NullPointerException", myTag)
            liveData.postValue(Resource.error("NullPointerException", null, -1))
        }

        return liveData
    }

    fun updateAlarm(alarm: Alarm): MutableLiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()
        val id = alarm.id
        val timestamp = alarm.timestamp
        val title =  alarm.title
        val days =  alarm.days
        val challenge =  alarm.challenge
        val isTurnedOn =  alarm.isTurnedOn
        val isRepeatable =  alarm.isRepeatable
        val timeInMs = alarm.timeInMs
        val userId =  alarm.userId

        val alarmItem: Map<String, Any> = mutableMapOf(
            "id" to id,
            "timestamp" to timestamp,
            "title" to title,
            "days" to days,
            "challenge" to challenge,
            "isTurnedOn" to isTurnedOn,
            "isRepeatable" to isRepeatable,
            "timeInMs" to timeInMs,
            "userId" to userId
//            "isPlayed" to alarm.isPlayed,
//            "isVibrated" to city,
        )

        liveData.value = Resource.loading(null)
        firestoreInstance
            .collection(ALARM_PATH)
            .document(alarm.docId)
            .set(alarmItem)
            .addOnSuccessListener {
                liveData.value = Resource.success(true)
                Log.d("DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                liveData.value = Resource.error(e.toString(), null, null)
            }

        return liveData
    }

//    fun getUserPortfolios(userId: String): MutableLiveData<Resource<Portfolios>> {
//        val liveData = MutableLiveData<Resource<Portfolios>>()
//        firestoreInstance.collection(PORTFOLIO).document(userId).get().addOnSuccessListener {
//            it.let { document ->
//                if (document != null) {
//                    val da = document.toObject(Portfolios::class.java)
//                    liveData.postValue(Resource.success(da))
//                }
//            }
//        }.addOnFailureListener {
//            liveData.postValue(Resource.error(it.message, null))
//        }
//        return liveData
//    }

//    fun getUserProjects(userId: String): MutableLiveData<Resource<Projects>> {
//        val liveData = MutableLiveData<Resource<Projects>>()
//        firestoreInstance.collection(PROJECTS).document(userId).get().addOnSuccessListener {
//            it.let { document ->
//                if (document != null) {
//                    val da = document.toObject(Projects::class.java)
//                    liveData.postValue(Resource.success(da))
//                }
//            }
//        }.addOnFailureListener {
//            liveData.postValue(Resource.error(it.message, null))
//        }
//        return liveData
//    }

//    fun getMilestone(projectId: String): MutableLiveData<Resource<Milestones>> {
//        val liveData = MutableLiveData<Resource<Milestones>>()
//        firestoreInstance.collection(MILESTONE).document(projectId).get().addOnSuccessListener {
//            it.let { document ->
//                if (document != null) {
//                    val da = document.toObject(Milestones::class.java)
//                    liveData.postValue(Resource.success(da))
//                }
//            }
//        }.addOnFailureListener {
//            liveData.postValue(Resource.error(it.message, null))
//        }
//        return liveData
//    }


//    fun getRevisions(projectId: String): MutableLiveData<Resource<Revisions>> {
//        val liveData = MutableLiveData<Resource<Revisions>>()
//        firestoreInstance.collection(REVISION).document(projectId).get().addOnSuccessListener {
//            it.let { document ->
//                if (document != null) {
//                    val da = document.toObject(Revisions::class.java)
//                    liveData.postValue(Resource.success(da))
//                }
//            }
//        }.addOnFailureListener {
//            liveData.postValue(Resource.error(it.message, null))
//        }
//        return liveData
//    }

    companion object {
        const val ALARM_PATH = "alarms"
        const val ALARM_TIMESTAMP_FIELD = "timestamp"
        const val ALARM_ID_FIELD = "id"
        const val ALARM_TITLE_FIELD = "title"
        const val ALARM_IS_TURN_ON_FIELD = "isTurnedOn"
        const val ALARM_TIME_IN_MS_FIELD = "timeInMs"
        const val ALARM_CHALLENGE_FIELD = "challenge"
        const val ALARM_DAYS_FIELD = "days"
        const val ALARM_USER_ID_FIELD = "userId"
    }
}