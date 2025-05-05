package mau.se.personalfinance.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mau.se.personalfinance.viewmodels.BalanceViewModel
import java.util.Date
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import mau.se.personalfinance.ui.components.TransactionRow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import mau.se.personalfinance.data.IncomeEntity
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.saveable.rememberSaveable
import mau.se.personalfinance.ui.components.DateRangeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    viewModel: BalanceViewModel = viewModel()
) {
    val transactions by viewModel.filteredTransactions.collectAsState(emptyList())
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val dateFormatter = rememberSaveable { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Calculate total balance
    val totalBalance = rememberSaveable(transactions) {
        transactions.sumOf { transaction ->
            if (transaction is IncomeEntity) transaction.amount else -transaction.amount
        }
    }

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
                            text = "Balance",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
        }
    ){ padding ->
        LazyColumn(contentPadding = padding) {
            items(transactions) { transaction ->
                val isIncome = transaction is IncomeEntity
                TransactionRow(
                    description = transaction.description,
                    amount = transaction.amount,
                    isIncome = isIncome,
                    date = transaction.date,
                    category = transaction.category,
                    onDelete = { viewModel.deleteTransaction(transaction) }
                )
            }

            item {
                TotalBalanceRow(total = totalBalance)
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
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }
}



@Composable
private fun TotalBalanceRow(total: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Total balance: ${"%.2f".format(total)}€",
            color = if (total >= 0) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium
        )
    }
}