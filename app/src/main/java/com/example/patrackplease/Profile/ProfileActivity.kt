package com.example.patrackplease.Profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.patrackplease.R
import com.example.patrackplease.Dashboard.DashboardActivity
import com.example.patrackplease.Tasks.TaskActivity
import com.example.patrackplease.api.ApiClient
import com.example.patrackplease.utils.SessionManager // <-- ADDED THIS IMPORT
import coil.load

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProfileActivity : Activity(), ProfileContract.View {

    private lateinit var presenter: ProfileContract.Presenter
    private lateinit var sessionManager: SessionManager // <-- ADDED THIS

    private lateinit var tvHeaderName: TextView
    private lateinit var tvHeaderEmail: TextView
    private lateinit var tvValueName: TextView
    private lateinit var tvValueEmail: TextView
    private lateinit var ivProfilePicture: ImageView
    private lateinit var ivProfileAvatar: ImageView

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Session Manager right away
        sessionManager = SessionManager(this)

        setContentView(R.layout.activity_profile)

        // Window Insets (Edge-to-Edge compatibility)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Acti)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initPresenter()
        setupClickListeners()
        setupBottomNavigation()

        presenter.loadProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun initViews() {
        tvHeaderName = findViewById(R.id.tvHeaderName)
        tvHeaderEmail = findViewById(R.id.tvHeaderEmail)
        tvValueName = findViewById(R.id.tvValueName)
        tvValueEmail = findViewById(R.id.tvValueEmail)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar)
    }

    private fun initPresenter() {
        val apiService = ApiClient.apiService
        // FIXED: Now passing both apiService AND sessionManager to the Model!
        presenter = ProfilePresenter(ProfileModel(apiService, sessionManager))
        presenter.attachView(this)
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnChangePhoto).setOnClickListener { presenter.onEditPhotoClicked() }
        findViewById<Button>(R.id.btnChangeUsername).setOnClickListener { presenter.onChangeUsernameClicked() }
        findViewById<Button>(R.id.btnChangeEmail).setOnClickListener { presenter.onChangeEmailClicked() }
        findViewById<Button>(R.id.btnChangePassword).setOnClickListener { presenter.onChangePasswordClicked() }
        findViewById<Button>(R.id.btnRemovePhoto).setOnClickListener { presenter.onRemovePhotoClicked() }
    }

    // --- CUSTOM BOTTOM NAVIGATION LOGIC ---
    private fun setupBottomNavigation() {
        val navDashboard = findViewById<android.view.View>(R.id.nav_dashboard)
        val navTasks = findViewById<android.view.View>(R.id.nav_tasks)
        val navProfile = findViewById<android.view.View>(R.id.nav_profile)

        navDashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        navTasks.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        navProfile.setOnClickListener {
            // Do nothing, we are already on the Profile screen!
        }
    }

    override fun showLoading() {
        findViewById<Button>(R.id.btnChangeUsername).isEnabled = false
        findViewById<Button>(R.id.btnChangeEmail).isEnabled = false
    }

    override fun hideLoading() {
        findViewById<Button>(R.id.btnChangeUsername).isEnabled = true
        findViewById<Button>(R.id.btnChangeEmail).isEnabled = true
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }

    override fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun displayProfileData(username: String, email: String, profileImageUrl: String?) {
        val safeUsername = username.trim().ifBlank { "Unknown User" }
        val safeEmail = email.trim().ifBlank { "No email provided" }

        tvHeaderName.text = safeUsername
        tvValueName.text = safeUsername
        tvHeaderEmail.text = safeEmail
        tvValueEmail.text = safeEmail

        loadProfileImage(ivProfilePicture, profileImageUrl)
        loadProfileImage(ivProfileAvatar, profileImageUrl)
    }

    private fun resolveProfileImageUrl(profileImageUrl: String?): String? {
        val normalizedUrl = profileImageUrl
            ?.trim()
            ?.takeUnless { it.isBlank() || it.equals("null", ignoreCase = true) }

        if (normalizedUrl == null) {
            return null
        }

        return if (normalizedUrl.startsWith("http://") || normalizedUrl.startsWith("https://")) {
            normalizedUrl
        } else {
            val baseUrl = "https://patrackpleasebackend.onrender.com"
            if (normalizedUrl.startsWith("/")) {
                "$baseUrl$normalizedUrl"
            } else {
                "$baseUrl/$normalizedUrl"
            }
        }
    }

    private fun loadProfileImage(imageView: ImageView, profileImageUrl: String?) {
        val resolvedImageUrl = resolveProfileImageUrl(profileImageUrl)

        if (resolvedImageUrl == null) {
            imageView.setImageResource(R.drawable.ic_launcher_background)
            return
        }

        imageView.load(resolvedImageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }
    }

    // --- CLASSIC PHOTO PICKER ---
    override fun openPhotoPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val uri: Uri? = data.data
            if (uri != null) {
                val imageFile = getFileFromUri(uri)
                if (imageFile != null) {
                    presenter.updateProfilePhoto(imageFile.absolutePath)
                } else {
                    showErrorMessage("Failed to process image.")
                }
            }
        }
    }

    // --- STANDARD ANDROID DIALOGS ---
    override fun showChangeUsernameDialog() {
        val editText = EditText(this).apply {
            hint = "Enter new username"
            setPadding(48, 32, 48, 32)
        }
        AlertDialog.Builder(this)
            .setTitle("Change Username")
            .setView(editText)
            .setPositiveButton("Update") { _, _ -> presenter.updateUsername(editText.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun showChangeEmailDialog() {
        val editText = EditText(this).apply {
            hint = "Enter new email address"
            setPadding(48, 32, 48, 32)
        }
        AlertDialog.Builder(this)
            .setTitle("Change Email")
            .setView(editText)
            .setPositiveButton("Update") { _, _ -> presenter.updateEmail(editText.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun showChangePasswordDialog() {
        Toast.makeText(this, "Password reset flow triggered.", Toast.LENGTH_SHORT).show()
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("profile_upload", ".jpg", cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
