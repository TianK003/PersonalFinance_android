package mau.se.personalfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mau.se.personalfinance.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel()
    val userName by viewModel.userName.collectAsStateWithLifecycle(initialValue = "")
    val userSurname by viewModel.userSurname.collectAsStateWithLifecycle(initialValue = "")

    var editedName by rememberSaveable { mutableStateOf(userName) }
    var editedSurname by rememberSaveable { mutableStateOf(userSurname) }

    LaunchedEffect(Unit) {
        snapshotFlow { userName to userSurname }.collect { (name, surname) ->
            editedName = name
            editedSurname = surname
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Input your details",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(0.8f),
                isError = editedName.length > 20,
                supportingText = {
                    if (editedName.length > 20) {
                        Text(
                            "Name must be less than 20 characters long",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = editedSurname,
                onValueChange = { editedSurname = it },
                label = { Text("Surname") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (editedName.length <= 20) {
                        viewModel.saveUserDetails(editedName, editedSurname)
                        onBack()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                enabled = editedName.length <= 20
            ) {
                Text("Save")
            }
        }
    }
}