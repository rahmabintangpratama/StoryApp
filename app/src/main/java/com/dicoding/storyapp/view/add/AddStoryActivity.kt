package com.dicoding.storyapp.view.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.dicoding.storyapp.BuildConfig
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.utils.DialogType
import com.dicoding.storyapp.utils.createCustomTempFile
import com.dicoding.storyapp.utils.loadImage
import com.dicoding.storyapp.utils.showAlertDialog
import com.dicoding.storyapp.utils.uriToFile
import com.dicoding.storyapp.view.main.MainActivity
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private val addStoryViewModel by viewModels<AddStoryViewModel>()
    private var imageSelected: Uri? = null
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadStory() }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        observeViewModel()
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private fun startCamera() {
        val photoFile = createCustomTempFile(application)
        currentPhotoPath = photoFile.absolutePath
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            photoFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        launcherCamera.launch(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun uploadStory() {

        val desc = binding.edAddDescription.text.toString()

        val isDescValid = desc.isNotBlank()

        if (isDescValid) {
            if (getFile != null) {
                val file = getFile!!
                val description = binding.edAddDescription.text.toString().trim()
                addStoryViewModel.postStory(file, description)
            } else {
                Toast.makeText(this, getString(R.string.select_image), Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.edAddDescription.error = getString(R.string.error_empty_desc)
        }
    }

    private fun observeViewModel() {
        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        addStoryViewModel.isPosted.observe(this) { isSuccess ->
            if (isSuccess) {
                showAlertDialog(
                    title = R.string.yeah,
                    message = getString(R.string.upload_success),
                    icon = R.drawable.ic_success,
                    type = DialogType.SUCCESS,
                    positiveButtonText = R.string.continued,
                    positiveButtonAction = {
                        setResult(RESULT_OK)
                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            } else {
                showAlertDialog(
                    title = R.string.error,
                    message = getString(R.string.upload_failed),
                    icon = R.drawable.ic_error,
                    type = DialogType.ERROR,
                    positiveButtonText = R.string.try_again,
                    positiveButtonAction = {}
                )
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageSelected = uri
            getFile = uriToFile(uri, this)
            showImage()
        } else {
            showToast(getString(R.string.no_image))
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val file = File(currentPhotoPath)
            getFile = file
            showImage()
        } else {
            showToast(getString(R.string.capture_failed))
        }
    }

    private fun showImage() {
        if (getFile != null) {
            binding.previewImageView.loadImage(getFile!!)
        } else {
            showToast(getString(R.string.no_image))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}