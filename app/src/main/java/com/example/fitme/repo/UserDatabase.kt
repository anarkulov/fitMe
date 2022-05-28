package com.example.fitme.repo

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.utils.Log
import com.example.fitme.data.local.Constants.Home.PERIOD_MONTH
import com.example.fitme.data.local.Constants.Home.PERIOD_WEEK
import com.example.fitme.data.models.*
import com.example.fitme.utils.Constants.Collection.USERS
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import org.koin.dsl.module
import java.util.*

val databaseModule = module {
    single { UserDatabase() }
}

class UserDatabase : AppDatabase() {

    private val firebaseUser = MutableLiveData<FirebaseUser?>()
    private val myTag = "UserDatabase"

//    fun getProfile(): MutableLiveData<Resource<User>> {
//
//        val liveData = MutableLiveData<Resource<User>>()
//
//        if (currentUser?.uid == null) {
//            liveData.value = Resource.error("UID is null", null, null)
//            return liveData
//        }
//
//        liveData.value = Resource.loading(null)
//
//        val docRef =
//            firestoreInstance.collection(USERS).document(currentUser.uid)
//        docRef.get().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val document = task.result
//                if (document.exists()) {
//                    liveData.value = Resource.success(document.toObject(User::class.java))
//                } else {
//                    liveData.value =
//                        Resource.error("No document found on ${docRef.path}", null, 404)
//                }
//            } else {
//                liveData.value = Resource.error(task.exception.toString(), null, null)
//            }
//        }
//        return liveData
//    }

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
                    liveData.postValue(Resource.success(task.result.user?.uid))
                    setCurrentUser()
                } else {
                    liveData.postValue(Resource.error("Failed to login",null, -1))
                    Log.d("Failed to login", myTag)
                }
            }
            .addOnFailureListener {
                liveData.postValue(Resource.error("Failed to login",null, -1))
                Log.d("Failed to login", myTag)
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
                    Log.d("Failed to register", myTag)
                }
            }
            .addOnFailureListener {
                liveData.value = Resource.error(it.message, null, null)
                Log.d("Failed to register", myTag)
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
                Log.d("Failed to createUser", myTag)
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

        firestoreInstance
            .collection(USERS)
            .document(uid)
            .set(user)
            .addOnCompleteListener {
                liveData.value = Resource.success(1)
                liveData.postValue(Resource(Status.SUCCESS, 1, "", 200))
                setCurrentUser()
                Log.d("Success to createProfile", myTag)
            }
            .addOnFailureListener { e ->
                liveData.value = Resource.error(e.toString(), null, null)
                liveData.postValue(Resource(Status.SUCCESS, null, e.message, 500))
                Log.d("Failed to createProfile", myTag)
            }

        return liveData
    }

    fun forgotPassword(email: String): MutableLiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()

        liveData.value = Resource.loading(null)

        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    liveData.value = Resource.success(true)
                }
            }
            .addOnFailureListener {
                liveData.value = Resource.error(it.message, null, null)
                Log.d("Failed to forgotPassword", myTag)
            }

        return liveData
    }

    fun getUser(): MutableLiveData<Resource<User>> {
        val liveData = MutableLiveData<Resource<User>>()

        liveData.value = Resource.loading(null)

        firebaseAuth.uid?.let { id ->
            firestoreInstance
                .collection(USERS)
                .document(id)
                .get()
                .addOnSuccessListener {
                    val document = it
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            user.id = document.id
                            liveData.postValue(Resource.success(user))
                        }
                    } else {
                        liveData.postValue(Resource.error("No user with $it id", null, null))
                    }
                }
                .addOnFailureListener {
                    liveData.postValue(Resource.error(it.message.toString(), null, null))
                }
        } ?: run {
            liveData.postValue(Resource.error("firebaseAuth is null", null, -1))
        }

        return liveData
    }

    fun updateUser(user: User): MutableLiveData<Resource<Boolean>> {

        val liveData = MutableLiveData<Resource<Boolean>>()

        val userMap = hashMapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "image" to user.image,
            "email" to user.email,
            "phone" to user.phone,
            "age" to user.age,
            "weight" to user.weight,
            "height" to user.height,
            "plan" to user.plan,
            "state" to user.state,
            "city" to user.city
        )

        liveData.value = Resource.loading(null)
        currentUser?.uid?.let { uid ->
            firestoreInstance.collection(USERS).document(uid)
                .set(userMap)
                .addOnSuccessListener {
                    liveData.postValue(Resource.success(true))
                    Log.d("DocumentSnapshot successfully written!", myTag)
                }
                .addOnFailureListener { e ->
                    liveData.postValue(Resource.error(e.toString(), null, null))
                }
        }

        return liveData
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
        firebaseAuth.uid?.let { id ->
            firestoreInstance
                .collection(USERS)
                .document(id)
                .collection(ALARM_PATH)
                .orderBy(ID_FIELD, Query.Direction.DESCENDING)
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
                                item.time = snapshot["time"] as String
                                item.days = snapshot["days"] as ArrayList<Boolean>
                                item.timeInMs = snapshot["timeInMs"] as Long
                                item.isTurnedOn = snapshot["isTurnedOn"] as Boolean? ?: true
                                item.isRepeatable = snapshot["isRepeatable"] as Boolean? ?: true
                                alarmList.add(item)
                            }
                        }
                    }
                    liveData.postValue(Resource.success(alarmList))
