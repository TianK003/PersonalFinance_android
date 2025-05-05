package mau.se.personalfinance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mau.se.personalfinance.viewmodels.SettingsViewModel
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceTopBar(
    isSettings: Boolean,
    onSettingsClick: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel()
    val userName by viewModel.userName.collectAsStateWithLifecycle(initialValue = "")

    CenterAlignedTopAppBar(
        title = {
            if (isSettings) {
                Text("Settings")
            } else {
                Text(
                    text = if (userName.isNotBlank()) "$userName's finances"
                    else "Click the profile icon :)"
                )
            }
        },
        actions = {
            if (!isSettings) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Person, "Settings")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}