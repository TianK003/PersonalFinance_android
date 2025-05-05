package mau.se.personalfinance.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mau.se.personalfinance.ui.components.TransactionRow
import mau.se.personalfinance.viewmodels.IncomeViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import mau.se.personalfinance.ui.components.DateRangeSelector
import mau.se.personalfinance.ui.components.FinanceFab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(
    onAddClick: () -> Unit,
    viewModel: IncomeViewModel = viewModel()
) {
    val incomes by viewModel.sortedIncomes.collectAsState(emptyList())
    var showSortMenu by rememberSaveable { mutableStateOf(false) }
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val currentSortType by viewModel.sortType.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val dateFormatter = rememberSaveable { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Title Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sort Button with dropdown
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopStart)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.clickable { showSortMenu = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Sort by: ${currentSortType.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                        color = MaterialTheme.colorScheme.secondary,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                IncomeViewModel.SortType.entries.forEach { sortType ->
                                    DropdownMenuItem(
                                        text = { Text(text = "By ${sortType.name.lowercase().replaceFirstChar { it.uppercase() }}") },
                                        onClick = {
                                            viewModel.setSortType(sortType)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        // Date Range Selector
                        DateRangeSelector(
                            startDate = startDate,
                            endDate = endDate,
                            dateFormatter = dateFormatter,
                            onClick = { showPicker = true }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FinanceFab(onClick = onAddClick)
        }
    ){ padding ->
        LazyColumn(contentPadding = padding) {
            items(incomes) { income ->
                TransactionRow(
                    description = income.description,
                    amount = income.amount,
                    isIncome = true,
                    date = income.date,
                    category = income.category,
                    onDelete = { viewModel.deleteIncome(income) }
                )
            }
        }
    }

    if (showPicker) {
        val pickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate.time,
            initialSelectedEndDateMillis = endDate.time
        )

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedStartDateMillis?.let { startMillis ->
                            pickerState.selectedEndDateMillis?.let { endMillis ->
                                viewModel.updateDates(
                                    start = Date(startMillis),
                                    end = Date(endMillis)
                                )
                            }
                        }
                        showPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPicker = false }
                ) { Text("Cancel") }
            }
        ) {
            DateRangePicker(
                state = pickerState,
                title = {
                    Text(
                        "Select date range",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }
}