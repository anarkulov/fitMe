package com.example.fitme.ui.alarm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.os.Process
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.fitme.R
import com.example.fitme.camera.CameraSource
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.ui.BaseActivity
import com.example.fitme.core.utils.Log
import com.example.fitme.data.enums.Pose
import com.example.fitme.data.models.Alarm
import com.example.fitme.data.models.ml.Device
import com.example.fitme.data.models.ml.KeyPoint
import com.example.fitme.databinding.ActivityAlarmBinding
import com.example.fitme.managers.MyAlarmManager
import com.example.fitme.tf.ml.ModelType
import com.example.fitme.tf.ml.MoveNet
import com.example.fitme.tf.ml.PoseClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmActivity : BaseActivity<AlarmViewModel, ActivityAlarmBinding>() {

    private val myTag = "AlarmActivity"
    override val viewModel: AlarmViewModel by viewModel()

    private var isAllowed = false
    private var wakeUpCounter = 0

    private var cameraSource: CameraSource? = null
    private var alarm: Alarm? = null
    private var alarmPoseName: String = ""



    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                showToast("The camera permission is needed")
            }
        }

    private var cameraSourceListener = object : CameraSource.CameraSourceListener {

        override fun onFPSListener(fps: Int) {}

        override fun onDetectedInfo(
            personScore: Float?,
            keyPoint: List<KeyPoint>?,
            poseLabels: List<Pair<String, Float>>?,
        ) {
//            runOnUiThread {
//                binding.tvScore.text = getString(R.string.tv_score, personScore ?: 0f)
//            }
            poseLabels?.sortedByDescending { it.second }?.let {
                runOnUiThread {
//                    val poseName = convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
                    checkPose(if (it.isNotEmpty()) it[0] else null)
//                    convertPoseLabels(if (it.size >= 2) it[1] else null)
//                    convertPoseLabels(if (it.size >= 3) it[2] else null)
                }
            }
        }
    }

    private fun checkPose(pair: Pair<String, Float>?) {
        if (pair == null) return

        val poseName = pair.first
        if (poseName == alarmPoseName) {
            if (pair.second >= 0.95) {
                stopAlarm()
            }
        }
    }

    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.2f", pair.second)})"
    }

    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(binding.surfaceView, cameraSourceListener).apply {
                        prepareCamera()
                    }
                cameraSource?.setClassifier(PoseClassifier.create(this))
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun createPoseEstimator() {
        val poseDetector = MoveNet.create(this, Device.CPU, ModelType.Lightning)
        cameraSource?.setDetector(poseDetector)
    }


    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ),
            -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

//    private fun showStopExerciseDialog() {
//        val dialogBinding = FragmentSaveExerciseActivityDialogBinding.inflate(requireActivity().layoutInflater)
//
//        val dialogBuilder = AlertDialog.Builder(activity).apply {
//            setView(dialogBinding.root)
//            setCancelable(false)
//        }
//
//        caloriesCounter = calculateCalories()
//        dialogBinding.etExerciseCounter.text = exerciseCounter.toString()
//        dialogBinding.etSecondsCounter.text = secondsCounter.toString()
//        dialogBinding.etCaloriesCounter.text = caloriesCounter.toString()
//
//        dialog = dialogBuilder.create()
//        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
//        dialog.show()
//
//        dialogBinding.btnCancel.setOnClickListener {
//            dialog.cancel()
//        }
//
//        dialogBinding.btnSave.setOnClickListener {
//
//            if (dialogBinding.etActivityName.text.isNullOrEmpty()) {
//                showToast("Please type name for activity")
//            } else {
//                val activity = Activity(
//                    System.currentTimeMillis().toString(),
//                    "",
//                    dialogBinding.etActivityName.text.toString(),
//                    "",
//                    exerciseCounter,
//                    secondsCounter.toInt(),
//                    caloriesCounter,
//                    args.exercise.workout,
//                    args.exercise.exercise,
//                    "",
//                    System.currentTimeMillis()
//                )
//                saveActivity(activity)
//            }
//        }
//    }

//    private fun saveActivity(activity: Activity) {
//        viewModel.createActivity(activity).observe(this) { response ->
//            when (response.status) {
//                Status.LOADING -> {
//                    viewModel.loading.postValue(true)
//                }
//                Status.ERROR -> {
//                    viewModel.loading.postValue(false)
//                }
//                Status.SUCCESS -> {
//                    viewModel.loading.postValue(false)
//                    requireActivity().showSnackBar("Activity is created")
//                    dialog.cancel()
//                    findNavController().popBackStack()
//                }
//            }
//        }
//    }

    override fun initViewModel() {
        super.initViewModel()
    }

    override fun initView() {
        super.initView()


        if (!isCameraPermissionGranted()) {
            requestPermission()
        }

        handleMyIntent(intent)
    }

    override fun initListeners() {
        super.initListeners()

//        binding.tvName.setOnClickListener {
//            stopAlarm()
//        }
    }

    private fun stopAlarm() {
        MyAlarmManager.stopAlarm(this)
        binding.tvTimer.visible = true
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = (millisUntilFinished / 1000).toString()
            }
            override fun onFinish() {
                binding.tvTimer.visible = false
            }
        }.start()
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        super.onResume()
        cameraSource?.resume()
    }

    override fun onPause() {
        super.onPause()
        cameraSource?.close()
        cameraSource = null
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleMyIntent(intent)
    }

    private fun handleMyIntent(intent: Intent?) {
        alarm = intent?.getBundleExtra(MyAlarmManager.ALARM_KEY)?.getSerializable(MyAlarmManager.ALARM_KEY) as Alarm?
        setAlarmPose()
    }

    private fun setAlarmPose() {
        binding.ivPose.setImageResource(
            when (alarm?.challenge) {
                Pose.Tree.name -> R.drawable.iv_pose_tree
                Pose.Cobra.name -> R.drawable.ic_iv_pose_cobra
                Pose.Chair.name -> R.drawable.iv_pose_chair
                Pose.Dog.name -> R.drawable.iv_pose_dog
                else -> R.drawable.iv_pose__warrior
        })
        alarmPoseName = alarm?.challenge ?: ""
        binding.tvAlarmPoseName.text = alarmPoseName
    }


    override fun onBackPressed() {
        if (isAllowed){
            super.onBackPressed()
        }
        Log.d("onBackPressed", myTag)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityAlarmBinding {
        return ActivityAlarmBinding.inflate(inflater)
    }
}