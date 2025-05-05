package mau.se.personalfinance.ui.screens

import ChartViewModel
import android.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import mau.se.personalfinance.ui.components.DateRangeSelector
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen() {
    val viewModel: ChartViewModel = viewModel()
    val chartData by viewModel.chartData.collectAsState(emptyList())
    var selectedSlice by rememberSaveable { mutableStateOf<ChartViewModel.PieChartData?>(null) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val dateFormatter = rememberSaveable { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var showTypeMenu by rememberSaveable { mutableStateOf(false) }
    val transactionType by viewModel.transactionType.collectAsState()

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
                            text = "Charts",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date Range Selector
                        DateRangeSelector(
                            startDate = startDate,
                            endDate = endDate,
                            dateFormatter = dateFormatter,
                            onClick = { showDatePicker = true }
                        )

                        Spacer(Modifier.weight(1f))

                        // Type Selector
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopEnd)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.clickable { showTypeMenu = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Type: ${transactionType.displayName}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showTypeMenu,
                                onDismissRequest = { showTypeMenu = false },
                                modifier = Modifier.wrapContentSize()
                            ) {
                                ChartViewModel.TransactionType.entries.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.displayName) },
                                        onClick = {
                                            viewModel.setTransactionType(type)
                                            showTypeMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (chartData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available")
                }
            } else {
                AndroidView(
                    factory = { context ->
                        PieChart(context).apply {
                            setUsePercentValues(false)
                            description.isEnabled = false
                            setEntryLabelColor(Color.BLACK)
                            legend.isEnabled = false
                            setEntryLabelTextSize(12f)
                            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                                override fun onValueSelected(e: Entry?, h: Highlight?) {
                                    e?.let {
                                        val index = it.data as Int
                                        selectedSlice = chartData[index]
                                    }
                                }

                                override fun onNothingSelected() {
                                    selectedSlice = null
                                }
                            })
                        }
                    },
                    update = { pieChart ->
                        val entries = chartData.mapIndexed { index, data ->
                            PieEntry(data.amount.toFloat(), index).apply {
                                this.data = index
                            }
                        }
                        val dataSet = PieDataSet(entries, "").apply {
                            colors = ColorTemplate.MATERIAL_COLORS.toList()
                            valueTextSize = 12f
                            valueTextColor = androidx.compose.ui.graphics.Color.Black.toArgb()
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return "%.2f €".format(value)
                                }
                            }
                        }
                        pieChart.data = PieData(dataSet)
                        pieChart.invalidate()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                )
            }

            // Total row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val total by viewModel.totalBalance.collectAsState()
                Text(
                    text = "Total: ${"%.2f".format(total)} €",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    selectedSlice?.let { data ->
        AlertDialog(
            onDismissRequest = { selectedSlice = null },
            title = { Text("Category Details") },
            text = { Text("${data.label}\nTotal: ${"%.2f".format(data.amount)} €") },
            confirmButton = {
                TextButton(onClick = { selectedSlice = null }) { Text("OK") }
            }
        )
    }

    if (showDatePicker) {
        val pickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate.time,
            initialSelectedEndDateMillis = endDate.time
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedStartDateMillis?.let { startMillis ->
                            pickerState.selectedEndDateMillis?.let { endMillis ->
                                viewModel.updateDates(Date(startMillis), Date(endMillis))
                            }
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DateRangePicker(
                state = pickerState,
                title = { Text("Select date range", modifier = Modifier.padding(16.dp)) }
            )
        }
    }
}