//                    if (alarmList.isNotEmpty()) {
//                    } else {
//                        liveData.value = Resource.error("Alarm list is empty", null, -1)
//                    }
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
            ID_FIELD to alarm.id,
            ALARM_TITLE_FIELD to alarm.title,
            ALARM_TIMESTAMP_FIELD to alarm.time,
            ALARM_IS_TURN_ON_FIELD to alarm.isTurnedOn,
            ALARM_IS_REPEATABLE_FIELD to alarm.isRepeatable,
            ALARM_TIME_IN_MS_FIELD to alarm.timeInMs,
            ALARM_CHALLENGE_FIELD to alarm.challenge,
            ALARM_DAYS_FIELD to alarm.days
        )

        try {
            firebaseAuth.uid?.let { id ->
                firestoreInstance
                    .collection(USERS)
                    .document(id)
                    .collection(ALARM_PATH)
                    .add(alarmMap)
                    .addOnSuccessListener {
                        Log.d("Successfully added: ${it.id}", myTag)
                        liveData.postValue(Resource.success(it.id, 1))
                    }.addOnFailureListener {
                        Log.d("Failure", myTag)
                        liveData.postValue(Resource.error(it.message, null, -1))
                    }
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
        val timestamp = alarm.time
        val title =  alarm.title
        val days =  alarm.days
        val docId =  alarm.docId
        val challenge =  alarm.challenge
        val isTurnedOn =  alarm.isTurnedOn
        val isRepeatable =  alarm.isRepeatable
        val timeInMs = alarm.timeInMs

        val alarmItem: Map<String, Any> = mutableMapOf(
            ID_FIELD to id,
            ALARM_TIMESTAMP_FIELD to timestamp,
            ALARM_TITLE_FIELD to title,
            ALARM_DAYS_FIELD to days,
            ALARM_DAYS_FIELD to days,
            ALARM_CHALLENGE_FIELD to challenge,
            ALARM_IS_TURN_ON_FIELD to isTurnedOn,
            ALARM_IS_REPEATABLE_FIELD to isRepeatable,
            ALARM_TIME_IN_MS_FIELD to timeInMs,
//            "isPlayed" to alarm.isPlayed,
//            "isVibrated" to city,
        )

        liveData.value = Resource.loading(null)
        firebaseAuth.uid?.let { id ->
            firestoreInstance
                .collection(USERS)
                .document(id)
                .collection(ALARM_PATH)
                .document(docId)
                .set(alarmItem)
                .addOnSuccessListener {
                    liveData.value = Resource.success(true)
                    Log.d("DocumentSnapshot successfully written!", myTag)
                }
                .addOnFailureListener { e ->
                    liveData.value = Resource.error(e.toString(), null, null)
                }
        }

        return liveData
    }


    fun deleteAlarm(docId: String?): MutableLiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()

        liveData.value = Resource.loading(null)
        if (docId == null) {
            liveData.value = Resource.error("id is null", null, null)
            return liveData
        }
        firebaseAuth.uid?.let { id ->
            firestoreInstance
                .collection(USERS)
                .document(id)
                .collection(ALARM_PATH)
                .document(docId)
                .delete()
                .addOnSuccessListener {
                    liveData.value = Resource.success(true)
                    Log.d("DocumentSnapshot successfully deleted!", myTag)
                }
                .addOnFailureListener { e ->
                    liveData.value = Resource.error(e.toString(), null, null)
                }
        }

        return liveData
    }



    fun getActivityList(): MutableLiveData<Resource<List<Activity>>> {
        val liveData = MutableLiveData<Resource<List<Activity>>>()
        liveData.value = Resource.loading(null)

        val activityList = ArrayList<Activity>()
        firebaseAuth.uid?.let { id ->
            firestoreInstance
                .collection(USERS)
                .document(id)
                .collection(ACTIVITY_PATH)
                .orderBy(ACTIVITY_CREATED_AT_FIELD, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshots ->
                    if (snapshots != null) {
                        for (snapshot: DocumentSnapshot in snapshots) {
                            val activity : Activity? = snapshot.toObject(Activity::class.java)
                            Log.d("getActivityList - snapshot: $activity", myTag)
                            activity?.let { item ->
                                item.docId = snapshot.id
                                item.createdAt = snapshot.get(ACTIVITY_CREATED_AT_FIELD) as Long
                                activityList.add(item)
                            }
                        }
                    }
                    liveData.postValue(Resource.success(activityList))
//                    if (activityList.isNotEmpty()) {
//                    } else {
//                        liveData.postValue(Resource.error("Activity list is empty", null, -1))
//                    }
                }
                .addOnFailureListener {
                    liveData.postValue(Resource.error("Failed to get activity list", null, -1))
                }
        }

        return liveData
    }

    fun getAllActivityCountersBy(type: Int): MutableLiveData<Resource<List<Activity>>> {
        val liveData = MutableLiveData<Resource<List<Activity>>>()
        liveData.value = Resource.loading(null)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startDay = when(type) {
            PERIOD_WEEK -> {
                val weekday = calendar[Calendar.DAY_OF_WEEK]
                val monday = Calendar.MONDAY
                val day = if ((weekday - monday) < 0) (7 - (monday - weekday)) else (weekday - monday)
                Log.d("dayOfWeek $day ", myTag)
                calendar[Calendar.DAY_OF_YEAR] - day
            }
            PERIOD_MONTH -> {
                1
            }
            else -> {
                calendar[Calendar.DAY_OF_YEAR] - calendar[Calendar.DAY_OF_WEEK]
            }
        }

        calendar.set(Calendar.DAY_OF_YEAR, startDay)

        Log.d("getAllActivityCountersBy: ${calendar.time}", myTag)

        val activityList = ArrayList<Activity>()
        firebaseAuth.uid?.let { id ->
            firestoreInstance
                .collection(USERS)
                .document(id)
                .collection(ACTIVITY_PATH)
                .whereGreaterThanOrEqualTo("createdAt", calendar.timeInMillis)
                .get()
                .addOnSuccessListener { snapshots ->
                    if (snapshots != null) {
                        for (snapshot: DocumentSnapshot in snapshots) {
                            val activity : Activity? = snapshot.toObject(Activity::class.java)
                            Log.d("getAllActivityCountersBy - snapshot: $activity", myTag)
                            activity?.let { item ->
                                item.docId = snapshot.id
                                item.createdAt = snapshot.get(ACTIVITY_CREATED_AT_FIELD) as Long
                                activityList.add(item)
                            }
                        }
                    }
                    liveData.postValue(Resource.success(activityList))
                }
                .addOnFailureListener {
                    liveData.postValue(Resource.error("Failed to get activity list", null, -1))
                }
        }

        return liveData
    }

    fun createActivity(activity: Activity): MutableLiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()

        val activityMap = mapOf<String, Any>(
            ID_FIELD to activity.id,
            NAME_FIELD to activity.name,
            DESCRIPTION_FIELD to activity.description,
            SECONDS_FIELD to activity.seconds,
            WORKOUT_FIELD to activity.workout,
            EXERCISE_FIELD to activity.exercise,
            ACTIVITY_KCAL_FIELD to activity.calories,
            ACTIVITY_COUNTERS_FIELD to activity.counters,
            ACTIVITY_CREATED_AT_FIELD to activity.createdAt
        )

        liveData.value = Resource.loading(null)
        firebaseAuth.uid?.let { id ->
            firestoreInstance
                .collection(USERS)
                .document(id)
                .collection(ACTIVITY_PATH)
                .add(activityMap)
                .addOnSuccessListener {
                    liveData.postValue(Resource.success(true, 1))
                    Log.d("DocumentSnapshot successfully written!", myTag)
                }
                .addOnFailureListener { e ->
                    liveData.postValue(Resource.error(e.toString(), null, null))
                }
        }

        return liveData
    }

    fun updateActivity(activity: Activity): MutableLiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()

        val activityMap = mapOf<String, Any>(
            ID_FIELD to activity.id,
            NAME_FIELD to activity.name,
            DESCRIPTION_FIELD to activity.description,
            WORKOUT_FIELD to activity.workout,
            EXERCISE_FIELD to activity.exercise,
            SECONDS_FIELD to activity.seconds,
            ACTIVITY_KCAL_FIELD to activity.calories,
            ACTIVITY_COUNTERS_FIELD to activity.counters,
            ACTIVITY_CREATED_AT_FIELD to activity.createdAt
        )

        liveData.value = Resource.loading(null)
        firebaseAuth.uid?.let { id ->
            firestoreInstance
                .collection(USERS)
                .document(id)
                .collection(ACTIVITY_PATH)
                .document(activity.docId)
                .set(activityMap)
                .addOnSuccessListener {
                    liveData.postValue(Resource.success(true, 1))
                    Log.d("DocumentSnapshot successfully updated!", myTag)
                }
                .addOnFailureListener { e ->
                    liveData.postValue(Resource.error(e.toString(), null, null))
                }
        }

        return liveData
    }

    fun getWorkouts(): MutableLiveData<Resource<List<Workout>>> {
        val liveData = MutableLiveData<Resource<List<Workout>>>()
        liveData.value = Resource.loading(null)

        val workoutList = ArrayList<Workout>()
        firebaseAuth.uid?.let {
            firestoreInstance
                .collection(WORKOUT_PATH)
                .get()
                .addOnSuccessListener { snapshots ->
                    if (snapshots != null) {
                        for (snapshot: DocumentSnapshot in snapshots) {
                            val workout : Workout? = snapshot.toObject(Workout::class.java)
                            Log.d("getWorkoutList - workout: $workout", myTag)
                            workout?.let { item ->
                                item.docId = snapshot.id
                                workoutList.add(item)
                            }
                        }
                    }
                    liveData.postValue(Resource.success(workoutList))
                }
                .addOnFailureListener {
                    liveData.postValue(Resource.error("Failed to get workout list", null, -1))
                }
        }

        return liveData
    }

    fun getExercisesByWorkoutId(id: String): MutableLiveData<Resource<List<Exercise>>> {
        val liveData = MutableLiveData<Resource<List<Exercise>>>()
        liveData.value = Resource.loading(null)

        val exerciseList = ArrayList<Exercise>()
        firebaseAuth.uid?.let {
            firestoreInstance
                .collection(WORKOUT_PATH)
                .document(id)
                .collection(EXERCISES_PATH)
                .orderBy(ID_FIELD, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshots ->
                    if (snapshots != null) {
                        for (snapshot: DocumentSnapshot in snapshots) {
                            val workout : Exercise? = snapshot.toObject(Exercise::class.java)
                            Log.d("getExerciseListList - exerciseList: $workout", myTag)
                            workout?.let { item ->
                                item.docId = snapshot.id
                                exerciseList.add(item)
                            }
                        }
                    }
                    liveData.postValue(Resource.success(exerciseList))
//                    if (exerciseList.isNotEmpty()) {
//                    } else {
//                        liveData.postValue(Resource.error("exerciseList list is empty", null, -1))
//                    }
                }
                .addOnFailureListener {
                    liveData.postValue(Resource.error("Failed to get exerciseList list", null, -1))
                }
        }

        return liveData
    }

    fun uploadImageFile(filePath: Uri): MutableLiveData<Resource<String>> {
        val liveData = MutableLiveData<Resource<String>>()
        liveData.value = Resource.loading(null)


        val child = "images/"+UUID.randomUUID().toString()
        Log.d("child: $child, file: $filePath", myTag)

        storageReference
            .child(child)
            .putFile(filePath)
            .addOnSuccessListener {
                it.metadata?.path?.let { it1 ->
                    storageReference.child(it1).downloadUrl.addOnSuccessListener { uri ->
                        liveData.postValue(Resource.success(uri.toString(), null))
                        Log.d("task: $uri", myTag)
                    }
                }
            }
            .addOnFailureListener {
                Log.d("${it.message}", myTag)
                liveData.postValue(Resource.error("Failed to upload file", null, -1))
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
        const val ACTIVITY_PATH = "activities"
        const val WORKOUT_PATH = "workouts"
        const val EXERCISES_PATH = "exercises"

        const val ID_FIELD = "id"
        const val NAME_FIELD = "name"
        const val WORKOUT_FIELD = "workout"
        const val EXERCISE_FIELD = "exercise"
        const val SECONDS_FIELD = "seconds"
        const val WORKOUT_ID_FIELD = "workoutId"
        const val DESCRIPTION_FIELD = "description"

        const val ACTIVITY_CREATED_AT_FIELD = "createdAt"
        const val ACTIVITY_COUNTERS_FIELD = "counters"
        const val ACTIVITY_KCAL_FIELD = "calories"

        const val ALARM_TIMESTAMP_FIELD = "time"
        const val ALARM_TITLE_FIELD = "title"
        const val ALARM_IS_TURN_ON_FIELD = "isTurnedOn"
        const val ALARM_IS_REPEATABLE_FIELD = "isRepeatable"
        const val ALARM_TIME_IN_MS_FIELD = "timeInMs"
        const val ALARM_CHALLENGE_FIELD = "challenge"
        const val ALARM_DAYS_FIELD = "days"
        const val ALARM_USER_ID_FIELD = "userId"
    }
}