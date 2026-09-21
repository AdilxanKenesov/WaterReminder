package com.visionsystems.waterreminder.presenter.screens.editprofile

import com.visionsystems.waterreminder.navigation.AppNavigator
import javax.inject.Inject

class EditProfileDirection @Inject constructor(
    private val navigator: AppNavigator
) : EditProfileContract.Direction {

    override fun back() {
        navigator.back()
    }
}
