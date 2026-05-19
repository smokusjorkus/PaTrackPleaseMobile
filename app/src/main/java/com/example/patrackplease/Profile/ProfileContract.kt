package com.example.patrackplease.Profile

interface ProfileContract {

    /**
     * The View: Implemented by ProfileActivity.
     * Its ONLY job is to update the UI and capture user inputs. It knows nothing about data saving/fetching.
     */
    interface View {
        // UI States
        fun showLoading()
        fun hideLoading()
        fun showErrorMessage(message: String)
        fun showSuccessMessage(message: String)

        // Data Rendering
        fun displayProfileData(username: String, email: String, profileImageUrl: String?)

        // Navigation & Dialog Triggers
        fun openPhotoPicker()
        fun showChangeUsernameDialog()
        fun showChangeEmailDialog()
        fun showChangePasswordDialog()
    }

    /**
     * The Presenter: The "brain" of the screen.
     * It fetches data, handles business logic, and tells the View exactly what to display.
     */
    interface Presenter {
        // Lifecycle
        fun attachView(view: View)
        fun detachView()

        // Initial Data Load
        fun loadProfile()

        // Click Handlers (Triggered by the View)
        fun onEditPhotoClicked()
        fun onChangeUsernameClicked()
        fun onChangeEmailClicked()
        fun onChangePasswordClicked()
        fun onRemovePhotoClicked()

        // Actions (Triggered after a user submits data in a dialog/picker)
        fun updateProfilePhoto(imageUri: String)
        fun confirmRemovePhoto()
        fun updateUsername(newUsername: String)
        fun updateEmail(newEmail: String)
    }
}