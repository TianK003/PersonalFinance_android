package mau.se.personalfinance.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import mau.se.personalfinance.data.FinanceRepository
import mau.se.personalfinance.data.IncomeEntity
import mau.se.personalfinance.data.OutcomeEntity
import mau.se.personalfinance.data.Transaction
import mau.se.personalfinance.data.TransactionsDatabase
import java.util.Calendar
import java.util.Date

class BalanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    private val _startDate = MutableStateFlow(getDefaultStartDate())
    val startDate: StateFlow<Date> = _startDate

    private val _endDate = MutableStateFlow(getDefaultEndDate())
    val endDate: StateFlow<Date> = _endDate

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredTransactions: Flow<List<Transaction>> =
        combine(_startDate, _endDate) { start, end ->
            combine(
                repository.getIncomesBetweenDates(start, end),
                repository.getOutcomesBetweenDates(start, end)
            ) { incomes: List<IncomeEntity>, outcomes: List<OutcomeEntity> ->
                (incomes + outcomes).sortedByDescending { it.date }
            }
        }.flatMapLatest { it }

    init {
        val database = TransactionsDatabase.getDatabase(application)
        repository = FinanceRepository(database.incomeDao(), database.outcomeDao())
    }

    fun updateDates(start: Date, end: Date) {
        updateStartDate(start)
        updateEndDate(end)
    }

    fun updateStartDate(date: Date) {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _startDate.value = calendar.time
    }

    fun updateEndDate(date: Date) {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        _endDate.value = calendar.time
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            when (transaction) {
                is IncomeEntity -> repository.deleteIncome(transaction)
                is OutcomeEntity -> repository.deleteOutcome(transaction)
            }
        }
    }

    fun getDefaultStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        return calendar.time
    }

    fun getDefaultEndDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.time
    }
}