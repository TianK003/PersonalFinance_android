package mau.se.personalfinance.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mau.se.personalfinance.data.UserPreferencesRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application)

    val userName: Flow<String> = repository.userName
    val userSurname: Flow<String> = repository.userSurname

    fun saveUserDetails(name: String, surname: String) {
        viewModelScope.launch {
            repository.saveUserDetails(name, surname)
        }
    }

}