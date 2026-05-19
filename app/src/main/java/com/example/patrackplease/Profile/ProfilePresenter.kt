package com.example.patrackplease.Profile

import com.example.patrackplease.Profile.Dto.ProfileRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class ProfilePresenter(private val model: ProfileModel) : ProfileContract.Presenter {

    private var view: ProfileContract.View? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun attachView(view: ProfileContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
        presenterScope.cancel()
    }

    override fun loadProfile() {
        view?.showLoading()
        presenterScope.launch {
            val result = model.getProfileData()
            view?.hideLoading()
            result.fold(
                onSuccess = { user ->
                    view?.displayProfileData(user.username ?: "Unknown", user.email, user.profileImageUrl)
                },
                onFailure = { error ->
                    view?.showErrorMessage(error.message ?: "Failed to load profile.")
                }
            )
        }
    }

    override fun onEditPhotoClicked() = view?.openPhotoPicker() ?: Unit
    override fun onChangeUsernameClicked() = view?.showChangeUsernameDialog() ?: Unit
    override fun onChangeEmailClicked() = view?.showChangeEmailDialog() ?: Unit
    override fun onChangePasswordClicked() = view?.showChangePasswordDialog() ?: Unit
    override fun onRemovePhotoClicked() = confirmRemovePhoto()

    override fun updateProfilePhoto(imagePath: String) {
        val file = File(imagePath)
        if (!file.exists()) {
            view?.showErrorMessage("Invalid image file.")
            return
        }
        view?.showLoading()
        presenterScope.launch {
            val result = model.uploadProfilePhoto(file)
            view?.hideLoading()
            result.fold(
                onSuccess = { user ->
                    view?.showSuccessMessage("Profile photo updated!")
                    view?.displayProfileData(user.username ?: "", user.email, user.profileImageUrl)
                },
                onFailure = { error -> view?.showErrorMessage(error.message ?: "Failed to upload photo.") }
            )
        }
    }

    override fun confirmRemovePhoto() {
        view?.showLoading()
        presenterScope.launch {
            val result = model.removeProfilePhoto()
            view?.hideLoading()
            result.fold(
                onSuccess = { user ->
                    view?.showSuccessMessage("Photo removed successfully.")
                    view?.displayProfileData(user.username ?: "", user.email, null)
                },
                onFailure = { error -> view?.showErrorMessage(error.message ?: "Failed to remove photo.") }
            )
        }
    }

    override fun updateUsername(newUsername: String) {
        if (newUsername.trim().length < 3) {
            view?.showErrorMessage("Username must be at least 3 characters.")
            return
        }
        view?.showLoading()
        presenterScope.launch {
            val request = ProfileRequest(username = newUsername.trim(), email = null)
            val result = model.updateProfileInfo(request)
            view?.hideLoading()
            result.fold(
                onSuccess = { user ->
                    view?.showSuccessMessage("Username updated!")
                    view?.displayProfileData(user.username ?: "", user.email, user.profileImageUrl)
                },
                onFailure = { error -> view?.showErrorMessage(error.message ?: "Failed to update username.") }
            )
        }
    }

    override fun updateEmail(newEmail: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            view?.showErrorMessage("Please enter a valid email address.")
            return
        }
        view?.showLoading()
        presenterScope.launch {
            val request = ProfileRequest(username = null, email = newEmail.trim())
            val result = model.updateProfileInfo(request)
            view?.hideLoading()
            result.fold(
                onSuccess = { user ->
                    view?.showSuccessMessage("Email updated!")
                    view?.displayProfileData(user.username ?: "", user.email, user.profileImageUrl)
                },
                onFailure = { error -> view?.showErrorMessage(error.message ?: "Failed to update email.") }
            )
        }
    }
}