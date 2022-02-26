package com.example.fitme.ui.auth

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.fitme.core.network.result.Resource
import com.example.fitme.core.ui.BaseViewModel
import com.example.fitme.data.models.User
import com.example.fitme.utils.Constants.DEFAULT.EMAIL
import com.example.fitme.utils.Constants.DEFAULT.FIRST_NAME
import com.example.fitme.utils.Constants.DEFAULT.LAST_NAME
import com.example.fitme.utils.Constants.DEFAULT.PASSWORD
import com.example.fitme.utils.Constants.DEFAULT.PHONE
import com.example.fitme.utils.Constants.DEFAULT.UID
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(private val authRepository: AuthRepository) : BaseViewModel() {

    private var _createUserAccount = MutableLiveData<Bundle>()
    private var _forgotPassword = MutableLiveData<String>()
    private var _getUser = MutableLiveData<String>()
    private var _createUserProfile = MutableLiveData<Bundle>()
    private var _signIn = MutableLiveData<String>()

    var email = ""
    var uid = ""

    fun register(email: String, password: String): MutableLiveData<Resource<String>> {
        return authRepository.register(email, password)
    }

    fun login(email: String, password: String): MutableLiveData<Resource<String>> {
        return authRepository.login(email, password)
    }

    fun getCurrentFirebaseUser(): MutableLiveData<FirebaseUser?> {
        return authRepository.getCurrentUser()
    }

    var forgotPassword: LiveData<Resource<Boolean>> = _forgotPassword.switchMap {
        authRepository.forgotPassword(it)
    }

    fun forgotPassword(email: String) {
        _forgotPassword.postValue(email)
    }


    var getProfile: LiveData<Resource<User>> = _getUser.switchMap {
        authRepository.getUser(it)
    }

    fun getProfile(uid: String, email: String) {
        _getUser.postValue(uid)
        this.email = email
        this.uid = uid
    }


    var createAccount: LiveData<Resource<String>> = _createUserAccount.switchMap {
        authRepository.createUser(it.getString(EMAIL, ""), it.getString(PASSWORD, ""))
    }

    fun createAccount(email: String, password: String) {
        val bundle = Bundle()
        bundle.putString(EMAIL, email)
        bundle.putString(PASSWORD, password)
        _createUserAccount.postValue(bundle)
    }

    var createUserProfile: LiveData<Resource<Int>> = _createUserProfile.switchMap { bundle ->
        authRepository.createProfile(
            bundle.getString(UID, ""),
            bundle.getString(FIRST_NAME, ""),
            bundle.getString(LAST_NAME, ""),
            bundle.getString(EMAIL, ""),
            bundle.getString(PHONE, ""),
            "",
            "",
            ""
//            bundle.getString(Constants.DEFAULT.COUNTRY, ""),
//            bundle.getString(Constants.DEFAULT.STATE, ""),
//            bundle.getString(Constants.DEFAULT.CITY, "")
        )
    }

    fun createUserProfile(
        uid: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
    ) {
        val bundle = Bundle()
        bundle.putString(EMAIL, email)
        bundle.putString(UID, uid)
        bundle.putString(FIRST_NAME, firstName)
        bundle.putString(LAST_NAME, lastName)
        bundle.putString(PHONE, phone)
        _createUserProfile.postValue(bundle)
    }

//    private val phone = MutableLiveData<String>()
//
//    fun getPhone(): String? {
//        return phone.value
//    }
//
//    private var _createUserAccount = MutableLiveData<Bundle>()
//    private var _forgotPassword = MutableLiveData<String>()
//    private var _getUser = MutableLiveData<String>()
//    private var _createUserProfile = MutableLiveData<Bundle>()
//
//    var email = ""
//
//    var uid = ""
//
//    var forgotPassword: LiveData<Resource<Boolean>> = _forgotPassword.switchMap {
//        authRepository.forgotPassword(it)
//    }
//
//    var getProfile: LiveData<Resource<User>> = _getUser.switchMap {
//        re authRepository.getUser(it)
//    }
//
//    var createUserAccount: LiveData<Resource<String>> = _createUserAccount.switchMap {
//        authRepository.createUser(it.getString(EMAIL, ""), it.getString(PASSWORD, ""))
//    }
//
//    var createUserProfile: LiveData<Resource<Int>> = _createUserProfile.switchMap { bundle ->
//        authRepository.createProfile(
//            bundle.getString(UID, ""),
//            bundle.getString(FIRST_NAME, ""),
//            bundle.getString(LAST_NAME, ""),
//            bundle.getString(EMAIL, ""),
//            bundle.getString(PHONE, ""),
//            "",
//            "",
//            ""
//        )
//    }
//
//    fun forgotPassword(email: String) {
//        _forgotPassword.postValue(email)
//    }
//
//    fun createAccount(
//        email: String,
//        password: String
//    ) {
//        val bundle = Bundle()
//        bundle.putString(EMAIL, email)
//        bundle.putString(PASSWORD, password)
//        _createUserAccount.postValue(bundle)
//    }
//
//    fun getProfile(uid: String, email: String) {
//        _getUser.postValue(uid)
//        this.email = email
//        this.uid = uid
//    }
//
//    fun createUserProfile(
//        uid: String,
//        firstName: String,
//        lastName: String,
//        email: String,
//        phone: String
//    ) {
//        val bundle = Bundle()
//        bundle.putString(EMAIL, email)
//        bundle.putString(UID, uid)
//        bundle.putString(FIRST_NAME, firstName)
//        bundle.putString(LAST_NAME, lastName)
//        bundle.putString(PHONE, phone)
//        _createUserProfile.postValue(bundle)
//    }
}