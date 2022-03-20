package com.example.fitme.core.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.fitme.R
import com.example.fitme.data.local.Constants.Date.DATE_FORMAT
import com.example.fitme.data.local.Constants.Date.DATE_FORMAT_SERVER
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.math.ceil

open class  CoreUtils {

    fun toDp(dp: Float, context: Context) = context.let {
        val density = context.resources.displayMetrics.density
        if (dp == 0f) 0 else ceil((density * dp).toDouble()).toInt()
    }

    /*Key board manipulation*/
    fun showKeyboard(activity: Activity, view: View) {
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /*Get captured image from local storage*/
    fun getBitmapFromUri(context: Context, selectedPhotoUri: Uri): Bitmap {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver,
                        selectedPhotoUri
                    )
                )
            }
            else -> {
                MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    selectedPhotoUri
                )
            }
        }
    }

    fun checkLocationPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    fun isEmailValid(email: String): Boolean {
        val regexPattern = Pattern.compile(Patterns.EMAIL_ADDRESS.pattern())
        val regMatcher = regexPattern.matcher(email)
        return regMatcher.matches()
    }
    fun isPhoneValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    fun formatTime(hourOfDay: Int, minute: Int): String {
        val h = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
        val m = if (minute < 10) ":0$minute" else ":$minute"
        return h + m
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(date: String, initDateFormat: String = DATE_FORMAT_SERVER, endDateFormat: String = DATE_FORMAT): String {
        val initDate = SimpleDateFormat(initDateFormat)
        initDate.timeZone = TimeZone.getTimeZone("GMT")

        val formatter = SimpleDateFormat(endDateFormat)
        return formatter.format(initDate.parse(date))
    }

    fun convertDateToTimestamp(date: String): Long {
        val date = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.getDefault()).parse(date)
        return date.time
    }

    fun getDateTime(timestamp: Long): String? {
        return try {
            val format = "MM/dd/yyyy"
            val sdf = SimpleDateFormat(format)
            val netDate = Date(timestamp * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun getVersionName(context: Context): String {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        return "${context.getString(R.string.app_name)} version ${info.versionName}"
    }

    fun convertHMtoMS(time: String, isRepeating: Boolean, days: TreeMap<Int, Boolean>) : Long {
        var timeInMs: Long = 0

        val splitTime = time.split(":")
        val hour = splitTime[0].toInt()
        val minute = splitTime[1].toInt()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.AM_PM, Calendar.PM)
            set(Calendar.HOUR, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

//        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
//        val nowTime = simpleDateFormat.format(Date())
//
////        val currentTime = nowTime.split(":")
////        val hr = currentTime[0].toInt()

        timeInMs = calendar.timeInMillis

        if (isRepeating) {
            val timeSet = TreeSet<Long>()
            for (i in 0 until 7) {
                var repeatTime = timeInMs

                if (days[i] == true) {
                    val calendar = Calendar.getInstance()
//                    val weekday = calendar[Calendar.DAY_OF_WEEK]
//                    val monday = Calendar.MONDAY
//                    var currentDay = if ((weekday - monday) < 0) (7 - (monday - weekday)) else (weekday - monday)
                    var currentDay = calendar.get(Calendar.DAY_OF_WEEK)-1
                    currentDay--
                    if ((repeatTime < System.currentTimeMillis() && currentDay == i) || currentDay != i) {
                        if (i > currentDay) {
                            repeatTime += TimeUnit.MILLISECONDS.convert((i - currentDay).toLong(), TimeUnit.DAYS)
                        } else {
                            repeatTime += TimeUnit.MILLISECONDS.convert((7 - currentDay).toLong(), TimeUnit.DAYS)
                            TimeUnit.MILLISECONDS.convert(i.toLong(), TimeUnit.DAYS)
                        }
                        timeSet.add(repeatTime)
                    } else if (currentDay == i) {
                        timeSet.add(repeatTime)
                    }
                }
            }

            Log.d("Repeated times: $timeSet", "AlarmManager")

            if (timeSet.isNotEmpty()) {
                return timeSet.first()
            }
        }

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        timeInMs = calendar.timeInMillis
        return timeInMs
    }

    fun formatTimestampDate(createdAt: Timestamp): String {
        val milliseconds = createdAt.seconds * 1000 + createdAt.nanoseconds / 1000000
        val sdf = SimpleDateFormat("MMMM d")
        val netDate = Date(milliseconds)

        return sdf.format(netDate)
    }

//    val date = Date(time * 1000)
//        val newDate = SimpleDateFormat("dd MMMM", Locale("ru")).format(date)
//        val timeFormat = SimpleDateFormat("HH:mm", Locale("ru"))
//        timeFormat.timeZone = TimeZone.getTimeZone("UTC")
//        val time = timeFormat.format(date)
//
//        return "$newDate в $time"
}