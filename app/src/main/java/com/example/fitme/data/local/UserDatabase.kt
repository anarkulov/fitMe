//package com.example.fitme.data.local
//
//import android.content.Context
//import android.net.Uri
//import android.webkit.MimeTypeMap
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.example.fitme.App
//import com.example.fitme.core.network.result.Resource
//import com.example.fitme.core.network.result.Status
//import com.example.fitme.core.utils.Log
//import com.example.fitme.data.models.User
//import com.example.fitme.utils.Constants.Collection.USERS
//import com.google.firebase.auth.FirebaseAuth
//import java.util.*
//
//class UserDatabase {
//
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
//            fireStoreInstance.collection(USERS).document(currentUser!!.uid)
//
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
//
//    fun updateProfile(
//        firstName: String,
//        lastName: String,
//        email: String,
//        phone: String,
//        country: String,
//        state: String,
//        city: String
//    ): MutableLiveData<Resource<Boolean>> {
//
//        val liveData = MutableLiveData<Resource<Boolean>>()
//
//        val user = hashMapOf(
//            "firstName" to firstName,
//            "lastName" to lastName,
//            "email" to email,
//            "phone" to phone,
//            "country" to country,
//            "state" to state,
//            "city" to city
//        )
//
//        liveData.value = Resource.loading(null)
//        currentUser?.uid?.let { uid ->
//
//            fireStoreInstance.collection(USERS).document(uid)
//                .set(user)
//                .addOnSuccessListener {
//                    liveData.value = Resource.success(true)
//                    Log.d("DocumentSnapshot successfully written!")
//                }
//                .addOnFailureListener { e ->
//                    liveData.value = Resource.error(e.toString(), null, null)
//                }
//        }
//
//        return liveData
//    }
//
//    fun createUser(
//        email: String,
//        password: String
//    ): MutableLiveData<Resource<String>> {
//
//        val liveData = MutableLiveData<Resource<String>>()
//
//        firebaseAuth
//            .createUserWithEmailAndPassword(email, password)
//            .addOnSuccessListener {
//                liveData.value = Resource.success(it.user?.uid)
//            }.addOnFailureListener {
//                liveData.value = Resource.error(it.message, null, null)
//            }
//
//        return liveData
//    }
//
//    fun createProfile(
//        uid: String,
//        firstName: String?,
//        lastName: String?,
//        email: String,
//        phone: String?,
//        country: String?,
//        state: String?,
//        city: String?
//    ): MutableLiveData<Resource<Int>> {
//
//        val liveData = MutableLiveData<Resource<Int>>()
//
//        val user = hashMapOf(
//            "firstName" to firstName,
//            "lastName" to lastName,
//            "email" to email,
//            "phone" to phone,
//            "country" to country,
//            "state" to state,
//            "city" to city
//        )
//
//        liveData.value = Resource.loading(null)
//        fireStoreInstance
//            .collection(USERS)
//            .document(uid)
//            .set(user)
//            .addOnCompleteListener {
//                liveData.value = Resource.success(1)
//                liveData.postValue(Resource(Status.SUCCESS, 1, "", 200))
//            }
//            .addOnFailureListener { e ->
//                liveData.postValue(Resource(Status.SUCCESS, null, e.message, 500))
//                liveData.value = Resource.error(e.toString(), null, null)
//            }
//
//        return liveData
//    }
//
//    fun forgotPassword(email: String): MutableLiveData<Resource<Boolean>> {
//
//        val liveData = MutableLiveData<Resource<Boolean>>()
//
//        liveData.value = Resource.loading(null)
//
//        FirebaseAuth
//            .getInstance()
//            .sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    liveData.value = Resource.success(true)
//                }
//            }.addOnFailureListener {
//                liveData.value = Resource.error(it.message, null, null)
//            }
//
//        return liveData
//    }
//
//
//    fun getUser(uid: String): MutableLiveData<Resource<User>> {
//
//        val user = MutableLiveData<Resource<User>>()
//
//        user.value = Resource.loading(null)
//
//        val docRef = fireStoreInstance.collection("users").document(uid)
//        docRef.get().addOnSuccessListener {
//            val document = it
//            if (document.exists()) {
//                user.value = Resource.success(document.toObject(User::class.java))
//            } else {
//                user.value = Resource.error(null, null, null)
//            }
//        }.addOnFailureListener {
//            user.value = Resource.error(it.message.toString(), null, null)
//        }
//        return user
//    }
//
////    fun fetchFileReference(
////        timeStamp: String,
////        imageUri: Uri,
////        context: Context
////    ): LiveData<StorageReference> {
////        val fetchFileReferenceImage = MutableLiveData<StorageReference>()
////        val fileReference =
////            storageReference.child(timeStamp + "." + getFileExtension(imageUri, context))
////        fetchFileReferenceImage.value = fileReference
////        return fetchFileReferenceImage
////    }
//
//    private fun getFileExtension(uri: Uri, context: Context): String? {
//        val contentResolver = context.contentResolver
//        val mimeTypeMap = MimeTypeMap.getSingleton()
//        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri))
//    }
//
//    fun addImageUrlInDatabase(imageUrl: String, mUri: String): LiveData<Boolean> {
//        val successAddUriImage = MutableLiveData<Boolean>()
//        val reference =
//            currentUser?.uid?.let {
//                fireStoreInstance.collection(USERS).document(it)
//            }
//        val map = HashMap<String, Any>()
//        map[imageUrl] = mUri
//        reference?.update(map)?.addOnCompleteListener { successAddUriImage.setValue(true) }
//            ?.addOnFailureListener {
//                successAddUriImage.setValue(
//                    false
//                )
//            }
//
//        return successAddUriImage
//    }
//}