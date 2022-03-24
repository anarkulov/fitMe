package com.example.fitme.ui.alarm.exercise

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Process
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fitme.R
import com.example.fitme.camera.CameraSource
import com.example.fitme.core.extentions.runAfter
import com.example.fitme.core.extentions.showSnackBar
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.ui.widgets.CountUpTimer
import com.example.fitme.core.utils.Log
import com.example.fitme.data.enums.Exercise
import com.example.fitme.data.local.AppPrefs
import com.example.fitme.data.models.Activity
import com.example.fitme.data.models.ml.BodyPart
import com.example.fitme.data.models.ml.Device
import com.example.fitme.data.models.ml.KeyPoint
import com.example.fitme.databinding.FragmentExerciseBinding
import com.example.fitme.databinding.FragmentSaveExerciseActivityDialogBinding
import com.example.fitme.tf.ml.ModelType
import com.example.fitme.tf.ml.MoveNet
import com.example.fitme.tf.ml.PoseClassifier
import com.example.fitme.ui.alarm.AlarmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class ExerciseFragment : BaseFragment<AlarmViewModel, FragmentExerciseBinding>(), TextToSpeech.OnInitListener {
    private val myTag = "ExerciseFragment"

    override val viewModel: AlarmViewModel by viewModel()
    private val args: ExerciseFragmentArgs by navArgs()
    private val appPrefs: AppPrefs by inject()

    private var cameraSource: CameraSource? = null
    lateinit var dialog: AlertDialog

    private var isHandDown = false
    private var isHandUp = false

    private var isBodyPartCorrect = true
    private var isPoseCorrect = true
    private var exerciseCounter = 0
    private var caloriesCounter = 0
    private var secondsCounter = 0L
    private lateinit var countUpTimer: CountDownTimer

    private var tts: TextToSpeech? = null

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
            activity?.runOnUiThread {
                binding.tvScore.text = activity?.getString(R.string.tv_score, personScore ?: 0f)
            }
            poseLabels?.sortedByDescending { it.second }?.let {
                activity?.runOnUiThread {
//                    binding.tvFirstClassification.text = convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
//                    binding.tvSecondClassification.text = convertPoseLabels(if (it.size >= 2) it[1] else null)
//                    binding.tvThirdClassification.text = convertPoseLabels(if (it.size >= 3) it[2] else null)
                }
            }

            keyPoint?.let {
                isPoseCorrect = if (personScore == null || personScore <= 0.5) {
                    isPoseCorrect = false
                    return@let
                } else {
                    true
                }
                calculate(keyPoint)
            }
            activity?.runOnUiThread {
                checkCorrect(isBodyPartCorrect, isPoseCorrect)
            }

//            poseByKeyPoints?.let {
//                Log.d("onDetectedInfo: poseByKeyPoints - $poseByKeyPoints, ", myTag)
//                activity?.runOnUiThread {
//                    binding.tvFirstClassification.text = convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
//                    binding.tvSecondClassification.text = convertPoseLabels(if (it.size >= 2) it[1] else null)
//                    binding.tvThirdClassification.text = convertPoseLabels(if (it.size >= 3) it[2] else null)
//                }
//            }
        }
    }

    private fun checkCorrect(firstValue: Boolean, secondValue:Boolean) {
        cameraSource?.isCorrect(firstValue&&secondValue)

        if (!(firstValue && secondValue) && !binding.tvWarning.visible) {
            binding.tvWarning.visible = true
            if (tts?.isSpeaking == false) ttsSpeakOut(binding.tvWarning.text)
        } else if (firstValue && secondValue && binding.tvWarning.visible){
            binding.tvWarning.visible = false
        }
    }

    private fun createPoseEstimator() {
        val poseDetector = MoveNet.create(requireContext(), Device.CPU, ModelType.Lightning)
        cameraSource?.setDetector(poseDetector)
    }

    // Open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(binding.surfaceView, cameraSourceListener).apply {
                        prepareCamera()
                    }
                cameraSource?.setClassifier(context?.let { PoseClassifier.create(it) })
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
            runAfter(5000) {
                setSecondsMeasurement()
            }
        }
    }

    var secondsMod = 1
    private fun setSecondsMeasurement() {
        countUpTimer = object : CountUpTimer(108000000) {
            override fun onTicks(second: Long) {
                if (isAdded) {
                    secondsCounter = second
                    binding.tvSeconds.text = second.toString()

                    val temp = secondsCounter
                    if (temp.toInt() / 15 == secondsMod) {
                        secondsMod++
                        ttsSpeakOut("$temp seconds")
                    }
                }
            }
        }.start()
    }

    private fun isCameraPermissionGranted(): Boolean {
        return activity?.checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("The Language specified is not supported!", myTag)
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    override fun initView() {
        super.initView()

        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
        tts = TextToSpeech(requireContext(), this)
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
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

    override fun initListeners() {
        super.initListeners()

        binding.btnStop.setOnClickListener {
            countUpTimer.cancel()
            if (secondsCounter > 10) {
                ttsSpeakOut("Good job ${appPrefs.profile?.firstName}!")
            }
            showStopExerciseDialog()
        }

        binding.tvSecond.setOnClickListener {
            incrementCounter()
        }
    }

    private fun showStopExerciseDialog() {
        val dialogBinding = FragmentSaveExerciseActivityDialogBinding.inflate(requireActivity().layoutInflater)

        val dialogBuilder = AlertDialog.Builder(activity).apply {
            setView(dialogBinding.root)
            setCancelable(false)
        }

        caloriesCounter = calculateCalories()
        dialogBinding.etExerciseCounter.text = exerciseCounter.toString()
        dialogBinding.etSecondsCounter.text = secondsCounter.toString()
        dialogBinding.etCaloriesCounter.text = caloriesCounter.toString()

        dialog = dialogBuilder.create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.show()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialogBinding.btnSave.setOnClickListener {

            if (dialogBinding.etActivityName.text.isNullOrEmpty()) {
                showToast("Please type name for activity")
            } else {
                val activity = Activity(
                    System.currentTimeMillis().toString(),
                    "",
                    dialogBinding.etActivityName.text.toString(),
                    "",
                    exerciseCounter,
                    secondsCounter.toInt(),
                    caloriesCounter,
                    args.exercise.workout,
                    args.exercise.exercise,
                    "",
                    System.currentTimeMillis()
                )
                saveActivity(activity)
            }
        }
    }

    private fun saveActivity(activity: Activity) {
        viewModel.createActivity(activity).observe(this) { response ->
            when (response.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    viewModel.loading.postValue(false)
                    requireActivity().showSnackBar("Activity is created")
                    dialog.cancel()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun calculate(keyPoint: List<KeyPoint>) {

        when(args.exercise.exercise) {
            Exercise.StandartPushUp.name -> {
                calculateStandartPushUp(keyPoint)
            }
            Exercise.BasicSquat.name -> {
                calculateBasicSquat(keyPoint)
            }
        }
    }

    /**
     * for calculating standartPushUp
     ***/
    private fun calculateStandartPushUp(keyPoint: List<KeyPoint>) {
        checkBack(keyPoint)

        val leftWrist = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_WRIST }
        val rightWrist = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_WRIST }

        val leftElbow = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_ELBOW }
        val rightElbow = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_ELBOW }

        val leftShoulder = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_SHOULDER }
        val rightShoulder = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_SHOULDER }

        if (rightElbow == null || rightShoulder == null || rightWrist == null || leftElbow == null || leftShoulder == null || leftWrist == null) return

        val leftWristY = leftWrist.coordinate.y
        val leftWristX = leftWrist.coordinate.x
        val rightWristY = rightWrist.coordinate.y
        val rightWristX = rightWrist.coordinate.x

        val leftElbowY = leftElbow.coordinate.y
        val leftElbowX = leftElbow.coordinate.x
        val rightElbowY = rightElbow.coordinate.y
        val rightElbowX = rightElbow.coordinate.x

        val leftShoulderY = leftShoulder.coordinate.y
        val leftShoulderX = leftShoulder.coordinate.x
        val rightShoulderY = rightShoulder.coordinate.y
        val rightShoulderX = rightShoulder.coordinate.x

        val rightRadians =
            atan2(rightShoulderY.minus(rightElbowY), rightShoulderX.minus(rightElbowX)) -
                    atan2(rightWristY.minus(rightElbowY), rightWristX.minus(rightElbowX))

        val leftRadians =
            atan2(leftShoulderY.minus(leftElbowY), leftShoulderX.minus(leftElbowX)) -
                    atan2(leftWristY.minus(leftElbowY), leftWristX.minus(leftElbowX))

        val angle = abs((rightRadians + leftRadians) / 2 * (180.0 / PI))

        if (angle > 160 && angle < 200) {
            isHandUp = true
            isHandDown = false
        }

        if (angle > 20 && angle < 70) {
            if (isHandUp) {
                incrementCounter()
            }
            isHandDown = true
            isHandUp = false
        }

        Log.d("calculateStandartPushUp: $angle", myTag)
    }

    //

    private fun calculateCalories(): Int {
        //MET * WIEGHT(kg) / (hour(60minutes) or (* seconds / 3600)

        val weight: Float = appPrefs.profile?.weight ?: 170f
        val metValue: Float = if (args.exercise.metValue == 0f) 3f else args.exercise.metValue
        val caloriesCounter = weight.times(metValue).times(3.5f).times(secondsCounter.div(3600f))

        return if (caloriesCounter <= 0) return 0 else caloriesCounter.toInt()
    }

    /**
     * for calculating BasicSquat
     ***/
    private fun calculateBasicSquat(keyPoint: List<KeyPoint>) {
//        checkBack(keyPoint)

        val leftHip = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_HIP }
        val rightHip = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_HIP }

        val leftKnee = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_KNEE }
        val rightKnee = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_KNEE }

        val leftAnkle = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_ANKLE }
        val rightAnkle = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_ANKLE }

        if (rightKnee == null || rightAnkle == null || rightHip == null || leftKnee == null || leftAnkle == null || leftHip == null) return

        val leftHipY = leftHip.coordinate.y
        val leftHipX = leftHip.coordinate.x
        val rightHipY = rightHip.coordinate.y
        val rightHipX = rightHip.coordinate.x


        val leftKneeY = leftKnee.coordinate.y
        val leftKneeX = leftKnee.coordinate.x
        val rightKneeY = rightKnee.coordinate.y
        val rightKneeX = rightKnee.coordinate.x

        val leftAnkleY = leftAnkle.coordinate.y
        val leftAnkleX = leftAnkle.coordinate.x
        val rightAnkleY = rightAnkle.coordinate.y
        val rightAnkleX = rightAnkle.coordinate.x

        val rightRadians =
            atan2(rightAnkleY.minus(rightKneeY), rightAnkleX.minus(rightKneeX)) -
                    atan2(rightHipY.minus(rightKneeY), rightHipX.minus(rightKneeX))

        val leftRadians =
            atan2(leftAnkleY.minus(leftKneeY), leftAnkleX.minus(leftKneeX)) -
                    atan2(leftHipY.minus(leftKneeY), leftHipX.minus(leftKneeX))

        val angle = abs((rightRadians + leftRadians) / 2 * (180.0 / PI))

        if (angle > 160 && angle < 200) {
            isHandUp = true
            isHandDown = false
        }

        if (angle > 60 && angle < 110) {
            if (isHandUp) {
                incrementCounter()
            }
            isHandDown = true
            isHandUp = false
        }

        Log.d("calculateBasicSquat: $angle", myTag)
    }

    private fun checkBack(keyPoint: List<KeyPoint>) {
        val leftShoulder = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_SHOULDER }
        val rightShoulder = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_SHOULDER }

        val leftHip = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_HIP }
        val rightHip = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_HIP }

        val leftKnee = keyPoint.lastOrNull { it.bodyPart == BodyPart.LEFT_KNEE }
        val rightKnee = keyPoint.lastOrNull { it.bodyPart == BodyPart.RIGHT_KNEE }

        if (leftHip == null || rightShoulder == null || rightHip == null || leftKnee == null || leftShoulder == null || rightKnee == null) return

        val leftKneeY = leftKnee.coordinate.y
        val leftKneeX = leftKnee.coordinate.x
        val rightKneeY = rightKnee.coordinate.y
        val rightKneeX = rightKnee.coordinate.x

        val leftHipY = leftHip.coordinate.y
        val leftHipX = leftHip.coordinate.x
        val rightHipY = rightHip.coordinate.y
        val rightHipX = rightHip.coordinate.x

        val leftShoulderY = leftShoulder.coordinate.y
        val leftShoulderX = leftShoulder.coordinate.x
        val rightShoulderY = rightShoulder.coordinate.y
        val rightShoulderX = rightShoulder.coordinate.x

        val rightRadians = // atan2((a.y - b.y), (a.x - b.x)) - atan2((c.y-b.y), (c.x - b.x))
            atan2(rightShoulderY.minus(rightHipY), rightShoulderX.minus(rightHipX)) - atan2(rightKneeY.minus(rightHipY), rightKneeX.minus(rightHipX))

        val leftRadians =
            atan2(leftShoulderY.minus(leftHipY), leftShoulderX.minus(leftHipX)) - atan2(leftKneeY.minus(leftHipY), leftKneeX.minus(leftHipX))

        val angle = abs((rightRadians + leftRadians) / 2 * (180.0 / PI))

        isBodyPartCorrect = angle in 160.0 .. 200.0

        Log.d("isCorrect: $isBodyPartCorrect, angle: $angle", myTag)
    }

    /**
     * for calculating biceps
     ***/
    private fun calculateBiceps(keyPoints: List<KeyPoint>) {

        val rightWrist = keyPoints.lastOrNull { keyPoint ->
            keyPoint.bodyPart == BodyPart.RIGHT_WRIST
        }

        val rightElbow = keyPoints.lastOrNull { keyPoint ->
            keyPoint.bodyPart == BodyPart.RIGHT_ELBOW
        }

        val rightShoulder = keyPoints.lastOrNull { keyPoint ->
            keyPoint.bodyPart == BodyPart.RIGHT_SHOULDER
        }

        if (rightElbow == null || rightShoulder == null || rightWrist == null) return
//        if (rightElbow.score < 0.3 || rightShoulder.score < 0.4 || rightWrist.score < 0.3) return

        val rightWristY = rightWrist.coordinate.y
        val rightWristX = rightWrist.coordinate.x

        val rightElbowY = rightElbow.coordinate.y
        val rightElbowX = rightElbow.coordinate.x

        val rightShoulderY = rightShoulder.coordinate.y
        val rightShoulderX = rightShoulder.coordinate.x

        val radians =
            atan2(rightShoulderY.minus(rightElbowY), rightShoulderX.minus(rightElbowX)) -
                    atan2(rightWristY.minus(rightElbowY), rightWristX.minus(rightElbowX))

        val angle = abs(radians * (180.0 / PI))

//        if (angle > 180.0) {
//            angle = 360.0 - angle
//        }

        if (angle > 170 && angle < 200) {
            isHandUp = true
            isHandDown = false
        }

        if (angle > 25 && angle < 45) {
            if (isHandUp) {
                incrementCounter()
            }
            isHandDown = true
            isHandUp = false
        }

        Log.d("calculateBiceps: $angle", myTag)
    }

    private fun incrementCounter() {
        exerciseCounter = binding.tvCounter.text.toString().toInt()
        exerciseCounter += 1
        activity?.runOnUiThread {
            binding.tvCounter.text = exerciseCounter.toString()
            ttsSpeakOut(binding.tvCounter.text)
        }
    }

    private fun ttsSpeakOut(text: CharSequence?) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

