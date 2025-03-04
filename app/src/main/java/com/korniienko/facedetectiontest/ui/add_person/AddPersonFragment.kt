package com.korniienko.facedetectiontest.ui.add_person

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.korniienko.facedetectiontest.R
import com.korniienko.facedetectiontest.databinding.FragmentAddPersonBinding
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import com.korniienko.facedetectiontest.utils.FaceDetectionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AddPersonFragment : Fragment() {

    @Inject
    lateinit var faceDetectionHelper: FaceDetectionHelper


    private lateinit var binding: FragmentAddPersonBinding
    private val viewModel: AddPersonViewModel by viewModels()
    private var imageUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val bitmap = faceDetectionHelper.loadBitmapFromUri(imageUri!!, requireContext())
            binding.imageView.setImageBitmap(bitmap)
        }
    }
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            val bitmap = faceDetectionHelper.loadBitmapFromUri(it, requireContext())
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddPersonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTakePhoto.setOnClickListener { showImagePickerDialog() }
        binding.btnSave.setOnClickListener { savePerson() }
        setupObserver()


    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Зробити фото", "Вибрати з галереї")
        AlertDialog.Builder(requireContext())
            .setTitle("Виберіть опцію")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> pickImage()
                }
            }
            .show()
    }

    private fun takePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            launchCamera()
        }
    }

    private fun launchCamera() {
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            File(requireContext().filesDir, "person_${System.currentTimeMillis()}.jpg")
        )
        takePictureLauncher.launch(imageUri)
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Доступ до камери відхилено!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupObserver() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
        }
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun savePerson() {
        val name = binding.etName.text.toString().trim()
        val position = binding.etPosition.text.toString().trim()

        if (name.isEmpty() || position.isEmpty()) {
            Toast.makeText(requireContext(), "Заповніть всі поля!", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(requireContext(), "Виберіть або зробіть фото!", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val bitmap = faceDetectionHelper.loadBitmapFromUri(imageUri!!, requireContext())

            if (bitmap == null) {
                Log.e("FaceDetection", "Завантаження фото не вдалося")
                Toast.makeText(requireContext(), "Помилка завантаження фото!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            Log.d("FaceDetection", "Фото для аналізу: ${bitmap.width}x${bitmap.height}")

            val detectedFaces = FaceDetectionHelper().detectFaces(bitmap)

            if (detectedFaces.isEmpty()) {
                Log.e("FaceDetection", "Обличчя не знайдено")
                Toast.makeText(requireContext(), "Обличчя не знайдено! Спробуйте інше фото.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (detectedFaces.size > 1) {
                Log.w("FaceDetection", "На фото більше одного обличчя")
                Toast.makeText(requireContext(), "На фото більше одного обличчя! Спробуйте інше фото.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val person = PersonDomain(
                name = name,
                position = position,
                faceImageBase64 = faceDetectionHelper.bitmapToBase64(bitmap)
            )

            viewModel.addPerson(person)
            Toast.makeText(requireContext(), "Особа додана!", Toast.LENGTH_SHORT).show()
        }
    }



}
