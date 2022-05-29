package com.example.fitme.ui.profile

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.fitme.R
import com.example.fitme.core.extentions.showSnackBar
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.User
import com.example.fitme.databinding.FragmentProfileEditBinding
import com.example.fitme.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class ProfileEditFragment: BaseFragment<HomeViewModel, FragmentProfileEditBinding>() {

    private val PICK_IMAGE_REQUEST: Int = 1001
    override val viewModel: HomeViewModel by viewModel()
    private var selectedPlan = ""
    private var profile: User? = null

    override fun initViewModel() {
        super.initViewModel()

        viewModel.loading.observe(this) {
            binding.progressBar.visible = it
        }

        viewModel.getProfile.observe(this) { response ->
            when(response.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    viewModel.loading.postValue(false)
                    response.data?.let {
                        profile = it
                        setProfileData(it)
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        viewModel.getUserProfile()
        initSpinner()
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.plan_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.btnFilterSpinner.adapter = adapter
        }
    }

    private fun setProfileData(user: User) {
        binding.etFirstName.setText(user.firstName)
        binding.etLastName.setText(user.lastName)
        if (user.age == null) {
            binding.etAge.setText("0")
        } else {
            binding.etAge.setText(user.age.toString())
        }

        if (user.height == null) {
            binding.etHeight.setText("0")
        } else {
            binding.etHeight.setText(user.height.toString())
        }

        if (user.weight == null) {
            binding.etWeight.setText("0")
        } else {
            binding.etWeight.setText(user.weight.toString())
        }

        if (user.plan != null) {
            when(user.plan) {
                "Weight Loss" -> binding.btnFilterSpinner.setSelection(1)
                else -> binding.btnFilterSpinner.setSelection(2)
            }
        }
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnFilterSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedPlan = when (p2) {
                        0 -> {
                            "-"
                        }
                        1 -> {
                            "Weight Loss"
                        }
                        else -> {
                            "Weight Gain"
                        }
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivAvatar.setOnClickListener {
            selectImage()
        }
    }

//    private val launchActivityForResult =
//        registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) {
//            if (it.resultCode == RESULT_OK) {
//                Intent
//            }
//        }

    private var filePath : Uri = Uri.EMPTY

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            Log.d("uri: $uri", "ProfileEdit")
            filePath = uri
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                binding.ivAvatar.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("exception: $e", "ProfileEdit")
            }
        }
    }

    private fun selectImage() {
        getContent.launch("image/*")
    }

    private fun saveProfile() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val age = binding.etAge.text.toString().ifEmpty { "0" }
        val weight = binding.etWeight.text.toString().ifEmpty { "0" }
        val height = binding.etHeight.text.toString().ifEmpty { "0" }

        profile?.firstName = firstName
        profile?.lastName = lastName
        profile?.age = age.toInt()
        profile?.weight = weight.toFloat()
        profile?.height = height.toFloat()
        profile?.plan = selectedPlan

        if(profile == null) {
            showToast("Something went wrong")
            findNavController().popBackStack()
        } else if (filePath != Uri.EMPTY) {

            viewModel.uploadImageFile(filePath).observe(this) { response ->
                when (response.status) {
                    Status.LOADING -> {
                        viewModel.loading.postValue(true)
                    }
                    Status.ERROR -> {
                        viewModel.loading.postValue(false)
                    }
                    Status.SUCCESS -> {
                        viewModel.loading.postValue(false)
                        Log.d("success file: ${response.data}", "ProfileEdit")
                        profile?.image = response.data
                        updateUser()
                    }
                }
            }
        } else {
            updateUser()
        }
    }

    private fun updateUser() {
        viewModel.updateUser(profile!!).observe(this) { response ->
            when (response.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    viewModel.loading.postValue(false)
                    requireActivity().showSnackBar("Profile updated")
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentProfileEditBinding {
        return FragmentProfileEditBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentProfileEditBinding {
        return FragmentProfileEditBinding.bind(view)
    }
}