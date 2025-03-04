package com.korniienko.facedetectiontest.ui.recognize_face

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.korniienko.facedetectiontest.databinding.FragmentRecognizeFaceBinding
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import com.korniienko.facedetectiontest.utils.FaceDetectionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class RecognizeFaceFragment : Fragment() {

    @Inject
    lateinit var faceDetectionHelper: FaceDetectionHelper
    private lateinit var binding: FragmentRecognizeFaceBinding
    private val viewModel: RecognizeFaceViewModel by viewModels()
    private var imageUri: Uri? = null


    @RequiresApi(Build.VERSION_CODES.P)
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Доступ до камери відхилено!", Toast.LENGTH_SHORT).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri?.let {
                val bitmap = faceDetectionHelper.loadBitmapFromUri(it, requireContext())
                binding.imageView.setImageBitmap(bitmap)
                checkPhoto(it)
                binding.tvResult.text = ""
            } ?: run {
                Log.e("RecognizeFaceFragment", "Помилка: imageUri дорівнює null")
            }
        } else {
            Log.e("RecognizeFaceFragment", "Помилка: Фото не було зроблено")
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = it
            val bitmap = faceDetectionHelper.loadBitmapFromUri(imageUri!!, requireContext())
            binding.imageView.setImageBitmap(bitmap)
            checkPhoto(it)
            binding.tvResult.text = ""
        }
    }


    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(requireContext(), "Дозвіл на сповіщення не надано", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRecognizeFaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        binding.btnCaptureFace.setOnClickListener { showImagePickerDialog() }
        setupObserver()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showImagePickerDialog() {
        val options = arrayOf("Зробити фото", "Вибрати з галереї")
        AlertDialog.Builder(requireContext())
            .setTitle("Виберіть опцію")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndTakePhoto()
                    1 -> pickImage()
                }
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePhoto()
            } else {
                Toast.makeText(requireContext(), "Доступ до камери відхилено", Toast.LENGTH_SHORT).show()
            }
        }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun takePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            launchCamera()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun launchCamera() {
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            File(requireContext().filesDir, "person_${System.currentTimeMillis()}.jpg")
        )
        takePictureLauncher.launch(imageUri)
    }

    private fun checkPhoto(uri: Uri) {
        lifecycleScope.launch {
            val bitmap = faceDetectionHelper.loadBitmapFromUri(uri, requireContext())

            if (bitmap == null) {
                Toast.makeText(requireContext(), "Помилка завантаження фото!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val detectedFaces = FaceDetectionHelper().detectFaces(bitmap)

            if (detectedFaces.isEmpty()) {
                Toast.makeText(requireContext(), "Обличчя не знайдено! Спробуйте інше фото.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if(detectedFaces.size > 1)
            {
                Toast.makeText(requireContext(), "На фото більше одного обличчя! Спробуйте інше фото.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            viewModel.recognizeFace(bitmap, requireContext())
        }
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun setupObserver() {
        viewModel.recognizedPersons.observe(viewLifecycleOwner) { message ->
            binding.tvResult.text = message
        }
    }
}
