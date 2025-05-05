package mau.se.personalfinance.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mau.se.personalfinance.viewmodels.AddTransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionType: String,
    onBack: () -> Unit,
    viewModel: AddTransactionViewModel
) {
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var dateMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val dateFormatter = rememberSaveable { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val categories = rememberSaveable(transactionType) {
        when (transactionType.lowercase()) {
            "income" -> listOf("Salary", "Other")
            "outcome" -> listOf("Food", "Leisure", "Travel", "Accommodation", "Other")
            else -> emptyList()
        }
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { dateMillis = it }
                    showPicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New ${transactionType.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Form fields
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            var expanded by rememberSaveable { mutableStateOf(false) }
            val dropdownWidth = rememberSaveable { mutableStateOf(0) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        dropdownWidth.value = coordinates.size.width
                    }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(with(LocalDensity.current) { dropdownWidth.value.toDp() })
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                expanded = false
                            }
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0f)
                        .clickable { expanded = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Date: ${dateFormatter.format(Date(dateMillis))}")
            }
        }

        // Fixed bottom buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (description.isNotBlank() && amount.isNotBlank()) {
                        viewModel.saveTransaction(
                            transactionType,
                            amount.toDouble(),
                            description,
                            category,
                            Date(dateMillis)
                        )
                        onBack()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }
}