package com.example.fitme.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

open class AppDatabase {
    protected val realtimeInstance = FirebaseDatabase.getInstance("https://fitme-dc727-default-rtdb.firebaseio.com/")
    protected val firestoreInstance = FirebaseFirestore.getInstance()
    val firebaseAuth= FirebaseAuth.getInstance()
    protected val currentUser = FirebaseAuth.getInstance().currentUser
    protected val storageReference = FirebaseStorage.getInstance().reference
}