// pushUpAngle
//    private fun calculatePushUpAngle(keyPoints: List<KeyPoint>) {
//
//                Log.d("onDetectedInfo: keyPoints - $keyPoints, ", myTag)
//                val leftElbow = it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.LEFT_ELBOW
//                }
//
//                val right_elbow = it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.RIGHT_ELBOW
//                }
//
//                val left_wrist = it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.LEFT_WRIST
//                }
//
//                val right_wrist= it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.RIGHT_WRIST
//                }
//
//                val left_shoulder = it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.LEFT_SHOULDER
//                }
//
//                val right_shoulder = it.lastOrNull { keyPoint -> keyPoint.bodyPart == BodyPart.RIGHT_SHOULDER }

//        val leftWristY = leftWrist?.coordinate?.y ?: 0.0f
//        val leftWristX = leftWrist?.coordinate?.x ?: 0.0f
//        val rightWristY = rightWrist?.coordinate?.y ?: 0.0f
//        val rightWristX = rightWrist?.coordinate?.x ?: 0.0f
//
//        val leftElbowY = leftElbow?.coordinate?.y ?: 0.0f
//        val leftElbowX = leftElbow?.coordinate?.x ?: 0.0f
//        val rightElbowY = rightElbow?.coordinate?.y ?: 0.0f
//        val rightElbowX = rightElbow?.coordinate?.x ?: 0.0f
//
//        val leftShoulderY = leftShoulder?.coordinate?.y ?: 0.0f
//        val leftShoulderX = leftShoulder?.coordinate?.x ?: 0.0f
//        val rightShoulderY = rightShoulder?.coordinate?.y ?: 0.0f
//        val rightShoulderX = rightShoulder?.coordinate?.x ?: 0.0f
//
//        val leftRadians = atan2(c[1]-b[1], c[0] - b[0]) - atan2(a[1]-b[1], a[0] - b[0])
//        val leftRadians = atan2(leftShoulderY.minus(leftElbowY), leftShoulderX.minus(leftElbowX)) -
//                atan2(leftWristY.minus(leftElbowY), leftWristX.minus(leftElbowX))
//
//        val rightRadians = atan2(c[1]-b[1], c[0] - b[0]) - atan2(a[1]-b[1], a[0] - b[0])
//        val rightRadians =
//            atan2(rightShoulderY.minus(rightElbowY), rightShoulderX.minus(rightElbowX)) -
//                    atan2(rightWristY.minus(rightElbowY), rightWristX.minus(rightElbowX))
//
//        var angle = abs(((leftRadians + rightRadians) / 2.0) * (180.0 / PI))
//
//        if (angle > 180.0) {
//            angle = 360.0 - angle
//        }
//
//        Log.d("calculatePushUpAngle: $angle", myTag)
//
//    }

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

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentExerciseBinding {
        return FragmentExerciseBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentExerciseBinding {
        return FragmentExerciseBinding.bind(view)
    }
}